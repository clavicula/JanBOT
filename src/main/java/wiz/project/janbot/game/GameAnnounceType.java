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
     * ロン和了
     */
    COMPLETE_RON,
    
    /**
     * ツモ和了
     */
    COMPLETE_TSUMO,
    
    /**
     * 流局
     */
    GAME_OVER,
    
    /**
     * ロン可能
     */
    CALLABLE_RON,
    
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
     * 当たり牌を実況するか
     * 
     * @return 判定結果。
     */
    public boolean isAnnounceActiveDiscard() {
        switch (this) {
        case COMPLETE_RON:
            return true;
        default:
            return false;
        }
    }
    
    /**
     * 場情報を実況するか
     * 
     * @return 判定結果。
     */
    public boolean isAnnounceField() {
        switch (this) {
        case COMPLETE_TSUMO:
        case GAME_OVER:
        case CALLABLE_RON:
        case HAND_TSUMO_FIELD:
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
        case COMPLETE_TSUMO:
        case GAME_OVER:
        case CALLABLE_RON:
        case HAND:
        case HAND_TSUMO:
        case HAND_TSUMO_FIELD:
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
        case COMPLETE_TSUMO:
        case GAME_OVER:
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
        case COMPLETE_RON:
        case COMPLETE_TSUMO:
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
        case COMPLETE_TSUMO:
        case HAND_TSUMO:
        case HAND_TSUMO_FIELD:
            return true;
        default:
            return false;
        }
    }
    
    /**
     * 副露可能か
     * 
     * @return 判定結果。
     */
    public boolean isCallable() {
        switch (this) {
        case CALLABLE_RON:
            return true;
        default:
            return false;
        }
    }
    
    /**
     * ロン可能か
     * 
     * @return 判定結果。
     */
    public boolean isCallableRon() {
        switch (this) {
        case CALLABLE_RON:
            return true;
        default:
            return false;
        }
    }
    
}

