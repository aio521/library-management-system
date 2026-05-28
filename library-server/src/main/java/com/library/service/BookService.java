package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.BookQueryDTO;
import com.library.entity.Book;
import com.library.entity.BookStock;
import com.library.vo.BookVO;

import java.util.List;

public interface BookService {
    Page<BookVO> page(BookQueryDTO query);
    BookVO getById(Long id);
    Book create(Book book);
    Book update(Book book);
    void delete(Long id);
    List<BookStock> getStocks(Long bookId);
    BookStock addStock(Long bookId);
    void updateStock(BookStock stock);
    void deleteStock(Long stockId);
}
