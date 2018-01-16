package com.github.lenemarix.autoplay.dq11.poker.statemachine.event;

/**
 * イベント。
 */
public enum Events {

    /** カード配布検知イベント。 */
    DEAL_CARDS_EVENT,

    /** ロイヤルストレートスライム検知イベント。 */
    ROYAL_STRAIGHT_SLIME_EVENT,

    /** かけ金入力待ち検知イベント。 */
    BEFORE_BET_COIN_EVENT,

    /** その他イベント。 */
    OTHER_EVENT,

}
