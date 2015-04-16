package com.epeisong.service.notify;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.epeisong.utils.java.Tool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 存储菜单和ACT的DB
 * @author chenchong
 *
 */
 
public class ComActDb extends SQLiteOpenHelper{
	private final static String DATABASE_NAME = "MOBILEEPS.db"; // 数据库名称
	private final static int DATABASE_VERSION = 1;
 
	private static Class[] tableClass = { MenuBean.class  };
 
 
	   public ComActDb(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		for (Class cla : tableClass) {
			if(!isTableExist(cla.getSimpleName())) {
				db.execSQL(getCreateSql(cla));
			}
			
		}
	}
 
 
 
 
 

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // if(newVersion>=oldVersion && !isTableExist("MenuBean")) {
        // db.execSQL(getCreateSql(MenuBean.class));
        // }
        // for (Class cla : tableClass) {
        // db.execSQL("DROP TABLE IF EXISTS " + cla.getSimpleName());
        // }

    }

    /**
     * 插入一个对象
     * 
     * @param obj
     */
    public void insertSql(Object obj) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();
        /* ContentValues */
        ContentValues cv = new ContentValues();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String type = field.getType().toString();
            if ("int".equals(type) || "float".equals(type) || "class java.lang.String".equals(type)) {
                cv.put(field.getName(), String.valueOf(field.get(obj)));
            }
        }

        long row = db.insert(obj.getClass().getSimpleName(), null, cv);
        closeConDb(null, db);
    }

    /**
     * 批量插入
     * 
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public boolean batchInsert(List<?> listBean, Class cla) throws Exception {
        boolean insert = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        if (!Tool.isEmpty(listBean)) {
            for (Object bean : listBean) {
                ContentValues cv = new ContentValues();
                Field[] fields = cla.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    String type = field.getType().toString();
                    if ("int".equals(type) || "float".equals(type) || "class java.lang.String".equals(type)) {
                        cv.put(field.getName(), String.valueOf(field.get(bean)));
                    }
                }
                long _id = db.insert(cla.getSimpleName(), null, cv);
                insert = _id > 0;
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        closeConDb(null, db);
        return insert;
    }

    /**
     * 根据SQL语句直接修改或删除数据
     */
    public void executeSql(String sql) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        closeConDb(null, db);
    }

    /**
     * 根据条件删除数据
     * 
     * @param cla
     * @param where
     */
    public void deleteByWhere(Class cla, String where) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "delete From " + cla.getSimpleName() + " " + Tool.filerStr(where);
        db.execSQL(sql);
        closeConDb(null, db);
    }

    /**
     * 根据条件删除数据 String where = PARAM_GUID + " =?"; String[] whereValue = {
     * String.valueOf(guid) }; //
     */
    // public void deleteByFilter(Class cla,String where,String [] whereValue) {
    //
    // SQLiteDatabase db = this.getWritableDatabase();
    //
    // db.delete(cla.getSimpleName(), where, whereValue);
    // closeConDb(null,db);
    //
    // }
    /**
     * 根据条件查询语句,返回一个对象
     * 
     * @throws Exception
     */
    public Object getBeanByFilter(Class cla, String where) throws Exception {
        Object ob = cla.newInstance();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * From " + cla.getSimpleName() + " " + where, null);

        if (cursor != null) {
            if (cursor.moveToNext()) {
                ob = getObject(cla, cursor);
            }
        }
        closeConDb(cursor, db);
        return ob;
    }

    /**
     * 根据条件查询语句,返回对象列表
     * 
     * @throws Exception
     */
    public List<?> listBeanByFilter(Class cla, String where) throws Exception {
        List<Object> list = new ArrayList<Object>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * From " + cla.getSimpleName() + " " + Tool.filerStr(where), null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(getObject(cla, cursor));
            }
        }
        closeConDb(cursor, db);
        return list;
    }

    @SuppressWarnings({ "unchecked", "unused" })
    private Object getObject(Class cla, Cursor cursor) throws Exception {
        Object ob = cla.newInstance();

        for (Field field : cla.getDeclaredFields()) {
            field.setAccessible(true);
            if ("int".equals(field.getType().toString())) {

                // Method m =
                // cla.getDeclaredMethod("set"+Tool.toUpperFirst(field.getName()),
                // Integer.class);
                String inval = cursor.getString(cursor.getColumnIndex(field.getName()));
                if (Tool.isEmpty(inval)) {
                    field.set(ob, 0);
                    // m.invoke(ob, 0);
                } else {
                    field.set(ob, Integer.parseInt(inval));
                    // m.invoke(ob, Integer.parseInt(inval));
                }
            } else if ("float".equals(field.getType().toString())) {
                // Method m =
                // cla.getDeclaredMethod("set"+Tool.toUpperFirst(field.getName()),
                // Float.class);
                String val = cursor.getString(cursor.getColumnIndex(field.getName()));
                if (Tool.isEmpty(val)) {
                    field.set(ob, 0.0);
                    // m.invoke(ob, 0.0);
                } else {
                    field.set(ob, Float.parseFloat(val));
                    // m.invoke(ob, Float.parseFloat( val));
                }
            } else if ("class java.lang.String".equals(field.getType().toString())) {
                Method m = cla.getDeclaredMethod("set" + Tool.toUpperFirst(field.getName()), String.class);
                String val = Tool.filerStr(cursor.getString(cursor.getColumnIndex(field.getName())));
                m.invoke(ob, val);
            }
        }
        return ob;
    }
 

    /**
     * 创建表语句
     * 
     * @param cla
     * @return
     */
    public String getCreateSql(Class cla) {
        String tableName = cla.getSimpleName();
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE if not exists ").append(tableName).append("( ");
        Field[] fields = cla.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            if (i > 0) {
                sb.append(",");
            }
            sb.append(field.getName()).append(" text ");
        }
        sb.append(");");
        return sb.toString();
    }

    /**
     * 关闭游标和DB
     */
    private void closeConDb(Cursor cursor, SQLiteDatabase db) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
    }

    /**
    * 判断某张表是否存在
    * @param tabName 表名
    * @return
    */
    public boolean isTableExist(String tableName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = null;
            String sql = "select count(1) as c from sqlite_master where type ='table' and name ='" + tableName.trim()
                    + "'";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }
            cursor.close();
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 判断某张表中是否存在某字段(注，该方法无法判断表是否存在，因此应与isTableExist一起应用)
     * @param tabName 表名
     * @param columnName 列名
     * @return
     */
    public boolean isColumnExist(String tableName, String columnName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = null;
            String sql = "select count(1) as c from sqlite_master where type ='table' and name ='" + tableName.trim()
                    + "' and sql like '%" + columnName.trim() + "%'";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }
            cursor.close();
        } catch (Exception e) {
        }
        return result;
    }

  
}
