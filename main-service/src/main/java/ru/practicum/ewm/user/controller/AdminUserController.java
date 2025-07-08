package ru.practicum.ewm.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.user.dto.request.NewUserRequestDto;
import ru.practicum.ewm.user.dto.response.UserResponseDto;
import ru.practicum.ewm.user.service.UserService;

import java.util.List;

import static ru.practicum.ewm.helper.ControllerConstants.DEFAULT_TEN;
import static ru.practicum.ewm.helper.ControllerConstants.DEFAULT_ZERO;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getAllUsersByFilters(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = DEFAULT_ZERO) @PositiveOrZero int from,
            @RequestParam(defaultValue = DEFAULT_TEN) @Positive int size) {

        log.info("GET /admin/users — фильтрация событий ids={}, from={}, size={}", ids, from, size);
        return userService.getAllUsersByFilters(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody NewUserRequestDto newUserDto) {
        log.info("POST /admin/users — создание пользователя: {}", newUserDto);
        return userService.createUser(newUserDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long userId) {
        log.info("DELETE /admin/users/{} — удаление пользователя", userId);
        userService.deleteUserById(userId);
    }
}