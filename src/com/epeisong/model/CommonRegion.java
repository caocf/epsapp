package com.epeisong.model;

import com.epeisong.data.dao.helper.CommonDataDaoHelper.t_common_region;

import android.content.ContentValues;

public class CommonRegion {

    private int code;
    private int type;
    private int count;
    private long update_time;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(t_common_region.FIELD.CODE, code);
        values.put(t_common_region.FIELD.TYPE, type);
        values.put(t_common_region.FIELD.COUNT, count);
        values.put(t_common_region.FIELD.UPDATE_TIME, update_time);
        return values;
    }
}
