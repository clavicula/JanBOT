/**
 * AnnounceType.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;



/**
 * 実況タイプ
 */
enum AnnounceType {
    
    /**
     * 手牌 (ツモ牌込み)
     */
    HAND_TSUMO,
    
    /**
     * 手牌と場情報 (ツモ牌込み)
     */
    HAND_TSUMO_FIELD;
    
    
    
    /**
     * 場情報を実況するか
     * 
     * @return 判定結果。
     */
    public boolean isAnnounceField() {
        switch (this) {
        case HAND_TSUMO_FIELD:
            return true;
        default:
            return false;
        }
    }
    
    /**
     * 手牌を実況するか
     * 
     * @return 判定結果。
     */
    public boolean isAnnounceHand() {
        switch (this) {
        case HAND_TSUMO:
        case HAND_TSUMO_FIELD:
            return true;
        default:
            return false;
        }
    }
    
    /**
     * ツモ牌を実況するか
     * 
     * @return 判定結果。
     */
    public boolean isAnnounceTsumo() {
        switch (this) {
        case HAND_TSUMO:
        case HAND_TSUMO_FIELD:
            return true;
        default:
            return false;
        }
    }
    
}

