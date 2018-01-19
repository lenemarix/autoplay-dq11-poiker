package com.github.lenemarix.autoplay.dq11.poker.statemachine.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;

import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;

/**
 * {@link StateMachine}の状態遷移に関連するログを出力するリスナ。
 */
@WithStateMachine
public class LoggingStatemachineListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingStatemachineListener.class);
    
    @OnTransition
    public void onTransition(StateContext<States, Events> stateContext) {
        String eventId = stateContext.getMessageHeaders().get("eventId", String.class);
        if (eventId == null) {
            eventId = "-";
        }
        String eventName = "-";
        if (stateContext.getEvent() != null) {
            eventName = stateContext.getEvent().name();
        }
        String from = "-";
        if (stateContext.getSource() != null) {
            from = stateContext.getSource().getId().toString();
        }
        String to = "-";
        if (stateContext.getTarget() != null) {
            to = stateContext.getTarget().getId().toString();
        }
        String transitionKind = "-";
        if (stateContext.getTransition() != null) {
            transitionKind = stateContext.getTransition().getKind().toString();
        }

        LOGGER.info("[Transition] EventId: {}, Event: {}, from: {}, to: {}, transitionKind: {}",
                eventId, eventName, from, to, transitionKind);
    }
}
