/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.data.dao.util.CursorParser.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2014年10月22日下午7:41:12
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.data.dao.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import android.content.ContentValues;
import android.database.Cursor;

public class DaoHelperUtils {

    public static void cursorParse(Cursor cursor, Field[] fields, Object object) {
        for (Field field : fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            if (annotations.length > 0) {
                continue;
            }

            field.setAccessible(true);

            String name = field.getName();
            Class<?> c = field.getType();

            Object objValue = null;
            if (Integer.class == c || int.class == c) {
                objValue = cursor.getInt(cursor.getColumnIndex(name));
            } else if (String.class == c) {
                objValue = cursor.getString(cursor.getColumnIndex(name));
            } else if (float.class == c) {
                objValue = cursor.getFloat(cursor.getColumnIndex(name));
            } else if (long.class == c || Long.class == c) {
                objValue = cursor.getLong(cursor.getColumnIndex(name));
            }

            try {
                field.set(object, objValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public static ContentValues getContentValues(Field[] fields, Object object) {
        ContentValues values = new ContentValues();

        for (Field field : fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            if (annotations.length > 0) {
                continue;
            }

            field.setAccessible(true);

            try {
                String name = field.getName();
                Object obj = field.get(object);
                String value = "";
                if (obj != null) {
                    value = obj.toString();
                }

                values.put(name, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return values;
    }
}
