package com.epeisong.data.dao.helper;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.epeisong.EpsApplication;

public class ChatRoomDaoHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;

	public ChatRoomDaoHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, getDB_name(), factory, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = t_chatroom.getSql();
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	private static String getDB_name() {
		return EpsApplication.getDbName("chatroom");
	}

	public String getTableName() {
		return t_chatroom.T_NAME;
	}

	public static class t_chatroom {

		public static String T_NAME = "chatroom";

		public static String getSql() {
			Map<String, String> map = new HashMap<String, String>();
			map.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
			map.put(FIELD.ID, "TEXT UNIQUE");
			map.put(FIELD.BUSINESS_TYPE, "TEXT");
			map.put(FIELD.BUSINESS_ID, "TEXT");
			map.put(FIELD.BUSINESS_OWNER_ID, "TEXT");
			map.put(FIELD.BUSINESS_DESC, "TEXT");
			map.put(FIELD.BUSINESS_EXTRA, "TEXT");
			map.put(FIELD.REMOTE_ID, "TEXT");
			map.put(FIELD.REMOTE_NAME, "TEXT");
			map.put(FIELD.REMOTE_LOGISTIC_TYPE_CODE, "TEXT");
			map.put(FIELD.REMOTE_LOGISTIC_TYPE_NAME, "TEXT");
			map.put(FIELD.UPDATE_TIME, "TEXT");
			map.put(FIELD.LAST_MSG, "TEXT");
			map.put(FIELD.NEW_MSG_COUNT, "TEXT");
			map.put(FIELD.STATUS, "TEXT");

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
			String BUSINESS_TYPE = "business_type";
			String BUSINESS_ID = "business_id";
			String BUSINESS_OWNER_ID = "business_owner_id";
			String BUSINESS_DESC = "business_desc";
			String BUSINESS_EXTRA = "business_extra";
			String REMOTE_ID = "remote_id";
			String REMOTE_NAME = "remote_name";
			String REMOTE_LOGISTIC_TYPE_CODE = "remote_logistic_type_code";
			String REMOTE_LOGISTIC_TYPE_NAME = "remote_logistic_type_name";
			String UPDATE_TIME = "update_time";
			String LAST_MSG = "last_msg";
			String NEW_MSG_COUNT = "new_msg_count";
			String STATUS = "status";
		}
	}
}
