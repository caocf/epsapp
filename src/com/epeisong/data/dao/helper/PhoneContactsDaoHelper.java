package com.epeisong.data.dao.helper;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.epeisong.EpsApplication;

public class PhoneContactsDaoHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    public PhoneContactsDaoHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, getDB_name(), factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = t_phonecontacts.getSql();
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static String getDB_name() {
        return EpsApplication.getDbName("phonecontacts");
    }

    public String getTableName() {
        return t_phonecontacts.T_NAME;
    }

    public static class t_phonecontacts {

        public static final String T_NAME = "phonecontacts";

        public static String getSql() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
            map.put(FIELD.ID, "TEXT");
            map.put(FIELD.SORT_KEY, "TEXT");
            map.put(FIELD.NAME, "TEXT");
            map.put(FIELD.PINYIN, "TEXT");
            map.put(FIELD.NAME_PINYIN, "TEXT");
            map.put(FIELD.PHONE_NUM, "TEXT");
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
            String SORT_KEY = "sort_key";
            String NAME = "name";
            String PINYIN = "pinyin";
            String NAME_PINYIN = "name_pinyin";
            String PHONE_NUM = "phone_num";
            String STATUS = "status";
        }
    }
}
