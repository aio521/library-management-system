package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.BusinessException;
import com.library.common.Constants;
import com.library.dto.PageDTO;
import com.library.entity.*;
import com.library.mapper.*;
import com.library.service.BorrowService;
import com.library.vo.BorrowRecordVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordMapper borrowRecordMapper;
    private final BookMapper bookMapper;
    private final BookStockMapper bookStockMapper;
    private final ReaderMapper readerMapper;
    private final ReserveMapper reserveMapper;

    public BorrowServiceImpl(BorrowRecordMapper borrowRecordMapper, BookMapper bookMapper,
                             BookStockMapper bookStockMapper, ReaderMapper readerMapper,
                             ReserveMapper reserveMapper) {
        this.borrowRecordMapper = borrowRecordMapper;
        this.bookMapper = bookMapper;
        this.bookStockMapper = bookStockMapper;
        this.readerMapper = readerMapper;
        this.reserveMapper = reserveMapper;
    }

    @Override
    @Transactional
    public BorrowRecord borrow(Long readerId, String barcode, Long operatorId) {
        Reader reader = readerMapper.selectById(readerId);
        if (reader == null) throw new BusinessException("读者不存在");
        if (reader.getStatus() != Constants.READER_NORMAL) {
            throw new BusinessException("读者状态异常，无法借阅");
        }

        int overdueCount = readerMapper.countOverdue(readerId);
        if (overdueCount > 0) throw new BusinessException("该读者有逾期未还图书，请先归还");

        int currentBorrows = readerMapper.countCurrentBorrows(readerId);
        if (currentBorrows >= reader.getMaxBorrow()) {
            throw new BusinessException("已达到最大借阅数量: " + reader.getMaxBorrow());
        }

        BookStock stock = bookStockMapper.selectOne(
                new LambdaQueryWrapper<BookStock>().eq(BookStock::getBarcode, barcode));
        if (stock == null) throw new BusinessException("条形码无效");
        if (stock.getStatus() != Constants.STOCK_AVAILABLE) {
            throw new BusinessException("该复本不可借阅");
        }

        stock.setStatus(Constants.STOCK_BORROWED);
        bookStockMapper.updateById(stock);

        bookMapper.decrementAvailable(stock.getBookId());

        BorrowRecord record = new BorrowRecord();
        record.setReaderId(readerId);
        record.setStockId(stock.getId());
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDate.now().plusDays(reader.getBorrowDays()));
        record.setStatus(Constants.BORROW_ACTIVE);
        record.setOperatorId(operatorId);
        borrowRecordMapper.insert(record);

        return record;
    }

    @Override
    @Transactional
    public void returnBook(Long recordId, Long operatorId) {
        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) throw new BusinessException("借阅记录不存在");
        if (record.getStatus() == Constants.BORROW_RETURNED) {
            throw new BusinessException("该图书记归还");
        }

        record.setReturnDate(LocalDateTime.now());
        record.setStatus(Constants.BORROW_RETURNED);
        borrowRecordMapper.updateById(record);

        bookStockMapper.updateStatus(record.getStockId(), Constants.STOCK_AVAILABLE);

        BookStock stock = bookStockMapper.selectById(record.getStockId());
        if (stock != null) {
            bookMapper.incrementAvailable(stock.getBookId());
        }
    }

    @Override
    @Transactional
    public void renew(Long recordId) {
        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) throw new BusinessException("借阅记录不存在");
        if (record.getStatus() == Constants.BORROW_RETURNED) {
            throw new BusinessException("该图书记归还，无法续借");
        }
        if (record.getRenewCount() >= Constants.MAX_RENEW_COUNT) {
            throw new BusinessException("已达最大续借次数");
        }

        Reader reader = readerMapper.selectById(record.getReaderId());
        record.setDueDate(LocalDate.now().plusDays(reader.getBorrowDays()));
        record.setRenewCount(record.getRenewCount() + 1);
        record.setStatus(Constants.BORROW_RENEWED);
        borrowRecordMapper.updateById(record);
    }

    @Override
    public Page<BorrowRecordVO> page(PageDTO pageDTO, Long readerId, Integer status, String readerNo, String bookTitle) {
        Page<BorrowRecordVO> page = new Page<>(pageDTO.getPage(), pageDTO.getPageSize());
        List<BorrowRecordVO> list = borrowRecordMapper.selectBorrowList(readerId, status, readerNo, bookTitle);
        page.setTotal(list.size());
        int start = (pageDTO.getPage() - 1) * pageDTO.getPageSize();
        int end = Math.min(start + pageDTO.getPageSize(), list.size());
        if (start < list.size()) {
            page.setRecords(list.subList(start, end));
        } else {
            page.setRecords(Collections.emptyList());
        }
        return page;
    }

    @Override
    public Page<BorrowRecordVO> overduePage(PageDTO pageDTO) {
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(BorrowRecord::getDueDate, LocalDate.now())
               .in(BorrowRecord::getStatus, Constants.BORROW_ACTIVE, Constants.BORROW_RENEWED);

        Page<BorrowRecord> page = new Page<>(pageDTO.getPage(), pageDTO.getPageSize());
        borrowRecordMapper.selectPage(page, wrapper);

        for (BorrowRecord record : page.getRecords()) {
            record.setStatus(Constants.BORROW_OVERDUE);
            borrowRecordMapper.updateById(record);
        }

        List<BorrowRecordVO> allOverdue = borrowRecordMapper.selectBorrowList(null, Constants.BORROW_OVERDUE, null, null);
        Page<BorrowRecordVO> voPage = new Page<>(pageDTO.getPage(), pageDTO.getPageSize(), allOverdue.size());
        int start = (pageDTO.getPage() - 1) * pageDTO.getPageSize();
        int end = Math.min(start + pageDTO.getPageSize(), allOverdue.size());
        if (start < allOverdue.size()) {
            voPage.setRecords(allOverdue.subList(start, end));
        }
        return voPage;
    }

    @Override
    @Transactional
    public Reserve reserve(Long readerId, Long bookId) {
        Reader reader = readerMapper.selectById(readerId);
        if (reader == null) throw new BusinessException("读者不存在");

        Book book = bookMapper.selectById(bookId);
        if (book == null) throw new BusinessException("图书不存在");

        if (book.getAvailableStock() > 0) {
            throw new BusinessException("该书有可借复本，可直接借阅");
        }

        Long existing = reserveMapper.selectCount(
                new LambdaQueryWrapper<Reserve>()
                        .eq(Reserve::getReaderId, readerId)
                        .eq(Reserve::getBookId, bookId)
                        .eq(Reserve::getStatus, 0));
        if (existing > 0) throw new BusinessException("已预约过该书");

        Reserve reserve = new Reserve();
        reserve.setReaderId(readerId);
        reserve.setBookId(bookId);
        reserve.setReserveDate(LocalDateTime.now());
        reserve.setExpireDate(LocalDate.now().plusDays(Constants.RESERVE_HOLD_DAYS));
        reserve.setStatus(0);
        reserveMapper.insert(reserve);
        return reserve;
    }

    @Override
    public void cancelReserve(Long reserveId) {
        Reserve reserve = new Reserve();
        reserve.setId(reserveId);
        reserve.setStatus(2);
        reserveMapper.updateById(reserve);
    }
}
