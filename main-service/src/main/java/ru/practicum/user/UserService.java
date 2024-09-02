package ru.practicum.user;

import ru.practicum.user.model.dto.NewUserDto;
import ru.practicum.user.model.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto create(NewUserDto newUserDto);

    List<UserDto> getAll(Set<Long> ids, int from, int size);

    void delete(Long userId);
}
