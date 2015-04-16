package com.epeisong.base.db;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite数据库基类
 * 
 * 常见问题：
 *  1、android.database.sqlite.SQLiteConstraintException: error code 19:
 * constraint failed 
 * 	情况1：你定义的字段为 not null而插入时对应的字段却没值。
 * 	情况2：你定义的字段设定PRIMARY，而插入的值已经在表中存在。
 * 
 * 
 * @author poet
 * @date 2015-4-4 下午7:47:47
 */
public abstract class BaseSqliteOpenHelper extends SQLiteOpenHelper {

    public BaseSqliteOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    protected String getCreateTableSql(Class<?> clazz) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        getProperties(true, clazz, map);

        StringBuilder sb = new StringBuilder("CREATE TABLE " + clazz.getSimpleName() + " (");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey() + " " + entry.getValue() + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.append(" );").toString();
    }

    void getProperties(boolean forCreateTable, Class<?> clazz, Map<String, String> map) {
        Field[] fields = clazz.getDeclaredFields();
        List<String> primaryKeyNames = new ArrayList<String>();
        for (Field f : fields) {
            f.setAccessible(true);
            Property annotation = f.getAnnotation(Property.class);
            if (annotation != null) {
                String name = f.getName();
                String value = annotation.type();
                if (!Property.TYPE_FOR_ID.equals(value)) {
                    if (annotation.primaryKey()) {
                        primaryKeyNames.add(name);
                    }
                    map.put(name, value);
                }
            }
        }
        if (forCreateTable && primaryKeyNames.size() > 0) {
            StringBuilder sb = new StringBuilder("(");
            int i = 0;
            for (String name : primaryKeyNames) {
                if (i++ > 0) {
                    sb.append(",");
                }
                sb.append(name);
            }
            map.put("primary key", sb.append(")").toString());
        }
    }

    protected ContentValues getContentValues(Object obj) throws NoSuchFieldException, IllegalAccessException,
            IllegalArgumentException {
        Map<String, String> map = new HashMap<String, String>();
        getProperties(false, obj.getClass(), map);
        ContentValues values = new ContentValues();
        Set<String> set = map.keySet();
        for (String name : set) {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            Object value = field.get(obj);
            values.put(name, value == null ? "" : value.toString());
        }
        return values;
    }

    protected boolean isTableExist(SQLiteDatabase db, String tableName) {
        boolean exist = false;
        String sql = "SELECT COUNT(*) FROM sqlite_master where type='table' and name='" + tableName + "'";
        Cursor c = null;
        try {
            c = db.rawQuery(sql, null);
            if (c.moveToNext()) {
                if (c.getInt(0) > 0) {
                    exist = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return exist;
    }

    public void createTableIfNotExist(Class<?> clazz) {
        SQLiteDatabase db = getWritableDatabase();
        if (!isTableExist(db, clazz.getSimpleName())) {
            String sql = getCreateTableSql(clazz);
            db.execSQL(sql);
        }
        db.close();
    }

    public boolean insert(Object obj) {
        ContentValues values;
        try {
            values = getContentValues(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        long _id = db.insert(obj.getClass().getSimpleName(), null, values);
        db.close();
        return _id > 0;
    }

    public boolean replace(Object obj, Condition condition) {
        ContentValues values;
        try {
            values = getContentValues(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        long _id = db.replace(obj.getClass().getSimpleName(), null, values);
        return _id > 0;
    }

    public boolean updateById(Object obj, String id) {
        return update(obj, new Condition().equal("id", id));
    }

    public boolean update(Object obj, Condition condition) {
        ContentValues values;
        try {
            values = getContentValues(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        String whereClause = condition == null ? null : condition.getCondition();
        SQLiteDatabase db = getWritableDatabase();
        int count = db.update(obj.getClass().getSimpleName(), values, whereClause, null);
        db.close();
        return count > 0;
    }

    public boolean deleteById(Class<?> clazz, String id) {
        return delete(clazz, new Condition().equal("id", id));
    }

    public boolean delete(Class<?> clazz, Condition condition) {
        SQLiteDatabase db = getWritableDatabase();
        int count = db.delete(clazz.getSimpleName(), condition == null ? null : condition.getCondition(), null);
        db.close();
        return count > 0;
    }

    public <T> T queryById(Class<T> clazz, String id) {
        return query(clazz, new Condition().equal("id", id));
    }

    public <T> T query(Class<T> clazz, Condition condition) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(clazz.getSimpleName(), null, condition == null ? null : condition.getCondition(),
                null, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return parseCursor(clazz, cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return null;
    }

    public <T> List<T> queryList(Class<T> clazz, Condition condition) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(clazz.getSimpleName(), null, condition == null ? null : condition.getCondition(),
                null, null, null, condition == null ? null : condition.getOrderBy());
        List<T> result = new ArrayList<T>();
        try {
            while (cursor.moveToNext()) {
                result.add(parseCursor(clazz, cursor));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            cursor.close();
            db.close();
        }
        return result;
    }

    <T> T parseCursor(Class<T> clazz, Cursor c) throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        Constructor<T> constructor = clazz.getConstructor();
        T t = constructor.newInstance();
        Map<String, String> map = new HashMap<String, String>();
        getProperties(false, clazz, map);
        Set<String> keySet = map.keySet();
        for (String name : keySet) {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            Class<?> type = field.getType();
            Object value = null;
            if (type == String.class) {
                value = c.getString(c.getColumnIndex(name));
            } else if (type == int.class || type == Integer.class) {
                value = c.getInt(c.getColumnIndex(name));
            } else if (type == float.class || type == Float.class) {
                value = c.getFloat(c.getColumnIndex(name));
            } else if (type == double.class || type == Double.class) {
                value = c.getDouble(c.getColumnIndex(name));
            } else if (type == long.class || type == Long.class) {
                value = c.getLong(c.getColumnIndex(name));
            } else {
                continue;
            }
            field.set(t, value);
        }
        return t;
    }
}
