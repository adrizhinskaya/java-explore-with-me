package ru.practicum.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Component;
import ru.practicum.event.model.QEventEntity;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.event.model.param.AdminEventParam;
import ru.practicum.event.model.param.EventParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ConditionBuilder {
    public BooleanExpression makeCondition(AdminEventParam param) {
        List<BooleanExpression> conditions = new ArrayList<>();
        addTimeCondition(param.getRangeStart(), param.getRangeEnd(), conditions);
        if (param.getUsers() != null) {
            conditions.add(QEventEntity.eventEntity.initiator.id.in(param.getUsers()));
        }
        if (param.getStates() != null) {
            conditions.add(QEventEntity.eventEntity.state.in(param.getStates()));
        }
        if (param.getCategories() != null) {
            conditions.add(QEventEntity.eventEntity.category.id.in(param.getCategories()));
        }
        if (conditions.isEmpty()) {
            conditions.add(QEventEntity.eventEntity.isNotNull());
        }

        return conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    public BooleanExpression makeCondition(EventParam param) {
        List<BooleanExpression> conditions = new ArrayList<>();
        conditions.add(QEventEntity.eventEntity.state.eq(EventState.PUBLISHED));
        addTextCondition(param, conditions);
        addCategoriesCondition(param, conditions);
        addPaidCondition(param, conditions);
        addTimeCondition(param.getRangeStart(), param.getRangeEnd(), conditions);

        return conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    private void addTextCondition(EventParam param, List<BooleanExpression> conditions) {
        if (param.getText() != null) {
            conditions.add((QEventEntity.eventEntity.annotation.containsIgnoreCase(param.getText())
                    .or(QEventEntity.eventEntity.description.containsIgnoreCase(param.getText()))));
        }
    }

    private void addCategoriesCondition(EventParam param, List<BooleanExpression> conditions) {
        if (param.getCategories() != null) {
            conditions.add((QEventEntity.eventEntity.category.id.in(param.getCategories())));
        }
    }

    private void addPaidCondition(EventParam param, List<BooleanExpression> conditions) {
        if (param.getPaid() != null) {
            conditions.add(QEventEntity.eventEntity.paid.eq(param.getPaid()));
        }
    }

    private void addTimeCondition(LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                  List<BooleanExpression> conditions) {
        QEventEntity event = QEventEntity.eventEntity;
        if (rangeStart != null && rangeEnd != null) {
            conditions.add(event.eventDate.between(rangeStart, rangeEnd));
        }
        if (rangeStart != null) {
            conditions.add(event.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            conditions.add(event.eventDate.before(rangeEnd));
        }
    }
}
