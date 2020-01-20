package jp.stressfreesoft.app.chat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
import jp.stressfreesoft.app.chat.client.GlobalHttpClient;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ChatWindowActivity extends ListActivity {
	public static final String ENTERCHATROOM = "http://stress-free.sakura.ne.jp/chatroom.php"; 
	private String userID;
	private String responseText;
	
	private List<String> chatList = new ArrayList<String>();
    private ArrayAdapter<String> chatListArrayAdapter;
    
	
	TextView textDebug;
	EditText editmessage;
	Button sendButton; 
	ListView chatListView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatlayout);
		
		//Intentに渡されたデータうけとる
		Intent receivedIntent = this.getIntent();
		userID = receivedIntent.getStringExtra("ID");
		AlertUtil.showToast("userID" + userID , this);
		
		analyzeResponseText(receivedIntent.getStringExtra("Data"));
//		textDebug = (TextView)this.findViewById(R.id.text_debug2);
	
		//ListViewの設定
		chatListArrayAdapter = new ArrayAdapter<String>(this, R.layout.message, chatList);
		this.setListAdapter(chatListArrayAdapter);
		
		sendButton = (Button)this.findViewById(R.id.btn_send);
		editmessage = (EditText)this.findViewById(R.id.edit_message);
		
	
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
	}
	
	
	/**
	 * メッセージ送信
	 * @return
	 */
	public String doPost() {
		try {
//			Log.i("debug", "post start");
			HttpPost method = new HttpPost(ENTERCHATROOM);

//			method.set
			DefaultHttpClient client = GlobalHttpClient.getInstance();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("ACTIONID", String.valueOf(2)));
			
			String message = editmessage.getText().toString();
			nameValuePairs.add(new BasicNameValuePair("MESSAGE", message));
						
			method.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
 
			HttpResponse response = client.execute(method);
			int status = response.getStatusLine().getStatusCode();
			if (status != HttpStatus.SC_OK){
				Log.w("ChatWindowActivity", "Status is not OK:" + HttpStatus.SC_OK);
			}else{
				Log.i("ChatWindowActivity", "Status is OK");
				
			}
//				throw new Exception("");
 
			responseText = EntityUtils.toString(response.getEntity(), "UTF-8");
			AlertUtil.showToast(responseText, this);
			
//			textview.refreshDrawableState();
//			Log.i("debug", EntityUtils
//					.toString(response.getEntity(), "UTF-8"));
			
			return null;
		} catch (Exception e) {
			Log.i("debug", "exception" + e.toString());
			return null;
		}
	}

	/**
	 * 受信したメッセージを解析する
	 * チャットにはアクションID,ログメッセージリストとユーザリストが羅列される
	 * @param responseData
	 */
	private void analyzeResponseText(String responseData) {
		//がんばる
		
		String[] div = responseData.split(";");
		String logs = div[0];
		String userlists = div[1];
		
		String logArray[] = logs.split(",");
		//先頭はアクションID
		for(int i = 1;i < logArray.length; i += 2){
			String username = logArray[i];
			String text = logArray[i+1];
			chatList.add(username + ":" + text);
		}
		if(this.chatListArrayAdapter != null){
			this.chatListArrayAdapter.notifyDataSetChanged();
		}
	}

}
