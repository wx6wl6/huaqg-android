package com.qlp2p.doctorcar.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qlp2p.doctorcar.common.MyGlobal;
import com.qlp2p.doctorcar.data.Citys;
import com.qlp2p.doctorcar.data.Province;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sx on 2017/2/14.
 */
public class LocalCityTable {
    //省份的表
    private static final String LOCAL_PROVINCE_TABLE_SQL = "create table if not exists " +
            "allProvince" +
            "(id " +
            "integer primary key autoincrement," +
            "provinceName varchar(20)," +
            "provinceId varchar(20))";
    private static final String DROP_LOCAL_PROVINCE_TABLE_SQL = "drop table if exists allProvince";
    private static final String PROVINCE_NAME = "provinceName";
    private static final String PROVINCE_ID = "provinceId";
    private static final String PROVINCE_TABLE = "allProvince";
    //城市的表
    private static final String LOCAL_CITY_TABLE_SQL = "create table if not exists allCity" +
            "(id " +
            "integer primary key autoincrement," +
            "provinceId varchar(20)," +
            "cityName varchar(20)," +
            "cityId varchar(20))";
    private static final String DROP_LOCAL_CITY_TABLE_SQL = "drop table if exists allCity";
    private static final String CITY_NAME = "cityName";
    private static final String CITY_ID = "cityId";
    private static final String CITY_TABLE = "allCity";

    private static LocalCityTable localCityTable;
    public DBHelper dbHelper;

    private LocalCityTable() {
        dbHelper = DBHelper.getInstance(MyGlobal.getInstance());
    }

    public synchronized static LocalCityTable getInstance() {
        if (localCityTable == null) {
            localCityTable = new LocalCityTable();
        }
        return localCityTable;
    }

    /**
     * 批量添加城市信息
     *
     * @param provinces
     */
    public void insertCityList(List<Province> provinces) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Province province : provinces) {
                ContentValues cv = new ContentValues();
                cv.put(PROVINCE_NAME, province.getName());
                cv.put(PROVINCE_ID, province.getId());
                db.insert(PROVINCE_TABLE, null, cv);
                for (Citys city : province.getCitys()) {
                    ContentValues cv2 = new ContentValues();
                    cv2.put(CITY_NAME, city.getName());
                    cv2.put(CITY_ID, city.getId());
                    cv2.put(PROVINCE_ID, province.getId());
                    db.insert(CITY_TABLE, null, cv2);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 批量更新城市信息
     *
     * @param provinces
     */
    public void updateCityList(List<Province> provinces) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Province province : provinces) {
                ContentValues cv = new ContentValues();
                cv.put(CITY_NAME, province.getName());
                cv.put(CITY_ID, province.getId());
                db.update(PROVINCE_TABLE, cv, PROVINCE_ID + "=?", new String[]{province.getId() + ""});
                for (Citys city : province.getCitys()) {
                    ContentValues cv2 = new ContentValues();
                    cv2.put(CITY_NAME, city.getName());
                    cv2.put(CITY_ID, city.getId());
                    cv2.put(PROVINCE_ID, province.getId());
                    db.update(CITY_TABLE, cv, CITY_ID + "=?", new String[]{city.getId() + ""});
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 获取省份城市
     * @param
     */
    public List<Province> getAllProvince(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from allProvince", null);
        List<Province> list = new ArrayList<>();
        while (cursor.moveToNext()){
            Province province = new Province();
            province.setId(cursor.getString(cursor.getColumnIndex(PROVINCE_ID)));
            province.setName(cursor.getString(cursor.getColumnIndex(PROVINCE_NAME)));
            list.add(province);
        }
        cursor.close();
        return list;
    }
    /**
     * 根据城市ID获取城市名
     * @param id
     */
    public String getProvinceByProvinceId(String id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from allProvince where provinceId =?", new
                String[]{id});
        String name = null;
        if(cursor.moveToFirst()){
            name = cursor.getString(cursor.getColumnIndex(PROVINCE_NAME));
        }
        cursor.close();
        return  name;
    }

    public String getProvincIdProvinceName(String province){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from allProvince where provinceName =?", new
                String[]{province});
        String name = null;
        if(cursor.moveToFirst()){
            name = cursor.getString(cursor.getColumnIndex(PROVINCE_ID));
        }
        cursor.close();
        return  name;
    }
    /**
     * 获取两级数据省份城市
     * @param
     */
    public HashMap<String, List<String>> getAllProvinceAndCity(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from allProvince", null);

        HashMap<String,List<String>> map = new HashMap<>();
        while (cursor.moveToNext()){
            Province province = new Province();
            province.setId(cursor.getString(cursor.getColumnIndex(PROVINCE_ID)));
            province.setName(cursor.getString(cursor.getColumnIndex(PROVINCE_NAME)));
            Cursor cursorCity = db.rawQuery("select * from allCity where provinceId ="+cursor
                    .getString(cursor.getColumnIndex(PROVINCE_ID)),null);
            List<String> citys = new ArrayList<>();
            while (cursorCity.moveToNext()){
                citys.add(cursorCity.getString(cursorCity.getColumnIndex(CITY_NAME)));
            }
            cursorCity.close();
            map.put(cursor.getString(cursor.getColumnIndex(PROVINCE_NAME)),citys);
            cursorCity.close();
        }
        cursor.close();
        return map;
    }
    /**
     * 获取城市
     * @param
     */
    public List<String> getAllCity() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from allProvince", null);

        HashMap<String, List<String>> map = new HashMap<>();
        List<String> citys = null;
        while (cursor.moveToNext()) {
            Province province = new Province();
            province.setId(cursor.getString(cursor.getColumnIndex(PROVINCE_ID)));
            province.setName(cursor.getString(cursor.getColumnIndex(PROVINCE_NAME)));
            Cursor cursorCity = db.rawQuery("select * from allCity where provinceId =" + cursor
                    .getString(cursor.getColumnIndex(PROVINCE_ID)), null);
            citys = new ArrayList<>();
            while (cursorCity.moveToNext()) {
                citys.add(cursorCity.getString(cursorCity.getColumnIndex(CITY_NAME)));
            }
            cursorCity.close();
            map.put(cursor.getString(cursor.getColumnIndex(PROVINCE_NAME)), citys);
            cursorCity.close();
        }
        cursor.close();
        return citys;
    }

    /**
     * 根据城市名获取城市ID
     * @param
     */
    public String getCityIdByCityName(String name){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from allCity where cityName =?", new String[]{name});
        String id = null;
        if(cursor.moveToFirst()){
            id = cursor.getString(cursor.getColumnIndex(CITY_ID));
        }
        cursor.close();
        return  id;
    }

    /**
     * 根据城市ID获取城市名
     * @param id
     */
    public String getCityByCityId(String id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from allCity where cityId =?", new
                String[]{id});
        String name = null;
        if(cursor.moveToFirst()){
            name = cursor.getString(cursor.getColumnIndex(CITY_NAME));
        }
        cursor.close();
        return  name;
    }


    public void createTable(SQLiteDatabase db) {
        db.execSQL(LOCAL_PROVINCE_TABLE_SQL);
        db.execSQL(LOCAL_CITY_TABLE_SQL);
    }

    public void dropTable(SQLiteDatabase db) {
        db.execSQL(DROP_LOCAL_PROVINCE_TABLE_SQL);
        db.execSQL(DROP_LOCAL_CITY_TABLE_SQL);
    }
}
