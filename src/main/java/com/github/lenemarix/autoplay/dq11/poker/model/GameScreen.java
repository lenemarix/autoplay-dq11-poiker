package com.github.lenemarix.autoplay.dq11.poker.model;

import java.awt.Rectangle;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * PS4リモートプレイのゲーム画面の長方形の位置・サイズを保持するクラス。
 * この長方形はウィンドウのメニューバーなどは含めない純粋なゲーム画面を表す。
 */
@Component
@ConfigurationProperties(prefix = "game-screen")
public class GameScreen {

    private int locationX;
    private int locationY;
    private int width;
    private int height;

    public Rectangle getRectangle() {
        return new Rectangle(locationX, locationY, width, height);
    }

    public int getLocationX() {
        return locationX;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
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

}
