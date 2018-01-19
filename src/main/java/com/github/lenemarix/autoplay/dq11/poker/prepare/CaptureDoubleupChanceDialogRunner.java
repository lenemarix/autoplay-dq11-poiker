package com.github.lenemarix.autoplay.dq11.poker.prepare;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.lenemarix.autoplay.dq11.poker.model.CaptureManager;
import com.github.lenemarix.autoplay.dq11.poker.model.CaptureManager.Capture;
import com.github.lenemarix.autoplay.dq11.poker.util.ActivateWindowUtil;
import com.github.lenemarix.autoplay.dq11.poker.util.ImageUtil;
import com.github.lenemarix.autoplay.dq11.poker.util.RobotUtil;

/**
 * ダブルアップチャンスの選択ダイアログのキャプチャを行う。
 */
@Component
public class CaptureDoubleupChanceDialogRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptureDoubleupChanceDialogRunner.class);

    @Autowired
    RobotUtil robotUtil;

    @Autowired
    ImageUtil imageUtil;

    @Autowired
    ActivateWindowUtil activateWindowUtil;

    @Autowired
    CaptureManager captureRectangleManager;;

    /**
     * ダブルアップチャンスの選択ダイアログをキャプチャする。
     */
    public void capture() {
        // PS4リモートプレイのウィンドウを前面に出す。
        activateWindowUtil.activate();
        Capture doubleupChanceSelectDialogCapture = captureRectangleManager.getMap()
                .get(CaptureManager.DOUBLEUP_CHANCE_SELECT_DIALOG);
        Capture doubleupChanceSelectMessageCapture = captureRectangleManager.getMap()
                .get(CaptureManager.DOUBLEUP_CHANCE_SELECT_MESSAGE);
        try {
            BufferedImage gameScreen = robotUtil.captureGameScreen();
            imageUtil.imageToFile(gameScreen, doubleupChanceSelectDialogCapture.getRectangle(),
                    doubleupChanceSelectDialogCapture.getFilepath());
            imageUtil.imageToFile(gameScreen, doubleupChanceSelectMessageCapture.getRectangle(),
                    doubleupChanceSelectMessageCapture.getFilepath());
            LOGGER.info("doubleup chance select dialog/message capture is done.");
        } catch (IOException e) {
            LOGGER.error("fail to capture doubleup chance select dialog/message", e);
        }
    }
}