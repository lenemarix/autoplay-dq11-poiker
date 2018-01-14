package com.github.lenemarix.autoplay.dq11.poker.config;

import java.awt.AWTException;
import java.awt.Robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link Robot}の設定。
 */
@Configuration
@ConfigurationProperties(prefix = "autoplay.dq11.poker.robot")
public class RobotConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RobotConfig.class);

    /** {@link Robot}に設定する自動遅延の値。 */
    private int autodelay;

    @Bean
    public Robot robot() throws AWTException {
        LOGGER.info("Robot auto delay: " + autodelay + "ms");

        System.setProperty("java.awt.headless", "false");

        Robot robot = new Robot();
        robot.setAutoDelay(autodelay);

        return robot;
    }

    public int getAutodelay() {
        return autodelay;
    }

    public void setAutodelay(int autodelay) {
        this.autodelay = autodelay;
    }
}
