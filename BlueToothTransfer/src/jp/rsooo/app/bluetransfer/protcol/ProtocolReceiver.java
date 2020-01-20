package jp.rsooo.app.bluetransfer.protcol;

import java.util.ArrayList;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import static jp.rsooo.app.bluetransfer.protcol.ProtocolConstants.*;
import java.util.*;

//import jp.rsooo.app.lib.alert.AlertUtil;

/**
 * データを受け取って解析内容を電話帳に登録する
 * @author a-saitoh
 *
 */
public class ProtocolReceiver {

	final Context context;
	List<String> receivedData = new ArrayList<String>();
	
	public ProtocolReceiver(final Context c){
		this.context = c;
	}
	/**
	 * データを受け取って格納する
	 * FINが来てたらtrue返す
	 * @param datam
	 * @return
	 */
	public boolean receiveData(byte[] datam, int count){
		String data = new String(datam, 0, count);
		String[] eachdata = data.split("\n");
		for(String d : eachdata){
			if(readHeader(d).equals(FIN)){
				//受け取り終了
				return true;
			}
			this.receivedData.add(d);
		}
		return false;
	}
	
	/**
	 * 受け取った内容を解析して電話帳データ登録する
	 * とりあえず受信した名前返しとく
	 */
	public String analyzeAndRegistData(){
		String name = "", address, phone;
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		try{
	    ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
    		   .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
	            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)     
	    		.build());
	    
		for(String recode : this.receivedData){
			final String header = readHeader(recode);
			final String data = readData(recode);
			if(header.equals(NAMEHEADER)){
				name = data;
			    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
			            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
			            .withValue(ContactsContract.Data.MIMETYPE,
			                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
			            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
			            .build());	
			}else if(header.equals(PHONEHEADER)){
				phone = data;
				  ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				            .withValue(ContactsContract.Data.MIMETYPE,
				                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
				            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
//				            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, phoneType)
				            .build());				
			}else if(header.equals(ADDRESSHEADER)){
				address = data; 
				   ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				            .withValue(ContactsContract.Data.MIMETYPE,
				                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
				            .withValue(ContactsContract.CommonDataKinds.Email.DATA, address)
//				            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, emailType)
				            .build());
			}else if(header.equals(PH_FAMILYHEADER)){
				String phFamily = data; 
				   ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) //updateにしたらちゃんと登録された？？よくわからん
				            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				            .withValue(ContactsContract.Data.MIMETYPE,
				                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
				            .withValue(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME, phFamily)
//				            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, emailType)
				            .build());
			}else if(header.equals(PH_MIDDLEHEADER)){
				String phMiddle = data;
				   ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				            .withValue(ContactsContract.Data.MIMETYPE,
				                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
				            .withValue(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME, phMiddle)
//				            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, emailType)
				            .build());
			}else if(header.equals(PH_GIVENHEADER)){
				String phGiven = data;
				   ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				            .withValue(ContactsContract.Data.MIMETYPE,
				                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
				            .withValue(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME, phGiven)
//				            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, emailType)
				            .build());
			}else{
				throw new IllegalStateException("unknown header");
			}
		}
	    context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//	    AlertUtil.showAlert("analyzing done", context);
		Log.i("SEND", "sending complete");
		}catch(Exception e){
			Log.e("SEND", "Exception");
			e.printStackTrace();
//			AlertUtil.showAlert("Exception", context);
		}
		return name;

	}
	
	private String readHeader(String data){
		return data.substring(0, HEADERLENGTH);
	}
	
	private String readData(String data){
		return data.substring(HEADERLENGTH + DELIMITER.length(), data.length());
	}
}
