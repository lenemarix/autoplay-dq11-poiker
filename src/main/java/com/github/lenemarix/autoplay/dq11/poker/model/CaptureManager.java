package com.github.lenemarix.autoplay.dq11.poker.model;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 画像キャプチャを管理するクラス。
 */
@Component
@ConfigurationProperties("autoplay.dq11.poker.capture")
public class CaptureManager {

    public static final String DEAL_CARDS_BUTTON = "deal-cards-button";
    public static final String BET_COIN_INPUT = "bet-coin-input";
    public static final String DOUBLEUP_CHANCE_SELECT_DIALOG = "doubleup-chance-select-dialog";
    public static final String DOUBLEUP_CHANCE_SELECT_MESSAGE = "doubleup-chance-select-message";
    
    private Map<String, Capture> map = new HashMap<>();

    public Map<String, Capture> getMap() {
        return map;
    }

    public void setMap(Map<String, Capture> map) {
        this.map = map;
    }

    /**
     * 画像キャプチャの情報。
     */
    public static class Capture {
        private String filepath;
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
    }
}
