package com.github.lenemarix.autoplay.dq11.poker.statemachine.action;

import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.statemachine.StateContext;

import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;
import com.github.lenemarix.autoplay.dq11.poker.util.RobotUtil;

/**
 * かけ金の選択をおこなうアクション。
 */
@ConfigurationProperties(prefix = "autoplay.dq11.poker.bet-coin")
public class BetCoinAction extends AbstractAutoplayAction {

    /** かけ金の設定のために上キーを押す回数(最大10回)。 */
    private int numberOfTimesToPushUpArrow;

    @Autowired
    RobotUtil robotUtil;

    @Override
    public void doExecute(StateContext<States, Events> context) {
        IntStream.range(0, numberOfTimesToPushUpArrow).forEach(i -> robotUtil.upKeyPress());
        robotUtil.enterKeyPress();
    }

    public int getNumberOfTimesToPushUpArrow() {
        return numberOfTimesToPushUpArrow;
    }

    public void setNumberOfTimesToPushUpArrow(int numberOfTimesToPushUpArrow) {
        this.numberOfTimesToPushUpArrow = numberOfTimesToPushUpArrow;
    }

}
