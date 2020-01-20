package jp.stressfreesoft.app.chat;

import jp.rsooo.app.lib.alert.AlertUtil;
import jp.stressfreesoft.app.chat.client.RequestSender;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * このアプリの中で招待処理など独立してそうな処理をここに書く
 * @author akira
 *
 */
public class CommonQuery {
	//public static final String LOGIN2 = "http://stress-free.sakura.ne.jp/login2.php";
	public static String CHATURL = GlobalData.CHATROOMPHP; //"http://stress-free.sakura.ne.jp/chatroom2.php";
	
	
	Context context;
	
	ProgressDialog waitDialog;
	private String invitingId;
	
	
	public CommonQuery(Context context){
		this.context = context;
	}
	
	public void showWait(final String invitingId){
		
		//ダイアログ使いまわすととまるので毎回生成
		waitDialog = new ProgressDialog(context){
				@Override
				public void onBackPressed(){
					super.onBackPressed();
					cancelInvite(invitingId);
//					AlertUtil.showToast("back", context);
				}
		};
		waitDialog.setMessage("チャット相手を招待中");
		waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		waitDialog.show();
	}
	 
	public void dismiss(){
		if(waitDialog != null){
			waitDialog.dismiss();
		}
	}

	private void cancelInvite(String invId){
		assert invId != null : "InvitingId is null!";
		
		//pollingService.setState(POLLING_STATE.WAIT);
		RequestSender sender = new RequestSender(CHATURL);
		sender.addVeluePair("ACTIONID", "31");
		sender.addVeluePair("INVITINGID", invId);
		sender.executeQueryAsThread();
//		AlertUtil.showAlert(sender.getResponseText(), context);
	}
	public void setInvitingId(String invitingId) {
		this.invitingId = invitingId;
	}


}
