package jp.rsooo.app.cockroach;

/**
 * cock�\���N���X
 * @author akira
 *
 */
public class Cock {
	//�����������߂̃t���O
	boolean ismove = false;
	
	int hp = 1;
	int x;
	int y;
	int vx;
	int vy;
	int width; //�T�C�Y(���)
	int height;
	int direction;
	
	//�ǂꂭ�炢���������Ă��邩
	private int lifecycle = 0;
	
	//��ʂ��������܂ł̃J�E���g
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
		//�r�b�g���]
		ismove = ismove ? false : true;
		if(hp == 0){
			deadCount--;
		}else{
			lifecycle++;
		}
	}
	
	/**
	 * Cock�|������True�Ԃ�
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
	 * ���S�����W�Ԃ�
	 */
	public int getCenterX(){
		return x + GlobalSettings.COCKWIDTH / 2;
	}
	
	public int getCenterY(){
		return y + GlobalSettings.COCKHEIGHT / 2;
	}

	/**
	 * ����Cock��|�����Ƃ��̃X�R�A�o��
	 * @param combo �A�����ē|������
	 * @return
	 */
	public int calcScore(int combo){
		int base = 100; //�x�[�X��100
		int comboBonus = combo * 10;
		int life = this.lifecycle < 20 ? 20 - lifecycle : 0;
		int otherBonus = vy * vx * life;
		return base + comboBonus + otherBonus;
		
	}
}
