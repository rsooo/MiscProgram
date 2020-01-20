package jp.rsooo.app.cockroach;

import jp.rsooo.app.cockroach.CockroachView.SurfaceViewCallBack;

import jp.rsooo.app.cockroach.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainSurfaceView extends SurfaceView {

	int canvasHeight;
	int canvasWidth;
	Context c;
	Bitmap titleBitmap;
	Rect canvasRect;
	Rect bitmapRect;
	
	SurfaceHolder holder;
	
	public MainSurfaceView(Context context) {
		super(context);
		this.c = context;
		Resources res = getResources();
		titleBitmap = BitmapFactory.decodeResource(res, R.drawable.titleen);
		bitmapRect = new Rect(0,0, titleBitmap.getWidth(), titleBitmap.getHeight());
		this.getHolder().addCallback(new SurfaceViewCallBack());

		
		// TODO Auto-generated constructor stub
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
	
	class SurfaceViewCallBack implements SurfaceHolder.Callback{

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			
			
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {

			canvasWidth = width;
			canvasHeight = height;
			canvasRect = new Rect(0,0, width, height);
	
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
		}

	}


}
