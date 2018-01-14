package com.github.lenemarix.autoplay.dq11.poker.model;

/**
 * アプリケーション動作モードを表すenum。
 */
public enum Mode {

    /** ポーカーの自動実行を行うモード。 */
    AUTOPLAY,

    /** 配布済みカードの画像キャプチャを行うモード(準備用)。 */
    CAPTURE_CARD,

    /** "くばる"ボタンの画像キャプチャを行うモード(準備用)。 */
    CAPTURE_DEAL_CARDS_BUTTON,

    /** "かけ金入力欄の画像キャプチャを行うモード(準備用)。 */
    CAPTURE_BET_COIN_INPUT,

    /** テストモード(単体試験時などに使用)。 */
    TEST
}
