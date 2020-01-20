package jp.rsooo.app.credit;

import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.List;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;

//import com.admob.android.ads.AdManager;

import jp.rsooo.app.R;

import android.app.*;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class CreditActivity extends ListActivity {

	//リスト内で表示するデータ
	List<TwoTextItem> items =  new ArrayList<TwoTextItem>();
	private TwoTextAdapter mListAdapter;
	
	final String url = "http://mobile.twitter.com/rsooo";
	final String mail = "rso.mobile+develop@gmail.com";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        items.add(new TwoTextItem("作者", "rsooo"));
        items.add(new TwoTextItem("Twitter", url));
        items.add(new TwoTextItem("メール", mail));
        mListAdapter = new TwoTextAdapter(this, R.layout.twolistitem, items);
        this.setListAdapter(mListAdapter);
        this.setContentView(R.layout.credit);
        
        // Create the adView
        AdView adView = new AdView(this, AdSize.BANNER, "a14d4222d796e55");
        // Lookup your LinearLayout assuming it’s been given
        // the attribute android:id="@+id/mainLayout"
        LinearLayout layout = (LinearLayout)findViewById(R.id.admoblayout);
        // Add the adView to it
        layout.addView(adView);
        adView.setAdListener(new AdListener(){

			@Override
			public void onDismissScreen(Ad arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
				// TODO Auto-generated method stub
				//Toast.makeText(CreditActivity.this, "Fail", Toast.LENGTH_SHORT).show();
				
			}

			@Override
			public void onLeaveApplication(Ad arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPresentScreen(Ad arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onReceiveAd(Ad arg0) {
				// TODO Auto-generated method stub
				//Toast.makeText(CreditActivity.this, "Success", Toast.LENGTH_SHORT).show();
				
			}
        	
        });
        // Initiate a generic request to load it with an ad
        adView.loadAd(new AdRequest());
//        AdManager.setTestDevices( new String[] { 
//        		AdManager.TEST_EMULATOR,
//        	});
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
    	switch(position){
    	case 1:
    		 Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    		 this.startActivity(intent);
    	 	 return;
    	case 2:
    		Intent mailintent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + URLEncoder.encode(mail)));
//    		mailintent.putExtra(Intent.EXTRA_EMAIL, Uri.parse("mailto:"+ mail));
    		this.startActivity(mailintent);

    		return;
    	}
    	super.onListItemClick(l, v, position, id);
    }
}
