package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.helper.EntityHelper;
import ru.practicum.ewm.user.dto.request.NewUserRequestDto;
import ru.practicum.ewm.user.dto.response.UserResponseDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityHelper entityHelper;

    @Override
    @Transactional
    public UserResponseDto createUser(NewUserRequestDto newUserDto) {
        log.info("Создание нового пользователя с email={}", newUserDto.getEmail());

        validateEmailIsUnique(newUserDto.getEmail());

        User user = UserMapper.toNewUser(newUserDto);
        User savedUser = userRepository.save(user);

        log.info("Пользователь с id={} успешно создан", savedUser.getId());
        return UserMapper.toUserResponseDto(savedUser);
    }

    @Override
    public List<UserResponseDto> getAllUsersByFilters(List<Long> ids, int from, int size) {
        log.info("Получение списка пользователей: ids={}, from={}, size={}", ids, from, size);

        Pageable pageRequest = entityHelper.toPageRequest(from, size);

        Page<User> page = (ids != null && !ids.isEmpty())
                ? userRepository.findByIdIn(ids, pageRequest)
                : userRepository.findAll(pageRequest);

        return page.stream()
                .map(UserMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        log.info("Удаление пользователя c id={}", userId);

        entityHelper.getExistingUserByIdOrThrow(userId);
        userRepository.deleteById(userId);

        log.info("Пользователь с id={} успешно удален", userId);
    }

    private void validateEmailIsUnique(String email) {
        if (userRepository.existsUserByEmail(email)) {
            throw new ConflictException(String.format("Пользователь с email=%s уже существует", email));
        }
    }
}