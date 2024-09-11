package ru.practicum.user;

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

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

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
                                @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        ids = ids != null ? ids : Collections.emptySet();
        String url = String.format("MAIN /admin/users?ids={%s}&from{%s}&size{%s}", ids.toString(), from, size);
        ColoredCRUDLogger.logGet(url);
        List<UserDto> result = userService.getAll(ids, from, size);
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