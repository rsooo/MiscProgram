package jp.rsooo.app.cockroach;

/**
 * �v���C���̃f�[�^�����N���X
 * @author akira
 *
 */
public class PlayerData {
	private boolean isGameover = false;
	int life = 5;
	int score = 0;
	
	//�A�����ē|������
	int combo = 0;
	
	public void clear(){
		life = 10;
		score = 0;
		combo = 0;
		isGameover = false;
		
	}
	
	public boolean miss(){
		combo = 0;
		life--;
		if(life == 0){
			isGameover = true;
		}
		return isGameover;
	} 
	
	public boolean isGameover(){
		return isGameover;
	}
	
}
