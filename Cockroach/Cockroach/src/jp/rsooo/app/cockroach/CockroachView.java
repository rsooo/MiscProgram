package jp.rsooo.app.cockroach;

import java.util.*;



import jp.rsooo.app.cockroach.R;
import jp.rsooo.app.lib.ImageDrawer;
import jp.rsooo.app.lib.hiscore.HiScoreManager;
import jp.rsooo.app.lib.timerevent.BooleanTimerFlag;
import jp.rsooo.app.lib.timerevent.TimerEventManager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CockroachView extends SurfaceView {

	boolean soundEnabled = false;
	
	public final static int BASE_PERIOD = 25; //基準となる描画速度
	public final static int TIMER_PERIOD = BASE_PERIOD;
	
	//cock発生の間隔
	public int COCK_PERIOD = 3000;
	public int cockPeriod;
	
	//
	public int restCock;
	
	public long previousTimerCalledTime = Calendar.getInstance().getTimeInMillis();
	public long timeDifference = BASE_PERIOD;

	int touchedX;
	int touchedY;

	int canvasWidth;
	int canvasHeight;
	
	Context context;
	//タイマを扱うハンドら
	Handler timerHandler;
	private Bitmap cockroachBitmap;
	private Bitmap cockroachDeadBitmap;
	
	private Bitmap gabageBitmap;
	private Bitmap trashboxBitmap;
	
	private Bitmap gameOverBitmap;
	private Bitmap titleBitmap;
	
	Bitmap testBitmap;
	Bitmap[] cockABitmap = new Bitmap[16];
	Bitmap[] cockBBitmap = new Bitmap[16];
	Rect[] cockRectArray = new Rect[16];
//	Bitmap[] cockDBitmap = new Bitmap[16];
	Bitmap backGroundBitmap;
	Bitmap[] backGroundBitmapArray = new Bitmap[9]; 
	
	private Rect canvasRect;
	private Rect cockRect;
	private Rect cockDeadRect;
	private Rect backimgRect;
	private Rect gabageRect;
	private Rect trashboxRect;
	
	private Rect titleRect;
	private Rect gameOverRect;
	
//	private List<Rect> gabageRectList = new ArrayList<Rect>();
//	private List<Rect> trashboxRectList = new ArrayList<Rect>();

	private Rect gabaseDispRect;
	private Rect trashboxDispRect;
	
	//画面上に描画するときのcockのサイズ
	private Rect drawcockRect;
	Paint debugPaint = new Paint(); 
	Paint whiteBackPaint = new Paint();
	Paint textPaint = new Paint();
	Paint textPaintRed = new Paint();
	Paint textScorePaint = new Paint();
	
	SurfaceHolder holder;
	
	CockGenerator cockGenerator = new CockGenerator();
	CockManager cockManager;
	SoundManager soundManager;
	HiScoreManager hiScoreManager;
	//タイマイベントを発行するマネージャ
	TimerEventManager timerEventManager = new TimerEventManager();

	StageInitializer stageInitializer;
	Stage currentStage;
	int currentStageCount = StageInitializer.INITSTAGE;
	Timer timer = new Timer();
	
	PlayerData playerData = new PlayerData();
	
	//GameOver時に画面タッチを一時的に無効にするフラグ
	BooleanTimerFlag gameOverTouchDisable;
	
	//Destory時に立てるフラグ
	public boolean destroyFlag = false;
	
	class Timer extends Thread{
		
		@Override
		public void run(){
			if(!gameOverTouchDisable.isFlagOn() && playerData.isGameover()){
				return;
			}
			if(destroyFlag){
				return;
			}
			
			
//			if(timeDifference < BASE_PERIOD){
//				try {
//					Thread.sleep(BASE_PERIOD - timeDifference);
//					//待ったあとで時間再計測。2度はかるのはコスト高い？？	
//					time = calendar.getTimeInMillis();
//					timeDifference = time - previousTimerCalledTime; 					
//					Log.i("cock", "sleep called:d " + timeDifference);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			} 
			 
//			previousTimerCalledTime = time;
			//delayの計算
			double delay = 1.0;
			if(timeDifference > BASE_PERIOD){
				delay = timeDifference / BASE_PERIOD;
			}
			cockManager.removeOutofCock();
			cockManager.removeDeadCock();
			
			timerEventManager.notifyTimerEvent(timeDifference);
			//画面内のcockが動く
			stepTimer();
			cockManager.move();
			repaint();
			
			Calendar calendar = Calendar.getInstance();
			long time = calendar.getTimeInMillis();
			timeDifference = time - previousTimerCalledTime;
			previousTimerCalledTime = time;
//			Log.i("cock", "sleep called:d " + timeDifference);
			if(timeDifference < BASE_PERIOD){
				timerHandler.postDelayed(this, BASE_PERIOD - timeDifference);				
			}else{
				timerHandler.post(this);
			}
		}
		
		/**
		 * cock発生タイマーを進める
		 */
		private void stepTimer(){
			if(playerData.isGameover()){
				return;
			}
			
			cockPeriod -= timeDifference;
			if(cockPeriod < 0){
				if(restCock > 0){
					restCock--;
					Cock newCock = cockGenerator.generate();
					cockManager.add(newCock);
					cockPeriod = COCK_PERIOD;
				}else{
					//ステージクリア
					nextStage();
					cockPeriod = COCK_PERIOD;
				}
			}
		}
	}
	
	
	public CockroachView(Context context) {
		super(context);
		this.context = context;
		Resources res = getResources();
		cockroachBitmap = BitmapFactory.decodeResource(res, R.drawable.gokia);
		cockroachDeadBitmap = BitmapFactory.decodeResource(res, R.drawable.gokidead);
		//ビットマップ作成(時間かかる？？)
		this.createBitmap(res);
		this.backGroundBitmap = BitmapFactory.decodeResource(res, R.drawable.background);
		
		Matrix m = new Matrix();
		m.postRotate(45.0f);
		testBitmap = Bitmap.createBitmap(this.cockroachBitmap,0,0,this.cockroachBitmap.getWidth()
				, this.cockroachBitmap.getHeight(), m, true);

		//SurfaceViewの初期化処理
		holder = getHolder();
		setFocusable(true); 
		requestFocus();
		textPaint.setColor(Color.BLUE);
//		textPaint.setTextAlign(Align.CENTER);
		textPaint.setTextSize(20);

		textScorePaint.setColor(Color.BLUE);
		textScorePaint.setTextAlign(Align.RIGHT);
		textScorePaint.setTextSize(20);

		
		textPaintRed.setColor(Color.RED);
		textPaintRed.setTextSize(26);

		
		holder.addCallback(new SurfaceViewCallBack());
		
		this.setDebugStatus();
		
	}
	
	private void initRect(){
		this.canvasRect = new Rect(0,0, this.canvasWidth, this.canvasHeight);
		this.cockRect = new Rect(0,0, this.cockroachBitmap.getWidth(), this.cockroachBitmap.getHeight());
		this.drawcockRect = new Rect(0,0, GlobalSettings.COCKWIDTH, GlobalSettings.COCKHEIGHT);
		this.backimgRect = new Rect(0,0, this.backGroundBitmap.getWidth(), this.backGroundBitmap.getHeight());
		this.cockDeadRect = new Rect(0,0, this.cockroachDeadBitmap.getWidth(), this.cockroachDeadBitmap.getHeight());
		this.gabageRect = new Rect(0,0, this.gabageBitmap.getWidth(), this.gabageBitmap.getHeight());
		this.trashboxRect = new Rect(0,0, this.trashboxBitmap.getWidth(), this.trashboxBitmap.getHeight());
		this.gameOverRect = new Rect(0,0, this.gameOverBitmap.getWidth(), this.gameOverBitmap.getHeight());
		this.titleRect = new Rect(0,0, this.titleBitmap.getWidth(), this.gameOverBitmap.getHeight());
		
//		/this.gabaseDispRect = new Rect(this.canvasWidth * 3 / 5, this.canvasHeight / 8, this.canvasWidth * 3 / 5 + 100, this.canvasHeight / 8 + 100);
		

	}
	
	private void initManager(){
		
//		CockGeneratePoint generatePt = new CockGeneratePoint(this.canvasWidth * 3 / 5 + 50, this.canvasHeight / 8 + 50, new int[]{0,15,14,13,12,11,10,9,8,7}, 2);
		
		
		cockManager = new CockManager(this, canvasWidth, canvasHeight);
		soundManager = new SoundManager(context);
		hiScoreManager = new HiScoreManager(context, 10);
		hiScoreManager.loadHiScore();
		gameOverTouchDisable = new BooleanTimerFlag(timerEventManager, 3000);
	}
	
	/**
	 * 画面がタッチされたときの動作
	 */
	public void touchEvent(int x, int y){
		touchedX = x;
		touchedY = y;
		if(gameOverTouchDisable.isFlagOn()){
			return; //GameOver時は一時的に停止
		}
		if(this.playerData.isGameover()){
//			this.playerData.clear();
//			this.currentStageCount = 0;
//			this.loadStage(0);
		}
		for(Rect gabageR : currentStage.gabageRectList){
			if(isInnerRect(x, y, gabageR)){
				return;
			}
		}
		for(Rect trashR : currentStage.trashboxRectList){
			if(isInnerRect(x, y, trashR)){
				return;
			}
		}
		this.cockManager.touch(x, y);
	}
	
	@Override
	public void draw(Canvas canvas){
//		this.whiteBackPaint.setStyle(Paint.Style.FILL);
//		this.whiteBackPaint.setColor(Color.WHITE);
		Rect back = new Rect(0,0, canvas.getWidth(), canvas.getHeight());
		
		canvas.drawBitmap(this.backGroundBitmapArray[currentStage.backgroundId], this.backimgRect, back, null);
//		canvas.drawBitmap(this.cockroachBitmap, cockRect, drawcockRect, null);
		
		for(Cock c : cockManager.getCockList()){
			int x = c.x;
			int y = c.y;
			  
//			Rect testRect = new Rect(0,0, testBitmap.getWidth(), testBitmap.getHeight());
			Rect canvasdst = new Rect(x, y, x + GlobalSettings.COCKWIDTH, y + GlobalSettings.COCKHEIGHT);
			if(c.hp == 0){
				canvas.drawBitmap(this.cockroachDeadBitmap, this.cockDeadRect, canvasdst, null);
			}else{
//				canvas.drawBitmap(this.cockroachBitmap, this.cockRect, dst, null);
				int d = c.direction;
				if(c.ismove){
					canvas.drawBitmap(this.cockABitmap[d], this.cockRectArray[d ], canvasdst, null);					
				}else{
					canvas.drawBitmap(this.cockBBitmap[d], this.cockRectArray[d ], canvasdst, null);					
				}
			}
		}
		for(Rect gabageDisplayRect : this.currentStage.gabageRectList){
			canvas.drawBitmap(this.gabageBitmap, this.gabageRect, gabageDisplayRect, null);
		}
		for(Rect trashBoxDisplayRect : this.currentStage.trashboxRectList){
			canvas.drawBitmap(this.trashboxBitmap, this.trashboxRect, trashBoxDisplayRect, null);
		}
		if(this.playerData.isGameover()){
//			drawCock(canvas);
			canvas.drawBitmap(this.gameOverBitmap, this.gameOverRect, this.canvasRect, null);
		}else{
			drawLife(canvas);
		}
		this.showtext(canvas,String.valueOf((int)(1000 / (timeDifference == 0 ? 1 : timeDifference))) + "FPS");
//		this.showtext2(canvas, String.valueOf(touchedX));
//		this.showtext3(canvas, this.hiScoreManager.debug());
	}
	
	//画面にCockいっぱいかく？？いらない
	private void drawCock(Canvas canvas){
		int xsize = canvasWidth / 20;
		int ysize = canvasHeight / 10;
		for(int y = 0;y < 10; y++){
			for(int x = 0; x < 20;x++){
				Rect r = new Rect(xsize * x, ysize*y, xsize * (x+1) , ysize*(y+1));
				canvas.drawBitmap(this.cockroachBitmap, this.cockRect, r, null);
			}
		}
			
}
	
	void repaint(){
//		SurfaceHolder holder = this.getHolder();
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
	
	private void showtext(Canvas c, String text){
		c.drawText(text, 10, 55, debugPaint);
	}
	private void showtext2(Canvas c, String text){
		c.drawText(text, 10, 20, debugPaint);
	}
	private void showtext3(Canvas c, String text){
		c.drawText(text, 10, 30, debugPaint);
	}
	
	private void setDebugStatus(){
		debugPaint = new Paint();
		debugPaint.setColor(Color.BLACK);
	}
	class SurfaceViewCallBack implements SurfaceHolder.Callback{

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
//			Canvas canvas = holder.lockCanvas();
//			holder.unlockCanvasAndPost(canvas);
	//		setCanvasSize();
			cockPeriod = COCK_PERIOD;
			startTimerhander();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			//とりあえず縦のサイズを基本に
			GlobalSettings.COCKHEIGHT = height / 8;
			float imgratio = cockroachBitmap.getWidth() / cockroachBitmap.getHeight();
			GlobalSettings.COCKWIDTH = (int)(GlobalSettings.COCKHEIGHT * imgratio);
			canvasWidth = width;
			canvasHeight = height;
			stageInitializer = new StageInitializer(canvasWidth, canvasHeight);
			stageInitializer.createStage();
			loadStage(stageInitializer.INITSTAGE); //初めのステージ読む
			initRect();
			initManager();
//			Canvas canvas  holder.lockCanvas();
//			holder.unlockCanvasAndPost(canvas);
			// TODO Auto-generated method stub
//			repaint();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
		}
		
		private void startTimerhander(){
			timerHandler = new Handler();
			timerHandler.postDelayed(timer, TIMER_PERIOD);
		}
	}
	
	private void drawLife(Canvas c){
		c.drawText("LIFE:" + playerData.life, 10, 20, this.textPaint);
		c.drawText("STAGE:" + (this.currentStageCount + 1), 10, 40, this.textPaint);
		c.drawText("SCORE:" + this.playerData.score , this.canvasWidth - 10, 20, this.textScorePaint);
		
	}
	
	private void nextStage(){
		cockManager.clear();
		loadStage(++currentStageCount);
		
	}
	
	private void loadStage(int i){
		currentStage = stageInitializer.STAGE[i];
		
		COCK_PERIOD = currentStage.period; 
		restCock = currentStage.num;
		this.cockGenerator.clear();
		for(CockGeneratePoint genpt : currentStage.genPtList){
			this.cockGenerator.addPoint(genpt);
		}
		if(i < stageInitializer.liferecove.length){
			this.playerData.life += stageInitializer.liferecove[i];			
		}
	}
	
	private void createBitmap(Resources res){
		float step =22.5f;
		float degree = step;
		Matrix m = new Matrix();
		this.cockABitmap[0] = this.backGroundBitmap = BitmapFactory.decodeResource(res, R.drawable.gokia);
		this.cockBBitmap[0] = this.backGroundBitmap = BitmapFactory.decodeResource(res, R.drawable.gokib);
		
		this.cockRectArray[0] = new Rect(0,0,this.cockABitmap[0].getWidth(), this.cockABitmap[0].getHeight());
		for(int i = 1;i < this.cockABitmap.length; i++ /*,degree += step*/){
			m.postRotate(degree);
			this.cockABitmap[i] = Bitmap.createBitmap(this.cockABitmap[0],0,0,this.cockABitmap[0].getWidth()
					, this.cockABitmap[0].getHeight(), m, true);
			this.cockBBitmap[i] = Bitmap.createBitmap(this.cockBBitmap[0],0,0,this.cockBBitmap[0].getWidth()
					, this.cockBBitmap[0].getHeight(), m, true);
			
			this.cockRectArray[i] = new Rect(0,0, this.cockABitmap[i].getWidth(), this.cockABitmap[i].getHeight());
		}
		
		//背景
		backGroundBitmapArray[0] = BitmapFactory.decodeResource(res, R.drawable.background);
		backGroundBitmapArray[1] = BitmapFactory.decodeResource(res, R.drawable.background2);
		backGroundBitmapArray[2] = BitmapFactory.decodeResource(res, R.drawable.background3);
		backGroundBitmapArray[3] = BitmapFactory.decodeResource(res, R.drawable.background4);
		backGroundBitmapArray[4] = BitmapFactory.decodeResource(res, R.drawable.background5);
		backGroundBitmapArray[5] = BitmapFactory.decodeResource(res, R.drawable.toilet1);
		backGroundBitmapArray[6] = BitmapFactory.decodeResource(res, R.drawable.toilet2);
		backGroundBitmapArray[7] = BitmapFactory.decodeResource(res, R.drawable.toilet3);
		backGroundBitmapArray[8] = BitmapFactory.decodeResource(res, R.drawable.toilet4);
		
		//ゴミ袋とか
		this.gabageBitmap = BitmapFactory.decodeResource(res, R.drawable.trash1);
		this.trashboxBitmap = BitmapFactory.decodeResource(res, R.drawable.trashbox2);

		//タイトル画面とか
		this.gameOverBitmap = BitmapFactory.decodeResource(res, R.drawable.gameover);
		this.titleBitmap = BitmapFactory.decodeResource(res, R.drawable.titleen);
	}


	
	private boolean isInnerRect(int x, int y, Rect r){
		if(r.left < x && r.right > x && r.top < y && r.bottom > y){
			return true;
		}
		return false;
	}

	public Handler getTimerHandler() {
		return timerHandler;
	}
}
