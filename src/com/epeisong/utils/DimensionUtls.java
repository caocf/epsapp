package com.epeisong.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import com.epeisong.EpsApplication;

/**
 * 尺寸：dp、pixel工具类
 * 
 * @author poet
 * 
 */
public class DimensionUtls {

    private static Context context = EpsApplication.getInstance();

    private static float sDensity;

    private static int sScreenWidthInPx;
    private static float sScreenWidthInCm;

    static {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        sDensity = dm.density;

        sScreenWidthInPx = dm.widthPixels;
        sScreenWidthInCm = sScreenWidthInPx / dm.xdpi * 2.54f;
    }

    public static float getPixelValue(int unit, float value) {
        return TypedValue.applyDimension(unit, value, context.getResources().getDisplayMetrics());
    }

    public static float getPixelFromDp(float value) {
        return sDensity * value;
    }

    public static int getPixelFromDpInt(float value) {
        return Math.round(value * sDensity);
    }

    public static float getDensity() {
        return sDensity;
    }

    public static float getScreenWidthInCm() {
        return sScreenWidthInCm;
    }
}
