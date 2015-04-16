package com.epeisong.data.dao.helper;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.epeisong.EpsApplication;

public class FansDaoHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;

	public FansDaoHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, getDB_name(), factory, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = t_fans.getSql();
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	private static String getDB_name() {
		return EpsApplication.getDbName("fans");
	}

	public String getTableName() {
		return t_fans.T_NAME;
	}

	public static class t_fans {

		public static String T_NAME = "fans";

		public static String getSql() {
			Map<String, String> map = new HashMap<String, String>();
			map.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
			map.put(FIELD.ID, "TEXT UNIQUE");
			map.put(FIELD.NAME, "TEXT");
			map.put(FIELD.SOURCE, "TEXT");
			map.put(FIELD.STATUS, "TEXT");
			map.put(FIELD.TIME, "TEXT");

			StringBuilder sb = new StringBuilder("CREATE TABLE " + T_NAME
					+ " (");
			for (Map.Entry<String, String> entry : map.entrySet()) {
				sb.append(entry.getKey() + " " + entry.getValue() + ",");
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.append(" );").toString();
		}

		public static interface FIELD {
			String ID = "id";
			String NAME = "name";
			String SOURCE = "source";
			String STATUS = "status";
			String TIME = "time";
		}
	}
}
