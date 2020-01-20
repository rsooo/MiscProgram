package jp.stressfreesoft.app.chat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import jp.rsooo.app.lib.alert.AlertUtil;
import jp.stressfreesoft.app.chat.client.RequestSender;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 設定を行うアクティビティ
 * @author akira
 *
 */
public class SettingActivity extends Activity {

	public static final String ENTERCHATROOM = GlobalData.CHATROOMPHP;//"http://stress-free.sakura.ne.jp/chatroom2.php"; 

	Button changePasswdButton;
	CheckBox inviteCheckBox;
	CheckBox publishLocationCheckBox;

	
	Context context;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.settinglayout);
        context = this;

        inviteCheckBox = (CheckBox)this.findViewById(R.id.chechbox_invite);
		publishLocationCheckBox = (CheckBox)this.findViewById(R.id.chechbox_publishlocation);
        changePasswdButton = (Button)this.findViewById(R.id.btn_changepasswd);
        changePasswdButton.setOnClickListener(new OnClickListener(){
        
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ChangePassWordActivity.class);
				startActivity(intent);
			}
        	
        });
        
        inviteCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
    		
    		@Override
    		public void onCheckedChanged(CompoundButton buttonView,
    				boolean isChecked) {
    				
    				if(isChecked){ 
    					//INVITE許可
    					RequestSender sender = new RequestSender(ENTERCHATROOM); 
    					sender.addVeluePair("ACTIONID", "7");
//    					FutureTask<String> future = sender.executeQueryAsCallable();
    					sender.executeQueryAsThread();
//    					try {
//    						AlertUtil.showToast(future.get(), context);
//    					} catch (InterruptedException e) {
//    						e.printStackTrace();
//    					} catch (ExecutionException e) {
//    						e.printStackTrace();
//    					}
    				}else{
    					//INVITE拒否
    					RequestSender sender = new RequestSender(ENTERCHATROOM);
    					sender.addVeluePair("ACTIONID", "6");
    					sender.executeQueryAsThread();
//    					FutureTask<String> future = sender.executeQueryAsCallable();
//    					sender.executeQueryAsThread();
//    					try {
//    						AlertUtil.showToast(future.get(), context);
//    					} catch (InterruptedException e) {
//    						e.printStackTrace();
//    					} catch (ExecutionException e) {
//    						e.printStackTrace();
//    					}
    				}
    		}
    		
    	});
    	
    	publishLocationCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

    		@Override
    		public void onCheckedChanged(CompoundButton buttonView,
    				boolean isChecked) {
    			if(isChecked){
    				//位置情報公開許可
    				RequestSender sender = new RequestSender(ENTERCHATROOM); 
    				sender.addVeluePair("ACTIONID", "12");
//    				FutureTask<String> future = sender.executeQueryAsCallable();
    				sender.executeQueryAsThread();
//    				try {
//    					AlertUtil.showToast(future.get(), context);
//    				} catch (InterruptedException e) {
//    					e.printStackTrace();
//    				} catch (ExecutionException e) {
//    					e.printStackTrace();
//    				}
    			}else{
    				//位置情報公開拒否
    				RequestSender sender = new RequestSender(ENTERCHATROOM);
    				sender.addVeluePair("ACTIONID", "11");
    				sender.executeQueryAsThread();
//    				FutureTask<String> future = sender.executeQueryAsCallable();
    				sender.executeQueryAsThread();
//    				try {
//    					AlertUtil.showToast(future.get(), context);
//    				} catch (InterruptedException e) {
//    					e.printStackTrace();
//    				} catch (ExecutionException e) {
//    					e.printStackTrace();
//    				}
    			}

    		}
    	});

	}
	
	
	
	
	
	
	
}
