package com.github.lenemarix.autoplay.dq11.poker.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.lenemarix.autoplay.dq11.poker.model.Card;
import com.github.lenemarix.autoplay.dq11.poker.model.DealtCardsCapture;

/**
 * カードの読み込みを行うクラス。
 */
@Component
public class CardReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardReader.class);

    @Autowired
    RobotUtil robotUtil;

    @Autowired
    ImageComparator imageComparator;

    @Autowired
    DealtCardsCapture dealtCardsCapture;

    /**
     * 指定されたカードが保持すべきカードかを判定する。
     * 
     * @param cardNumber
     *            カード番号(左側から1)。
     * @param screen
     *            ゲーム画像のスクリーンショット画像。
     * @return 保持すべきカードならtrue、それ以外はfalse。
     */
    public boolean isHoldCard(int cardNumber, BufferedImage screen) {
        return !Card.XX.equals(readCard(cardNumber, screen));
    }

    /**
     * カード5枚の種類を読み取る。
     * 
     * @param screen
     *            ゲーム画面のスクリーンショット画像。
     * @return カード5枚の種類のリスト。
     */
    public List<Card> readAllCards(BufferedImage screen) {
        return IntStream.rangeClosed(1, 5).mapToObj(i -> readCard(i, screen)).collect(Collectors.toList());
    }

    /**
     * 指定されたカードの種類を読み取る。
     * 
     * @param cardNumber
     *            カード番号(左側から1)。
     * @parma screen ゲーム画面のスクリーンショット画像。
     * @return カードの種類。
     */
    public Card readCard(int cardNumber, BufferedImage screen) {
        // 指定のカード番号の画像イメージを抽出。
        BufferedImage capturedCardImage = screen.getSubimage(dealtCardsCapture.getX().get(cardNumber - 1),
                dealtCardsCapture.getY(), dealtCardsCapture.getWidth(), dealtCardsCapture.getHeight());
        List<Card> recognizedCards = dealtCardsCapture.getCardCaptureFileMap()
                .entrySet()
                .stream()
                .filter(e -> compareCard(capturedCardImage, e.getValue()))
                .map(e -> e.getKey())
                .collect(Collectors.toList());

        if (recognizedCards.size() == 0) {
            return Card.XX;
        } else if (recognizedCards.size() == 1) {
            return recognizedCards.get(0);
        } else {
            String recognizedCardString = recognizedCards.stream().map(c -> c.name()).collect(Collectors.joining(", "));
            throw new IllegalStateException(
                    "read card error!! cardNumber: " + cardNumber + ", recognized card: " + recognizedCardString);
        }
    }

    /**
     * 指定されたカードの画像キャプチャが、保存されているカードのキャプチャと一致するか判定する。
     * 
     * @param capturedCardImage
     *            キャプチャしたカードの画像イメージ。
     * @param cardFile
     *            比較対象となるカードの画像のファイルパス。
     * @return 一致していればtrue、それ以外はfalse。
     */
    private boolean compareCard(BufferedImage capturedCardImage, String cardFile) {
        boolean result = false;
        try {
            result = imageComparator.compare(cardFile, capturedCardImage);
        } catch (IOException e) {
            // ファイルが読み込めなかった場合はログ出力して継続する
            LOGGER.error("fail to read card file: " + cardFile);
        }
        return result;
    }

    /**
     * 2つのカード読み取り結果のリストをマージする。
     * カード読み取り結果に違いがある場合は、以下の優先順位でマージする。
     * <ol>
     * <li>ロイヤルストレートスライムを構成するカードを優先する。</li>
     * <li>list2の要素を優先する。</li>
     * </ol>
     * 
     * @param list1 カード読み取り結果リスト1。
     * @param list2 カード読み取り結果リスト2。
     * @return マージしたカード読み取り結果のリスト。
     */
    public List<Card> mergeCardList(List<Card> list1, List<Card> list2) {
        if (list1.size() != list2.size()) {
            throw new IllegalArgumentException("the size of each list is different: list1: " + list1.size() + ", list2: "
                    + list2.size());
        }
        return IntStream.range(0, 5)
                .mapToObj(i -> mergeCard(list1.get(i), list2.get(i)))
                .collect(Collectors.toList());
    }

    /**
     * 2枚のカード読み取り結果をマージする。
     * カード読み取り結果に違いがある場合は、以下の優先順位でマージする。
     * <ol>
     * <li>ロイヤルストレートスライムを構成するカードを優先する。</li>
     * <li>card2の要素を優先する。</li>
     * </ol>
     * 
     * @param card1 カード読み取り結果1。
     * @param card2 カード読み取り結果2。
     * @return マージしたカード読み取り結果。
     */
    private Card mergeCard(Card card1, Card card2) {
        if (card1.equals(card2)) {
            return card2;
        }
        Card result = card2.isHoldCard() ? card2 : card1;
        LOGGER.info("detect read card difference, card1: {}, card2: {}, result: {}", card1, card2, result);
        return result;
    }
}
