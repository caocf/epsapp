package com.epeisong.base.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.ToastUtils;

public abstract class PendingFragment extends Fragment {

    private View mPendingView;
    private View mFailView;
    private View mContentView;

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        FrameLayout layout = new FrameLayout(getActivity());
        layout.addView(mPendingView = onCreatePendingView(inflater), params);

        mFailView = onCreateFailView(inflater);
        mFailView.setVisibility(View.GONE);
        layout.addView(mFailView, params);

        mContentView = onCreateContentView(inflater);
        if (mContentView != null) {
            mContentView.setVisibility(View.GONE);
            layout.addView(mContentView, params);
        }
        return layout;
    }

    protected abstract View onCreateContentView(LayoutInflater inflater);

    protected abstract void onPendingSuccess(Bundle bundle);

    protected View onCreatePendingView(LayoutInflater inflater) {
        int p = (int) DimensionUtls.getPixelFromDp(20);
        TextView tv = new TextView(getActivity());
        tv.setText("加载中...");
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setPadding(0, p, 0, 0);
        return tv;
    }

    protected View onCreateFailView(LayoutInflater inflater) {
        TextView tv = new TextView(getActivity());
        tv.setText("加载失败！");
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        int p = (int) DimensionUtls.getPixelFromDp(20);
        tv.setPadding(0, p, 0, 0);
        return tv;
    }

    public void endPending(boolean success, Bundle bundle, final OnPendingAgainListener listener) {
        if (success) {
            mPendingView.setVisibility(View.GONE);
            mContentView.setVisibility(View.VISIBLE);
            onPendingSuccess(bundle);
        } else {
            try {
                mPendingView.setVisibility(View.GONE);
                mFailView.setVisibility(View.VISIBLE);
                mFailView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFailView.setVisibility(View.GONE);
                        mPendingView.setVisibility(View.VISIBLE);
                        if (listener != null) {
                            listener.onPendingAgain();
                        }
                    }
                });
            } catch (Exception e) {
                LogUtils.e("PendingFragment", e);
                ToastUtils.showToast("endPending exception!");
            }
        }
    }

    public interface OnPendingAgainListener {
        void onPendingAgain();
    }
}
