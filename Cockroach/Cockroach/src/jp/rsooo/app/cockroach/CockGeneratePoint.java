package jp.rsooo.app.cockroach;

import java.util.*;

import android.text.Layout.Directions;

/**
 * cock�̔����|�C���g���`����
 * @author akira
 *
 */
public class CockGeneratePoint {
	//�����|�C���g
	int x;
	int y;
	//���x
	int vwaight;
	//���x�̃����_����
//	int vxrange;
//	int vyrange;
	//direction��\�����X�g(0 - 15made)
	int[] directions;
	
	
	CockGeneratePoint(int x, int y, int[] directions, int vwaight){
		this.x = x;
		this.y = y;
		this.directions = directions;
//		this.vxrange = vxrange;
//		this.vyrange = vyrange;
		this.vwaight = vwaight;
	}
	
	public Cock generate(){
		Random rnd = new Random();
		int r = rnd.nextInt(directions.length);
		int direction = directions[r];
		int vx = GlobalSettings.DIRECTIONV[direction].x;
		int vy = GlobalSettings.DIRECTIONV[direction].y;
		return new Cock(x, y, vx * vwaight , vy * vwaight , direction,  1); 
	}
}
