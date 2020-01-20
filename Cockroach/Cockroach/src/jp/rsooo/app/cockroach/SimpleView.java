package jp.rsooo.app.cockroach;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class SimpleView extends ImageView {
	public final int soundIconSize = 50; //ÇﬂÇÒÇ«Ç¢Ç©ÇÁÉTÉCÉYå≈íË

	Bitmap soundOnBitmap;
	   Bitmap soundOffBitmap;
	   Rect soundImgRect;
	   Rect soundDispRect;
	   
	   int width;
	   int height;
	   
	public boolean soundState;
	public SimpleView(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.soundOnBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sound);
        this.soundOffBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.soundoff);
        this.soundImgRect = new Rect(0,0, this.soundOnBitmap.getWidth(), this.soundOnBitmap.getHeight());
        this.setVisibility(VISIBLE);
	}
	
	public SimpleView(Context context, int width, int height){
		super(context);
		
        this.soundOnBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sound);
        this.soundOffBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.soundoff);
        this.soundImgRect = new Rect(0,0, this.soundOnBitmap.getWidth(), this.soundOnBitmap.getHeight());
        this.setVisibility(VISIBLE);
//        this.setSize(width, height);
	}
/*
	public void setSize(int width, int height){
//      this.soundDispRect = new Rect(this.width - soundIconSize, height - soundIconSize, width, height);		
//      this.soundDispRect = new Rect(0, 0, width, height);		
	}
	*/
	/*
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh){
		this.width = w;
		this.height = h;
        this.soundDispRect = new Rect(this.width - soundIconSize, this.height - soundIconSize, this.width, this.height);		
	}*/
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		if(this.soundDispRect == null){
			this.width = canvas.getWidth();
			this.height = canvas.getHeight();
			soundDispRect = new Rect(this.width - soundIconSize, this.height - soundIconSize, this.width, this.height);		
	
		}
		//sound on
		if(this.soundState){
			canvas.drawBitmap(this.soundOnBitmap, this.soundImgRect, this.soundDispRect, null);
		}else{
			canvas.drawBitmap(this.soundOffBitmap, this.soundImgRect, this.soundDispRect, null);
		}
	}
}
