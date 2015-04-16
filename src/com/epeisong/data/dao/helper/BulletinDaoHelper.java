package com.epeisong.data.dao.helper;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.epeisong.EpsApplication;
import com.epeisong.data.dao.helper.BulletinDaoHelper.t_bulletin.FIELD;

/**
 * 联系人发布的公告
 * 
 * @author poet
 * 
 */
public class BulletinDaoHelper extends SQLiteOpenHelper {

    // private static final int DB_VERSION = 1;

    /**
     * 增加sender_name字段
     */
    private static final int DB_VERSION = 2;

    public BulletinDaoHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, getDB_name(), factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = t_bulletin.getSql();
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            String sql = "ALTER TABLE " + getTableName() + " ADD COLUMN " + FIELD.SENDER_NAME + " TEXT";
            db.execSQL(sql);
            db.delete(getTableName(), null, null);
        }
    }

    private static String getDB_name() {
        return EpsApplication.getDbName("bulletin");
    }

    public String getTableName() {
        return t_bulletin.T_NAME;
    }

    public static class t_bulletin {

        public static String T_NAME = "bulletin";

        public static String getSql() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
            map.put(FIELD.ID, "TEXT UNIQUE");
            map.put(FIELD.SENDER_ID, "TEXT");
            map.put(FIELD.SENDER_NAME, "TEXT");
            map.put(FIELD.CREATE_TIME, "TEXT");
            map.put(FIELD.UPDATE_TIME, "TEXT");
            map.put(FIELD.CONTENT, "TEXT");
            map.put(FIELD.CONTENT_TYPE, "TEXT");
            map.put(FIELD.STATUS, "TEXT");

            StringBuilder sb = new StringBuilder("CREATE TABLE " + T_NAME + " (");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append(entry.getKey() + " " + entry.getValue() + ",");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.append(" );").toString();
        }

        public static interface FIELD {
            String ID = "id";
            String SENDER_ID = "sender_id";
            String SENDER_NAME = "sender_name";
            String CREATE_TIME = "create_time";
            String UPDATE_TIME = "update_time";
            String CONTENT = "content";
            String CONTENT_TYPE = "content_type";
            String STATUS = "status";
        }
    }
}
