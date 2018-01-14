package com.github.lenemarix.autoplay.dq11.poker.util;

import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.github.lenemarix.autoplay.dq11.poker.model.GameScreen;

/**
 * PS4リモートプレイのウィンドウを前面に出すためのユーティリティ。
 */
public interface ActivateWindowUtil {

    void activate();

    /**
     * PS4リモートプレイのウィンドウを前面に出す。 
     * プロパティで指定された座標にウィンドウが存在し、クリック可能な状態となっていること。
     */
    @ConfigurationProperties(prefix = "autoplay.dq11.poker.mouse-click-activate-window")
    public static class MouseClickActivateWindowUtil implements ActivateWindowUtil {
        private static final Logger LOGGER = LoggerFactory.getLogger(MouseClickActivateWindowUtil.class);

        /** PS4リモートプレイのウィンドウを前面に出すためにクリックする座標。 */
        private int mouseClickX;
        private int mouseClickY;

        @Autowired
        RobotUtil robotUtil;
 
        @Autowired
        GameScreen gameScreen;

        @Override
        public void activate() {
            LOGGER.info("activate ps4 remote play window");
            robotUtil.mouseMove(mouseClickX + gameScreen.getLocationX(),
                    mouseClickY + gameScreen.getLocationY());
            robotUtil.mousePress(InputEvent.BUTTON1_DOWN_MASK);

        }

        public int getMouseClickX() {
            return mouseClickX;
        }

        public void setMouseClickX(int mouseClickX) {
            this.mouseClickX = mouseClickX;
        }

        public int getMouseClickY() {
            return mouseClickY;
        }

        public void setMouseClickY(int mouseClickY) {
            this.mouseClickY = mouseClickY;
        }
    }

    /**
     * PS4リモートプレイのウィンドウをApple Scriptを使って前面に出す。
     */
    public static class AppleScriptActivateWindowUtil implements ActivateWindowUtil {

        private static final Logger LOGGER = LoggerFactory.getLogger(AppleScriptActivateWindowUtil.class);

        @Autowired
        RobotUtil robotUtil;

        @Override
        public void activate() {
            ProcessBuilder builder = new ProcessBuilder("osascript", "-e",
                    "tell application \"RemotePlay\" to activate");

            try (InputStreamReader isr = new InputStreamReader(builder.start().getErrorStream());
                    BufferedReader bufferedReader = new BufferedReader(isr)) {
                String scriptError;
                while ((scriptError = bufferedReader.readLine()) != null) {
                    LOGGER.error(scriptError);
                }
                robotUtil.delay();
            } catch (IOException e) {
                throw new IllegalStateException("fail to activate RemotePlay window", e);
            }
        }
    }

    /**
     * PS4リモートプレイのウィンドウを前面に出す際の動作モード。
     */
    public static enum ActivateWindowMode {
        /** 指定座標をクリックすることでウィンドウを前面に出すモード。 */
        MOUSE_CLICK,

        /** Apple Scriptを使って前面に出すモード。 */
        APPLE_SCRIPT,

        /** モードの自動設定を行うモード。 */
        AUTO
    }

}
