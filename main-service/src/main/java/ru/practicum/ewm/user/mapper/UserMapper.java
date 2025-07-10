package ru.practicum.ewm.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.user.dto.request.NewUserRequestDto;
import ru.practicum.ewm.user.dto.response.UserResponseDto;
import ru.practicum.ewm.user.dto.response.UserShortResponseDto;
import ru.practicum.ewm.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User toNewUser(NewUserRequestDto newUserDto) {
        return User.builder()
                .name(newUserDto.getName())
                .email(newUserDto.getEmail())
                .build();
    }

    public static UserResponseDto toUserResponseDto(final User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortResponseDto toUserShortResponseDto(final User user) {
        return UserShortResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}