package jp.rsooo.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BlueToothTransfer extends ListActivity {
    // Intent request codes
//    private static final int REQUEST_CONNECT_DEVICE = 1;
//    private static final int REQUEST_ENABLE_BT = 2;
//
//    //Bluetoothを扱うアダプタ
//	private BluetoothAdapter mBluetooth;
//
//	private ArrayAdapter<String> mListAdapter;
//
//	
//	/** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mListAdapter = new ArrayAdapter<String>(this, R.layout.list_item);
//        setContentView(R.layout.main);
//        setListAdapter(mListAdapter);
//    }
//
//    @Override
//    public void onStart(){
//    	super.onStart();
//    	if(this.ensureSupported()){
//    		this.ensureEnabled();
//    	}
//    }
//    
//    /**
//     * XMLファイルからメニュー読み込み
//     */
//    @Override
//    public boolean  onCreateOptionsMenu(Menu menu){
//    	MenuInflater inflater = this.getMenuInflater();
//    	inflater.inflate(R.menu.option_menu, menu);
//    	return true;
//    }
//    
//    /**
//     * オプションメニュー押された時の挙動
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//    	switch(item.getItemId()){
//    	case R.id.scan:
//    		Intent serverIntent = new Intent(this, _DeviceListActivity.class);
//    		this.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
//    	}
//    	
//    	return false;
//    }
//    
//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id){
//    	
//    	switch(position){
//    	case 0:
//    		Intent intent = new Intent(this, DeviceListActivityHack.class);
//    		startActivity(intent);
//    		break;
//    	case 1:
//    		this.showAlert("1");
//    		break;
//    	case 2:
//    		this.showAlert("2");
//    		break;
//    	}
//    }
//    
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//    	switch(requestCode){
//    	case REQUEST_ENABLE_BT:
//    		boolean enabled = (resultCode == Activity.RESULT_OK);
//    		if(enabled){
//    			onBluetoothEnabled();
//    		}else{
//    			showAlert("Bluetooth is not enabled on your device.");
//    		}
//    		break;
//    	}
//    }
//    
//    /**
//     * BlueToothが使えるかどうかを調べるメソッド
//     * @return
//     */
//	private boolean ensureSupported() {
//		BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
//		if (bt == null) {
//			showAlert("Bluetooth is not supported on your device.");
//			return false;
//		}
//		mBluetooth = bt;
//		return true;
//	}
//	
//    /**
//     * Bluetoothを有効にする
//     * @return
//     */
//	private boolean ensureEnabled(){
//		boolean enabled = mBluetooth.isEnabled();
//		if(enabled){
//			onBluetoothEnabled();
//			return true;
//		} 
//		Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//		startActivityForResult(enableBt, REQUEST_ENABLE_BT);
//		return false;
//	}
//	
//    /**
//     * アラートを表示するメソッド
//     * @param message
//     */
//	private void showAlert(String message) {
//		Builder builder = new AlertDialog.Builder(this);
//		builder.setMessage(message);
//		builder.setPositiveButton(android.R.string.ok,
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						// noop.
//					}
//				});
//		AlertDialog dialog = builder.create();
//		dialog.show();
//	}
//
//	private void onBluetoothEnabled(){
//		this.mListAdapter.clear();
//		mListAdapter.add("DeviceList");
//		mListAdapter.add("Client");
//		mListAdapter.add("Server");
//		
//		
//	}
	
}