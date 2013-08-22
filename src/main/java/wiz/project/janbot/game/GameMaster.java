/**
 * GameMaster.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import wiz.io.serializer.Serializer;
import wiz.project.jan.JanPai;
import wiz.project.jan.Wind;
import wiz.project.jan.util.JanPaiUtil;
import wiz.project.janbot.JanBOT;
import wiz.project.janbot.game.exception.JanException;



/**
 * ゲーム管理
 */
public final class GameMaster {
    
    /**
     * コンストラクタを自分自身に限定許可
     */
    private GameMaster() {
    }
    
    
    
    /**
     * インスタンスを取得
     * 
     * @return インスタンス。
     */
    public static GameMaster getInstance() {
        return INSTANCE;
    }
    
    
    
    /**
     * ゲームの状態を取得
     * 
     * @return ゲームの状態。
     */
    public GameStatus getStatus() {
        synchronized (_STATUS_LOCK) {
            return _status;
        }
    }
    
    /**
     * 終了処理
     */
    public void onEnd() {
        synchronized (_STATUS_LOCK) {
            if (_status != GameStatus.IDLE) {
                _status = GameStatus.IDLE;
                JanBOT.getInstance().println("--- 終了 ---");
            }
        }
    }
    
    /**
     * 開始処理 (ソロ)
     * 
     * @param playerName プレイヤー名。
     * @throws JanException ゲーム処理エラー。
     * @throws IOException ファイル入出力に失敗。
     */
    public void onStartSolo(final String playerName) throws JanException, IOException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (playerName.isEmpty()) {
            throw new IllegalArgumentException("Player name is empty.");
        }
        
        // 開始済み判定
        synchronized (_STATUS_LOCK) {
            if (!_status.isIdle()) {
                JanBOT.getInstance().println("--- Already started ---");
                return;
            }
            _status = GameStatus.PLAYING_SOLO;
        }
        
        // 牌山生成
        final List<JanPai> deck = JanPaiUtil.createAllJanPaiList();
        Collections.shuffle(deck, new SecureRandom());
        
        // 席決め
        final Map<Wind, Player> playerTable = createPlayerTable(Arrays.asList(playerName));
        
        // 保存 (リプレイ用)
        Serializer.writeOverwrite(deck, DECK_SAVE_PATH);
        Serializer.writeOverwrite(playerTable, PLAYER_TABLE_SAVE_PATH);
        
        // ゲーム開始
        synchronized (_CONTROLLER_LOCK) {
            _controller = createJanController(true);
            _controller.start(deck, playerTable);
        }
    }
    
    
    
    /**
     * 麻雀コントローラを生成
     * 
     * @param solo ソロプレイか。
     * @return 麻雀コントローラ。
     */
    private JanController createJanController(final boolean solo) {
        if (solo) {
            return new SoloJanController();
        }
        else {
            // TODO ネトマ未実装
            return null;
        }
    }
    
    /**
     * プレイヤーテーブルを生成
     * 
     * @param playerNameList 参加プレイヤー名のリスト。
     * @return プレイヤーテーブル。
     */
    private Map<Wind, Player> createPlayerTable(final List<String> playerNameList) {
        // 風をシャッフル
        final List<Wind> windList = new ArrayList<>(Arrays.asList(Wind.values()));
        Collections.shuffle(windList, new SecureRandom());
        
        // プレイヤーを格納
        final Map<Wind, Player> playerTable = new TreeMap<>();
        for (final String playerName : playerNameList) {
            playerTable.put(windList.remove(0), new Player(playerName, PlayerType.HUMAN));
        }
        
        // 4人になるまでNPCで埋める
        final int limitCOM = 4 - playerNameList.size();
        for (int i = 0; i < limitCOM; i++) {
            playerTable.put(windList.remove(0), NPC_LIST.get(i));
        }
        return playerTable;
    }
    
    
    
    /**
     * 自分自身のインスタンス
     */
    private static final GameMaster INSTANCE = new GameMaster();
    
    /**
     * 保存パス
     */
    private static final String DECK_SAVE_PATH         = "./deck.bin";
    private static final String PLAYER_TABLE_SAVE_PATH = "./player_table.bin";
    
    /**
     * NPCリスト
     */
    private static final List<Player> NPC_LIST =
        Collections.unmodifiableList(Arrays.asList(new Player("COM_01", PlayerType.COM),
                                                   new Player("COM_02", PlayerType.COM),
                                                   new Player("COM_03", PlayerType.COM)));
    
    
    
    /**
     * ロックオブジェクト (ゲームコントローラ)
     */
    private final Object _CONTROLLER_LOCK = new Object();
    
    /**
     * ロックオブジェクト (ゲームの状態)
     */
    private final Object _STATUS_LOCK = new Object();
    
    
    
    /**
     * ゲームコントローラ
     */
    private JanController _controller = null;
    
    /**
     * ゲームの状態
     */
    private GameStatus _status = GameStatus.IDLE;
    
}

