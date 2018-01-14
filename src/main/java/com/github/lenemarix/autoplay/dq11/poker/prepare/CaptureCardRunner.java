package com.github.lenemarix.autoplay.dq11.poker.prepare;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.lenemarix.autoplay.dq11.poker.model.Card;
import com.github.lenemarix.autoplay.dq11.poker.model.DealtCardsCapture;
import com.github.lenemarix.autoplay.dq11.poker.util.ActivateWindowUtil;
import com.github.lenemarix.autoplay.dq11.poker.util.ImageUtil;
import com.github.lenemarix.autoplay.dq11.poker.util.RobotUtil;

/**
 * 配布済みカードのキャプチャを行うクラス。
 */
@Component
public class CaptureCardRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptureCardRunner.class);

    @Autowired
    RobotUtil robotUtil;

    @Autowired
    ImageUtil imageUtil;

    @Autowired
    ActivateWindowUtil activateWindowUtil;

    @Autowired
    DealtCardsCapture dealtCardsCapture;

    /**
     * 指定されたカード番号のカードを指定されたカード種別のカードとしてキャプチャし、保存する。
     * キャプチャしたいカードが表示されている状態で実行すること。
     * 
     * @param cardNumber
     *            カード番号(左から1枚め)。
     * @param cardType
     *            カード種別。
     */
    public void capture(int cardNumber, Card cardType) {
        // PS4リモートプレイのウィンドウを前面に出す。
        activateWindowUtil.activate();
        try {
            BufferedImage gameScreen = robotUtil.captureGameScreen();
            imageUtil.imageToFile(gameScreen, dealtCardsCapture.getRectangle(cardNumber),
                    dealtCardsCapture.getFilepath(cardType));
            LOGGER.info("card capture is done. cardNumber: " + cardNumber + ", card: " + cardType);
        } catch (IOException e) {
            LOGGER.error("fail to capture card", e);
        }
    }
}
