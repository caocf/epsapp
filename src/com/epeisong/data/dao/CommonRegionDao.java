package com.epeisong.data.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.epeisong.EpsApplication;
import com.epeisong.data.dao.RegionDao.Field;
import com.epeisong.data.dao.helper.CommonDataDaoHelper;
import com.epeisong.data.dao.helper.CommonDataDaoHelper.t_common_region;
import com.epeisong.model.CommonRegion;
import com.epeisong.model.Region;
import com.epeisong.ui.activity.ChooseRegionActivity;

/**
 * 常用地址
 * @author poet
 *
 */
public class CommonRegionDao {
    private static CommonRegionDao dao = new CommonRegionDao();

    private CommonDataDaoHelper mDaoHelper;
    private String mTableName;

    private CommonRegionDao() {
        mDaoHelper = new CommonDataDaoHelper(EpsApplication.getInstance(), null, null, 0);
        mTableName = mDaoHelper.getCommonRegionTableName();
    }

    public static CommonRegionDao getInstance() {
        return dao;
    }

    public synchronized void insert(Region region) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        Cursor c = db.query(mTableName, null, t_common_region.FIELD.CODE + "=?",
                new String[] { String.valueOf(region.getCode()) }, null, null, null);
        CommonRegion cr = null;
        if (c.moveToFirst()) {
            cr = parseCursor(c);
            cr.setCount(cr.getCount() + 1);
            cr.setUpdate_time(System.currentTimeMillis());
        }
        c.close();
        if (cr == null) {
            cr = new CommonRegion();
            cr.setCode(region.getCode());
            cr.setType(region.getType());
            cr.setCount(1);
            cr.setUpdate_time(System.currentTimeMillis());
        }
        db.replace(mTableName, null, cr.getContentValues());
        db.close();
    }

    public synchronized List<CommonRegion> query(int filter, int size) {
        String selection = "(" + Field.TYPE;
        List<String> args = new ArrayList<String>();
        if (filter == ChooseRegionActivity.FILTER_0_3) {
            selection += "<=? or " + Field.TYPE + "=?";
            args.add("3");
            args.add("11");
        } else if (filter == ChooseRegionActivity.FILTER_0_2) {
            selection += "<=? or " + Field.TYPE + "=?";
            args.add("2");
            args.add("11");
        } else if (filter == ChooseRegionActivity.FILTER_2) {
            selection += "=? or " + Field.TYPE + "=?";
            args.add("2");
            args.add("11");
        } else if (filter == ChooseRegionActivity.FILTER_1_3) {
            selection += ">=? or " + Field.TYPE + "=?";
            args.add("1");
            args.add("11");
        } else {
            return null;
        }
        selection += ")";
        String[] selectionArgs = new String[args.size()];
        args.toArray(selectionArgs);
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, selection, selectionArgs, null, null, t_common_region.FIELD.COUNT
                + " desc," + t_common_region.FIELD.UPDATE_TIME + " desc limit 0," + size);
        List<CommonRegion> result = new ArrayList<CommonRegion>();
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        db.close();
        return result;
    }

    private CommonRegion parseCursor(Cursor c) {
        CommonRegion r = new CommonRegion();
        r.setCode(c.getInt(c.getColumnIndex(t_common_region.FIELD.CODE)));
        r.setType(c.getInt(c.getColumnIndex(t_common_region.FIELD.TYPE)));
        r.setCount(c.getInt(c.getColumnIndex(t_common_region.FIELD.COUNT)));
        r.setUpdate_time(c.getLong(c.getColumnIndex(t_common_region.FIELD.UPDATE_TIME)));
        return r;
    }
}
