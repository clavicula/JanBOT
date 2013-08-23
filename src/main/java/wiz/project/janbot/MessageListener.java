/**
 * MessageListener.java
 * 
 * @author Yuki
 */

package wiz.project.janbot;

import java.util.Arrays;
import java.util.List;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import wiz.project.janbot.game.CallType;
import wiz.project.janbot.game.GameAnnounceType;
import wiz.project.janbot.game.GameMaster;
import wiz.project.janbot.game.GameSetStatus;
import wiz.project.janbot.game.exception.CallableException;
import wiz.project.janbot.game.exception.GameSetException;
import wiz.project.janbot.game.exception.JanException;



/**
 * メッセージ受付
 */
final class MessageListener<T extends PircBotX> extends ListenerAdapter<T> {
    
    /**
     * コンストラクタ
     */
    public MessageListener() {
    }
    
    
    
    /**
     * メッセージ受信時の処理
     * 
     * @param event イベント情報。
     * @throws InterruptedException 処理に失敗。
     */
    @Override
    public void onMessage(final MessageEvent<T> event) throws Exception {
        if (event == null) {
            throw new NullPointerException("Event information is null.");
        }
        
        // メッセージ解析
        final String message = event.getMessage();
        try {
            if (message.equals("jantest ochiro")) {
                JanBOT.getInstance().println("(  ；∀；)");
                JanBOT.getInstance().disconnect();
            }
            else if (message.equals("jantest start")) {
                GameMaster.getInstance().onStartSolo(event.getUser().getNick());
            }
            else if (message.equals("jantest end")) {
                GameMaster.getInstance().onEnd();
            }
            else if (message.equals("jantest d")) {
                GameMaster.getInstance().onDiscard();
            }
            else if (message.startsWith("jantest d ")) {
                GameMaster.getInstance().onDiscard(message.substring(10));
            }
            else if (message.equals("jantest i")) {
                GameMaster.getInstance().onInfo(GameAnnounceType.FIELD);
            }
            else if (message.equals("jantest r")) {
                GameMaster.getInstance().onInfo(GameAnnounceType.RIVER);
            }
            else if (message.equals("jantest i r") || message.equals("jantest r i")) {
                GameMaster.getInstance().onInfo(GameAnnounceType.FIELD_RIVER);
            }
            else if (message.equals("jantest ron")) {
                GameMaster.getInstance().onCompleteRon(event.getUser().getNick());
            }
            else if (message.equals("jantest tsumo")) {
                GameMaster.getInstance().onCompleteTsumo(event.getUser().getNick());
            }
            else if (message.equals("jantest help")) {
                final List<String> messageList = Arrays.asList("start：開始   end：終了",
                "i：状態   r：捨て牌   d X：指定牌(ex.9p)を切る (X指定無し：ツモ切り)",
                "tsumo：ツモ和了");
                JanBOT.getInstance().println(messageList);
            }
        }
        catch (final CallableException e) {
            // TODO 副露対応
            final List<CallType> callTypeList = e.getTypeList();
            if (callTypeList.contains(CallType.RON)) {
                GameMaster.getInstance().onInfo(GameAnnounceType.CALLABLE_RON);
            }
        }
        catch (final GameSetException e) {
            onGameSet(e.getStatus());
        }
        catch (final JanException e) {
            // TODO ログ記録
        }
    }
    
    /**
     * トーク受信時の処理
     * 
     * @param event イベント情報。
     * @throws Exception 処理に失敗。
     */
    @Override
    public void onPrivateMessage(final PrivateMessageEvent<T> event) throws Exception {
        if (event == null) {
            throw new NullPointerException("Event information is null.");
        }
        
        // TODO ネトマ未対応
        super.onPrivateMessage(event);
//        final String message = event.getMessage();
    }
    
    
    
    /**
     * ゲーム終了時の処理
     * 
     * @param status ゲーム終了状態。
     */
    private void onGameSet(final GameSetStatus status) {
        switch (status) {
        case GAME_OVER:
            GameMaster.getInstance().onInfo(GameAnnounceType.GAME_OVER);
            break;
        default:
            throw new InternalError();
        }
    }
    
}

