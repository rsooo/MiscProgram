package jp.stressfreesoft.app.chat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * �e�핁�ՂȐݒ���e�Ȃ�
 * @author akira
 *  
 */
public class GlobalData { 
	public static final String PREFERENCE_TAG = "Daifugou";
	public static final boolean D = true; //true�Ȃ�f�o�b�O���[�h
	public static final boolean HONBAN = false; //true�Ȃ�{�Ԋ��ɃA�N�Z�X
	public static final ExecutorService executerService = Executors.newFixedThreadPool(1); //���L����X���b�h�v�[��
	 
	public static final String LOGINPHP;// = "http://stress-free.sakura.ne.jp/login2.php";
	public static final String CHATROOMPHP;// =  "http://stress-free.sakura.ne.jp/chatroom2.php";
	public static final String SELECTLISTPHP;// = "http://stress-free.sakura.ne.jp/selectlist.php";
	
	public static final String SESSION_ERROR_CODE = "ERROR";
	//public static final String SESSION_ERROR_MESSAGE = "�����ԑ��삪�Ȃ����߃Z�b�V�����������ɂȂ�܂����B������x���O�C�����Ă��������B";
	
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
	
	
	//���O�C�����Ƀ��[�U�l�[�����i�[�B���ׂẴA�N�e�B�r�e�B����Q�Ɖ\�Ƃ���
	//���O�C�����ɂ����ύX���Ȃ����̂Ƃ���
	public static String userName = "";
} 
 