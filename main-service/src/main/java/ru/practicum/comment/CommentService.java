package ru.practicum.comment;

import ru.practicum.comment.model.dto.CommentDto;
import ru.practicum.comment.model.dto.CommentFullDto;
import ru.practicum.comment.model.param.AdminCommentParam;
import ru.practicum.comment.model.param.DeleteCommentParam;

import java.util.List;

public interface CommentService {
    CommentFullDto create(Long userId, Long eventId, CommentDto commentDto);

    CommentFullDto update(Long userId, Long commentId, CommentDto commentDto);

    CommentFullDto getById(Long commentId);

    List<CommentFullDto> getAllByEvent(Long eventId, Boolean sortByTime, Integer from, Integer size);

    List<CommentFullDto> getAllByUser(Long userId, Boolean sortByTime, Integer from, Integer size);

    List<CommentFullDto> getAll(AdminCommentParam param);

    void deleteAll(DeleteCommentParam param);

    void delete(Long userId, Long commentId);
}