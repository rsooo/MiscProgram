package jp.stressfreesoft.app.chat;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.*;
import android.util.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;


public class _ChatActivity extends Activity implements LocationListener{

	private LocationManager mLocationManager;
	TextView textview;
	Button btn;
	EditText edittext;

	double latitude;
	double longitude;
	double accuracy;
	double altitude;
//	double 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Log.i("debug", "oncreate!");
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		setContentView(R.layout.main);
		textview = (TextView) this.findViewById(R.id.hello);
		edittext = (EditText) this.findViewById(R.id.EditText01); 
		
		btn = (Button) this.findViewById(R.id.Button01);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("debug","click");
				doPost("http://stress-free.sakura.ne.jp/main.php", "hogehoge", edittext.getText().toString());
			}

		});
		
	}

	@Override
	protected void onResume() {
		 if (mLocationManager != null) {
		 mLocationManager.requestLocationUpdates(
//		  LocationManager.GPS_PROVIDER,
		 LocationManager.NETWORK_PROVIDER,
		 (long) 0, 0f, this);
		 }
//		doPost("http://stress-free.sakura.ne.jp/main.php", "hogehoge");
		 Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		 if(location != null){
			Log.v("----------", "----------");
			Log.v("debug", String.valueOf(location.getLatitude()));
			Log.v("debug", String.valueOf(location.getLongitude()));
			Log.v("debug", String.valueOf(location.getAccuracy()));
			Log.v("debug", String.valueOf(location.getAltitude()));
			Log.v("debug", String.valueOf(location.getTime()));
			Log.v("debug", String.valueOf(location.getSpeed()));
			Log.v("debug", String.valueOf(location.getBearing()));
		 }else{
			 Log.v("debug", "no locationinfo");
		 }
		 super.onResume();
	}

	@Override
	public void onPause() {
		if (mLocationManager != null) {
			mLocationManager.removeUpdates(this);
		}
		super.onPause();
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		textview.setText(String.valueOf(location.getLatitude()));
		Log.v("----------", "----------");
		Log.v("Latitude", String.valueOf(location.getLatitude()));
		Log.v("Longitude", String.valueOf(location.getLongitude()));
		Log.v("Accuracy", String.valueOf(location.getAccuracy()));
		Log.v("Altitude", String.valueOf(location.getAltitude()));
		Log.v("Time", String.valueOf(location.getTime()));
		Log.v("Speed", String.valueOf(location.getSpeed()));
		Log.v("Bearing", String.valueOf(location.getBearing()));
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		switch (status) {
		case LocationProvider.AVAILABLE:
			Log.v("Status", "AVAILABLE");
			break;
		case LocationProvider.OUT_OF_SERVICE:
			Log.v("Status", "OUT_OF_SERVICE");
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			Log.v("Status", "TEMPORARILY_UNAVAILABLE");
			break;
		}
	}

	/**
	 * 指定URLからgetした文字列を取得する
	 * 
	 * @param sUrl
	 * @return
	 */
	/*
	 * public String getData(String sUrl) { HttpClient objHttp = new
	 * DefaultHttpClient(); HttpParams params = objHttp.getParams();
	 * HttpConnectionParams.setConnectionTimeout(params, 1000); //接続のタイムアウト
	 * HttpConnectionParams.setSoTimeout(params, 1000); //データ取得のタイムアウト String
	 * sReturn = ""; try { HttpGet objGet = new HttpGet(sUrl); HttpResponse
	 * objResponse = objHttp.execute(objGet); if
	 * (objResponse.getStatusLine().getStatusCode() < 400){ InputStream
	 * objStream = objResponse.getEntity().getContent(); InputStreamReader
	 * objReader = new InputStreamReader(objStream); BufferedReader objBuf = new
	 * BufferedReader(objReader); StringBuilder objJson = new StringBuilder();
	 * String sLine; while((sLine = objBuf.readLine()) != null){
	 * objJson.append(sLine); } sReturn = objJson.toString(); objStream.close();
	 * } } catch (IOException e) { return null; } return sReturn; }
	 */

	public String doPost(String url, String params, String id) {
		try {
			Log.i("debug", "post start");
			HttpPost method = new HttpPost(url);

			DefaultHttpClient client = new DefaultHttpClient();

			// POST データの設定
			// StringEntity paramEntity = new StringEntity( params );
			// paramEntity.setChunked( false );
			// paramEntity.setContentType( "application/x-www-form-urlencoded"
			// );

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("actionid", id));
//			nameValuePairs.add(new BasicNameValuePair("flag", "1"));
			
			// nameValuePairs.add(new BasicNameValuePair("pass", "unko");
			method.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// method.setEntity( paramEntity );

			// method.setParams();

			HttpResponse response = client.execute(method);
			int status = response.getStatusLine().getStatusCode();
			if (status != HttpStatus.SC_OK)
				throw new Exception("");
 
			textview.setText(EntityUtils
					.toString(response.getEntity(), "UTF-8"));
//			textview.refreshDrawableState();
			Log.i("debug", EntityUtils
					.toString(response.getEntity(), "UTF-8"));
			return null;
		} catch (Exception e) {
			Log.i("debug", "exception" + e.toString());
			return null;
		}
	}

	private void setLocationData(Location location){
		if(location == null){
			throw new IllegalArgumentException("location is null");
		}
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		accuracy = location.getAccuracy();
		altitude = location.getAltitude();

	}

}