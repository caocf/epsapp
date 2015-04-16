package com.epeisong.model;

import com.epeisong.data.dao.helper.PointDaoHelper.t_point;

import android.content.ContentValues;

/**
 * 界面新内容提醒
 * @author poet
 *
 */
public class Point {

    private int code;
    private int show; // 0不显示，1显示
    private String note;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isShow() {
        return show == 1;
    }

    public void setShow(boolean show) {
        this.show = show ? 1 : 0;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(t_point.FIELD.CODE, code);
        values.put(t_point.FIELD.SHOW, show);
        values.put(t_point.FIELD.NOTE, note);
        return values;
    }

    public static enum PointCode {

        Code_Home(0),
        Code_Task(100), 
        Code_Task_FreightOfContacts(101),
        Code_Task_InfoFee(102),
        Code_Message(200),
        Code_Mine(300),
        Code_Mine_Common_Setup(301),
        Code_Mine_Common_Setup_New_Version(3011),
        Code_Contacts(400),
        Code_Contacts_Fans(401);

        private int mCode;

        PointCode(int code) {
            this.mCode = code;
        };

        public int getValue() {
            return mCode;
        }

        public static PointCode convertFromValue(int code) {
            for (PointCode pointCode : PointCode.values()) {
                if (pointCode.getValue() == code) {
                    return pointCode;
                }
            }
            return null;
        }
    }
}
