package com.github.lenemarix.autoplay.dq11.poker.util;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.lenemarix.autoplay.dq11.poker.model.GameScreen;

/**
 * {@link Robot}の操作をラップするユーティリティ。
 */
@Component
public class RobotUtil {

    @Autowired
    Robot robot;

    @Autowired
    GameScreen gameScreen;

    /**
     * ゲーム画面全体のキャプチャを取得。
     * 
     * @return ゲーム画面全体のキャプチャ。
     */
    public BufferedImage captureGameScreen() {
        return capture(gameScreen.getRectangle());
    }

    /**
     * ゲーム画面全体のキャプチャを連続で取得。
     * 
     * @param count 取得回数。
     * @param interval 取得する間隔(ms)。
     * @return ゲーム画面全体のキャプチャのリスト。
     */
    public List<BufferedImage> captureGameScreenBurst(int count, int interval) {
        return IntStream.range(0, count)
                .mapToObj(c -> {
                    BufferedImage capture = captureGameScreen();
                    robot.delay(interval);
                    return capture;
                })
                .collect(Collectors.toList());
    }

    /**
     * 指定範囲の画面キャプチャを取得。
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @return 画面キャプチャ。
     */
    public BufferedImage capture(int x, int y, int width, int height) {
        return capture(new Rectangle(x, y, width, height));
    }

    /**
     * 指定範囲の画面キャプチャを取得。
     * 
     * @param screenRect
     * @return 画面キャプチャ。
     */
    public BufferedImage capture(Rectangle screenRect) {
        return robot.createScreenCapture(screenRect);
    }

    /**
     * キーを押下して離す。
     * 
     * @param keyEvent
     */
    public void keyPress(int keyEvent) {
        robot.keyPress(keyEvent);
        robot.keyRelease(keyEvent);
    }

    /**
     * マウスをクリックして離す。
     * 
     * @param mouseEvent
     */
    public void mousePress(int mouseEvent) {
        robot.mousePress(mouseEvent);
        robot.mouseRelease(mouseEvent);
    }

    /**
     * マウスカーソルを移動する。
     * 
     * @param x
     * @param y
     */
    public void mouseMove(int x, int y) {
        robot.mouseMove(x, y);
    }

    /**
     * Enterキーを押下する。
     */
    public void enterKeyPress() {
        keyPress(KeyEvent.VK_ENTER);
    }

    /**
     * ↑キーを押下する。
     */
    public void upKeyPress() {
        keyPress(KeyEvent.VK_UP);
    }

    /**
     * ↓キーを押下する。
     */
    public void downKeyPress() {
        keyPress(KeyEvent.VK_DOWN);
    }

    /**
     * →キーを押下する。
     */
    public void rightKeyPress() {
        keyPress(KeyEvent.VK_RIGHT);
    }

    /**
     * 受け取ったコマンドに応じたキーを順番に実行する。
     * 有効な文字は以下の通り
     * <ul>
     * <li>R: 右キー</li>
     * <li>D: 下キー</li>
     * <li>C: Enterキー(○ボタン)</li>
     * </ul>
     * 
     * @param command コマンドを羅列した文字列。
     */
    public void keyPressByCommand(String command) {
        command.chars().forEach(c -> {
            switch (c) {
            case 'R':
                rightKeyPress();
                break;
            case 'D':
                downKeyPress();
                break;
            case 'C':
                enterKeyPress();
                break;
            default:
                throw new IllegalArgumentException("Illegal command: " + (char)c);
            }
        });
    }

    /**
     * Robotに設定されているautoDelay値の分だけ待つ。
     */
    public void delay() {
        robot.delay(robot.getAutoDelay());
    }
}
