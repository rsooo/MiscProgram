package jp.stressfreesoft.app.chat;

import java.io.IOException;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import jp.rsooo.app.lib.alert.AlertUtil;
import jp.stressfreesoft.app.chat.PollingService.POLLING_STATE;
import jp.stressfreesoft.app.chat.client.GlobalHttpClient;
import jp.stressfreesoft.app.chat.client.RequestSender;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus; 
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class ShowMapActivity extends MapActivity {

	public static String POSTURL = GlobalData.SELECTLISTPHP; //"http://stress-free.sakura.ne.jp/selectlist.php";
	
	//クエリ受付はとりあえずこのページに全て任せることにする。以前の分は分散しているけど後でなんとかする
	public static String URL = GlobalData.CHATROOMPHP; //"http://stress-free.sakura.ne.jp/chatroom2.php";
	
	public final static int RESULT_ACCEPTED = 1;
	public final static int RESULT_CLOSED = 0;
	
	
	MapView mapView;
	private Context context;
	private Activity showMapActivity;
	private MapController mapController;

	private LocationItemizedOverlay itemizedOverlayMyself; //自分の場所
	private LocationItemizedOverlay itemizedOverlayMale;
	private LocationItemizedOverlay itemizedOverlayFemale;
	private LocationItemizedOverlay itemizedOverlayUnknown;
	
	
	InviteAcceptBroadCastReceiver broadcastReceiver = new InviteAcceptBroadCastReceiver();
	
	// 自分自身の位置情報。アクティビティ起動時に受け取る
	private GeoLocation myGeoLocation;
	ArrayList<UserListData> userListDataList = new ArrayList<UserListData>();
	
	//招待する相手のID番号
	String invitingId = null;
//    Drawable marker_female;
//    Drawable marker_unknown;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mapView = new MapView(this,
//				"0hlpivdVl4ecJT1HhkewEFzwyCujh_ij9kTnh4g");
				"0hlpivdVl4ed8H7G8HGGVTVWhu3ycKQw8MCSEhA");
				context = this;
		showMapActivity = this;
		// ビルトインズーム表示
		mapView.setBuiltInZoomControls(true);
		// マップコントローラ取得
		mapController = mapView.getController();

		// オーバレイ--- ここから ------------------------
		Drawable marker_myself = getResources().getDrawable(R.drawable.ownicon);
        Drawable marker_male = getResources().getDrawable(
        				R.drawable.male_pin);  
//                       R.drawable.mark2);   // マーカー画像を指定
    
        Drawable marker_female = getResources().getDrawable(R.drawable.female_pin);
        Drawable marker_unknown = getResources().getDrawable(R.drawable.non_pin);
        
        itemizedOverlayMyself = new LocationItemizedOverlayForMyself(marker_myself);
        itemizedOverlayMale = new LocationItemizedOverlay(marker_male);
        itemizedOverlayFemale = new LocationItemizedOverlay(marker_female);
        itemizedOverlayUnknown = new LocationItemizedOverlay(marker_unknown);
        
        // マップにオーバーレイ管理クラスを追加
        mapView.getOverlays().add(itemizedOverlayMyself);
        mapView.getOverlays().add(itemizedOverlayMale);
        mapView.getOverlays().add(itemizedOverlayFemale);
        mapView.getOverlays().add(itemizedOverlayUnknown);
        //itemizedOverlayMale = new LocationItemizedOverlay(marker_female);
        //mapView.getOverlays().add(itemizedOverlayMale);

        //-- ここまで -----------------------------------
		
		mapView.setEnabled(true);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		setContentView(mapView);

//		//Waitダイアログ生成
//		 waitDialog = new ProgressDialog(this){
//				@Override
//				public void onBackPressed(){
//					super.onBackPressed();
//					cancelInvite();
////					AlertUtil.showToast("back", context);
//				}
//		};
//		waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(PollingService.INTENT_ACCEPTED);
		this.registerReceiver(broadcastReceiver, intentFilter);
		
		//デフォルトのRESULTコード
		this.setResult(RESULT_CLOSED);
		if(GlobalData.D){Log.d("ShowMapActivity","create finish");}

	}

	@Override
	public void onResume() {
		super.onResume();
		Intent intent = this.getIntent();
		this.myGeoLocation = (GeoLocation) intent
				.getSerializableExtra("GeoLocation");
		getListPost();// 起動時にユーザリスト取得する 
//		itemizedOverlayMyself.addNewItem(this.myGeoLocation.createGeoPoint(), "myself", "" /*no snipet*/);
//		for(UserListData userdata : this.userListDataList){
//			
//			this.addPointMarkerUpdateMap(userdata.createGeoPoint(), userdata.username, String.valueOf(userdata.id), userdata.sex);
//		}
		// mapController.animateTo(this.myGeoLocation.createGeoPoint());
		mapController.setZoom(16);
		mapController.setCenter(this.myGeoLocation.createGeoPoint());
	}

	   @Override
	    protected void onDestroy(){
	    	//LocationManager停止
	    	mapView.getOverlays().remove(itemizedOverlayMale);
	        itemizedOverlayMale.clear();
	        this.unregisterReceiver(broadcastReceiver);
	        if(pollingService != null){
	        	this.unbindService(serviceConnection);
	        	if(GlobalData.D){Log.d("ShowMapActivity","destroy:unbind service");}
	        }
	        super.onDestroy();
	    }
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * ユーザ一覧を受け取る要求を送るポスト
	 */
	private void getListPost() {
//		HttpPost method = new HttpPost(POSTURL);

//		DefaultHttpClient client = GlobalHttpClient.getInstance();

		RequestSender sender = new RequestSender(POSTURL);
		sender.addVeluePair("ACTIONID", "1");
		sender.executeQueryAsAsyncTask(context, new QueryResponseReceiver() {
			
			@Override
			public void analyzeResponse(String response) {
				// TODO Auto-generated method stub
				analyzeResponseText(response);	
				itemizedOverlayMyself.addNewItem(myGeoLocation.createGeoPoint(), "myself", "" /*no snipet*/);
				for(UserListData userdata : userListDataList){					
					addPointMarkerUpdateMap(userdata.createGeoPoint(), userdata.username, String.valueOf(userdata.id), userdata.sex);
				}

			}
		}, "ユーザ一覧受信中・・・");
//		String ret = "";
//		try {
//			ret = future.get();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			e.printStackTrace();
//		}
////		AlertUtil.showToast(ret, this);
//		
		
	}

	// 現在地のマーカー追加
//       mapController.animateTo(point);
	private void addPointMarkerUpdateMap(GeoPoint point, String addressName, String snipet, ESEX sex) {
       
		//性別によってマーカーを色分け
		switch(sex){
		case MALE:
			itemizedOverlayMale.addNewItem(point, addressName, snipet);
			break;
		case FEMALE:
			itemizedOverlayFemale.addNewItem(point, addressName, snipet);
			break;
		case UNKNOWN:
			itemizedOverlayUnknown.addNewItem(point, addressName, snipet);
			break;
		default:
			throw new IllegalStateException(); 
		}
       mapView.invalidate();   //必須
    }
	
	/**
	 * 応答のメッセージを解析する。
	 * 応答はid, username, latitude, longitude # の順で返す
	 * #でsplitして必要な分を,で分ける
	 * @param resText
	 */
	private void analyzeResponseText(String resText){
		if(resText.equals("")){
			return; //誰もいない場合は#でスプリットしない
		} 
		if(GlobalData.D){AlertUtil.showToast(resText, context);}
		if(resText.equals(GlobalData.SESSION_ERROR_CODE)){
			AlertUtil.showToast(getString(R.string.session_error_mes), context);
		}else{
			String[] datam = resText.split("#");
			for(String recode : datam){
				String[] data = recode.split(",");
				this.userListDataList.add(new UserListData(Integer.parseInt(data[0]), data[1], data[2], Integer.parseInt(data[3]), Integer.parseInt(data[4])));
			}
		}
	}
	
	public class LocationItemizedOverlay extends ItemizedOverlay<OverlayItem> {

		// private Context context;
		// マーカーの表示位置とメッセージを保持するオーバレイクラスのリスト
		private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

		public LocationItemizedOverlay(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
			// context = this;
			this.populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return items.get(i);
		}

		@Override
		public int size() {
			return items.size();
		}

		/** ユーザがマーカーをタップした時に親クラスから呼び出される
		 * あとでがんばる
		 */
		@Override
		protected boolean onTap(int index) {
//			final String id = items.get(index).getSnippet();
			invitingId = items.get(index).getSnippet();
			final String username = items.get(index).getTitle();
			new AlertDialog.Builder(context).setTitle("チャットに招待").setMessage(username + "を招待します")
			.setPositiveButton("YES", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
//					Toast.makeText(context, invitingId, Toast.LENGTH_SHORT).show();
					//showWait();
					String ret = "";
					if(pollingService == null){
						pollingBind();						
					}else{
						executeInvite();
					}
//					if(ret.equals("0")){
//						commonQuery = new CommonQuery(context);
//						commonQuery.showWait(invitingId);						
//					}
				}
			}).setNegativeButton("NO", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
			return true;
		}

		public void clear() {
		      items.clear();
		      populate();
	   }
		
		

		
		/**
		 * 新しい位置を追加する。但し、同じ位置がリストに存在したら追加しない。
		 * 
		 * @param point
		 *            位置
		 * @param markerText
		 *            マーカーに付随する文字列
		 * @param snippet
		 *            断片文字列
		 */
			// if (getIndexGeoPoint(point) == NOT_GEOPOINT) {
		public void addNewItem(GeoPoint point, String markerText, String snippet) {
			items.add(new OverlayItem(point, markerText, snippet));
			populate();
			// }
		}
	}

	public class LocationItemizedOverlayForMyself extends LocationItemizedOverlay {

		public LocationItemizedOverlayForMyself(Drawable defaultMarker) {
			super(defaultMarker);
			// TODO Auto-generated constructor stub
		}
	
		//何もしない
		@Override
		protected boolean onTap(int index) {
			return true;
		}			
	}
	
	CommonQuery commonQuery; 
	/*ProgressDialog waitDialog;
	private void showWait(){
		
		//ダイアログ使いまわすととまるので毎回生成
		waitDialog = new ProgressDialog(this){
				@Override
				public void onBackPressed(){
					super.onBackPressed();
					cancelInvite();
//					AlertUtil.showToast("back", context);
				}
		};
		waitDialog.setMessage("チャット相手を招待中");
		waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		waitDialog.show();
	}
	*/ 
	private void cancelInvite(){
		assert invitingId != null : "InvitingId is null!";
		
		//pollingService.setState(POLLING_STATE.WAIT);
		RequestSender sender = new RequestSender(URL);
		sender.addVeluePair("ACTIONID", "31");
		sender.addVeluePair("INVITINGID", invitingId);
		sender.executeQuery();
//		AlertUtil.showAlert(sender.getResponseText(), context);
	}
	
	private void executeInvite() {
		assert invitingId != null : "InvitingId is null!";
		// pollingService.setState(POLLING_STATE.INVITING);
		pollingService.changeInviteState(invitingId);
		final RequestSender sender = new RequestSender(URL);
		sender.addVeluePair("ACTIONID", "30");
		sender.addVeluePair("INVITINGID", invitingId);
		sender.executeQueryAsAsyncTask(context, new QueryResponseReceiver() {

			@Override
			public void analyzeResponse(String response) {
				// TODO Auto-generated method stub
				if (sender.checkResponse(response, context)) {

					if (response.equals("0")) {
						commonQuery = new CommonQuery(context);
						commonQuery.showWait(invitingId);
					} else if (response.equals("1")) {
						AlertUtil.showToast("相手は既にチャット中です", context);
					} else if (response.equals("2") || response.equals("3")) {
						AlertUtil.showToast("相手は他の人とチャット接続中です", context);
					} else if (response.equals("-1")) {
						AlertUtil.showToast("招待相手の情報が見つかりません。マップを再表示して下さい",
								context);
					} else {
						AlertUtil.showToast("クエリ送信エラー", context);
					}
				}

			}
		}, "クエリ送信中");
		/*String ret = "";
		try {
			future.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return ret;*/
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
        	if(GlobalData.D){Log.d("ShowMapActivity","connect service");}

			pollingService = ((PollingService.PollingBinder)service).getService();
			executeInvite();
		}
		@Override
		public void onServiceDisconnected(ComponentName className){
        	if(GlobalData.D){Log.d("ShowMapActivity","unbind service");}
			pollingService = null;
		}
	};
	
	//-----BroadCastReceiver関連
	public class InviteAcceptBroadCastReceiver extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent) {
//			AlertUtil.showToast("Intent_ACCEPTED", context);
			assert commonQuery != null : "CommonQUery is null!!";
			commonQuery.dismiss();
			showMapActivity.setResult(RESULT_ACCEPTED);
			finish();
		}
		
	}
	
}
