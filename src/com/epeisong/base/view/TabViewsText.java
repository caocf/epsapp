package com.epeisong.base.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.utils.SystemUtils;

/**
 * tabview：TextView的tab，可在text右侧显示数字
 * 
 * @author poet
 * 
 */
public class TabViewsText extends TabViews {

	private List<TextView> mTitleTvs;
	private List<TextView> mTabRightTvs;

	private float mTextSizeInSp;
	private int mTextColorResId;

	public TabViewsText(Context context) {
		super(context);
	}

	public TabViewsText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTitleTvs = new ArrayList<TextView>();
		mTabRightTvs = new ArrayList<TextView>();
	}

	public void setTextSizeInSp(float size) {
		mTextSizeInSp = size;
	}

	public void setTextColorResId(int resId) {
		mTextColorResId = resId;
	}

	@Override
	protected void onTabChange(int tab) {
		for (int i = 0; i < mTitleTvs.size(); i++) {
			if (i == tab) {
				mTitleTvs.get(i).setSelected(true);
			} else {
				mTitleTvs.get(i).setSelected(false);
			}
		}
	}

	@Override
	public void setTab(int pos) {
		super.setTab(pos);
		mTitleTvs.get(pos).setSelected(true);
	}

	public void setTabTexts(List<String> titles) {
		List<View> views = new ArrayList<View>();
		for (String title : titles) {
			View v = SystemUtils.inflate(R.layout.tab_view_text_item);
			TextView tv_center = (TextView) v.findViewById(R.id.tv);
			tv_center.setText(title);
			if (mTextSizeInSp > 0) {
				tv_center
						.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSizeInSp);
			}
			if (mTextColorResId > 0) {
				ColorStateList list = getContext().getResources()
						.getColorStateList(mTextColorResId);
				if (list != null) {
					tv_center.setTextColor(list);
				} else {
					tv_center.setTextColor(getContext().getResources()
							.getColor(mTextColorResId));
				}
			}
			mTitleTvs.add(tv_center);
			TextView tv_right = (TextView) v.findViewById(R.id.tv_right);
			mTabRightTvs.add(tv_right);
			views.add(v);
		}
		setTabViews(views);
		setIndicatorColorResId(R.color.blue);
	}

	public void setTabRightTv(int pos, int count) {
		if (pos < 0 || pos > mTabRightTvs.size() - 1) {
			return;
		}
		TextView tv = mTabRightTvs.get(pos);
		if (count > 0) {
			tv.setText(String.valueOf(count));
			tv.setVisibility(View.VISIBLE);
		} else {
			tv.setText("");
			tv.setVisibility(View.GONE);
		}
	}
}
