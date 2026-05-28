package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.BusinessException;
import com.library.common.Constants;
import com.library.dto.BookQueryDTO;
import com.library.entity.Book;
import com.library.entity.BookStock;
import com.library.entity.Category;
import com.library.mapper.BookMapper;
import com.library.mapper.BookStockMapper;
import com.library.mapper.CategoryMapper;
import com.library.service.BookService;
import com.library.utils.BarCodeUtil;
import com.library.vo.BookVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BookStockMapper bookStockMapper;
    private final CategoryMapper categoryMapper;

    public BookServiceImpl(BookMapper bookMapper, BookStockMapper bookStockMapper,
                           CategoryMapper categoryMapper) {
        this.bookMapper = bookMapper;
        this.bookStockMapper = bookStockMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Page<BookVO> page(BookQueryDTO query) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getIsbn()), Book::getIsbn, query.getIsbn())
               .like(StringUtils.hasText(query.getTitle()), Book::getTitle, query.getTitle())
               .like(StringUtils.hasText(query.getAuthor()), Book::getAuthor, query.getAuthor())
               .eq(query.getCategoryId() != null, Book::getCategoryId, query.getCategoryId())
               .orderByDesc(Book::getCreateTime);

        Page<Book> page = new Page<>(query.getPage(), query.getPageSize());
        Page<Book> bookPage = bookMapper.selectPage(page, wrapper);

        Page<BookVO> voPage = new Page<>(query.getPage(), query.getPageSize(), bookPage.getTotal());
        voPage.setRecords(bookPage.getRecords().stream().map(this::toVO).toList());
        return voPage;
    }

    @Override
    public BookVO getById(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) throw new BusinessException("图书不存在");
        return toVO(book);
    }

    @Override
    @Transactional
    public Book create(Book book) {
        bookMapper.insert(book);
        return book;
    }

    @Override
    public Book update(Book book) {
        bookMapper.updateById(book);
        return book;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long count = bookStockMapper.selectCount(
                new LambdaQueryWrapper<BookStock>().eq(BookStock::getBookId, id)
                        .ne(BookStock::getStatus, Constants.STOCK_DAMAGED));
        if (count > 0) {
            throw new BusinessException("该书目下还有在库或借出的复本，无法删除");
        }
        bookMapper.deleteById(id);
    }

    @Override
    public List<BookStock> getStocks(Long bookId) {
        return bookStockMapper.selectList(
                new LambdaQueryWrapper<BookStock>().eq(BookStock::getBookId, bookId));
    }

    @Override
    @Transactional
    public BookStock addStock(Long bookId) {
        BookStock stock = new BookStock();
        stock.setBookId(bookId);
        stock.setBarcode(BarCodeUtil.generateBookBarcode());
        stock.setStatus(Constants.STOCK_AVAILABLE);
        bookStockMapper.insert(stock);
        bookMapper.incrementStock(bookId);
        return stock;
    }

    @Override
    public void updateStock(BookStock stock) {
        bookStockMapper.updateById(stock);
    }

    @Override
    @Transactional
    public void deleteStock(Long stockId) {
        BookStock stock = bookStockMapper.selectById(stockId);
        if (stock == null) throw new BusinessException("复本不存在");
        if (stock.getStatus() == Constants.STOCK_BORROWED) {
            throw new BusinessException("该复本正在借出中，无法报损");
        }
        stock.setStatus(Constants.STOCK_DAMAGED);
        bookStockMapper.updateById(stock);
        bookMapper.decrementStock(stock.getBookId());
    }

    private BookVO toVO(Book book) {
        BookVO vo = new BookVO();
        vo.setId(book.getId());
        vo.setIsbn(book.getIsbn());
        vo.setTitle(book.getTitle());
        vo.setAuthor(book.getAuthor());
        vo.setPublisher(book.getPublisher());
        vo.setPublishDate(book.getPublishDate());
        vo.setCategoryId(book.getCategoryId());
        vo.setEdition(book.getEdition());
        vo.setCoverUrl(book.getCoverUrl());
        vo.setDescription(book.getDescription());
        vo.setTotalStock(book.getTotalStock());
        vo.setAvailableStock(book.getAvailableStock());
        vo.setCreateTime(book.getCreateTime());

        if (book.getCategoryId() != null) {
            Category category = categoryMapper.selectById(book.getCategoryId());
            if (category != null) vo.setCategoryName(category.getName());
        }
        return vo;
    }
}
