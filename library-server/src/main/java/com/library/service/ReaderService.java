package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.ReaderQueryDTO;
import com.library.entity.Reader;
import com.library.entity.ReaderCard;
import com.library.vo.ReaderVO;

public interface ReaderService {
    Page<ReaderVO> page(ReaderQueryDTO query);
    ReaderVO getById(Long id);
    Reader create(Reader reader);
    Reader update(Reader reader);
    void updateStatus(Long id, Integer status);
    ReaderCard issueCard(Long readerId);
}
