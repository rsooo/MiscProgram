package coindozer.app;

import android.graphics.Canvas;
import android.graphics.Point;

/*****
 * 
 * @author akira
 *
 */
public class Coin {
	public static int ACCELETE = 1;
	private int size;
	private Point pt;
	private int velocity;
	//�R�C���������Ă邩�ǂ���
	private boolean isFall = true;
	
	public int getSize() {
		return size;
	}

	public Point getPt() {
		return pt;
	}

	public int getVelocity() {
		return velocity;
	}

	public void move(){
		pt.y += this.velocity;
		if(this.isFall){
			velocity += ACCELETE;
		}
	}
	
	public Coin(Point p, int size, int v){
		this.pt = p;
		this.size = size;
		this.velocity = v;
	}
	
	public Coin(Point p, int size, int v, boolean isfall){
		this(p,size,v);
		this.isFall = isfall;
	}
	
	/**
	 * ���ɓ��������Ƃ��̏���
	 */
	public void hitFloor(){
		//�Ƃ肠�������x0�ɂ���
		this.velocity = 0;
		this.isFall = false;
	}

	public boolean isFall() {
		return isFall;
	}

}
