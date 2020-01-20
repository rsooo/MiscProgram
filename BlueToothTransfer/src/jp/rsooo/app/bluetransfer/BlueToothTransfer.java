package jp.rsooo.app.bluetransfer;

import java.util.ArrayList;
import java.util.List;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;


//import com.admob.android.ads.AdListener;
//import com.admob.android.ads.AdManager;
//import com.admob.android.ads.AdView;

import jp.rsooo.app.bluetransfer.R;

//import jp.rsooo.app.lib.alert.AlertUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import jp.rsooo.app.bluetransfer.layout.*;
import jp.rsooo.app.lib.alert.AlertUtil;

public class BlueToothTransfer extends ListActivity {
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_CONTACT_PICK = 3;
  //  VCardParser b;
    //    private static final int REQUEST_ENABLE_BT = 2;
    
    //Bluetoothを扱うアダプタ
	private BluetoothAdapter mBluetooth;

	private TwoTextAdapter mListAdapter;
//	private ArrayAdapter<String> testAdapter;

	//リスト内で表示するデータ
	List<TwoTextItem> items =  new ArrayList<TwoTextItem>();
//	List<String> testArray = new ArrayList<String>();
	
	
	Context context;
	Button preferenceButton;
	Button sendButton;
	Button receiveButton;

    TwoTextItem deviceText;
	TwoTextItem nameText;
	TwoTextItem phoneText;
	TwoTextItem mailText;
	ListView listView;
	
	//Bluetoothが使えるかどうか表す
	boolean btSuported = true;
	//BluetoothがONかどうかを表す
	boolean btEnabled = true;
	//画面に表示するコンタクト情報
	ContactInfo contactInfo = new ContactInfo();
	//前回読み込んだコンタクトのID
	long previousId;
	/*String nameDisp = "no data";
	String phoneDisp = "no data";
	String addressDisp = "no data";*/
	
	EditText deviceNameEdit;
	
	//Bluetooth接続してくれるコネクタ
	ClientConnector clientConnector;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.clientConnector = new ClientConnector(this);
        
        setContentView(R.layout.bluetooth_mainlayout);
        
//        
        // Create the adView
        AdView adView = new AdView(this, AdSize.BANNER, "a14d3a7c267c504");
        // Lookup your LinearLayout assuming it’s been given
        // the attribute android:id="@+id/mainLayout"
        LinearLayout layout = (LinearLayout)findViewById(R.id.LinearLayout_admob);
        // Add the adView to it
        layout.addView(adView);
        // Initiate a generic request to load it with an ad
        
        //For test
//        AdRequest request = new AdRequest();
//        request.addTestDevice(AdRequest.TEST_EMULATOR);
//        request.addTestDevice("E83D20734F72FB3108F104ABC0FFC738");    // My T-Mobile G1 test phone
//        adView.loadAd(request);
        
        adView.loadAd(new AdRequest());
        
        
        adView.setAdListener(new AdListener(){

			@Override
			public void onDismissScreen(Ad arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
				// 読み込み失敗時はADlantisを読む？？
		        //adView.setRequestInterval(0);
		        //adView.setVisibility(AdView.GONE);
//				Toast.makeText(BlueToothTransfer.this, "Fail", Toast.LENGTH_SHORT).show();
		        findViewById(R.id.LinearLayout_admob).setVisibility(View.GONE);		        
//		        findViewById(R.id.LinearLayout_adlantis).setVisibility(View.VISIBLE);
//		        Log.v("BluetoothTranfer", "change ad to Adlantis");

			}

			@Override
			public void onLeaveApplication(Ad arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPresentScreen(Ad arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onReceiveAd(Ad arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });

        /*
        final AdView adView = new AdView(this);
        adView.setVisibility(android.view.View.VISIBLE);
        adView.requestFreshAd();
        
        //AdMobの呼び出しが失敗したらAdlantisを開始する
        adView.setAdListener (new AdListener(){

			@Override
			public void onFailedToReceiveAd(AdView arg0) {
				// AdMakerの広告を表示するのでAdViewの更新を行わせない
		        adView.setRequestInterval(0);
		        adView.setVisibility(AdView.GONE);
		        findViewById(R.id.LinearLayout_admob).setVisibility(View.GONE);		        
		        findViewById(R.id.LinearLayout_adlantis).setVisibility(View.VISIBLE);
		        Log.v("BluetoothTranfer", "change ad to Adlantis");
			}
 
			@Override
			public void onFailedToReceiveRefreshedAd(AdView arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onReceiveAd(AdView arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onReceiveRefreshedAd(AdView arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        */
        /*
        AdManager.setTestDevices( new String[] {
        		AdManager.TEST_EMULATOR,
        		});
        */
        //        mListAdapter = new ArrayAdapter<String>(this, R.layout.list_item);
//        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name_preferencemain), MODE_WORLD_READABLE);
        //        setContentView(R.layout.main);
        //        BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
//		AlertUtil.showAlert(bt.getName(),this);
//        bt.setName("a-saitoh's Device");
//        AlertUtil.showAlert(bt.getName(),this);


        
        mListAdapter = new TwoTextAdapter(this, R.layout.twolistitem, items);
//        testAdapter = new ArrayAdapter<String>(this, R.layout.list_item, testArray);
//      ArrayAdapter<String> adapter  = new ArrayAdapter<String>(this, R.layout.checkedtext);
//        setContentView(R.layout.main);
  	
        this.setListAdapter(mListAdapter);
//        this.setListAdapter(testAdapter);
        
      
      
      deviceNameEdit = new EditText(this);
      
      this.context = this;
      listView = this.getListView();
      preferenceButton = (Button)this.findViewById(R.id.prefButton);
      sendButton = (Button)this.findViewById(R.id.sendButton);
      receiveButton = (Button)this.findViewById(R.id.receiveButton);
      //送信情報変更 
      preferenceButton.setOnClickListener(new OnClickListener() {
      	@Override
			public void onClick(View v) {
//      		Intent intent =  new Intent(context, BlueToothPreferenceActivity.class);
//      		startActivity(intent);
//      		Intent myintent = new Intent( Intent.ACTION_EDIT, Uri.parse("content://contacts/people/1") );
//      		startActivity( myintent );
      		Intent myintent = new Intent( Intent.ACTION_PICK, Uri.parse("content://contacts/people") );
//      		Uri uri = ContactsContract.CommonDataKinds. Phone.CONTENT_URI;
      		Uri uri = ContactsContract.Contacts.CONTENT_URI; // Phone.CONTENT_URI;
      		Intent intent2 = new Intent(Intent.ACTION_PICK, uri);
      		startActivityForResult(intent2, REQUEST_CONTACT_PICK);
      	}
      	
		}); 
      	//送信ボタン
      	sendButton.setOnClickListener(new OnClickListener() {
        	@Override
  			public void onClick(View v) {
        		if(!btSuported){
        			showAlert(getString(R.string.dialogmsg_noBt)); 
        			return;
        		}
        		if(!btEnabled){
        			showAlert(getString(R.string.dialogmsg_btDisable)); 
        			return;
        		}
        		if(contactInfo.getName().equals(Constants.NODATA) && contactInfo.outputAddressAsString().equals(Constants.NODATA) &&
        				contactInfo.outputNumberAsString().equals(Constants.NODATA)){
        			showAlert(getString(R.string.dialogmsg_noContact));
        		}else{
        			Intent intent = new Intent(context, DeviceListDialogActivity.class);
        			startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
        		}
  			}
  		});
      	//受信ボタン
    	receiveButton.setOnClickListener(new OnClickListener() {
        	@Override
  			public void onClick(View v) {
        		if(!btSuported){
        			showAlert(getString(R.string.dialogmsg_noBt)); 
        			return;
        		}
        		if(!btEnabled){
        			showAlert(getString(R.string.dialogmsg_btDisable)); 
        			return;
        		}
        		Intent intent = new Intent(context, ServerActivityHack.class);
        		startActivity(intent);
  			}
  		});
    }

    private void constractListItem(){
        /*SharedPreferences preferences = getSharedPreferences("jp.rsooo.app.bluetransfer_preferences"      
                , MODE_PRIVATE);
    	preferences.edit().commit();
        String name = preferences.getString("NAME", "no data");*/
    	if(btSuported){
    		deviceText = new TwoTextItem(getString(R.string.pref_title_device), mBluetooth.getName());
    	}else{
       		deviceText = new TwoTextItem(getString(R.string.pref_title_device), "no device ");   		
    	}
    	nameText = new TwoTextItem(getString(R.string.pref_title_name),contactInfo.getName());
    	phoneText = new TwoTextItem(getString(R.string.pref_title_phone) ,contactInfo.outputNumberAsString());
    	mailText = new TwoTextItem(getString(R.string.pref_title_mail), contactInfo.outputAddressAsString());
    	
    	items.add(deviceText);
    	items.add(nameText);
    	items.add(phoneText);
    	items.add(mailText);
    	
    	
    }
    
    @Override
    public void onStart(){
    	super.onStart();
    	if(this.ensureSupported()){
    		this.ensureEnabled();
    	}
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	this.loadData();
		this.contactInfo = readContactFromId(this.previousId);

//        listView.invalidate();
//    	mListAdapter.clear();
//    	mListAdapter.setNotifyOnChange(notifyOnChange);

//    	mListAdapter.notifyDataSetChanged();
    	
//    	this.items.clear(); //一度アイテムをすべてけす．効率は良くない
    	mListAdapter.clear();
    	this.constractListItem();
    	
    	mListAdapter.notifyDataSetChanged();
    	listView.invalidate();
    	
    	//        SharedPreferences preferences = getSharedPreferences("jp.rsooo.app.bluetransfer_preferences"      
//                , MODE_PRIVATE);
////    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//    	preferences.edit().commit();
//        String name = preferences.getString("NAME", "no data");
//        nameText.setSecondaryText(name);
//        if(testArray.size() != 0)testArray.remove(0);
//        testArray.add(name);
//        testAdapter.notifyDataSetChanged();
//        mListAdapter.notifyDataSetChanged();
//        listView.invalidate();
//        List<TwoTextItem> items = new ArrayList<TwoTextItem>();
//        items.add(new TwoTextItem("端末名", "test"));
//        items.add(new TwoTextItem("名前", name));
//        items.add(new TwoTextItem("電話番号", "no data"));
//        items.add(new TwoTextItem("メールアドレス", "no data"));
    	
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	this.clientConnector.shutdown();
    }
    
    
    /**
     * XMLファイルからメニュー読み込み
     */
    /*
    @Override
    public boolean  onCreateOptionsMenu(Menu menu){
    	MenuInflater inflater = this.getMenuInflater();
    	inflater.inflate(R.menu.option_menu, menu);
    	return true;
    }
    */
    /**
     * オプションメニュー押された時の挙動
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    	case R.id.scan:
//    		Intent serverIntent = new Intent(this, _DeviceListActivity.class);
//    		this.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    	    
    		//名前がfoo barの人をアドレス帳に追記。
            ContentValues values = new ContentValues();
            values.put(android.provider.ContactsContract.Contacts.DISPLAY_NAME, "Test Person");
            values.put(android.provider.ContactsContract.Contacts.STARRED, true);
//            final Uri uri = getContentResolver().insert(android.provider.ContactsContract.Contacts.CONTENT_URI, values);
//            values.clear();
//            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, "09011111111");
            final Uri uri = getContentResolver().insert(android.provider.ContactsContract.Contacts.CONTENT_URI, values);
//            uri.withAppendedPath(uri, ContactsContract.CommonDataKinds.Phone.content);

            
//          final Cursor result = managedQuery(android.provider.Contacts.People.CONTENT_URI, null, null, null, null);
            final Cursor result = managedQuery(android.provider.ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
             
            result.moveToNext();
            final String name = result.getString(result.getColumnIndex(android.provider.ContactsContract.Contacts.DISPLAY_NAME));
//            AlertUtil.showToast(name, this);
    		//アドレス帳の名前がfoo barの人のeメールアドレスを追記。

    	}
    	
    	return false;
    }
    
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
    	//Bt使えないときは反応しない
    	if(!btSuported){
			return;
		}

    	switch(position){
    	case 0:
    			
    		   /* AlertDialog(EditText) */  
    			 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    			 deviceNameEdit = new EditText(this);  
    		     deviceNameEdit.setText(mBluetooth.getName());  
    		   
    		     alertDialogBuilder = new AlertDialog.Builder(this);  
    		     alertDialogBuilder.setTitle(getString(R.string.pref_dialogt_device));  
    		   
    		     // AlertDialog に View を設定  
    		     alertDialogBuilder.setView(deviceNameEdit);  
    		   
    		     // Positive Button を設定  
    		     alertDialogBuilder.setPositiveButton(  
    		       getString(R.string.ok),   
    		       new DialogInterface.OnClickListener() {  
    		         public void onClick(DialogInterface dialog, int which) {  
    		        	 mBluetooth.setName(deviceNameEdit.getText().toString());
    		         }  
    		       }  
    		     );   
    		     alertDialogBuilder.show();    
    		//    		Intent intent = new Intent(this, DeviceListActivityHack.class);
//    		startActivity(intent);
//    		 AlertDialog.show(context, "テキストを入力", deviceNameEdit, "ok",
//    		         null , "cancel", null, true, null);
//    		 AlertDialog a = new ;
//    		 show();
    		return;
    		//AlertDialog.
    		/*
    	case 1:
    		Intent intentc = new Intent(this, ClientActivityHack.class);
    		startActivity(intentc);
    		break;
    	case 2:
     		Intent intents = new Intent(this, ServerActivityHack.class);
    		startActivity(intents);
    		break;*/
    	}
    	super.onListItemClick(l, v, position, id);
    }
    
	class OkAdapter implements android.content.DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			AlertUtil.showToast("test", context);
		}

	}
    
	class CancelAdapter implements
			android.content.DialogInterface.OnCancelListener {

		@Override
		public void onCancel(DialogInterface arg0) {
			AlertUtil.showToast("cancel", context);
		}

  }
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	switch(requestCode){
    	case REQUEST_ENABLE_BT:
    		boolean enabled = (resultCode == Activity.RESULT_OK);
    		btEnabled = enabled;
    		if(enabled){
//    			onBluetoothEnabled();
    		}else{
//    			showAlert("Bluetooth is not enabled on your device.");
    			showAlert(getString(R.string.dialogmsg_btDisable));
    			
    		}
    		return;
    	case REQUEST_CONNECT_DEVICE:
    		final boolean selected = (resultCode == Activity.RESULT_OK);
    		if(selected){
    			BluetoothDevice device = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    			this.clientConnector.setSendingData(contactInfo);
    			this.clientConnector.onDeviceSelected(device);
    			Log.i("bluetooth", "device selected " + device.getName());
    		}else{
    			Log.i("bluetooth", "device select cancelled.");
    		}
    		return;
    	case REQUEST_CONTACT_PICK:
    		if(resultCode == RESULT_OK){
    			
    			Cursor cursor = managedQuery(Uri.parse(data.getDataString()), null, null, null, null);
    			if(cursor.moveToFirst()){
//    				String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
//    				AlertUtil.showAlert(name, this);

    				//    				
    				
//    				String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
    				long id = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Data._ID));
    				saveIdData(id); //ID保存
    			}else{
    				Log.i("debug", "no entry");
    			}
    		}
    		return;
    	}
    		
    	super.onActivityResult(requestCode, resultCode, data);
    	
    }
    
    /**
     * IDからコンタクトの情報読み込む
     * IDが引数
     * @return
     */
    private ContactInfo readContactFromId(final long id){
    	if(id == -1){ //コンタクト情報なし
        	return new ContactInfo();    		
    	}
		List<String> phoneList = new ArrayList<String>();
	 	List<String> addressList = new ArrayList<String>();
    	String name = "";
	 	
    	//よみがな名前
    	String phoneticGiven = Constants.NODATA;
    	//よみがなミドル
    	String phoneticMiddle = Constants.NODATA ;
    	//読み仮名姓
    	String phoneticFamily = Constants.NODATA;
    	
    	Cursor nameCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,ContactsContract.Contacts._ID + " = "+ id,null, null);
        if (nameCursor.moveToNext()){
        	name = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        	int i = nameCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME);
//        	phoneticFamily = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME));
        	//phoneticFirst = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME));
        	 
//        	AlertUtil.showAlert(phoneticFamily + phoneticFirst, this);        
        }else{
        	//コンタクトが変更されたもしくはなし
        	return new ContactInfo();
        }
        nameCursor.close();
/*    	Cursor phoneticCursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI, 
    			new String[]{ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME, ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME }, ContactsContract.CommonDataKinds.StructuredName._ID + " = " + id + " and " + ContactsContract.Data.MIMETYPE + " =?"  ,new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}, null);
        while (phoneticCursor.moveToNext()){ 
        	phoneticFamily = phoneticCursor.getString(0);
    		phoneticFirst = phoneticCursor.getString(phoneticCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME));
//        	AlertUtil.showAlert(phoneticFamily + phoneticFirst, this);        
        }
        phoneticCursor.close();
*/
     // 名前一覧検索
        
        /**バグってるからちょっと消す
        Cursor curName = getContentResolver().query(
        		ContactsContract.Data.CONTENT_URI,
        		new String[] { 
        				StructuredName.PHONETIC_FAMILY_NAME, 
        				StructuredName.PHONETIC_MIDDLE_NAME, 
        				StructuredName.PHONETIC_GIVEN_NAME 
        		},
        		ContactsContract.Data.MIMETYPE + "=? and " + StructuredName.CONTACT_ID  + "=?",
        		new String[] { 
        				StructuredName.CONTENT_ITEM_TYPE, Long.toString(id) 
        		},null
        		*/
        		/*StructuredName.CONTACT_ID + " ASC"*/
    /*   
    );
        if (curName.moveToNext()){  
        	phoneticFamily = curName.getString(0);
        	phoneticFamily = phoneticFamily != null? phoneticFamily : Constants.NODATA;
        	phoneticMiddle = curName.getString(1);
        	phoneticMiddle = phoneticMiddle != null? phoneticMiddle : Constants.NODATA;
        	phoneticGiven = curName.getString(2);
        	phoneticGiven = phoneticGiven != null? phoneticGiven : Constants.NODATA;
//        	AlertUtil.showAlert(phoneticFamily , this);        
        	          	
        }
        curName.close();
        */
		Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
        while (phones.moveToNext()) {
          String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
          phoneList.add(phoneNumber);
        }
        phones.close();
        
        Cursor adresses = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
        while (adresses.moveToNext()) 
        {
          String address = adresses.getString(adresses.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
          addressList.add(address);
          //    		          AlertUtil.showAlert(address, this);
        }
        adresses.close();
        
        return new ContactInfo(name, phoneList, addressList, phoneticFamily, phoneticMiddle, phoneticGiven );
    }
    
    /**
     * BlueToothが使えるかどうかを調べるメソッド
     * @return
     */
	private boolean ensureSupported() {
		BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
		if (bt == null) {
//			showAlert("Bluetooth is not supported on your device.");
			showAlert(getString(R.string.dialogmsg_noBt));
			
			btSuported = false;
			return false;
		}
		mBluetooth = bt;
		return true;
	}
	
    /**
     * Bluetoothを有効にする
     * @return
     */
	private boolean ensureEnabled(){
		 btEnabled = mBluetooth.isEnabled();
		if(btEnabled){
//			onBluetoothEnabled();
			return true;
		} 
		Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(enableBt, REQUEST_ENABLE_BT);
		return false;
	}
	
    /**
     * アラートを表示するメソッド
     * @param message
     */
	private void showAlert(String message) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Info");
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	/**
     * Show message by alert
     * @param message
     * @param c current context
     */
	private void _showAlert(final String message, final Context c) {
		Builder builder = new AlertDialog.Builder(c);
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();

	}

	/**
	 * 依然読み込んだID読む
	 */
	private void loadData(){
		SharedPreferences contactIdPref = this.getSharedPreferences("bluetransfer", Context.MODE_PRIVATE);
		long id = contactIdPref.getLong("id", -1);
		this.previousId = id;
	}
	
	/**
	 * IDをセーブ
	 * @param savedata
	 */
	private void saveIdData(long id){
		SharedPreferences previsouIdPref = this.getSharedPreferences("bluetransfer", Context.MODE_PRIVATE);
		Editor editor = previsouIdPref.edit();
		editor.putLong("id", id);
		editor.commit();
	}

/*
	private void onBluetoothEnabled(){
		this.mListAdapter.clear();
		mListAdapter.add("DeviceList");
		mListAdapter.add("Client");
		mListAdapter.add("Server");
//		mListAdapter.
	}
 */		
	/*    		values.clear();
	values.put(android.provider.Contacts.People.ContactMethods.KIND, android.provider.Contacts.KIND_EMAIL);
	values.put(android.provider.Contacts.People.ContactMethods.DATA, "foo@xxx.jp");
	values.put(android.provider.Contacts.People.ContactMethods.TYPE, android.provider.Contacts.People.ContactMethods.TYPE_HOME);
	getContentResolver().insert(Uri.withAppendedPath(uri ,android.provider.Contacts.People.ContactMethods.CONTENT_DIRECTORY), values);

	//アドレス帳の名前がfoo barの人の名前を変更。
	values.clear();
	values.put(android.provider.Contacts.People.NAME, "foo bar2");
	getContentResolver().update(uri, values, null, null);

	//名前がfoo bar改め、foo bar2の人をアドレス帳から削除。
    getContentResolver().delete(uri, null, null);
*/
	
}