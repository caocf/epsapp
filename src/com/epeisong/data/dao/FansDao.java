package com.epeisong.data.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.epeisong.EpsApplication;
import com.epeisong.data.dao.helper.FansDaoHelper;
import com.epeisong.data.dao.helper.FansDaoHelper.t_fans;
import com.epeisong.data.dao.util.CRUD;
import com.epeisong.model.Fans;

public class FansDao {

    private static FansDao dao = new FansDao();

    private FansDaoHelper mDaoHelper;

    private String mTableName;

    private List<FansObserver> mObservers;

    private FansDao() {
        mDaoHelper = new FansDaoHelper(EpsApplication.getInstance(), null, null, 0);
        mTableName = mDaoHelper.getTableName();
    }

    public static FansDao getInstance() {
        return dao;
    }

    public void addObserver(FansObserver ob) {
        if (ob == null) {
            return;
        }
        if (mObservers == null) {
            mObservers = new ArrayList<FansDao.FansObserver>();
        }
        mObservers.add(ob);
    }

    private boolean replace(SQLiteDatabase db, Fans fans) {
        Cursor c = db.query(mTableName, null, t_fans.FIELD.ID + "=?", new String[] { fans.getId() }, null, null, null);
        boolean bReplace = false;
        if (c.getCount() <= 0) {
            long _id = db.insert(mTableName, null, fans.getContentValues());
            if (_id > 0) {
                bReplace = true;
            }
        } else {
            int count = db.update(mTableName, fans.getContentValues(), t_fans.FIELD.ID + "=? and " + t_fans.FIELD.TIME
                    + "<?", new String[] { fans.getId(), String.valueOf(fans.getTime()) });
            if (count > 0) {
                bReplace = true;
            }
        }
        c.close();
        return bReplace;
    }

    public synchronized boolean replace(Fans fans) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        boolean bNotify = replace(db, fans);
        if (bNotify) {
            notifyObserver(fans, CRUD.REPLACE);
        }
        return bNotify;
    }

    public synchronized void insert(Fans fans) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        long _id = db.insert(mTableName, null, fans.getContentValues());
        db.close();
        if (_id > 0) {
            notifyObserver(fans, CRUD.CREATE);
        }
    }

    public synchronized void delete(Fans fans) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        int count = db.delete(mTableName, t_fans.FIELD.ID + "=?", new String[] { fans.getId() });
        db.close();
        if (count > 0) {
            notifyObserver(fans, CRUD.DELETE);
        }
    }

    public synchronized void update(Fans fans) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        int count = db.update(mTableName, fans.getContentValues(), t_fans.FIELD.ID + "=?",
                new String[] { fans.getId() });
        if (count > 0) {
            notifyObserver(fans, CRUD.UPDATE);
        }
        db.close();
    }

    public synchronized void changeStatus(String id, int status) {
        ContentValues values = new ContentValues();
        values.put(t_fans.FIELD.STATUS, status);
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        int count = db.update(mTableName, values, t_fans.FIELD.ID + "=?", new String[] { id });
        if (count > 0) {
            notifyObserver(null, CRUD.UPDATE);
        }
        db.close();
    }

    public synchronized List<Fans> queryAll() {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        Cursor c = db.query(mTableName, null, null, null, null, null, null);
        List<Fans> result = new ArrayList<Fans>();
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                result.add(parseCursor(c));
            }
        }
        c.close();
        db.close();
        return result;
    }

    private void notifyObserver(Fans fans, CRUD crud) {
        if (mObservers != null && mObservers.size() > 0) {
            for (FansObserver ob : mObservers) {
                ob.onFansChange(fans, crud);
            }
        }
    }

    private Fans parseCursor(Cursor c) {
        Fans fans = new Fans();
        String id = c.getString(c.getColumnIndex(t_fans.FIELD.ID));
        fans.setId(id);
        String name = c.getString(c.getColumnIndex(t_fans.FIELD.NAME));
        fans.setName(name);
        int status = c.getInt(c.getColumnIndex(t_fans.FIELD.STATUS));
        fans.setStatus(status);
        long time = c.getLong(c.getColumnIndex(t_fans.FIELD.TIME));
        fans.setTime(time);
        return fans;
    }

    public void removeObserver(FansObserver ob) {
        if (ob != null && mObservers != null) {
            mObservers.remove(ob);
        }
    }

    public interface FansObserver {
        void onFansChange(Fans fans, CRUD crud);
    }
}
