package com.library.service;

import com.library.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> tree();
    Category create(Category category);
    Category update(Category category);
    void delete(Long id);
}
