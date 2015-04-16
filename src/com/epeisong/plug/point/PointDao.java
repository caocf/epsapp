package com.epeisong.plug.point;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;

import com.epeisong.EpsApplication;
import com.epeisong.plug.PlugDaoHelper;
import com.epeisong.plug.TabPoint;

/**
 * 提醒Dao
 * @author poet
 *
 */
@SuppressLint("UseSparseArrays")
public class PointDao {

    private static PointDao dao = new PointDao();

    private PlugDaoHelper mPlugDaoHelper;
    private String mTableName;

    Map<Integer, PointObserver> mObserverMap;

    private PointDao() {
        mPlugDaoHelper = new PlugDaoHelper(EpsApplication.getInstance(), null, null, 0);
        mTableName = TabPoint.NAME;
    }

    public static PointDao getInstance() {
        return dao;
    }

    public synchronized void change(int code, boolean show) {
        SQLiteDatabase db = mPlugDaoHelper.getWritableDatabase();
        Point p = new Point(code, show);
        do {
            p.setCode(code);
            boolean targetGet = notifyObserver(p);
            if (!show || !targetGet) {
                db.update(mTableName, p.getContentValues(), TabPoint.FIELD.CODE + "=?",
                        new String[] { String.valueOf(code) });
            } else {
                break;
            }
        } while ((code /= 100) > 0);
        db.close();
    }

    boolean notifyObserver(Point p) {
        if (mObserverMap != null && !mObserverMap.isEmpty()) {
            Set<Entry<Integer, PointObserver>> set = mObserverMap.entrySet();
            for (Entry<Integer, PointObserver> entry : set) {
                if (entry.getKey() == p.getCode()) {
                    return entry.getValue().onPoint(p);
                }
            }
        }
        return false;
    }

    public void addObserver(int code, PointObserver ob) {
        if (mObserverMap == null) {
            mObserverMap = new HashMap<Integer, PointObserver>();
        }
        mObserverMap.put(code, ob);
    }

    public void removeObserver(int code) {
        if (mObserverMap != null && mObserverMap.containsKey(code)) {
            mObserverMap.remove(code);
        }
    }
}
