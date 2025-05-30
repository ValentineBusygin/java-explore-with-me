package ru.practicum.ewm.service.category;

import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.CategoryNewRequest;

import java.util.List;

public interface CategoryService {
    public CategoryDto addCategory(CategoryNewRequest categoryNewRequest);
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto);
    public void deleteCategory(Long catId);
    public CategoryDto getCategoryById(Long categoryId);
    public List<CategoryDto> getAllCategories(Integer from, Integer size);
}
