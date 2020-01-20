package jp.stressfreesoft.app.chat;

import jp.rsooo.app.lib.alert.AlertUtil;
import jp.stressfreesoft.app.chat.client.RequestSender;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 新規登録を行うアクティビティ
 * !!!現在使用していない
 * @author akira
 *
 */
public class SignUpActivity extends Activity{
	//ログイン処理はここががんばる
	public static final String POSTURL = GlobalData.LOGINPHP; //"http://stress-free.sakura.ne.jp/login2.php";

//	public static final int RESULT_NOCREATE = 0;
//	public static final int RESULT_ACCREATED = 1;

	Button signupButtion;
	EditText nameEdit; 
	EditText passEdit; 
	EditText passEdit2; 
	Context context;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registlayout);
		context = this;
		
		signupButtion = (Button)this.findViewById(R.id.btn_execsignup);
		passEdit = (EditText)this.findViewById(R.id.newpass);
		passEdit2 = (EditText)this.findViewById(R.id.newpass2);
		nameEdit = (EditText)this.findViewById(R.id.newusername);

		this.setResult(RESULT_CANCELED);
		signupButtion.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String u1 = nameEdit.getText().toString();
				String t1 = passEdit.getText().toString();
				String t2 = passEdit2.getText().toString();
				
				if(u1.equals("")){
					AlertUtil.showToast("ユーザ名を入力してください", context);
				}else if(t1.equals("")){
					AlertUtil.showToast("パスワードを入力してください", context);
				}else if(!t1.equals(t2)){
					AlertUtil.showToast("パスワードが一致しません", context);
				}else if(!u1.matches("\\w+")){
					AlertUtil.showToast("ユーザ名に不正な記号が使用されています", context);				
				}else if(!t1.matches("\\w+")){
					AlertUtil.showToast("パスワードに不正な記号が使用されています", context);				
				}else{
					//ここが正常処理
					RequestSender sender = new RequestSender(POSTURL);
					sender.addVeluePair("ACTIONID", "1");
					sender.addVeluePair("NAME", u1);
					sender.addVeluePair("PASS", t1);
					String ret = sender.executeQuery();
					if(ret.equals("0")){
						AlertUtil.showToast("アカウントを作成しました", context);
						Intent retIntent = new Intent();
						retIntent.putExtra("NAME", u1);
						retIntent.putExtra("PASS", t1);
						setResult(RESULT_OK, retIntent);
						finish();
					}else if(ret.equals("1")){
						AlertUtil.showToast("既に同じユーザ名が登録されています。他のユーザIDを使用してください", context);
					}else{
						AlertUtil.showToast(ret, context);
					}
//					AlertUtil.showToast(ret, context);
					
				}
				
						
			}
			
		});
	}
}
