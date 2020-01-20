package jp.rsooo.app.cockroach;

import java.util.*;

import com.google.android.gms.games.Game;
import com.google.android.gms.games.Games;

import jp.rsooo.app.lib.hiscore.MinimumHiScoreRecode;

import android.graphics.Point;
import android.util.Log;

/**
 * �S�L�u���S�̂��Ǘ�����N���X
 * @author akira
 *
 */
public class CockManager {

	List<Cock> cockList = new ArrayList<Cock>();
	//��ʂ���cock��������܂ł̃}�[�W��
	final static int MARGIN = 200;
	int canvasHeight;
	int canvasWidth;
	final CockroachView cockroachView;
	
	public CockManager(CockroachView cockroachView, int width, int height){
		this.cockroachView = cockroachView;
		this.canvasHeight = height;
		this.canvasWidth = width;
	}
	
	/**
	 * cock�ǉ�
	 */
	public void add(Cock c){
		this.cockList.add(c);
	}
	
	public List<Cock> getCockList(){
		return this.cockList;
	}
	
	public void move(){
		for(final Cock c : cockList){
			c.move();
		}
	}
	
	/**
	 * �^�b�`����Cock�����邩���`�F�b�N
	 * ����������HP���炷
	 */
	public void touch(int x, int y){
		for(Cock c : this.cockList){
			int distance = this.calcDistance2(x, y, c.getCenterX(), c.getCenterY());
			if(distance < GlobalSettings.COCKWIDTH * GlobalSettings.COCKWIDTH){
				if(c.damage()){
					int score = c.calcScore(this.cockroachView.playerData.combo++);
					this.cockroachView.playerData.score += score;
				}else{
					this.cockroachView.playerData.score += 5;
				}
				if(this.cockroachView.soundEnabled){ //���y�Đ��L��Ȃ炨�Ɩ炷
					this.cockroachView.soundManager.play(0);
				}
			}else{
//				this.cockroachView.soundManager.play(3);
			}
		}
	} 
	
	public void removeOutofCock(){
		for(int i = cockList.size() - 1; i >= 0; i--){
			Cock c = cockList.get(i);
					
			if(c.x < -MARGIN || c.y  < -MARGIN 
					|| c.x > canvasWidth + MARGIN || c.y > canvasHeight + MARGIN){
				cockList.remove(i);
				//���C�t���P�Ȃ̂�Gameover�ɂȂ钼�O�B������X�R�A�ۑ�
				if(this.cockroachView.playerData.life == 1){
					MinimumHiScoreRecode newRecode = new MinimumHiScoreRecode();
					newRecode.setHiScore(this.cockroachView.playerData.score);
					final int rank =  this.cockroachView.hiScoreManager.addScore(newRecode);
                    Log.d("LOG", "rank:"+ rank);
                    if(rank == 0){
                        this.cockroachView.hiScoreManager.setHiscoreUpdated(true);
                    }
                    //    Games.Leaderboards.submitScore();

					this.cockroachView.hiScoreManager.saveHiScore();
					this.cockroachView.gameOverTouchDisable.start();
				}
				this.cockroachView.playerData.miss();
				
//				Log.i("coin", "cock removed");
			}
		}
	}
	
	/**
	 * �|����CO����������
	 */
	public void removeDeadCock(){
		for(int i = cockList.size() - 1; i >= 0; i--){
			Cock c = cockList.get(i);
					
			if(c.deadCount < 0){
				cockList.remove(i);
//				Log.i("coin", "cock dead removed");
			}
		}
	} 
	
	/**
	 * ��ʏォ��cock�S������
	 */
	public void clear(){
		this.cockList.clear();
	}
	
	/**
	 * �Q�̍��W���狗���̓����Z�o����
	 * @return 
	 */
	private int calcDistance2(int ax, int ay, int bx, int by){
		return (ax - bx) * (ax - bx) + (ay - by) * (ay - by);
	}

	

}
