package com.github.lenemarix.autoplay.dq11.poker.statemachine.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateContext.Stage;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;

/**
 * {@link StateMachine}の状態遷移に関連するログを出力するリスナ。
 */
public class LoggingStatemachineListener extends StateMachineListenerAdapter<States, Events> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingStatemachineListener.class);

    // StateContextにアクセスするにはstateContext()メソッドをオーバーライドする必要がある。
    @Override
    public void stateContext(StateContext<States, Events> stateContext) {
        // 遷移が発生した時のみログ出力する。
        if (!Stage.TRANSITION.equals(stateContext.getStage())) {
            return;
        }

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
