package com.epeisong.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.ToastUtils;

public class ChooseFreightTypeLayout extends FrameLayout implements
		OnItemClickListener {

	private Fragment mFragment;

	private MyAdapter mAdapter;
	private List<String> mData;
	private int mSelectedPos;

	private OnChooseFreightTypeListener mListener;

	public ChooseFreightTypeLayout(Context context) {
		this(context, null);
	}

	public ChooseFreightTypeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mData = new ArrayList<String>();
		mData.add("全部车源货源");
		mData.add("车源");
		mData.add("货源");
		ListView lv = new ListView(context);
		lv.setAdapter(mAdapter = new MyAdapter());
		mAdapter.replaceAll(mData);
		lv.setOnItemClickListener(this);
		lv.setBackgroundColor(Color.WHITE);
		
		this.addView(lv);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mListener == null) {
			ToastUtils.showToast("Please set listener first!");
			return;
		}
		if (mSelectedPos == position) {
			mListener.onChoosedFreightType(null, -1, false);
		} else {
			mSelectedPos = position;
			int type = -1;
			if (mSelectedPos == 0) {
				type = OnChooseFreightTypeListener.FREIGHT_TYPE_ALL;
			} else if (mSelectedPos == 1) {
				type = OnChooseFreightTypeListener.FREIGHT_TYPE_TRUCK;
			} else if (mSelectedPos == 2) {
				type = OnChooseFreightTypeListener.FREIGHT_TYPE_GOODS;
			}
			mListener.onChoosedFreightType(mData.get(mSelectedPos), type, true);
			mAdapter.notifyDataSetChanged();
		}
	}

	private class MyAdapter extends HoldDataBaseAdapter<String> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RelativeLayout rl = new RelativeLayout(getContext());
			int h = (int) DimensionUtls.getPixelFromDp(50);
			rl.setLayoutParams(new AbsListView.LayoutParams(-1, h));
			TextView tv = new TextView(getContext());
			tv.setText(getItem(position));
			tv.setTextColor(Color.BLACK);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					-2, -2);
			params.leftMargin = (int) DimensionUtls.getPixelFromDp(10);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			rl.addView(tv, params);
			ImageView iv = new ImageView(getContext());
			iv.setImageResource(R.drawable.selector_common_hook);
			if (mSelectedPos == position) {
				iv.setSelected(true);
			} else {
				iv.setSelected(false);
			}
			RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
					-2, -2);
			params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params2.addRule(RelativeLayout.CENTER_VERTICAL);
			params2.rightMargin = (int) DimensionUtls.getPixelFromDp(10);
			rl.addView(iv, params2);
			return rl;
		}
	}

	public void setOnChooseFreightTypeListener(
			OnChooseFreightTypeListener listener) {
		mListener = listener;
	}

	public interface OnChooseFreightTypeListener {

		public static final int FREIGHT_TYPE_ALL = 1;
		public static final int FREIGHT_TYPE_TRUCK = 2;
		public static final int FREIGHT_TYPE_GOODS = 3;

		void onChoosedFreightType(String name, int type, boolean change);
	}
	
	public void setFragment(Fragment fragment) {
		mFragment = fragment;
	}
}
