package jp.rsooo.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


/**
 * 端末起動のメッセージを受け取るためのレシーバ
 * @author akira
 *
 */
public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(loadData(context)){
			context.startService(new Intent(context, DisplayLocationService.class)); //設定にチェック入っていればサービス起動
		}
	}   	
	
	private boolean loadData(Context c){
		SharedPreferences checkBootPref = c.getSharedPreferences("checkboot", Context.MODE_PRIVATE);
		return checkBootPref.getBoolean("check", false);
	}
}