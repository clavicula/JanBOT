/**
 * JanBOT.java
 * 
 * @Author Yuki
 */

package wiz.project.janbot;

import java.io.IOException;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;



/**
 * BOT本体
 */
public final class JanBOT {
    
    /**
     * コンストラクタを自分自身に限定許可
     */
    private JanBOT() {
    }
    
    
    
    /**
     * インスタンスを取得
     * 
     * @return インスタンス。
     */
    public static JanBOT getInstance() {
        return INSTANCE;
    }
    
    /**
     * エントリポイント
     * 
     * @param paramList 実行引数リスト。
     */
    public static void main(final String[] paramList) {
        if (paramList == null) {
            throw new NullPointerException("Parameter list is null.");
        }
        if (paramList.length < PARAM_SIZE) {
            System.out.println("Call with parameter. (ex.: java -jar janbot.jar \"foo,irc.net\" \"1234\" \"#your-channel\")");
            return;
        }
        
        try {
            final String serverHost = paramList[PARAM_INDEX_SERVER_URI];
            final int serverPort = Integer.parseInt(paramList[PARAM_INDEX_SERVER_PORT]);
            final String channel = paramList[PARAM_INDEX_CHANNEL_NAME];
            INSTANCE.initialize(BOT_NAME, serverHost, serverPort, channel);
        }
        catch (final Throwable e) {
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * 初期化処理
     * 
     * @param nickname BOTのニックネーム。
     * @param serverHost 接続先サーバURI。
     * @param serverPort 接続先サーバポート。
     * @throws NickAlreadyInUseException ニックネームが使用されている。
     * @throws IrcException IRC接続に失敗。
     * @throws IOException 入出力エラー。
     */
    public void initialize(final String nickname, final String serverHost, final int serverPort, final String channel)
            throws NickAlreadyInUseException, IrcException, IOException {
        if (nickname == null) {
            throw new NullPointerException("Nickname is null.");
        }
        if (serverHost == null) {
            throw new NullPointerException("Server host name is null.");
        }
        if (channel == null) {
            throw new NullPointerException("Channel name is null.");
        }
        if (nickname.isEmpty()) {
            throw new IllegalArgumentException("Nickname is empty.");
        }
        if (serverHost.isEmpty()) {
            throw new IllegalArgumentException("Server host name is empty.");
        }
        if (serverPort < 0) {
            throw new IllegalArgumentException("Invalid server port - " + serverPort);
        }
        if (channel.isEmpty()) {
            throw new IllegalArgumentException("Channel name is empty.");
        }
        
        synchronized (_CORE_LOCK) {
            _core.setName(nickname);
            _core.setVerbose(true);
            _core.setAutoNickChange(true);
            _core.setCapEnabled(true);
            _core.getListenerManager().addListener(createListener());
            
            _core.connect(serverHost, serverPort);
            _core.joinChannel(channel);
            
            synchronized (_CHANNEL_LOCK) {
                _channel = _core.getChannel(channel);
            }
        }
    }
    
    /**
     * サーバから切断
     */
    public void disconnect() {
        synchronized (_CORE_LOCK) {
            _core.disconnect();
        }
    }
    
    /**
     * メッセージを出力
     * 
     * @param message メッセージ。
     */
    public void println(final String message) {
        if (message == null) {
            throw new NullPointerException("Message is null.");
        }
        
        synchronized (_CHANNEL_LOCK) {
            if (_channel != null) {
                synchronized (_CORE_LOCK) {
                    println(_channel, message);
                }
            }
        }
    }
    
    /**
     * メッセージを出力 (複数行)
     * 
     * @param messageList メッセージリスト。
     */
    public void println(final List<String> messageList) {
        if (messageList == null) {
            throw new NullPointerException("Message list is null.");
        }
        if (messageList.isEmpty()) {
            // 何もしない
            return;
        }
        
        synchronized (_CHANNEL_LOCK) {
            if (_channel != null) {
                synchronized (_CORE_LOCK) {
                    println(_channel, convertToSingleMessage(messageList));
                }
            }
        }
    }
    
    /**
     * 個別メッセージを出力
     * 
     * @param playerName プレイヤー名。 
     * @param message メッセージ。
     */
    public void talk(final String playerName, final String message) {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (message == null) {
            throw new NullPointerException("Talk message is null.");
        }
        
        synchronized (_CORE_LOCK) {
            final User user = _core.getUser(playerName);
            _core.sendMessage(user, message);
        }
    }
    
    /**
     * 個別メッセージを出力
     * 
     * @param playerName プレイヤー名。 
     * @param messageList メッセージリスト。
     */
    public void talk(final String playerName, final List<String> messageList) {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (messageList == null) {
            throw new NullPointerException("Message list is null.");
        }
        if (messageList.isEmpty()) {
            // 何もしない
            return;
        }
        
        talk(playerName, convertToSingleMessage(messageList));
    }
    
    
    
    /**
     * 単一のメッセージに変換
     * 
     * @param messageList メッセージリスト。
     * @return 変換結果。
     */
    private String convertToSingleMessage(final List<String> messageList) {
        final String eol = "\r\nPRIVMSG " + _channel.getName() + " :";
        final StringBuilder buf = new StringBuilder();
        for (final String message : messageList) {
            if (message != null) {
                if (buf.length() > 0) {
                    buf.append(eol);
                }
                buf.append(message);
            }
        }
        return buf.toString();
    }
    
    /**
     * リスナーオブジェクトを生成
     * 
     * @return リスナーオブジェクト。
     */
    private <T extends PircBotX> MessageListener<T> createListener() {
        return new MessageListener<T>();
    }
    
    /**
     * メッセージを出力
     * 
     * @param channel 対象チャンネル。
     * @param message メッセージ。
     */
    private void println(final Channel channel, final String message) {
        _core.sendMessage(channel, message);
    }
    
    
    
    /**
     * 自分自身のインスタンス
     */
    private static final JanBOT INSTANCE = new JanBOT();
    
    
    
    /**
     * BOTのニックネーム
     */
    private static final String BOT_NAME = "JanBOT";
    
    /**
     * 実行パラメータサイズ
     */
    private static final int PARAM_SIZE = 3;
    
    /**
     * 実行パラメータインデックス
     */
    private static final int PARAM_INDEX_SERVER_URI   = 0;
    private static final int PARAM_INDEX_SERVER_PORT  = 1;
    private static final int PARAM_INDEX_CHANNEL_NAME = 2;
    
    
    
    /**
     * ロックオブジェクト (BOT本体)
     */
    private final Object _CORE_LOCK = new Object();
    
    /**
     * ロックオブジェクト (対象チャンネル)
     */
    private final Object _CHANNEL_LOCK = new Object();
    
    
    
    /**
     * BOT本体
     */
    private final PircBotX _core = new PircBotX();
    
    /**
     * 対象チャンネル
     */
    private Channel _channel = null;
    
}

