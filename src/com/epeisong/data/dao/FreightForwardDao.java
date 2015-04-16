package com.epeisong.data.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.epeisong.EpsApplication;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.data.dao.helper.FreightForwardDaoHelper;
import com.epeisong.data.dao.helper.FreightForwardDaoHelper.t_freight_forward;
import com.epeisong.data.dao.util.CRUD;
import com.epeisong.model.Freight;
import com.epeisong.model.FreightForward;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.NotificationUtils;

/**
 * 别人转发给我的车源货源Dao
 * 
 * @author poet
 * 
 */
@Deprecated
public class FreightForwardDao {

    private static FreightForwardDao dao = new FreightForwardDao();

    private FreightForwardDaoHelper mDaoHelper;
    private String mTableName;

    private List<FreightForwardObserver> mObservers;

    private FreightForwardDao() {
        mDaoHelper = new FreightForwardDaoHelper(EpsApplication.getInstance(), null, null, 0);
        mTableName = mDaoHelper.getTableName_FreightForward();
    }

    public static FreightForwardDao getInstance() {
        return dao;
    }

    private boolean replace(SQLiteDatabase db, FreightForward ff) {
        Cursor c = db.query(mTableName, null, t_freight_forward.FIELD.ID + "=?", new String[] { ff.getId() }, null,
                null, null);
        boolean bReplace = false;
        if (c.getCount() <= 0) {
            long _id = db.insert(mTableName, null, ff.getContentValues());
            if (_id > 0) {
                bReplace = true;
            }
        } else {
            int count = db.update(mTableName, ff.getContentValues(), t_freight_forward.FIELD.ID + "=? and "
                    + t_freight_forward.FIELD.UPDATE_TIME + "<?",
                    new String[] { ff.getId(), String.valueOf(ff.getForward_update_time()) });
            if (count > 0) {
                bReplace = true;
            }
        }
        c.close();
        return bReplace;
    }

    public synchronized boolean replace(FreightForward ff) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        boolean bNotify = replace(db, ff);
        db.close();
        if (bNotify) {
            notifyObserver(ff, CRUD.REPLACE);
            if (!BaseActivity.isTop()) {
                NotificationUtils.notify(ff.getFreight());
            }
        }
        return bNotify;
    }

    public synchronized boolean replaceAll(List<FreightForward> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        db.beginTransaction();
        for (FreightForward ff : list) {
            replace(db, ff);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return true;
    }

    public synchronized boolean insertAll(List<FreightForward> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        db.beginTransaction();
        for (FreightForward ff : list) {
            long _id = db.insert(mTableName, null, ff.getContentValues());
            if (_id == -1) {
                db.endTransaction();
                db.close();
                return false;
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return true;
    }

    public synchronized void delete(String freightId) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        FreightForward ff = null;
        Cursor c = db.query(mTableName, null, t_freight_forward.FIELD.FREIGHT_ID + "=?", new String[] { freightId },
                null, null, null);
        if (c.moveToFirst()) {
            ff = parseCursor(c);
        }
        c.close();
        if (ff != null) {
            db.delete(mTableName, t_freight_forward.FIELD.FREIGHT_ID + "=?", new String[] { freightId });
            notifyObserver(ff, CRUD.DELETE);
        }
        db.close();
    }

    public synchronized void delete(FreightForward ff) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        int count = db.delete(mTableName, t_freight_forward.FIELD.ID + "=?", new String[] { ff.getId() });
        db.close();
        if (count > 0) {
            notifyObserver(ff, CRUD.DELETE);
        }
    }

    public synchronized void update(FreightForward ff) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        int count = db.update(mTableName, ff.getContentValues(), t_freight_forward.FIELD.ID + "=?",
                new String[] { ff.getId() });
        db.close();
        if (count > 0) {
            notifyObserver(ff, CRUD.UPDATE);
        }
    }

    public synchronized FreightForward queryById(String id) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, "id=?", new String[] { id }, null, null, null);
        FreightForward ff = null;
        if (c != null) {
            if (c.moveToNext()) {
                ff = parseCursor(c);
            }
            c.close();
        }
        db.close();
        return ff;
    }

    public synchronized List<FreightForward> queryFirst(int size) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, null, null, null, null, t_freight_forward.FIELD.SERIAL + " desc limit 0,"
                + size);
        List<FreightForward> result = new ArrayList<FreightForward>();
        if (c != null) {
            while (c.moveToNext()) {
                result.add(parseCursor(c));
            }
            c.close();
        }
        db.close();
        // TODO 未处理：标记为不显示（删除）的数据，要被过滤掉，同时需要进一步查询数据库，以得到预期的个数。
        return result;
    }

    public synchronized List<FreightForward> queryNewer(long last_time, long last_serial, int size) {

        List<FreightForward> result = new ArrayList<FreightForward>();

        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_freight_forward.FIELD.FORWARD_CREATE_TIME + ">=? and "
                + t_freight_forward.FIELD.SERIAL + ">?",
                new String[] { String.valueOf(last_time), String.valueOf(last_serial) }, null, null,
                t_freight_forward.FIELD.SERIAL + " limit 0," + size);
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        db.close();
        // TODO 未处理：标记为不显示（删除）的数据，要被过滤掉，同时需要进一步查询数据库，以得到预期的个数。
        return result;
    }

    public synchronized List<FreightForward> queryOlder(long last_time, long last_serial, int size) {
        if (last_serial == 0) {
            last_serial = Long.MAX_VALUE;
        }
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_freight_forward.FIELD.FORWARD_CREATE_TIME + "<=? and "
                + t_freight_forward.FIELD.SERIAL + "<?",
                new String[] { String.valueOf(last_time), String.valueOf(last_serial) }, null, null,
                t_freight_forward.FIELD.SERIAL + " desc limit 0," + size);
        List<FreightForward> result = new ArrayList<FreightForward>();
        if (c != null) {
            while (c.moveToNext()) {
                result.add(parseCursor(c));
            }
            c.close();
        }
        db.close();
        // TODO 未处理：标记为不显示（删除）的数据，要被过滤掉，同时需要进一步查询数据库，以得到预期的个数。
        return result;
    }

    private FreightForward parseCursor(Cursor c) {
        String id = c.getString(c.getColumnIndex(t_freight_forward.FIELD.ID));
        long serial = c.getLong(c.getColumnIndex(t_freight_forward.FIELD.SERIAL));
        String user_id = c.getString(c.getColumnIndex(t_freight_forward.FIELD.USER_ID));
        String user_name = c.getString(c.getColumnIndex(t_freight_forward.FIELD.USER_NAME));
        long forward_create_time = c.getLong(c.getColumnIndex(t_freight_forward.FIELD.FORWARD_CREATE_TIME));
        long forward_update_time = c.getLong(c.getColumnIndex(t_freight_forward.FIELD.FORWARD_UPDATE_TIME));

        String freight_id = c.getString(c.getColumnIndex(t_freight_forward.FIELD.FREIGHT_ID));
        String sender_id = c.getString(c.getColumnIndex(t_freight_forward.FIELD.SENDER_ID));
        long create_time = c.getLong(c.getColumnIndex(t_freight_forward.FIELD.CREATE_TIME));
        long update_time = c.getLong(c.getColumnIndex(t_freight_forward.FIELD.UPDATE_TIME));
        String start_region = c.getString(c.getColumnIndex(t_freight_forward.FIELD.START_REGION));
        int start_region_code = c.getInt(c.getColumnIndex(t_freight_forward.FIELD.START_REGION_CODE));
        String end_region = c.getString(c.getColumnIndex(t_freight_forward.FIELD.END_REGION));
        int end_region_code = c.getInt(c.getColumnIndex(t_freight_forward.FIELD.END_REGION_CODE));
        int type = c.getInt(c.getColumnIndex(t_freight_forward.FIELD.TYPE));
        int goods_type = c.getInt(c.getColumnIndex(t_freight_forward.FIELD.GOODS_TYPE));
        String goods_type_name = c.getString(c.getColumnIndex(t_freight_forward.FIELD.GOODS_TYPE_NAME));
        float goods_ton = c.getFloat(c.getColumnIndex(t_freight_forward.FIELD.GOODS_TON));
        int goods_square = c.getInt(c.getColumnIndex(t_freight_forward.FIELD.GOODS_SQUARE));
        String goods_exceed = c.getString(c.getColumnIndex(t_freight_forward.FIELD.GOODS_EXCEED));
        int truck_type_code = c.getInt(c.getColumnIndex(t_freight_forward.FIELD.TRUCK_TYPE_CODE));
        String truck_type_name = c.getString(c.getColumnIndex(t_freight_forward.FIELD.TRUCK_TYPE_NAME));
        int truck_length_code = c.getInt(c.getColumnIndex(t_freight_forward.FIELD.TRUCK_LENGTH_CODE));
        String truck_length_name = c.getString(c.getColumnIndex(t_freight_forward.FIELD.TRUCK_LENGTH_NAME));
        int truck_spare_meter = c.getInt(c.getColumnIndex(t_freight_forward.FIELD.TRUCK_SPARE_METER));
        int status = c.getInt(c.getColumnIndex(t_freight_forward.FIELD.STATUS));
        int order_status = c.getInt(c.getColumnIndex(t_freight_forward.FIELD.ORDER_STATUS));
        String owner_name = c.getString(c.getColumnIndex(t_freight_forward.FIELD.FREIGHT_OWNER_NAME));
        int info_cost = c.getInt(c.getColumnIndex(t_freight_forward.FIELD.INFO_COST));

        FreightForward ff = new FreightForward();
        ff.setId(id);
        ff.setSerial(serial);
        ff.setUser_id(user_id);
        ff.setUser_show_name(user_name);
        ff.setForward_create_time(forward_create_time);
        ff.setForward_update_time(forward_update_time);

        Freight f = new Freight();
        f.setId(freight_id);
        f.setUser_id(sender_id);
        f.setCreate_time(create_time);
        f.setUpdate_time(update_time);
        f.setStart_region(start_region);
        f.setStart_region_code(start_region_code);
        f.setEnd_region(end_region);
        f.setEnd_region_code(end_region_code);
        f.setType(type);
        f.setGoods_type(goods_type);
        f.setGoods_type_name(goods_type_name);
        f.setGoods_ton(goods_ton);
        f.setGoods_square(goods_square);
        f.setGoods_exceed(goods_exceed);
        f.setTruck_type_code(truck_type_code);
        f.setTruck_type_name(truck_type_name);
        f.setTruck_length_code(truck_length_code);
        f.setTruck_length_name(truck_length_name);
        f.setTruck_spare_meter(truck_spare_meter);
        f.setStatus(status);
        f.setOwner_name(owner_name);
        f.setInfo_cost(info_cost);

        ff.setFreight(f);

        return ff;
    }

    private void notifyObserver(final FreightForward ff, final CRUD crud) {
        if (mObservers != null && mObservers.size() > 0) {
            HandlerUtils.post(new Runnable() {
                @Override
                public void run() {
                    for (FreightForwardObserver o : mObservers) {
                        o.onFreightForwardChange(ff, crud);
                    }
                }
            });

        }
    }

    public void addObserver(FreightForwardObserver observer) {
        if (observer == null) {
            return;
        }
        if (mObservers == null) {
            mObservers = new ArrayList<FreightForwardObserver>();
        }
        mObservers.add(observer);
    }

    public void removeObserver(FreightForwardObserver observer) {
        if (mObservers != null && mObservers.size() > 0 && observer != null) {
            mObservers.remove(observer);
        }
    }

    public interface FreightForwardObserver {
        void onFreightForwardChange(FreightForward ff, CRUD crud);
    }
}
