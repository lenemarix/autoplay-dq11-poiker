package com.github.lenemarix.autoplay.dq11.poker.statemachine.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;

import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;
import com.github.lenemarix.autoplay.dq11.poker.util.ActivateWindowUtil;

/**
 * PS4リモートプレイのウィンドウを前面に出すためのアクション。
 */
public class ActivateWindowAction extends AbstractAutoplayAction {

    @Autowired
    ActivateWindowUtil activateWindowUtil;

    @Override
    public void doExecute(StateContext<States, Events> context) {
        activateWindowUtil.activate();
    }

}
