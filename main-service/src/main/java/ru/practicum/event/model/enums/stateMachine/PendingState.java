package ru.practicum.event.model.enums.stateMachine;

import ru.practicum.event.model.enums.EventState;

public class PendingState implements EventStateHandler {
    @Override
    public EventState getNext() {
        return EventState.PUBLISHED;
    }

    @Override
    public EventState getPrevious() {
        return EventState.CANCELED;
    }
}

