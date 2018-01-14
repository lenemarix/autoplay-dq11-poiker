package com.github.lenemarix.autoplay.dq11.poker.prepare;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.lenemarix.autoplay.dq11.poker.model.DealCardsButtonCapture;
import com.github.lenemarix.autoplay.dq11.poker.util.ActivateWindowUtil;
import com.github.lenemarix.autoplay.dq11.poker.util.ImageUtil;
import com.github.lenemarix.autoplay.dq11.poker.util.RobotUtil;

/**
 * ボタンのキャプチャを行うクラス。 "くばる"ボタンの画像キャプチャは、カードが配られた状態で有ることを検出するのに使用する。
 */
@Component
public class CaptureDealCardsButtonRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptureDealCardsButtonRunner.class);

    @Autowired
    RobotUtil robotUtil;

    @Autowired
    ImageUtil imageUtil;

    @Autowired
    ActivateWindowUtil activateWindowUtil;

    @Autowired
    DealCardsButtonCapture dealCardsButtonCapture;

    /**
     * "くばる"ボタンを画像キャプチャを保存する。 "くばる"ボタンが表示されている状態で実行すること。
     */
    public void capture() {
        // PS4リモートプレイのウィンドウを前面に出す。
        activateWindowUtil.activate();
        try {
            BufferedImage gameScreen = robotUtil.captureGameScreen();
            imageUtil.imageToFile(gameScreen, dealCardsButtonCapture.getRectangle(),
                    dealCardsButtonCapture.getFilepath());
            LOGGER.info("deal card button capture is done.");
        } catch (IOException e) {
            LOGGER.error("fail to capture card", e);
        }
    }
}
