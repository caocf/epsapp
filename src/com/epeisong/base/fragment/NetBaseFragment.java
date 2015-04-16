package com.epeisong.base.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.epeisong.R;

/**
 * 网络请求时的fragment基类：统一处理加载中、加载失败界面
 * @author poet
 *
 */
public abstract class NetBaseFragment extends Fragment {

	private View mContentView;
	private View mLoadingView;
	private View mLoadFailView;

	private boolean mIsFirstLoad;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mIsFirstLoad = true;

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		FrameLayout root = (FrameLayout) inflater.inflate(
				R.layout.one_base_fragment, null);

		mLoadingView = onCreateLoadingView(inflater);
		mLoadingView.setVisibility(View.GONE);
		root.addView(mLoadingView, params);

		mLoadFailView = onCreateLoadFailView(inflater);
		mLoadFailView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mLoadFailView.setVisibility(View.GONE);
				onFailViewClick();
			}
		});
		mLoadFailView.setVisibility(View.GONE);
		root.addView(mLoadFailView, params);

		mContentView = onChildCreateView(inflater, container,
				savedInstanceState);
		mContentView.setVisibility(View.GONE);
		root.addView(mContentView, params);
		return root;
	}

	protected abstract View onChildCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState);

	protected View onCreateLoadingView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.one_loading_view, null);
	}

	protected View onCreateLoadFailView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.one_load_fail_view, null);
	}

	protected abstract void onFailViewClick();

	protected void showLoadingView() {
		mLoadingView.setVisibility(View.VISIBLE);
	}
	
	protected void onRequestComplete(boolean success) {
		if(mIsFirstLoad) {
			if(success) {
				mIsFirstLoad = false;
				mContentView.setVisibility(View.VISIBLE);
				mLoadingView.setVisibility(View.GONE);
			} else {
				mLoadFailView.setVisibility(View.VISIBLE);
			}
		} 
	}

}
