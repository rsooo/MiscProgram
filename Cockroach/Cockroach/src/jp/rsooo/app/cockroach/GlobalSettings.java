package jp.rsooo.app.cockroach;

import android.graphics.Point;

public class GlobalSettings {
	//Ç∆ÇËÇ†Ç¶Ç∏ÉOÉçÅ[ÉoÉãÇ…ÇµÇ∆Ç≠
	static int COCKHEIGHT;
	static int COCKWIDTH;
	
	static final Point[] DIRECTIONV = new Point[16];
	
	static{
		DIRECTIONV[0] = new Point(-2, 0);
		DIRECTIONV[1] = new Point(-2, -1);
		DIRECTIONV[2] = new Point(-1, -1);
		DIRECTIONV[3] = new Point(-1, -2);
		DIRECTIONV[4] = new Point(0, -2);
		DIRECTIONV[5] = new Point(1, -2);
		DIRECTIONV[6] = new Point(1, -1);
		DIRECTIONV[7] = new Point(2, -1);
		DIRECTIONV[8] = new Point(2, 0);
		DIRECTIONV[9] = new Point(2, 1);
		DIRECTIONV[10] = new Point(1, 1);
		DIRECTIONV[11] = new Point(1, 2);
		DIRECTIONV[12] = new Point(0, 2);
		DIRECTIONV[13] = new Point(-1, 2);
		DIRECTIONV[14] = new Point(-1, 1);
		DIRECTIONV[15] = new Point(-2, 1);
	}

		
}
