package activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;

import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;

/**
 * Created by Administrator on 2016/4/30.
 */
public class WeatherActivity extends Activity {

    private static final String TAG = "WeatherActivity";
    private Context mContext = null;//上下文
    private String countyCode = null;//县 代码

    private TextView tvCityName = null;//相关天气信息显示控件
    private TextView tvTemperature = null;
    private TextView tvWeatherInfo = null;
    private TextView tvPublishTime = null;
    private TextView tvCurrentDate = null;
    private LinearLayout weatherInfoLinearLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        mContext = getApplicationContext();//上下文

        countyCode = getIntent().getStringExtra("countyCode");
        Log.i(TAG, countyCode);

        // 初始化各控件
        tvCityName = (TextView) findViewById(R.id.tvCityName);//绑定控件
        tvTemperature = (TextView) findViewById(R.id.tvTemperature);
        tvWeatherInfo = (TextView) findViewById(R.id.tvWeatherInfo);
        tvPublishTime = (TextView) findViewById(R.id.tvPublishTime);
        tvCurrentDate = (TextView) findViewById(R.id.tvCurrentDate);
        //weatherInfoLinearLayout = (LinearLayout) findViewById(R.layout.weather_layout);

        if(!TextUtils.isEmpty(countyCode)){
            // 有县级代号时就去查询天气
            tvPublishTime.setText("同步中...");
            tvTemperature.setText("同步中...");
            tvWeatherInfo.setText("同步中...");
            tvCityName.setText("同步中...");
            tvCurrentDate.setText("同步中...");
            queryWeatherCode(countyCode);
        }else {
            showWeatherInfo();
        }
    }

    public static void actionStart(){

    }

    /**
     * 根据县级代号，查询天气代号
     * **/
    private void queryWeatherCode(String countyCode){

        String address = "http://www.weather.com.cn/data/list3/city" +
                countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }

    /**
     * 根据天气代号，查询天气信息
     * **/
    private void queryWeatherInfo(String weatherCode){

        String address = "http://www.weather.com.cn/data/cityinfo/" +
                weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    private void queryFromServer(String address, final String type){

        try {

            HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Log.i(TAG, "response ="+ response);
                    if(type.equalsIgnoreCase("countyCode")){

                        String[] array = response.split("\\|");

                        if (array != null && array.length == 2){
                            for (int i=0; i<array.length; i++)
                                Log.i(TAG, array[i]);
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }else if(type.equalsIgnoreCase("weatherCode")){
                        Utility.handleWeatherResponse(mContext, response);

                        showWeatherInfo();
                    }
                }

                @Override
                public void onError(Exception e) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showWeatherInfo(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("weatherInfo", Context.MODE_PRIVATE);

        final String cityName = sharedPreferences.getString("citiName", "error");
        final String weatherCode = sharedPreferences.getString("weatherCode", "error");
        final String temp1 = sharedPreferences.getString("temp1", "error");
        final String temp2 = sharedPreferences.getString("temp2", "error");
        final String weatherDesp = sharedPreferences.getString("weatherDesp", "error");
        final String publishTime = sharedPreferences.getString("publishTime", "error");
        final String currentDate = sharedPreferences.getString("currentDate", "2016年04月30日");

        Log.i(TAG, cityName+weatherCode+temp1+temp2+weatherDesp+publishTime);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvCityName.setText(cityName);
                tvTemperature.setText(temp2+"~"+temp1);
                tvWeatherInfo.setText(weatherDesp);
                tvPublishTime.setText("今天" + publishTime + "发布");
                tvCurrentDate.setText(currentDate);
            }
        });

    }
}
