package com.github.lenemarix.autoplay.dq11.poker.statemachine.event;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.statemachine.StateMachine;

import com.github.lenemarix.autoplay.dq11.poker.model.BetCoinInputCapture;
import com.github.lenemarix.autoplay.dq11.poker.model.Card;
import com.github.lenemarix.autoplay.dq11.poker.model.DealCardsButtonCapture;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;
import com.github.lenemarix.autoplay.dq11.poker.util.CardReader;
import com.github.lenemarix.autoplay.dq11.poker.util.ImageComparator;
import com.github.lenemarix.autoplay.dq11.poker.util.RobotUtil;

/**
 * イベントを送信するクラス。
 * {@link @Scheduled}で定期的に画面キャプチャし、条件に応じたイベントを{@link StateMachine}に送信する。
 */
@ConfigurationProperties(prefix = "autoplay.dq11.poker.event")
public class EventDispatcherTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventDispatcherTask.class);

    @Autowired
    StateMachine<States, Events> stateMachine;

    @Autowired
    RobotUtil robotUtil;

    @Autowired
    ImageComparator imageComparator;

    @Autowired
    CardReader cardReader;

    @Autowired
    DealCardsButtonCapture dealCardsButtonCapture;

    @Autowired
    BetCoinInputCapture betCoinInputCapture;

    @Autowired
    CaptureHistory captureHistory;

    /**
     * イベント検知の周期(ms)。 application.propertiesでの補完の有効化、および、
     * デバッグログ出力のためのフィールドであり、実際にはこのフィールドは設定には使われない。
     * <code>@Scheduled</code>の<code>fixedDelayString</code>に指定された値が使われる。
     */
    private int timerInterval;

    /**
     * デバッグ用。指定回数目のイベント送信でロイヤルストレートスライム検出イベントを送信する。 0以下の数値が設定されている場合は無視される。
     */
    private int debugRssEventCount = 0;

    /** 初期化イベントの実施済みフラグ。 */
    private boolean doneInitialEvent = false;

    /** 画面キャプチャを連続何回取得するか。 */
    private int captureBurstCount;

    /** 画面キャプチャを連続取得する際の間隔(ms)。 */
    private int captureBurstInterval;

    /**
     * 定期的に画面をキャプチャし、条件に応じたイベントを{@link StateMachine}に送信する。
     */
    @Scheduled(fixedDelayString = "${autoplay.dq11.poker.event.timer-interval}")
    public void dispatchEvent() {
        LOGGER.debug("dispatchEvent start. timer-interval: {}ms", timerInterval);

        // StatemachineがINITIAL_STATEになるまで初期化イベントを投げない
        if (!doneInitialEvent && !stateMachine.getInitialState().equals(stateMachine.getState())) {
            LOGGER.debug("Statemachine is not ready");
            return;
        }

        /*
         * 役が成立した際にカードが光るアニメーションが入るため、1回のキャプチャだとカード認識に失敗する可能性がある。
         * そのため、キャプチャを連続取得し、それぞれのキャプチャでカードの読み取りを行い、補正を行う。
         */
        List<BufferedImage> captureList = robotUtil.captureGameScreenBurst(captureBurstCount, captureBurstInterval);
        LocalDateTime captureDateTime = LocalDateTime.now();

        // それぞれのキャプチャでカード読み取りを実施。
        List<List<Card>> readCardFromAllCapture = captureList.stream()
            .map(screen -> cardReader.readAllCards(screen))
            .collect(Collectors.toList());

        // それぞれのカード読み取り結果をマージ。
        List<Card> cards = readCardFromAllCapture.stream()
            .reduce((list1, list2) -> cardReader.mergeCardList(list1, list2))
            .get();

        // キャプチャ画像には最後のキャプチャを使う。
        BufferedImage screen = captureList.get(captureList.size() - 1);

        LOGGER.info("read cards: {}", cards.stream().map(c -> c.name()).collect(Collectors.joining(",")));

        Events event = null;
        if (shouldSendInitialEvent()) {
            event = Events.INITIAL_EVENT;
        } else if (shouldSendRoyalStraightSlimeEvent(cards)) {
            event = Events.ROYAL_STRAIGHT_SLIME_EVENT;
        } else if (shouldSendDealCardsEvent(cards, screen)) {
            event = Events.DEAL_CARDS_EVENT;
        } else if (shouldSendBeforeBetCoinInputEvent(screen)) {
            event = Events.BEFORE_BET_COIN_EVENT;
        } else {
            event = Events.OTHER_EVENT;
        }

        // イベントを一意に表すID。
        String eventId = UUID.randomUUID().toString();

        // イベント送信。
        Message<Events> message = MessageBuilder
                .withPayload(event)
                .setHeader("cards", cards)
                .setHeader("eventId", eventId)
                .build();
        stateMachine.sendEvent(message);

        // ゲーム画面のキャプチャ画像を履歴に保持。
        captureHistory.add(screen, eventId, event, captureDateTime);

        LOGGER.debug("dispatchEvent end. dispatched event: {}", event);
    }

    /**
     * 初期化イベントを送信すべきかを判定する。
     * 
     * @return イベントを送信するべきならtrue。それ以外はfalse。
     */
    private boolean shouldSendInitialEvent() {
        if (!doneInitialEvent) {
            // 初期化イベント
            doneInitialEvent = true;
            return true;
        }
        return false;
    }

    /**
     * カード配布済みイベントを送信するべきか判定する。
     * 
     * @param cards
     *            配布されたカードのカード種別のリスト。
     * @param screen
     *            ゲーム画面のキャプチャ。
     * @return イベントを送信するべきならtrue。それ以外はfalse。
     */
    private boolean shouldSendDealCardsEvent(List<Card> cards, BufferedImage screen) {
        // "くばる"ボタンが表示される箇所の画面を抽出。
        BufferedImage capture = screen.getSubimage(
                dealCardsButtonCapture.getX(),
                dealCardsButtonCapture.getY(),
                dealCardsButtonCapture.getWidth(),
                dealCardsButtonCapture.getHeight());
        try {
            // "くばる"ボタンの画像とキャプチャが一致したらイベント送信。
            if (imageComparator.compare(dealCardsButtonCapture.getFilepath(), capture)) {
                return true;
            }
        } catch (IOException e) {
            // ファイル読み込みに失敗したらログを出して継続。
            LOGGER.error("fail to read deal cards button image file: {}", dealCardsButtonCapture.getFilepath(), e);
        }
        return false;
    }

    /**
     * ロイヤルストレートスライムイベントを送信するべきか判定する。
     * 
     * @param cards
     *            配布されたカードのカード種別のリスト。
     * @return イベントを送信するべきならtrue。それ以外はfalse。
     */
    private boolean shouldSendRoyalStraightSlimeEvent(List<Card> cards) {
        // デバッグ用のイベントカウントが設定されていれば、カウント0でイベントを送信する。
        if (debugRssEventCount == 1) {
            return true;
        } else if (debugRssEventCount > 1) {
            debugRssEventCount--;
        }

        if (cards.stream().filter(c -> c.isHoldCard()).count() == 5) {
            return true;
        }
        return false;
    }

    /**
     * かけ金入力待ちイベントを送信するべきか判定する。
     * 
     * @param screen
     *            ゲーム画面のキャプチャ。
     * @return イベントを送信するべきならtrue。それ以外はfalse。
     */
    private boolean shouldSendBeforeBetCoinInputEvent(BufferedImage screen) {
        // かけ金入力欄が表示される箇所の画面を抽出。
        BufferedImage capture = screen.getSubimage(
                betCoinInputCapture.getX(),
                betCoinInputCapture.getY(),
                betCoinInputCapture.getWidth(),
                betCoinInputCapture.getHeight());
        try {
            // かけ金入力欄の画像とキャプチャが一致したらイベント送信。
            if (imageComparator.compare(betCoinInputCapture.getFilepath(), capture)) {
                return true;
            }
        } catch (IOException e) {
            // ファイル読み込みに失敗したらログを出して継続。
            LOGGER.error("fail to read bet coin input image file: {}", betCoinInputCapture.getFilepath(), e);
        }
        return false;
    }

    public int getTimerInterval() {
        return timerInterval;
    }

    public void setTimerInterval(int timerInterval) {
        this.timerInterval = timerInterval;
    }

    public int getDebugRssEventCount() {
        return debugRssEventCount;
    }

    public void setDebugRssEventCount(int debugRssEventCount) {
        this.debugRssEventCount = debugRssEventCount;
    }

    public int getCaptureBurstCount() {
        return captureBurstCount;
    }

    public void setCaptureBurstCount(int captureBurstCount) {
        this.captureBurstCount = captureBurstCount;
    }

    public int getCaptureBurstInterval() {
        return captureBurstInterval;
    }

    public void setCaptureBurstInterval(int captureBurstInterval) {
        this.captureBurstInterval = captureBurstInterval;
    }

}
