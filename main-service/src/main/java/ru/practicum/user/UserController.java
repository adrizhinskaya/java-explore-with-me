package ru.practicum.user;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.logger.ColoredCRUDLogger;
import ru.practicum.user.model.dto.NewUserRequest;
import ru.practicum.user.model.dto.UserDto;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Validated @RequestBody NewUserRequest newUserRequest) {
        ColoredCRUDLogger.logPost("MAIN /admin/users", newUserRequest.toString());
        UserDto result = userService.create(newUserRequest);
        ColoredCRUDLogger.logPostComplete("MAIN /admin/users", result.toString());
        return result;
    }

    @GetMapping("/admin/users")
    public List<UserDto> getAll(@RequestParam(name = "ids", required = false) Set<Long> ids,
                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        ids = ids != null ? ids : Collections.emptySet();
        String url = String.format("MAIN /admin/users?ids={%s}&from{%s}&size{%s}", ids, from, size);
        ColoredCRUDLogger.logGet(url);
        List<UserDto> result = userService.getAll(mapper.createUserParam(ids, from, size));
        ColoredCRUDLogger.logGetComplete(url, "size = " + result.size());
        return result;
    }

    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        String url = String.format("MAIN /admin/users/{%s}", userId);
        ColoredCRUDLogger.logDelete(url);
        userService.delete(userId);
        ColoredCRUDLogger.logDeleteComplete(url);
    }
}