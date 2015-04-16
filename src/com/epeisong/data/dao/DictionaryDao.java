package com.epeisong.data.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.epeisong.EpsApplication;
import com.epeisong.data.dao.helper.DictionaryDaoHelper;
import com.epeisong.data.dao.helper.DictionaryDaoHelper.t_sys_dictionary;
import com.epeisong.model.Dictionary;

/**
 * 系统字典
 * 
 * @author poet
 * 
 */
public class DictionaryDao {

    private static DictionaryDao dao = new DictionaryDao();

    private DictionaryDaoHelper mDaoHelper;
    private String mTableName;

    private DictionaryDao() {
        mDaoHelper = new DictionaryDaoHelper(EpsApplication.getInstance(), null, null, 0);
        mTableName = mDaoHelper.getTableName();
    }

    public static DictionaryDao getInstance() {
        return dao;
    }

    public synchronized boolean insertAll(List<Dictionary> dicts) {
        if (dicts == null || dicts.isEmpty()) {
            return false;
        }
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        db.beginTransaction();
        db.delete(mTableName, null, null);
        for (Dictionary dict : dicts) {
            db.insert(mTableName, null, dict.getContentValues());
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return true;
    }

    public synchronized List<Dictionary> queryByType(int type) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_sys_dictionary.FIELD.TYPE + "=?",
                new String[] { String.valueOf(type) }, null, null, t_sys_dictionary.FIELD.SORT_ORDER);
        List<Dictionary> result = new ArrayList<Dictionary>();
        while (c.moveToNext()) {
            result.add(parse(c));
        }
        c.close();
        db.close();
        return result;
    }
    
    public synchronized void clear() {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        db.delete(mTableName, null, null);
        db.close();
    }

    private Dictionary parse(Cursor c) {
        Dictionary dict = new Dictionary();
        int id = c.getInt(c.getColumnIndex(t_sys_dictionary.FIELD.ID));
        dict.setId(id);
        String name = c.getString(c.getColumnIndex(t_sys_dictionary.FIELD.NAME));
        dict.setName(name);
        int sort_order = c.getInt(c.getColumnIndex(t_sys_dictionary.FIELD.SORT_ORDER));
        dict.setSort_order(sort_order);
        int type = c.getInt(c.getColumnIndex(t_sys_dictionary.FIELD.TYPE));
        dict.setType(type);
        return dict;
    }
}
