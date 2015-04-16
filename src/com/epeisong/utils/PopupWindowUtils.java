package com.epeisong.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

/**
 * 
 * @author poet
 *
 */
public class PopupWindowUtils {

    public static PopupWindow list(Activity a, BaseAdapter adapter, int width, int height, OnItemClickListener l) {
        ListView lv = new ListView(a);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(l);
        lv.setCacheColorHint(Color.TRANSPARENT);
        PopupWindow window = new PopupWindow(lv, width, height);
        window.setFocusable(true);
        window.setBackgroundDrawable(new ColorDrawable());
        return window;
    }
}
