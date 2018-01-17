package com.github.lenemarix.autoplay.dq11.poker.statemachine.event;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

/**
 * ゲーム画面キャプチャの履歴の1要素を表すクラス。
 */
public class CaptureHistoryElement {

    public CaptureHistoryElement(BufferedImage image, String eventId, Events dispatchedEvent,
            LocalDateTime captureDateTime) {
        this.image = image;
        this.eventId = eventId;
        this.dispatchedEvent = dispatchedEvent;
        this.captureDateTime = captureDateTime;
    }

    private BufferedImage image;
    private String eventId;
    private Events dispatchedEvent;
    private LocalDateTime captureDateTime;

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Events getDispatchedEvent() {
        return dispatchedEvent;
    }

    public void setDispatchedEvent(Events dispatchedEvent) {
        this.dispatchedEvent = dispatchedEvent;
    }

    public LocalDateTime getCaptureDateTime() {
        return captureDateTime;
    }

    public void setCaptureDateTime(LocalDateTime captureDateTime) {
        this.captureDateTime = captureDateTime;
    }
}
