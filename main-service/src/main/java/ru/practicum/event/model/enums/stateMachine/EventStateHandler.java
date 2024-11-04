package ru.practicum.event.model.enums.stateMachine;

import ru.practicum.event.model.enums.EventState;

public interface EventStateHandler {
    EventState getNext();

    EventState getPrevious();
}