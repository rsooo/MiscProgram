package jp.stressfreesoft.app.chat;

/**
 * «•Ê‚ğ•\‚·—ñ‹“Œ^
 * @author akira
 *
 */
public enum ESEX {
	MALE("male"), FEMALE("female"), UNKNOWN("unknown");
	private final String name;
	ESEX(String val){
		this.name = val;
	}
	public String toString(){
		return name;
	}
	
	public static ESEX toESEX(String sex){
		if(sex.equals("male")){
			return ESEX.MALE;
		}else if(sex.equals("female")){
			return ESEX.FEMALE;
		}else if(sex.equals("unknown")){
			return ESEX.UNKNOWN;			
		}else{
			throw new IllegalArgumentException();//ˆø”ƒGƒ‰[
//			return null;
		}
	}
}
