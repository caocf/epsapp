package com.epeisong.data.dao.helper;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.epeisong.EpsApplication;

/**
 * 联系人标签关系
 * @author poet
 *
 */
public class ContactsTagDaoHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    public ContactsTagDaoHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, getDB_name(), factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = t_contacts_tag.getSql();
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private static String getDB_name() {
        return EpsApplication.getDbName("contacts_tag");
    }

    public String getTableName() {
        return t_contacts_tag.T_NAME;
    }

    public static class t_contacts_tag {
        public static String T_NAME = "contacts_tag";

        public static String getSql() {
            Map<String, String> map = new HashMap<String, String>();
            map.put(FIELD.ID, "TEXT");
            map.put(FIELD.CONTACTS_ID, "TEXT");
            map.put(FIELD.TAG_ID, "TEXT");
            map.put(FIELD.UPDATE_TIME, "TEXT");

            StringBuilder sb = new StringBuilder("CREATE TABLE " + T_NAME + " (");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append(entry.getKey() + " " + entry.getValue() + ",");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.append(" );").toString();
        }

        public static interface FIELD {
            String ID = "id";
            String CONTACTS_ID = "contacts_id";
            String TAG_ID = "tag_id";
            String UPDATE_TIME = "update_time";
        }
    }
}
