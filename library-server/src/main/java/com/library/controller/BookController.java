package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.dto.BookQueryDTO;
import com.library.entity.Book;
import com.library.entity.BookStock;
import com.library.service.BookService;
import com.library.vo.BookVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public Result<Page<BookVO>> page(BookQueryDTO query) {
        return Result.success(bookService.page(query));
    }

    @GetMapping("/{id}")
    public Result<BookVO> getById(@PathVariable Long id) {
        return Result.success(bookService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_ADMIN')")
    public Result<Book> create(@RequestBody Book book) {
        return Result.success(bookService.create(book));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_ADMIN')")
    public Result<Book> update(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        return Result.success(bookService.update(book));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}/stocks")
    public Result<List<BookStock>> stocks(@PathVariable Long id) {
        return Result.success(bookService.getStocks(id));
    }

    @PostMapping("/{id}/stocks")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_ADMIN')")
    public Result<BookStock> addStock(@PathVariable Long id) {
        return Result.success(bookService.addStock(id));
    }

    @PutMapping("/stocks/{id}")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_ADMIN')")
    public Result<Void> updateStock(@PathVariable Long id, @RequestBody BookStock stock) {
        stock.setId(id);
        bookService.updateStock(stock);
        return Result.success();
    }

    @DeleteMapping("/stocks/{id}")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_ADMIN')")
    public Result<Void> deleteStock(@PathVariable Long id) {
        bookService.deleteStock(id);
        return Result.success();
    }
}
