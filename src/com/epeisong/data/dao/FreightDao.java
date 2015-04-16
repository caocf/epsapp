package com.epeisong.data.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.epeisong.EpsApplication;
import com.epeisong.MainActivity;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.data.dao.helper.FreightDaoHelper;
import com.epeisong.data.dao.helper.FreightDaoHelper.t_freight;
import com.epeisong.data.dao.util.CRUD;
import com.epeisong.model.Freight;
import com.epeisong.model.Point.PointCode;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.NotificationUtils;

/**
 * 车源货源（朋友发布的，朋友转发的）
 * 
 * @author poet
 * 
 */
public class FreightDao {

    private static FreightDao dao = new FreightDao();

    private FreightDaoHelper mDaoHelper;

    private String mTableName;

    private List<FreightObserver> mObservers;

    private FreightDao() {
        mDaoHelper = new FreightDaoHelper(EpsApplication.getInstance(), null, null, 0);
        mTableName = mDaoHelper.getTableName();
    }

    public static FreightDao getInstance() {
        return dao;
    }

    public synchronized void replace(Freight f) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        long _id = db.replace(mTableName, null, f.getContentValues());
        db.close();
        if (_id > 0) {
            notifyObserver(f, CRUD.REPLACE);
            if (!BaseActivity.isTop()) {
                NotificationUtils.notify(f);
            }
            if (MainActivity.sCurPagePos != MainActivity.MESSAGE_POS) {
                PointDao.getInstance().show(PointCode.Code_Message);
            }
        }
    }

    public synchronized void delete(Freight f) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        int count = db.delete(mTableName, t_freight.FIELD.ID + "=?", new String[] { f.getId() });
        db.close();
        if (count > 0) {
            notifyObserver(f, CRUD.DELETE);
        }
    }

    public synchronized void update(Freight f) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        int count = db.update(mTableName, f.getContentValues(), t_freight.FIELD.ID + "=?", new String[] { f.getId() });
        db.close();
        if (count > 0) {
            notifyObserver(f, CRUD.UPDATE);
        }
    }

    public synchronized List<Freight> queryFirst(int size) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, null, null, null, null, t_freight.FIELD.PUSH_TIME + " desc limit 0,"
                + size);
        List<Freight> result = new ArrayList<Freight>();
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        db.close();
        return result;
    }

    public synchronized List<Freight> queryNewer(long last_time, String edge_id, int size) {
        if (edge_id == null) {
            edge_id = String.valueOf(0);
        }
        List<Freight> result = new ArrayList<Freight>();
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_freight.FIELD.PUSH_TIME + ">=? and " + t_freight.FIELD.ID + ">?",
                new String[] { String.valueOf(last_time), edge_id }, null, null, t_freight.FIELD.PUSH_TIME
                        + " limit 0," + size);
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        db.close();
        return result;
    }

    public synchronized List<Freight> queryOlder(long last_time, String edge_id, int size) {
        if (edge_id == null) {
            edge_id = String.valueOf(Integer.MAX_VALUE);
        }
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_freight.FIELD.PUSH_TIME + "<=? and " + t_freight.FIELD.ID + "<?",
                new String[] { String.valueOf(last_time), edge_id }, null, null, t_freight.FIELD.PUSH_TIME
                        + " desc limit 0," + size);
        List<Freight> result = new ArrayList<Freight>();
        if (c != null) {
            while (c.moveToNext()) {
                result.add(parseCursor(c));
            }
            c.close();
        }
        db.close();
        return result;
    }

    public synchronized Freight queryById(String id) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_freight.FIELD.ID + "=?", new String[] { id }, null, null, null);
        Freight f = null;
        if (c != null) {
            if (c.moveToNext()) {
                f = parseCursor(c);
            }
            c.close();
        }
        db.close();
        return f;
    }

    public synchronized List<Freight> queryAll() {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, null, null, null, null, t_freight.FIELD.UPDATE_TIME + " desc");
        List<Freight> list = new ArrayList<Freight>();
        while (c.moveToNext()) {
            list.add(parseCursor(c));
        }
        c.close();
        db.close();
        return list;
    }

    public static Freight parseCursor(Cursor c) {
        String id = c.getString(c.getColumnIndex(t_freight.FIELD.ID));
        String sender_id = c.getString(c.getColumnIndex(t_freight.FIELD.SENDER_ID));
        String sender_name = c.getString(c.getColumnIndex(t_freight.FIELD.SENDER_NAME));
        long create_time = c.getLong(c.getColumnIndex(t_freight.FIELD.CREATE_TIME));
        long update_time = c.getLong(c.getColumnIndex(t_freight.FIELD.UPDATE_TIME));
        String start_region = c.getString(c.getColumnIndex(t_freight.FIELD.START_REGION));
        int start_region_code = c.getInt(c.getColumnIndex(t_freight.FIELD.START_REGION_CODE));
        String end_region = c.getString(c.getColumnIndex(t_freight.FIELD.END_REGION));
        int end_region_code = c.getInt(c.getColumnIndex(t_freight.FIELD.END_REGION_CODE));
        int type = c.getInt(c.getColumnIndex(t_freight.FIELD.TYPE));
        String goods_type = c.getString(c.getColumnIndex(t_freight.FIELD.GOODS_TYPE));
        float goods_ton = c.getFloat(c.getColumnIndex(t_freight.FIELD.GOODS_TON));
        int goods_square = c.getInt(c.getColumnIndex(t_freight.FIELD.GOODS_SQUARE));
        String goods_exceed = c.getString(c.getColumnIndex(t_freight.FIELD.GOODS_EXCEED));
        int truck_type_code = c.getInt(c.getColumnIndex(t_freight.FIELD.TRUCK_TYPE_CODE));
        String truck_type_name = c.getString(c.getColumnIndex(t_freight.FIELD.TRUCK_LENGTH_NAME));
        int truck_length_code = c.getInt(c.getColumnIndex(t_freight.FIELD.TRUCK_LENGTH_CODE));
        String truck_length_name = c.getString(c.getColumnIndex(t_freight.FIELD.TRUCK_LENGTH_NAME));
        int truck_spare_meter = c.getInt(c.getColumnIndex(t_freight.FIELD.TRUCK_SPARE_METER));
        int status = c.getInt(c.getColumnIndex(t_freight.FIELD.STATUS));
        long pushTime = c.getLong(c.getColumnIndex(t_freight.FIELD.PUSH_TIME));

        Freight f = new Freight();
        f.setId(id);
        f.setUser_id(sender_id);
        f.setOwner_name(sender_name);
        f.setCreate_time(create_time);
        f.setUpdate_time(update_time);
        f.setStart_region(start_region);
        f.setStart_region_code(start_region_code);
        f.setEnd_region(end_region);
        f.setEnd_region_code(end_region_code);
        f.setType(type);
        f.setGoods_type_name(goods_type);
        f.setGoods_ton(goods_ton);
        f.setGoods_square(goods_square);
        f.setGoods_exceed(goods_exceed);
        f.setTruck_length_code(truck_length_code);
        f.setTruck_length_name(truck_length_name);
        f.setTruck_type_code(truck_type_code);
        f.setTruck_type_name(truck_type_name);
        f.setTruck_spare_meter(truck_spare_meter);
        f.setStatus(status);
        f.setPushTime(pushTime);
        return f;
    }

    private void notifyObserver(final Freight f, final CRUD crud) {
        if (mObservers != null && mObservers.size() > 0) {
            HandlerUtils.post(new Runnable() {
                @Override
                public void run() {
                    for (FreightObserver ob : mObservers) {
                        ob.onFreightChange(f, crud);
                    }
                }
            });
        }
    }

    public void addObserver(FreightObserver ob) {
        if (ob == null) {
            return;
        }
        if (mObservers == null) {
            mObservers = new ArrayList<FreightDao.FreightObserver>();
        }
        mObservers.add(ob);
    }

    public void removeObserver(FreightObserver ob) {
        if (ob != null && mObservers != null) {
            mObservers.remove(ob);
        }
    }

    public interface FreightObserver {
        void onFreightChange(Freight f, CRUD crud);
    }

}
