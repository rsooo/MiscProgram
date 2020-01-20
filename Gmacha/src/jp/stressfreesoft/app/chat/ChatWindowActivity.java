package jp.stressfreesoft.app.chat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import jp.rsooo.app.lib.alert.AlertUtil;
import jp.stressfreesoft.app.chat.PollingService.POLLING_STATE;
import jp.stressfreesoft.app.chat.ShowMapActivity.InviteAcceptBroadCastReceiver;
import jp.stressfreesoft.app.chat.client.GlobalHttpClient;
import jp.stressfreesoft.app.chat.client.RequestSender;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatWindowActivity extends ListActivity {
	public static final String URL = GlobalData.CHATROOMPHP; //"http://stress-free.sakura.ne.jp/chatroom2.php"; 
	private String userID;
	private String responseText;
	
	private List<String> chatList = new ArrayList<String>();
    private ArrayAdapter<String> chatListArrayAdapter;
    
	
	TextView textDebug;
	EditText editmessage;
	Button sendButton; 
	ListView chatListView;
	Context context;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatlayout);
		
		//Intent�ɓn���ꂽ�f�[�^�����Ƃ�
		Intent receivedIntent = this.getIntent();
		userID = receivedIntent.getStringExtra("ID");
//		AlertUtil.showToast("userID" + userID , this);
		context = this;
//		textDebug = (TextView)this.findViewById(R.id.text_debug2);
	
		//ListView�̐ݒ�
		chatListArrayAdapter = new ArrayAdapter<String>(this, R.layout.message, chatList);
		this.setListAdapter(chatListArrayAdapter);
		
		sendButton = (Button)this.findViewById(R.id.btn_send);
		editmessage = (EditText)this.findViewById(R.id.edit_message);
	
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(PollingService.INTENT_CHATMESSAGE);
		this.registerReceiver(broadcastReceiver, intentFilter);
		
		
		//�m�[�e�B�t�B�P�[�V��������N���������ǂ���
		boolean receiver = receivedIntent.getBooleanExtra("RECEIVER", false);
		if(receiver){
			//���̎��_�Ŏ�����ID���������Ă��炸�A�Z�b�V�������ێ�����Ă��邱�Ƃ����҂��Ă���
			//�_���������ꍇ�͑��̕��@�ł�����ID��ǉ�����
			RequestSender sender = new RequestSender(URL);
			sender.addVeluePair("ACTIONID", "40");
			FutureTask<String> future = sender.executeQueryAsCallable();
			String ret= "";
			try {
				ret = future.get();
			} catch (InterruptedException e) { 
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!ret.equals("0")){ //�[���̓`���b�g�����B���̑��͎��s�Ȃ̂ŏI��
				AlertUtil.showToast("����ɂ���ă`���b�g���҂��L�����Z������܂���", context);
				finish();
			}
			
			
			if(this.pollingService == null){
				pollingBind();				
			}else{
				//this.pollingService.setState(POLLING_STATE.CHAT);
			}
//			AlertUtil.showAlert(sender.getResponseText(), context);
		}
		
		
		sendButton.setOnClickListener(new OnClickListener(){
		
	
			
			@Override
			public void onClick(View v) {
				doPost();
			}
		});
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
        this.unregisterReceiver(broadcastReceiver);
        if(pollingService != null){
        	this.unbindService(serviceConnection);
        	if(GlobalData.D){Log.d("ChatWindowActivity","destroy:unbind service");}
        }

	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		boolean a;
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				new AlertDialog.Builder(context).setTitle("�ޏo").setMessage("�`���b�g���I�����܂�")
				.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						RequestSender finishSender = new RequestSender(URL);
						finishSender.addVeluePair("ACTIONID", "51");
						finishSender.executeQueryAsThread();
//						AlertUtil.showToast(res, context);
						finish();
					}
				}).setNegativeButton("NO", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//do nothing
					}
				}).show();
				return false;
			}
		}
		return super.dispatchKeyEvent(event);
		
	}
	/**
	 * ���b�Z�[�W���M
	 * @return
	 */
	public void doPost() {
		String message = editmessage.getText().toString();
		if (!message.equals("")) {
			RequestSender mesSender = new RequestSender(URL);
			mesSender.addVeluePair("ACTIONID", "50");
			mesSender.addVeluePair("MESSAGE", message);
			// mesSender.addVeluePair("NAME", "noname");

			// String res = mesSender.executeQuery();
			FutureTask<String> future = mesSender.executeQueryAsCallable();
			String response;
			try {
				response = future.get();
				if (mesSender.checkResponse(response, context)) {

					// ���b�Z�[�W�𑗂�Ɠ����ɂ߁[���[����M���s��
					if (response != null && !response.equals("")) {
						Intent intent = new Intent();
						intent.setAction(PollingService.INTENT_CHATMESSAGE);
						intent.putExtra(PollingService.INTENT_CHATMESSAGE,
								response);
						sendBroadcast(intent);
					}
				}
			} catch (InterruptedException ee) {
				ee.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			// }
			// if(pollingService != null){
			// pollingService.chatPoll();
			// }
			editmessage.setText("");
		}

	}

	/**
	 * ��M�������b�Z�[�W����͂���
	 * �`���b�g�ɂ̓A�N�V����ID,���O���b�Z�[�W���X�g�ƃ��[�U���X�g�����񂳂��
	 * @param responseData
	 */
	private void analyzeResponseText(String responseData) {
		//����΂�
		
		//name,mes#name,mes....
//		AlertUtil.showAlert(responseData, this);
		Log.i("ChatWindowActivity", "ResponseData:" + responseData);
		
		String[] datam = responseData.split("#");
		for(String data : datam){
			String[] recode = data.split(",");
			String name = recode[0];
			String mes = recode[1];
			chatList.add(name + ":" + mes);

		}
		
		if(this.chatListArrayAdapter != null){
			this.chatListArrayAdapter.notifyDataSetChanged();
		}
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
			pollingService = ((PollingService.PollingBinder)service).getService();
			//pollingService.setState(POLLING_STATE.CHAT);
		}
		@Override
		public void onServiceDisconnected(ComponentName className){
			AlertUtil.showAlert("UnBind!!", context);
			pollingService = null;
		}
	};

	ChatBroadCastReceiver broadcastReceiver = new ChatBroadCastReceiver();
	//-----BroadCastReceiver�֘A
	class ChatBroadCastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String mes = intent.getStringExtra(PollingService.INTENT_CHATMESSAGE);
			analyzeResponseText(mes);
			
//			AlertUtil.showToast(mes, context);
//			finish();
		}
		
	}


}
