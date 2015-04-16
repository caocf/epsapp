package com.epeisong.data.dao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.epeisong.EpsApplication;
import com.epeisong.data.dao.helper.CommonDataDaoHelper;
import com.epeisong.data.dao.helper.CommonDataDaoHelper.t_common_line;
import com.epeisong.model.CommonLine;

/**
 * 常用线路
 * @author poet
 *
 */
public class CommonLineDao {

    private static CommonLineDao dao = new CommonLineDao();

    private CommonDataDaoHelper mDaoHelper;
    private String mTableName;

    private WeakReference<CommonLineObserver> mObserverRef;

    private CommonLineDao() {
        mDaoHelper = new CommonDataDaoHelper(EpsApplication.getInstance(), null, null, 0);
        mTableName = mDaoHelper.getCommonLineTableName();
    }

    public static CommonLineDao getInstance() {
        return dao;
    }

    public synchronized void insert(CommonLine line) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        Cursor c = db.query(mTableName, null, t_common_line.FIELD.START_CODE + "=? and " + t_common_line.FIELD.END_CODE
                + "=?", new String[] { String.valueOf(line.getStart_code()), String.valueOf(line.getEnd_code()) },
                null, null, null);
        if (c.moveToFirst()) {
            CommonLine cl = parseCursor(c);
            line.setCount(cl.getCount() + 1);
            db.update(mTableName, line.getContentValues(), t_common_line.FIELD.START_CODE + "=? and "
                    + t_common_line.FIELD.END_CODE + "=?",
                    new String[] { String.valueOf(line.getStart_code()), String.valueOf(line.getEnd_code()) });
        } else {
            line.setCount(1);
            db.insert(mTableName, null, line.getContentValues());
        }
        c.close();
        db.close();
        notifyObserver();
    }

    public synchronized List<CommonLine> query(int scene, int size) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_common_line.FIELD.SCENE + "=?", new String[] { String.valueOf(scene) },
                null, null, t_common_line.FIELD.COUNT + " desc," + t_common_line.FIELD.UPDATE_TIME + " desc limit 0,"
                        + size);
        List<CommonLine> result = new ArrayList<CommonLine>();
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        db.close();
        return result;
    }

    private void notifyObserver() {
        if (mObserverRef != null && mObserverRef.get() != null) {
            mObserverRef.get().onCommonLineChange();
        }
    }

    private CommonLine parseCursor(Cursor c) {
        CommonLine line = new CommonLine();
        line.setStart_code(c.getInt(c.getColumnIndex(t_common_line.FIELD.START_CODE)));
        line.setStart_name(c.getString(c.getColumnIndex(t_common_line.FIELD.START_NAME)));
        line.setStart_type(c.getInt(c.getColumnIndex(t_common_line.FIELD.START_TYPE)));
        line.setEnd_code(c.getInt(c.getColumnIndex(t_common_line.FIELD.END_CODE)));
        line.setEnd_name(c.getString(c.getColumnIndex(t_common_line.FIELD.END_NAME)));
        line.setEnd_type(c.getInt(c.getColumnIndex(t_common_line.FIELD.END_TYPE)));
        line.setScene(c.getInt(c.getColumnIndex(t_common_line.FIELD.SCENE)));
        line.setCount(c.getInt(c.getColumnIndex(t_common_line.FIELD.COUNT)));
        line.setUpdate_time(c.getLong(c.getColumnIndex(t_common_line.FIELD.UPDATE_TIME)));
        return line;
    }

    public void addCommonLineObserver(CommonLineObserver ob) {
        mObserverRef = new WeakReference<CommonLineObserver>(ob);
    }

    public interface CommonLineObserver {
        void onCommonLineChange();
    }
}
