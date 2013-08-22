/**
 * SoloJanController.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import wiz.project.jan.Hand;
import wiz.project.jan.JanPai;
import wiz.project.jan.Wind;
import wiz.project.janbot.game.exception.GameSetException;
import wiz.project.janbot.game.exception.JanException;



/**
 * 麻雀コントローラ (ソロ)
 * 
 * ※性能を考慮して排他制御しない。マルチスレッド化の際には注意。
 */
class SoloJanController implements JanController {
    
    /**
     * コンストラクタ
     */
    public SoloJanController() {
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
        
        _info.addObserver(new GameAnnouncer());
        
        _info.setPlayerTable(playerTable);
        _info.setDeck(deck);
        _info.setFieldWind(Wind.TON);
        
        // 王牌を生成
        final int deckSize = deck.size();
        _info.setWanPai(new WanPai(new ArrayList<>(deck.subList(deckSize - 14, deckSize))));
        
        // 配牌
        final Map<Wind, Hand> handTable = new TreeMap<>();
        handTable.put(Wind.TON, new Hand(new ArrayList<JanPai>(deck.subList( 0, 13))));
        handTable.put(Wind.NAN, new Hand(new ArrayList<JanPai>(deck.subList(13, 26))));
        handTable.put(Wind.SHA, new Hand(new ArrayList<JanPai>(deck.subList(26, 39))));
        handTable.put(Wind.PEI, new Hand(new ArrayList<JanPai>(deck.subList(39, 52))));
        _info.setHandTable(handTable);
        _info.setDeckIndex(13 * 4);
        _info.setRemainCount(70);
        
        // 1巡目
        _firstPhase = true;
        _info.setActiveWind(Wind.TON);
        onPhase();
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
            _info.setActiveDiscard(activeTsumo);
            // TODO 鳴き処理
            
            // 次順へ
            _info.setActiveWind(_info.getActiveWind().getNext());
            onPhase();
            return;
        case HUMAN:
            _info.notifyObservers(_firstPhase ? AnnounceType.HAND_TSUMO_FIELD : AnnounceType.HAND_TSUMO);
            break;
        }
    }
    
    
    
    /**
     * 麻雀ゲーム情報
     */
    private JanInfo _info = new JanInfo();
    
    /**
     * 初順フラグ
     */
    private boolean _firstPhase = true;
    
}

