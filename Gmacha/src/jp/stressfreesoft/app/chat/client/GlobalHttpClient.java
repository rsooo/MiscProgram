package jp.stressfreesoft.app.chat.client;

import org.apache.http.impl.client.DefaultHttpClient;

public class GlobalHttpClient {
	private GlobalHttpClient(){}
	
	public static DefaultHttpClient getInstance(){
		 if(client == null){
			 client = new DefaultHttpClient(); 
		 }
		 return client;
	}
	
	public static DefaultHttpClient client = null;
}
