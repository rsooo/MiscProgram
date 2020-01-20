package coindozer.app;

import java.util.*;

import android.graphics.Point;
import android.util.Log;

/**
 * コイン全体をこいつが管理する.
 * 
 * @author akira
 *
 */
public class CoinManager {
	
	
	List<Coin> coinList = new ArrayList<Coin>();
	
	
	
	public void coinMove(){
		for(Coin c : this.coinList){
			c.move();
		}
	}

	public List<Coin> getCoinList() {
		return coinList;
	}
	
	/**
	 * 画面外のコインを全て消す
	 */
	public void removeOutofCoin(int width, int height){
		for(int i = this.coinList.size() - 1; i >= 0; i--){
			Coin c = this.coinList.get(i);
			Point pt = c.getPt();
			if(pt.x + c.getSize() < 0 || pt.y + c.getSize() < 0 
					|| pt.x > width || pt.y > height){
				this.coinList.remove(i);
				Log.i("coin", "coin removed");
			}
		}
	}
	
	public boolean addCoin(final Coin c){
		return this.coinList.add(c);
	}
	
	/**
	 * 床に当たったコインをひっくり返す
	 */
	public void checkHitFloor(){
		final int floorY = (int)(GrobalData.FLAT_RATIO * GrobalData.canvasHeight);
		for(Coin c : this.coinList){
			if(c.getPt().y  > floorY && c.isFall()){
				c.getPt().y = floorY;
				c.hitFloor();
			}
		}
	}
	
}
