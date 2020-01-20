package jp.stressfreesoft.app.chat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 各種普遍な設定内容など
 * @author akira
 *  
 */
public class GlobalData { 
	public static final String PREFERENCE_TAG = "Daifugou";
	public static final boolean D = true; //trueならデバッグモード
	public static final boolean HONBAN = false; //trueなら本番環境にアクセス
	public static final ExecutorService executerService = Executors.newFixedThreadPool(1); //共有するスレッドプール
	 
	public static final String LOGINPHP;// = "http://stress-free.sakura.ne.jp/login2.php";
	public static final String CHATROOMPHP;// =  "http://stress-free.sakura.ne.jp/chatroom2.php";
	public static final String SELECTLISTPHP;// = "http://stress-free.sakura.ne.jp/selectlist.php";
	
	public static final String SESSION_ERROR_CODE = "ERROR";
	//public static final String SESSION_ERROR_MESSAGE = "長時間操作がないためセッションが無効になりました。もう一度ログインしてください。";
	
	static{
		if(HONBAN){
			LOGINPHP = "http://stress-free.sakura.ne.jp/honban/login2.php";
			CHATROOMPHP = "http://stress-free.sakura.ne.jp/honban/chatroom2.php";
			SELECTLISTPHP = "http://stress-free.sakura.ne.jp/honban/selectlist.php";
		}else{
			LOGINPHP = "http://stress-free.sakura.ne.jp/login2.php";
			CHATROOMPHP = "http://stress-free.sakura.ne.jp/chatroom2.php";
			SELECTLISTPHP = "http://stress-free.sakura.ne.jp/selectlist.php";
		}
	}
	
	
	//ログイン時にユーザネームを格納。すべてのアクティビティから参照可能とする
	//ログイン時にしか変更しないものとする
	public static String userName = "";
} 
 