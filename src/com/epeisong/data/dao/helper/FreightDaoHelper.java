package com.epeisong.data.dao.helper;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.epeisong.EpsApplication;
import com.epeisong.data.dao.helper.FreightDaoHelper.t_freight.FIELD;

/**
 * 车源货源数据
 * 
 * @author poet
 * 
 */
public class FreightDaoHelper extends SQLiteOpenHelper {

    // private static final int DB_VERSION = 1;

    /**
     * add comlun sender_name
     */
    private static final int DB_VERSION = 2;

    public FreightDaoHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, getDB_name(), factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = t_freight.getSql();
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            String sql = "ALTER TABLE " + getTableName() + " ADD COLUMN " + FIELD.SENDER_NAME + " TEXT";
            db.execSQL(sql);
            db.delete(getTableName(), null, null);
        }
    }

    private static String getDB_name() {
        return EpsApplication.getDbName("freight");
    }

    public String getTableName() {
        return t_freight.T_NAME;
    }

    public static class t_freight {

        public static String T_NAME = "freight";

        public static String getSql() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
            map.put(FIELD.ID, "TEXT UNIQUE");
            map.put(FIELD.SENDER_ID, "TEXT");
            map.put(FIELD.SENDER_NAME, "TEXT");
            map.put(FIELD.CREATE_TIME, "TEXT");
            map.put(FIELD.UPDATE_TIME, "TEXT");
            map.put(FIELD.START_REGION, "TEXT");
            map.put(FIELD.START_REGION_CODE, "TEXT");
            map.put(FIELD.END_REGION, "TEXT");
            map.put(FIELD.END_REGION_CODE, "TEXT");
            map.put(FIELD.TYPE, "INTEGER");
            map.put(FIELD.GOODS_TYPE, "INTEGER");
            map.put(FIELD.GOODS_TYPE_NAME, "TEXT");
            map.put(FIELD.GOODS_TON, "TEXT");
            map.put(FIELD.GOODS_SQUARE, "TEXT");
            map.put(FIELD.GOODS_EXCEED, "TEXT");
            map.put(FIELD.TRUCK_TYPE_CODE, "INTEGER");
            map.put(FIELD.TRUCK_TYPE_NAME, "TEXT");
            map.put(FIELD.TRUCK_LENGTH_CODE, "TEXT");
            map.put(FIELD.TRUCK_LENGTH_NAME, "TEXT");
            map.put(FIELD.TRUCK_SPARE_METER, "TEXT");
            map.put(FIELD.STATUS, "TEXT");
            map.put(FIELD.ORDER_STATUS, "TEXT");
            map.put(FIELD.FREIGHT_OWNER_NAME, "TEXT");
            map.put(FIELD.INFO_COST, "INTEGER");
            map.put(FIELD.FREIGHT_COST, "INTEGER");
            map.put(FIELD.FORWARD_TO_BLACK_LIST, "INTEGER");

            map.put(FIELD.PUSH_TIME, "TEXT");

            StringBuilder sb = new StringBuilder("CREATE TABLE " + T_NAME + " (");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append(entry.getKey() + " " + entry.getValue() + ",");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.append(" );").toString();
        }

        public static interface FIELD {
            String ID = "id";
            String SENDER_ID = "sender_id";
            String SENDER_NAME = "sender_name";
            String CREATE_TIME = "create_time";
            String UPDATE_TIME = "update_time";
            String START_REGION = "start_region";
            String START_REGION_CODE = "start_region_code";
            String END_REGION = "end_region";
            String END_REGION_CODE = "end_region_code";
            String TYPE = "type";
            String GOODS_TYPE = "goods_type";
            String GOODS_TYPE_NAME = "goods_type_name";
            String GOODS_TON = "goods_ton";
            String GOODS_SQUARE = "goods_square";
            String GOODS_EXCEED = "goods_exceed"; // 三不超
            String TRUCK_TYPE_CODE = "truck_type";
            String TRUCK_TYPE_NAME = "truck_type_name";
            String TRUCK_LENGTH_CODE = "truck_length_code";
            String TRUCK_LENGTH_NAME = "truck_length_name";
            String TRUCK_SPARE_METER = "truch_spare_meter";
            String STATUS = "status";
            String ORDER_STATUS = "order_status";
            String FREIGHT_OWNER_NAME = "freight_owner_name";
            String INFO_COST = "info_cost";
            String FREIGHT_COST = "freight_cost";
            String FORWARD_TO_BLACK_LIST = "forward_to_black_list";

            String PUSH_TIME = "push_time";
        }
    }
}
