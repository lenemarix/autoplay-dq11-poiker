package com.github.lenemarix.autoplay.dq11.poker.model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.github.lenemarix.autoplay.dq11.poker.util.RobotUtil;

/**
 * 配布されたカードのキャプチャに関するクラス。
 */
@Component
@ConfigurationProperties(prefix = "autoplay.dq11.poker.dealt-cards-capture")
public class DealtCardsCapture {

    @Autowired
    RobotUtil robotUtil;

    @Autowired
    GameScreen gameScreen;

    /** スライムの10のカードのキャプチャ保存先ファイルパス。 **/
    private String cardS10Filepath;
    /** スライムのJackのカードのキャプチャ保存先ファイルパス。 **/
    private String cardSJFilepath;
    /** スライムのQueenのカードのキャプチャ保存先ファイルパス。 **/
    private String cardSQFilepath;
    /** スライムのKingのカードのキャプチャ保存先ファイルパス。 **/
    private String cardSKFilepath;
    /** スライムのAのカードのキャプチャ保存先ファイルパス。 **/
    private String cardSAFilepath;
    /** 配布済みカード5枚それぞれのキャプチャ範囲のx座標を格納したリスト(左が1枚目)。 */
    private List<Integer> x = new ArrayList<>();
    /** 配布済みカードのキャプチャ範囲のY座標(各カード共通)。 */
    private int y;
    /** 配布済みカードのキャプチャ範囲の幅(各カード共通)。 */
    private int width;
    /** 配布済みカードのキャプチャ範囲の高さ(各カード共通)。 */
    private int height;

    /** カードとそのキャプチャ画像のファイルパスを対応付けたMap。 */
    private Map<Card, String> cardCaptureFileMap;

    @PostConstruct
    public void init() {
        Map<Card, String> map = new TreeMap<>();
        map.put(Card.S10, getCardS10Filepath());
        map.put(Card.SJ, getCardSJFilepath());
        map.put(Card.SQ, getCardSQFilepath());
        map.put(Card.SK, getCardSKFilepath());
        map.put(Card.SA, getCardSAFilepath());
        cardCaptureFileMap = Collections.unmodifiableMap(map);
    }

    public String getCardS10Filepath() {
        return cardS10Filepath;
    }

    public void setCardS10Filepath(String cardS10Filepath) {
        this.cardS10Filepath = cardS10Filepath;
    }

    public String getCardSJFilepath() {
        return cardSJFilepath;
    }

    public void setCardSJFilepath(String cardSJFilepath) {
        this.cardSJFilepath = cardSJFilepath;
    }

    public String getCardSQFilepath() {
        return cardSQFilepath;
    }

    public void setCardSQFilepath(String cardSQFilepath) {
        this.cardSQFilepath = cardSQFilepath;
    }

    public String getCardSKFilepath() {
        return cardSKFilepath;
    }

    public void setCardSKFilepath(String cardSKFilepath) {
        this.cardSKFilepath = cardSKFilepath;
    }

    public String getCardSAFilepath() {
        return cardSAFilepath;
    }

    public void setCardSAFilepath(String cardSAFilepath) {
        this.cardSAFilepath = cardSAFilepath;
    }

    public List<Integer> getX() {
        return x;
    }

    public void setX(List<Integer> x) {
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

    public Map<Card, String> getCardCaptureFileMap() {
        return cardCaptureFileMap;
    }

    /**
     * 配布済みカードの内、指定されたカードのキャプチャ範囲の長方形を生成して返す。
     * 
     * @param cardNumber
     *            1から5のカード番号 (左から1枚目)。
     * @return カードのキャプチャ範囲を表す長方形。
     */
    public Rectangle getRectangle(int cardNumber) {
        if (cardNumber < 1 || 5 < cardNumber) {
            throw new IllegalArgumentException("Invalid cardNumber: " + cardNumber);
        }

        int targetCardX = getX().get(cardNumber - 1);
        return new Rectangle(targetCardX, getY(), width, height);
    }

    /**
     * カード種別に対応する配布済みカードのキャプチャ画像のファイルパスを返す。
     * 
     * @param card カード種別。
     * @return カードのキャプチャ画像のファイルパス。
     */
    public String getFilepath(Card card) {
        return cardCaptureFileMap.get(card);
    }
}
