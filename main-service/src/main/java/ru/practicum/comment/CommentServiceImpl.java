package ru.practicum.comment;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.comment.model.CommentEntity;
import ru.practicum.comment.model.QCommentEntity;
import ru.practicum.comment.model.dto.CommentDto;
import ru.practicum.comment.model.dto.CommentFullDto;
import ru.practicum.comment.model.param.AdminCommentParam;
import ru.practicum.comment.model.param.DeleteCommentParam;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.EventEntity;
import ru.practicum.exception.ConstraintConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.pagination.PaginationHelper;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.UserEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentFullDto create(Long userId, Long eventId, CommentDto commentDto) {
        UserEntity user = userExistCheck(userId);
        EventEntity event = eventExistCheck(eventId);
        CommentEntity entity = commentRepository.save(commentMapper.toCommentEntity(commentDto, user, event));
        return commentMapper.toCommentFullDto(entity);
    }

    @Override
    public CommentFullDto update(Long userId, Long commentId, CommentDto commentDto) {
        CommentEntity comment = commentExistCheck(commentId);
        userIsCommentatorCheck(comment, userId);
        if (commentDto.getContent() != null) {
            comment.setContent(commentDto.getContent());
            comment.setIsEdited(true);
            comment.setSendTime(LocalDateTime.now());
        }
        CommentEntity entity = commentRepository.save(comment);
        return commentMapper.toCommentFullDto(entity);
    }

    @Override
    public CommentFullDto getById(Long commentId) {
        return commentMapper.toCommentFullDto(commentExistCheck(commentId));
    }

    @Override
    public List<CommentFullDto> getAllByEvent(Long eventId, Boolean sortByTime, Integer from, Integer size) {
        eventExistCheck(eventId);
        BooleanExpression finalCondition = QCommentEntity.commentEntity.event.id.eq(eventId);
        List<CommentEntity> entities = getAllWithPagination(from, size, finalCondition, makeSort(sortByTime));
        return commentMapper.toCommentFullDto(entities);
    }

    @Override
    public List<CommentFullDto> getAllByUser(Long userId, Boolean sortByTime, Integer from, Integer size) {
        userExistCheck(userId);
        BooleanExpression finalCondition = QCommentEntity.commentEntity.user.id.eq(userId);
        List<CommentEntity> entities = getAllWithPagination(from, size, finalCondition, makeSort(sortByTime));
        return commentMapper.toCommentFullDto(entities);
    }

    @Override
    public List<CommentFullDto> getAll(AdminCommentParam param) {
        List<BooleanExpression> conditions = new ArrayList<>();
        addTextCondition(param.getText(), conditions);
        addCommentsCondition(param.getComments(), conditions);
        addTimeCondition(param.getRangeStart(), param.getRangeEnd(), conditions);
        List<CommentEntity> entities = getAllWithPagination(param.getFrom(), param.getSize(),
                makeCondition(conditions), makeSort(param.getSort()));
        return commentMapper.toCommentFullDto(entities);
    }

    @Override
    public void delete(Long userId, Long commentId) {
        CommentEntity comment = commentExistCheck(commentId);
        userIsCommentatorCheck(comment, userId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteAll(DeleteCommentParam param) {
        List<BooleanExpression> conditions = new ArrayList<>();
        addTextCondition(param.getText(), conditions);
        addCommentsCondition(param.getComments(), conditions);
        addTimeCondition(param.getRangeStart(), param.getRangeEnd(), conditions);
        Iterable<CommentEntity> comments = commentRepository.findAll(makeCondition(conditions));
        commentRepository.deleteAll(comments);
    }

    private List<CommentEntity> getAllWithPagination(int from, int size, BooleanExpression finalCondition, Sort sort) {
        PaginationHelper<CommentEntity> ph = new PaginationHelper<>(from, size);
        Page<CommentEntity> firstPage = commentRepository.findAll(finalCondition, ph.getPageRequestForFirstPage(sort));
        Page<CommentEntity> nextPage = firstPage.hasNext() ? commentRepository.findAll(finalCondition,
                ph.getPageRequestForNextPage(sort)) : null;
        return ph.mergePages(firstPage, nextPage);
    }

    private BooleanExpression makeCondition(List<BooleanExpression> conditions) {
        if (conditions.isEmpty()) {
            conditions.add(QCommentEntity.commentEntity.isNotNull());
        }
        return conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    private void addTextCondition(String text, List<BooleanExpression> conditions) {
        if (text != null) {
            conditions.add(QCommentEntity.commentEntity.content.containsIgnoreCase(text));
        }
    }

    private void addCommentsCondition(Set<Long> comments, List<BooleanExpression> conditions) {
        if (comments != null) {
            conditions.add((QCommentEntity.commentEntity.id.in(comments)));
        }
    }

    private void addTimeCondition(LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                  List<BooleanExpression> conditions) {
        QCommentEntity comment = QCommentEntity.commentEntity;
        if (rangeStart != null && rangeEnd != null) {
            conditions.add(comment.sendTime.between(rangeStart, rangeEnd));
        }
        if (rangeStart != null) {
            conditions.add(comment.sendTime.after(rangeStart));
        }
        if (rangeEnd != null) {
            conditions.add(comment.sendTime.before(rangeEnd));
        }
    }

    private Sort makeSort(Boolean sortByTime) {
        return sortByTime ? Sort.by("sendTime").descending() : null;
    }

    private CommentEntity commentExistCheck(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException("Comment not found ."));
    }

    private UserEntity userExistCheck(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found ."));
    }

    private EventEntity eventExistCheck(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event not found ."));
    }

    private void userIsCommentatorCheck(CommentEntity comment, Long userId) {
        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new ConstraintConflictException("User is not commentator .");
        }
    }
}