package com.github.lenemarix.autoplay.dq11.poker.model;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * 画像キャプチャの範囲を表すクラス。
 */
public class CaptureRectangle {
    private int x;
    private int y;
    private int width;
    private int height;

    /**
     * スクリーンキャプチャ範囲の長方形を生成して返す。
     * 
     * @return スクリーンキャプチャ範囲の長方形。
     */
    public Rectangle getRectangle() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * イメージからこのオブジェクトが表すキャプチャ範囲を切り取る。
     * 
     * @param image イメージ。
     * @return キャプチャ範囲で切り取ったイメージ。
     */
    public BufferedImage getSubImage(BufferedImage image) {
         return image.getSubimage(x, y, width, height);
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
}
