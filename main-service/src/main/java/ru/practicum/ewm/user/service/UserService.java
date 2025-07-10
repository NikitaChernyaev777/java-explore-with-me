package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.request.NewUserRequestDto;
import ru.practicum.ewm.user.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(NewUserRequestDto newUserDto);

    List<UserResponseDto> getAllUsersByFilters(List<Long> ids, int from, int size);

    void deleteUserById(Long userId);
}