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
	GeoLocation geoLocation = new GeoLocation(35422006,139454095,0,0); //�f�o�b�O���[�h�̂Ƃ��͂��̏ꏊ
	private TextView getlocationText;
	boolean isGetGeoLocation = false; 
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	private Timer locationTimer;
	private long time;
	
	//�T�[�o����̉���
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
		  
		//�e��ݒ�̓ǂݍ���
		editName.setText(this.loadNameData());
		memorizeIdPassChkBox.setChecked(this.loadMemorizePass());
		if(memorizeIdPassChkBox.isChecked()){
			editPass.setText(loadPassword());
		} 
		if(GlobalData.D){
			//�f�o�b�O���̓��O�C���\
			isGetGeoLocation = true;
		}
		if(GlobalData.D){AlertUtil.showToast("�f�o�b�O���[�h�ł��B", context);}
		if(!GlobalData.HONBAN){AlertUtil.showToast("�e�X�g���ɐڑ����Ă��܂�", context);}
		
		loginButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String name = editName.getText().toString();
				String pass = editPass.getText().toString();
				if(name.equals("")){
					AlertUtil.showToast("���[�U������͂��Ă��������B���߂Ă̕��͐V�K�o�^�{�^���������Ă��������B", context);
				}else if(pass.equals("") && !name.equals("guest")){
					AlertUtil.showToast("�p�X���[�h����͂��Ă��������B", context);
				}else if(!name.matches("[a-zA-Z0-9_]+")){
					AlertUtil.showToast("���[�U���Ɏg�p�ł��Ȃ��������܂܂�Ă��܂��B", context);					
				}else{				
					if(isGetGeoLocation){
						doPost();
					}else{
						AlertUtil.showToast("�ʒu��񂪎擾�ł��Ă��܂���", LoginActivity.this);
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
					AlertUtil.showToast("�ʒu��񂪎擾�ł��Ă��܂���", LoginActivity.this);
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
		
			
		//�T�[�r�X���N�����Ă��邩�m�F
		//�����ɏ����Ă��̂܂�StartActivity���Ă����v�H�H
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
					//�Z�b�V�����؂�Ă�̂ŉ������Ȃ�
					Log.i("LoginActivity", "session unavailable");
				}else{
					String[] datam = ret.split("#"); //latitude#longitude�̌`���ŋA���Ă���
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
	 * �ʒu���擾���\�b�h
	 * @param useLastLocationInfo ���߂Ɏ擾�����ʒu�����g�p����Ȃ�true
	 */
    private void startLocationService(boolean useLastLocationInfo) {
    	stopLocationService();
    	mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
    	//�ʒu���@�\�Ȃ��[���̏ꍇ
    	if(mLocationManager == null){
    		AlertUtil.showToast("���̒[���ł͈ʒu���@�\���g�p�ł��܂���", context);
    		return;
    	}
    	final Criteria criteria = new Criteria();
    	criteria.setBearingRequired(false);//���ʕs�v
    	criteria.setSpeedRequired(false);//���x�s�v
    	final String provider = mLocationManager.getBestProvider(criteria, true);
    	if(provider == null){
			// �ʒu��񂪗L���ɂȂ��Ă��Ȃ��ꍇ�́AGoogle Maps �A�v�����C�N�� [���ݒn�@�\�����P] �_�C�A���O���N�����܂��B
			new AlertDialog.Builder(this)
				.setTitle("���ݒn�@�\�����P")
				.setMessage("���݁A�ʒu���͈ꕔ�L���ł͂Ȃ����̂�����܂��B���̂悤�ɐݒ肷��ƁA�����Ƃ����΂₭���m�Ɍ��ݒn�����o�ł���悤�ɂȂ�܂�:\n\n�� �ʒu���̐ݒ��GPS�ƃ��C�����X�l�b�g���[�N���I���ɂ���\n\n�� Wi-Fi���I���ɂ���")
				.setPositiveButton("�ݒ�", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						// �[���̈ʒu���ݒ��ʂ֑J��
						try {
							startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
						} catch (final ActivityNotFoundException e) {
							// �ʒu���ݒ��ʂ��Ȃ����[���̏ꍇ�́A�d���Ȃ��̂ŉ������Ȃ�
						}
					}
				})
				.setNegativeButton("�X�L�b�v", new DialogInterface.OnClickListener() {
					@Override public void onClick(final DialogInterface dialog, final int which) {}	// �����s��Ȃ�
				}) 
				.create()
				.show();
				stopLocationService();
				return; 
    	}
    	// �Ō�Ɏ擾�ł����ʒu���5���ȓ��̂��̂ł���ΗL���Ƃ��܂��B
    	final Location lastKnownLocation = mLocationManager.getLastKnownLocation(provider);
//    	long diff = new Date().getTime() - lastKnownLocation.getTime();
//    	AlertUtil.showToast("getLOCATION",context);
		if(lastKnownLocation != null && (new Date().getTime() - lastKnownLocation.getTime() <= (5 * 60 * 1000L))){
    		//���߂̈ʒu�����g�p���Ȃ��Ȃ牽�����Ȃ�
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
							AlertUtil.showToast("�ʒu�����擾���Ă��܂��E�E�E", context);
						}else if(time >= (30 * 1000L)){
							AlertUtil.showToast("�ʒu�����擾�ł��܂���ł����B��ł�蒼���ĉ������B", context, Toast.LENGTH_LONG);
							stopLocationService();
						}
						time = time + 1000L;
					} 
				});
			}
    		
    	}, 0, 1000L);
    	
		// �ʒu���̎擾���J�n���܂��B
		mLocationListener = new LocationListener() {
			@Override
			public void onLocationChanged(final Location location) {
//				AlertUtil.showToast("�ʒu���Q�b�g",context);
				Log.i("LoginActivity", "get location information");
				getlocationText.setText("OK");
				setLocation(location);
			}
			@Override public void onProviderDisabled(final String provider) {}
			@Override public void onProviderEnabled(final String provider) {}
			@Override public void onStatusChanged(final String provider, final int status, final Bundle extras) {}
		};
		
		//���Ɉʒu��񂪎擾�ł����ꍇLocationManager�͎g�p����Ȃ�
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
//    	getlocationText.setText("�ʒu���擾:OK");

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
				//���O�C����Ԃ�ێ����Ă���Ƃ��̓��O�C����ʂ�\�������Ȃ�
				if(resultCode == MainMenuActivity.RESULT_KEEPLOGIN){
					finish();
				}
				break;
			case START_GUESTLOGIN_ACTIVITY:
				if(resultCode == GuestLoginActivity.RESULT_OK){
						AlertUtil.showToast("���O�C�����܂���", this);
						final String nickname = data.getStringExtra("NickName");
//						String nickname = datam[1];
//						final String userName = editName.getText().toString(); 
//						this.saveNameData(userName);
						Intent intent = new Intent(this, MainMenuActivity.class);
						intent.putExtra("GeoLocation", this.geoLocation);
						intent.putExtra("NickName", nickname);
						intent.putExtra("GuestFlag", true); //�Q�X�g���O�C���ł��邱�Ƃ�����
//						intent.putExtra("CLIENT", client);
						
						//���O�C������ۑ�
						GlobalData.userName = "guest";
						startActivityForResult(intent, START_MAINMENU_ACTIVITY); 
				}
				break;
			}
			
		}
	    
	/**
	 * �T�[�r�X�N�����Ă邩�ǂ����𔻒f����
	 * PollingService���N�����Ă���Ȃ�true
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
	 * ���O�C���f�[�^�|�X�g
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
				// //�ʒu��񑗂�
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

				// �_�C�A���O�g���܂킷�ƂƂ܂�̂Ŗ��񐶐�
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
				// waitDialog.setMessage("���O�C�����E�E�E");
				// waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				// waitDialog.show();
				// }
				// });
				// final Future<String> future =
				// loginSender.executeQueryAsCallable();
				// final Future<String> future =
				// loginSender.executeQueryAsCallableTest(context, this);
				loginSender.executeQueryAsAsyncTask(context, this, "���O�C����...");
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
		
		 
		String[] datam = rText.split("#"); // 0#NickName���Ԃ��Ă���
		if(datam[0].equals("0")){
			AlertUtil.showToast("���O�C�����܂���", this);
			String nickname = datam[1];
			final String userName = editName.getText().toString(); 
			this.saveNameData(userName);
			Intent intent = new Intent(this, MainMenuActivity.class);
			intent.putExtra("GeoLocation", this.geoLocation);
			intent.putExtra("NickName", nickname);
//			intent.putExtra("CLIENT", client);
			
			//���O�C������ۑ�
			GlobalData.userName = userName;
			//�p�X���[�h�̕ۑ�
			if(memorizeIdPassChkBox.isChecked()){
				savePassword(editPass.getText().toString());
			}
			startActivityForResult(intent, START_MAINMENU_ACTIVITY); 
		}else{
			AlertUtil.showToast("���[�U���܂��̓p�X���[�h���قȂ�܂��B", this);
		}
		
	}

	
	private String loadNameData(){
		SharedPreferences contactIdPref = this.getSharedPreferences(GlobalData.PREFERENCE_TAG, Context.MODE_PRIVATE);
		return  contactIdPref.getString("name", "guest"); 	
	}

/**
 * ID���Z�[�u
 * @param savedata
 */
	private void saveNameData(String name){
		SharedPreferences previsouIdPref = this.getSharedPreferences(GlobalData.PREFERENCE_TAG, Context.MODE_PRIVATE);
		Editor editor = previsouIdPref.edit();
		editor.putString("name", name);
		editor.commit();
	}

	/**
	 * �p�X���[�h�ۑ����邩�ǂ�����ǂݏo��
	 * @return
	 */
	private boolean loadMemorizePass(){
		SharedPreferences pref = this.getSharedPreferences(GlobalData.PREFERENCE_TAG, Context.MODE_PRIVATE);
		return  pref.getBoolean("memorizePass", true);	
	}

	/**
	 * �p�X���[�h�ۑ����邩��ۑ�
	 * @param checked
	 */
	private void saveMemorizePass(boolean checked){
		SharedPreferences previsouIdPref = this.getSharedPreferences(GlobalData.PREFERENCE_TAG, Context.MODE_PRIVATE);
		Editor editor = previsouIdPref.edit();
		editor.putBoolean("memorizePass", checked);
		editor.commit();
	}
	
	/**
	 * �p�X���[�h�ǂݍ���
	 * @return
	 */
	private String loadPassword(){
		SharedPreferences contactIdPref = this.getSharedPreferences(GlobalData.PREFERENCE_TAG, Context.MODE_PRIVATE);
		return  contactIdPref.getString("password", "");	
	}

	
	/**
	 * �p�X���[�h�ۑ����邩��ۑ�
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
//	getlocationText.setText("�ʒu���擾:OK");
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
