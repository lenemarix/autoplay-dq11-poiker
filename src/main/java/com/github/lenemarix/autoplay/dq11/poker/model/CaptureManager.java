package com.github.lenemarix.autoplay.dq11.poker.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 画像キャプチャを管理するクラス。
 */
@Component
@ConfigurationProperties("autoplay.dq11.poker.capture")
public class CaptureManager {

    public enum CaptureKind {
        DEAL_CARDS_BUTTON_CAPTURE, 
        BET_COIN_INPUT_CAPTURE,
        DOUBLEUP_CHANCE_SELECT_DIALOG,
        DOUBLEUP_CHANCE_SELECT_MESSAGE
    }

    /** キャプチャする画面部品とキャプチャ範囲を対応付けたMap。 */
    private Map<CaptureKind, CaptureRectangle> rectangleMap = new HashMap<>();

    /** 画面部品とそのキャプチャ画像のファイルパスを対応付けたMap。 */
    private Map<CaptureKind, String> filePathMap = new HashMap<>();

    /** カードのキャプチャ範囲を格納したリスト。 */
    private List<CaptureRectangle> cardRectangleList = new ArrayList<>();

    /** カードとそのキャプチャ画像のファイルパスを対応付けたMap。 */
    private Map<Card, String> cardFilePathMap = new HashMap<>();

    /**
     * 画面部品のキャプチャ範囲を返す。
     * 
     * @param kind 画面部品の種類。
     * @return キャプチャ範囲。
     */
    public CaptureRectangle getCaptureRectangle(CaptureKind kind) {
        return rectangleMap.get(kind);
    }

    /**
     * 画面部品のキャプチャファイルのパスを返す。
     * 
     * @param kind 画面部品の種類。
     * @return ファイルパス。
     */
    public String getCaptureFilePath(CaptureKind kind) {
        return filePathMap.get(kind);
    }

    /**
     * カードのキャプチャ範囲を返す。
     * 
     * @param num 何番目のカードかを指定(左から1、最大で5)。
     * @return キャプチャ範囲。
     */
    public CaptureRectangle getCardCapture(int num) {
        return cardRectangleList.get(num - 1);
    }

    /**
     * カードのキャプチャファイルのパスを返す。
     * 
     * @param card カードの種類。
     * @return ファイルパス。
     */
    public String getCardCaptureFilePath(Card card) {
        return cardFilePathMap.get(card);
    }

    public Map<CaptureKind, CaptureRectangle> getRectangleMap() {
        return rectangleMap;
    }

    public void setRectangleMap(Map<CaptureKind, CaptureRectangle> rectangleMap) {
        this.rectangleMap = rectangleMap;
    }

    public Map<CaptureKind, String> getFilePathMap() {
        return filePathMap;
    }

    public void setFilePathMap(Map<CaptureKind, String> filePathMap) {
        this.filePathMap = filePathMap;
    }

    public List<CaptureRectangle> getCardRectangleList() {
        return cardRectangleList;
    }

    public void setCardRectangleList(List<CaptureRectangle> cardRectangleList) {
        this.cardRectangleList = cardRectangleList;
    }

    public Map<Card, String> getCardFilePathMap() {
        return cardFilePathMap;
    }

    public void setCardFilePathMap(Map<Card, String> cardFilePathMap) {
        this.cardFilePathMap = cardFilePathMap;
    }
}
