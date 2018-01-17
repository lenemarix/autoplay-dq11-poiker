package com.github.lenemarix.autoplay.dq11.poker.statemachine.event;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.github.lenemarix.autoplay.dq11.poker.util.ImageUtil;

/**
 * ゲーム画面キャプチャの履歴を保持し、アプリケーション終了時に自動的に保存する。
 */
@Component
@ConfigurationProperties("autoplay.dq11.poker.capture-history")
public class CaptureHistory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptureHistory.class);

    /** 履歴の最大保持数。 */
    private int max;

    /** 画面キャプチャの保存先ディレクトリ。 */
    private String saveDirectory;

    /** 以前に保存されている画面キャプチャファイルを削除するかどうか。 */
    private boolean autoOldFileDelete;

    @Autowired
    ImageUtil imageUtil;

    private LinkedList<CaptureHistoryElement> captureList = new LinkedList<>();

    public synchronized void add(BufferedImage image, String eventId, Events dispatchedEvent,
            LocalDateTime captureDateTime) {
        if (max <= 0) {
            // maxが0以下ならキャプチャを保存しない
            return;
        }
        if (captureList.size() > 1 && captureList.size() >= max) {
            captureList.removeFirst();
        }
        CaptureHistoryElement element = new CaptureHistoryElement(image, eventId, dispatchedEvent, captureDateTime);
        captureList.add(element);
    }

    /**
     * 直近のスクリーンキャプチャをファイル保存する。
     */
    @PreDestroy
    public synchronized void dump() {
        if (autoOldFileDelete) {
            try {
                deleteOldFile();
            } catch (IOException e) {
                LOGGER.warn("fail to delete old capture file.", e);
            }
        }

        captureList.stream().forEach(e -> {
            String filePath = createCaptureFilePath(e);
            try {
                imageUtil.imageToFile(e.getImage(), filePath);
                LOGGER.info("save capture file. date: {}, eventId: {}, event: {}, file: {}", e.getCaptureDateTime(),
                        e.getEventId(), e.getDispatchedEvent(), filePath);
            } catch (IOException e1) {
                LOGGER.error("fail to save capture file: {}", filePath, e1);
            }
        });
    }

    /**
     * すでに存在するキャプチャ履歴ファイルを削除する。 削除するのは数字が14桁 + ".png" のファイル。
     * 
     * @throws IOException
     *             ファイルの削除に失敗した場合。
     */
    private void deleteOldFile() throws IOException {
        Files.list(Paths.get(saveDirectory)).map(p -> p.toFile()).filter(f -> f.isFile())
                .filter(f -> f.getName().matches("[0-9]{14}\\.png")).forEach(f -> {
                    LOGGER.debug("delete old capture file: {}", f.getAbsolutePath());
                    f.delete();
                });
    }

    private String createCaptureFilePath(CaptureHistoryElement e) {
        String filePath = saveDirectory + File.separator
                + e.getCaptureDateTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".png";
        return filePath;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getSaveDirectory() {
        return saveDirectory;
    }

    public void setSaveDirectory(String saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    public boolean isAutoOldFileDelete() {
        return autoOldFileDelete;
    }

    public void setAutoOldFileDelete(boolean autoOldFileDelete) {
        this.autoOldFileDelete = autoOldFileDelete;
    }
}
