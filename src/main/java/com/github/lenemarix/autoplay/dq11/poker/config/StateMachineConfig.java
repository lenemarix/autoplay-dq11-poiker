package com.github.lenemarix.autoplay.dq11.poker.config;

import static com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events.*;
import static com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import com.github.lenemarix.autoplay.dq11.poker.statemachine.action.ActivateWindowAction;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.action.BetCoinAction;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.action.DecideExchangeCardAction;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.action.DecideTryDoubleupChanceAction;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.action.EnterKeyPushAction;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.action.PushDealButtonAction;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.action.PushShareButtonAction;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.event.Events;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.guard.RetryOnUnexpectedStateGuard;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.guard.RetryPushDealButtonGuard;
import com.github.lenemarix.autoplay.dq11.poker.statemachine.state.States;

/**
 * {@link StateMachine}の設定。
 */
@Configuration
@EnableStateMachine
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {

    @Bean
    public ActivateWindowAction activateWindowAction() {
        return new ActivateWindowAction();
    }

    @Bean
    public DecideExchangeCardAction decideExchangeCardAction() {
        return new DecideExchangeCardAction();
    }

    @Bean
    public EnterKeyPushAction enterKeyPushAction() {
        return new EnterKeyPushAction();
    }

    @Bean
    public PushDealButtonAction pushDealButtonAction() {
        return new PushDealButtonAction();
    }

    @Bean
    public BetCoinAction betCoinAction() {
        return new BetCoinAction();
    }

    @Bean
    public PushShareButtonAction pushShareButtonAction() {
        return new PushShareButtonAction();
    }

    @Bean
    public DecideTryDoubleupChanceAction decideTryDoubleupChanceAction() {
        return new DecideTryDoubleupChanceAction();
    }

    @Bean
    public RetryPushDealButtonGuard retryPushDealButtonGuard() {
        return new RetryPushDealButtonGuard();
    }

    @Bean
    public RetryOnUnexpectedStateGuard retryOnUnexpectedStateGuard() {
        return new RetryOnUnexpectedStateGuard();
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config.withConfiguration()
                .autoStartup(false);
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        // 各状態とEntry Actionの定義。
        states.withStates()
                .initial(PLAYING_POKER_STATE, activateWindowAction())
                .choice(RETRY_OR_END_STATE)
                .end(FINAL_STATE)
                .and()
                .withStates()
                    .parent(PLAYING_POKER_STATE)
                    .initial(OTHER_STATE)
                    .state(OTHER_STATE, enterKeyPushAction(), null)
                    .state(DEALT_CARDS_STATE, decideExchangeCardAction(), null);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions
                // ポーカープレイ中状態でロイヤルストレートスライムを検出。終了状態へ遷移。
                .withExternal()
                    .source(PLAYING_POKER_STATE)
                    .target(FINAL_STATE)
                    .event(ROYAL_STRAIGHT_SLIME_EVENT)
                    .action(pushShareButtonAction())
                    .and()
                // かけ金入力欄が0の状態で見つかった場合、何らかの原因でポーカーを終了してしまったとみなし、リトライ・終了選択状態へ遷移。
                .withExternal()
                    .source(PLAYING_POKER_STATE)
                    .target(RETRY_OR_END_STATE)
                    .event(BEFORE_BET_COIN_EVENT)
                    .and()
                // "くばる"ボタンを見つけたら、その他状態からカード配布済み状態へ遷移。
                .withExternal()
                    .source(OTHER_STATE)
                    .target(DEALT_CARDS_STATE)
                    .event(DEAL_CARDS_EVENT)
                    .and()
                // その他状態の継続。Entry ActionでEnterキーを押して先に進める。
                .withExternal()
                    .source(OTHER_STATE)
                    .target(OTHER_STATE)
                    .event(OTHER_EVENT)
                    .and()
                // カード配布済み状態でロイヤルストレートスライムを検出。終了状態へ遷移。
                .withExternal()
                    .source(DEALT_CARDS_STATE)
                    .target(OTHER_STATE)
                    .event(OTHER_EVENT)
                    .and()
                // 予期せぬ状態になったときにリトライかアプリケーション終了を判定する。
                .withChoice()
                    .source(RETRY_OR_END_STATE)
                    .first(PLAYING_POKER_STATE, retryOnUnexpectedStateGuard(), betCoinAction())
                    .last(FINAL_STATE)
                    .and()
                // カード配布済み状態が長く続く場合、"くばる"ボタンの押下に失敗したとみなし、再度ボタンの押下を試みる。
                .withInternal()
                    .source(DEALT_CARDS_STATE)
                    .event(DEAL_CARDS_EVENT)
                    .guard(retryPushDealButtonGuard())
                    .action(pushDealButtonAction())
                    .and()
                // ダブルアップチャンスが発生した際に自己遷移し、設定に従ってダブルアップチャンスを継続するか決定する。
                .withExternal()
                    .source(PLAYING_POKER_STATE)
                    .target(PLAYING_POKER_STATE)
                    .event(DOUBLEUP_CHANCE_EVENT)
                    .action(decideTryDoubleupChanceAction())
                    .and();
    }

}