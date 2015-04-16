package com.epeisong.data.dao.helper;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.epeisong.EpsApplication;

/**
 * 粉丝发来的车源货源（有别于联系人发布时，自动推送过来的）
 * 
 * @author poet
 * 
 */
public class FreightForwardDaoHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    public FreightForwardDaoHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, getDB_name(), factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String freight_forward_sql = t_freight_forward.getSql();
        db.execSQL(freight_forward_sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static String getDB_name() {
        return EpsApplication.getDbName("freight_forward");
    }

    public String getTableName_FreightForward() {
        return t_freight_forward.T_NAME;
    }

    public static class t_freight_forward {

        public static String T_NAME = "freight_forward";

        public static String getSql() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
            map.put(FIELD.ID, "TEXT UNIQUE");
            map.put(FIELD.SERIAL, "INTEGER");
            map.put(FIELD.USER_ID, "TEXT");
            map.put(FIELD.USER_NAME, "TEXT");
            map.put(FIELD.FORWARD_CREATE_TIME, "TEXT");
            map.put(FIELD.FORWARD_UPDATE_TIME, "TEXT");

            map.put(FIELD.FREIGHT_ID, "TEXT");
            map.put(FIELD.SENDER_ID, "TEXT");
            map.put(FIELD.CREATE_TIME, "TEXT");
            map.put(FIELD.UPDATE_TIME, "TEXT");
            map.put(FIELD.START_REGION, "TEXT");
            map.put(FIELD.START_REGION_CODE, "TEXT");
            map.put(FIELD.END_REGION, "TEXT");
            map.put(FIELD.END_REGION_CODE, "TEXT");
            map.put(FIELD.TYPE, "INTEGER");
            map.put(FIELD.GOODS_TYPE, "TEXT");
            map.put(FIELD.GOODS_TYPE_NAME, "TEXT");
            map.put(FIELD.GOODS_TON, "TEXT");
            map.put(FIELD.GOODS_SQUARE, "TEXT");
            map.put(FIELD.GOODS_EXCEED, "TEXT");
            map.put(FIELD.TRUCK_LENGTH_CODE, "TEXT");
            map.put(FIELD.TRUCK_LENGTH_NAME, "TEXT");
            map.put(FIELD.TRUCK_TYPE_CODE, "TEXT");
            map.put(FIELD.TRUCK_TYPE_NAME, "TEXT");
            map.put(FIELD.TRUCK_SPARE_METER, "TEXT");
            map.put(FIELD.STATUS, "TEXT");
            map.put(FIELD.ORDER_STATUS, "TEXT");
            map.put(FIELD.FREIGHT_OWNER_NAME, "TEXT");
            map.put(FIELD.INFO_COST, "TEXT");

            StringBuilder sb = new StringBuilder("CREATE TABLE " + T_NAME + " (");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append(entry.getKey() + " " + entry.getValue() + ",");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.append(" );").toString();
        }

        public static interface FIELD {
            String ID = "id";
            String SERIAL = "serial";
            String USER_ID = "user_id"; // 转发（通知）者的id
            String USER_NAME = "user_name";
            String FORWARD_CREATE_TIME = "forward_create_time";
            String FORWARD_UPDATE_TIME = "forward_update_time";

            String FREIGHT_ID = "freight_id";
            String SENDER_ID = "sender_id";
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
            String TRUCK_TYPE_CODE = "truck_type_code";
            String TRUCK_TYPE_NAME = "truck_type_name";
            String TRUCK_LENGTH_CODE = "truck_length_code";
            String TRUCK_LENGTH_NAME = "truck_length_name";
            String TRUCK_SPARE_METER = "truck_spare_meter";
            String STATUS = "status";
            String ORDER_STATUS = "order_status";
            String FREIGHT_OWNER_NAME = "freight_owner_name";
            String INFO_COST = "info_cost";
        }
    }
}
