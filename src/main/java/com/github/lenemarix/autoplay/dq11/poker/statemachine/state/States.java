package com.github.lenemarix.autoplay.dq11.poker.statemachine.state;

/**
 * 状態。
 */
public enum States {

    /** カード配布済み状態("くばる"ボタンが表示されている)。 */
    DEALT_CARDS_STATE,

    /** 予期せぬ状態になったとき、リトライかアプリケーション終了を選択する状態。 */
    RETRY_OR_END_STATE,

    /** その他の状態。 */
    OTHER_STATE,

    /** 終了状態。 */
    FINAL_STATE

}
