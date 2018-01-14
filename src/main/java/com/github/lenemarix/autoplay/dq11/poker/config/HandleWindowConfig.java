package com.github.lenemarix.autoplay.dq11.poker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.lenemarix.autoplay.dq11.poker.util.ActivateWindowUtil;
import com.github.lenemarix.autoplay.dq11.poker.util.ActivateWindowUtil.ActivateWindowMode;
import com.github.lenemarix.autoplay.dq11.poker.util.ActivateWindowUtil.AppleScriptActivateWindowUtil;
import com.github.lenemarix.autoplay.dq11.poker.util.ActivateWindowUtil.MouseClickActivateWindowUtil;

/**
 * PS4リモートプレイのウィンドウの扱いに関する設定。
 */
@Configuration
@ConfigurationProperties(prefix = "autoplay.dq11.poker.window")
public class HandleWindowConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleWindowConfig.class);

    /** PS4リモートプレイのウィンドウを前面に出す動作モード。 */
    private ActivateWindowMode activateWindowMode;

    /**
     * PS4リモートプレイのウィンドウを前面に出すためのBeanを指定されたモードに応じて返す。
     * モードがAUTOの場合は以下のようにBeanを決定する。
     * <ul>
     * <li>OSがMacOSの場合、{@link AppleScriptActivateWindowUtil}</li>
     * <li>それ以外の場合、{@link MouseClickActivateWindowUtil}</li>
     * </ul>
     * 
     * @return PS4リモートプレイのウィンドウを前面に出すアクション。
     */
    @Bean
    public ActivateWindowUtil activateWindowUtil() {

        ActivateWindowUtil util;

        LOGGER.info("activate window mode: " + activateWindowMode);
        switch (activateWindowMode) {
        case MOUSE_CLICK:
            util = new MouseClickActivateWindowUtil();
            break;
        case APPLE_SCRIPT:
            util = new AppleScriptActivateWindowUtil();
            break;
        case AUTO:
            String osname = System.getProperty("os.name").toLowerCase();
            if (osname.startsWith("mac")) {
                LOGGER.info("activate window mode (auto): " + ActivateWindowMode.APPLE_SCRIPT);
                util = new AppleScriptActivateWindowUtil();
            } else {
                LOGGER.info("activate window mode (auto): " + ActivateWindowMode.MOUSE_CLICK);
                util = new MouseClickActivateWindowUtil();
            }
            break;
        default:
            throw new IllegalStateException("invalid activate window mode: " + activateWindowMode);
        }

        return util;
    }

    public ActivateWindowMode getActivateWindowMode() {
        return activateWindowMode;
    }

    public void setActivateWindowMode(ActivateWindowMode activateWindowMode) {
        this.activateWindowMode = activateWindowMode;
    }

}
