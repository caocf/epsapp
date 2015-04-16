package com.epeisong.base.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 数据管理的adapter
 * 
 * @author poet
 * 
 * @param <T>
 */
public abstract class HoldDataBaseAdapter<T extends Object> extends BaseAdapter {

    public static final int MAX_SIZE_FLOOR = 10;

    protected List<T> mObjectList = new ArrayList<T>();
    private int mCurrentPosition = -1;
    private int mMaxSize;
    private View mEmptyView;

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }

    @Override
    public void notifyDataSetChanged() {
        if (mEmptyView != null) {
            if (getCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        }
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mObjectList.size();
    }

    @Override
    public T getItem(int position) {
        return mObjectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    public void replaceAll(Collection<T> collection) {
        mObjectList.clear();
        if (collection != null) {
            mObjectList.addAll(collection);
        }
        notifyDataSetChanged();
    }

    public void addAll(Collection<T> collection) {
        if (collection != null) {
            mObjectList.addAll(collection);
            checkSize(true);
        }
        notifyDataSetChanged();
    }

    public void addAllJust(Collection<T> collection) {
        if (collection != null) {
            mObjectList.addAll(collection);
            checkSize(true);
        }
    }

    public void addAll(int pos, Collection<T> collection) {
        if (collection != null) {
            mObjectList.addAll(pos, collection);
            if (pos < mObjectList.size() / 2) {
                checkSize(false);
            } else {
                checkSize(true);
            }
        }
        notifyDataSetChanged();
    }

    public void addItem(T t) {
        mObjectList.add(t);
        checkSize(true);
        notifyDataSetChanged();
    }

    public void addItem(int location, T t) {
        mObjectList.add(location, t);
        if (location < mObjectList.size() / 2) {
            checkSize(false);
        } else {
            checkSize(true);
        }
        notifyDataSetChanged();
    }

    public boolean removeItem(T t) {
        boolean removed = mObjectList.remove(t);
        if (removed) {
            notifyDataSetChanged();
        }
        return removed;
    }

    public int indexOf(T t) {
        return mObjectList.indexOf(t);
    }

    public void removeAllItem(List<T> list) {
        mObjectList.removeAll(list);
        notifyDataSetChanged();
    }

    public List<T> getAllItem() {
        return mObjectList;
    }

    public void clear() {
        mObjectList.clear();
        notifyDataSetChanged();
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public void setCurrentPosition(int pos) {
        this.mCurrentPosition = pos;
        this.notifyDataSetChanged();
    }

    private void checkSize(boolean cutTop) {
        if (mMaxSize >= MAX_SIZE_FLOOR && mObjectList.size() > mMaxSize) {
            if (cutTop) {
                int offset = mObjectList.size() - mMaxSize;
                mObjectList = mObjectList.subList(offset, offset + mMaxSize);
            } else {
                mObjectList = mObjectList.subList(0, mMaxSize);
            }
        }
    }

    public void setMaxSize(int maxSize) {
        if (maxSize >= MAX_SIZE_FLOOR) {
            mMaxSize = maxSize;
        }
    }
}
