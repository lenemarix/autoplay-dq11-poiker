package com.github.lenemarix.autoplay.dq11.poker.config;

import java.awt.MouseInfo;
import java.awt.Point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.statemachine.StateMachine;

import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.CaptureHistory;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.EventDispatcherTask;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;

/**
 * プロパティ"mode"が"AUTOPLAY"の時のみ有効となる設定。
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "mode", havingValue = "AUTOPLAY")
@ConfigurationProperties(prefix = "autoplay.dq11.poker.autoplay-config")
public class AutoplayConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoplayConfig.class);

    private int applicationExitCheckInterval;

    @Autowired
    ConfigurableApplicationContext context;

    @Autowired
    StateMachine<States, Events> stateMachine;

    @Autowired
    CaptureHistory captureHistory;

    @Bean
    public EventDispatcherTask eventDispatcherTask() {
        return new EventDispatcherTask();
    }

    /**
     * 定期的にアプリケーションの実行終了条件を判定し、条件を満たしたらアプリケーションを終了する。
     * 以下の場合に終了する。
     * <ul>
     * <li>StateMachineが終了している場合。</li>
     * <li>マウスポインタが(0,0)の位置にある場合(ユーザによる強制終了)。</li>
     * </ul>
     */
    @Scheduled(fixedDelayString = "${autoplay.dq11.poker.autoplay-config.application-exit-check-interval}")
    public void applicationExitIfNecessary() {
        // マウスポインタが(0,0)の位置にある場合はStatemachineを強制終了。
        Point mousePoint = MouseInfo.getPointerInfo().getLocation();
        if (mousePoint.x == 0 && mousePoint.y == 0) {
            LOGGER.info("Mouse point is (0,0). Stop StateMachine.");
            stateMachine.stop();
        }

        // Statemachineが終了していたらコンテキストをクローズ。
        if (stateMachine.getState() != null
                && stateMachine.isComplete()) {
            LOGGER.info("statemachine is complete. start shutdown.");
            context.close();
        }
    }

    public int getApplicationExitCheckInterval() {
        return applicationExitCheckInterval;
    }

    public void setApplicationExitCheckInterval(int applicationExitCheckInterval) {
        this.applicationExitCheckInterval = applicationExitCheckInterval;
    }

}
