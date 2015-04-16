package com.test.log;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.epeisong.EpsApplication;
import com.test.log.CrashLogListActivity.CrashLog;

public class CrashLogDao {

	private static CrashLogDao dao = new CrashLogDao();

	private CrashLogDaoHelper mDaoHelper;

	private CrashLogDao() {
		mDaoHelper = new CrashLogDaoHelper(EpsApplication.getInstance(), "log.db", null,
				1);
	}

	public static CrashLogDao getInstance() {
		return dao;
	}

	public synchronized void insert(CrashLog log) {
		SQLiteDatabase db = mDaoHelper.getWritableDatabase();
		db.insert("log", null, log.getContentValues());
		db.close();
	}

	public synchronized void clear() {
		SQLiteDatabase db = mDaoHelper.getWritableDatabase();
		db.delete("log", null, null);
		db.close();
	}

	public synchronized void delete(CrashLog log) {
		SQLiteDatabase db = mDaoHelper.getWritableDatabase();
		db.delete("log", "_id=?", new String[] { String.valueOf(log.get_id()) });
		db.close();
	}

	public synchronized List<CrashLog> queryAll() {
		SQLiteDatabase db = mDaoHelper.getReadableDatabase();
		Cursor c = db.query("log", null, null, null, null, null, "time desc");
		List<CrashLog> list = new ArrayList<CrashLogListActivity.CrashLog>();
		while (c.moveToNext()) {
			list.add(parseCursor(c));
		}
		c.close();
		db.close();
		return list;
	}

	private CrashLog parseCursor(Cursor c) {
		CrashLog log = new CrashLog();
		log.set_id(c.getLong(c.getColumnIndex("_id")));
		log.setTime(c.getLong(c.getColumnIndex("time")));
		log.setContent(c.getString(c.getColumnIndex("content")));
		return log;
	}

}
