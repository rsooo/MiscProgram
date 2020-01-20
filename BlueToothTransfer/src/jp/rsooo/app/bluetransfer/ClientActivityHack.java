package jp.rsooo.app.bluetransfer;

import static jp.rsooo.app.bluetransfer.Constants.BUFFER_SIZE;

import static jp.rsooo.app.bluetransfer.Constants.SERIAL_PORT_PROFILE;
import static jp.rsooo.app.bluetransfer.Constants.TAG;


import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import jp.rsooo.app.bluetransfer.protcol.ContactSender;
//import jp.rsooo.app.lib.alert.AlertUtil;









import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import static jp.rsooo.app.bluetransfer.protcol.ProtocolConstants.*;

public class ClientActivityHack extends Activity implements RejectedExecutionHandler{
	private static final int REQUEST_SELECT_DEVICE = 0;
	
	private ExecutorService mExec;
	private TextView mTextView;
	private ConnectedTask mConnectedTask;
	//Bluetooth扱うための基本クラス
	private BluetoothAdapter mBluetooth;
	//明示的なsynchronizedのようなもの？？
	private ReentrantLock mLock = new ReentrantLock();

	
	
	/**
	 * 接続した後の処理を行うクラス
	 * @author akira
	 *
	 */
	private final class ConnectedTask implements Cancelable{

		private final AtomicBoolean mmClosed = new AtomicBoolean();
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		private final BluetoothSocket mmSocket;
		
		public ConnectedTask(BluetoothSocket socket){
			InputStream in = null;
			OutputStream out = null;
			try{
				in = socket.getInputStream();
				out = socket.getOutputStream();
			}catch(IOException e){
				Log.e(TAG, "sockets not created", e);
			}
			mmSocket = socket;
			mmInStream = in;
			mmOutStream = out;
		}
		
		@Override
		public void cancel() {
			if (mmClosed.getAndSet(true)) {
				return;
			}
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close failed", e);
			}
		}

		@Override
		public void run() {
			InputStream in = mmInStream;
			byte[] buffer = new byte[BUFFER_SIZE];
			int count;
			while (!mmClosed.get()) {
				try {
					count = in.read(buffer);
					received(buffer, 0, count);
				} catch (IOException e) {
					connectionLost(e);
					cancel();
					break;
				}
			}
		}
		
		void connectionLost(IOException e) {
			dumpMessage(e.getLocalizedMessage());
		}
		
		void received(byte[] buffer, int offset, int count) {
			String str = new String(buffer, offset, count);
			dumpMessage(str);
		}
		
		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);
			} catch (IOException e) {
				Log.e(TAG, "write failed", e);
			}
		}
	}
	
	/**
	 * 接続処理を行うクラス
	 * @author akira
	 *
	 */
	private final class ConnectTask implements Cancelable {

		private final AtomicBoolean mmClosed = new AtomicBoolean();
		private final BluetoothSocket mmSocket;

		//ACKもらっていたらTRUE．すなわち次のデータが送れる
		private  boolean getACK = true;
		
		public ConnectTask(BluetoothDevice device, UUID uuid){
			BluetoothSocket socket = null;
			try{
				socket = device.createRfcommSocketToServiceRecord(uuid);
			}catch (IOException e){
				Log.e(TAG,"create failed", e);
			}
			mmSocket = socket;
		}
		@Override
		public void cancel() {
			//既に閉じられてたら終了
			if(mmClosed.getAndSet(true)){
				return;
			}
			try{
				mmSocket.close();
			}catch(IOException e){
				Log.e(TAG, "close failed", e);
			}
		}

		@Override
		public void run() {
			//端末検索してたら終了
			if (mBluetooth.isDiscovering()) {
				mBluetooth.cancelDiscovery();
			}
			try {
				mmSocket.connect();
				connected(mmSocket);
			} catch (IOException e) {
				connectionFailed(e);
				cancel();
			}
		}
		
		void connected(BluetoothSocket socket) {
			mLock.lock();
			try {
				dumpMessage("connected");
				final ConnectedTask task = new ConnectedTask(socket);
				Cancelable canceller = new CancellingTaskHack(mExec, task);
				mExec.execute(canceller);
				mConnectedTask = task;
				ContactSender sender = new ContactSender();
				mConnectedTask.write(sender.makeSendingPacket(NAMEHEADER, "Test Tarou\n"));
				mConnectedTask.write(sender.makeSendingPacket(PHONEHEADER, "09085353532\n"));
				mConnectedTask.write(sender.makeSendingPacket(ADDRESSHEADER, "test@test.com\n"));
				mConnectedTask.write(sender.makeFINPacket());
//				AlertUtil.showToast("sending end", ClientActivityHack.this);
				
				 
			} finally {
				mLock.unlock();
			}
		}
		
		void connectionFailed(IOException e) {
			dumpMessage(e.getLocalizedMessage());
		}
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.bluetooth_client);
		//		setContentView(R.layout.bluetooth_client);
		
		mTextView = (TextView) findViewById(R.id.text);
		mExec = Executors.newCachedThreadPool();
		((ThreadPoolExecutor) mExec).setRejectedExecutionHandler(this);
		mBluetooth = BluetoothAdapter.getDefaultAdapter();
		mTextView = (TextView) findViewById(R.id.text);

	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		mExec.shutdownNow();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.client, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		switch (id) {
//		case R.id.recent_device:
//			BluetoothDevice recent = loadDefault();
//			onDeviceSelected(recent);
//			break;
		case R.id.search:
			Intent selectDevice = new Intent(this, DeviceListDialogActivity.class);
			startActivityForResult(selectDevice, REQUEST_SELECT_DEVICE);
			break;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
		return true;
	}


	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_SELECT_DEVICE:
			consumeRequestDeviceSelect(resultCode, data);
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * ボタンが押されたときの挙動、リスナに登録しなくてもレイアウトに書いとけば動くみたい
	 * @param view
	 */
	public void onWriteButtonClick(View view) {
		mLock.lock();
		try {
			if (mConnectedTask == null) {
				dumpMessage("not connected");
				return;
			}
			String text = obtainText();
			mConnectedTask.write(text.getBytes());
		} finally {
			mLock.unlock();
		}
	}

	private String obtainText() {
		return mTextView.getText().toString();
	}
	
	private void consumeRequestDeviceSelect(int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		BluetoothDevice device = data
				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//		saveAsDefault(device);
		onDeviceSelected(device);
	}
	
	private void onDeviceSelected(BluetoothDevice device) {
		mLock.lock();
		try {
			if (mConnectedTask != null) {
				mConnectedTask.cancel();
				mConnectedTask = null;
			}
		} finally {
			mLock.unlock();
		}
		dumpMessage("connecting");
		ConnectTask task = new ConnectTask(device, SERIAL_PORT_PROFILE);
		Cancelable canceller = new CancellingTaskHack(mExec, task, 10,
				TimeUnit.SECONDS);
		mExec.execute(canceller);	
	}
	
	private void dumpMessage(final String msg) {
		final Context ctx = ClientActivityHack.this;
		Runnable dumpTask = new Runnable() {
			public void run() {
				Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
			}
		};
		runOnUiThread(dumpTask);
	}
	
	/**
	 * Show context on UiThread
	 * @param message
	 * @param current context
	 */
	private void ShowToast(final String msg, final Context c) {
		final Context ctx = c;
		Runnable dumpTask = new Runnable() {
			public void run() {
				Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
			}
		};
		runOnUiThread(dumpTask);
	}
}
