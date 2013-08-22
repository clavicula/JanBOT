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
     * 開始
     * 
     * @param deck 牌山。
     * @param playerTable プレイヤーテーブル。
     * @throws JanException 処理に失敗。
     */
    public void start(final List<JanPai> deck, final Map<Wind, Player> playerTable) throws JanException;
    
}

