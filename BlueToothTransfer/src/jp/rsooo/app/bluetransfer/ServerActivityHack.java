package jp.rsooo.app.bluetransfer;

import static jp.rsooo.app.bluetransfer.Constants.BUFFER_SIZE;

import static jp.rsooo.app.bluetransfer.Constants.SERIAL_PORT_PROFILE;
import static jp.rsooo.app.bluetransfer.Constants.TAG;
import static jp.rsooo.app.bluetransfer.Constants.MY_SERVICE;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import jp.rsooo.app.bluetransfer.protcol.ContactSender;
import jp.rsooo.app.bluetransfer.protcol.ProtocolReceiver;
import jp.rsooo.app.lib.alert.AlertUtil;
//import jp.rsooo.app.lib.alert.AlertUtil;






import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;


/**
 * サーバ側の挙動するアクティビティ
 * @author akira
 *
 */
public class ServerActivityHack extends Activity implements RejectedExecutionHandler{

	private static final int DURATION = 300;
	private static final int REQUEST_DISCOVERABLE = 0;

	
	private BluetoothAdapter mBluetooth;
	private ConnectedTask mConnectedTask;
	private ExecutorService mExec;
	private ReentrantLock mLock = new ReentrantLock();
//	private TextView mTextView;

	private final class AcceptTask implements Cancelable {

		private final AtomicBoolean mmClosed = new AtomicBoolean();
		private final BluetoothServerSocket mmServerSocket;

		AcceptTask(String service, UUID uuid) {
			BluetoothServerSocket socket = null;
			try {
				socket = mBluetooth.listenUsingRfcommWithServiceRecord(service,
						uuid);
			} catch (IOException e) {
				Log.e(TAG, "listen failed", e);
			}
			mmServerSocket = socket;
		}

		public void cancel() {
			if (mmClosed.getAndSet(true)) {
				return;
			}
			try {
				mmServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close failed", e);
			}
		}

		public void run() {
			BluetoothSocket socket;
			while (!mmClosed.get()) {
				try {
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					cancel();
					Log.e(TAG, "accept failed", e);
					break;
				}
				if (socket != null) {
					accepted(socket);
					cancel();
					break;
				}
			}
		}

		void accepted(BluetoothSocket socket) {
			mLock.lock();
			try {
				runOnUiThread(new Runnable() {
					public void run() {
						setProgressBarIndeterminateVisibility(false);
					}
				});
				dumpMessage("accepted");
				final ConnectedTask task = new ConnectedTask(socket);
				Cancelable canceller = new CancellingTaskHack(mExec, task);
				mExec.execute(canceller);
				mConnectedTask = task;
			} finally {
				mLock.unlock();
			}
		}
	}

	
	private final class ConnectedTask implements Cancelable {

		private final AtomicBoolean mmClosed = new AtomicBoolean();
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		private final BluetoothSocket mmSocket;

		public ConnectedTask(BluetoothSocket socket) {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = socket.getInputStream();
				out = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "sockets not created", e);
			}
			mmSocket = socket;
			mmInStream = in;
			mmOutStream = out;
		}

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

		public void run() {
			InputStream in = mmInStream;
			byte[] buffer = new byte[BUFFER_SIZE];
			int count;
			ProtocolReceiver receiver = new ProtocolReceiver(ServerActivityHack.this);
			while (!mmClosed.get()) {
				try {
					count = in.read(buffer);
					if(receiver.receiveData(buffer, count)){
						//FINがきたら登録
						final String receivedName  = receiver.analyzeAndRegistData();
//						AlertUtil.showToast("regist done", ServerActivityHack.this);
						ContactSender sender = new ContactSender();
						mConnectedTask.write(sender.makeFINPacket()); //受信完了の通知
						AlertUtil.showToast(getString(R.string.server_receive_complete) + ": " + receivedName, ServerActivityHack.this);
						cancel();
						finish();
//						break;
					} 
//					received(buffer, 0, count);
				} catch (IOException e) {
					connectionLost(e);
					cancel();
					break;
				}
			}
		}

		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);
			} catch (IOException e) {
				Log.e(TAG, "write failed", e);
			}
		}

		void connectionLost(IOException e) {
			mLock.lock();
			try {
				dumpMessage(e.getLocalizedMessage());
				mConnectedTask = null;
				startup();
			} finally {
				mLock.unlock();
			}
		}

		/*
		void received(byte[] buffer, int offset, int count) {
			String str = new String(buffer, offset, count);
			dumpMessage(str);
		}*/
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		
		setContentView(R.layout.bluetooth_server);
//		mTextView = (TextView) findViewById(R.id.text);
		mExec = Executors.newCachedThreadPool();
		((ThreadPoolExecutor) mExec).setRejectedExecutionHandler(this);
		mBluetooth = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public void onStart(){
		super.onStart();
		setProgressBarIndeterminateVisibility(true);
		ensureDiscoverable();
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mExec.shutdownNow();
		if(this.mConnectedTask != null){
			this.mConnectedTask.cancel();
		}
	}
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.server, menu);
		return true;
	}
	*/
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.discoverable:
			ensureDiscoverable();
			break;
		case R.id.startup:
			startup();
			break;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
		return true;
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_DISCOVERABLE:
//			boolean discoverable = (resultCode != RESULT_CANCELED);
//			if (resultCode == RESULT_CANCELED) {
////				showAlert(getString(R.string.dialogmsg_no_dicoverable));
//			}
			if(resultCode == DURATION){
				//ここから実際に検出可能にする
				startup();	
			}else{
				AlertUtil.showToast(getString(R.string.dialogmsg_no_dicoverable), this);
				finish();				
			}
			
//			dumpMessage("device is discoverable");
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 端末を検出可能にする
	 */
	private void ensureDiscoverable() {
		Intent discoverable = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
				DURATION);
		startActivityForResult(discoverable, REQUEST_DISCOVERABLE);
	}
	
	protected void startup() {
		runOnUiThread(new Runnable() {
			public void run() {
				setProgressBarIndeterminateVisibility(true);
			}
		}); 
		AcceptTask task = new AcceptTask(MY_SERVICE, SERIAL_PORT_PROFILE);
		Cancelable canceller = new CancellingTaskHack(mExec, task);
		mExec.execute(canceller);
	}
 
	private void dumpMessage(final String msg) {
		final Context ctx = ServerActivityHack.this;
		Runnable dumpTask = new Runnable() {
			public void run() {
				Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
			}
		};
		runOnUiThread(dumpTask);
	}
	
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
}
