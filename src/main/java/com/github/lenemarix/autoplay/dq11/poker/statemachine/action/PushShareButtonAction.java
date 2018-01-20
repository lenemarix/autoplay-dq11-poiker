package com.github.lenemarix.autoplay.dq11.poker.statemachine.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(
            PushShareButtonAction.class);

    /** Shareボタンのゲーム画面内X座標。 */
    private int shareButtonX;

    /** Shareボタンのゲーム画面内Y座標。 */
    private int shareButtonY;

    /** Shareボタンを押すまで待つ時間(ms)。ファンファーレ終了を待つ想定。 */
    private int pushDelay;

    @Autowired
    RobotUtil robotUtil;

    @Autowired
    GameScreen gameScreen;

    @Override
    public void doExecute(StateContext<States, Events> context) {
        LOGGER.info("Start delay before push share button: {}ms", pushDelay);
        robotUtil.delay(pushDelay);
        LOGGER.info("End delay before push share button: {}ms", pushDelay);
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

    public int getPushDelay() {
        return pushDelay;
    }

    public void setPushDelay(int pushDelay) {
        this.pushDelay = pushDelay;
    }

}
