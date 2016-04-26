package model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import db.CoolWeatherSQLiteOpenHelper;

/**
 * Created by Administrator on 2016/4/25.
 */
public class CoolWeatherDataBaseAccess {

    private static final String DB_Name = "CoolWeather.db";//数据库文件名称
    private static final int VERSION = 1;//数据库版本
    private static CoolWeatherDataBaseAccess coolWeatherDataBaseAccess = null;
    private static SQLiteDatabase mSQLiteDatabase = null;



    private CoolWeatherDataBaseAccess(Context context) {
        CoolWeatherSQLiteOpenHelper dbHelper =
                new CoolWeatherSQLiteOpenHelper(context, DB_Name, null, VERSION);
        mSQLiteDatabase = dbHelper.getReadableDatabase();
    }

    /**
     * 获取 CoolWeatherDataBaseAccess 的实类
     */
    public static CoolWeatherDataBaseAccess getIntance(Context context){
        if(coolWeatherDataBaseAccess == null){
            coolWeatherDataBaseAccess = new CoolWeatherDataBaseAccess(context);
        }

        return coolWeatherDataBaseAccess;
    }

    /**
     *保存一个Province实例保存到数据库
     * */
    public static void saveProvince(Province province){

        if(province != null){
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            mSQLiteDatabase.insert("Province", null, values);
        }
    }

    /**
     *加载全国所有的Province
     * */
    public static List<Province> loadProvince(){
        List<Province> list = new ArrayList<Province>();

        Cursor cursor = mSQLiteDatabase.query("Province", null, null, null, null, null, null);

        if(cursor.moveToFirst()){

            do{
                Province province = new Province();

                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));

                list.add(province);
            }while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将City实例存储到数据库。
     */
    public static void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            mSQLiteDatabase.insert("City", null, values);
        }
    }

    /**
     * 从数据库读取某省下所有的城市信息。
     */
    public static List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = mSQLiteDatabase.query("City", null, "province_id = ?",
                new String[] { String.valueOf(provinceId)  }, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor
                        .getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor
                        .getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将County实例存储到数据库。
     */
    public static void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());

            mSQLiteDatabase.insert("County", null, values);
        }
    }

    /**
     * 从数据库读取某城市下所有的县信息。
     */
    public static List<County> loadCounties(int cityId) {
        List<County> list = new ArrayList<County>();
        Cursor cursor = mSQLiteDatabase.query("County", null, "city_id = ?",
                new String[] { String.valueOf(cityId) }, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor
                        .getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor
                        .getColumnIndex("county_code")));
                county.setCityId(cityId);
                list.add(county);
            } while (cursor.moveToNext());
        }
        return list;
    }

}
