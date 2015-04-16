package com.epeisong.plug.point;

import com.epeisong.plug.TabPoint;

import android.content.ContentValues;

/**
 * 提醒
 * @author poet
 *
 */
public class Point {

    public static final int code_home = 1;
    public static final int code_msg = 2;
    public static final int code_order = 3;
    public static final int code_order_infofee = 301;
    public static final int code_order_infofee_exeing = 30101;
    public static final int code_order_infofee_complete = 30102;
    public static final int code_order_infofee_cancel = 30103;
    public static final int code_mine = 4;
    public static final int code_contacts = 5;
    public static final int code_contacts_fans = 501;

    private int code;
    private int show; // 0不显示，1显示

    public Point(int code, boolean bShow) {
        super();
        this.code = code;
        this.show = bShow ? 1 : 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(TabPoint.FIELD.CODE, code);
        values.put(TabPoint.FIELD.SHOW, show);
        return values;
    }
}
