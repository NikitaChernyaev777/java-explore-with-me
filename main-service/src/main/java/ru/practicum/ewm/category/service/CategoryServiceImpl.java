package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.request.NewCategoryRequestDto;
import ru.practicum.ewm.category.dto.request.UpdateCategoryRequestDto;
import ru.practicum.ewm.category.dto.response.CategoryResponseDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.helper.EntityHelper;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EntityHelper entityHelper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryResponseDto createCategory(NewCategoryRequestDto newCategoryDto) {
        log.info("Создание новой категории с name={}", newCategoryDto.getName());

        if (categoryRepository.findByName(newCategoryDto.getName()) != null) {
            throw new ConflictException(String.format("Категория с name=%s уже существует", newCategoryDto.getName()));
        }

        Category newCategory = Category.builder()
                .name(newCategoryDto.getName())
                .build();

        Category savedCategory = categoryRepository.save(newCategory);

        log.info("Категория с id={} успешно создана: name='{}'", savedCategory.getId(), savedCategory.getName());
        return CategoryMapper.toCategoryResponseDto(savedCategory);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories(int from, int size) {
        log.info("Получение списка категорий: from={}, size={}", from, size);

        Pageable pageRequest = entityHelper.toPageRequest(from, size);

        List<CategoryResponseDto> categories = categoryRepository.findAll(pageRequest).stream()
                .map(CategoryMapper::toCategoryResponseDto)
                .toList();

        log.info("Найдено {} категорий", categories.size());
        return categories;
    }

    @Override
    public CategoryResponseDto getCategoryById(Long catId) {
        log.info("Получение категории с id={}", catId);

        Category category = entityHelper.getExistingCategoryByIdOrThrow(catId);

        log.info("Категория с id={} найдена: {}", catId, category.getName());
        return CategoryMapper.toCategoryResponseDto(category);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(UpdateCategoryRequestDto updateCategoryDto, Long catId) {
        log.info("Обновление категории с id={}, name={}", catId, updateCategoryDto.getName());

        Category category = entityHelper.getExistingCategoryByIdOrThrow(catId);

        Category existingByName = categoryRepository.findByName(updateCategoryDto.getName());
        if (existingByName != null && !existingByName.getId().equals(catId)) {
            throw new ConflictException(
                    String.format("Категория с name=%s уже существует", updateCategoryDto.getName()));
        }

        category.setName(updateCategoryDto.getName());
        Category updatedCategory = categoryRepository.save(category);

        log.info("Категория с id={} успешно обновлена: новое имя='{}'",
                updatedCategory.getId(), updatedCategory.getName());
        return CategoryMapper.toCategoryResponseDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long catId) {
        log.info("Удаление категории c id={}", catId);

        entityHelper.getExistingCategoryByIdOrThrow(catId);

        List<Event> categoryEvents = eventRepository.findByCategoryId(catId);
        if (!categoryEvents.isEmpty()) {
            throw new ConflictException("Категория не является пустой");
        }

        categoryRepository.deleteById(catId);
        log.info("Категория с id={} успешно удалена", catId);
    }
}