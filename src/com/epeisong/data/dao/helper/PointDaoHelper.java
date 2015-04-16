package com.epeisong.data.dao.helper;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.epeisong.EpsApplication;
import com.epeisong.model.Point;
import com.epeisong.model.Point.PointCode;

/**
 * 界面新消息提醒
 * @author poet
 *
 */
public class PointDaoHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    public PointDaoHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, getDB_name(), factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = t_point.getSql();
        db.execSQL(sql);
        PointCode[] values = PointCode.values();
        for (PointCode code : values) {
            Point p = new Point();
            p.setCode(code.getValue());
            db.insert(getTableName(), null, p.getContentValues());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private static String getDB_name() {
        return EpsApplication.getDbName("point");
    }

    public String getTableName() {
        return t_point.T_NAME;
    }

    public static class t_point {

        public static String T_NAME = "point";

        public static String getSql() {
            Map<String, String> map = new HashMap<String, String>();
            map.put(FIELD.CODE, "TEXT UNIQUE PRIMARY KEY");
            map.put(FIELD.SHOW, "TEXT");
            map.put(FIELD.NOTE, "TEXT");

            StringBuilder sb = new StringBuilder("CREATE TABLE " + T_NAME + " (");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append(entry.getKey() + " " + entry.getValue() + ",");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.append(" );").toString();
        }

        public static interface FIELD {
            String CODE = "code";
            String SHOW = "show";
            String NOTE = "note";
        }
    }
}
