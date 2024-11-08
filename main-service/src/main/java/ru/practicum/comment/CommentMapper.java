package ru.practicum.comment;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.comment.model.CommentEntity;
import ru.practicum.comment.model.dto.CommentDto;
import ru.practicum.comment.model.dto.CommentFullDto;
import ru.practicum.comment.model.param.AdminCommentParam;
import ru.practicum.comment.model.param.DeleteCommentParam;
import ru.practicum.event.EventMapper;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.model.dto.EventDto;
import ru.practicum.exception.ValidationBadRequestException;
import ru.practicum.user.UserMapper;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.model.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public class CommentMapper {
    private final EventMapper eventMapper;
    private final UserMapper userMapper;

    public CommentMapper() {
        this.eventMapper = new EventMapper();
        this.userMapper = new UserMapper();
    }

    public CommentEntity toCommentEntity(CommentDto commentDto, UserEntity userEntity, EventEntity eventEntity) {
        return CommentEntity.builder()
                .content(commentDto.getContent())
                .user(userEntity)
                .event(eventEntity)
                .sendTime(LocalDateTime.now())
                .isEdited(false)
                .build();
    }

    public CommentFullDto toCommentFullDto(CommentEntity entity) {
        EventDto eventDto = eventMapper.toEventDto(entity.getEvent());
        UserShortDto userShortDto = userMapper.toUserShortDto(entity.getUser());
        return CommentFullDto.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .user(userShortDto)
                .event(eventDto)
                .sendTime(entity.getSendTime())
                .isEdited(entity.getIsEdited())
                .build();
    }

    public List<CommentFullDto> toCommentFullDto(Iterable<CommentEntity> commentEntities) {
        List<CommentFullDto> dtos = new ArrayList<>();
        for (CommentEntity entity : commentEntities) {
            dtos.add(toCommentFullDto(entity));
        }
        return dtos;
    }

    public AdminCommentParam createAdminCommentParam(String text,
                                                     Set<Long> comments,
                                                     LocalDateTime rangeStart,
                                                     LocalDateTime rangeEnd,
                                                     Boolean sort,
                                                     Integer from,
                                                     Integer size) {
        validateParam(comments, rangeStart, rangeEnd);
        return AdminCommentParam.builder()
                .text(text)
                .comments(comments)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .sort(sort)
                .from(from)
                .size(size)
                .build();
    }

    public DeleteCommentParam createDeleteCommentParam(String text,
                                                       Set<Long> comments,
                                                       LocalDateTime rangeStart,
                                                       LocalDateTime rangeEnd) {
        validateParam(comments, rangeStart, rangeEnd);
        return DeleteCommentParam.builder()
                .text(text)
                .comments(comments)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .build();
    }

    private void validateParam(Set<Long> comments,
                               LocalDateTime rangeStart,
                               LocalDateTime rangeEnd) {
        if (comments != null && !comments.stream().allMatch(id -> id > 0)) {
            throw new ValidationBadRequestException("Some id in comments are not valid.");
        }
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationBadRequestException("rangeStart should be before rangeEnd.");
        }
    }
}