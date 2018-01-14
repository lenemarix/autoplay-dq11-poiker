package com.github.lenemarix.autoplay.dq11.poker.model;

import java.awt.Rectangle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.github.lenemarix.autoplay.dq11.poker.util.RobotUtil;

/**
 * "くばる"ボタンのスクリーンキャプチャに関するクラス。
 */
@Component
@ConfigurationProperties(prefix = "autoplay.dq11.poker.deal-cards-button-capture")
public class DealCardsButtonCapture {

    @Autowired
    RobotUtil robotUtil;

    @Autowired
    GameScreen gameScreen;

    /** スクリーンキャプチャの保存先ファイルパス。 */
    private String filepath;
    /** スクリーンキャプチャの座標。 */
    private int x;
    private int y;
    private int width;
    private int height;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * スクリーンキャプチャ範囲の長方形を生成して返す。
     * 
     * @return スクリーンキャプチャ範囲の長方形。
     */
    public Rectangle getRectangle() {
        return new Rectangle(x, y, width, height);
    }
}
