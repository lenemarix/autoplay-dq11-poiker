package com.github.lenemarix.autoplay.dq11.poker.prepare;

import static com.github.lenemarix.autoplay.dq11.poker.model.CaptureManager.CaptureKind.BET_COIN_INPUT_CAPTURE;
import static com.github.lenemarix.autoplay.dq11.poker.model.CaptureManager.CaptureKind.DEAL_CARDS_BUTTON_CAPTURE;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.lenemarix.autoplay.dq11.poker.model.CaptureManager;
import com.github.lenemarix.autoplay.dq11.poker.model.CaptureManager.CaptureKind;
import com.github.lenemarix.autoplay.dq11.poker.model.CaptureRectangle;
import com.github.lenemarix.autoplay.dq11.poker.model.Card;
import com.github.lenemarix.autoplay.dq11.poker.util.ActivateWindowUtil;
import com.github.lenemarix.autoplay.dq11.poker.util.ImageUtil;
import com.github.lenemarix.autoplay.dq11.poker.util.RobotUtil;

/**
 * 画面部品のキャプチャを行う。
 */
@Component
public class CaptureRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptureRunner.class);

    @Autowired
    RobotUtil robotUtil;

    @Autowired
    ImageUtil imageUtil;

    @Autowired
    ActivateWindowUtil activateWindowUtil;

    @Autowired
    CaptureManager captureManager;;

    /**
     * かけ金入力欄のキャプチャを行う。かけ金入力欄が表示され、0が設定されていること。
     */
    public void captureBetCoinInput() {
        CaptureRectangle capture = captureManager.getCaptureRectangle(BET_COIN_INPUT_CAPTURE);
        String filePath = captureManager.getCaptureFilePath(BET_COIN_INPUT_CAPTURE);
        capture(capture.getRectangle(), filePath, "bet coin input");
    }

    /**
     * "くばる"ボタンを画像キャプチャを保存する。 "くばる"ボタンが表示されている状態で実行すること。
     */
    public void captureDealCardsButton() {
        CaptureRectangle capture = captureManager.getCaptureRectangle(DEAL_CARDS_BUTTON_CAPTURE);
        String filePath = captureManager.getCaptureFilePath(DEAL_CARDS_BUTTON_CAPTURE);
        capture(capture.getRectangle(), filePath, "deal card button");
    }

    /**
     * 指定されたカード番号のカードを指定されたカード種別のカードとしてキャプチャし、保存する。
     * キャプチャしたいカードが表示されている状態で実行すること。
     * 
     * @param cardNumber カード番号(左から1枚め)。
     * @param cardType カード種別。
     */
    public void captureCard(int cardNumber, Card cardType) {
        CaptureRectangle capture = captureManager.getCardCapture(cardNumber);
        String filePath = captureManager.getCardCaptureFilePath(cardType);
        capture(capture.getRectangle(), filePath, "card (number: " + cardNumber
                + ", card: " + cardType + ")");
    }

    /**
     * ダブルアップチャンスの選択ダイアログをキャプチャする。
     */
    public void captureDoubleupChanceDialog() {
        // PS4リモートプレイのウィンドウを前面に出す。
        activateWindowUtil.activate();

        CaptureRectangle dialogCapture = 
                captureManager.getCaptureRectangle(CaptureKind.DOUBLEUP_CHANCE_SELECT_DIALOG);
        String dialogFilePath = captureManager.getCaptureFilePath(CaptureKind.DOUBLEUP_CHANCE_SELECT_DIALOG);
        capture(dialogCapture.getRectangle(), dialogFilePath, "doubleup chance select dialog");

        CaptureRectangle messageCapture = 
                captureManager.getCaptureRectangle(CaptureKind.DOUBLEUP_CHANCE_SELECT_MESSAGE);
        String messageFilePath = captureManager.getCaptureFilePath(CaptureKind.DOUBLEUP_CHANCE_SELECT_MESSAGE);
        capture(messageCapture.getRectangle(), messageFilePath, "doubleup chance select message");
    }

    /**
     * キャプチャを行う。
     * 
     * @param captureRect キャプチャ範囲。
     * @param filePath キャプチャ保存先。
     * @param captureName キャプチャする画面部品名。ログ出力で使用。
     */
    private void capture(Rectangle captureRect, String filePath, String captureName) {
        // PS4リモートプレイのウィンドウを前面に出す。
        activateWindowUtil.activate();
        try {
            BufferedImage gameScreen = robotUtil.captureGameScreen();
            imageUtil.imageToFile(gameScreen, captureRect, filePath);
            LOGGER.info("{} capture is done.", captureName);
        } catch (IOException e) {
            LOGGER.error("fail to capture {}", captureName, e);
        }
    }
}