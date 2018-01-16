package com.github.lenemarix.autoplay.dq11.poker.statemachine;

import static com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events.OTHER_EVENT;
import static com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events.ROYAL_STRAIGHT_SLIME_EVENT;
import static com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States.DEALT_CARDS_STATE;
import static com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States.FINAL_STATE;
import static com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States.OTHER_STATE;
import static com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States.PLAYING_POKER_STATE;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.lenemarix.autoplay.dq11.poker.model.Card;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;
import com.github.lenemarix.autoplay.dq11.poker.util.ActivateWindowUtil;


@RunWith(SpringRunner.class)
@SpringBootTest(properties = "mode=TEST")
public class AutoplayStatemachineTest {

    @Autowired
    StateMachine<States, Events> stateMachine;

    @MockBean
    Robot robot;

    @MockBean
    ActivateWindowUtil activateWindowUtil;

    @Before
    public void setUp() {
        when(robot.createScreenCapture(anyObject()))
            .thenReturn(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB));
        doNothing().when(activateWindowUtil).activate();
    }

    @Test
    public void testStateMachine01() throws Exception {
        List<Card> cards = Arrays.asList(Card.S10, Card.SJ, Card.SQ, Card.XX, Card.XX);
        Message<Events> dealCardsEventMessage = MessageBuilder
                .withPayload(Events.DEAL_CARDS_EVENT)
                .setHeader("cards", cards)
                .build();

        StateMachineTestPlanBuilder.<States, Events>builder()
            .stateMachine(stateMachine)
            .step()
                .expectStates(PLAYING_POKER_STATE, OTHER_STATE)
                .and()
            .step()
                .sendEvent(dealCardsEventMessage)
                .expectStateChanged(1)
                .expectStates(PLAYING_POKER_STATE, DEALT_CARDS_STATE)
                .and()
            .step()
                .sendEvent(OTHER_EVENT)
                .expectStateChanged(1)
                .expectStates(PLAYING_POKER_STATE, OTHER_STATE)
                .and()
            .step()
                .sendEvent(ROYAL_STRAIGHT_SLIME_EVENT)
                .expectStateChanged(1)
                .expectState(FINAL_STATE)
                .expectStateMachineStopped(2)
                .and()
            .build()
            .test();
    }

}
