package com.epeisong.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.utils.ToastUtils;

/**
 * 快速导航
 * 
 * @author poet
 * 
 */
public class FastBar extends LinearLayout implements OnClickListener {

	private LinearLayout mFastBarContainer;
	private OnFastBarClickListener mOnFastBarClickListener;

	public FastBar(Context context) {
		this(context, null);
	}

	public FastBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		addView(LayoutInflater.from(context).inflate(R.layout.fast_bar, null));
		findViewById(R.id.iv_top).setOnClickListener(this);
		findViewById(R.id.iv_bottom).setOnClickListener(this);
		mFastBarContainer = (LinearLayout) findViewById(R.id.ll_container);
		mFastBarContainer.addView(getPaddingView());
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.iv_top) {
			ToastUtils.showToast("top");
		} else if (id == R.id.iv_bottom) {
			ToastUtils.showToast("bottom");
		} else {
			Object obj = v.getTag();
			if (obj != null && obj instanceof String) {
				ToastUtils.showToast((String) obj);
			}
		}
	}

	private View getPaddingView() {
		View v = new View(getContext());
		LinearLayout.LayoutParams p = new LayoutParams(-1, 0);
		p.weight = 1;
		v.setLayoutParams(p);
		return v;
	}

	public void addFastBar(String bar, String tag) {
		// VerticalTextView tv = new VerticalTextView(getContext());
		TextView tv = new TextView(getContext());
		// tv.setTextSize(EpsApplication.getDensity() * 8);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setTextColor(getResources().getColor(R.color.gray));
		tv.setText(bar);
		tv.setTag(tag);
		tv.setOnClickListener(this);
		mFastBarContainer.addView(tv);
		mFastBarContainer.addView(getPaddingView());
	}

	public void setOnFastBarClickListener(OnFastBarClickListener listener) {
		this.mOnFastBarClickListener = listener;
	}

	public interface OnFastBarClickListener {
		void onFastBarClick(View v, String tag);
	}
}
