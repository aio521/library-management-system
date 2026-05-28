package com.library.controller;

import com.library.common.Result;
import com.library.entity.Category;
import com.library.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Result<List<Category>> tree() {
        return Result.success(categoryService.tree());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_LIBRARIAN', 'ROLE_ADMIN')")
    public Result<Category> create(@Valid @RequestBody Category category) {
        return Result.success(categoryService.create(category));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_LIBRARIAN', 'ROLE_ADMIN')")
    public Result<Category> update(@PathVariable Long id, @Valid @RequestBody Category category) {
        category.setId(id);
        return Result.success(categoryService.update(category));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_LIBRARIAN', 'ROLE_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
