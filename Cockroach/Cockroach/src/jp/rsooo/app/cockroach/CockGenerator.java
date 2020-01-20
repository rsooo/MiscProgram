package jp.rsooo.app.cockroach;

import java.util.*;

public class CockGenerator {
	List<CockGeneratePoint> generatePoints = new ArrayList<CockGeneratePoint>();
	
	/**
	 * ポイント追加
	 * @param generatePt
	 */
	public void addPoint(CockGeneratePoint generatePt){
		this.generatePoints.add(generatePt);		
	}
	
	/**
	 * 管理しているポイントの中からランダムに１つ選ぶ
	 * @return
	 */
	public Cock generate(){
		if(generatePoints.size() <= 0){
			//ポイントゼロはだめ
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
