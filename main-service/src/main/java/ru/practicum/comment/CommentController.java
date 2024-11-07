package ru.practicum.comment;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.model.dto.CommentDto;
import ru.practicum.comment.model.dto.CommentFullDto;
import ru.practicum.comment.model.param.AdminCommentParam;
import ru.practicum.comment.model.param.DeleteCommentParam;
import ru.practicum.logger.ColoredCRUDLogger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping("/users/{userId}/comments/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto create(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @Validated @RequestBody CommentDto commentDto) {
        String url = String.format("MAIN /users/{%s}/comments/{%s}", userId, eventId);
        ColoredCRUDLogger.logPost(url, commentDto.toString());
        CommentFullDto result = commentService.create(userId, eventId, commentDto);
        ColoredCRUDLogger.logPostComplete(url, result.toString());
        return result;
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    public CommentFullDto update(@PathVariable Long userId,
                                 @PathVariable Long commentId,
                                 @Validated @RequestBody CommentDto commentDto) {
        String url = String.format("MAIN /users/{%s}/comments/{%s}", userId, commentId);
        ColoredCRUDLogger.logPatch(url, commentDto.toString());
        CommentFullDto result = commentService.update(userId, commentId, commentDto);
        ColoredCRUDLogger.logPatchComplete(url, result.toString());
        return result;
    }

    @GetMapping("/admin/comments/{commentId}")
    public CommentFullDto getById(@PathVariable Long commentId) {
        String url = String.format("MAIN /admin/comments/{%s}", commentId);
        ColoredCRUDLogger.logGet(url);
        CommentFullDto result = commentService.getById(commentId);
        ColoredCRUDLogger.logGetComplete(url, result.toString());
        return result;
    }

    @GetMapping("/admin/events/{eventId}/comments")
    public List<CommentFullDto> getAllByEvent(@PathVariable Long eventId,
                                              @RequestParam(name = "sortByTime", defaultValue = "false")
                                              Boolean sortByTime,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                              Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10")
                                              Integer size) {
        String url = String.format("MAIN /admin/comments/{%s}?{%s}&{%s}&{%s}", eventId, sortByTime, from, size);
        ColoredCRUDLogger.logGet(url);
        List<CommentFullDto> result = commentService.getAllByEvent(eventId, sortByTime, from, size);
        ColoredCRUDLogger.logGetComplete(url, "size = " + result.size());
        return result;
    }

    @GetMapping("/admin/users/{userId}/comments")
    public List<CommentFullDto> getAllByUser(@PathVariable Long userId,
                                             @RequestParam(name = "sortByTime", defaultValue = "false")
                                             Boolean sortByTime,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                             Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        String url = String.format("MAIN /admin/{%s}/comments?{%s}&{%s}&{%s}", userId, sortByTime, from, size);
        ColoredCRUDLogger.logGet(url);
        List<CommentFullDto> result = commentService.getAllByUser(userId, sortByTime, from, size);
        ColoredCRUDLogger.logGetComplete(url, "size = " + result.size());
        return result;
    }

    @GetMapping("/admin/comments")
    public List<CommentFullDto> getAll(@RequestParam(required = false, name = "text") String text,
                                       @RequestParam(required = false, name = "comments") Set<Long> comments,
                                       @RequestParam(required = false, name = "rangeStart") String rangeStart,
                                       @RequestParam(required = false, name = "rangeEnd") String rangeEnd,
                                       @RequestParam(name = "sortByTime", defaultValue = "false") Boolean sortByTime,
                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                       @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        String url = String.format("MAIN /admin/comments?{%s}&{%s}&{%s}&{%s}&{%s}&{%s}&{%s}",
                text, comments, rangeStart, rangeEnd, sortByTime, from, size);
        ColoredCRUDLogger.logGet(url);
        LocalDateTime start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, formatter);
        AdminCommentParam param = commentMapper.createAdminCommentParam(text, comments, start, end,
                sortByTime, from, size);
        List<CommentFullDto> result = commentService.getAll(param);
        ColoredCRUDLogger.logGetComplete(url, "size = " + result.size());
        return result;
    }

    @DeleteMapping("/admin/comments/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll(@RequestParam(required = false, name = "text") String text,
                          @RequestParam(required = false, name = "comments") Set<Long> comments,
                          @RequestParam(required = false, name = "rangeStart") String rangeStart,
                          @RequestParam(required = false, name = "rangeEnd") String rangeEnd) {
        String url = String.format("MAIN /admin/comments/delete?{%s}&{%s}&{%s}&{%s}",
                text, comments, rangeStart, rangeEnd);
        ColoredCRUDLogger.logDelete(url);
        LocalDateTime start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, formatter);
        DeleteCommentParam param = commentMapper.createDeleteCommentParam(text, comments, start, end);
        commentService.deleteAll(param);
        ColoredCRUDLogger.logDeleteComplete(url);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId,
                       @PathVariable Long commentId) {
        String url = String.format("MAIN /users/{%s}/comments/{%s}", userId, commentId);
        ColoredCRUDLogger.logDelete(url);
        commentService.delete(userId, commentId);
        ColoredCRUDLogger.logDeleteComplete(url);
    }
}