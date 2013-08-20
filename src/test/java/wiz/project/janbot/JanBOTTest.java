/**
 * JanBOTTest.java
 * 
 * @author Yuki
 */

package wiz.project.janbot;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;


/**
 * JanBOTのテスト
 */
public final class JanBOTTest {
    
    /**
     * コンストラクタ
     */
    public JanBOTTest() {
    }
    
    
    
    /**
     * initialize() のテスト
     */
    @Test
    public void testInitialize() throws NickAlreadyInUseException, IrcException, IOException {
        {
            // 正常
            MockBOT.initialize();
            
            final String nickname = TEST_NICKNAME;
            final String serverHost = TEST_SERVER_HOST;
            final int serverPort = TEST_SERVER_PORT;
            final String channel = TEST_CHANNEL;
            JanBOT.getInstance().initialize(nickname, serverHost, serverPort, channel);
        }
        {
            // エラー (ニックネームが空文字列)
            MockBOT.initialize();
            
            final String nickname = "";
            final String serverHost = TEST_SERVER_HOST;
            final int serverPort = TEST_SERVER_PORT;
            final String channel = TEST_CHANNEL;
            try {
                JanBOT.getInstance().initialize(nickname, serverHost, serverPort, channel);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Nickname is empty.", e.getMessage());
            }
        }
        {
            // エラー (ホスト名が空文字列)
            MockBOT.initialize();
            
            final String nickname = TEST_NICKNAME;
            final String serverHost = "";
            final int serverPort = TEST_SERVER_PORT;
            final String channel = TEST_CHANNEL;
            try {
                JanBOT.getInstance().initialize(nickname, serverHost, serverPort, channel);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Server host name is empty.", e.getMessage());
            }
        }
        {
            // エラー (ポート番号が負の数)
            MockBOT.initialize();
            
            final String nickname = TEST_NICKNAME;
            final String serverHost = TEST_SERVER_HOST;
            final int serverPort = -1;
            final String channel = TEST_CHANNEL;
            try {
                JanBOT.getInstance().initialize(nickname, serverHost, serverPort, channel);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Invalid server port - " + serverPort, e.getMessage());
            }
        }
        {
            // エラー (チャンネル名が空文字列)
            MockBOT.initialize();
            
            final String nickname = TEST_NICKNAME;
            final String serverHost = TEST_SERVER_HOST;
            final int serverPort = TEST_SERVER_PORT;
            final String channel = "";
            try {
                JanBOT.getInstance().initialize(nickname, serverHost, serverPort, channel);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Channel name is empty.", e.getMessage());
            }
        }
        {
            // エラー (ニックネームがNull)
            MockBOT.initialize();
            
            final String nickname = null;
            final String serverHost = TEST_SERVER_HOST;
            final int serverPort = TEST_SERVER_PORT;
            final String channel = TEST_CHANNEL;
            try {
                JanBOT.getInstance().initialize(nickname, serverHost, serverPort, channel);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Nickname is null.", e.getMessage());
            }
        }
        {
            // エラー (ホスト名がNull)
            MockBOT.initialize();
            
            final String nickname = TEST_NICKNAME;
            final String serverHost = null;
            final int serverPort = TEST_SERVER_PORT;
            final String channel = TEST_CHANNEL;
            try {
                JanBOT.getInstance().initialize(nickname, serverHost, serverPort, channel);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Server host name is null.", e.getMessage());
            }
        }
        {
            // エラー (チャンネル名がNull)
            MockBOT.initialize();
            
            final String nickname = TEST_NICKNAME;
            final String serverHost = TEST_SERVER_HOST;
            final int serverPort = TEST_SERVER_PORT;
            final String channel = null;
            try {
                JanBOT.getInstance().initialize(nickname, serverHost, serverPort, channel);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Channel name is null.", e.getMessage());
            }
        }
    }
    
    /**
     * main() のテスト
     */
    @Test
    public void testMain() {
        {
            // 正常
            MockBOT.initialize();
            
            final String[] paramList = { TEST_SERVER_HOST, TEST_SERVER_PORT_STRING, TEST_CHANNEL };
            JanBOT.main(paramList);
        }
        {
            // エラー (実行引数の数が不足)
            MockBOT.initialize();
            
            final String[] paramList = { TEST_SERVER_HOST, TEST_SERVER_PORT_STRING };
            JanBOT.main(paramList);
        }
        {
            // エラー (実行引数がNull)
            MockBOT.initialize();
            
            final String[] paramList = null;
            try {
                JanBOT.main(paramList);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Parameter list is null.", e.getMessage());
            }
        }
    }
    
    /**
     * println() のテスト
     */
    @Test
    public void testPrintln() {
        {
            // 正常
            MockBOT.initialize();
            MockBOT.connect();
            
            final String message = TEST_MESSAGE;
            JanBOT.getInstance().println(message);
        }
        {
            // 正常 (複数行)
            MockBOT.initialize();
            MockBOT.connect();
            
            final List<String> messageList = TEST_MESSAGE_LIST;
            JanBOT.getInstance().println(messageList);
        }
        {
            // 正常 (空のメッセージリスト)
            MockBOT.initialize();
            MockBOT.connect();
            
            final List<String> message = new ArrayList<>();
            JanBOT.getInstance().println(message);
        }
        {
            // エラー (メッセージがNull)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String message = null;
            try {
                JanBOT.getInstance().println(message);
            }
            catch (final NullPointerException e) {
                assertEquals("Message is null.", e.getMessage());
            }
        }
        {
            // エラー (メッセージリストがNull)
            MockBOT.initialize();
            MockBOT.connect();
            
            final List<String> message = null;
            try {
                JanBOT.getInstance().println(message);
            }
            catch (final NullPointerException e) {
                assertEquals("Message list is null.", e.getMessage());
            }
        }
    }
    
    /**
     * talk() のテスト
     */
    @Test
    public void testTalk() {
        // TODO ネトマ未実装
    }
    
    
    
    /**
     * ニックネーム
     */
    private static final String TEST_NICKNAME = "TestBOT";
    
    /**
     * サーバ情報
     */
    private static final String TEST_SERVER_HOST = "test.server.com";
    private static final int    TEST_SERVER_PORT = 6667;
    private static final String TEST_SERVER_PORT_STRING = String.valueOf(TEST_SERVER_PORT);
    
    /**
     * チャンネル
     */
    private static final String TEST_CHANNEL = "#test-channel";
    
    /**
     * メッセージ
     */
    private static final String TEST_MESSAGE = "テストメッセージ";
    private static final List<String> TEST_MESSAGE_LIST =
        Collections.unmodifiableList(Arrays.asList(TEST_MESSAGE + "01", TEST_MESSAGE + "02", TEST_MESSAGE + "03"));
    
}

