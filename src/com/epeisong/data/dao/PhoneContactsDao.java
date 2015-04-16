package com.epeisong.data.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.epeisong.EpsApplication;
import com.epeisong.data.dao.helper.PhoneContactsDaoHelper;
import com.epeisong.data.dao.helper.PhoneContactsDaoHelper.t_phonecontacts;
import com.epeisong.data.dao.util.CRUD;
import com.epeisong.model.PhoneContacts;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.LogUtils;

public class PhoneContactsDao {

    private static PhoneContactsDao dao = new PhoneContactsDao();

    private PhoneContactsDaoHelper mDaoHelper;

    private String mTableName;

    private List<PhoneContactsObserver> mObservers;

    private PhoneContactsDao() {
        mDaoHelper = new PhoneContactsDaoHelper(EpsApplication.getInstance(), null, null, 0);
        mTableName = mDaoHelper.getTableName();
    }

    public static PhoneContactsDao getInstance() {
        return dao;
    }

    public synchronized void insert(PhoneContacts pc) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        long _id = db.insert(mTableName, null, pc.getContentValues());
        if (_id > 0) {
            notifyObserver(CRUD.CREATE);
        }
    }

    public synchronized boolean insertAll(List<PhoneContacts> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        db.beginTransaction();
        for (PhoneContacts pc : list) {
            long _id = 0;
            try {
                _id = db.insert(mTableName, null, pc.getContentValues());
            } catch (SQLException e) {
                LogUtils.e("", e);
            }
            if (_id <= 0) {
                db.endTransaction();
                db.close();
                return false;
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        notifyObserver(CRUD.CREATE);
        return true;
    }

    public synchronized void update(PhoneContacts pc) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        int count = db.update(mTableName, pc.getContentValues(), t_phonecontacts.FIELD.ID + "=?",
                new String[] { pc.getId() });
        if (count > 0) {
            notifyObserver(CRUD.UPDATE);
        }
    }

    public synchronized void updateAdded(String phone) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(t_phonecontacts.FIELD.STATUS, PhoneContacts.STATUS_ADDED);
        int count = db.update(mTableName, values, t_phonecontacts.FIELD.PHONE_NUM + "=?", new String[] { phone });
        if (count > 0) {
            notifyObserver(CRUD.UPDATE);
        }
    }

    public synchronized void updateUnAdd(String phone) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(t_phonecontacts.FIELD.STATUS, PhoneContacts.STATUS_UN_ADD);
        int count = db.update(mTableName, values, t_phonecontacts.FIELD.PHONE_NUM + "=?", new String[] { phone });
        if (count > 0) {
            notifyObserver(CRUD.UPDATE);
        }
    }

    public synchronized boolean updateAll(List<PhoneContacts> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        db.beginTransaction();
        for (PhoneContacts pc : list) {
            int count = db.update(mTableName, pc.getContentValues(), t_phonecontacts.FIELD.ID + "=?",
                    new String[] { pc.getId() });
            if (count <= 0) {
                db.endTransaction();
                db.close();
                return false;
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        notifyObserver(CRUD.UPDATE);
        return true;
    }

    public synchronized List<PhoneContacts> queryAll() {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, null, null, null, null, t_phonecontacts.FIELD.SORT_KEY);
        List<PhoneContacts> result = new ArrayList<PhoneContacts>();
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        return result;
    }

    public synchronized List<PhoneContacts> queryUnCheck(int size) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_phonecontacts.FIELD.STATUS + "=?",
                new String[] { String.valueOf(PhoneContacts.STATUS_UN_SPECIFIED) }, null, null,
                t_phonecontacts.FIELD.ID + " limit 0," + size);
        List<PhoneContacts> result = new ArrayList<PhoneContacts>();
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        return result;
    }

    private PhoneContacts parseCursor(Cursor c) {
        PhoneContacts contacts = new PhoneContacts();
        contacts.setId(c.getString(c.getColumnIndex(t_phonecontacts.FIELD.ID)));
        contacts.setSort_key(c.getString(c.getColumnIndex(t_phonecontacts.FIELD.SORT_KEY)));
        contacts.setName(c.getString(c.getColumnIndex(t_phonecontacts.FIELD.NAME)));
        contacts.setPinyin(c.getString(c.getColumnIndex(t_phonecontacts.FIELD.PINYIN)));
        contacts.setPhone_num(c.getString(c.getColumnIndex(t_phonecontacts.FIELD.PHONE_NUM)));
        contacts.setStatus(c.getInt(c.getColumnIndex(t_phonecontacts.FIELD.STATUS)));
        return contacts;
    }

    private void notifyObserver(final CRUD crud) {
        if (mObservers != null && mObservers.size() > 0) {
            HandlerUtils.post(new Runnable() {

                @Override
                public void run() {
                    for (PhoneContactsObserver ob : mObservers) {
                        ob.onPhoneContactsChange(crud);
                    }
                }
            });

        }
    }

    public void addObserver(PhoneContactsObserver ob) {
        if (ob == null) {
            return;
        }
        if (mObservers == null) {
            mObservers = new ArrayList<PhoneContactsObserver>();
        }
        mObservers.add(ob);
    }

    public void removeObserver(PhoneContactsObserver ob) {
        if (mObservers != null && ob != null) {
            mObservers.remove(ob);
        }
    }

    public interface PhoneContactsObserver {
        void onPhoneContactsChange(CRUD crud);
    }
}
