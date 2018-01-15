package com.github.lenemarix.autoplay.dq11.poker.statemachine.action;

import java.awt.event.MouseEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.statemachine.StateContext;

import com.github.lenemarix.autoplay.dq11.poker.model.GameScreen;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;
import com.github.lenemarix.autoplay.dq11.poker.util.RobotUtil;

/**
 * PSボタンを押してプレイを中断する。
 */
@ConfigurationProperties(prefix = "autoplay.dq11.poker.push-ps-button-action")
public class PushPsButtonAction extends AbstractAutoplayAction {

    /** PSボタンのX座標。 */
    private int psButtonX;
    /** PSボタンのY座標。 */
    private int psButtonY;

    @Autowired
    GameScreen gameScreen;

    public int getPsButtonX() {
        return psButtonX;
    }

    public void setPsButtonX(int psButtonX) {
        this.psButtonX = psButtonX;
    }

    public int getPsButtonY() {
        return psButtonY;
    }

    public void setPsButtonY(int psButtonY) {
        this.psButtonY = psButtonY;
    }

    @Autowired
    RobotUtil robotUtil;

    @Override
    public void doExecute(StateContext<States, Events> context) {
        robotUtil.mouseMove(psButtonX + gameScreen.getLocationX(), psButtonY + gameScreen.getLocationY());
        robotUtil.mousePress(MouseEvent.BUTTON1_DOWN_MASK);

    }

}
