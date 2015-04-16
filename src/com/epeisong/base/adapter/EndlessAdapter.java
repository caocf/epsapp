package com.epeisong.base.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.SystemUtils;

/**
 * 处理加载更多的adapter
 * 
 * @author poet
 * 
 */
public class EndlessAdapter extends BaseAdapter implements View.OnClickListener {

    public boolean mUseEndView = true;
    private View mUnUseView;

    private String mNoMoreText = "没有更多数据";
    private boolean mIsAutoLoad = true;

    private ListAdapter mWrappedAdapter;
    private OnLoadMoreListener mOnLoadMoreListener;

    private View mEndView;
    private View mProgressBar;
    private TextView mTextView;

    private boolean mHasMore;
    private boolean mIsLoading;
    private boolean mIsLoadFail;

    public EndlessAdapter(Context context, ListAdapter wrapped) {
        mWrappedAdapter = wrapped;
        mEndView = SystemUtils.inflate(R.layout.adapter_endless_bottom_load);
        mEndView.setOnClickListener(this);
        mProgressBar = mEndView.findViewById(R.id.pb);
        mTextView = (TextView) mEndView.findViewById(R.id.tv);

        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean isEmpty() {
        if (mWrappedAdapter != null) {
            return mWrappedAdapter.isEmpty();
        }
        return super.isEmpty();
    }

    @Override
    public int getCount() {
        return mWrappedAdapter.getCount() + 1;
    }

    @Override
    public long getItemId(int position) {
        return mWrappedAdapter.getItemId(position);
    }

    public int getItemViewType(int position) {
        if (position == getWrappedAdapter().getCount()) {
            return (IGNORE_ITEM_VIEW_TYPE);
        }
        return (getWrappedAdapter().getItemViewType(position));
    }

    public int getViewTypeCount() {
        return (getWrappedAdapter().getViewTypeCount() + 1);
    }

    @Override
    public Object getItem(int position) {
        if (position >= getWrappedAdapter().getCount()) {
            return null;
        }
        return (getWrappedAdapter().getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mWrappedAdapter.isEmpty()) {
            return new View(EpsApplication.getInstance());
        }
        if (position == getWrappedAdapter().getCount()) {
            if (!mUseEndView) {
                if (mUnUseView == null) {
                    mUnUseView = new View(EpsApplication.getInstance());
                }
                return mUnUseView;
            }
            if (mIsAutoLoad && !mIsLoading && mHasMore) {
                HandlerUtils.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startLoadMore();
                    }
                }, 300);
            } else if (!mHasMore) {
                mTextView.setText(mNoMoreText);
            } else {
                mTextView.setText("加载更多");
            }

            return mEndView;
        }
        return (getWrappedAdapter().getView(position, convertView, parent));
    }

    @Override
    public void onClick(View v) {
        if (v != mEndView)
            return;
        if (!mIsAutoLoad || mIsLoadFail) {
            if (!mIsLoading && mHasMore)
                startLoadMore();
        }
    }

    public void setHasMore(boolean bHasMore) {
        mHasMore = bHasMore;
        mEndView.setVisibility(View.VISIBLE);
        if (bHasMore) {
            // mProgressBar.setVisibility(View.VISIBLE);
            mTextView.setText("加载更多");
        } else {
            mProgressBar.setVisibility(View.GONE);
            mTextView.setText(mNoMoreText);
        }
    }

    protected void startLoadMore() {
        mIsLoading = true;
        mIsLoadFail = false;
        mProgressBar.setVisibility(View.VISIBLE);
        mTextView.setText("正在加载...");
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onStartLoadMore(this);
        }
    }

    public void endLoad(boolean success) {
        mIsLoading = false;
        // if (!mIsAutoLoad) {
        mProgressBar.setVisibility(View.GONE);
        // }
        if (!success) {
            mEndView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mTextView.setText("加载失败");
            mIsLoadFail = true;
        }
    }

    public void setIsAutoLoad(boolean bAuto) {
        mIsAutoLoad = bAuto;
        if (mIsAutoLoad) {
            mProgressBar.setVisibility(View.VISIBLE);
            // mTextView.setText("加载更多");
        } else {
            if (!mIsLoading) {
                mProgressBar.setVisibility(View.GONE);
                // mTextView.setText("加载更多");
            }
        }
    }

    public void showBottomView() {
        mEndView.setVisibility(View.VISIBLE);
    }

    public void hideBottomView() {
        mEndView.setVisibility(View.GONE);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        if (position >= getWrappedAdapter().getCount()) {
            return false;
        }
        return (getWrappedAdapter().isEnabled(position));
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        getWrappedAdapter().registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        getWrappedAdapter().unregisterDataSetObserver(observer);
    }

    public ListAdapter getWrappedAdapter() {
        return mWrappedAdapter;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    public static interface OnLoadMoreListener {
        public void onStartLoadMore(EndlessAdapter adapter);
    }
}
