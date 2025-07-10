package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.request.NewCategoryRequestDto;
import ru.practicum.ewm.category.dto.request.UpdateCategoryRequestDto;
import ru.practicum.ewm.category.dto.response.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto createCategory(NewCategoryRequestDto newCategoryDto);

    List<CategoryResponseDto> getAllCategories(int from, int size);

    CategoryResponseDto getCategoryById(Long catId);

    CategoryResponseDto updateCategory(UpdateCategoryRequestDto updateCategoryDto, Long catId);

    void deleteCategoryById(Long catId);
}