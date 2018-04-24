package com.qlp2p.doctorcar.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VER = 1;
    private static DBHelper currentUserDBHelper=null;

    public synchronized static  DBHelper getInstance(Context context){
        if (currentUserDBHelper==null){
            String name = "qlp2p.db";
            currentUserDBHelper = new DBHelper(context,name,DB_VER);
        }
        return currentUserDBHelper;
    }


    private DBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LocalCityTable.getInstance().createTable(db);
    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        for (int i = oldVersion; i<newVersion; i++) {
            switch (i) {
                case 6:
                   // db.execSQL(" ALTER TABLE person ADD phone VARCHAR(12) NULL "); //往表中增加一列
                    Log.d("sql", "sql update success 6");
                case 5:
                    Log.d("sql", "sql update success 5");
                case 4:
                    Log.d("sql", "sql update success 4");
                case 3:
                    Log.d("sql", "sql update success 3");
                case 2:
                    Log.d("sql", "sql update success 2");
                case 1:
                    break;
            }
        }
    }

    public synchronized void closeHelper() {
        currentUserDBHelper = null;
    }
}

