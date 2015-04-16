package com.epeisong.base.fragment;

import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import lib.pulltorefresh.PullToRefreshListView;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.SystemUtils;

/**
 * 下拉刷新的fragment基类：带有右侧快捷导航和过滤view
 * 
 * @author poet
 * 
 */
public abstract class PullToRefreshListViewFragment extends PullToRefreshBaseFragment implements OnScrollListener,
        OnRefreshListener<ListView>, OnItemClickListener, OnItemLongClickListener {

    protected FrameLayout mTopLayout;
    private View mRootView;
    protected PullToRefreshListView mPullToRefreshListView;
    protected ListView mListView;
    protected ListAdapter mAdapter;
    private boolean mIsSetedAdapter;

    private View mEmptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mIsSetedAdapter = false;
        LinearLayout root = new LinearLayout(getActivity());
        root.setOrientation(LinearLayout.VERTICAL);
        mTopLayout = new FrameLayout(getActivity());
        root.addView(mTopLayout);
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(-1, -1);
        root.addView(super.onCreateView(inflater, container, savedInstanceState), p1);
        return root;
    }

    @Override
    protected View onChildCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (FrameLayout) inflater.inflate(R.layout.one_ptr_listview_with_fastbar, null);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = mEndlessAdapter.getWrappedAdapter();
        mListView = onCreateListView();
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mAdapter.registerDataSetObserver(mDataSetObserver);

        mEmptyView = onCreateEmptyView();
        FrameLayout emptyLayout = (FrameLayout) view.findViewById(R.id.emptyLayout);
        emptyLayout.addView(mEmptyView, new ViewGroup.LayoutParams(-1, -1));
    }

    protected ListView onCreateListView() {
        mPullToRefreshListView = (PullToRefreshListView) mRootView.findViewById(R.id.ptr_lv);
        mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
        mPullToRefreshListView.setOnRefreshListener(this);
        return mPullToRefreshListView.getRefreshableView();
    }

    protected View onCreateEmptyView() {
        return SystemUtils.inflate(R.layout.empty_layout);
    }

    protected final View createEmptyViewDefault(String msg) {
        LinearLayout emptyLayout = new LinearLayout(getActivity());
        emptyLayout.setBackgroundColor(Color.WHITE);
        emptyLayout.setOrientation(LinearLayout.VERTICAL);
        emptyLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        emptyLayout.setPadding(0, DimensionUtls.getPixelFromDpInt(120), 0, 0);
        ImageView iv = new ImageView(getActivity());
        iv.setImageResource(R.drawable.nopeihuo);
        emptyLayout.addView(iv);
        TextView tv = new TextView(getActivity());
        tv.setText(msg);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setTextColor(Color.argb(0xff, 0xaa, 0xaa, 0xaa));
        tv.setGravity(Gravity.CENTER);
        emptyLayout.addView(tv);
        return emptyLayout;
    }

    protected void onRequestComplete(boolean success) {
        super.onRequestComplete(success);
        if (!mIsSetedAdapter) {
            if (success) {
                mPullToRefreshListView.setAdapter(mEndlessAdapter);
                mIsSetedAdapter = true;
            }
        }
        mPullToRefreshListView.onRefreshComplete();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter.unregisterDataSetObserver(mDataSetObserver);
    }

    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            HandlerUtils.post(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter.isEmpty()) {
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        mEmptyView.setVisibility(View.GONE);
                    }
                }
            });
        };
    };
}
