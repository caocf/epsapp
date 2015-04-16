package com.epeisong.data.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.epeisong.EpsApplication;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.data.dao.helper.BulletinDaoHelper;
import com.epeisong.data.dao.helper.BulletinDaoHelper.t_bulletin;
import com.epeisong.data.dao.util.CRUD;
import com.epeisong.model.Bulletin;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.NotificationUtils;

public class BulletinDao {

    private static BulletinDao dao = new BulletinDao();

    private BulletinDaoHelper mDaoHelper;

    private String mTableName;

    private List<BulletinObserver> mObservers;

    private BulletinDao() {
        mDaoHelper = new BulletinDaoHelper(EpsApplication.getInstance(), null, null, 0);
        mTableName = mDaoHelper.getTableName();
    }

    public static BulletinDao getInstance() {
        return dao;
    }

    private boolean replace(SQLiteDatabase db, Bulletin b) {
        Cursor c = db.query(mTableName, null, t_bulletin.FIELD.ID + "=?", new String[] { b.getId() }, null, null, null);
        boolean bReplace = false;
        if (c.getCount() <= 0) {
            long _id = db.insert(mTableName, null, b.getContentValues());
            if (_id > 0) {
                bReplace = true;
            }
        } else {
            int count = db.update(mTableName, b.getContentValues(), t_bulletin.FIELD.ID + "=? and "
                    + t_bulletin.FIELD.UPDATE_TIME + "<?",
                    new String[] { b.getId(), String.valueOf(b.getUpdate_time()) });
            if (count > 0) {
                bReplace = true;
            }
        }
        c.close();
        return bReplace;
    }

    public synchronized boolean replace(Bulletin b) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        boolean bNotify = replace(db, b);
        db.close();
        if (bNotify) {
            notifyObserver(b, CRUD.REPLACE);
        }
        return bNotify;
    }

    public synchronized boolean insert(Bulletin b) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        long _id = db.insert(mTableName, null, b.getContentValues());
        db.close();
        if (_id > 0) {
            notifyObserver(b, CRUD.CREATE);
            if (!BaseActivity.isTop()) {
                NotificationUtils.notify(b);
            }
            return true;
        }
        return false;
    }

    public synchronized boolean delete(Bulletin b) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        int count = db.delete(mTableName, t_bulletin.FIELD.ID + "=?", new String[] { b.getId() });
        if (count > 0) {
            notifyObserver(b, CRUD.DELETE);
            return true;
        }
        return false;
    }

    public synchronized boolean update(Bulletin b) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        int count = db.update(mTableName, b.getContentValues(), t_bulletin.FIELD.ID + "=?", new String[] { b.getId() });
        if (count > 0) {
            notifyObserver(b, CRUD.UPDATE);
            return true;
        }
        return false;
    }

    public synchronized Bulletin queryById(String id) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_bulletin.FIELD.ID + "=?", new String[] { id }, null, null, null);
        Bulletin b = null;
        if (c.moveToNext()) {
            b = parseCursor(c);
        }
        c.close();
        db.close();
        return b;
    }

    public synchronized List<Bulletin> queryFirst(int size) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, null, null, null, null, t_bulletin.FIELD.UPDATE_TIME + " desc limit 0,"
                + size);
        List<Bulletin> result = new ArrayList<Bulletin>();
        if (c != null) {
            while (c.moveToNext()) {
                result.add(parseCursor(c));
            }
            c.close();
        }
        db.close();
        return result;
    }

    public synchronized List<Bulletin> queryNewer(long last_time, int size) {

        List<Bulletin> result = new ArrayList<Bulletin>();

        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_bulletin.FIELD.UPDATE_TIME + ">?",
                new String[] { String.valueOf(last_time) }, null, null, t_bulletin.FIELD.UPDATE_TIME + " limit 0,"
                        + size);
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        db.close();
        return result;
    }

    public synchronized List<Bulletin> queryOlder(long last_time, int size) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_bulletin.FIELD.UPDATE_TIME + "<?",
                new String[] { String.valueOf(last_time) }, null, null, t_bulletin.FIELD.UPDATE_TIME + " desc limit 0,"
                        + size);
        List<Bulletin> result = new ArrayList<Bulletin>();
        if (c != null) {
            while (c.moveToNext()) {
                result.add(parseCursor(c));
            }
            c.close();
        }
        db.close();
        return result;
    }

    private Bulletin parseCursor(Cursor c) {
        Bulletin b = new Bulletin();
        String id = c.getString(c.getColumnIndex(t_bulletin.FIELD.ID));
        b.setId(id);
        String sender_id = c.getString(c.getColumnIndex(t_bulletin.FIELD.SENDER_ID));
        b.setSender_id(sender_id);
        String sender_name = c.getString(c.getColumnIndex(t_bulletin.FIELD.SENDER_NAME));
        b.setSender_name(sender_name);
        long create_time = c.getLong(c.getColumnIndex(t_bulletin.FIELD.CREATE_TIME));
        b.setCreate_time(create_time);
        long update_time = c.getLong(c.getColumnIndex(t_bulletin.FIELD.UPDATE_TIME));
        b.setUpdate_time(update_time);
        String content = c.getString(c.getColumnIndex(t_bulletin.FIELD.CONTENT));
        b.setContent(content);
        int content_type = c.getInt(c.getColumnIndex(t_bulletin.FIELD.CONTENT_TYPE));
        b.setContent_type(content_type);
        int status = c.getInt(c.getColumnIndex(t_bulletin.FIELD.STATUS));
        b.setStatus(status);
        return b;
    }

    private void notifyObserver(final Bulletin b, final CRUD crud) {
        if (mObservers != null && mObservers.size() > 0) {
            HandlerUtils.post(new Runnable() {
                @Override
                public void run() {
                    for (BulletinObserver ob : mObservers) {
                        ob.onBulletinChange(b, crud);
                    }
                }
            });

        }
    }

    public void addObserver(BulletinObserver ob) {
        if (ob == null) {
            return;
        }
        if (mObservers == null) {
            mObservers = new ArrayList<BulletinObserver>();
        }
        mObservers.add(ob);
    }

    public void removeObserver(BulletinObserver ob) {
        if (mObservers != null && ob != null) {
            mObservers.remove(ob);
        }
    }

    public interface BulletinObserver {
        void onBulletinChange(Bulletin b, CRUD crud);
    }
}
