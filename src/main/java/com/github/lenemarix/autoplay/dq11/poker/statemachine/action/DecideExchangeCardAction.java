package com.github.lenemarix.autoplay.dq11.poker.statemachine.action;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;

import com.github.lenemarix.autoplay.dq11.poker.model.Card;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;
import com.github.lenemarix.autoplay.dq11.poker.util.CardReader;
import com.github.lenemarix.autoplay.dq11.poker.util.ImageComparator;
import com.github.lenemarix.autoplay.dq11.poker.util.RobotUtil;

/**
 * カードの交換を行うアクション。 交換するカードと残すカードを決定し、適切にキー操作を行ったあとに"くばる"ボタンを押下する。
 */
public class DecideExchangeCardAction extends AbstractAutoplayAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecideExchangeCardAction.class);

    @Autowired
    RobotUtil robotUtil;

    @Autowired
    CardReader cardReader;

    @Autowired
    ImageComparator imageComparator;

    @Override
    public void doExecute(StateContext<States, Events> context) {
        @SuppressWarnings("unchecked")
        List<Card> cards = context.getMessageHeaders().get("cards", List.class);

        // キー操作コマンドの作成
        String command = cards.stream().map(c -> {
            if (c.isHoldCard()) {
                // 残すカードだった場合、Enter(○ボタン)を押して"かえる"→"のこす"にして、右キー
                return "CR";
            } else {
                // 残すカードでない場合はそのまま右キーを押す。
                return "R";
            }
        }).collect(Collectors.joining());

        // コマンド末尾の右キーを削除して枝刈り
        int lastCircleIndex = command.lastIndexOf("C");
        if (lastCircleIndex == -1) {
            command = "";
        } else {
            command = command.substring(0, lastCircleIndex + 1);
        }

        // 下キーで"くばる"ボタンに移動し、Enterキー(○ボタン)で"くばる"ボタンを押下。
        command += "DC";

        robotUtil.keyPressByCommand(command);
    }
}
