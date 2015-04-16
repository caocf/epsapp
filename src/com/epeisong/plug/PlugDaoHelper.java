package com.epeisong.plug;

import com.epeisong.EpsApplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 插件数据库
 * @author poet
 *
 */
public class PlugDaoHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    public PlugDaoHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, EpsApplication.getDbName("plug"), factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String pointSql = TabPoint.getSql(); 
        db.execSQL(pointSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    
}
