package com.epeisong.data.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.epeisong.EpsApplication;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.data.dao.helper.FreightDaoHelper.t_freight;
import com.epeisong.data.dao.helper.FreightOfContactsDaoHelper;
import com.epeisong.data.dao.util.CRUD;
import com.epeisong.model.Freight;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.NotificationUtils;

/**
 * 联系人发布的车源货源
 * 
 * @author poet
 * 
 */
@Deprecated
public class FreightOfContactsDao {

    private static FreightOfContactsDao dao = new FreightOfContactsDao();

    private FreightOfContactsDaoHelper mDaoHelper;

    private String mTableName;

    private List<FreightOfContactsObserver> mObservers;

    private FreightOfContactsDao() {
        mDaoHelper = new FreightOfContactsDaoHelper(EpsApplication.getInstance(), null, null, 0);
        mTableName = mDaoHelper.getTableName();
    }

    public static FreightOfContactsDao getInstance() {
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
        }
    }

    public synchronized boolean insertAll(List<Freight> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        db.beginTransaction();
        for (Freight f : list) {
            long _id = db.insert(mTableName, null, f.getContentValues());
            if (_id < 0) {
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

    public synchronized List<Freight> queryAll() {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, null, null, null, null, t_freight.FIELD.UPDATE_TIME + " desc");
        List<Freight> result = new ArrayList<Freight>();
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        db.close();
        return result;
    }

    public synchronized List<Freight> queryAll(int type) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_freight.FIELD.TYPE + "=?", new String[] { String.valueOf(type) }, null,
                null, t_freight.FIELD.UPDATE_TIME + " desc");
        List<Freight> result = new ArrayList<Freight>();
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        db.close();
        return result;
    }

    public synchronized List<Freight> queryAll(int type, int startCode, int endCode) {
        StringBuilder sb = new StringBuilder();
        if (type == Freight.TYPE_GOODS || type == Freight.TYPE_TRUCK) {
            sb.append(t_freight.FIELD.TYPE + "=" + type);
        }
        if (startCode > 0 && endCode > 0) {
            if (startCode > 1000 && startCode < 9999) {
                sb.append(" and " + t_freight.FIELD.START_REGION_CODE + " BETWEEN " + (startCode * 100) + " and "
                        + ((startCode + 1) * 100 - 1));
            } else if (startCode > 100000 && startCode < 999999) {
                sb.append(" and " + t_freight.FIELD.START_REGION_CODE + "=" + startCode);
            } else if (startCode > 10 && startCode < 99) {
                sb.append(" and " + t_freight.FIELD.START_REGION_CODE + " BETWEEN " + (startCode * 10000) + " and "
                        + ((startCode + 1) * 10000 - 1));
            } else if (startCode != 9) {
                sb.append(" and " + t_freight.FIELD.START_REGION_CODE + " BETWEEN " + (startCode * 100000) + " and "
                        + ((startCode + 1) * 100000 - 1));
            }
            if (endCode > 1000 && endCode < 9999) {
                sb.append(" and " + t_freight.FIELD.END_REGION_CODE + " BETWEEN " + (endCode * 100) + " and "
                        + ((endCode + 1) * 100 - 1));
            } else if (endCode > 10000 && endCode < 999999) {
                sb.append(" and " + t_freight.FIELD.END_REGION_CODE + "=" + endCode);
            } else if (endCode > 10 && endCode < 99) {
                sb.append(" and " + t_freight.FIELD.END_REGION_CODE + " BETWEEN " + (endCode * 10000) + " and "
                        + ((endCode + 1) * 10000 - 1));
            } else if (endCode != 9) {
                sb.append(" and " + t_freight.FIELD.END_REGION_CODE + " BETWEEN " + (endCode * 100000) + " and "
                        + ((endCode + 1) * 100000 - 1));
            }
        }

        String selection = null;
        if (sb.toString().startsWith(" and ")) {
            selection = sb.substring(4);
        } else if (sb.length() > 0) {
            selection = sb.toString();
        }
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, selection, null, null, null, t_freight.FIELD.UPDATE_TIME + " desc");
        List<Freight> result = new ArrayList<Freight>();
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        db.close();
        return result;
    }

    public synchronized List<Freight> queryFirst(int size) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, null, null, null, null, t_freight.FIELD.CREATE_TIME + " desc limit 0,"
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
        List<Freight> result = new ArrayList<Freight>();
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_freight.FIELD.CREATE_TIME + ">=? and " + t_freight.FIELD.ID + ">?",
                new String[] { String.valueOf(last_time), edge_id }, null, null, t_freight.FIELD.CREATE_TIME
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
        Cursor c = db.query(mTableName, null, t_freight.FIELD.CREATE_TIME + "<=? and " + t_freight.FIELD.ID + "<?",
                new String[] { String.valueOf(last_time), edge_id }, null, null, t_freight.FIELD.CREATE_TIME
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

    private Freight parseCursor(Cursor c) {
        String id = c.getString(c.getColumnIndex(t_freight.FIELD.ID));
        String sender_id = c.getString(c.getColumnIndex(t_freight.FIELD.SENDER_ID));
        long create_time = c.getLong(c.getColumnIndex(t_freight.FIELD.CREATE_TIME));
        long update_time = c.getLong(c.getColumnIndex(t_freight.FIELD.UPDATE_TIME));
        String start_region = c.getString(c.getColumnIndex(t_freight.FIELD.START_REGION));
        int start_region_code = c.getInt(c.getColumnIndex(t_freight.FIELD.START_REGION_CODE));
        String end_region = c.getString(c.getColumnIndex(t_freight.FIELD.END_REGION));
        int end_region_code = c.getInt(c.getColumnIndex(t_freight.FIELD.END_REGION_CODE));
        int type = c.getInt(c.getColumnIndex(t_freight.FIELD.TYPE));
        int goods_type = c.getInt(c.getColumnIndex(t_freight.FIELD.GOODS_TYPE));
        String goods_type_name = c.getString(c.getColumnIndex(t_freight.FIELD.GOODS_TYPE));
        float goods_ton = c.getFloat(c.getColumnIndex(t_freight.FIELD.GOODS_TON));
        int goods_square = c.getInt(c.getColumnIndex(t_freight.FIELD.GOODS_SQUARE));
        String goods_exceed = c.getString(c.getColumnIndex(t_freight.FIELD.GOODS_EXCEED));
        int truck_type_code = c.getInt(c.getColumnIndex(t_freight.FIELD.TRUCK_TYPE_CODE));
        String truck_type_name = c.getString(c.getColumnIndex(t_freight.FIELD.TRUCK_TYPE_NAME));
        int truck_length_code = c.getInt(c.getColumnIndex(t_freight.FIELD.TRUCK_LENGTH_CODE));
        String truck_length_name = c.getString(c.getColumnIndex(t_freight.FIELD.TRUCK_LENGTH_NAME));
        int truck_spare_meter = c.getInt(c.getColumnIndex(t_freight.FIELD.TRUCK_SPARE_METER));
        int status = c.getInt(c.getColumnIndex(t_freight.FIELD.STATUS));
        int order_status = c.getInt(c.getColumnIndex(t_freight.FIELD.ORDER_STATUS));
        String owner_name = c.getString(c.getColumnIndex(t_freight.FIELD.FREIGHT_OWNER_NAME));
        int info_cost = c.getInt(c.getColumnIndex(t_freight.FIELD.INFO_COST));
        int freight_cost = c.getInt(c.getColumnIndex(t_freight.FIELD.FREIGHT_COST));

        Freight f = new Freight();
        f.setId(id);
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
        f.setFreight_cost(freight_cost);
        return f;
    }

    private void notifyObserver(final Freight f, final CRUD crud) {
        if (mObservers != null && mObservers.size() > 0) {
            HandlerUtils.post(new Runnable() {
                @Override
                public void run() {
                    for (FreightOfContactsObserver ob : mObservers) {
                        ob.onFreightOfContactsChange(f, crud);
                    }
                }
            });
        }
    }

    public void addObserver(FreightOfContactsObserver ob) {
        if (ob == null) {
            return;
        }
        if (mObservers == null) {
            mObservers = new ArrayList<FreightOfContactsDao.FreightOfContactsObserver>();
        }
        mObservers.add(ob);
    }

    public void removeObserver(FreightOfContactsObserver ob) {
        if (ob != null && mObservers != null) {
            mObservers.remove(ob);
        }
    }

    public interface FreightOfContactsObserver {
        void onFreightOfContactsChange(Freight f, CRUD crud);
    }
}
