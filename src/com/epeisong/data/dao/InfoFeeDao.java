/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.data.dao.InfoFeeDao.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2014年10月22日下午4:19:53
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.data.dao;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.epeisong.EpsApplication;
import com.epeisong.data.dao.helper.DaoHelper;
import com.epeisong.data.dao.util.CRUD;
import com.epeisong.data.dao.util.DaoHelperUtils;
import com.epeisong.model.InfoFee;
import com.epeisong.model.Point.PointCode;
import com.epeisong.ui.activity.InfoFeeListActivity;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.LogUtils;

@Deprecated
public class InfoFeeDao {
    private static InfoFeeDao dao = null;;

    private DaoHelper helper = null;
    private static String TABLE_NAME = "info_fee";

    private List<WeakReference<InfoFeeObserver>> mObserverRefs;

    public InfoFeeDao() {
        int version = 1;
        version = 2; // 添加字段 payerGuaranteeProductOwnerLogo
        helper = new DaoHelper(EpsApplication.getInstance(), TABLE_NAME, null, version) {
            @Override
            public Class<?> setObject() {
                return InfoFee.class;
            }
        };
    }

    public static InfoFeeDao getInstance() {
        if (null == dao) {
            dao = new InfoFeeDao();
        }

        return dao;
    }

    private boolean replace(SQLiteDatabase db, InfoFee infoFee) {
        if (TextUtils.isEmpty(infoFee.getId())) {
            return false;
        }
        Cursor cursorAll = db.query(TABLE_NAME, new String[] { "syncIndex" }, null, null, null, null, null);
        if (cursorAll.getCount() > 1000 && infoFee.getSyncIndex() > 1000) {
            db.delete(TABLE_NAME, "syncIndex<" + (infoFee.getSyncIndex() - 500), null);
        }
        cursorAll.close();

        boolean success = false;
        Cursor c = db.query(TABLE_NAME, null, "id=" + infoFee.getId(), null, null, null, null);
        Field[] fields = InfoFee.class.getDeclaredFields();
        ContentValues values = DaoHelperUtils.getContentValues(fields, infoFee);
        if (c.getCount() > 0) {
            c.moveToFirst();
            int count = db.update(TABLE_NAME, values,
                    "id=" + infoFee.getId() + " and updateDate<=" + infoFee.getUpdateDate(), null);
            success = count > 0;
        } else {
            long _id = db.insert(TABLE_NAME, null, values);
            success = _id > 0;
        }
        c.close();
        return success;
    }

    public synchronized boolean replace(InfoFee infoFee) {

        SQLiteDatabase db = helper.getWritableDatabase();
        boolean success = replace(db, infoFee);
        db.close();
        if (success) {
            notifyObserver(infoFee, CRUD.REPLACE);
//            if (!InfoFeeListActivity.SHOWING) {
//                PointDao.getInstance().show(PointCode.Code_Task_InfoFee);
//            }
        }
        return success;
    }

    public synchronized void read(InfoFee infoFee) {
        if (infoFee != null) {
            infoFee.setLocalStatus(1);
            SQLiteDatabase db = helper.getWritableDatabase();
            Field[] fields = InfoFee.class.getDeclaredFields();
            ContentValues values = DaoHelperUtils.getContentValues(fields, infoFee);
            int count = db.update(TABLE_NAME, values, "id=" + infoFee.getId(), null);
            db.close();
            if (count > 0) {
                notifyObserver(infoFee, CRUD.READ);
                PointDao.getInstance().hide(PointCode.Code_Task_InfoFee);
            }
        }
    }

    public synchronized boolean replace(List<InfoFee> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        for (InfoFee item : list) {
            replace(db, item);
        }
        db.close();
        return true;
    }

    public synchronized InfoFee queryNewestByUpdateTime() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, "updateDate desc limit 0,1");
        InfoFee result = null;
        if (c.moveToNext()) {
            result = parseCursor(c);
        }
        c.close();
        db.close();
        return result;
    }

    public synchronized InfoFee queryNewestBySyncIndex(int status) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, null, "status = ?", new String[] { String.valueOf(status) }, null, null,
                "syncIndex desc limit 0,1");
        InfoFee result = null;
        if (c.moveToNext()) {
            result = parseCursor(c);
        }
        c.close();
        db.close();
        return result;
    }

    @Deprecated
    public synchronized long insert(InfoFee infoFee) {
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "syncIndex asc");
        if (null != cursor && cursor.getCount() >= 1000) {
            Cursor cursor2 = db.query(TABLE_NAME, null, null, null, null, null, "syncIndex desc limit 0,1");
            if (cursor2.moveToNext()) {
                if (infoFee.getSyncIndex() < cursor2.getInt(cursor.getColumnIndex("syncIndex"))) {
                    cursor2.close();
                    return 0;
                }
                cursor2.close();
            }

            if (cursor.moveToNext()) {
                db.delete(TABLE_NAME, "id=?",
                        new String[] { String.valueOf(cursor.getInt(cursor.getColumnIndex("id"))) });
            }
            cursor.close();
        }

        Field[] fields = InfoFee.class.getDeclaredFields();

        ContentValues values = DaoHelperUtils.getContentValues(fields, infoFee);

        long count = db.insert(TABLE_NAME, null, values);
        db.close();
        if (count > 0) {
            notifyObserver(infoFee, CRUD.CREATE);
            PointDao.getInstance().show(PointCode.Code_Task_InfoFee);
        }

        return count;
    }

    public synchronized int deleteById(String infoFeeId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete(TABLE_NAME, "id=?", new String[] { String.valueOf(infoFeeId) });

        InfoFee infoFee = queryById(infoFeeId);
        db.close();
        if (count > 0) {
            notifyObserver(infoFee, CRUD.DELETE);
        }

        return count;
    }

    public synchronized int update(InfoFee infoFee) {
        InfoFee localInfoFee = queryById(infoFee.getId());
        if (infoFee.getUpdateDate() <= localInfoFee.getUpdateDate()) {
            return 0;
        }

        SQLiteDatabase db = helper.getWritableDatabase();

        Field[] fields = InfoFee.class.getDeclaredFields();

        ContentValues values = DaoHelperUtils.getContentValues(fields, infoFee);

        values.remove("syncIndex");

        int count = db.update(TABLE_NAME, values, "id=?", new String[] { String.valueOf(infoFee.getId()) });
        db.close();
        if (count > 0) {
            notifyObserver(infoFee, CRUD.UPDATE);
        }

        return count;
    }

    public synchronized InfoFee queryById(String infoFeeId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "id=?", new String[] { infoFeeId }, null, null, null);

        Field[] fields = InfoFee.class.getDeclaredFields();
        InfoFee infoFee = null;

        if (cursor != null) {
            if (cursor.moveToNext()) {
                infoFee = new InfoFee();
                DaoHelperUtils.cursorParse(cursor, fields, infoFee);
            }
            cursor.close();
        }
        db.close();
        return infoFee;
    }

    public synchronized List<InfoFee> queryList(Integer lessSyncIndex, Integer thanSyncIndex, int count) {

        List<InfoFee> result = new ArrayList<InfoFee>();

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = null;
        if (0 == lessSyncIndex && 0 == lessSyncIndex) {
            cursor = db.query(TABLE_NAME, null, null, null, null, null, "syncIndex desc limit 0," + count);
        } else if (0 != lessSyncIndex) {
            cursor = db.query(TABLE_NAME, null, "syncIndex <? ", new String[] { String.valueOf(lessSyncIndex) }, null,
                    null, "syncIndex desc limit 0," + count);
        } else if (0 != thanSyncIndex) {
            cursor = db.query(TABLE_NAME, null, "syncIndex >? ", new String[] { String.valueOf(thanSyncIndex) }, null,
                    null, "syncIndex desc limit 0," + count);
        }

        Field[] fields = InfoFee.class.getDeclaredFields();
        InfoFee infoFee = null;

        if (null != cursor) {
            while (cursor.moveToNext()) {
                infoFee = new InfoFee();
                DaoHelperUtils.cursorParse(cursor, fields, infoFee);
                result.add(infoFee);
            }
            cursor.close();
        }
        db.close();
        // TODO 未处理：标记为不显示（删除）的数据，要被过滤掉，同时需要进一步查询数据库，以得到预期的个数。
        return result;
    }

    private InfoFee parseCursor(Cursor c) {
        InfoFee infoFee = new InfoFee();
        Field[] fields = InfoFee.class.getDeclaredFields();
        DaoHelperUtils.cursorParse(c, fields, infoFee);
        return infoFee;
    }

    private void notifyObserver(final InfoFee infoFee, final CRUD crud) {
        if (mObserverRefs != null && mObserverRefs.size() > 0) {
            HandlerUtils.post(new Runnable() {
                @Override
                public void run() {
                    LogUtils.e("notifyObserver", crud.stringValue() + " id = " + infoFee.getId());
                    Iterator<WeakReference<InfoFeeObserver>> it = mObserverRefs.iterator();
                    while (it.hasNext()) {
                        WeakReference<InfoFeeObserver> next = it.next();
                        if (next.get() == null) {
                            it.remove();
                        } else {
                            next.get().onInfoFeeChange(infoFee, crud);
                        }
                    }
                }
            });

        }
    }

    public void addObserver(InfoFeeObserver observer) {
        if (observer == null) {
            return;
        }
        if (mObserverRefs == null) {
            mObserverRefs = new ArrayList<WeakReference<InfoFeeObserver>>();
        }
        mObserverRefs.add(new WeakReference<InfoFeeDao.InfoFeeObserver>(observer));
    }

    public void removeObserver(InfoFeeObserver observer) {
        if (mObserverRefs != null) {
            Iterator<WeakReference<InfoFeeObserver>> it = mObserverRefs.iterator();
            while (it.hasNext()) {
                WeakReference<InfoFeeObserver> next = it.next();
                if (next.get() == observer) {
                    it.remove();
                    break;
                }
            }
        }
    }

    public interface InfoFeeObserver {
        void onInfoFeeChange(InfoFee infoFee, CRUD crud);
    }
}
