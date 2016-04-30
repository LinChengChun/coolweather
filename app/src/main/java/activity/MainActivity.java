package activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TextView;

import com.coolweather.app.R;

import util.HttpCallbackListener;
import util.HttpUtil;

public class MainActivity extends AppCompatActivity {

    private ListView mListView = null;//定义一个 列表 控件
    private WebView mWebView = null;
    private TextView textView = null;
    private String weather_url = "http://flash.weather.com.cn/wmaps/xml/china.xml";

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case 200:
                    textView.setText((CharSequence) msg.obj);
                    break;
                default:break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView2);
        HttpUtil.sendHttpRequest(weather_url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
//                textView.setText(response);
                Message msg = new Message();
                msg.what = 200;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(Exception e) {

            }
        });
//        mWebView = (WebView) findViewById(R.id.webView);

    }
}
