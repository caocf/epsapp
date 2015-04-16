package com.epeisong.data.dao.helper;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDaoHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    public UserDaoHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, getDB_name(), factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String user_sql = t_user.getSql();
        db.execSQL(user_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private static String getDB_name() {
        return "user";
    }

    public String getTableName() {
        return t_user.T_NAME;
    }

    public static class t_user {

        public static String T_NAME = "user";

        public static String getSql() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
            map.put(FIELD.ID, "TEXT UNIQUE");
            map.put(FIELD.ACCOUNT_NAME, "TEXT");
            map.put(FIELD.PHONE, "TEXT");
            map.put(FIELD.LOGO_URL, "TEXT");
            map.put(FIELD.REMARK_PINYIN, "TEXT");
            map.put(FIELD.SHOW_NAME, "TEXT");
            map.put(FIELD.PINYIN, "TEXT");
            map.put(FIELD.CONTACT_NAME, "TEXT");
            map.put(FIELD.CONTACT_PHONE, "TEXT");
            map.put(FIELD.CONTACT_TELEPHONE, "TEXT");
            map.put(FIELD.ADDRESS, "TEXT");
            map.put(FIELD.QQ, "TEXT");
            map.put(FIELD.EMAIL, "TEXT");
            map.put(FIELD.WECHAT, "TEXT");
            map.put(FIELD.REGION, "TEXT");
            map.put(FIELD.REGION_CODE, "TEXT");
            map.put(FIELD.USER_TYPE_CODE, "TEXT");
            map.put(FIELD.USER_TYPE_NAME, "TEXT");
            map.put(FIELD.RECEIVE_CONTACTS_FREIGHT, "TEXT");
            map.put(FIELD.IS_HIDE, "TEXT");
            map.put(FIELD.STAR_LEVEL, "TEXT");
            map.put(FIELD.STATUS, "TEXT");

            map.put(FIELD.SERVE_ID_A, "TEXT");
            map.put(FIELD.SERVE_TYPE_A, "TEXT");
            map.put(FIELD.SERVE_ID_B, "TEXT");
            map.put(FIELD.SERVE_TYPE_B, "TEXT");

            map.put(FIELD.WALLET_ID, "TEXT");

            map.put(FIELD.role_region_code, "TEXT");
            map.put(FIELD.role_region_name, "TEXT");
            map.put(FIELD.role_cur_region_code, "TEXT");
            map.put(FIELD.role_cur_region_name, "TEXT");
            map.put(FIELD.role_cur_longitude, "TEXT");
            map.put(FIELD.role_cur_latitude, "TEXT");
            map.put(FIELD.role_line_start_code, "TEXT");
            map.put(FIELD.role_line_start_name, "TEXT");
            map.put(FIELD.role_line_end_code, "TEXT");
            map.put(FIELD.role_line_end_name, "TEXT");
            map.put(FIELD.role_validity_code, "TEXT");
            map.put(FIELD.role_validity_name, "TEXT");
            map.put(FIELD.role_insurance_code, "TEXT");
            map.put(FIELD.role_insurance_name, "TEXT");
            map.put(FIELD.role_device_code, "TEXT");
            map.put(FIELD.role_device_name, "TEXT");
            map.put(FIELD.role_depot_code, "TEXT");
            map.put(FIELD.role_depot_name, "TEXT");
            map.put(FIELD.role_pack_code, "TEXT");
            map.put(FIELD.role_pack_name, "TEXT");
            map.put(FIELD.role_truck_len_code, "TEXT");
            map.put(FIELD.role_truck_len_name, "TEXT");
            map.put(FIELD.role_truck_type_code, "TEXT");
            map.put(FIELD.role_truck_type_name, "TEXT");
            map.put(FIELD.role_load_ton, "TEXT");
            map.put(FIELD.role_goods_type_code, "TEXT");
            map.put(FIELD.role_goods_type_name, "TEXT");
            map.put(FIELD.role_transport_type_code, "TEXT");
            map.put(FIELD.role_transport_type_name, "TEXT");
            map.put(FIELD.role_weight, "TEXT");
            map.put(FIELD.role_is_full_loaded, "TEXT");

            StringBuilder sb = new StringBuilder("CREATE TABLE " + T_NAME + " (");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append(entry.getKey() + " " + entry.getValue() + ",");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.append(" );").toString();
        }

        public static interface FIELD {
            String ID = "id";
            String ACCOUNT_NAME = "account_name";
            String PHONE = "phone";
            String LOGO_URL = "logo_url";
            String REMARK_PINYIN = "remark_pinyin";
            String SHOW_NAME = "show_name";
            String PINYIN = "pinyin";
            String CONTACT_NAME = "contact_name";
            String CONTACT_PHONE = "contact_phone";
            String CONTACT_TELEPHONE = "contact_telephone";
            String ADDRESS = "address";
            String QQ = "qq";
            String EMAIL = "email";
            String WECHAT = "wechat";
            String REGION = "region";
            String REGION_CODE = "region_code";
            String USER_TYPE_CODE = "user_type_code";
            String USER_TYPE_NAME = "user_type_name";
            String RECEIVE_CONTACTS_FREIGHT = "receive_contacts_freight";
            String IS_HIDE = "is_hide";
            String STAR_LEVEL = "star_level";
            String STATUS = "status";

            String SERVE_ID_A = "serve_id_a";
            String SERVE_TYPE_A = "serve_type_a";
            String SERVE_ID_B = "serve_id_b";
            String SERVE_TYPE_B = "serve_type_b";

            String WALLET_ID = "wallet_id";

            String role_region_code = "role_region_code";
            String role_region_name = "role_region_name";
            String role_cur_region_code = "role_cur_region_code";
            String role_cur_region_name = "role_cur_region_name";
            String role_cur_longitude = "role_cur_longitude";
            String role_cur_latitude = "role_cur_latitude";
            String role_line_start_code = "role_line_start_code";
            String role_line_start_name = "role_line_start_name";
            String role_line_end_code = "role_line_end_code";
            String role_line_end_name = "role_line_end_name";
            String role_validity_code = "role_validity_code";
            String role_validity_name = "role_validity_name";
            String role_insurance_code = "role_insurance_code";
            String role_insurance_name = "role_insurance_name";
            String role_device_code = "role_device_code";
            String role_device_name = "role_device_name";
            String role_depot_code = "role_depot_code";
            String role_depot_name = "role_depot_name";
            String role_pack_code = "role_pack_code";
            String role_pack_name = "role_pack_name";
            String role_truck_len_code = "role_truck_len_code";
            String role_truck_len_name = "role_truck_len_name";
            String role_truck_type_code = "role_truck_type_code";
            String role_truck_type_name = "role_truck_type_name";
            String role_load_ton = "role_load_ton";
            String role_goods_type_code = "role_goods_type_code";
            String role_goods_type_name = "role_goods_type_name";
            String role_transport_type_code = "role_transport_type_code";
            String role_transport_type_name = "role_transport_type_name";
            String role_weight = "role_weight";
            String role_is_full_loaded = "role_is_full_loaded";
        }
    }
}
