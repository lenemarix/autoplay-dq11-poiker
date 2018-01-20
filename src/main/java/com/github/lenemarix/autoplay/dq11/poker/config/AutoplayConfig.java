package com.github.lenemarix.autoplay.dq11.poker.config;

import java.awt.MouseInfo;
import java.awt.Point;
import java.time.Duration;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
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
public class AutoplayConfig implements SchedulingConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoplayConfig.class);

    private int applicationExitCheckInterval;

    private LocalDateTime startTime = LocalDateTime.now();

    @Autowired
    ConfigurableApplicationContext context;

    @Autowired
    StateMachine<States, Events> stateMachine;

    @Autowired
    CaptureHistory captureHistory;

    @Bean
    public TaskScheduler taskSchedulerForAutoplay() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix("autoplay-pool-");
        taskScheduler.setPoolSize(2);
        return taskScheduler;
    }

    @Bean
    public EventDispatcherTask eventDispatcherTask() {
        return new EventDispatcherTask();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskSchedulerForAutoplay());
    }

    /**
     * 定期的にアプリケーションの実行終了条件を判定し、条件を満たしたらアプリケーションを終了する。 以下の場合に終了する。
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
        if (stateMachine.getState() != null && stateMachine.isComplete()) {
            LOGGER.info("statemachine is complete. start shutdown.");
            context.close();
            loggingDurationTime();
        }
    }

    private void loggingDurationTime() {
        Duration duration = Duration.between(startTime, LocalDateTime.now());
        long hours = duration.toHours();
        long minutes = duration.toMinutes() - (hours * 60);
        long seconds = duration.getSeconds() - (hours * 60 * 60) - (minutes * 60);

        LOGGER.info("Playing poker duration: {}h:{}m:{}s ({})", hours, minutes, seconds,
                duration.toString());
    }

    public int getApplicationExitCheckInterval() {
        return applicationExitCheckInterval;
    }

    public void setApplicationExitCheckInterval(int applicationExitCheckInterval) {
        this.applicationExitCheckInterval = applicationExitCheckInterval;
    }

}
