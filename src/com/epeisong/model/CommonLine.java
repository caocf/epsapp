package com.epeisong.model;

import com.epeisong.data.dao.helper.CommonDataDaoHelper.t_common_line;

import android.content.ContentValues;

/**
 * 
 * @author poet
 *
 */
public class CommonLine {

    public static final int SCENE_SEARCH = 1;

    private int start_code;
    private String start_name;
    private int start_type;
    private int end_code;
    private String end_name;
    private int end_type;
    private int scene;
    private int count;
    private long update_time;

    public int getStart_code() {
        return start_code;
    }

    public void setStart_code(int start_code) {
        this.start_code = start_code;
    }

    public String getStart_name() {
        return start_name;
    }

    public void setStart_name(String start_name) {
        this.start_name = start_name;
    }

    public int getEnd_code() {
        return end_code;
    }

    public void setEnd_code(int end_code) {
        this.end_code = end_code;
    }

    public String getEnd_name() {
        return end_name;
    }

    public int getStart_type() {
        return start_type;
    }

    public void setStart_type(int start_type) {
        this.start_type = start_type;
    }

    public int getEnd_type() {
        return end_type;
    }

    public void setEnd_type(int end_type) {
        this.end_type = end_type;
    }

    public void setEnd_name(String end_name) {
        this.end_name = end_name;
    }

    public int getScene() {
        return scene;
    }

    public void setScene(int scene) {
        this.scene = scene;
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
        values.put(t_common_line.FIELD.START_CODE, start_code);
        values.put(t_common_line.FIELD.START_NAME, start_name);
        values.put(t_common_line.FIELD.START_TYPE, start_type);
        values.put(t_common_line.FIELD.END_CODE, end_code);
        values.put(t_common_line.FIELD.END_NAME, end_name);
        values.put(t_common_line.FIELD.END_TYPE, end_type);
        values.put(t_common_line.FIELD.SCENE, scene);
        values.put(t_common_line.FIELD.COUNT, count);
        values.put(t_common_line.FIELD.UPDATE_TIME, update_time);
        return values;
    }
}
