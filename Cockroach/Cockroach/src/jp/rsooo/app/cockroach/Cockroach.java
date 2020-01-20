package jp.rsooo.app.cockroach;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class Cockroach extends Activity {
	
	CockroachView cockroachView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        
        cockroachView = new CockroachView(this);
        setContentView(cockroachView);
        controlOrientationFix(true);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	loadData();
    }
    
    public void onDestroy(){
    	super.onDestroy();
    	cockroachView.destroyFlag = true;
    	try {
			cockroachView.timer.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	cockroachView.soundManager.end();
    	
    }
    
	@Override
	public boolean onTouchEvent(MotionEvent event){
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN :
			if(!cockroachView.gameOverTouchDisable.isFlagOn() && cockroachView.playerData.isGameover()){
				
				finish();
			}
			int x = (int)event.getX();
			int y = (int)event.getY();
			cockroachView.touchEvent(x, y);
			break;
		}
		return super.onTouchEvent(event);
	}
	/**
	 * ècâ°å≈íËÇÃê›íËÇActivityÇ…ìKópÇ∑ÇÈ
	 * @param fixOrient å≈íËÇ∑ÇÈÇ»ÇÁtrueÅCâÒì]Ç∑ÇÈÇÊÇ§Ç…ñﬂÇ∑Ç»ÇÁfalse
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
		this.cockroachView.soundEnabled = checkBootPref.getBoolean("sound", false);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
//		ExAlertDialog.this.
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//				AlertDialog a = new AlertDialog(this){
				
//				}.
				new AlertDialog.Builder(this).setTitle(getText(R.string.exitdialog_title)).setMessage(getText(R.string.exitdialog_message))
				.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						finish();
					}
		        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//do nothing
					}
				}).show();
				return false;
			}
		}
		return super.dispatchKeyEvent(event);		
	}
	
	public class ExAlertDialog extends AlertDialog{

		protected ExAlertDialog(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
//			super.
		}
		
		
//		Builder b = AlertDialog.Builder;
		
	}

	
}