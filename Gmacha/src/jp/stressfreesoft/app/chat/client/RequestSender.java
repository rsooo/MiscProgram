package jp.stressfreesoft.app.chat.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;

import jp.rsooo.app.lib.alert.AlertUtil;
import jp.stressfreesoft.app.chat.GlobalData;
import jp.stressfreesoft.app.chat.LoginActivity;
import jp.stressfreesoft.app.chat.QueryResponseReceiver;
import jp.stressfreesoft.app.chat.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

/**
 * HTTPリクエストを送るクラス
 * 
 * @author akira 
 * 
 */
public class RequestSender {
	private final String url;
	private HttpPost method;
	private List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	private String responseText;
	private DefaultHttpClient client;
	
	public RequestSender(String url) {
		this.url = url;
		method = new HttpPost(url);
		client = GlobalHttpClient.getInstance();
	}

	public void addVeluePair(String name, String value) {
		nameValuePairs.add(new BasicNameValuePair(name, value));
	}

//	HttpResponse response;
	public synchronized String executeQuery() {
		
			try{
//			postThread.join();
				method.setEntity(new UrlEncodedFormEntity(nameValuePairs, "shift-jis"));
				HttpResponse response = client.execute(method);				
			responseText = EntityUtils.toString(response.getEntity(),
					"UTF-8");
			}catch(IllegalStateException ee){
				
				ee.printStackTrace();
			}catch(IOException e){ 
				e.printStackTrace();
			}
		return responseText;
	}

	/**
	 * スレッドとして実行する
	 * レスポンスは返さない
	 */
	public synchronized void executeQueryAsThread() {
		
		Thread postThread = new Thread(){
			@Override
			public void run(){
				try{
					method.setEntity(new UrlEncodedFormEntity(nameValuePairs ,"shift-jis"));
					client.execute(method);
				}catch(Exception e){
					e.printStackTrace();
				}
			}			
		};
		final ExecutorService executer = GlobalData.executerService;
		executer.execute(postThread);
//		postThread.start();
	}

	/**
	 * 別スレッドで動かしてテキストのレスポンスを返す
	 * 受け取る側はFutureTaskもらって何とか処理する
	 * @return
	 */
	public  FutureTask<String> executeQueryAsCallable() {
		
		Callable<String> postCallable = new Callable<String>(){
			@Override
			public String call(){
				String ret = "";
				try{
					method.setEntity(new UrlEncodedFormEntity(nameValuePairs ,"shift-jis"));
					HttpResponse resp = client.execute(method);
//					try {
//						Thread.sleep(10000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					ret = EntityUtils.toString(resp.getEntity(),
							"UTF-8");
				}catch(IOException e){ 
					Log.d("RequestSender", "exception");
					e.printStackTrace();
				}
				return ret;
			}
		};


		final ExecutorService scheduler = GlobalData.executerService;
//		final ExecutorService scheduler = Executors.newFixedThreadPool(1);
		
		final FutureTask<String> futureTask = new FutureTask<String>(postCallable);
		scheduler.execute(futureTask);
		return futureTask;
		
	}
	
	public  void executeQueryAsAsyncTask(Context context, QueryResponseReceiver receiver, String waitingText){
		ExecuteQueryTask executeQueryTask = new ExecuteQueryTask(context, receiver, waitingText);
		executeQueryTask.execute(null);
	}

	public  Future<String> executeQueryAsCallableTest(final Context context, final LoginActivity activity) {
		
		final ProgressDialog waitDialog = new ProgressDialog(context);
		final Handler h = new Handler(); 
		 
		Callable<String> postCallable = new Callable<String>(){
			@Override
			public String call(){
				String ret = "";
				try{
					method.setEntity(new UrlEncodedFormEntity(nameValuePairs ,"shift-jis"));
					HttpResponse resp = client.execute(method);
	 				try {
	 					Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ret = EntityUtils.toString(resp.getEntity(),
							"UTF-8");
		 
					h.post( new Runnable(){

						@Override
						public void run() {
							waitDialog.dismiss();
//							activity.analyzeResponseText("0#test");
						}
						
					});
				}catch(IOException e){ 
					Log.d("RequestSender", "exception");
					e.printStackTrace();
				}
				return ret;
			}
		};
			
        waitDialog.setMessage("waiting"); 
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
        h.post(new Runnable(){
			@Override
			public void run() {
		        waitDialog.show(); 						
			}
       	
        });

		final ExecutorService scheduler = GlobalData.executerService;
//		final ExecutorService scheduler = Executors.newFixedThreadPool(1);
		
//		final FutureTask<String> futureTask = new FutureTask<String>(postCallable);
//		scheduler.execute(futureTask);
		Future<String> future = scheduler.submit(postCallable);
		return future;
		
	}

	
	public String getResponseText() {
			return responseText;
	}
	
	
	public class ExecuteQueryTask extends AsyncTask<Void, Integer, String>{

		final Context context;
		final QueryResponseReceiver receiver;
		final String waitingText;
		ProgressDialog progressDialog;
		
		public ExecuteQueryTask(Context context, QueryResponseReceiver receiver, String waitingText){
			this.context = context;
			this.receiver = receiver;
			this.waitingText = waitingText;
		} 
		
		@Override
		protected void onPreExecute() {
	        progressDialog = new ProgressDialog(context){
				@Override
				public void onBackPressed(){
					super.onBackPressed();
					ExecuteQueryTask.this.cancel(true);
				}
	        };
	        progressDialog.setMessage(waitingText); 
	        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        progressDialog.setIndeterminate(false);
	        progressDialog.show();
	    } 

		
		@Override
		protected String doInBackground(Void... params) {
			String ret = "";
			
			try{
				method.setEntity(new UrlEncodedFormEntity(nameValuePairs ,"shift-jis"));
				HttpResponse resp = client.execute(method);
				ret = EntityUtils.toString(resp.getEntity(),
						"UTF-8");
			}catch(IOException e){ 
				Log.d("RequestSender", "exception");
				e.printStackTrace();
			}
			return ret;

		}
		
		
		 @Override
	    protected void onPostExecute(String result) {
		    progressDialog.dismiss();
		    receiver.analyzeResponse(result);
		    //ここでanalyzeText
	    }

		 
	}
	 /**
	  * レスポンスがエラーコード吐いているかチェック
	  * エラーコード吐いていなければtrue 
	  * 別にこのクラスの役割でもないけど一応ここに
	  * @return
	  */
	public boolean checkResponse(final String response, final Context context){
		if(response.equals(GlobalData.SESSION_ERROR_CODE)){
			AlertUtil.showToast(context.getString(R.id.setting_imageview), context);
			return false;
		}
		return true;
	}


}
