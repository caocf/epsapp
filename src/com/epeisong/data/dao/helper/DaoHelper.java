/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.data.dao.helper.DaoHelper.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2014年10月22日下午2:55:55
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.data.dao.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.epeisong.EpsApplication;

public abstract class DaoHelper extends SQLiteOpenHelper {

    public static String TABELNAME;

    public DaoHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, EpsApplication.getDbName(name), factory, version);
        TABELNAME = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = getCreateTableSql(setObject());
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (TABELNAME.equals("info_fee")) {
            if (oldVersion < 2) {
//                String sql = "ALTER TABLE " + TABELNAME + " ADD COLUMN " + "payerGuaranteeProductOwnerLogo" + " TEXT";
//                db.execSQL(sql);
//                sql = "ALTER TABLE " + TABELNAME + " ADD COLUMN " + "payeeGuaranteeProductOwnerLogo" + " TEXT";
//                db.execSQL(sql);
//                db.delete(TABELNAME, null, null);
                db.execSQL("drop table " + TABELNAME);
                onCreate(db);
            }
        }
    }

    private String getCreateTableSql(Class<?> object) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        Field[] fields = object.getDeclaredFields();

        StringBuilder sb = new StringBuilder("CREATE TABLE " + TABELNAME + " (");
        for (Field field : fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            if (annotations.length > 0) {
                continue;
            }
            String name = field.getName();
            Class<?> c = field.getType();

            String type = "TEXT";
            if (Integer.class == c) {
                type = "INTEGER";
            }

            if ("id".equals(name)) {
                type = "TEXT UNIQUE";
            }
            sb.append(name + " " + type + ",");
        }

        sb.deleteCharAt(sb.length() - 1);
        return sb.append(" );").toString();
    }

    public abstract Class<?> setObject();
}
