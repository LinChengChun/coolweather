package util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	private final static String TAG = "HttpUtil";
	
	public static String sendHttpRequest(final String address, 
			final HttpCallbackListener listener){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Log.i(TAG, "new thread begin");
					if(address == null) return;

					URL url = new URL(address);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					connection.setDoInput(true);
					connection.setDoOutput(true);

					InputStream in = connection.getInputStream();
					//BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//					String response = null;
//					String temp = null;
//					int ret = 0;
//					int length = 0;
//					byte buf[] = new byte[1024];
//
//					while ( (ret = bufferedInputStream.read(buf, 0, buf.length))!= -1 ){
//						temp = new String(buf);
//						Log.i(TAG, temp);
//						response += temp;
//					}

					String line;
					StringBuilder response = new StringBuilder();
					while ( null != (line = reader.readLine())){
						response.append(line);
					}

					if(listener != null){
						listener.onFinish(response.toString());
					}
					Log.i(TAG, "httpClient.execute(httpGet) 2");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					if (listener != null) {
						// �ص�onError() ����
						listener.onError(e);
					}
				}
				Log.i(TAG, "new thread end");
			}
		}).start();
		
		return null;
	}
}
