package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.PageDTO;
import com.library.entity.BorrowRecord;
import com.library.entity.Reserve;
import com.library.vo.BorrowRecordVO;

public interface BorrowService {
    BorrowRecord borrow(Long readerId, String barcode, Long operatorId);
    void returnBook(Long recordId, Long operatorId);
    void renew(Long recordId);
    Page<BorrowRecordVO> page(PageDTO pageDTO, Long readerId, Integer status, String readerNo, String bookTitle);
    Page<BorrowRecordVO> overduePage(PageDTO pageDTO);
    Reserve reserve(Long readerId, Long bookId);
    void cancelReserve(Long reserveId);
}
