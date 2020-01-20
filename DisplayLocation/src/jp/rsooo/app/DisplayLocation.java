package jp.rsooo.app;

import java.util.List;


import jp.rsooo.app.DisplayLocationService.DisplayLocationBinder;
import jp.rsooo.app.credit.CreditActivity;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class DisplayLocation extends Activity {
    
	DisplayLocationService displayLocationService;
	Button b;
	Button dropButton;
	Button endButton;
	Context context;
	Intent serviceIntent;
	TextView textView;
	CheckBox bootCheck;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this.getApplicationContext();
        serviceIntent = new Intent(context,
    			DisplayLocationService.class);
        b = (Button)this.findViewById(R.id.start);
        dropButton = (Button)this.findViewById(R.id.drop);
        endButton = (Button)this.findViewById(R.id.end);
        textView = (TextView)this.findViewById(R.id.state);
        bootCheck = (CheckBox)this.findViewById(R.id.bootcheck);
        //チェックボックスのデータ読む
        loadData();
        
        b.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				startService(serviceIntent);
				updateState();
				// バインド
//				Log.i("TAG", "bind service");
//				bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
			}
		});
        dropButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(displayLocationService != null){
					if(displayLocationService.dbManager != null){
						displayLocationService.dbManager.dropTable();
					}
				}
			}
        	
        });
        
        endButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
				List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Integer.MAX_VALUE);
				
				for(RunningServiceInfo info : services){
					ComponentName name = info.service;
					if(name.toShortString().contains("jp.rsooo.app.DisplayLocationService")){
						Log.i("TAG", "service stopped");
						stopService(serviceIntent);
						updateState();
					}
				}
			}
        	
        });
        
        bootCheck.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//変更を保存
				saveData();
			}
        	
        });
    }
    
    /**
     * XMLファイルからメニュー読み込み
     */
    
    @Override
    public boolean  onCreateOptionsMenu(Menu menu){
    	MenuInflater inflater = this.getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    /**
     * オプションメニュー押された時の挙動
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    	case R.id.credit:
    		Intent intent = new Intent(this, CreditActivity.class);
    		startActivity(intent);
    	}
    	
    	return false;
    }

    
    @Override
    public void onStart(){
    	super.onStart();
    	this.updateState();
    }
    @Override
    public void onDestroy(){
    	super.onDestroy();
 /*   	if(this.displayLocationService != null){
    		this.displayLocationService.stopSelf();
    		this.displayLocationService = null;
    	}
    	*/
//    	this.stopService(serviceIntent);
    }

    /**
     * チェックボックスのデータ読み込む
     */
	private void loadData(){
		SharedPreferences checkBootPref = context.getSharedPreferences("checkboot", Context.MODE_PRIVATE);
		boolean check = checkBootPref.getBoolean("check", false);
		this.bootCheck.setChecked(check);
	}
	
	private void saveData(){
		SharedPreferences checkBootPref = context.getSharedPreferences("checkboot", Context.MODE_PRIVATE);
		Editor editor = checkBootPref.edit();
		editor.putBoolean("check", this.bootCheck.isChecked());
		editor.commit();
	}

	
    
    /**
     * サービス起動してるかどうかを判断する
     * @return
     */
    public boolean checkState(){
		ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Integer.MAX_VALUE);
		
		for(RunningServiceInfo info : services){
			ComponentName name = info.service;
			Log.i("TAG", "service:" + name);
			Log.i("TAG", "service:" + name.toShortString());
			if(name.toShortString().contains("jp.rsooo.app.DisplayLocationService")){
				return true;
			}
		}
		return false;
	}
	    
    public void updateState(){
    	if(checkState()){
    		textView.setText("現在の状態:ON");    		
    	}else{
    		textView.setText("現在の状態:OFF");
    	}
    }
    private ServiceConnection serviceConnection = new ServiceConnection(){
    	
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.i("TAG", "service connected");
			displayLocationService = ((DisplayLocationBinder)service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.i("TAG", "service disconnected");
			displayLocationService = null;
		}
    };
   
}