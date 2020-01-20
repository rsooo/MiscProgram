package jp.stressfreesoft.app.chat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import jp.rsooo.app.lib.alert.AlertUtil;
import jp.stressfreesoft.app.chat.client.GlobalHttpClient;
import jp.stressfreesoft.app.chat.client.RequestSender;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

 
  
public class LoginActivity extends Activity implements QueryResponseReceiver{
	public static final String POSTURL = GlobalData.LOGINPHP; //"http://stress-free.sakura.ne.jp/login2.php";
	public static final int START_SIGNUP_ACTIVITY = 1;
	public static final int START_MAINMENU_ACTIVITY = 2;
	public static final int START_GUESTLOGIN_ACTIVITY = 3;
	
	
	EditText editName;
	EditText editPass;
	Button loginButton;
	Button signupButton;
	Button getLocationButton;
//	TextView textDebug;
//	TextView locationFlagText;
	Context context;
	CheckBox memorizeIdPassChkBox;
	
	Button debugbtn;
	Button guestLoginButton;
	GeoLocation geoLocation = new GeoLocation(35422006,139454095,0,0); //デバッグモードのときはこの場所
	private TextView getlocationText;
	boolean isGetGeoLocation = false; 
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	private Timer locationTimer;
	private long time;
	
	//サーバからの応答
	String responseText;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginlayout);
		context = this;
		 
		editName = (EditText)this.findViewById(R.id.edit_name);
		editPass = (EditText)this.findViewById(R.id.edit_pass);
		loginButton = (Button)this.findViewById(R.id.btn_login);
		signupButton = (Button)this.findViewById(R.id.btn_signup);
		getLocationButton = (Button)this.findViewById(R.id.btn_getlocation);
//		textDebug = (TextView)this.findViewById(R.id.text_debug);
		getlocationText = (TextView)this.findViewById(R.id.locationflag);
//		editName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		memorizeIdPassChkBox = (CheckBox)this.findViewById(R.id.chkbox_memorizeidpass);
		guestLoginButton = (Button)this.findViewById(R.id.btn_guestlogin);
		
//		getlocationText.set
//		Typeface huiji = Typeface.createFromAsset(getAssets(),	"huiji.ttf");
//		loginButton.setTypeface(huiji);
//		signupButton.setTypeface(huiji);
//		getLocationButton.setTypeface(huiji);
//		TextView t = (TextView)this.findViewById(R.id.testtest);
//		t.setTypeface(huiji);
		  
		//各種設定の読み込み
		editName.setText(this.loadNameData());
		memorizeIdPassChkBox.setChecked(this.loadMemorizePass());
		if(memorizeIdPassChkBox.isChecked()){
			editPass.setText(loadPassword());
		} 
		if(GlobalData.D){
			//デバッグ時はログイン可能
			isGetGeoLocation = true;
		}
		if(GlobalData.D){AlertUtil.showToast("デバッグモードです。", context);}
		if(!GlobalData.HONBAN){AlertUtil.showToast("テスト環境に接続しています", context);}
		
		loginButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String name = editName.getText().toString();
				String pass = editPass.getText().toString();
				if(name.equals("")){
					AlertUtil.showToast("ユーザ名を入力してください。初めての方は新規登録ボタンを押してください。", context);
				}else if(pass.equals("") && !name.equals("guest")){
					AlertUtil.showToast("パスワードを入力してください。", context);
				}else if(!name.matches("[a-zA-Z0-9_]+")){
					AlertUtil.showToast("ユーザ名に使用できない文字が含まれています。", context);					
				}else{				
					if(isGetGeoLocation){
						doPost();
					}else{
						AlertUtil.showToast("位置情報が取得できていません", LoginActivity.this);
					}
				}
			}
			
		});
		
		guestLoginButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(isGetGeoLocation){
					editName.setText("guest");
					doPost();
				}else{
					AlertUtil.showToast("位置情報が取得できていません", LoginActivity.this);
				}
			}
			
		});
		
		signupButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(context, SignUpActivity.class);
				Intent intent = new Intent(context, SignUpSendMailActivity.class);
				startActivityForResult(intent, START_SIGNUP_ACTIVITY);
			}
			
		});
		
		getLocationButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startLocationService(false);
			}
			
		});
		
			
		//サービスが起動しているか確認
		//ここに書いてそのままStartActivityしても大丈夫？？
		if(checkPollingService()){
			RequestSender sender = new RequestSender(POSTURL);
			sender.addVeluePair("ACTIONID", "20");
			RequestSender getNickNamesender = new RequestSender(POSTURL);
			getNickNamesender.addVeluePair("ACTIONID", "21");
			FutureTask<String> future = sender.executeQueryAsCallable();
			FutureTask<String> future2 = getNickNamesender.executeQueryAsCallable();
			try {
				final String ret = future.get();
				final String ret2 = future2.get();
//				AlertUtil.showToast(future.get(), context); 
				if(ret.equals("-1") || ret2.equals("-1")){
					//セッション切れてるので何もしない
					Log.i("LoginActivity", "session unavailable");
				}else{
					String[] datam = ret.split("#"); //latitude#longitudeの形式で帰ってくる
					this.geoLocation.latitude = Integer.parseInt(datam[0]);
					this.geoLocation.longitude = Integer.parseInt(datam[1]);
				  
					Intent intent = new Intent(this, MainMenuActivity.class);
					intent.putExtra("GeoLocation", this.geoLocation);
					intent.putExtra("NickName", ret2);
					startActivityForResult(intent, START_MAINMENU_ACTIVITY);
					
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		memorizeIdPassChkBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				saveMemorizePass(isChecked);
			}
			
		});

	}

	/**
	 * 位置情報取得メソッド
	 * @param useLastLocationInfo 直近に取得した位置情報を使用するならtrue
	 */
    private void startLocationService(boolean useLastLocationInfo) {
    	stopLocationService();
    	mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
    	//位置情報機能なし端末の場合
    	if(mLocationManager == null){
    		AlertUtil.showToast("この端末では位置情報機能が使用できません", context);
    		return;
    	}
    	final Criteria criteria = new Criteria();
    	criteria.setBearingRequired(false);//方位不要
    	criteria.setSpeedRequired(false);//速度不要
    	final String provider = mLocationManager.getBestProvider(criteria, true);
    	if(provider == null){
			// 位置情報が有効になっていない場合は、Google Maps アプリライクな [現在地機能を改善] ダイアログを起動します。
			new AlertDialog.Builder(this)
				.setTitle("現在地機能を改善")
				.setMessage("現在、位置情報は一部有効ではないものがあります。次のように設定すると、もっともすばやく正確に現在地を検出できるようになります:\n\n● 位置情報の設定でGPSとワイヤレスネットワークをオンにする\n\n● Wi-Fiをオンにする")
				.setPositiveButton("設定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						// 端末の位置情報設定画面へ遷移
						try {
							startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
						} catch (final ActivityNotFoundException e) {
							// 位置情報設定画面がない糞端末の場合は、仕方ないので何もしない
						}
					}
				})
				.setNegativeButton("スキップ", new DialogInterface.OnClickListener() {
					@Override public void onClick(final DialogInterface dialog, final int which) {}	// 何も行わない
				}) 
				.create()
				.show();
				stopLocationService();
				return; 
    	}
    	// 最後に取得できた位置情報が5分以内のものであれば有効とします。
    	final Location lastKnownLocation = mLocationManager.getLastKnownLocation(provider);
//    	long diff = new Date().getTime() - lastKnownLocation.getTime();
//    	AlertUtil.showToast("getLOCATION",context);
		if(lastKnownLocation != null && (new Date().getTime() - lastKnownLocation.getTime() <= (5 * 60 * 1000L))){
    		//直近の位置情報を使用しないなら何もしない
//    		AlertUtil.showToast("lastKnownLocation:ON",context);
    		if(useLastLocationInfo){
//        		AlertUtil.showToast("setLastLocation:ON",context);
    			setLocation(lastKnownLocation);
    			return;
    		}
    	}
    	locationTimer = new Timer(true);
    	time = 0L;
    	final Handler handler = new Handler();
    	locationTimer.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				handler.post(new Runnable(){
					
					public void run(){
						if(time == 1000L){
							AlertUtil.showToast("位置情報を取得しています・・・", context);
						}else if(time >= (30 * 1000L)){
							AlertUtil.showToast("位置情報を取得できませんでした。後でやり直して下さい。", context, Toast.LENGTH_LONG);
							stopLocationService();
						}
						time = time + 1000L;
					} 
				});
			}
    		
    	}, 0, 1000L);
    	
		// 位置情報の取得を開始します。
		mLocationListener = new LocationListener() {
			@Override
			public void onLocationChanged(final Location location) {
//				AlertUtil.showToast("位置情報ゲット",context);
				Log.i("LoginActivity", "get location information");
				getlocationText.setText("OK");
				setLocation(location);
			}
			@Override public void onProviderDisabled(final String provider) {}
			@Override public void onProviderEnabled(final String provider) {}
			@Override public void onStatusChanged(final String provider, final int status, final Bundle extras) {}
		};
		
		//既に位置情報が取得できた場合LocationManagerは使用されない
		if(mLocationManager != null){
//			AlertUtil.showToast("requestLocationUpdates:ON",context);
			mLocationManager.requestLocationUpdates(provider, 60000, 0, mLocationListener);
		}
	}

    private void setLocation(Location location) {
    	stopLocationService();
    	
    	geoLocation.latitude = (int)(location.getLatitude() * 1E6);
//    	AlertUtil.showToast("latitude" + geoLocation.latitude ,context);
		geoLocation.longitude = (int)(location.getLongitude() * 1E6);
//		AlertUtil.showToast("longitude" + geoLocation.longitude ,context);
		geoLocation.altitude = (int)(location.getAltitude() * 1E6);
    	geoLocation.accuracy = (int)(location.getAccuracy() * 1E6);
    	this.isGetGeoLocation = true;
//    	getlocationText.setText("位置情報取得:OK");

	} 

	private void stopLocationService(){
    	if(locationTimer != null){
    		locationTimer.cancel();
    		locationTimer.purge();
    		locationTimer = null;
    	}
    	if(mLocationManager != null){
    		if(mLocationListener != null){
    			mLocationManager.removeUpdates(mLocationListener);
    			mLocationListener = null;
    		}
    		mLocationManager = null;
    	}
    }
    
	@Override
	public void onStart(){
		startLocationService(true);
		super.onStart();
	}
	
	@Override
	public void onDestroy(){
		this.stopLocationService();
		super.onDestroy();
	}
	
	@Override
	    public void onResume(){
//	        if (mLocationManager != null) {
//	            mLocationManager.requestLocationUpdates(
//	                LocationManager.GPS_PROVIDER, 
////	                LocationManager.NETWORK_PROVIDER,
//	                0,
//	                0,
//	                this);
//	        }        
	        super.onResume();
	    }
	    
	    @Override
	    protected void onPause() {
//	        if (mLocationManager != null) {
//	            mLocationManager.removeUpdates(this);
//	        }
	        
	        super.onPause();
	    }
	    
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data){
			switch(requestCode){
			case START_SIGNUP_ACTIVITY:
				if(resultCode == SignUpActivity.RESULT_OK){
//					AlertUtil.showToast(data.getStringExtra("NAME"),context);
					editName.setText(data.getStringExtra("NAME"));
//					editPass.setText(data.getStringExtra("PASS"));
				}
				break;
			case START_MAINMENU_ACTIVITY:
				//ログイン状態を保持しているときはログイン画面を表示させない
				if(resultCode == MainMenuActivity.RESULT_KEEPLOGIN){
					finish();
				}
				break;
			case START_GUESTLOGIN_ACTIVITY:
				if(resultCode == GuestLoginActivity.RESULT_OK){
						AlertUtil.showToast("ログインしました", this);
						final String nickname = data.getStringExtra("NickName");
//						String nickname = datam[1];
//						final String userName = editName.getText().toString(); 
//						this.saveNameData(userName);
						Intent intent = new Intent(this, MainMenuActivity.class);
						intent.putExtra("GeoLocation", this.geoLocation);
						intent.putExtra("NickName", nickname);
						intent.putExtra("GuestFlag", true); //ゲストログインであることを示す
//						intent.putExtra("CLIENT", client);
						
						//ログイン名を保存
						GlobalData.userName = "guest";
						startActivityForResult(intent, START_MAINMENU_ACTIVITY); 
				}
				break;
			}
			
		}
	    
	/**
	 * サービス起動してるかどうかを判断する
	 * PollingServiceが起動しているならtrue
	 * @return
	 */
	public boolean checkPollingService() {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> services = am
				.getRunningServices(Integer.MAX_VALUE);

//		AlertUtil.showToast("checkService", context);
		for (RunningServiceInfo info : services) {
			ComponentName name = info.service;
			Log.i("TAG", "service:" + name);
			Log.i("TAG", "service:" + name.toShortString());
			if (name.toShortString().contains(
					"jp.stressfreesoft.app.chat.PollingService")) {
//				AlertUtil.showToast("ret:true", context);
				return true;
			}
		}
//		AlertUtil.showToast("ret:false", context);

		return false;
	}
		
	/**
	 * ログインデータポスト
	 * 
	 * @return
	 */
	public String doPost() {
		// Log.i("debug", "post start");

		// HttpPost method = new HttpPost(POSTURL);
		//
		// DefaultHttpClient client = GlobalHttpClient.getInstance();
		//
		// List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		// nameValuePairs.add(new BasicNameValuePair("ACTIONID", "0"));
		//			
		String name = editName.getText().toString();
		String pass = editPass.getText().toString();
		//
		// sinki tuika
		if (name.equals("guest")) {
			Intent guestIntent = new Intent(context, GuestLoginActivity.class);
			guestIntent.putExtra("GeoLocation", this.geoLocation);
			startActivityForResult(guestIntent, START_GUESTLOGIN_ACTIVITY);
		} else {

			try {
				// nameValuePairs.add(new BasicNameValuePair("NAME", name));
				// nameValuePairs.add(new BasicNameValuePair("PASS", pass));
				//			
				// //位置情報送る
				// nameValuePairs.add(new BasicNameValuePair("LATITUDE",
				// String.valueOf(geoLocation.latitude)));
				// nameValuePairs.add(new BasicNameValuePair("LONGITUDE",
				// String.valueOf(geoLocation.longitude)));
				// nameValuePairs.add(new BasicNameValuePair("ALTITUDE",
				// String.valueOf(geoLocation.altitude)));
				// nameValuePairs.add(new BasicNameValuePair("ACCURACY",
				// String.valueOf(geoLocation.accuracy)));
				// nameValuePairs.add(new BasicNameValuePair("STATUS", "WAIT"));
				//			
				//			
				// method.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				//
				//			
				// HttpResponse response = client.execute(method);
				//			
				//			
				// int status = response.getStatusLine().getStatusCode();
				// if (status != HttpStatus.SC_OK){
				// Log.w("LoginActivity", "Status is not OK:" +
				// HttpStatus.SC_OK);
				// }
				// throw new Exception("");

				RequestSender loginSender = new RequestSender(POSTURL);
				loginSender.addVeluePair("ACTIONID", "0");
				loginSender.addVeluePair("NAME", name);
				loginSender.addVeluePair("PASS", pass);
				loginSender.addVeluePair("LATITUDE", String
						.valueOf(geoLocation.latitude));
				loginSender.addVeluePair("LONGITUDE", String
						.valueOf(geoLocation.longitude));
				loginSender.addVeluePair("ALTITUDE", String
						.valueOf(geoLocation.altitude));
				loginSender.addVeluePair("ACCURACY", String
						.valueOf(geoLocation.accuracy));
				loginSender.addVeluePair("STATUS", "WAIT");
				// responseText = loginSender.executeQuery();

				// ダイアログ使いまわすととまるので毎回生成
				// final ProgressDialog waitDialog = new
				// ProgressDialog(context){
				// @Override
				// public void onBackPressed(){
				// super.onBackPressed();
				// // cancelInvite(invitingId);
				// // future.cancel(true);
				// // AlertUtil.showToast("back", context);
				// }
				// };
				// Handler h = new Handler();
				// h.post(new Runnable(){
				//
				// @Override
				// public void run() {
				// waitDialog.setMessage("ログイン中・・・");
				// waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				// waitDialog.show();
				// }
				// });
				// final Future<String> future =
				// loginSender.executeQueryAsCallable();
				// final Future<String> future =
				// loginSender.executeQueryAsCallableTest(context, this);
				loginSender.executeQueryAsAsyncTask(context, this, "ログイン中...");
				// responseText = future.get();
				// waitDialog.setMessage("waiting");
				// waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				// waitDialog.show();

				// responseText = future.get();
				// waitDialog.dismiss();

				// responseText = EntityUtils.toString(response.getEntity(),
				// "UTF-8");

				// // textDebug.setText(responseText);
				// textview.refreshDrawableState();
				// Log.i("debug", EntityUtils
				// .toString(response.getEntity(), "UTF-8"));
				// analyzeResponseText(responseText);

			} catch (Exception e) {
				Log.i("debug", "exception" + e.toString());
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
//	public void analyzeResponseText(String rText){
	public void analyzeResponse(String rText){
		
		 
		String[] datam = rText.split("#"); // 0#NickNameが返ってくる
		if(datam[0].equals("0")){
			AlertUtil.showToast("ログインしました", this);
			String nickname = datam[1];
			final String userName = editName.getText().toString(); 
			this.saveNameData(userName);
			Intent intent = new Intent(this, MainMenuActivity.class);
			intent.putExtra("GeoLocation", this.geoLocation);
			intent.putExtra("NickName", nickname);
//			intent.putExtra("CLIENT", client);
			
			//ログイン名を保存
			GlobalData.userName = userName;
			//パスワードの保存
			if(memorizeIdPassChkBox.isChecked()){
				savePassword(editPass.getText().toString());
			}
			startActivityForResult(intent, START_MAINMENU_ACTIVITY); 
		}else{
			AlertUtil.showToast("ユーザ名またはパスワードが異なります。", this);
		}
		
	}

	
	private String loadNameData(){
		SharedPreferences contactIdPref = this.getSharedPreferences(GlobalData.PREFERENCE_TAG, Context.MODE_PRIVATE);
		return  contactIdPref.getString("name", "guest"); 	
	}

/**
 * IDをセーブ
 * @param savedata
 */
	private void saveNameData(String name){
		SharedPreferences previsouIdPref = this.getSharedPreferences(GlobalData.PREFERENCE_TAG, Context.MODE_PRIVATE);
		Editor editor = previsouIdPref.edit();
		editor.putString("name", name);
		editor.commit();
	}

	/**
	 * パスワード保存するかどうかを読み出し
	 * @return
	 */
	private boolean loadMemorizePass(){
		SharedPreferences pref = this.getSharedPreferences(GlobalData.PREFERENCE_TAG, Context.MODE_PRIVATE);
		return  pref.getBoolean("memorizePass", true);	
	}

	/**
	 * パスワード保存するかを保存
	 * @param checked
	 */
	private void saveMemorizePass(boolean checked){
		SharedPreferences previsouIdPref = this.getSharedPreferences(GlobalData.PREFERENCE_TAG, Context.MODE_PRIVATE);
		Editor editor = previsouIdPref.edit();
		editor.putBoolean("memorizePass", checked);
		editor.commit();
	}
	
	/**
	 * パスワード読み込み
	 * @return
	 */
	private String loadPassword(){
		SharedPreferences contactIdPref = this.getSharedPreferences(GlobalData.PREFERENCE_TAG, Context.MODE_PRIVATE);
		return  contactIdPref.getString("password", "");	
	}

	
	/**
	 * パスワード保存するかを保存
	 * @param checked
	 */
	private void savePassword(String password){
		SharedPreferences previsouIdPref = this.getSharedPreferences(GlobalData.PREFERENCE_TAG, Context.MODE_PRIVATE);
		Editor editor = previsouIdPref.edit();
		editor.putString("password", password);
		editor.commit();
	}



}

//@Override
//public void onLocationChanged(Location location) {
//	// TODO Auto-generated method stub
//	geoLocation.latitude = (int)(location.getLatitude() * 1E6);
//	geoLocation.longitude = (int)(location.getLongitude() * 1E6);
//	geoLocation.altitude = (int)(location.getAltitude() * 1E6);
//	geoLocation.accuracy = (int)(location.getAccuracy() * 1E6);
//	this.isGetGeoLocation = true;
//	getlocationText.setText("位置情報取得:OK");
//
//}
//
//@Override
//public void onProviderDisabled(String provider) {
//	// TODO Auto-generated method stub
//	
//}
//
//@Override
//public void onProviderEnabled(String provider) {
//	// TODO Auto-generated method stub
//	
//}
//
//@Override
//public void onStatusChanged(String provider, int status, Bundle extras) {
//	// TODO Auto-generated method stub
//	
//}
