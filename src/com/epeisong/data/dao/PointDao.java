package com.epeisong.data.dao;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.epeisong.EpsApplication;
import com.epeisong.MainActivity;
import com.epeisong.data.dao.helper.PointDaoHelper;
import com.epeisong.data.dao.helper.PointDaoHelper.t_point;
import com.epeisong.model.Point;
import com.epeisong.model.Point.PointCode;
import com.epeisong.utils.HandlerUtils;

public class PointDao {

    private static PointDao dao = new PointDao();

    private PointDaoHelper mDaoHelper;
    private String mTableName;

    private Map<Integer, WeakReference<PointObserver>> mObserverMap;

    private PointDao() {
        mDaoHelper = new PointDaoHelper(EpsApplication.getInstance(), null, null, 0);
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        db.close();
        mTableName = mDaoHelper.getTableName();
        mObserverMap = new HashMap<Integer, WeakReference<PointObserver>>();
    }

    public static PointDao getInstance() {
        return dao;
    }

    public synchronized boolean isShow(PointCode pointCode) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor cursor = db.query(mTableName, null, t_point.FIELD.CODE + "=?",
                new String[] { String.valueOf(pointCode.getValue()) }, null, null, null);
        boolean show = false;
        if (cursor.moveToFirst()) {
            show = cursor.getInt(cursor.getColumnIndex(t_point.FIELD.SHOW)) == 1;
        }
        cursor.close();
        db.close();
        return show;
    }

    private void hideDelayed(final PointCode pointCode) {
        HandlerUtils.postDelayed(new Runnable() {
            @Override
            public void run() {
                hide(pointCode);
            }
        }, 100);
    }

    public synchronized void hide(PointCode code) {
        Point p = new Point();
        p.setCode(code.getValue());
        p.setShow(false);
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        int count = db.update(mTableName, p.getContentValues(), t_point.FIELD.CODE + "=? and " + t_point.FIELD.SHOW
                + "=?", new String[] { String.valueOf(p.getCode()), String.valueOf(1) });
        db.close();
        if (count > 0) {
            notifyObserver(p);
        }
        switch (code) {
        case Code_Contacts_Fans:
            hideDelayed(PointCode.Code_Contacts);
            break;
        case Code_Task_FreightOfContacts:
        case Code_Task_InfoFee:
            hideDelayed(PointCode.Code_Task);
            break;
        case Code_Mine_Common_Setup_New_Version:
            hideDelayed(PointCode.Code_Mine_Common_Setup);
            break;
        case Code_Mine_Common_Setup:
            hideDelayed(PointCode.Code_Mine);
            break;
        default:
            break;
        }
    }

    private void showDelayed(final PointCode pointCode) {
        HandlerUtils.postDelayed(new Runnable() {
            @Override
            public void run() {
                show(pointCode);
            }
        }, 100);
    }

    public synchronized void show(PointCode pointCode) {
        Point p = new Point();
        p.setCode(pointCode.getValue());
        p.setShow(true);
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        long _id = db.replace(mTableName, null, p.getContentValues());
        db.close();
        if (_id > 0) {
            notifyObserver(p);
        }
        switch (pointCode) {
        case Code_Contacts_Fans:
            showDelayed(PointCode.Code_Contacts);
            break;
        case Code_Task_FreightOfContacts:
            if (MainActivity.sCurPagePos != MainActivity.MESSAGE_POS) {
                PointDao.getInstance().show(PointCode.Code_Message);
            }
            break;
        case Code_Task_InfoFee:
            if (MainActivity.sCurPagePos != MainActivity.TASK_POS) {
                PointDao.getInstance().show(PointCode.Code_Task);
            }
            break;
        case Code_Mine_Common_Setup_New_Version:
            showDelayed(PointCode.Code_Mine_Common_Setup);
            break;
        case Code_Mine_Common_Setup:
            if (MainActivity.sCurPagePos != 3) {
                showDelayed(PointCode.Code_Mine);
            }
            break;
        default:
            break;
        }
    }

    public synchronized Point query(PointCode code) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor cursor = db.query(mTableName, null, t_point.FIELD.CODE + "=?",
                new String[] { String.valueOf(code.getValue()) }, null, null, null);
        Point p = null;
        if (cursor.moveToFirst()) {
            p = new Point();
            p.setCode(cursor.getInt(cursor.getColumnIndex(t_point.FIELD.CODE)));
            p.setShow(cursor.getInt(cursor.getColumnIndex(t_point.FIELD.SHOW)) == 1);
            p.setNote(cursor.getString(cursor.getColumnIndex(t_point.FIELD.NOTE)));
        }
        cursor.close();
        db.close();
        return p;
    }

    private void notifyObserver(final Point p) {
        if (mObserverMap.containsKey(p.getCode())) {
            final PointObserver ob = mObserverMap.get(p.getCode()).get();
            if (ob == null) {
                mObserverMap.remove(p.getCode());
            } else {
                HandlerUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        ob.onPointChange(p);
                    }
                });

            }
        }
    }

    public void addObserver(PointCode pointCode, PointObserver ob) {
        mObserverMap.put(pointCode.getValue(), new WeakReference<PointObserver>(ob));
    }

    public void removeObserver(PointCode pointCode) {
        mObserverMap.remove(pointCode.getValue());
    }

    public interface PointObserver {
        void onPointChange(Point p);
    }
}
