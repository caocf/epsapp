package com.epeisong.base.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;

import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.adapter.EndlessAdapter.OnLoadMoreListener;

/**
 * 下拉刷新的fragment基类，处理加载更多、
 * @author poet
 *
 */
public abstract class PullToRefreshBaseFragment extends NetBaseFragment
		implements OnLoadMoreListener {

	protected EndlessAdapter mEndlessAdapter;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mEndlessAdapter = new EndlessAdapter(getActivity(), onCreateAdapter());
		mEndlessAdapter.setOnLoadMoreListener(this);
		mEndlessAdapter.hideBottomView();
		mEndlessAdapter.mUseEndView = false;
	}

	protected abstract ListAdapter onCreateAdapter();
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mEndlessAdapter = null;
	}
}
