package jp.stressfreesoft.app.chat;

import jp.rsooo.app.lib.alert.AlertUtil;
import jp.stressfreesoft.app.chat.client.RequestSender;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

public class GuestLoginActivity extends Activity{

	public static final String POSTURL = GlobalData.LOGINPHP;

	Context context;
	Button loginButton;
	EditText nameEditText;
	RadioButton maleSelectButton;
	RadioButton femaleSelectButton;
	RadioButton uhknownSelectButton;
	GeoLocation geoLocation;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.guestloginlayout);
		context = this;
		loginButton = (Button)this.findViewById(R.id.btn_guestlogin);
		nameEditText = (EditText)this.findViewById(R.id.guestnameedit);
		maleSelectButton = (RadioButton)this.findViewById(R.id.radiobtn_guestlogin_male);
		femaleSelectButton = (RadioButton)this.findViewById(R.id.radiobtn_guestlogin_female);
		uhknownSelectButton = (RadioButton)this.findViewById(R.id.radiobtn_guestlogin_unknown);

		this.setResult(GuestLoginActivity.RESULT_CANCELED);
		this.geoLocation = (GeoLocation) this.getIntent().getSerializableExtra("GeoLocation");
		
        loginButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				String name = nameEditText.getText().toString();
				if(name.equals("")){
					AlertUtil.showToast("名前を入力してください", context);
					return;
				}
				ESEX se = ESEX.UNKNOWN;
				if(maleSelectButton.isChecked()){
					se = ESEX.MALE;
				}else if(femaleSelectButton.isChecked()){
					se = ESEX.FEMALE;
				}
				
				RequestSender sender = new RequestSender(POSTURL);
				sender.addVeluePair("ACTIONID", "30");
				sender.addVeluePair("NAME", name);
				sender.addVeluePair("SEX", se.toString());
				sender.addVeluePair("LATITUDE", String
						.valueOf(geoLocation.latitude));
				sender.addVeluePair("LONGITUDE", String
						.valueOf(geoLocation.longitude));
				sender.addVeluePair("ALTITUDE", String
						.valueOf(geoLocation.altitude));
				sender.addVeluePair("ACCURACY", String
						.valueOf(geoLocation.accuracy));
				sender.addVeluePair("STATUS", "WAIT");

				sender.executeQueryAsAsyncTask(context, new QueryResponseReceiver(){
 
					@Override
					public void analyzeResponse(String response) {
//						AlertUtil.showToast(response, context);
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						final String nickName = nameEditText.getText().toString();
						intent.putExtra("NickName", nickName);
						setResult(GuestLoginActivity.RESULT_OK, intent); //アカウント作成時はOK返す
						finish();
					}
					
				}, "ゲストログイン中・・・");
			}
        	
        });
	}
	
	
} 
