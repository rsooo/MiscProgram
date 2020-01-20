package jp.stressfreesoft.app.chat;

import com.google.android.maps.GeoPoint;

/**
 * Google Map上に表示するために必要なデータを扱うクラス
 * とりあえずid, username, latitude, longitudeを持つ
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
	 * Google Mapsで使うGeoPointを返す
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
