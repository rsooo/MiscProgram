package jp.rsooo.app.cockroach;

/**
 * cock表すクラス
 * @author akira
 *
 */
public class Cock {
	//足動かすためのフラグ
	boolean ismove = false;
	
	int hp = 1;
	int x;
	int y;
	int vx;
	int vy;
	int width; //サイズ(後で)
	int height;
	int direction;
	
	//どれくらい長生きしているか
	private int lifecycle = 0;
	
	//画面から消えるまでのカウント
	public int deadCount = 100;
	
	public Cock(int x, int y, int vx, int vy, int direction, int hp){
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.hp = hp;
		this.direction = direction;
		lifecycle = 0;
	}
	
	public void move(){
		x += vx;
		y += vy;
		//ビット反転
		ismove = ismove ? false : true;
		if(hp == 0){
			deadCount--;
		}else{
			lifecycle++;
		}
	}
	
	/**
	 * Cock倒したらTrue返す
	 * @return
	 */
	public boolean damage(){
		if(hp > 0){
			hp--;
			if(hp == 0){
				vx = vy = 0;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 中心ｘ座標返す
	 */
	public int getCenterX(){
		return x + GlobalSettings.COCKWIDTH / 2;
	}
	
	public int getCenterY(){
		return y + GlobalSettings.COCKHEIGHT / 2;
	}

	/**
	 * このCockを倒したときのスコア出す
	 * @param combo 連続して倒した数
	 * @return
	 */
	public int calcScore(int combo){
		int base = 100; //ベースは100
		int comboBonus = combo * 10;
		int life = this.lifecycle < 20 ? 20 - lifecycle : 0;
		int otherBonus = vy * vx * life;
		return base + comboBonus + otherBonus;
		
	}
}
