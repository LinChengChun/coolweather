/**
 * 
 */
package util;

/**
 * @author LAMBOAIR
 *
 */
public interface HttpCallbackListener {

	void onFinish(String response);
	void onError(Exception e);
}
