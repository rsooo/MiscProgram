package jp.stressfreesoft.app.chat;

import java.io.Serializable;

import com.google.android.maps.GeoPoint;

/**
 * ���̃A�v���ň����ʒu�����`����N���X
 * �Ƃ肠�����S��public
 * @author akira
 *
 */
public class GeoLocation implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6193938258948415018L;
	/**
	 * 
	 */
	public int latitude;
	public int longitude;
	public int altitude;
	public int accuracy;
	
	public GeoLocation(){}
	public GeoLocation(int lati, int longi, int alti, int accu){
		this.latitude = lati;
		this.longitude = longi;
		this.altitude = alti;
		this.accuracy = accu;
	}
	
	/**
	 * Google Maps�Ŏg��GeoPoint��Ԃ�
	 * @return
	 */
	public GeoPoint createGeoPoint(){
		return new GeoPoint(this.latitude, this.longitude);
	}
	
}
