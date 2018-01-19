package com.github.lenemarix.autoplay.dq11.poker.statemachine.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.statemachine.StateContext;

import com.github.lenemarix.autoplay.dq11.poker.model.GameScreen;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;
import com.github.lenemarix.autoplay.dq11.poker.util.RobotUtil;

/**
 * shareボタンを押下するアクション。
 */
@ConfigurationProperties(prefix = "autoplay.dq11.poker.push-share-button-action")
public class PushShareButtonAction extends AbstractAutoplayAction {

    private int shareButtonX;
    private int shareButtonY;

    @Autowired
    RobotUtil robotUtil;

    @Autowired
    GameScreen gameScreen;

    @Override
    public void doExecute(StateContext<States, Events> context) {
        robotUtil.mouseMove(shareButtonX + gameScreen.getLocationX(),
                shareButtonY + gameScreen.getLocationY());
        robotUtil.mouseLeftPress();
    }

    public int getShareButtonX() {
        return shareButtonX;
    }

    public void setShareButtonX(int shareButtonX) {
        this.shareButtonX = shareButtonX;
    }

    public int getShareButtonY() {
        return shareButtonY;
    }

    public void setShareButtonY(int shareButtonY) {
        this.shareButtonY = shareButtonY;
    }

}
