package jp.stressfreesoft.app.chat;

/**
 * Httpクエリの結果を処理できるインターフェース
 * @author akira
 *
 */
public interface QueryResponseReceiver {
	void analyzeResponse(String response);
}
