package com.github.lenemarix.autoplay.dq11.poker.statemachine.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;

import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;
import com.github.lenemarix.autoplay.dq11.poker.util.RobotUtil;

/**
 * Enterキーを押して次に進めるためのアクション。
 */
public class EnterKeyPushAction extends AbstractAutoplayAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnterKeyPushAction.class);

    @Autowired
    RobotUtil robotUtil;

    @Override
    public void doExecute(StateContext<States, Events> context) {
        robotUtil.enterKeyPress();
    }
}
