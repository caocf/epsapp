package com.epeisong.base.activity;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import lib.pulltorefresh.PullToRefreshListView;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.epeisong.R;

/**
 * 下拉刷新的Activity
 * 
 * @author poet
 * 
 */
public abstract class PullToRefreshListViewActivity extends BaseActivity
		implements OnItemClickListener, OnRefreshListener<ListView>,
		OnScrollListener {

	protected Mode mRefreshMode;
	protected PullToRefreshListView mPullToRefreshListView;
	protected ListView mListView;
	private ListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.one_ptr_listview);
		mAdapter = onCreateAdapter();
		mListView = onCreateListView();
		mListView.setAdapter(mAdapter);
	}

	protected ListView onCreateListView() {
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.ptr_lv);
		mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
		mPullToRefreshListView.setOnRefreshListener(this);
		mPullToRefreshListView.setOnItemClickListener(this);
		mPullToRefreshListView.setOnScrollListener(this);
		mRefreshMode = mPullToRefreshListView.getMode();
		return mPullToRefreshListView.getRefreshableView();
	}

	protected abstract ListAdapter onCreateAdapter();

	protected void onSetAdapter() {
		mListView.setAdapter(mAdapter);
	}

	protected void setMode(Mode mode) {
		mPullToRefreshListView.setMode(mode);
		mRefreshMode = mode;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_FLING:
			mPullToRefreshListView.setMode(Mode.DISABLED);
			break;
		default:
			mPullToRefreshListView.setMode(mRefreshMode);
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
	}
}
