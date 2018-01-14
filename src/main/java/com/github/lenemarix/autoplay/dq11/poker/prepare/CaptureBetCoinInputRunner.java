package com.github.lenemarix.autoplay.dq11.poker.prepare;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.lenemarix.autoplay.dq11.poker.model.BetCoinInputCapture;
import com.github.lenemarix.autoplay.dq11.poker.util.ActivateWindowUtil;
import com.github.lenemarix.autoplay.dq11.poker.util.ImageUtil;
import com.github.lenemarix.autoplay.dq11.poker.util.RobotUtil;

/**
 * かけ金入力欄のキャプチャを行う。
 */
@Component
public class CaptureBetCoinInputRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptureBetCoinInputRunner.class);

    @Autowired
    RobotUtil robotUtil;

    @Autowired
    ImageUtil imageUtil;

    @Autowired
    ActivateWindowUtil activateWindowUtil;

    @Autowired
    BetCoinInputCapture betCoinInputCapture;

    /**
     * かけ金入力欄のキャプチャを行う。かけ金入力欄が表示され、0が設定されていること。
     */
    public void capture() {
        // PS4リモートプレイのウィンドウを前面に出す。
        activateWindowUtil.activate();
        try {
            BufferedImage gameScreen = robotUtil.captureGameScreen();
            imageUtil.imageToFile(gameScreen, betCoinInputCapture.getRectangle(),
                    betCoinInputCapture.getFilepath());
            LOGGER.info("deal card button capture is done.");
        } catch (IOException e) {
            LOGGER.error("fail to capture card", e);
        }
    }
}