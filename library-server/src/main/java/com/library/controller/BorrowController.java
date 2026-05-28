package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.dto.BorrowRequestDTO;
import com.library.dto.PageDTO;
import com.library.entity.BorrowRecord;
import com.library.entity.Reserve;
import com.library.service.BorrowService;
import com.library.vo.BorrowRecordVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @PostMapping("/borrows")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_ADMIN')")
    public Result<BorrowRecord> borrow(@Valid @RequestBody BorrowRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long operatorId = (Long) auth.getPrincipal();
        return Result.success(borrowService.borrow(dto.getReaderId(), dto.getBarcode(), operatorId));
    }

    @PostMapping("/borrows/{id}/return")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_ADMIN')")
    public Result<Void> returnBook(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long operatorId = (Long) auth.getPrincipal();
        borrowService.returnBook(id, operatorId);
        return Result.success();
    }

    @PostMapping("/borrows/{id}/renew")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_ADMIN', 'ROLE_READER')")
    public Result<Void> renew(@PathVariable Long id) {
        borrowService.renew(id);
        return Result.success();
    }

    @GetMapping("/borrows")
    public Result<Page<BorrowRecordVO>> page(PageDTO pageDTO,
                                              @RequestParam(required = false) Long readerId,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam(required = false) String readerNo,
                                              @RequestParam(required = false) String bookTitle) {
        return Result.success(borrowService.page(pageDTO, readerId, status, readerNo, bookTitle));
    }

    @GetMapping("/borrows/overdue")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_ADMIN')")
    public Result<Page<BorrowRecordVO>> overdue(PageDTO pageDTO) {
        return Result.success(borrowService.overduePage(pageDTO));
    }

    @PostMapping("/reserves")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_ADMIN', 'ROLE_READER')")
    public Result<Reserve> reserve(@RequestBody Map<String, Long> body) {
        return Result.success(borrowService.reserve(body.get("readerId"), body.get("bookId")));
    }

    @DeleteMapping("/reserves/{id}")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_ADMIN', 'ROLE_READER')")
    public Result<Void> cancelReserve(@PathVariable Long id) {
        borrowService.cancelReserve(id);
        return Result.success();
    }
}
