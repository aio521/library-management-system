package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.BusinessException;
import com.library.common.Constants;
import com.library.dto.ReaderQueryDTO;
import com.library.entity.Reader;
import com.library.entity.ReaderCard;
import com.library.mapper.ReaderCardMapper;
import com.library.mapper.ReaderMapper;
import com.library.service.ReaderService;
import com.library.utils.BarCodeUtil;
import com.library.vo.ReaderVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
public class ReaderServiceImpl implements ReaderService {

    private final ReaderMapper readerMapper;
    private final ReaderCardMapper readerCardMapper;

    public ReaderServiceImpl(ReaderMapper readerMapper, ReaderCardMapper readerCardMapper) {
        this.readerMapper = readerMapper;
        this.readerCardMapper = readerCardMapper;
    }

    @Override
    public Page<ReaderVO> page(ReaderQueryDTO query) {
        LambdaQueryWrapper<Reader> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getReaderNo()), Reader::getReaderNo, query.getReaderNo())
               .like(StringUtils.hasText(query.getName()), Reader::getName, query.getName())
               .like(StringUtils.hasText(query.getDept()), Reader::getDept, query.getDept())
               .eq(query.getStatus() != null, Reader::getStatus, query.getStatus())
               .orderByDesc(Reader::getCreateTime);

        Page<Reader> page = new Page<>(query.getPage(), query.getPageSize());
        Page<Reader> readerPage = readerMapper.selectPage(page, wrapper);

        Page<ReaderVO> voPage = new Page<>(query.getPage(), query.getPageSize(), readerPage.getTotal());
        voPage.setRecords(readerPage.getRecords().stream().map(this::toVO).toList());
        return voPage;
    }

    @Override
    public ReaderVO getById(Long id) {
        Reader reader = readerMapper.selectById(id);
        if (reader == null) throw new BusinessException("读者不存在");
        return toVO(reader);
    }

    @Override
    @Transactional
    public Reader create(Reader reader) {
        if (!StringUtils.hasText(reader.getReaderNo())) {
            reader.setReaderNo("XJ" + System.currentTimeMillis() % 100000000);
        }
        Long exists = readerMapper.selectCount(
                new LambdaQueryWrapper<Reader>().eq(Reader::getReaderNo, reader.getReaderNo()));
        if (exists > 0) throw new BusinessException("读者编号已存在");

        reader.setMaxBorrow(reader.getMaxBorrow() != null ? reader.getMaxBorrow() : Constants.DEFAULT_MAX_BORROW);
        reader.setBorrowDays(reader.getBorrowDays() != null ? reader.getBorrowDays() : Constants.DEFAULT_BORROW_DAYS);
        reader.setStatus(Constants.READER_NORMAL);
        readerMapper.insert(reader);

        issueCard(reader.getId());
        return reader;
    }

    @Override
    public Reader update(Reader reader) {
        readerMapper.updateById(reader);
        return reader;
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Reader reader = readerMapper.selectById(id);
        if (reader == null) throw new BusinessException("读者不存在");
        reader.setStatus(status);
        readerMapper.updateById(reader);
    }

    @Override
    public ReaderCard issueCard(Long readerId) {
        Reader reader = readerMapper.selectById(readerId);
        if (reader == null) throw new BusinessException("读者不存在");

        ReaderCard card = new ReaderCard();
        card.setReaderId(readerId);
        card.setCardNo(BarCodeUtil.generateCardNo());
        card.setIssueDate(LocalDate.now());
        card.setExpireDate(LocalDate.now().plusYears(4));
        card.setStatus(0);
        readerCardMapper.insert(card);
        return card;
    }

    private ReaderVO toVO(Reader reader) {
        ReaderVO vo = new ReaderVO();
        vo.setId(reader.getId());
        vo.setReaderNo(reader.getReaderNo());
        vo.setName(reader.getName());
        vo.setGender(reader.getGender());
        vo.setIdCard(reader.getIdCard());
        vo.setDept(reader.getDept());
        vo.setPhone(reader.getPhone());
        vo.setMaxBorrow(reader.getMaxBorrow());
        vo.setBorrowDays(reader.getBorrowDays());
        vo.setStatus(reader.getStatus());
        vo.setCreateTime(reader.getCreateTime());

        ReaderCard card = readerCardMapper.selectOne(
                new LambdaQueryWrapper<ReaderCard>().eq(ReaderCard::getReaderId, reader.getId()));
        if (card != null) vo.setCardNo(card.getCardNo());

        vo.setCurrentBorrowCount(readerMapper.countCurrentBorrows(reader.getId()));
        return vo;
    }
}
