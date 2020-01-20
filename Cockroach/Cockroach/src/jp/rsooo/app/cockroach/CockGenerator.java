package jp.rsooo.app.cockroach;

import java.util.*;

public class CockGenerator {
	List<CockGeneratePoint> generatePoints = new ArrayList<CockGeneratePoint>();
	
	/**
	 * �|�C���g�ǉ�
	 * @param generatePt
	 */
	public void addPoint(CockGeneratePoint generatePt){
		this.generatePoints.add(generatePt);		
	}
	
	/**
	 * �Ǘ����Ă���|�C���g�̒����烉���_���ɂP�I��
	 * @return
	 */
	public Cock generate(){
		if(generatePoints.size() <= 0){
			//�|�C���g�[���͂���
			throw new IllegalStateException();
		}
		Random rnd = new Random();
		int r = rnd.nextInt(generatePoints.size());
		return  generatePoints.get(r).generate();
	}
	
	public void clear(){
		this.generatePoints.clear();
	}
	
}
