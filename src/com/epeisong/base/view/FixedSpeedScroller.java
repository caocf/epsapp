package com.epeisong.base.view;

import java.lang.reflect.Field;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class FixedSpeedScroller extends Scroller {

    private static FixedSpeedScroller sFixedSpeedScroller;

    private int mDuration = 500;

    public FixedSpeedScroller(Context context) {
        super(context);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public static boolean setViewPagerScrollSpeed(int duration, ViewPager viewPager) {
        Field mField;
        try {
            mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            if (sFixedSpeedScroller == null) {
                sFixedSpeedScroller = new FixedSpeedScroller(viewPager.getContext());
            }
            if(duration > 0) {
                sFixedSpeedScroller.setDuration(duration);
            }
            mField.set(viewPager, sFixedSpeedScroller);
            return true;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }
}
