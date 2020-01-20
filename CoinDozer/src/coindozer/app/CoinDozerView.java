package coindozer.app;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CoinDozerView extends SurfaceView{
	public final static int TIMER_PERIOD = 17;
	//画面から落ちてくるコインが描画される割合
	public final static double FLAT_RATIO = 0.4544;
	
	
	//めんどいからpublic
	public CoinManager coinManager = new CoinManager();
	
	private Bitmap coinfallBitmap;
	private Bitmap coinBitmap;
	private Bitmap backGround;
	SurfaceHolder holder;
	Paint debugPaint;
	Handler timerHandler;
	
	private int canvasWidth;
	private int canvasHeight;
//	private int CoinSize;
	private int fallY;
	
	private int count = 0; 
	
	public CoinDozerView(Context context) {
		super(context);
		Resources res = getResources();
		
		coinfallBitmap = BitmapFactory.decodeResource(res, R.drawable.coinfall);
		coinBitmap = BitmapFactory.decodeResource(res, R.drawable.coin);
		backGround = BitmapFactory.decodeResource(res, R.drawable.base0);
		//SurfaceViewの初期化処理
		holder = getHolder();
		setFocusable(true);
		requestFocus();
		holder.addCallback(new SurfaceViewCallBack());

		setDebugStatus();
		
	}
	
	
	private void init(){
		
	}
	
	public void startTimerhander(){
		this.timerHandler = new Handler();
		timerHandler.postDelayed(this.timer, TIMER_PERIOD);
	}
	
	void setDebugStatus(){
		debugPaint = new Paint();
		debugPaint.setColor(Color.BLACK);
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh){
		this.canvasHeight = h;
		this.canvasWidth = w;
		GrobalData.canvasWidth = w;
		GrobalData.canvasHeight = h;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void draw(Canvas canvas){
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		Rect canvasRect = new Rect(0,0, this.canvasWidth, this.canvasHeight);
		Rect imgRect = new Rect(0,0,this.backGround.getWidth(), this.backGround.getHeight());
		canvas.drawBitmap(this.backGround, imgRect, canvasRect, null);
		Rect coinsrc = new Rect(0,0, coinfallBitmap.getWidth(), coinfallBitmap.getHeight());
//		//ここでコイン描画
		for(Coin c : this.coinManager.getCoinList()){
			final Point ptdst = c.getPt();
			Rect coindst = new Rect(ptdst.x, ptdst.y, ptdst.x + c.getSize(), ptdst.y + c.getSize());
			if(c.isFall()){
				canvas.drawBitmap(this.coinfallBitmap, coinsrc, coindst, null);				
			}else{
				canvas.drawBitmap(this.coinBitmap, coinsrc, coindst, null);								
			}
		}
		
//		canvas.drawBitmap(this.coinBitmap, src, src, null);
		canvas.drawText(String.valueOf(this.count), 10, 10, debugPaint);
	}
	
	void repaint(){
		SurfaceHolder holder = this.getHolder();
		Canvas canvas = holder.lockCanvas();
		if(canvas != null){
			synchronized( holder){
				draw(canvas);
			}
			holder.unlockCanvasAndPost(canvas);
		}else{
			Log.i("coin", "canvas is null in repaint");
		}
	}
	
	public void drawTest(){
		
		Canvas canvas = holder.lockCanvas();
		synchronized( holder){
			draw(canvas);
		}
		holder.unlockCanvasAndPost(canvas);			
	}

	
	
	
	//以下コールバック用のクラス書いとく
	
	class SurfaceViewCallBack implements SurfaceHolder.Callback{

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
//			Canvas canvas = holder.lockCanvas();
//			holder.unlockCanvasAndPost(canvas);
	//		setCanvasSize();
//			repaint();
			startTimerhander();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
//			Canvas canvas  holder.lockCanvas();
//			holder.unlockCanvasAndPost(canvas);
			// TODO Auto-generated method stub
//			repaint();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
		}
	}

	private Timer timer = new Timer();
	
	class Timer extends Thread{

		@Override
		public void run() {
			//Canvas canvas = holder.lockCanvas();
			count++;
			repaint();
			coinManager.coinMove();
			//毎回引数与えるのだるい??
			coinManager.removeOutofCoin(canvasWidth, canvasHeight);
			coinManager.checkHitFloor();
			timerHandler.postDelayed(this, TIMER_PERIOD);
		}
		
	}


	public Handler getTimerHandler() {
		return timerHandler;
	}


	public Timer getTimer() {
		return timer;
	}


	public int getCanvasWidth() {
		return canvasWidth;
	}


	public int getCanvasHeight() {
		return canvasHeight;
	}
}
//https://dl-ssl.google.com/android/eclipse/