package com.library.service.impl;

import com.library.common.BusinessException;
import com.library.entity.Category;
import com.library.mapper.CategoryMapper;
import com.library.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Category> tree() {
        List<Category> all = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getCode));

        Map<Long, List<Category>> parentMap = all.stream()
                .collect(Collectors.groupingBy(Category::getParentId));

        return buildChildren(0L, parentMap);
    }

    private List<Category> buildChildren(Long parentId, Map<Long, List<Category>> parentMap) {
        List<Category> result = new ArrayList<>();
        List<Category> children = parentMap.get(parentId);
        if (children == null) return result;

        for (Category cat : children) {
            cat.setChildren(buildChildren(cat.getId(), parentMap));
            result.add(cat);
        }
        return result;
    }

    @Override
    public Category create(Category category) {
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public Category update(Category category) {
        categoryMapper.updateById(category);
        return category;
    }

    @Override
    public void delete(Long id) {
        Long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>().eq(Category::getParentId, id));
        if (childCount > 0) throw new BusinessException("该分类下有子分类，无法删除");
        categoryMapper.deleteById(id);
    }
}
