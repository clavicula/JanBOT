/**
 * SoloJanController.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.TreeMap;

import wiz.project.jan.Hand;
import wiz.project.jan.JanPai;
import wiz.project.jan.Wind;
import wiz.project.jan.util.HandCheckUtil;
import wiz.project.jan.util.JanPaiUtil;
import wiz.project.janbot.game.exception.BoneheadException;
import wiz.project.janbot.game.exception.CallableException;
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
            synchronized (_GAME_INFO_LOCK) {
                _info.addObserver(observer);
            }
        }
    }
    
    
    
    /**
     * 副露
     */
    public void call(final String playerName, final CallType type) throws JanException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (type == null) {
            throw new NullPointerException("Call type is null.");
        }
        if (playerName.isEmpty()) {
            throw new IllegalArgumentException("Player name is empty.");
        }
        if (!_onGame) {
            throw new JanException("Game is not started.");
        }
        
        // TODO 副露対応
    }
    
    /**
     * 和了 (ロン)
     */
    public void completeRon(final String playerName) throws JanException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (playerName.isEmpty()) {
            throw new IllegalArgumentException("Player name is empty.");
        }
        if (!_onGame) {
            throw new JanException("Game is not started.");
        }
        
        synchronized (_GAME_INFO_LOCK) {
            if (!_info.isValidPlayer(playerName)) {
                throw new IllegalArgumentException("Inavlid player name - " + playerName);
            }
            
            // 打牌したプレイヤーの風を記録
            final Wind activeWind = _info.getActiveWind();
            try {
                // ロン宣言したプレイヤーをアクティブ化して判定
                _info.setActivePlayer(playerName);
                final JanPai discard = _info.getActiveDiscard();
                final Map<JanPai, Integer> handWithDiscard = getHandMap(_info.getActiveWind(), discard);
                if (!HandCheckUtil.isComplete(handWithDiscard)) {
                    // チョンボ
                    throw new BoneheadException("Not completed.");
                }
                if (_info.getActiveRiver().contains(discard)) {
                    // フリテン
                    throw new BoneheadException("Furiten.");
                }
                // TODO 役確認
            }
            catch (final Throwable e) {
                // 和了しない場合、アクティブプレイヤーを元に戻す
                _info.setActiveWind(activeWind);
                throw e;
            }
            
            _onGame = false;
            _info.notifyObservers(GameAnnounceType.COMPLETE_RON);
        }
    }
    
    /**
     * 和了 (ツモ)
     */
    public void completeTsumo() throws JanException {
        if (!_onGame) {
            throw new JanException("Game is not started.");
        }
        
        synchronized (_GAME_INFO_LOCK) {
            final Map<JanPai, Integer> handWithTsumo = getHandMap(_info.getActiveWind(), _info.getActiveTsumo());
            if (!HandCheckUtil.isComplete(handWithTsumo)) {
                // チョンボ
                throw new BoneheadException("Not completed.");
            }
            
            _onGame = false;
            _info.notifyObservers(GameAnnounceType.COMPLETE_TSUMO);
        }
    }
    
    /**
     * 打牌 (ツモ切り)
     */
    public void discard() throws JanException {
        if (!_onGame) {
            throw new JanException("Game is not started.");
        }
        
        _firstPhase = false;
        
        synchronized (_GAME_INFO_LOCK) {
            discardCore(_info.getActiveTsumo());
            
            // 次の打牌へ
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
        if (!_onGame) {
            throw new JanException("Game is not started.");
        }
        
        synchronized (_GAME_INFO_LOCK) {
            final JanPai activeTsumo = _info.getActiveTsumo();
            if (target == activeTsumo) {
                // 直前のツモ牌が指定された
                discard();
                return;
            }
            
            final Hand hand = _info.getActiveHand();
            if (hand.getMenZenMap().get(target) <= 0) {
                // 手牌に存在しないが指定された
                throw new InvalidInputException("Invalid discard target - " + target);
            }
            
            // 打牌
            _firstPhase = false;
            hand.removeJanPai(target);
            hand.addJanPai(activeTsumo);
            
            final Wind activeWind = _info.getActiveWind();
            _info.setHand(activeWind, hand);
            discardCore(target);
            
            // 手変わりがあったので聴牌判定
            _completeWait.put(activeWind, HandCheckUtil.getCompletableJanPaiList(getHandMap(activeWind)));
            
            // 次の打牌へ
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
     * 次のプレイヤーの打牌へ
     */
    public void next() throws JanException {
        if (!_onGame) {
            throw new JanException("Game is not started.");
        }
        
        synchronized (_GAME_INFO_LOCK) {
            _info.setActiveWindToNext();
            onPhase();
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
        if (_onGame) {
            throw new JanException("Game is already started.");
        }
        
        synchronized (_GAME_INFO_LOCK) {
            _onGame = true;
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
            
            // 聴牌判定
            for (final Wind wind : Wind.values()) {
                if (playerTable.get(wind).getType() == PlayerType.COM) {
                    // NPCはツモ切り専用
                    _completeWait.put(wind, new ArrayList<JanPai>());
                }
                else {
                    _completeWait.put(wind, HandCheckUtil.getCompletableJanPaiList(getHandMap(wind)));
                }
            }
            
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
     * @throws CallableException 副露が可能。
     */
    private void discardCore(final JanPai target) throws CallableException {
        final Wind activeWind = _info.getActiveWind();
        _info.addDiscard(activeWind, target);
        _info.setActiveDiscard(target);
        
        Wind targetWind = activeWind.getNext();
        while (targetWind != activeWind) {
            if (_info.getPlayer(targetWind).getType() != PlayerType.COM) {
                final List<CallType> callableList = getCallableList(targetWind, target);
                if (!callableList.isEmpty()) {
                    throw new CallableException(callableList);
                }
            }
            targetWind = targetWind.getNext();
        }
    }
    
    /**
     * 可能な副露リストを取得
     * 
     * @param wind 判定対象の風。
     * @param discard 捨て牌。
     * @return 可能な副露リスト。
     */
    private List<CallType> getCallableList(final Wind wind, final JanPai discard) {
        final List<CallType> callTypeList = new ArrayList<>();
        // TODO 副露対応
        if (_completeWait.get(wind).contains(discard)) {
            callTypeList.add(CallType.RON);
        }
        return callTypeList;
    }
    
    /**
     * プレイヤーの手牌マップを取得
     * 
     * @param wind プレイヤーの風。
     * @return プレイヤーの手牌マップ。
     */
    private Map<JanPai, Integer> getHandMap(final Wind wind) {
        final Map<JanPai, Integer> hand = _info.getHand(wind).getMenZenMap();
        JanPaiUtil.cleanJanPaiMap(hand);
        return hand;
    }
    
    /**
     * 指定牌込みでプレイヤーの手牌マップを取得
     * 
     * @param wind プレイヤーの風。
     * @param source 手牌に追加する牌。
     * @return プレイヤーの手牌マップ。
     */
    private Map<JanPai, Integer> getHandMap(final Wind wind, final JanPai source) {
        final Map<JanPai, Integer> hand = _info.getHand(wind).getMenZenMap();
        JanPaiUtil.addJanPai(hand, source, 1);
        JanPaiUtil.cleanJanPaiMap(hand);
        return hand;
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
     * @throws CallableException 副露が可能。
     * @throws GameSetException 局が終了した。
     */
    private void onPhase() throws CallableException, GameSetException {
        if (_info.getRemainCount() == 0) {
            _onGame = false;
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
     * ゲーム中か
     */
    private volatile boolean _onGame = false;
    
    /**
     * 初巡フラグ
     */
    private volatile boolean _firstPhase = true;
    
    /**
     * 和了の待ち
     */
    private Map<Wind, List<JanPai>> _completeWait = Collections.synchronizedMap(new TreeMap<Wind, List<JanPai>>());
    
}

