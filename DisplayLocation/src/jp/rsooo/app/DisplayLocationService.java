package jp.rsooo.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class DisplayLocationService extends Service {

	public DBManager dbManager;
	private Handler timerHandler = new Handler();
	private Toast toast;
	private boolean isCallingDisplay; 
	
	private String displayText;
	PhoneCallReceiver phoneCallReceiver = new PhoneCallReceiver();
	
	@Override
	public void onCreate(){
		super.onCreate();
		Log.i("debug", "service started");
		
        IntentFilter filter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        this.registerReceiver(this.phoneCallReceiver, filter);
        dbManager = new DBManager(this.getApplicationContext());
	}
	@Override
	public void onDestroy(){
		this.unregisterReceiver(this.phoneCallReceiver);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new DisplayLocationBinder();
	}

	public class DisplayLocationBinder extends Binder{
//		@Override
		public DisplayLocationService getService(){
			return DisplayLocationService.this;
		}
	}
	
    private class PhoneCallReceiver extends BroadcastReceiver{

    	
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.i("debug", "receiver");
			final String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
				final String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
						
				if(null == number){
					return; //非通知電話or公衆電話
				}
				
				if(number.charAt(0) != '0'){
					Log.i("debug", "top 0");		
					return; //先頭ゼロじゃなかったら分からん
				}
				if(number.charAt(2) == '0'){
					Log.i("debug", "0-0 like");		
					return; //これはケータイとか
				}
				String location = getLocationFromDB(number);
				Log.i("debug", number);		
				if(location != null){
					isCallingDisplay = true;
					displayText = "発信地:" + location;
//					toast = Toast.makeText(context, displayText, Toast.LENGTH_LONG);
//					toast.show();
					timerHandler.post(new TimerThread(context));
				
				}
			}else{
				if(toast != null){
					isCallingDisplay = false;
					toast.cancel();
				}
			}
			   
		}
		
		/**
		 * 電話番号から件名を予測
		 * @param number
		 * @return
		 */
		private String getLocationFromDB(final String number){
			String num4 = number.substring(1,5);
			String num3 = number.substring(1,4);
			String num2 = number.substring(1,3);
			String num1 = number.substring(1,2);
			
			String ret;
			if((ret = dbManager.query(num4)) == null){
				if((ret = dbManager.query(num3)) == null){
					if((ret = dbManager.query(num2)) == null){
						ret = dbManager.query(num1);							
					}
				}
			}
			return ret;
//			int num4 = Integer.parseInt(numBase);
//			int num3 = num4 / 10;
//			int num2 = num3 / 10;
//			int num1 = num2 / 10;
			
			
		}
    }
    
    class TimerThread extends Thread{
    	
    	Context context;
    	
    	TimerThread(Context c){
    		context = c;
    	}
    	
    	public void run(){
    		if(toast != null){
    			toast.cancel();
    		}
			
			if(isCallingDisplay){
				toast = Toast.makeText(context, displayText, Toast.LENGTH_SHORT);
				toast.show();
				timerHandler.postDelayed(this, 3000);
			}			
    	}
    }
    
    

}
