/**
 * MessageListener.java
 * 
 * @author Yuki
 */

package wiz.project.janbot;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import wiz.project.janbot.game.GameMaster;



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
    
}

