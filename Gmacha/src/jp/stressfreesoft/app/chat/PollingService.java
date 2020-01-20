package jp.stressfreesoft.app.chat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import jp.rsooo.app.lib.alert.AlertUtil;
import jp.stressfreesoft.app.chat.client.RequestSender;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

public class PollingService extends Service {
	public static final String INTENT_ACCEPTED = "jp.rsooo.app.chat.ACCEPTED";
	public static final String INTENT_CHATMESSAGE = "jp.rsooo.app.chat.CHATMESSAGE";

	enum POLLING_STATE {
		WAIT("WAIT"), NOINVITE("NOINVITE"), INVITING("INVITING"), CHAT("CHAT"), DISABLE(
				"DISABLE"), INVITED("INVITED");

		private String str;

		private POLLING_STATE(String s) {
			str = s;
		}

		@Override
		public String toString() {
			return str;
		}
	}

	// private POLLING_STATE state = POLLING_STATE.WAIT;

	public static final int INTERVAL = 10000; // 60 * 1000; //ポーリングのインターバル
	public static final String URL = GlobalData.CHATROOMPHP; //"http://stress-free.sakura.ne.jp/chatroom2.php";
	Context context;
	// String test;
	private Handler handler = new Handler();

	private String invitingId = null;

	// public void handleMessage(Message msg){
	// AlertUtil.showToast(test, context);
	// }

	/**
	 * トーストを出力するスレッド
	 */
	public class MessageThread extends Thread {
		private final String mes;
		private final Context context;

		public MessageThread(String mes, Context context) {
			this.mes = mes;
			this.context = context;
		}

		@Override
		public void run() {
			AlertUtil.showToast(mes, context);
		}
	}

	public void sendMessage(final String mes) {
		Thread showToast = new Thread() {
			@Override
			public void run() {
				AlertUtil.showToast(mes, context);
			}
		};
		handler.post(showToast);
		return;
	}

//	private MessageThread inveteMessageThread;
	// private MessageThread loginErrorMessageThread;

	// private boolean enable = true;
	PollThread pollThread = new PollThread();

	private RequestSender chatSender;
	private RequestSender pollingSender;
	private RequestSender pollingSenderWithUpdate;

	public void stopThisService() {
		pollThread.stopService();
	}

	class PollThread extends Thread {

		// private RequestSender sender;
		private RequestSender invitingSender;

		/**
		 * 招待者のIDを追加する
		 * 
		 * @param invitingId
		 */
		public void addValueToInvitingSender(String invitingId) {
			invitingSender.addVeluePair("INVITINGID", invitingId);
		}

		public PollThread() {
			// sender = new RequestSender(URL);
			// sender.addVeluePair("ACTIONID", "20");
			invitingSender = new RequestSender(URL);
			invitingSender.addVeluePair("ACTIONID", "21");
			chatSender = new RequestSender(URL);
			chatSender.addVeluePair("ACTIONID", "22");
			pollingSender = new RequestSender(URL);
			pollingSender.addVeluePair("ACTIONID", "29");
			pollingSenderWithUpdate = new RequestSender(URL);
			pollingSenderWithUpdate.addVeluePair("ACTIONID", "29");
			pollingSenderWithUpdate.addVeluePair("UPDATETIME", "1"); //時刻更新フラグをセット
			
		}

		// ノーティフィケーションが出たかどうか。INVITED状態に連続でノーティフィケーションが出るのを防ぐその場しのぎ
		private boolean presentedNotification = false;

		boolean enable = true;
		
		//時刻更新を行うポーリング周期をカウント
		int accessUpdateCount = 0;
		public void run() {
			try {
				Thread.sleep(INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (enable) {
				
				String ret = "";
				
				try {
					if(accessUpdateCount++ > 10){
						FutureTask<String> future = pollingSenderWithUpdate.executeQueryAsCallable();
						ret = future.get();
						accessUpdateCount = 0;
					}else{
						FutureTask<String> future = pollingSender.executeQueryAsCallable();
						ret = future.get();
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					e1.printStackTrace();
				}
				if(ret.equals(GlobalData.SESSION_ERROR_CODE)){
					Log.e("PollingService", "SESSION ERROR OCCURED");
					stopService();
				} else {
					String[] data = ret.split("#");

					if (GlobalData.D) {
						sendMessage(ret);
					}
					// if(data.length < 2){
					// sendMessage("polling error");
					// return;
					// }

					// data[0]がstatusの種類, data[1]が出力結果表す
					if (data[0].equals(POLLING_STATE.WAIT.toString())) {
						// final String response = data[1];
						if (presentedNotification) {
							notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
							notificationManager.cancel(0); // 通知キャンセル
							presentedNotification = false;
						}
					} else if (data[0]
							.equals(POLLING_STATE.INVITING.toString())) {
						presentedNotification = false;
						final String response = data[1];
						// 招待完了した場合は"CHAT"が返される
						if (response.equals("CHAT")) {
							Intent intent = new Intent();
							intent.setAction(INTENT_ACCEPTED);
							RequestSender acceptedSender = new RequestSender(
									URL);
							acceptedSender.addVeluePair("ACTIONID", "41");
							acceptedSender.addVeluePair("INVITINGID",
									invitingId);
							acceptedSender.executeQuery();
							// state = POLLING_STATE.CHAT;
							sendBroadcast(intent);
						} else if (response.equals("INVITING")) {
							// 招待を継続
						} else {
							// 拒否かエラー
						}
					} else if (data[0].equals(POLLING_STATE.INVITED.toString())) {
						// 初回のみノーティフィケーション表示
						if (!presentedNotification) {
							showNotification();
							executeVibration();
							// handler.post(inveteMessageThread);
							presentedNotification = true;
						}
					} else if (data[0].equals(POLLING_STATE.CHAT.toString())) {
						chatPoll();
						presentedNotification = false;
					} else {
						sendMessage("エラーが発生しました。ログインしなおして下さい");
						stopService();
					}
					try {
						Thread.sleep(INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		private void executeVibration() {
			final Vibrator vib;
//			final long pattern[] = {2000};
			vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
			if(vib != null){
				vib.vibrate(2500);
			}
			
		}

		// このサービスを停止する
		public void stopService() {
			enable = false;
			stopSelf();
		}

		private NotificationManager notificationManager;;

		private void showNotification() {
			notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Intent intent = new Intent(getApplicationContext(),
					ChatWindowActivity.class/* LatestEventActivity.class */);
			intent.putExtra("RECEIVER", true);

			Notification n = new Notification(R.drawable.icon, "チャットに招待されました",
					System.currentTimeMillis());
			PendingIntent p = PendingIntent.getActivity(
					getApplicationContext(), 0, intent, /*
														 * Intent.FLAG_ACTIVITY_NEW_TASK
														 */0);
			n.setLatestEventInfo(PollingService.this, "G-macha", "チャットに招待されました", p);

			n.flags = Notification.FLAG_AUTO_CANCEL;
			notificationManager.notify(0, n);

		}
	}

	// 今すぐPollingする。多分同期とってないからｙばい
	public void chatPoll() {
		final String response = chatSender.executeQuery();
		// メッセージをブロードキャスト
		if (response != null && !response.equals("")) {
			Intent intent = new Intent();
			intent.setAction(INTENT_CHATMESSAGE);
			intent.putExtra(INTENT_CHATMESSAGE, response);
			sendBroadcast(intent);
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;

		// handler.postDelayed(pollThread, 5000);

		pollThread.start();
		// pollThread.run(); すれっどじゃなきゃだめ

//		inveteMessageThread = new MessageThread("invite", context);
		// loginErrorMessageThread = new MessageThread("login error", context);

	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		// while (enable) {
		// try {
		// Thread.sleep(INTERVAL);
		// RequestSender sender = new RequestSender(URL);
		// sender.addVeluePair("ACTIONID", "20");
		// sender.executeQuery();
		// String response = sender.getResponseText();
		// if (response.equals("invite")) {
		// // showNotification();
		// // handler.post(showToast);
		// AlertUtil.showToast("invite", context);
		//
		// } else if (response.equals("noinvite")) {
		//
		// } else {
		// /*Thread showToast = new Thread() {
		// @Override
		// public void run() {
		// AlertUtil.showToast("login error", context);
		// }
		// };*/
		// //handler.post(showToast);
		// // context.stopService(new Intent(this.))
		// PollingService.this.stopSelf();
		// enable = false;
		// }
		// // AlertUtil.showToast(sender.getResponseText(), context);
		//
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new PollingBinder();
	}

	public class PollingBinder extends Binder {
		// @Override
		public PollingService getService() {
			return PollingService.this;
		}
	}

	/*
	 * public void setState(POLLING_STATE state) { this.state = state; }
	 */

	public void changeInviteState(String invitingId) {
		// スレッドの競合とかしない？？
		this.invitingId = invitingId;
		pollThread.addValueToInvitingSender(invitingId);
		// this.state = POLLING_STATE.INVITING;

	}

	// @Override
	/*
	 * public void _run() { while (true) { switch (state) { case WAIT: try {
	 * Thread.sleep(INTERVAL); sender.executeQuery(); final String response =
	 * sender.getResponseText(); if (response.equals("invite")) {
	 * showNotification(); handler.post(inveteMessageThread); } else if
	 * (response.equals("noinvite")) {
	 * 
	 * } else { Thread showToast = new Thread() {
	 * 
	 * @Override public void run() { AlertUtil.showToast("login error",
	 * context); } }; handler.post(showToast); PollingService.this.stopSelf();
	 * // enable = false; } } catch (InterruptedException e) {
	 * e.printStackTrace(); } break;
	 * 
	 * case INVITING: assert invitingId != null : "invitngId is null!"; try {
	 * Thread.sleep(INTERVAL); invitingSender.executeQuery(); String response =
	 * invitingSender.getResponseText(); if(response.equals("CHAT")){ // Thread
	 * showToast = new Thread() { // @Override // public void run() { //
	 * AlertUtil.showToast("chat", context); // } // }; //
	 * handler.post(showToast); Intent intent = new Intent();
	 * intent.setAction(INTENT_ACCEPTED); RequestSender acceptedSender = new
	 * RequestSender(URL); acceptedSender.addVeluePair("ACTIONID", "41");
	 * acceptedSender.addVeluePair("INVITINGID", invitingId);
	 * acceptedSender.executeQuery(); state = POLLING_STATE.CHAT;
	 * 
	 * sendBroadcast(intent);
	 * 
	 * }else if(response.equals("INVITED")){
	 * 
	 * }else{ Thread showToast = new Thread() {
	 * 
	 * @Override public void run() { AlertUtil.showToast("invite error",
	 * context); } }; handler.post(showToast); } } catch (InterruptedException
	 * e) { e.printStackTrace(); } break;
	 * 
	 * case CHAT: try { Thread.sleep(INTERVAL); } catch (InterruptedException e)
	 * { e.printStackTrace(); } chatPoll(); break; }
	 * 
	 * } }
	 */

}
