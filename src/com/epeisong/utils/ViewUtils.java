package com.epeisong.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * View工具类
 * @author poet
 *
 */
public class ViewUtils {

    public static boolean isPointOnView(Activity a, View v, int screenX, int screenY) {
        if (v == null || v.getVisibility() != View.VISIBLE) {
            return false;
        }
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        return screenX > location[0] && screenX < location[0] + v.getWidth() && screenY > location[1]
                && screenY < location[1] + v.getHeight();
    }

    public static int getListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return 0;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            // listItem.measure(0, 0);
            listItem.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            totalHeight += listItem.getMeasuredHeight();
        }

        totalHeight += totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        return totalHeight;
    }

    public static void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int nWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int nHeight = p.height;

        int nHeightSpec;
        if (nHeight > 0) {
            nHeightSpec = MeasureSpec.makeMeasureSpec(nHeight, MeasureSpec.EXACTLY);
        } else {
            nHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(nWidthSpec, nHeightSpec);
    }

    public static Bitmap getBitmap(View v) {
        if (false) {
            v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.buildDrawingCache();
            Bitmap bitmap = v.getDrawingCache();
            return bitmap;
        }

        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        Bitmap bmp = v.getDrawingCache();
        // v.destroyDrawingCache();
        return bmp;
    }
}
