package com.epeisong.data.dao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.epeisong.EpsApplication;
import com.epeisong.data.dao.helper.ContactsTagDaoHelper;
import com.epeisong.data.dao.helper.ContactsTagDaoHelper.t_contacts_tag;
import com.epeisong.model.ContactsTag;

/**
 * 联系人标签关系
 * @author poet
 *
 */
public class ContactsTagDao {

    private static ContactsTagDao dao = new ContactsTagDao();

    private ContactsTagDaoHelper mDaoHelper;
    private String mTableName;

    private List<WeakReference<ContactsTagObserver>> mObserverRefs;

    private ContactsTagDao() {
        mDaoHelper = new ContactsTagDaoHelper(EpsApplication.getInstance(), null, null, 0);
        mTableName = mDaoHelper.getTableName();
    }

    public static ContactsTagDao getInstance() {
        return dao;
    }

    public synchronized void insert(ContactsTag contactsTag) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        long _id = db.insert(mTableName, null, contactsTag.getContentValues());
        db.close();
        if (_id > 0) {
            notifyObserver(contactsTag.getContacts_id());
        }
    }

    public synchronized void delete(String contactsId, int tagId) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        int count = db.delete(mTableName, t_contacts_tag.FIELD.CONTACTS_ID + "=? and " + t_contacts_tag.FIELD.TAG_ID
                + "=?", new String[] { contactsId, String.valueOf(tagId) });
        if (count > 0) {
            notifyObserver(contactsId);
        }
        db.close();
    }

    public synchronized List<String> queryContactsIds(int tagId) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_contacts_tag.FIELD.TAG_ID + "=?",
                new String[] { String.valueOf(tagId) }, null, null, null);
        List<String> result = new ArrayList<String>();
        while (c.moveToNext()) {
            result.add(c.getString(c.getColumnIndex(t_contacts_tag.FIELD.CONTACTS_ID)));
        }
        c.close();
        db.close();
        return result;
    }

    public synchronized List<Integer> queryTagIds(String contactsId) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_contacts_tag.FIELD.CONTACTS_ID + "=?", new String[] { contactsId },
                null, null, null);
        List<Integer> result = new ArrayList<Integer>();
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndex(t_contacts_tag.FIELD.TAG_ID));
            result.add(id);
        }
        c.close();
        db.close();
        return result;
    }

    public synchronized ContactsTag queryContactsTag(String contactsId, int tagId) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_contacts_tag.FIELD.CONTACTS_ID + "=? and "
                + t_contacts_tag.FIELD.TAG_ID + "=?", new String[] { contactsId, String.valueOf(tagId) }, null, null,
                null);
        ContactsTag ct = null;
        if (c.moveToNext()) {
            ct = parseCursor(c);
        }
        c.close();
        db.close();
        return ct;
    }

    public synchronized List<Integer> queryTagIds() {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, null, null, t_contacts_tag.FIELD.TAG_ID, null, null);
        List<Integer> result = null;
        if (c.getCount() > 0) {
            result = new ArrayList<Integer>();
            while (c.moveToNext()) {
                ContactsTag ct = parseCursor(c);
                result.add(ct.getTag_id());
            }
        }
        c.close();
        db.close();
        return result;
    }

    private ContactsTag parseCursor(Cursor c) {
        ContactsTag contactsTag = new ContactsTag();
        contactsTag.setContacts_id(c.getString(c.getColumnIndex(t_contacts_tag.FIELD.CONTACTS_ID)));
        contactsTag.setTag_id(c.getInt(c.getColumnIndex(t_contacts_tag.FIELD.TAG_ID)));
        contactsTag.setUpdate_time(c.getLong(c.getColumnIndex(t_contacts_tag.FIELD.UPDATE_TIME)));
        return contactsTag;
    }

    private void notifyObserver(String contacts_id) {
        if (mObserverRefs != null && !mObserverRefs.isEmpty()) {
            Iterator<WeakReference<ContactsTagObserver>> it = mObserverRefs.iterator();
            while (it.hasNext()) {
                WeakReference<ContactsTagObserver> next = it.next();
                if (next.get() == null) {
                    it.remove();
                } else {
                    next.get().onContactsTagChange(contacts_id);
                }
            }
        }
    }

    public void addObserver(ContactsTagObserver ob) {
        if (mObserverRefs == null) {
            mObserverRefs = new ArrayList<WeakReference<ContactsTagObserver>>();
        }
        mObserverRefs.add(new WeakReference<ContactsTagDao.ContactsTagObserver>(ob));
    }

    public interface ContactsTagObserver {
        void onContactsTagChange(String contacts_id);
    }
}
