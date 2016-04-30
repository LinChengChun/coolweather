package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.coolweather.app.Const;
import com.coolweather.app.R;

import java.util.ArrayList;
import java.util.List;

import model.City;
import model.CoolWeatherDataBaseAccess;
import model.County;
import model.MyAdapter;
import model.Province;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;

/**
 * Created by Administrator on 2016/4/26.
 */
public class ChooseAreaActivity extends Activity{

    private static final String TAG = "ChooseAreaActivity";

    private Context mContext = null;//全局上下文
    private int currentLevel = 0;//当前所处级别
//    private ArrayAdapter<String> adapter = null;//字符串类型适配器
    private MyAdapter adapter;
    private List<String> mList = new ArrayList<String>();//保存数据集合
    private CoolWeatherDataBaseAccess mCoolWeatherDataBaseAccess = null;//数据库操作类

    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;

    private List<Province> mListProvince = null;
    private List<City> mListCity = null;
    private List<County> mListCounty = null;

    private TextView mTextView = null;//标题字符串控件
    private ListView mListView = null;//列表控件
    private ProgressDialog mProgressDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area_layout);
        mContext = getApplicationContext();//初始化 全局上下文变量

        mTextView = (TextView) findViewById(R.id.textView);
        mListView = (ListView) findViewById(R.id.listView);

//        adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, mList);//初始化适配器
        adapter = new MyAdapter(mContext, mList, R.layout.item_layout);
        mListView.setAdapter(adapter);

        mCoolWeatherDataBaseAccess = CoolWeatherDataBaseAccess.getIntance(mContext);//实例化 数据库操作类：创建数据库和3张表格
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "position = "+position);
                if(currentLevel == Const.LEVEL_PROVINCE){

                    selectedProvince = mListProvince.get(position);
                    Log.i(TAG, selectedProvince.getProvinceName());
                    Log.i(TAG, selectedProvince.getProvinceCode());
                    Log.i(TAG, ""+selectedProvince.getId());
                    queryCities();
                }else if(currentLevel == Const.LEVEL_CITY){
                    selectedCity = mListCity.get(position);
                    Log.i(TAG, "getCityName: "+selectedCity.getCityName());
                    Log.i(TAG, "getCityCode: "+selectedCity.getCityCode());
                    Log.i(TAG, "getId: "+selectedCity.getId());
                    Log.i(TAG, "getProvinceId: "+selectedCity.getProvinceId());
                    queryCounties();
                }
            }
        });
        Log.i(TAG, "onCreate");
        queryProvinces();//第一次进入，ListView显示的应该是全国有多少个省
    }

    /**
     * 查询所有的省，并加载到ListView中
     *  查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     * **/
    private void queryProvinces(){
        mListProvince = CoolWeatherDataBaseAccess.loadProvince();//从数据库加载数据
        if( mListProvince.size() > 0 ){
            int i = 0;
            mList.clear();
            for(i = 0; i<mListProvince.size(); i++){
                Province province = mListProvince.get(i);
                String provinceName = province.getProvinceName();
                String provinceCode = province.getProvinceName();
                mList.add(provinceName);
            }
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTextView.setText("中国");
            currentLevel = Const.LEVEL_PROVINCE;
        }else {
            queryFromServer(null, "province");
        }

    }

    /**
     *  查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     * **/
    private void queryCities(){
        mListCity = mCoolWeatherDataBaseAccess.loadCities(selectedProvince.getId());
        if(mListCity.size() > 0){
            int i;
            mList.clear();
            for(i=0; i<mListCity.size(); i++){
                City city = mListCity.get(i);
                String cityName = city.getCityName();
                String cityCode = city.getCityCode();
                int provinceID = city.getProvinceId();
                Log.i(TAG, cityName+"-"+cityCode+"-"+provinceID);
                mList.add(cityName);
            }
            adapter.notifyDataSetChanged();
            mTextView.setText(selectedProvince.getProvinceName());
            mListView.setSelection(0);
            currentLevel = Const.LEVEL_CITY;
        }else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     *  查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     * **/
    private void queryCounties(){
        mListCounty = mCoolWeatherDataBaseAccess.loadCounties(selectedCity.getId());
        if(mListCounty.size() > 0){
            int i;
            mList.clear();
            for(i=0; i<mListCounty.size(); i++){
                County county = mListCounty.get(i);
                String countyName = county.getCountyName();
                String countyCode = county.getCountyCode();
                int cityId = county.getCityId();
                Log.i(TAG,countyName+"-"+countyCode+"-"+cityId);
                mList.add(countyName);
            }
            adapter.notifyDataSetChanged();
            mTextView.setText(selectedCity.getCityName());
            mListView.setSelection(0);
            currentLevel = Const.LEVEL_COUNTY;
        }else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    private void queryFromServer(final String code, final String type){
        String address;
        if( !TextUtils.isEmpty(code) ){//check code is nulll or code is "",return ture
            address ="http://www.weather.com.cn/data/list3/city" + code + ".xml";
        }else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        Log.i(TAG, "queryFromServer start");
        Log.i(TAG, "address :"+address);
        Log.i(TAG, "type :"+type);
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            boolean result = false;
            @Override
            public void onFinish(String response) {
                Log.i(TAG, "onFinish response: "+response);
                if(type.equalsIgnoreCase("province"))
                    result = Utility.handleProvincesResponse(mCoolWeatherDataBaseAccess, response);
                else if(type.equalsIgnoreCase("city"))
                    result = Utility.handleCitiesResponse(mCoolWeatherDataBaseAccess, response, selectedProvince.getId());
                else if(type.equalsIgnoreCase("county")) {
                    result = Utility.handleCountiesResponse(mCoolWeatherDataBaseAccess, response, selectedCity.getId());
                    Log.i(TAG, "result = "+result);
                    Log.i(TAG, "response: "+response);
                }
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                Log.i(TAG, "restart queryCounties() "+selectedCity.getCityName());
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void showProgressDialog(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }
    private void closeProgressDialog(){
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.i(TAG, "onBackPressed()");

        String[] array = mContext.getResources().getStringArray(R.array.levelArray);
        for(String temp: array){
            Log.i(TAG, temp);
        }
        Log.i(TAG, "currentLevel = "+currentLevel);
        Log.i(TAG, "currentLevel = "+array[currentLevel]);

        if(currentLevel == Const.LEVEL_COUNTY){//当前处于 县级，即返回上一层 市级
            queryCities();
        }else if(currentLevel == Const.LEVEL_CITY){//当前处于 市级，即返回上一层 省级
            queryProvinces();
        }else{
            super.onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }
}
