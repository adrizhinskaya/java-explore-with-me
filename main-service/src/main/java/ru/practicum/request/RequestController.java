package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.logger.ColoredCRUDLogger;
import ru.practicum.request.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.model.dto.ParticipationRequestDto;
import ru.practicum.user.model.dto.UserDto;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable Long userId,
                                          @RequestParam Long eventId) {
        String url = String.format("MAIN /users/{%s}/requests?eventId={%s}", userId, eventId);
        ColoredCRUDLogger.logPost(url);
        ParticipationRequestDto result = requestService.create(userId, eventId);
        ColoredCRUDLogger.logPostComplete(url, result.toString());
        return result;
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        String url = String.format("MAIN /users/{%s}/requests/{%s}/cancel", userId, requestId);
        ColoredCRUDLogger.logPatch(url);
        ParticipationRequestDto result = requestService.cancelRequest(userId, requestId);
        ColoredCRUDLogger.logPatchComplete(url, result.toString());
        return result;
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult update(@PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 @Validated @RequestBody EventRequestStatusUpdateRequest request) {
        String url = String.format("MAIN /users/{%s}/events/{%s}/requests", userId, eventId);
        ColoredCRUDLogger.logPatch(url, request.toString());
        EventRequestStatusUpdateResult result = requestService.update(userId, eventId, request);
        ColoredCRUDLogger.logPatchComplete(url, result.toString());
        return result;
    }

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDto> getAllByRequester(@PathVariable Long userId) {
        String url = String.format("MAIN /users/{%s}/requests", userId);
        ColoredCRUDLogger.logGet(url);
        List<ParticipationRequestDto> result = requestService.getAllByRequester(userId);
        ColoredCRUDLogger.logGetComplete(url, "size = " + result.size());
        return result;
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getAllByRequesterAndEvent(@PathVariable Long userId,
                                                                   @PathVariable Long eventId) {
        String url = String.format("MAIN /users/{%s}/events/{%s}/requests", userId, eventId);
        ColoredCRUDLogger.logGet(url);
        List<ParticipationRequestDto> result = requestService.getAllByInitiatorAndEvent(userId, eventId);
        ColoredCRUDLogger.logGetComplete(url, "size = " + result.size());
        return result;
    }
}
