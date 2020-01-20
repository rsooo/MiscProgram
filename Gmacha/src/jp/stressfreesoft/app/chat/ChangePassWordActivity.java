package jp.stressfreesoft.app.chat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import jp.rsooo.app.lib.alert.AlertUtil;
import jp.stressfreesoft.app.chat.client.RequestSender;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class ChangePassWordActivity extends Activity {

		public static final String POSTURL = GlobalData.LOGINPHP;//"http://stress-free.sakura.ne.jp/login2.php";
		Button execButton;
		EditText userNameEditText;
		EditText currentPassEditText;
		EditText newPassEditText;
		EditText newPassEditText2; //�m�F�p
		Context context;
		
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.setContentView(R.layout.changepasswordlayout);
	        context = this;
	        
	        userNameEditText = (EditText)this.findViewById(R.id.changepassusername);
	        currentPassEditText = (EditText)this.findViewById(R.id.currentpass);
	        newPassEditText = (EditText)this.findViewById(R.id.change_newpass1);
	        newPassEditText2 = (EditText)this.findViewById(R.id.change_newpass2);
	        
	        execButton = (Button)this.findViewById(R.id.btn_execchangepass);	        
	        userNameEditText.setText(GlobalData.userName);
	        
	        execButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					String c1 = currentPassEditText.getText().toString();
					String t1 = newPassEditText.getText().toString();
					String t2 = newPassEditText2.getText().toString();
					
					if(c1.equals("") || t1.equals("") || t2.equals("")){
						AlertUtil.showToast("�p�X���[�h����͂��Ă�������", context);
					}else if(!t1.equals(t2)){
						AlertUtil.showToast("�p�X���[�h����v���܂���", context);
					}else if(!t1.matches("\\w{4,}")){
						AlertUtil.showToast("�p�X���[�h���Z�����܂�", context);				
					}else if(!t1.matches("\\w+")){
					AlertUtil.showToast("�p�X���[�h�Ɏg�p�ł��Ȃ��L�����g�p����Ă��܂�", context);
				} else if (!t1.matches("\\w+")) {
					AlertUtil.showToast("�p�X���[�h�Ɏg�p�ł��Ȃ��L�����g�p����Ă��܂�", context);
				} else {
					RequestSender sender = new RequestSender(POSTURL);
					sender.addVeluePair("ACTIONID", "10");
					sender.addVeluePair("CURRENTPASS", c1);
					sender.addVeluePair("NEWPASS", t1);
					// �Z�b�V�����ɖ�肠��Ȃ烆�[�U�l�[�����|�X�g���Ă���

					FutureTask<String> result = sender.executeQueryAsCallable();
					try {
						String ret = result.get();
						if (sender.checkResponse(ret, context)) {
							if (ret.equals("0")) {
								AlertUtil.showToast("�p�X���[�h���ύX����܂���", context);
								finish();
							} else if (ret.equals("1")) {
								AlertUtil
										.showToast(
												"���݂̃p�X���[�h���o�^����Ă���p�X���[�h�ƈ�v���܂���",
												context);
							} else {
								AlertUtil.showToast("�G���[�B�Ǘ��҂ɖ₢���킹�ĉ�����",
										context);
							}
						}

					} catch (InterruptedException e) {
						e.printStackTrace(); // ����͂܂����H
					} catch (ExecutionException e) {
						e.printStackTrace();
					}

				}
			}

		});
	}

}
