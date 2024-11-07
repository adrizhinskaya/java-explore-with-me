package ru.practicum.event.model.enums.stateMachine;

import ru.practicum.event.model.enums.EventState;
import ru.practicum.exception.ConstraintConflictException;

public class PublishedState implements EventStateHandler {
    @Override
    public EventState getNext() {
        throw new ConstraintConflictException("Invalid stateAction .");
    }

    @Override
    public EventState getPrevious() {
        throw new ConstraintConflictException("Invalid stateAction .");
    }
}
