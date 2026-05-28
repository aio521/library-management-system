package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.dto.ReaderQueryDTO;
import com.library.entity.Reader;
import com.library.entity.ReaderCard;
import com.library.service.ReaderService;
import com.library.vo.ReaderVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/readers")
@PreAuthorize("hasAnyAuthority('ROLE_LIBRARIAN', 'ROLE_ADMIN')")
public class ReaderController {

    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @GetMapping
    public Result<Page<ReaderVO>> page(ReaderQueryDTO query) {
        return Result.success(readerService.page(query));
    }

    @GetMapping("/{id}")
    public Result<ReaderVO> getById(@PathVariable Long id) {
        return Result.success(readerService.getById(id));
    }

    @PostMapping
    public Result<Reader> create(@Valid @RequestBody Reader reader) {
        return Result.success(readerService.create(reader));
    }

    @PutMapping("/{id}")
    public Result<Reader> update(@PathVariable Long id, @Valid @RequestBody Reader reader) {
        reader.setId(id);
        return Result.success(readerService.update(reader));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        readerService.updateStatus(id, body.get("status"));
        return Result.success();
    }

    @PostMapping("/{id}/card")
    public Result<ReaderCard> issueCard(@PathVariable Long id) {
        return Result.success(readerService.issueCard(id));
    }
}
