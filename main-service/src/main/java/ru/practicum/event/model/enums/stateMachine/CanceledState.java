package ru.practicum.event.model.enums.stateMachine;

import ru.practicum.event.model.enums.EventState;
import ru.practicum.exception.ConstraintConflictException;

public class CanceledState implements EventStateHandler {
    @Override
    public EventState getNext() {
        return EventState.PENDING;
    }

    @Override
    public EventState getPrevious() {
        throw new ConstraintConflictException("Invalid stateAction .");
    }
}
