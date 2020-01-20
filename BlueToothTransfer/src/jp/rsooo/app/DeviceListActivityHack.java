package jp.rsooo.app;

import java.util.Set;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DeviceListActivityHack extends ListActivity {
//	private BluetoothAdapter mBluetooth;
//	private BluetoothDeviceAdapter mListAdapter;
//
//	// private BluetoothDeviceAdapter
//	private final class BluetoothDeviceAdapter extends
//			ArrayAdapter<BluetoothDevice> {
//
//		private Context mContext;
//		private LayoutInflater mLayoutInflater;
//		private int mTextViewResourceId;
//
//		public BluetoothDeviceAdapter(Context context, int textViewResourceId) {
//			super(context, textViewResourceId);
//			mContext = context;
//			mTextViewResourceId = textViewResourceId;
//			mLayoutInflater = (LayoutInflater) mContext
//					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			View resultView = convertView;
//			if (resultView == null) {
//				resultView = mLayoutInflater.inflate(mTextViewResourceId, null);
//			}
//			BluetoothDevice device = getItem(position);
//			String name = device.getName();
//			String addr = device.getAddress();
//			((TextView) resultView.findViewById(R.id.device_name))
//					.setText(name);
//			((TextView) resultView.findViewById(R.id.mac_address))
//					.setText(addr);
//			return resultView;
//		}
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
//		setContentView(R.layout.device_list);
//		setResult(RESULT_CANCELED); // ??
//		mBluetooth = BluetoothAdapter.getDefaultAdapter();
//		BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(this,
//				R.layout.device_list_item);
//		setListAdapter(adapter);
//		mListAdapter = adapter;
//	
//
//	}
//	
//	@Override
	
//	protected void onStart() {
//		super.onStart();
//		mListAdapter.clear();
//		ensurePairedDevices();
//	}
//
//	void ensurePairedDevices() {
//		Set<BluetoothDevice> pairedDevices = mBluetooth.getBondedDevices();
//		for (BluetoothDevice device : pairedDevices) {
//			mListAdapter.add(device);
//		}
//	}
}
