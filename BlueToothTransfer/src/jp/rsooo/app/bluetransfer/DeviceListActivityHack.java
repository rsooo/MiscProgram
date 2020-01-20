package jp.rsooo.app.bluetransfer;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.bluetooth.BluetoothDevice.ACTION_NAME_CHANGED;
import static android.bluetooth.BluetoothDevice.EXTRA_DEVICE;

import java.util.Set;




import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DeviceListActivityHack extends ListActivity {
	private BluetoothAdapter mBluetooth;
	private BluetoothDeviceAdapter mListAdapter;
	private DeviceDiscoveryHandler mDeviceDiscoveryHandler;

	// private BluetoothDeviceAdapter
	private final class BluetoothDeviceAdapter extends
			ArrayAdapter<BluetoothDevice> {

		private Context mContext;
		private LayoutInflater mLayoutInflater;
		private int mTextViewResourceId;

		public BluetoothDeviceAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			mContext = context;
			mTextViewResourceId = textViewResourceId;
			mLayoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View resultView = convertView;
			if (resultView == null) {
				resultView = mLayoutInflater.inflate(mTextViewResourceId, null);
			}
			BluetoothDevice device = getItem(position);
			String name = device.getName();
			String addr = device.getAddress();
			((TextView) resultView.findViewById(R.id.device_name))
					.setText(name);
			((TextView) resultView.findViewById(R.id.mac_address))
					.setText(addr);
			return resultView;
		}
	}
	
	private final class DeviceDiscoveryHandler extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_FOUND)) {
				BluetoothDevice device = intent
						.getParcelableExtra(EXTRA_DEVICE);
				if (isDeviceBonded(device)) {
					return;
				}
				if (!isNameRetreived(device)) {
					return;
				}
				mListAdapter.add(device);
				return;
			}
			if (action.equals(ACTION_NAME_CHANGED)) {
				BluetoothDevice device = intent
						.getParcelableExtra(EXTRA_DEVICE);
				if (isDeviceBonded(device)) {
					return;
				}
				mListAdapter.add(device);
				return;
			}
			if (action.equals(ACTION_DISCOVERY_STARTED)) {
				setProgressBarIndeterminateVisibility(true);
				return;
			}
			if (action.equals(ACTION_DISCOVERY_FINISHED)) {
				setProgressBarIndeterminateVisibility(false);
				return;
			}			
		}

		/**
		 * Bluetooth�Ɋւ��郁�b�Z�[�W���󂯎��悤�ɐݒ�
		 * @param context
		 */
		void registerFor(Context context) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(ACTION_FOUND);
			filter.addAction(ACTION_NAME_CHANGED);
			filter.addAction(ACTION_DISCOVERY_STARTED);
			filter.addAction(ACTION_DISCOVERY_FINISHED);
			context.registerReceiver(this, filter);
		}

		/**
		 * ���Ƀy�A�����O�ς݂��𒲂ׂ�
		 * @param device
		 * @return
		 */
		private boolean isDeviceBonded(BluetoothDevice device) {
			boolean bonded = device.getBondState() == BluetoothDevice.BOND_BONDED;
			return bonded;
		}

		/**
		 * ���O���擾���Ă��邩�ǂ������ׂ�
		 * @param device
		 * @return
		 */
		private boolean isNameRetreived(BluetoothDevice device) {
			boolean resolved = device.getName() != null;
			return resolved;
		}


		
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.device_list_hack);
		setResult(RESULT_CANCELED); // ??
		mBluetooth = BluetoothAdapter.getDefaultAdapter();
		BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(this,
				R.layout.device_list_item);
		setListAdapter(adapter);
		mListAdapter = adapter;
		mDeviceDiscoveryHandler = new DeviceDiscoveryHandler();

	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mListAdapter.clear();
		ensurePairedDevices();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		this.mDeviceDiscoveryHandler.registerFor(this);
	}
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.device_list, menu);
		return true;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		BluetoothDevice remDev = mListAdapter.getItem(position);
		Intent result = new Intent();
		result.putExtra(EXTRA_DEVICE, remDev);
		setResult(RESULT_OK, result);
		finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan_new_devices:
			startDiscovery();
			return true;
		}
		return false;
	}
	
	/**
	 * �V�����f�o�C�X������
	 */
	void startDiscovery(){
		if(mBluetooth.isDiscovering()){
			mBluetooth.cancelDiscovery();
		}
		mBluetooth.startDiscovery();
	}
	
	void ensurePairedDevices() {
		Set<BluetoothDevice> pairedDevices = mBluetooth.getBondedDevices();
		for (BluetoothDevice device : pairedDevices) {
			mListAdapter.add(device);
		}
	}
}
