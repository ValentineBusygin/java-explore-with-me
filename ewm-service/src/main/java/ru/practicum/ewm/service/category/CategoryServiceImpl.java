package ru.practicum.ewm.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.CategoryNewRequest;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.category.CategoryMapper;
import ru.practicum.ewm.service.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(CategoryNewRequest categoryNewRequest) {
        Category category = CategoryMapper.toCategory(categoryNewRequest);

        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = getCategory(catId);

        String newName = categoryDto.getName();
        if (newName == null || category.getName().equalsIgnoreCase(newName)) {
            return CategoryMapper.toCategoryDto(category);
        }

        category.setName(newName);

        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {

        getCategory(catId);

        if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {
            throw new ConflictException("Невозможно удалить категорию с id = " + catId + ", т.к. она привязана к событиям");
        }

        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long categoryId) {
        return CategoryMapper.toCategoryDto(getCategory(categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);

        return categoryRepository.findAll(page).stream()
                .map(CategoryMapper::toCategoryDto)
                .toList();
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Не найдена категория с id = " + categoryId));
    }
}
