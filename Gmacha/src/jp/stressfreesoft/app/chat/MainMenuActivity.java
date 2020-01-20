package jp.stressfreesoft.app.chat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import jp.rsooo.app.lib.alert.AlertUtil;
import jp.stressfreesoft.app.chat.ShowMapActivity.InviteAcceptBroadCastReceiver;
import jp.stressfreesoft.app.chat.client.GlobalHttpClient;
import jp.stressfreesoft.app.chat.client.RequestSender;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainMenuActivity extends Activity {
	public static final String ENTERCHATROOM = GlobalData.CHATROOMPHP; //"http://stress-free.sakura.ne.jp/chatroom2.php"; 
	public static final int RESULT_KEEPLOGIN = 100; 
//	private String userID;
	private String responseText;
	
	Context context;
	 
//	TextView textDebug;
	TextView textWelcome;
	Button enterButton; 
	Button showmapButton;
	Button settingButton;
//	Button testButton;
	
//	CheckBox inviteCheckBox;
//	CheckBox publishLocationCheckBox;
	
	private GeoLocation geoLocation;
	String nickname;
	boolean isGuestLogin = false; //ゲストログインならtrue
	
	public static final int REQUEST_SHOWMAP = 0;
	public static final int REQUEST_INVITENEARUSER = 1;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmenulayout);
		context = this;
		
//		Intent thisIntent = this.getIntent();
//		userID = thisIntent.getStringExtra("ID");
//		AlertUtil.showToast("userID" + userID , this);
	
//		textDebug = (TextView)this.findViewById(R.id.text_debug2);
		textWelcome = (TextView)this.findViewById(R.id.text_welcome);
		enterButton = (Button)this.findViewById(R.id.btn_startchat);
		showmapButton = (Button)this.findViewById(R.id.btn_selectmap);
		settingButton = (Button)this.findViewById(R.id.btn_config);
//		inviteCheckBox = (CheckBox)this.findViewById(R.id.chechbox_invite);
//		publishLocationCheckBox = (CheckBox)this.findViewById(R.id.chechbox_publishlocation);
		
		commonQuery = new CommonQuery(context);

		
//		testButton = (Button)this.findViewById(R.id.testbutton);
//		testButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				RequestSender sender = new RequestSender(ENTERCHATROOM);
//				sender.addVeluePair("ACTIONID", "101");
//				sender.executeQuery();
//				Intent intent = new Intent(context, ChatWindowActivity.class);
//				startActivity(intent);
//			}
//		});
	
//		inviteCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//					
//					if(isChecked){
//						//INVITE許可
//						RequestSender sender = new RequestSender(ENTERCHATROOM); 
//						sender.addVeluePair("ACTIONID", "7");
//						FutureTask<String> future = sender.executeQueryAsCallable();
//						try {
//							AlertUtil.showToast(future.get(), context);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						} catch (ExecutionException e) {
//							e.printStackTrace();
//						}
//					}else{
//						//INVITE拒否
//						RequestSender sender = new RequestSender(ENTERCHATROOM);
//						sender.addVeluePair("ACTIONID", "6");
//						sender.executeQueryAsThread();
//						FutureTask<String> future = sender.executeQueryAsCallable();
//						try {
//							AlertUtil.showToast(future.get(), context);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						} catch (ExecutionException e) {
//							e.printStackTrace();
//						}
//					}
//			}
//			
//		});
//		
//		publishLocationCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//				if(isChecked){
//					//位置情報公開許可
//					RequestSender sender = new RequestSender(ENTERCHATROOM); 
//					sender.addVeluePair("ACTIONID", "12");
//					FutureTask<String> future = sender.executeQueryAsCallable();
//					try {
//						AlertUtil.showToast(future.get(), context);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					} catch (ExecutionException e) {
//						e.printStackTrace();
//					}
//				}else{
//					//位置情報公開拒否
//					RequestSender sender = new RequestSender(ENTERCHATROOM);
//					sender.addVeluePair("ACTIONID", "11");
//					sender.executeQueryAsThread();
//					FutureTask<String> future = sender.executeQueryAsCallable();
//					try {
//						AlertUtil.showToast(future.get(), context);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					} catch (ExecutionException e) {
//						e.printStackTrace();
//					}
//				}
//
//			}
//		});
		
		
		enterButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
//				doPost();
				Intent intent = new Intent(context, InviteNearUserActivity.class);
				startActivityForResult(intent, REQUEST_INVITENEARUSER);
			} 
		});
		showmapButton.setOnClickListener(new OnClickListener(){
 
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ShowMapActivity.class);
				intent.putExtra("GeoLocation", geoLocation);
				startActivityForResult(intent, REQUEST_SHOWMAP);
			}
		});
		
		settingButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(isGuestLogin){
					AlertUtil.showAlert("ゲストログイン時には使用できません", context, "設定");
				}else{
					Intent intent = new Intent(context, SettingActivity.class);
					startActivity(intent);
				}
			}
			
		});
		
//		assert userID != null : "userId is null!";
		Intent serviceIntent = new Intent(MainMenuActivity.this, PollingService.class);
//		serviceIntent.putExtra("ownId", userID);
//		startService(new Intent(MainMenuActivity.this, PollingService.class));				
		startService(serviceIntent);
		
		//BroadcastReceiverを登録
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(PollingService.INTENT_ACCEPTED);
		this.registerReceiver(broadcastReceiver, intentFilter);
		
		//サービス起動後に速攻でバインドする
		pollingBind();
	}
	
	@Override
	public void onResume(){
		super.onResume(); 
		geoLocation = (GeoLocation)this.getIntent().getSerializableExtra("GeoLocation");
		this.nickname = this.getIntent().getStringExtra("NickName");
		this.isGuestLogin = this.getIntent().getBooleanExtra("GuestFlag", false);
//		this.getIntent().getB
//		AlertUtil.showToast(nickname, context);
		textWelcome.setText("ようこそ " + this.nickname + " さん");
//		AlertUtil.showToast(String.valueOf(geoLocation.latitude), context);
		assert geoLocation != null : "GeoLocation is null!!";
//		AlertUtil.showToast(String.valueOf(geoLocation.latitude), this);
	
	}
	
	@Override
	public void onDestroy(){
//		postLogout(); 
        if(pollingService != null){
        	this.unbindService(serviceConnection);
        	if(GlobalData.D){Log.d("MainMenuActivity","destroy:unbind service");}
        }
        this.unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(requestCode){
		case REQUEST_SHOWMAP:
			if(resultCode == ShowMapActivity.RESULT_ACCEPTED){
				Intent intent = new Intent(this, ChatWindowActivity.class);
				startActivity(intent);
			}
			
			break;
		case REQUEST_INVITENEARUSER:
			if(resultCode == InviteNearUserActivity.RESULT_OK){
				final int selectedRange = data.getIntExtra("SELECTEDRANGE", 10);
				final String selectedSex = data.getStringExtra("SELECTEDSEX");
				
				doPost(selectedRange, selectedSex);
			}
			break;
		}
		
		
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				new AlertDialog.Builder(context).setTitle("終了").setMessage("ログイン状態を保持しますか？NOを選択するとこれ以降チャットに招待されません。")
				.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						setResult(MainMenuActivity.RESULT_KEEPLOGIN);
						finish();
					}
				}).setNeutralButton("No", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		                //バインド、バインドしてそっこうサービス終わらすだけ
		            	//こんなことしなくても良い？？
		            	if(pollingService == null){
		            		AlertUtil.showToast("サービスバインド失敗", context);
//		            		pollingBind();
//		            		pollingService. 
		            	}else{
		            		pollingService.stopThisService();
		            	}
		            	postLogout();
		            	finish();
		            }
		        }).show();/*.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override 
					public void onClick(DialogInterface dialog, int which) {
						//do nothing
					}
				}).*/
				return false;
			}
		}
		return super.dispatchKeyEvent(event);		
	} 

	
	private void postLogout(){
		/*
		HttpPost method = new HttpPost(ENTERCHATROOM);
		DefaultHttpClient client = GlobalHttpClient.getInstance();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("ACTIONID", String.valueOf(5)));
		try {
			method.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(method);
			String responseText = EntityUtils.toString(response.getEntity(), "UTF-8");
			AlertUtil.showToast(responseText, context, Toast.LENGTH_LONG);
		} catch (UnsupportedEncodingException e) {
			Log.e("MainMenuActivity", "exception:"  + e.toString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		RequestSender sender = new RequestSender(ENTERCHATROOM);
		sender.addVeluePair("ACTIONID", "5");
		sender.executeQueryAsThread();
		AlertUtil.showToast("ログアウトしました。", context);	
		   
	}
	
	/** 
	 * チャット入室のためのクエリ送信
	 * @return
	 */
	public void doPost(final int selectedRange, final String selectedSex) {

//			RequestSender s = new RequestSender(ENTERCHATROOM);
//			s.addVeluePair("ACTIONID", "100");
//			s.addVeluePair("test", "テスト");
//			AlertUtil.showToast(s.executeQuery(), context);

		RequestSender sender = new RequestSender(ENTERCHATROOM);
		sender.addVeluePair("ACTIONID", "32"); //近くの人を招待
		sender.addVeluePair("SELECTEDSEX", selectedSex);
		sender.addVeluePair("SELECTEDRANGE", String.valueOf(selectedRange));
		sender.addVeluePair("LATITUDE", String.valueOf(geoLocation.latitude));
		sender.addVeluePair("LONGITUDE", String.valueOf(geoLocation.longitude));
//		sender.exe
		sender.executeQueryAsAsyncTask(context, new QueryResponseReceiver(){
			
			@Override
			public void analyzeResponse(String response) {
				// TODO Auto-generated method stub
				if(response.equals(GlobalData.SESSION_ERROR_CODE)){
					AlertUtil.showToast(getString(R.string.session_error_mes), context);
				} else {
					String[] datam = response.split("#"); // ID#nameで格納を想定
					if (datam[0].equals("-1")) {
						AlertUtil.showToast("近くに招待可能なユーザはいないようです。", context);
					} else {
						if (GlobalData.D) {
							AlertUtil.showToast(response, context);
						}
						inviteUser(Integer.valueOf(datam[0]), datam[1]);
					}
				}
			}
		}, "検索中・・・");
//		try {
////			String ret = future.get();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		/*
			new AlertDialog.Builder(context).setTitle("チャットに招待").setMessage("近くにいる人を招待します")
			.setPositiveButton("YES", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//yesの処理
					
					
				}
			}).setNegativeButton("NO", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//Noの処理。何もしない
				}
			}).show(); 

//			Log.i("debug", "post start");
			*/
	}
	
	private CommonQuery commonQuery; 

	
	/** ユーザを招待するか尋ねてYESなら招待
	 *  MAPVIEWにあった処理からコピペ
	 */

	private void inviteUser(final int invitingId, final String invitingUserName) {
//		final String id = items.get(index).getSnippet();
//		int invitingId = items.get(index).getSnippet();
//		final String username = items.get(index).getTitle();
		new AlertDialog.Builder(context).setTitle("チャットに招待").setMessage(invitingUserName + "を招待します")
		.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			//	Toast.makeText(context, invitingId, Toast.LENGTH_SHORT).show();
				commonQuery.showWait(String.valueOf(invitingId));
				if(pollingService == null){
//					pollingBind();
					AlertUtil.showAlert("バインド失敗しましたcode:01", context);
				}else{
				 	executeInvite(String.valueOf(invitingId));
				}
			}
		}).setNegativeButton("NO", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
		//return true;
	}

	private void executeInvite(String invitingId){
		assert invitingId != null : "InvitingId is null!";
//		pollingService.setState(POLLING_STATE.INVITING);
		pollingService.changeInviteState(invitingId);
		RequestSender sender = new RequestSender(ENTERCHATROOM);
		sender.addVeluePair("ACTIONID", "30");
		sender.addVeluePair("INVITINGID", invitingId);
		sender.executeQueryAsThread();
	}

	
	//-----BIND関連
	private void pollingBind(){
		Intent intent = new Intent(this, PollingService.class);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	private PollingService pollingService = null;
	private ServiceConnection serviceConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName className, IBinder service){
			pollingService = ((PollingService.PollingBinder)service).getService();
//			pollingService.stopThisService();
		}
		@Override
		public void onServiceDisconnected(ComponentName className){
			pollingService = null;
		}
	};
	
	
	private void analyzeResponseText(String rText) {
		//がんばる。とりあえず無条件で遷移
		Intent intent = new Intent(this, ChatWindowActivity.class);
//		intent.putExtra("ID", userID);
		intent.putExtra("Data", rText);
		startActivity(intent);

		
	}
	
	InviteAcceptBroadCastReceiver broadcastReceiver = new InviteAcceptBroadCastReceiver();
	//-----BroadCastReceiver関連
	public class InviteAcceptBroadCastReceiver extends BroadcastReceiver{
		 
		@Override
		public void onReceive(Context context, Intent intent) {
			AlertUtil.showToast("Intent_ACCEPTED", context);
			assert commonQuery != null : "CommonQUery is null!!";
			commonQuery.dismiss();
			//Intent chatintent = new Intent(context, ChatWindowActivity.class);
			//startActivity(chatintent);
		}
	} 


}
