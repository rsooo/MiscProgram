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
	
	//�N�G����t�͂Ƃ肠�������̃y�[�W�ɑS�ĔC���邱�Ƃɂ���B�ȑO�̕��͕��U���Ă��邯�ǌ�łȂ�Ƃ�����
	public static String URL = GlobalData.CHATROOMPHP; //"http://stress-free.sakura.ne.jp/chatroom2.php";
	
	public final static int RESULT_ACCEPTED = 1;
	public final static int RESULT_CLOSED = 0;
	
	
	MapView mapView;
	private Context context;
	private Activity showMapActivity;
	private MapController mapController;

	private LocationItemizedOverlay itemizedOverlayMyself; //�����̏ꏊ
	private LocationItemizedOverlay itemizedOverlayMale;
	private LocationItemizedOverlay itemizedOverlayFemale;
	private LocationItemizedOverlay itemizedOverlayUnknown;
	
	
	InviteAcceptBroadCastReceiver broadcastReceiver = new InviteAcceptBroadCastReceiver();
	
	// �������g�̈ʒu���B�A�N�e�B�r�e�B�N�����Ɏ󂯎��
	private GeoLocation myGeoLocation;
	ArrayList<UserListData> userListDataList = new ArrayList<UserListData>();
	
	//���҂��鑊���ID�ԍ�
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
		// �r���g�C���Y�[���\��
		mapView.setBuiltInZoomControls(true);
		// �}�b�v�R���g���[���擾
		mapController = mapView.getController();

		// �I�[�o���C--- �������� ------------------------
		Drawable marker_myself = getResources().getDrawable(R.drawable.ownicon);
        Drawable marker_male = getResources().getDrawable(
        				R.drawable.male_pin);  
//                       R.drawable.mark2);   // �}�[�J�[�摜���w��
    
        Drawable marker_female = getResources().getDrawable(R.drawable.female_pin);
        Drawable marker_unknown = getResources().getDrawable(R.drawable.non_pin);
        
        itemizedOverlayMyself = new LocationItemizedOverlayForMyself(marker_myself);
        itemizedOverlayMale = new LocationItemizedOverlay(marker_male);
        itemizedOverlayFemale = new LocationItemizedOverlay(marker_female);
        itemizedOverlayUnknown = new LocationItemizedOverlay(marker_unknown);
        
        // �}�b�v�ɃI�[�o�[���C�Ǘ��N���X��ǉ�
        mapView.getOverlays().add(itemizedOverlayMyself);
        mapView.getOverlays().add(itemizedOverlayMale);
        mapView.getOverlays().add(itemizedOverlayFemale);
        mapView.getOverlays().add(itemizedOverlayUnknown);
        //itemizedOverlayMale = new LocationItemizedOverlay(marker_female);
        //mapView.getOverlays().add(itemizedOverlayMale);

        //-- �����܂� -----------------------------------
		
		mapView.setEnabled(true);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		setContentView(mapView);

//		//Wait�_�C�A���O����
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
		
		//�f�t�H���g��RESULT�R�[�h
		this.setResult(RESULT_CLOSED);
		if(GlobalData.D){Log.d("ShowMapActivity","create finish");}

	}

	@Override
	public void onResume() {
		super.onResume();
		Intent intent = this.getIntent();
		this.myGeoLocation = (GeoLocation) intent
				.getSerializableExtra("GeoLocation");
		getListPost();// �N�����Ƀ��[�U���X�g�擾���� 
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
	    	//LocationManager��~
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
	 * ���[�U�ꗗ���󂯎��v���𑗂�|�X�g
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
		}, "���[�U�ꗗ��M���E�E�E");
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

	// ���ݒn�̃}�[�J�[�ǉ�
//       mapController.animateTo(point);
	private void addPointMarkerUpdateMap(GeoPoint point, String addressName, String snipet, ESEX sex) {
       
		//���ʂɂ���ă}�[�J�[��F����
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
       mapView.invalidate();   //�K�{
    }
	
	/**
	 * �����̃��b�Z�[�W����͂���B
	 * ������id, username, latitude, longitude # �̏��ŕԂ�
	 * #��split���ĕK�v�ȕ���,�ŕ�����
	 * @param resText
	 */
	private void analyzeResponseText(String resText){
		if(resText.equals("")){
			return; //�N�����Ȃ��ꍇ��#�ŃX�v���b�g���Ȃ�
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
		// �}�[�J�[�̕\���ʒu�ƃ��b�Z�[�W��ێ�����I�[�o���C�N���X�̃��X�g
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

		/** ���[�U���}�[�J�[���^�b�v�������ɐe�N���X����Ăяo�����
		 * ���Ƃł���΂�
		 */
		@Override
		protected boolean onTap(int index) {
//			final String id = items.get(index).getSnippet();
			invitingId = items.get(index).getSnippet();
			final String username = items.get(index).getTitle();
			new AlertDialog.Builder(context).setTitle("�`���b�g�ɏ���").setMessage(username + "�����҂��܂�")
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
		 * �V�����ʒu��ǉ�����B�A���A�����ʒu�����X�g�ɑ��݂�����ǉ����Ȃ��B
		 * 
		 * @param point
		 *            �ʒu
		 * @param markerText
		 *            �}�[�J�[�ɕt�����镶����
		 * @param snippet
		 *            �f�Е�����
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
	
		//�������Ȃ�
		@Override
		protected boolean onTap(int index) {
			return true;
		}			
	}
	
	CommonQuery commonQuery; 
	/*ProgressDialog waitDialog;
	private void showWait(){
		
		//�_�C�A���O�g���܂킷�ƂƂ܂�̂Ŗ��񐶐�
		waitDialog = new ProgressDialog(this){
				@Override
				public void onBackPressed(){
					super.onBackPressed();
					cancelInvite();
//					AlertUtil.showToast("back", context);
				}
		};
		waitDialog.setMessage("�`���b�g��������Ғ�");
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
						AlertUtil.showToast("����͊��Ƀ`���b�g���ł�", context);
					} else if (response.equals("2") || response.equals("3")) {
						AlertUtil.showToast("����͑��̐l�ƃ`���b�g�ڑ����ł�", context);
					} else if (response.equals("-1")) {
						AlertUtil.showToast("���ґ���̏�񂪌�����܂���B�}�b�v���ĕ\�����ĉ�����",
								context);
					} else {
						AlertUtil.showToast("�N�G�����M�G���[", context);
					}
				}

			}
		}, "�N�G�����M��");
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
	
	//-----BIND�֘A
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
	
	//-----BroadCastReceiver�֘A
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
