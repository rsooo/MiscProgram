package jp.stressfreesoft.app.chat;

import com.google.android.maps.GeoPoint;

/**
 * Google Map��ɕ\�����邽�߂ɕK�v�ȃf�[�^�������N���X
 * �Ƃ肠����id, username, latitude, longitude������
 * @author akira
 *
 */
public class UserListData {
	public int id;
	public String username;
	public ESEX sex; 
	public int latitude;
	public int longitude;
	
	/**
	 * Google Maps�Ŏg��GeoPoint��Ԃ�
	 * @return
	 */
	public GeoPoint createGeoPoint(){
		return new GeoPoint(this.latitude, this.longitude);
	}
	
	public UserListData(int id, String username, String sex, int lati, int longi){
		this.id = id;
		this.username = username;
//		this.sex = sex;
		this.latitude = lati;
		this.longitude = longi;
		this.sex = ESEX.toESEX(sex);
		
	}

}
