package ru.practicum.ewm.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.category.dto.request.NewCategoryRequestDto;
import ru.practicum.ewm.category.dto.request.UpdateCategoryRequestDto;
import ru.practicum.ewm.category.dto.response.CategoryResponseDto;
import ru.practicum.ewm.category.service.CategoryService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto createCategory(@Valid @RequestBody NewCategoryRequestDto newCategoryDto) {
        log.info("POST /admin/categories — создание категории администратором с name={}", newCategoryDto.getName());
        return categoryService.createCategory(newCategoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoryResponseDto updateCategory(@Valid @RequestBody UpdateCategoryRequestDto updateCategoryDto,
                                              @PathVariable @Positive Long catId) {
        log.info("PATCH /admin/categories/{} — обновление категории: name={}", catId, updateCategoryDto.getName());
        return categoryService.updateCategory(updateCategoryDto, catId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable @Positive Long catId) {
        log.info("DELETE /admin/categories/{} — удаление категории администратором", catId);
        categoryService.deleteCategoryById(catId);
    }
}