package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categorie.model.dto.CategoryDto;
import ru.practicum.user.model.dto.NewUserDto;
import ru.practicum.user.model.dto.UserDto;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/admin/users")
    public UserDto create(@Validated NewUserDto newUserDto) {
        return userService.create(newUserDto);
        // ДОДЕЛАТЬ РЕАЛИЗАЦИЮ ВОЗВРАТА ТЕЛА ОШИБКИ
    }

    @GetMapping("/admin/users")
    public List<UserDto> getAll(@RequestParam(name = "ids") Set<Long> ids, // defaultValue = "[]"
                                    @RequestParam(name = "from", defaultValue = "0") int from,
                                    @RequestParam(name = "size", defaultValue = "10") int size) {
        return userService.getAll(ids, from, size);
        // ДОДЕЛАТЬ РЕАЛИЗАЦИЮ ВОЗВРАТА ТЕЛА ОШИБКИ
    }

    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
        // ДОДЕЛАТЬ РЕАЛИЗАЦИЮ ВОЗВРАТА ТЕЛА ОШИБКИ
    }
}
