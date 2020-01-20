package jp.rsooo.app.bluetransfer;

import java.util.*;
//import org.apache.commons.lang.StringUtils;

/**
 * ���M���邽�߂̃R���^�N�g�̏��������N���X
 * @author a-saitoh
 *
 */
public class ContactInfo {

	private List<String> numberList = new ArrayList<String>();
	private List<String> addressList = new ArrayList<String>();
	private String name = Constants.NODATA;
	private String phoneticGiven = Constants.NODATA; //name
	private String phoneticMiddle = Constants.NODATA; 
	private String phoneticFamily = Constants.NODATA; //myoji
	
	public ContactInfo(String name, List<String> numList, List<String> addList, String familyName, String middleName, String givenName){
		this.name = name;
		this.numberList = numList;
		this.addressList = addList;
		this.phoneticFamily = familyName;
		this.phoneticMiddle = middleName;
		this.phoneticGiven = givenName;
	}

	public ContactInfo(){}
	
	public List<String> getNumberList() {
		return numberList;
	}
	public List<String> getAddressList() {
		return addressList;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	/*
	 * �d�b�ԍ��ꗗ�𕶎���ŕԂ�
	 */
	public String outputNumberAsString(){
		if(this.numberList.size() == 0){
			return Constants.NODATA;
		}
		String ret = "";
		for(String s : this.numberList){
			ret += s + "\n";
		}
		return ret.substring(0, ret.length() - 1); //�Ō�̉��s����Ȃ�
	}
	
	/*
	 * �A�h���X�ꗗ�𕶎���ŕԂ�
	 */
	public String outputAddressAsString(){
		if(this.addressList.size() == 0){
			return Constants.NODATA;
		}
		String ret = "";
		for(String s : this.addressList){
			ret += s + "\n";
		}
		return ret.substring(0, ret.length() - 1); //�Ō�̉��s����Ȃ�
	}

	public String getPhoneticGiven() {
		return phoneticGiven;
	}

	public String getPhoneticMiddle() {
		return phoneticMiddle;
	}

	public String getPhoneticFamily() {
		return phoneticFamily;
	}
}
