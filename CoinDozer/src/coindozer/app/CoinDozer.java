package coindozer.app;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

public class CoinDozer extends Activity {
    /** Called when the activity is first created. */
    CoinDozerView dozerView;
 
   
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dozerView = new CoinDozerView(this);
        setContentView(dozerView);
       // dozerView.drawTest();
//        dozerView.startTimerhander();
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN :
			final int leftwall = (int)(GrobalData.canvasWidth * GrobalData.LEFTWALL_RATIO); 
			final int rightwall = (int)(GrobalData.canvasWidth * GrobalData.RIGHTWALL_RATIO); 
			
			//落としたいコインのサイズ、変えたいときはここで
			int coinSize = 25;
			Coin c; // = new Coin(new Point(100, 0), 20, 0);
			final int width = dozerView.getCanvasWidth();
			final int x = (int)event.getX();
			if(x > leftwall && x + coinSize < rightwall){
				c = new Coin(new Point(x, 0), coinSize, 10);				
				dozerView.coinManager.addCoin(c);
			}
			//まんなかより左クリック
//			if(event.getX() < width / 2){
//				c = new Coin(new Point(100, 0), 25, 0);
//			}else{
//				c = new Coin(new Point(200, 0), 25, 0);				
//			}
			
			break;
		}
		return super.onTouchEvent(event);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Handler h = this.dozerView.getHandler();
		try {
			dozerView.getTimer().join(); //これ必要？？
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		h.removeCallbacks(dozerView.getTimer());
//		h.re
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
}