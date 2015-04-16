package com.epeisong.data.dao.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.epeisong.EpsApplication;
import com.epeisong.base.db.BaseSqliteOpenHelper;
import com.epeisong.model.InfoFee;

public class InfoFeeDbHelper extends BaseSqliteOpenHelper {

    static InfoFeeDbHelper instance;

    public static InfoFeeDbHelper getInstance() {
        if (instance == null) {
            instance = new InfoFeeDbHelper(EpsApplication.getInstance(), null, null, 0);
        }
        return instance;
    }

    public InfoFeeDbHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, EpsApplication.getDbName("InfoFee"), factory, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getCreateTableSql(InfoFee.class));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
