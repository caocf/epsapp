package com.epeisong.base.view;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.utils.DimensionUtls;

/**
 * 可以滑动的TabView，允许添加的View的中宽度大于屏幕的宽度
 * @author poet
 *
 */
public class TabBrowser extends HorizontalScrollView implements OnClickListener {

	protected LinearLayout mTabLayout;

	private OnTabItemClickListener mListener;

	public TabBrowser(Context context) {
		this(context, null);
	}

	public TabBrowser(Context context, AttributeSet attrs) {
		super(context, attrs);
		setHorizontalScrollBarEnabled(false);
		mTabLayout = new LinearLayout(context);
		mTabLayout.setOrientation(LinearLayout.HORIZONTAL);
		addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT,
				MATCH_PARENT));
	}

	@Override
	public void onClick(View arg0) {
		Object obj = arg0.getTag();
		if (obj != null && obj instanceof Integer) {
			int index = (Integer) obj;
			if (mListener != null) {
				mListener.onTabItemClick(index, arg0);
			}
		}
	}

	public void setTabs(List<String> titles) {
		if (titles == null || titles.isEmpty()) {
			throw new IllegalArgumentException("tab' titles can not be null!");
		}
		int size = titles.size();
		for (int i = 0; i < size; i++) {
			addTab(i, titles.get(i));
		}
		requestLayout();
	}

	private void addTab(int index, String title) {
		TextView tv = new TextView(getContext());
		tv.setText(title);
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(Color.WHITE);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setMinWidth((int)DimensionUtls.getPixelFromDp(60));
		int padding = (int) DimensionUtls.getPixelFromDp(15);
		tv.setPadding(padding, 0, padding, 0);
//		int colorIndex = index % colors.length;
//		tv.setBackgroundResource(colors[colorIndex]);
		tv.setBackgroundResource(R.color.orange);

		tv.setTag(index);
		tv.setOnClickListener(this);

		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-2, -1);
//		p.leftMargin = 5;
//		p.rightMargin = 5;
		mTabLayout.addView(tv, p);
		
		View v = new View(getContext());
		v.setBackgroundResource(R.color.gray);
		mTabLayout.addView(v, 1, -1);
	}

	public void setOnTabItemClickListener(OnTabItemClickListener listener) {
		this.mListener = listener;
	}

	public interface OnTabItemClickListener {
		void onTabItemClick(int index, View v);
	}
}
