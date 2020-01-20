package jp.rsooo.app.cockroach;

import java.util.*;
import android.graphics.Rect;


/**
 * ステージを表す情報
 * @author akira
 *
 */
public class Stage {
	//cockの数
	final int num;
	final int period;
	final int backgroundId;
//	final int height;
//	final int width; 
	
	List<Rect> gabageRectList = new ArrayList<Rect>();
	List<Rect> trashboxRectList = new ArrayList<Rect>();
	List<CockGeneratePoint> genPtList = new ArrayList<CockGeneratePoint>();
	
	
	public Stage(int num, int period, int background, List<Rect> gab, List<Rect> trash, List<CockGeneratePoint> genpt){
		this.num = num;
		this.period = period;
		this.backgroundId = background;
//		this.height = height;
//		this.width = width;
		this.gabageRectList = gab;
		this.trashboxRectList = trash;
		this.genPtList = genpt;
	
	}
}
