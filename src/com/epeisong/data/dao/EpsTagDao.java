package com.epeisong.data.dao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.epeisong.EpsApplication;
import com.epeisong.data.dao.helper.EpsTagDaoHelper;
import com.epeisong.data.dao.helper.EpsTagDaoHelper.t_eps_tag;
import com.epeisong.model.EpsTag;

/**
 * 易配送标签
 * @author poet
 *
 */
public class EpsTagDao {

    private static EpsTagDao dao = new EpsTagDao();

    private EpsTagDaoHelper mDaoHelper;
    private String mTableName;

    private List<WeakReference<EpsTagObserver>> mObserverRefs;

    private EpsTagDao() {
        mDaoHelper = new EpsTagDaoHelper(EpsApplication.getInstance(), null, null, 0);
        mTableName = mDaoHelper.getTableName();
    }

    public static EpsTagDao getInstance() {
        return dao;
    }

    public synchronized void insert(EpsTag tag) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        long _id = db.insert(mTableName, null, tag.getContentValues());
        db.close();
        if (_id > 0) {
            notifyObserver();
        }
    }

    public synchronized void replaceAll(List<EpsTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return;
        }
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        for (EpsTag tag : tags) {
            db.replace(mTableName, null, tag.getContentValues());
        }
        db.close();
        notifyObserver();
    }

    public synchronized List<EpsTag> query(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder("(");
        for (int i : ids) {
            sb.append("'" + i + "',");
        }
        sb.replace(sb.length() - 1, sb.length(), ")");
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor cursor = db.query(mTableName, null, t_eps_tag.FIELD.ID + " in " + sb.toString(), null, null, null, null);
        List<EpsTag> result = new ArrayList<EpsTag>();
        while (cursor.moveToNext()) {
            result.add(parseCursor(cursor));
        }
        cursor.close();
        db.close();
        return result;
    }

    public synchronized List<EpsTag> queryAll() {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, null, null, null, null, null);
        List<EpsTag> result = new ArrayList<EpsTag>();
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        db.close();
        return result;
    }

    private EpsTag parseCursor(Cursor c) {
        int id = c.getInt(c.getColumnIndex(t_eps_tag.FIELD.ID));
        String name = c.getString(c.getColumnIndex(t_eps_tag.FIELD.NAME));
        EpsTag tag = new EpsTag(id, name);
        return tag;
    }

    private void notifyObserver() {
        if (mObserverRefs != null && !mObserverRefs.isEmpty()) {
            Iterator<WeakReference<EpsTagObserver>> iterator = mObserverRefs.iterator();
            while (iterator.hasNext()) {
                WeakReference<EpsTagObserver> next = iterator.next();
                if (next.get() == null) {
                    iterator.remove();
                } else {
                    next.get().onEpsTagChange();
                }
            }
        }
    }

    public void addObserver(EpsTagObserver ob) {
        if (mObserverRefs == null) {
            mObserverRefs = new ArrayList<WeakReference<EpsTagObserver>>();
        }
        mObserverRefs.add(new WeakReference<EpsTagObserver>(ob));
    }

    public interface EpsTagObserver {
        void onEpsTagChange();
    }
}
