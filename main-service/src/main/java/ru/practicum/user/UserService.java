package ru.practicum.user;

import ru.practicum.user.model.dto.NewUserRequest;
import ru.practicum.user.model.dto.UserDto;
import ru.practicum.user.model.dto.UserParam;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto create(NewUserRequest newUserRequest);

    List<UserDto> getAll(UserParam params);

    void delete(Long userId);
}
