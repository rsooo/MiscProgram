package jp.stressfreesoft.app.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import jp.rsooo.app.lib.alert.AlertUtil;
import jp.stressfreesoft.app.chat.client.RequestSender;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * ���[���𑗂��ă��[�U�A�J�E���g���쐬���邽�߂̃A�N�e�B�r�e�B
 * @author akira
 *
 */
public class SignUpSendMailActivity extends Activity{
	//���O�C�������͂���������΂�
	public static final String POSTURL = GlobalData.LOGINPHP; //"http://stress-free.sakura.ne.jp/login2.php";

	Button signupButtion;
	Button getMailAdressButton;
	EditText nameEdit; 
	EditText mailEdit; 
	EditText nicknameEdit;
//	RadioGroup sexRadiog;
	RadioButton maleRadioButton;
	RadioButton femaleRadioButton;
	RadioButton unknownRadioButton;
//	EditText mailEdit2; 
	Context context;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registlayout);
		context = this;
		
		signupButtion = (Button)this.findViewById(R.id.btn_execsignup2);
		getMailAdressButton = (Button)this.findViewById(R.id.btn_getmailaddress);
		mailEdit = (EditText)this.findViewById(R.id.mail);
//		mailEdit2 = (EditText)this.findViewById(R.id.mail2);
		nameEdit = (EditText)this.findViewById(R.id.newusername2);
		nicknameEdit = (EditText)this.findViewById(R.id.nickname);
//		sexRadiog = (RadioGroup)this.findViewById(R.id.radiog_sex);
		maleRadioButton = (RadioButton)this.findViewById(R.id.radiobtn_male);
		femaleRadioButton = (RadioButton)this.findViewById(R.id.radiobtn_female);
		unknownRadioButton = (RadioButton)this.findViewById(R.id.radiobtn_unknown);
		this.setResult(RESULT_CANCELED);
		signupButtion.setOnClickListener(new OnClickListener(){
		
			@Override
			public void onClick(View v) {
				final String u1 = nameEdit.getText().toString();
				final String t1 = mailEdit.getText().toString();
//				String t2 = mailEdit2.getText().toString();
				String n1 = nicknameEdit.getText().toString();
				String sex = getSexText();
				
				
				if(u1.equals("")){
					AlertUtil.showToast("���[�U������͂��Ă�������", context);
				}else if(t1.equals("")){
					AlertUtil.showToast("���[���A�h���X����͂��Ă�������", context);
				}else if(n1.equals("")){
					AlertUtil.showToast("�j�b�N�l�[������͂��Ă�������", context);
				}/*else if(!t1.equals(t2)){
					AlertUtil.showToast("���[���A�h���X����v���܂���", context);
				}*/else if(!u1.matches("\\w+")){
					AlertUtil.showToast("���[�U���ɕs���ȋL�����g�p����Ă��܂�", context);				
				}//else if(!t1.matches("(\\w|\\.|@|-|_)+")){
				else if(!t1.matches("[\\w\\.\\-]+@(?:[\\w\\-]+\\.)+[\\w\\-]+")){
					AlertUtil.showToast("���[���A�h���X������������܂���", context);				
				}else{
					//���������폈��
					RequestSender sender = new RequestSender(POSTURL);
					sender.addVeluePair("ACTIONID", "2");
					sender.addVeluePair("NAME", u1);
					sender.addVeluePair("MAIL", t1);
					sender.addVeluePair("NICKNAME", n1);
					sender.addVeluePair("SEX", sex);
					
//					FutureTask<String> future = sender.executeQueryAsCallable();
					sender.executeQueryAsAsyncTask(context, new QueryResponseReceiver() {
						
						@Override
						public void analyzeResponse(String ret) {
							// TODO Auto-generated method stub
							if(ret.equals("0")){
								AlertUtil.showToast("���͂��ꂽ���[���A�h���X�ɃA�J�E���g���𑗐M���܂���", context);
								Intent retIntent = new Intent(); 
								retIntent.putExtra("NAME", u1);
								retIntent.putExtra("MAIL", t1);
//								retIntent.putExtra("NICKNAME", n1);
//								retIntent.putExtra("SEX", t1);
								setResult(RESULT_OK, retIntent);
								finish();
							}else if(ret.equals("1")){
								AlertUtil.showToast("���ɓ������[�U�����o�^����Ă��܂��B���̃��[�UID���g�p���Ă�������", context);
							}else if(ret.equals("2")){
								AlertUtil.showToast("���Ɏw�肵�����[���A�h���X�͎g�p����Ă��܂��B", context);
							}else{
								AlertUtil.showToast(ret, context);
//								AlertUtil.showToast("�w�肵�����[���A�h���X�ɓo�^���[���𑗐M���܂����B", context);					
							}
						}
					}, "�������E�E�E");					
				}
			}

			private String getSexText() {
				if(maleRadioButton.isChecked()){
					return ESEX.MALE.toString();
				}else if(femaleRadioButton.isChecked()){
					return ESEX.FEMALE.toString();
				}else if(unknownRadioButton.isChecked()){
					return ESEX.UNKNOWN.toString();
				}
				Log.w("SingUpSendMailActivity", "WARM; unknown state in selecting sex");
				return ESEX.UNKNOWN.toString();
			}

		});
		
		getMailAdressButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				final List<String> addressList = getAccountTest();
				if(addressList == null || addressList.isEmpty()){
					AlertUtil.showToast("�A�J�E���g��񂪌�����܂���", context);
				}else{
					new AlertDialog.Builder(context).setTitle("�A�J�E���g�̑I��")
					.setItems(addressList.toArray(new CharSequence[addressList.size()]), new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							mailEdit.setText(addressList.get(which));
						}

				
						
					}).show();
					
				}
			}
			
			/**
			 * �A�J�E���g��񂩂烁�[���A�h���X���擾
			 * @return
			 */
			public List<String> getAccountTest(){
				ArrayList<String> addressList = new ArrayList<String>();
				Account[] accounts = AccountManager.get(context).getAccounts();
				for(Account a : accounts){
					String name = a.name;
//					String type = a.type;
//					AlertUtil.showToast("name:" + name + ", type:" + type, context);
					if(name.matches("[\\w\\.\\-]+@(?:[\\w\\-]+\\.)+[\\w\\-]+")){
						addressList.add(name);
					}
				}
				return addressList;
			}

		});

	}
	
}
