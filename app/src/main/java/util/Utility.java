package util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import model.City;
import model.CoolWeatherDataBaseAccess;
import model.County;
import model.Province;

/**
 * Created by Administrator on 2016/4/25.
 */
public class Utility {

    private static final String TAG = "Utility";
    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDataBaseAccess coolWeatherDataBaseAccess,
                                                               String response){

        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    // 将解析出来的数据存储到Province表
                    coolWeatherDataBaseAccess.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCitiesResponse(CoolWeatherDataBaseAccess coolWeatherDataBaseAccess,
                                               String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
//                    Log.i(TAG, c);
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    // 将解析出来的数据存储到City表
                    coolWeatherDataBaseAccess.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountiesResponse(CoolWeatherDataBaseAccess coolWeatherDataBaseAccess,
                                                 String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
//                    if(c.startsWith("null")){
//                        Log.i(TAG, c.subSequence(0, 3).toString());
//                    }
//                    Log.i(TAG, c);
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    // 将解析出来的数据存储到County表
                    coolWeatherDataBaseAccess.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**url1:http: www.weather.com.cn/data/list3/city190404.xml
     * response: 190404|101190404
     * url: http://www.weather.com.cn/data/cityinfo/101190404.html
     * 先获取天气代号，再获取天气信息
     * 格式如下：{"weatherinfo":
     {"city":"昆山","cityid":"101190404","temp1":"21℃","temp2":"9℃",
     "weather":"多云转小雨","img1":"d1.gif","img2":"n7.gif","ptime":"11:00"}
     }
     *  解析服务器返回的数组，得到天气信息
     *  **/
    public static void handleWeatherResponse(Context context, String response) {
//        JSONArray jsonArray = new JSONArray(response);//传入 字符串，并创建一个JSONArray实例对象
//        for(int i=0; i<jsonArray.length(); i++){
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");

            //WeatherInfo weatherInfo = new WeatherInfo();
            String city = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");

            Log.i(TAG, city+temp1+temp2+weatherDesp+publishTime);

            saveWeatherInfo(context, city, weatherCode, temp1, temp2,
                    weatherDesp, publishTime);
        }catch (Exception e){

        }
    }

    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
     */
    public static void saveWeatherInfo(Context context, String cityName,
                                       String weatherCode, String temp1, String temp2, String weatherDesp, String
                                               publishTime) {

        SharedPreferences mSharedPreferences = context.getSharedPreferences("weatherInfo", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("citiName", cityName);
        editor.putString("weatherCode", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weatherDesp", weatherDesp);
        editor.putString("publishTime", publishTime);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        editor.putString("currentDate", sdf.format(new Date()));
        editor.commit();
    }
}