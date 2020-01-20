package jp.rsooo.app.cockroach;


import java.util.List;


import android.content.DialogInterface;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.google.example.games.basegameutils.GameHelper;
import jp.rsooo.app.cockroach.R;
import jp.rsooo.app.lib.hiscore.HiScoreManager;
import jp.rsooo.app.lib.hiscore.IHiScoreRecode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;

public class MainActivity extends Activity implements View.OnClickListener,GameHelper.GameHelperListener {

		public final int soundIconSize = 50; //�߂�ǂ�����T�C�Y�Œ�
		HiScoreManager hiScoreManger;
		ImageView view;
	   MainSurfaceView mainView;
	   TextView textView;
	   
	   
	   int soundtop;
	   int soundleft;
	   int soundright;
	   int soundbuttom;
	   
	   int width, height;

       private GameHelper gameHelper;

	   SimpleView soundView;
//	   SimpleView soundView2;
	   
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
           requestWindowFeature(Window.FEATURE_NO_TITLE);
           getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
           super.onCreate(savedInstanceState);



//	        mainView = new MainSurfaceView(this);
//	        SimpleView vv = (SimpleView)this.findViewById(R.id.sinpleView); 
	        
	        
	        setContentView(R.layout.main);

           findViewById(R.id.sign_in_button).setOnClickListener(this);
           findViewById(R.id.sign_out_button).setOnClickListener(this);
           findViewById(R.id.online_ranking).setOnClickListener(this);


           textView = (TextView)this.findViewById(R.id.TextView01);
	        soundView = (SimpleView)this.findViewById(R.id.sinpleView);
//	        view = (ImageView)this.findViewById(R.id.ImageView01);
//	        view.getContext().
	        controlOrientationFix(true);
	        this.hiScoreManger = new HiScoreManager(this, 10);
	      
	        WindowManager windowmanager = (WindowManager)getSystemService(WINDOW_SERVICE);
	        Display disp = windowmanager.getDefaultDisplay();
	        width = disp.getHeight();
	        height = disp.getWidth();
//	        soundView.setSize(width, height);
	        Resources res = getResources();
	        LayoutParams layoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, 
	        		LayoutParams.WRAP_CONTENT); 
//	        this.addContentView(soundView2, layoutParam); //�@������

           gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
           gameHelper.enableDebugLog(true);
           gameHelper.setup(this);

//

           // AdView をリソースとしてルックアップしてリクエストを読み込む
           AdView adView = (AdView)this.findViewById(R.id.adView);
//           adView.setAdSize(AdSize.BANNER);
//           adView.setAdUnitId("ca-app-pub-5965149896020203/2021232305");
           AdRequest adRequest = new AdRequest.Builder().build();
/*
           AdRequest adRequest = new AdRequest.Builder()
                   .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // エミュレータ
//                   .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4") // Galaxy Nexus テスト用携帯電話
                   .build();

*/
           adView.loadAd(adRequest);
/*           LinearLayout layout = (LinearLayout)this.findViewById(R.id.lineeradlayuout);
           // adView を作成する
           AdView adView = new AdView(this);
           adView.setAdUnitId("ca-app-pub-5965149896020203/2021232305");
           adView.setAdSize(AdSize.BANNER);
           layout.addView(adView);

           // 一般的なリクエストを行う
//           AdRequest adRequest = new AdRequest.Builder().build();

           AdRequest adRequest = new AdRequest.Builder()
                   .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // エミュレータ
//                   .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4") // Galaxy Nexus テスト用携帯電話
                   .build();
           // 広告リクエストを行って adView を読み込む
           adView.loadAd(adRequest);*/
       }

      @Override
      public void onStart(){
          super.onStart();
         gameHelper.onStart(this);
//          if(gameHelper.isConnecting()){
//              this.onSignInSucceeded();
//          }
      }

      @Override
      public void onStop(){
          super.onStop();
          gameHelper.onStop();
      }
	   
	   @Override
	   public void onResume(){
		   super.onResume();
		   loadData(); //�����Đ����邩�̐ݒ�̃��[�h
		   updateHighScore();
//		   soundView2.bringToFront();
//		   soundView2.
//		   this.soundView2.invalidate();
	   }
	   
	   private void updateHighScore(){
           Log.d("LOG", "update score");

	        this.hiScoreManger.loadHiScore();
	        List<IHiScoreRecode> hiScoreList = this.hiScoreManger.getHiScoreList();
	       int score = 0;
           if(hiScoreList.size() != 0){
	        	score = hiScoreList.get(0).getHiScore();
	        	textView.setText("HighScore:" + score);
	        }else{
	        	textView.setText("HighScore:0");	        	
	        }

           if(this.hiScoreManger.isHiscoreUpdated()){
               if(gameHelper.isSignedIn()) {
                   Log.d("LOG", "submit score");
                   Games.Leaderboards.submitScore(gameHelper.getApiClient(), getString(R.string.leaderboard_score), score);
                   this.hiScoreManger.setHiscoreUpdated(false);
               }
           }
	   }
	   
		@Override
		public boolean onTouchEvent(MotionEvent event){
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN :
				int x = (int)event.getX();
				int y = (int)event.getY();
				Rect soundRect = soundView.soundDispRect;
				if(x >= soundRect.left && x < soundRect.right && y >= soundRect.top && y < soundRect.bottom){
					soundView.soundState = soundView.soundState ? false : true; //���]
					saveData(soundView.soundState); //�l�̕ۑ�
					soundView.invalidate();
				}else{
					Intent intent = new Intent(this, Cockroach.class);
					startActivity(intent);
//				updateHighScore();
				}
			}
			return super.onTouchEvent(event);
		}


		/**
		 * �c���Œ�̐ݒ��Activity�ɓK�p����
		 * @param fixOrient �Œ肷��Ȃ�true�C��]����悤�ɖ߂��Ȃ�false
		 */
		private void controlOrientationFix(boolean fixOrient) {
			if (fixOrient) {
				int ori = getResources().getConfiguration().orientation;
				if (ori == Configuration.ORIENTATION_LANDSCAPE) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				} else {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED );
			}
		}
		
	  
		private void loadData(){
			SharedPreferences checkBootPref = this.getSharedPreferences("soundenable", Context.MODE_PRIVATE);
			boolean enable = checkBootPref.getBoolean("sound", false);
			this.soundView.soundState = enable;
		}
		
		private void saveData(boolean savedata){
			SharedPreferences checkBootPref = this.getSharedPreferences("soundenable", Context.MODE_PRIVATE);
			Editor editor = checkBootPref.edit();
			editor.putBoolean("sound", savedata);
			editor.commit();
		}


    @Override
    public void onSignInFailed() {
        Log.d("LOG", "Sign in is failed");
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_out_button).setVisibility(View.GONE);
    }

    @Override
    public void onSignInSucceeded() {
        Log.d("LOG", "Sign in is successed");
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);

      //  Games.Leaderboards.submitScore(gameHelper.getApiClient(), getString(R.string.leaderboard_test), 200);
        updateHighScore();
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode,
                                 Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        gameHelper.onActivityResult(requestCode,responseCode,intent);
    }

    @Override
    public void onClick(View v) {
        Log.d("LOG", "view clicked");
        if (v.getId() == R.id.sign_in_button) {
            // start the asynchronous sign in flow
            Log.d("LOG", "sign in clicked");

//            gameHelper.onStart(this);
            if(gameHelper.isSignedIn()){
                Toast.makeText(this, "already sigin in", Toast.LENGTH_SHORT).show();
            }


            gameHelper.beginUserInitiatedSignIn();
//            gameHelper.reconnectClient();
//            if(gameHelper.isSignedIn()){
//                this.onSignInSucceeded();
//            }
        }
        else if (v.getId() == R.id.sign_out_button) {
            // sign out.
            gameHelper.signOut();
            gameHelper.disconnect();

            // show sign-in button, hide the sign-out button
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }else if(v.getId() == R.id.online_ranking){
            if(gameHelper.isSignedIn()) {
                startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(gameHelper.getApiClient()), 0);
            }else {
                Toast.makeText(this, this.getString(R.string.signinneed), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
