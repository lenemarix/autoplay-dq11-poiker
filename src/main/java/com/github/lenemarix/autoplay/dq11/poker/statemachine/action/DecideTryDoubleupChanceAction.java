package com.github.lenemarix.autoplay.dq11.poker.statemachine.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.statemachine.StateContext;

import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;
import com.github.lenemarix.autoplay.dq11.poker.util.RobotUtil;

/**
 * ダブルアップチャンスのダイアログを選択するアクション。 
 * どちらを選択するかはパラメータによって設定。
 */
@ConfigurationProperties(prefix = "autoplay.dq11.poker.decide-try-doubleup-chance-action")
public class DecideTryDoubleupChanceAction extends AbstractAutoplayAction {

    private boolean tryDoubleup;

    @Autowired
    RobotUtil robotUtil;

    @Override
    public void doExecute(StateContext<States, Events> context) {
        if (!tryDoubleup) {
            // 下キーを押して"いいえ"にカーソル移動。
            robotUtil.downKeyPress();
        }
        // 決定は後続のOTHER_STATEのEntry Actionで行う。
    }

    public boolean isTryDoubleup() {
        return tryDoubleup;
    }

    public void setTryDoubleup(boolean tryDoubleup) {
        this.tryDoubleup = tryDoubleup;
    }
}
