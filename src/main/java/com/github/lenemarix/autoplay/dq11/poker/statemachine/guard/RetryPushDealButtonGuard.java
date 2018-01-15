package com.github.lenemarix.autoplay.dq11.poker.statemachine.guard;

import java.time.Duration;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.annotation.OnStateChanged;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.guard.Guard;

import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;

/**
 * カード配布済み状態で指定時間が経過した後に、再度"くばる"ボタンを押すかどうかのガード条件。
 * なんらかの原因(キー操作のとりこぼし等)で"くばる"ボタンが押せなかった場合の救済として、再度"くばる"ボタンを押すべきか判断する。
 */
@WithStateMachine
@ConfigurationProperties(prefix = "autoplay.dq11.poker.retry-push-deal-button-guard")
public class RetryPushDealButtonGuard implements Guard<States, Events> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryPushDealButtonGuard.class);

    private static final String DEALT_CARDS_STATE_START_DATE = "DEALT_CARDS_STATE.startDate";

    /** カード配布済み状態になってから"くばる"ボタンを再度押すまでに待つ時間(ms)。 */
    private int waitPeriod = 60 * 1000;

    @Override
    public boolean evaluate(StateContext<States, Events> context) {
        LocalDateTime dealCardsStateStartDate = context.getExtendedState().get(DEALT_CARDS_STATE_START_DATE,
                LocalDateTime.class);

        if (dealCardsStateStartDate == null) {
            throw new IllegalStateException("dealCardsStateStartDate is null");
        }
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dealCardsStateStartDate, now);

        boolean result = false;

        if (duration.toMillis() > waitPeriod) {
            context.getExtendedState().getVariables().put(DEALT_CARDS_STATE_START_DATE, now);
            result = true;
        }
        LOGGER.info("[Guard     ] RetryPushDealButtonGuard: {}, now wait(ms): {} (until {}ms)", result,
                duration.toMillis(), waitPeriod);
        return result;
    }

    /**
     * DEALT_CARDS_STATEに入った時間をExtendedStateとして記録する。
     * 他の状態に遷移した場合はExtendedStateから時間を削除する。
     * 
     * @param stateContext StateContext.
     */
    @OnStateChanged
    public void onStateChanged(StateContext<States, Events> stateContext) {
        LocalDateTime dealtCardsStateStartDate = null;
        if (States.DEALT_CARDS_STATE.equals(stateContext.getTarget().getId())) {
            dealtCardsStateStartDate = LocalDateTime.now();
            stateContext.getExtendedState().getVariables().put(DEALT_CARDS_STATE_START_DATE, dealtCardsStateStartDate);
        } else {
            stateContext.getExtendedState().getVariables().remove(DEALT_CARDS_STATE_START_DATE);
        }
        LOGGER.debug("save dealt cards state start Date: {}", dealtCardsStateStartDate);
    }

    public int getWaitPeriod() {
        return waitPeriod;
    }

    public void setWaitPeriod(int waitPeriod) {
        this.waitPeriod = waitPeriod;
    }
}
