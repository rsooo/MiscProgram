package jp.rsooo.app.bluetransfer.protcol;

/**
 * �d�b���f�[�^�𑗐M����N���X
 * @author a-saitoh
 *
 */
public class ContactSender {

	public ContactSender(){}
	
	public byte[] makeSendingPacket(String header, String data){
		StringBuffer sb = new StringBuffer();
		return sb.append(header).append(ProtocolConstants.DELIMITER).append(data).append("\n").toString().getBytes();
	}
	
	public byte[] makeFINPacket(){
		return ProtocolConstants.FIN.getBytes();
	}
}
