package com.epeisong.data.dao.helper;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 系统字典数据库
 * 
 * @author poet
 * 
 */
public class DictionaryDaoHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    public DictionaryDaoHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, "sys_dictionary.db", factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = t_sys_dictionary.getSql();
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    public String getTableName() {
        return t_sys_dictionary.T_NAME;
    }

    public static class t_sys_dictionary {

        public static String T_NAME = "dictionary";

        public static String getSql() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
            map.put(FIELD.ID, "INTEGER");
            map.put(FIELD.NAME, "TEXT");
            map.put(FIELD.SORT_ORDER, "INTEGER");
            map.put(FIELD.TYPE, "INTEGER");

            StringBuilder sb = new StringBuilder("CREATE TABLE " + T_NAME + " (");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append(entry.getKey() + " " + entry.getValue() + ",");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.append(" );").toString();
        }

        public static interface FIELD {
            String ID = "id";
            String NAME = "name";
            String SORT_ORDER = "sort_order";
            String TYPE = "type";
        }
    }
}
