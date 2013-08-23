/**
 * SoloJanController.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import wiz.project.jan.Hand;
import wiz.project.jan.JanPai;
import wiz.project.jan.Wind;
import wiz.project.janbot.game.exception.GameSetException;
import wiz.project.janbot.game.exception.InvalidInputException;
import wiz.project.janbot.game.exception.JanException;



/**
 * 麻雀コントローラ (ソロ)
 */
class SoloJanController implements JanController {
    
    /**
     * コンストラクタ
     */
    public SoloJanController() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param observer 監視者。
     */
    public SoloJanController(final Observer observer) {
        if (observer != null) {
            _info.addObserver(observer);
        }
    }
    
    
    
    /**
     * 打牌 (ツモ切り)
     */
    public void discard() throws JanException {
        _firstPhase = false;
        
        synchronized (_GAME_INFO_LOCK) {
            discardCore(_info.getActiveTsumo());
            
            // 次巡へ
            _info.setActiveWindToNext();
            onPhase();
        }
    }
    
    /**
     * 打牌 (手出し)
     */
    public void discard(final JanPai target) throws JanException {
        if (target == null) {
            throw new NullPointerException("Discard target is null.");
        }
        
        synchronized (_GAME_INFO_LOCK) {
            final JanPai activeTsumo = _info.getActiveTsumo();
            if (target == activeTsumo) {
                // 直前のツモ牌が指定された
                discard();
                return;
            }
            
            final Hand hand = _info.getActiveHand();
            if (!hand.getMenZenMap().containsKey(target)) {
                // 手牌に存在しないが指定された
                throw new InvalidInputException("Invalid discard target - " + target);
            }
            
            // 打牌
            _firstPhase = false;
            hand.removeJanPai(target);
            hand.addJanPai(activeTsumo);
            discardCore(target);
            
            // 次巡へ
            _info.setActiveWindToNext();
            onPhase();
        }
    }
    
    /**
     * ゲーム情報を取得
     */
    public JanInfo getGameInfo() {
        synchronized (_GAME_INFO_LOCK) {
            return _info.clone();
        }
    }
    
    /**
     * 開始
     */
    public void start(final List<JanPai> deck, final Map<Wind, Player> playerTable) throws JanException {
        if (deck == null) {
            throw new NullPointerException("Deck is null.");
        }
        if (playerTable == null) {
            throw new NullPointerException("Player table is null.");
        }
        if (deck.size() != (JanPai.values().length * 4)) {
            throw new IllegalArgumentException("Invalid deck size - " + deck.size());
        }
        if (playerTable.size() != 4) {
            throw new IllegalArgumentException("Invalid player table size - " + playerTable.size());
        }
        
        synchronized (_GAME_INFO_LOCK) {
            _info.clear();
            
            // 席決めと山積み
            _info.setFieldWind(Wind.TON);
            _info.setPlayerTable(playerTable);
            _info.setDeck(deck);
            
            // 王牌を生成
            final int deckSize = deck.size();
            _info.setWanPai(new WanPai(new ArrayList<>(deck.subList(deckSize - 14, deckSize))));
            
            // 配牌
            _info.setHand(Wind.TON, new Hand(new ArrayList<JanPai>(deck.subList( 0, 13))));
            _info.setHand(Wind.NAN, new Hand(new ArrayList<JanPai>(deck.subList(13, 26))));
            _info.setHand(Wind.SHA, new Hand(new ArrayList<JanPai>(deck.subList(26, 39))));
            _info.setHand(Wind.PEI, new Hand(new ArrayList<JanPai>(deck.subList(39, 52))));
            _info.setDeckIndex(13 * 4);
            _info.setRemainCount(70);
            
            // 1巡目
            _firstPhase = true;
            _info.setActiveWind(Wind.TON);
            onPhase();
        }
    }
    
    
    
    /**
     * 牌を切る
     * 
     * @param target 対象牌。
     */
    private void discardCore(final JanPai target) {
        _info.addDiscard(_info.getActiveWind(), target);
        _info.setActiveDiscard(target);
        // TODO 鳴き処理
    }
    
    /**
     * 牌をツモる
     * 
     * @return ツモ牌。
     */
    private JanPai getJanPaiFromDeck() {
        final JanPai pai = _info.getJanPaiFromDeck();
        _info.increaseDeckIndex();
        return pai;
    }
    
    /**
     * 巡目ごとの処理
     * 
     * @throws GameSetException 局が終了した。
     */
    private void onPhase() throws GameSetException {
        if (_info.getRemainCount() == 0) {
            throw new GameSetException(GameSetStatus.GAME_OVER);
        }
        
        // 牌をツモる
        final JanPai activeTsumo = getJanPaiFromDeck();
        _info.setActiveTsumo(activeTsumo);
        _info.decreaseRemainCount();
        
        // 打牌
        final Player activePlayer = _info.getActivePlayer();
        switch (activePlayer.getType()) {
        case COM:
            // ツモ切り
            discardCore(activeTsumo);
            
            // 次巡へ
            _info.setActiveWindToNext();
            onPhase();
            return;
        case HUMAN:
            _info.notifyObservers(_firstPhase ? GameAnnounceType.HAND_TSUMO_FIELD : GameAnnounceType.HAND_TSUMO);
            break;
        }
    }
    
    
    
    /**
     * ロックオブジェクト
     */
    private final Object _GAME_INFO_LOCK = new Object();
    
    
    
    /**
     * 麻雀ゲーム情報
     */
    private JanInfo _info = new JanInfo();
    
    /**
     * 初巡フラグ
     */
    private volatile boolean _firstPhase = true;
    
}

