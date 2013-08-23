/**
 * AnnounceType.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;



/**
 * 実況タイプ
 */
public enum GameAnnounceType {
    
    /**
     * 手牌 (ツモ牌抜き)
     */
    HAND,
    
    /**
     * 手牌 (ツモ牌込み)
     */
    HAND_TSUMO,
    
    /**
     * 手牌と場情報 (ツモ牌込み)
     */
    HAND_TSUMO_FIELD,
    
    /**
     * 手牌と場情報と捨て牌情報 (ツモ牌抜き)
     */
    HAND_FIELD_RIVER,
    
    /**
     * 手牌と場情報と捨て牌情報と裏ドラ (ツモ牌込み)
     */
    HAND_TSUMO_FIELD_RIVER_URADORA,
    
    /**
     * 場情報
     */
    FIELD,
    
    /**
     * 捨て牌情報
     */
    RIVER,
    
    /**
     * 全捨て牌情報
     */
    RIVER_ALL,
    
    /**
     * 場情報と捨て牌情報
     */
    FIELD_RIVER,
    
    /**
     * 場情報と全捨て牌情報
     */
    FIELD_RIVER_ALL;
    
    
    
    /**
     * 場情報を実況するか
     * 
     * @return 判定結果。
     */
    public boolean isAnnounceField() {
        switch (this) {
        case HAND_TSUMO_FIELD:
        case HAND_FIELD_RIVER:
        case HAND_TSUMO_FIELD_RIVER_URADORA:
        case FIELD:
        case FIELD_RIVER:
        case FIELD_RIVER_ALL:
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
        case HAND:
        case HAND_TSUMO:
        case HAND_TSUMO_FIELD:
        case HAND_FIELD_RIVER:
        case HAND_TSUMO_FIELD_RIVER_URADORA:
            return true;
        default:
            return false;
        }
    }
    
    /**
     * 捨て牌を実況するか
     * 
     * @return 判定結果。
     */
    public boolean isAnnounceRiverSingle() {
        switch (this) {
        case HAND_FIELD_RIVER:
        case HAND_TSUMO_FIELD_RIVER_URADORA:
        case RIVER:
        case FIELD_RIVER:
            return true;
        default:
            return false;
        }
    }
    
    /**
     * 全捨て牌を実況するか
     * 
     * @return 判定結果。
     */
    public boolean isAnnounceRiverAll() {
        switch (this) {
        case RIVER_ALL:
        case FIELD_RIVER_ALL:
            return true;
        default:
            return false;
        }
    }
    
    /**
     * 裏ドラを実況するか
     * 
     * @return 判定結果。
     */
    public boolean isAnnounceUraDora() {
        switch (this) {
        case HAND_TSUMO_FIELD_RIVER_URADORA:
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
        case HAND_TSUMO_FIELD_RIVER_URADORA:
            return true;
        default:
            return false;
        }
    }
    
}

