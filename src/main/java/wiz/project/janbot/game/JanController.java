/**
 * JanController.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.util.List;
import java.util.Map;

import wiz.project.jan.JanPai;
import wiz.project.jan.Wind;
import wiz.project.janbot.game.exception.JanException;



/**
 * 麻雀コントローラ
 */
interface JanController {
    
    /**
     * 打牌 (ツモ切り)
     * 
     * @throws JanException 処理に失敗。
     */
    public void discard() throws JanException;
    
    /**
     * 打牌 (手出し)
     * 
     * @param target 捨て牌。
     * @throws JanException 処理に失敗。
     */
    public void discard(final JanPai target) throws JanException;
    
    /**
     * ゲーム情報を取得
     * 
     * @return ゲーム情報。
     */
    public JanInfo getGameInfo();
    
    /**
     * 開始
     * 
     * @param deck 牌山。
     * @param playerTable プレイヤーテーブル。
     * @throws JanException 処理に失敗。
     */
    public void start(final List<JanPai> deck, final Map<Wind, Player> playerTable) throws JanException;
    
}

