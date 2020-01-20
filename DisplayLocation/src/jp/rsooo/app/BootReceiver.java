package jp.rsooo.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


/**
 * �[���N���̃��b�Z�[�W���󂯎�邽�߂̃��V�[�o
 * @author akira
 *
 */
public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(loadData(context)){
			context.startService(new Intent(context, DisplayLocationService.class)); //�ݒ�Ƀ`�F�b�N�����Ă���΃T�[�r�X�N��
		}
	}   	
	
	private boolean loadData(Context c){
		SharedPreferences checkBootPref = c.getSharedPreferences("checkboot", Context.MODE_PRIVATE);
		return checkBootPref.getBoolean("check", false);
	}
}