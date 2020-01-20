package hakomusu.app;

import hakomusu.app.Board.DIRECTION;

import java.util.EnumSet;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class HakoMusuView extends SurfaceView{
	enum Direction{
		UP, DOWN, LEFT, RIGHT
	}
	//final int FPS = 60;
	
	Context context;
	
	public int CELL_SIZE;
	public int TEXT_SIZE;
	public int BOARDER_MARGIN;
	public static final int BOARD_WIDTH = Board.MAP_WIDTH;
	public static final int BOARD_HEIGHT = Board.MAP_HEIGHT;
	public static final int STOP = -2;
	
	public final int TIMER_PERIOD = 50;
	
	private SurfaceView view = this;
	
	private int selectedId = -1;
	//タッチされたときの起点となる座標
	private Point touchedPoint;
	
	//計算の始点となる座標
	Point begin_point;
	
	//ボードの管理するクラス
	Board board = new Board();
	
	//チップのビットマップ
	private Bitmap chipBitmap;
	
	private int canvasWidth;
	private int canvasHeight;
	
	boolean moving = false;
	Paint debugPaint = new Paint();
	Point debugPoint = new Point(10,10);

	Handler handler = new Handler();
	//チップの動きを表現する
	private int moveOffsetx = 0;
	private int moveOffsety = 0;
	private int movingId = STOP;
	Thread timer;// = new Timer();
	
	Paint clearPaint = new Paint();
	boolean clearFlag = false;
	Paint textPaint = new Paint();
	//手数
	int tesu = 0;
	//minimumtesu
	int mintesu = 9999;
	//プレイ時間
	public long timeSec = 0;
	//1秒カウントするスレッド
	public TimerSec timerThread = new TimerSec();
	
	
	public HakoMusuView(Context context) {
		super(context);
		this.context = context;
		SurfaceHolder holder = getHolder();
		holder.addCallback(new SurfaceViewCallBack());
		// TODO Auto-generated constructor stub
		debugPaint.setStrokeWidth(2);
		debugPaint.setStyle(Paint.Style.FILL);
		chipBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.chip);
		this.setVisibility(View.VISIBLE);
//		handler.postDelayed(timer, TIMER_PERIOD);
		clearPaint.setColor(Color.RED);
		clearPaint.setAntiAlias(true);
		textPaint.setColor(Color.BLACK);
		textPaint.setAntiAlias(true);
		loadData();
		
	}

	@Override
	public void draw(Canvas canvas){
		Paint backp = new Paint();
		Paint cellp = new Paint();
		Paint focusp = new Paint();
		Paint boarderp = new Paint();
		cellp.setARGB(0x99, 0x66, 0x33, 0);
		boarderp.setARGB(0x99, 0x66, 0x33, 0);
		focusp.setColor(Color.RED);
		cellp.setStyle(Paint.Style.STROKE);
		boarderp.setStyle(Paint.Style.STROKE);
		cellp.setStrokeWidth(2);
		boarderp.setStrokeWidth(BOARDER_MARGIN);
		
		Paint innerp = new Paint();
		innerp.setARGB(0x99, 0x99, 0x66, 0);
		
		//cellp.
//		backp.setColor();
		backp.setARGB(0xff, 0xCC, 0xCC, 0xCC);
		canvas.drawRect(new Rect(0,0,this.canvasWidth,this.canvasHeight), backp);
		Rect boarderRect = new Rect(begin_point.x - BOARDER_MARGIN / 2, begin_point.y - BOARDER_MARGIN / 2, begin_point.x + CELL_SIZE*4 + BOARDER_MARGIN / 2, begin_point.y + CELL_SIZE*5 + BOARDER_MARGIN / 2);
		canvas.drawRect(boarderRect, boarderp);
		Rect innerRect = new Rect(begin_point.x, begin_point.y, begin_point.x + CELL_SIZE*4 , begin_point.y + CELL_SIZE*5);
		canvas.drawRect(innerRect, innerp);
		Rect bottomRect = new Rect(begin_point.x + CELL_SIZE + 1, begin_point.y + CELL_SIZE * 5, begin_point.x + CELL_SIZE * 3 - 1, begin_point.y + CELL_SIZE * 5 + BOARDER_MARGIN);
		canvas.drawRect(bottomRect, backp);
		
		for(Chip c : board.chipList){
			int beginx = this.begin_point.x + c.location.x * CELL_SIZE;
			int beginy = this.begin_point.y + c.location.y * CELL_SIZE;
			if(c.id == this.movingId){
				Rect mover = new Rect(beginx + moveOffsetx, beginy + moveOffsety, beginx + c.width * CELL_SIZE  + moveOffsetx, beginy + c.height * CELL_SIZE + moveOffsety);
				canvas.drawBitmap(chipBitmap, this.getBoardRect(c.id), mover, focusp);
				canvas.drawRect(mover, cellp);
				continue;
			}
			Rect r = new Rect(beginx, beginy, beginx + c.width * CELL_SIZE , beginy + c.height * CELL_SIZE );
//			if(c.id == this.selectedId){
				canvas.drawBitmap(chipBitmap, this.getBoardRect(c.id), r, focusp);
//				canvas.drawRect(r, focusp);
//			}else{
				canvas.drawRect(r, cellp);
//			}
		}
//		if(this.selectedId >= 0){
//			Chip c = board.chipList[selectedId];
//			int beginx = this.begin_point.x + c.location.x * CELL_SIZE;
//			int beginy = this.begin_point.y + c.location.y * CELL_SIZE;
//			Rect r = new Rect(beginx, beginy, beginx + c.width * CELL_SIZE - 3, beginy + c.height * CELL_SIZE - 3);
//			canvas.drawRect(r, cellp);
//		}
		
		/*Chip c = board.chipList[1];
		Rect r = new Rect(this.begin_point.x + c.location.x * CELL_SIZE, this.begin_point.y + c.location.y * CELL_SIZE, c.width * CELL_SIZE - 3, c.height * CELL_SIZE - 3);
		canvas.drawRect(r, cellp);
		*///canvas.drawRect(new Rect(0,0,100,100), cellp);
		
		//canvas.drawText(String.valueOf(this.selectedId), 10f, 10f, debugPaint);
		//canvas.drawText(this.board.toString(), 10f, 20f, debugPaint);
		
		if(clearFlag){
			canvas.drawText("CLEARED!", begin_point.x + CELL_SIZE / 2, begin_point.y + CELL_SIZE * 6, clearPaint);
			if(mintesu > tesu){
				mintesu = tesu;
				saveMinTesu();
			}
		}
		canvas.drawText(context.getText(R.string.AmountTime) + " " + calcPlayTimeAsString(timeSec), begin_point.x, begin_point.y + (int)(CELL_SIZE * 6.5), textPaint);
		canvas.drawText(context.getText(R.string.tesi).toString() + tesu, begin_point.x, begin_point.y + (int)(CELL_SIZE * 6.5) + TEXT_SIZE + 5, textPaint);
		canvas.drawText(context.getText(R.string.minTesu).toString() + mintesu, begin_point.x + CELL_SIZE * 2, begin_point.y + (int)(CELL_SIZE * 6.5) + TEXT_SIZE + 5, textPaint);
		
	}
	
	private String calcPlayTimeAsString(long time){
		long sec = time % 60;
		long min = (int)(time / 60) % 60;
		long hour = (int)(sec / 60);
		StringBuffer sb = new StringBuffer();
		sb.append(hour >= 10 ? hour : "0" + hour).append(":").append(min >= 10 ? min : "0" + min).append(":").append(sec >= 10 ? sec : "0" + sec);
		return sb.toString();
	}
	
	/**
	 * クリックされた座標を計算する
	 * ボード外は全て-１にする
	 * @param locationpt
	 * @return
	 */
	public Point selected(final Point locationpt){
		int x = (int)((locationpt.x - this.begin_point.x) / CELL_SIZE);
		if(x >= BOARD_WIDTH || x < 0){
			x = -1;
		}	
		int y = (int)((locationpt.y - this.begin_point.y) / CELL_SIZE);
		if(y >= BOARD_HEIGHT || y < 0){
			y = -1;
		}
		return new Point(x,y);
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh){
		this.canvasHeight = h;
		this.canvasWidth = w;
		CELL_SIZE = canvasHeight / 8;
		BOARDER_MARGIN = CELL_SIZE / 3;
		begin_point  = new Point((this.canvasWidth) / 2 - CELL_SIZE * 2, (this.canvasHeight ) / 2 - CELL_SIZE * 3);
		clearPaint.setTextSize((int)(CELL_SIZE / 1.5));
		TEXT_SIZE = (int)(CELL_SIZE / 4);
		textPaint.setTextSize(TEXT_SIZE);
		//時刻カウント開始
		//handler.postDelayed(timerThread, 1000);
		
		
	}
	
	@Override 
	public void onDraw(Canvas canvas){
		repaint();
	}
	
	void repaint(){
		SurfaceHolder holder = this.getHolder();
		Canvas canvas = holder.lockCanvas();
		synchronized( holder){
			draw(canvas);
		}
		holder.unlockCanvasAndPost(canvas);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		//動いてるときは他の動き受け付けない
		if(moving){
			return true;
		}
   		int x = (int)event.getX();
		int y = (int)event.getY();
		Point selectedPt = selected(new Point(x,y));
		//範囲外をタッチした場合は無視
		if(selectedPt.x == -1 || selectedPt.y == -1){
			return true;
		}
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			this.selectedId = board.getChipId(selectedPt.x, selectedPt.y);
			this.touchedPoint = new Point(x,y);
			repaint();
			break;
		case MotionEvent.ACTION_MOVE:
			if(this.selectedId == -1){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
			int chipId = board.getChipId(selectedPt.x, selectedPt.y);
			if(chipId == -1 || chipId == this.selectedId){
				boolean alreadymove = false;
				Point touchPt = new Point (x, y);
				//EnumSet<DIRECTION> direction = board.moveCheck(board.chipList[this.selectedId].location, touchPt/*selectedPt*/);
				EnumSet<DIRECTION> direction = board.moveCheck(this.touchedPoint, touchPt/*selectedPt*/);
				Chip currentChip = board.chipList[this.selectedId];
				if(direction.contains(DIRECTION.UP)){
					if(board.checkMoveUP(currentChip) && !alreadymove){
						board.moveUP(currentChip);
						alreadymove = true;
						this.movingId = currentChip.id;
						
						handler.postDelayed(new Timer(Direction.UP), TIMER_PERIOD);
						moving = true;
						this.touchedPoint = touchPt;
						tesu++;
					}
				}
				if(direction.contains(DIRECTION.DOWN)){
					if(board.checkMoveDOWN(currentChip) && !alreadymove){
						board.moveDOWN(currentChip);
						alreadymove = true;
						this.movingId = currentChip.id;
						handler.postDelayed(new Timer(Direction.DOWN), TIMER_PERIOD);
						moving = true;
						this.touchedPoint = touchPt;
						tesu++;
					}
				}
				if(direction.contains(DIRECTION.LEFT)){
					if(board.checkMoveLEFT(currentChip) && !alreadymove){
						board.moveLEFT(currentChip);
						alreadymove = true;
						this.movingId = currentChip.id;
						handler.postDelayed(new Timer(Direction.LEFT), TIMER_PERIOD);
						moving = true;
						this.touchedPoint = touchPt;
						tesu++;
					}
				}
				if(direction.contains(DIRECTION.RIGHT)){
					if(board.checkMoveRIGHT(currentChip) && !alreadymove){
						board.moveRIGHT(currentChip);
						alreadymove = true;
						this.movingId = currentChip.id;
						handler.postDelayed(new Timer(Direction.RIGHT), TIMER_PERIOD);
						moving = true;
						this.touchedPoint = touchPt;
						tesu++;
					}
				}
				if(board.checkClaer()){
					clearFlag = true;
				}
				repaint();					
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		}
		return true;
	}
	
	private Rect getBoardRect(int id){
		Rect r;
		switch(id){
		case 0:
		case 2:
		case 8:
		case 9:
			r = new Rect(0,0, CELL_SIZE, CELL_SIZE*2);
			break;
		case 1:
			r = new Rect(CELL_SIZE, CELL_SIZE, CELL_SIZE*3, CELL_SIZE*3);
			break;
		case 4:
			r = new Rect(CELL_SIZE*2, CELL_SIZE*2, CELL_SIZE*4, CELL_SIZE*3);
			break;
		case 3:
		case 5:
		case 6:
		case 7:
			r = new Rect(0,0, CELL_SIZE, CELL_SIZE);
			break;
		default:
			throw new AssertionError();			
		}
		return r;
	}
	
	class SurfaceViewCallBack implements SurfaceHolder.Callback{

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Canvas canvas = holder.lockCanvas();
			holder.unlockCanvasAndPost(canvas);
			repaint();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Canvas canvas = holder.lockCanvas();
			holder.unlockCanvasAndPost(canvas);
			repaint();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
		}
		
	}
	
	/**
	 * 手数保存
	 */
	private void saveMinTesu(){
		SharedPreferences mintesuPref = this.getContext().getSharedPreferences("mintesu", Context.MODE_PRIVATE);
		Editor editor = mintesuPref.edit();
		editor.putInt("mintesu", mintesu);
		editor.commit();
		//    	isLockDisable = flag.getBoolean(ISLOCKDISABLE, false);
//    	checkState();
	}
	
	public void saveTime(){
		SharedPreferences mintesuPref = this.getContext().getSharedPreferences("time", Context.MODE_PRIVATE);
		Editor editor = mintesuPref.edit();
		editor.putLong("time", timeSec);
		editor.commit();
	}
	
	/**
	 * tureでタイマー開始
	 * @param timerenable
	 */
	public void timerEnable(boolean timerenable){
		if(timerenable){
//			handler.postDelayed(this.timerThread, 1000);
			this.timerThread.setEnable(true);
			handler.postDelayed(this.timerThread, 1000);
		}else{
//			handler.removeCallbacks(this.timerThread);
			this.timerThread.setEnable(false);
		}
	}

	
	/**
	 * プレイ時間と手数を読み込む
	 */
	private void loadData(){
		SharedPreferences mintesuPref = this.getContext().getSharedPreferences("mintesu", Context.MODE_PRIVATE);
		mintesu = mintesuPref.getInt("mintesu", 9999);
		SharedPreferences time = this.getContext().getSharedPreferences("time", Context.MODE_PRIVATE);
		timeSec = time.getLong("time", 0);

	}
	
	class Timer extends Thread{
	
		private Direction direction;
		public final int COUNT = 6;
		public int count;
		public Timer(Direction d){
			count = COUNT;
			direction = d;
			switch(direction){
			case UP:
				moveOffsety = CELL_SIZE;
				break;
			case DOWN:
				moveOffsety = -CELL_SIZE;
				break;
			case LEFT:
				moveOffsetx = CELL_SIZE;
				break;
			case RIGHT:
				moveOffsetx = -CELL_SIZE;
				break;
			}

		}
		
		@Override
		public void run(){
			while(count > 0){
				count--;
				switch(direction){
				case UP:
					moveOffsety -= CELL_SIZE / COUNT;
					break;
				case DOWN:
					moveOffsety += CELL_SIZE / COUNT;
					break;
				case LEFT:
					moveOffsetx -= CELL_SIZE / COUNT;
					break;
				case RIGHT:
					moveOffsetx += CELL_SIZE / COUNT;
					break;		
				}
				repaint();
//				view.invalidate();
//				removeCallbacks(this);
				removeCallbacks(this);
				postDelayed(this, TIMER_PERIOD);
			}
			movingId = STOP;
			moveOffsetx = 0;
			moveOffsety= 0;
			moving = false;
			repaint();
//			view.invalidate();

		}
	}
	
	/**
	 * 描画用スレッド
	 * @author akira
	 *
	 */
	public class TimerSec extends Thread{
		
		boolean enable = true;
		
		public void setEnable(boolean b){
			enable = b;
		}
		
		@Override
		public void run(){
			if(enable && mintesu == 9999){
				timeSec++ ;
				
				removeCallbacks(this);
				postDelayed(this, 1000);
				repaint();
			}
		}
	}
	
}
