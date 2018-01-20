package com.github.lenemarix.autoplay.dq11.poker.model;

/**
 * カードを表すenum。
 */
public enum Card {
    /** スライムの10。 */
    S10,
    /** スライムのJack。 */
    SJ,
    /** スライムのQueen。 */
    SQ,
    /** スライムのKing。 */
    SK,
    /** スライムのA。 */
    SA,
    /** ジョーカー。 */
    JO,
    /** その他のカード。 */
    XX;

    /**
     * ロイヤルストレートスライムで残すべきカードか否かを返す。
     * 
     * @return 残すべきカードならtrue。それ以外はfalse。
     */
    public boolean isHoldCard() {
        return !XX.equals(this);
    }
}
