package ru.practicum.event.model.enums.stateMachine;

import ru.practicum.event.model.EventEntity;
import ru.practicum.event.model.enums.AdminStateAction;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.event.model.enums.UserStateAction;
import ru.practicum.exception.ConstraintConflictException;

import java.util.HashMap;
import java.util.Map;

public class StateMachine {
    private Map<EventState, EventStateHandler> stateHandlers;

    public StateMachine() {
        stateHandlers = new HashMap<>();
        stateHandlers.put(EventState.PENDING, new PendingState());
        stateHandlers.put(EventState.CANCELED, new CanceledState());
        stateHandlers.put(EventState.PUBLISHED, new PublishedState());
    }

    public void updateEventState(EventEntity event, UserStateAction action) {
        switch (action) {
            case CANCEL_REVIEW:
                event.setState(stateHandlers.get(event.getState()).getPrevious());
                break;
            case SEND_TO_REVIEW:
                if (event.getState() == EventState.PENDING)
                    throw new ConstraintConflictException("Invalid stateAction .");
                event.setState(stateHandlers.get(event.getState()).getNext());
                break;
        }
    }

    public void updateEventState(EventEntity event, AdminStateAction state) {
        switch (state) {
            case REJECT_EVENT:
                event.setState(stateHandlers.get(event.getState()).getPrevious());
                break;
            case PUBLISH_EVENT:
                if (event.getState() == EventState.CANCELED)
                    throw new ConstraintConflictException("Invalid stateAction .");
                event.setState(stateHandlers.get(event.getState()).getNext());
                break;
        }
    }
}
