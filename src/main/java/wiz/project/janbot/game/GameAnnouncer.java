/**
 * GameAnnouncer.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import wiz.project.jan.Hand;
import wiz.project.jan.JanPai;
import wiz.project.jan.MenTsu;
import wiz.project.jan.MenTsuType;
import wiz.project.janbot.JanBOT;



/**
 * ゲーム実況者
 */
public class GameAnnouncer implements Observer {
    
    /**
     * コンストラクタ
     */
    public GameAnnouncer() {
    }
    
    
    
    /**
     * 状況更新時の処理
     * 
     * @param target 監視対象。
     * @param param 更新パラメータ。
     */
    public void update(final Observable target, final Object param) {
        if (target instanceof JanInfo) {
            if (param instanceof GameAnnounceType) {
                updateOnSolo((JanInfo)target, (GameAnnounceType)param);
            }
        }
    }
    
    
    
    /**
     * 状況更新時の処理
     * 
     * @param info 麻雀ゲーム情報。
     */
    protected void updateOnSolo(final JanInfo info, final GameAnnounceType type) {
        if (info == null) {
            throw new NullPointerException("Game information is null.");
        }
        if (type == null) {
            throw new NullPointerException("Announce type is null.");
        }
        
        final Hand hand = info.getActiveHand();
        final JanPai activeTsumo = info.getActiveTsumo();
        
        final List<String> messageList = new ArrayList<>();
        if (type.isAnnounceField()) {
            messageList.add(convertFieldToString(info));
        }
        if (type.isAnnounceRiverSingle()) {
            messageList.add(convertRiverToString(info.getActiveRiver()));
        }
        if (type.isAnnounceHand()) {
            final StringBuilder buf = new StringBuilder();
            buf.append(convertHandToString(hand));
            if (type.isAnnounceTsumo()) {
                buf.append(" ").append(convertTsumoToString(activeTsumo));
            }
            messageList.add(buf.toString());
        }
        
        JanBOT.getInstance().println(messageList);
    }
    
    
    
    /**
     * 場情報を文字列に変換
     * 
     * @param info ゲーム情報。
     * @return 変換結果。
     */
    private String convertFieldToString(final JanInfo info) {
        final StringBuilder buf = new StringBuilder();
        buf.append("場風：").append(info.getFieldWind()).append("   ");
        buf.append("自風：").append(info.getActiveWind()).append("   ");
        buf.append("ドラ：");
        for (final JanPai pai : info.getWanPai().getDoraList()) {
            buf.append(COLOR_FLAG).append(getColorCode(pai));
            buf.append(pai);
            buf.append(COLOR_FLAG);
        }
        buf.append("   ");
        buf.append("残り枚数：").append(info.getRemainCount());
        return buf.toString();
    }
    
    /**
     * 手牌を文字列に変換
     * 
     * @param hand 手牌。
     * @return 変換結果。
     */
    private String convertHandToString(final Hand hand) {
        final StringBuilder buf = new StringBuilder();
        for (final JanPai pai : hand.getMenZenList()) {
            buf.append(COLOR_FLAG).append(getColorCode(pai));
            buf.append(pai);
            buf.append(COLOR_FLAG);
        }
        
        if (hand.getFixedMenTsuCount() == 0) {
            return buf.toString();
        }
        
        buf.append(" ");
        for (final MenTsu fixedMenTsu : hand.getFixedMenTsuList()) {
            buf.append(" ");
            final List<JanPai> sourceList = fixedMenTsu.getSource();
            if (fixedMenTsu.getMenTsuType() == MenTsuType.KAN_DARK) {
                final JanPai pai = sourceList.get(0);
                final String source = "[■]" + pai + pai + "[■]";
                buf.append(COLOR_FLAG).append(getColorCode(pai)).append(source).append(COLOR_FLAG);
            }
            else {
                buf.append(COLOR_FLAG).append(getColorCode(sourceList.get(0)));
                for (final JanPai pai : sourceList) {
                    buf.append(pai);
                }
                buf.append(COLOR_FLAG);
            }
        }
        return buf.toString();
    }
    
    /**
     * 捨て牌リストを文字列に変換
     * 
     * @param river 捨て牌リスト。
     * @return 変換結果。
     */
    private String convertRiverToString(final List<JanPai> river) {
        final StringBuilder buf = new StringBuilder();
        int count = 1;
        for (final JanPai pai : river) {
            buf.append(COLOR_FLAG).append(getColorCode(pai));
            buf.append(pai);
            buf.append(COLOR_FLAG);
            
            if (count % 6 == 0) {
                buf.append(" ");
            }
            count++;
        }
        return buf.toString();
    }
    
    /**
     * ツモ牌を文字列に変換
     * 
     * @param pai ツモ牌。
     * @return 変換結果。
     */
    private String convertTsumoToString(final JanPai pai) {
        final StringBuilder buf = new StringBuilder();
        buf.append(COLOR_FLAG).append(getColorCode(pai));
        buf.append(pai);
        buf.append(COLOR_FLAG);
        return buf.toString();
    }
    
    /**
     * 色コードを取得
     * 
     * @param pai 雀牌。
     * @return 対応する色コード。
     */
    private String getColorCode(final JanPai pai) {
        switch (pai) {
        case MAN_1:
        case MAN_2:
        case MAN_3:
        case MAN_4:
        case MAN_5:
        case MAN_6:
        case MAN_7:
        case MAN_8:
        case MAN_9:
        case CHUN:
            return "04";  // 赤
        case PIN_1:
        case PIN_2:
        case PIN_3:
        case PIN_4:
        case PIN_5:
        case PIN_6:
        case PIN_7:
        case PIN_8:
        case PIN_9:
            return "12";  // 青
        case SOU_1:
        case SOU_2:
        case SOU_3:
        case SOU_4:
        case SOU_5:
        case SOU_6:
        case SOU_7:
        case SOU_8:
        case SOU_9:
        case HATU:
            return "03";  // 緑
        case TON:
        case NAN:
        case SHA:
        case PEI:
            return "06";  // 紫
        default:
            return "01";  // 黒
        }
    }
    
    
    
    /**
     * 色付けフラグ
     */
    private static final char COLOR_FLAG = 3;
    
}

