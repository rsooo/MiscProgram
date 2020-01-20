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
 * �V�K�o�^���s���A�N�e�B�r�e�B
 * !!!���ݎg�p���Ă��Ȃ�
 * @author akira
 *
 */
public class SignUpActivity extends Activity{
	//���O�C�������͂���������΂�
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
					AlertUtil.showToast("���[�U������͂��Ă�������", context);
				}else if(t1.equals("")){
					AlertUtil.showToast("�p�X���[�h����͂��Ă�������", context);
				}else if(!t1.equals(t2)){
					AlertUtil.showToast("�p�X���[�h����v���܂���", context);
				}else if(!u1.matches("\\w+")){
					AlertUtil.showToast("���[�U���ɕs���ȋL�����g�p����Ă��܂�", context);				
				}else if(!t1.matches("\\w+")){
					AlertUtil.showToast("�p�X���[�h�ɕs���ȋL�����g�p����Ă��܂�", context);				
				}else{
					//���������폈��
					RequestSender sender = new RequestSender(POSTURL);
					sender.addVeluePair("ACTIONID", "1");
					sender.addVeluePair("NAME", u1);
					sender.addVeluePair("PASS", t1);
					String ret = sender.executeQuery();
					if(ret.equals("0")){
						AlertUtil.showToast("�A�J�E���g���쐬���܂���", context);
						Intent retIntent = new Intent();
						retIntent.putExtra("NAME", u1);
						retIntent.putExtra("PASS", t1);
						setResult(RESULT_OK, retIntent);
						finish();
					}else if(ret.equals("1")){
						AlertUtil.showToast("���ɓ������[�U�����o�^����Ă��܂��B���̃��[�UID���g�p���Ă�������", context);
					}else{
						AlertUtil.showToast(ret, context);
					}
//					AlertUtil.showToast(ret, context);
					
				}
				
						
			}
			
		});
	}
}
