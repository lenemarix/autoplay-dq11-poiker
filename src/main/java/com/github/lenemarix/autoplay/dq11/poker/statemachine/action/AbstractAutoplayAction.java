package com.github.lenemarix.autoplay.dq11.poker.statemachine.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;

/**
 * 共通Action。実行前にログを出力する。
 */
public abstract class AbstractAutoplayAction implements Action<States, Events> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAutoplayAction.class);

    @Override
    public void execute(StateContext<States, Events> context) {
        log(context);
        doExecute(context);
    }

    public abstract void doExecute(StateContext<States, Events> context);

    private void log(StateContext<States, Events> context) {
        String eventId = context.getMessageHeaders().get("eventId", String.class);
        if (eventId == null) {
            eventId = "-";
        }
        String actionClassName = this.getClass().getSimpleName();
        LOGGER.info("[Action    ] EventId: {}, Action: {}", eventId, actionClassName);
    }

}
