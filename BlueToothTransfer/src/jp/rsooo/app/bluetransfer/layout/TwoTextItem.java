package jp.rsooo.app.bluetransfer.layout;

/**
 * 2つのテキストを持つだけのクラス
 * @author akira
 *
 */
public class TwoTextItem {
	private String primaryText;
	private String secondaryText;
	
	public TwoTextItem(String p, String s){
		this.primaryText = p;
		this.secondaryText = s;
	}

	public String getPrimaryText() {
		return primaryText;
	}

	public void setPrimaryText(String primaryText) {
		this.primaryText = primaryText;
	}

	public String getSecondaryText() {
		return secondaryText;
	}

	public void setSecondaryText(String secondaryText) {
		this.secondaryText = secondaryText;
	}
	
	
}
