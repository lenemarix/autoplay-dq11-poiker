package com.github.lenemarix.autoplay.dq11.poker.statemachine.guard;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;

/**
 * ポーカーをリトライするか判定するガード条件。 予期せぬキー動作により、ポーカーを続けるかどうかのダイアログでいいえが選択されてしまい、
 * ポーカーが終了してしまった場合に、ポーカー自体をリトライするかどうか判定する。
 */
@ConfigurationProperties(prefix = "autoplay.dq11.poker.retry-on-unexpected-guard")
public class RetryOnUnexpectedStateGuard implements Guard<States, Events> {

    private boolean retry;

    @Override
    public boolean evaluate(StateContext<States, Events> context) {
        return retry;
    }

    public boolean isRetry() {
        return retry;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }

}
