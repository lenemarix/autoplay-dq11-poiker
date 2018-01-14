package com.github.lenemarix.autoplay.dq11.poker.statemachine.listener;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateContext.Stage;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;

/**
 *  カード配布済み状態に入った日時をExtendedStateで保持するためのリスナ。
 */
public class DealtCardsStateStartDateSaveListener extends StateMachineListenerAdapter<States, Events> {

    public static final String DEALT_CARDS_STATE_START_DATE = "DEALT_CARDS_STATE.startDate";

    private static final Logger LOGGER = LoggerFactory.getLogger(DealtCardsStateStartDateSaveListener.class);

    @Override
    public void stateContext(StateContext<States, Events> stateContext) {

        if (Stage.STATE_CHANGED.equals(stateContext.getStage())) {
            LocalDateTime dealtCardsStateStartDate = null;
            if (States.DEALT_CARDS_STATE.equals(stateContext.getTarget().getId())) {
                dealtCardsStateStartDate = LocalDateTime.now();
                stateContext.getExtendedState().getVariables().put(DEALT_CARDS_STATE_START_DATE, dealtCardsStateStartDate);
            } else {
                stateContext.getExtendedState().getVariables().remove(DEALT_CARDS_STATE_START_DATE);
            }
            LOGGER.debug("save dealt cards state start Date: {}", dealtCardsStateStartDate);
        }

    }

}
