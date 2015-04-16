package com.epeisong.data.dao.helper;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.epeisong.EpsApplication;

/**
 * 常用地址、联系人等数据
 * @author poet
 *
 */
public class CommonDataDaoHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    public CommonDataDaoHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, getDB_name(), factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String commonRegionSql = t_common_region.getSql();
        String commonLineSql = t_common_line.getSql();
        db.execSQL(commonRegionSql);
        db.execSQL(commonLineSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public String getCommonRegionTableName() {
        return t_common_region.T_NAME;
    }

    public String getCommonLineTableName() {
        return t_common_line.T_NAME;
    }

    private static String getDB_name() {
        return EpsApplication.getDbName("common_data");
    }

    private static String createSql(String tName, Map<String, String> map) {
        StringBuilder sb = new StringBuilder("CREATE TABLE " + tName + " (");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey() + " " + entry.getValue() + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.append(" );").toString();
    }

    public static class t_common_region {
        static String T_NAME = "common_region";

        public static String getSql() {
            return CommonDataDaoHelper.createSql(T_NAME, getFieldDesc());
        }

        private static Map<String, String> getFieldDesc() {
            Map<String, String> map = new HashMap<String, String>();
            map.put(FIELD.CODE, "TEXT UNIQUE");
            map.put(FIELD.TYPE, "TEXT");
            map.put(FIELD.COUNT, "INTEGER");
            map.put(FIELD.UPDATE_TIME, "TEXT");
            return map;
        }

        public static interface FIELD {
            String CODE = "code";
            String TYPE = "type";
            String COUNT = "count";
            String UPDATE_TIME = "update_time";
        }
    }

    public static class t_common_line {
        static String T_NAME = "common_line";

        public static String getSql() {
            return CommonDataDaoHelper.createSql(T_NAME, getFieldDesc());
        }

        private static Map<String, String> getFieldDesc() {
            Map<String, String> map = new HashMap<String, String>();
            map.put(FIELD.START_CODE, "TEXT");
            map.put(FIELD.START_NAME, "TEXT");
            map.put(FIELD.START_TYPE, "TEXT");
            map.put(FIELD.END_CODE, "TEXT");
            map.put(FIELD.END_NAME, "TEXT");
            map.put(FIELD.END_TYPE, "TEXT");
            map.put(FIELD.SCENE, "TEXT");
            map.put(FIELD.COUNT, "TEXT");
            map.put(FIELD.UPDATE_TIME, "TEXT");
            return map;
        }

        public static interface FIELD {
            String START_CODE = "start_code";
            String START_NAME = "start_name";
            String START_TYPE = "start_type";
            String END_CODE = "end_code";
            String END_TYPE = "end_type";
            String END_NAME = "end_name";
            String SCENE = "scene"; // 使用场景
            String COUNT = "count";
            String UPDATE_TIME = "update_time";
        }
    }
}
