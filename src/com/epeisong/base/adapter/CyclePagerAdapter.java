package com.epeisong.base.adapter;

import android.view.View;
import android.view.ViewGroup;

public abstract class CyclePagerAdapter extends CommonPagerAdapter {

    @Override
    public int getCount() {
        if (getRealCount() >= 2) {
            return Integer.MAX_VALUE;
        }
        return getRealCount();
    }

    public int getRealCount() {
        return super.getCount();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    protected final View getView(View v, int nPos) {
        if (nPos >= getRealCount()) {
            nPos = nPos % getRealCount();
        }
        return getCycleView(v, nPos);
    }

    protected abstract View getCycleView(View v, int pos);
}
