package com.epeisong.base.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epeisong.utils.DimensionUtls;

public class ListSimpleDialog extends ListDialog<String> {

	public ListSimpleDialog(Activity activity) {
		super(activity);
	}

	@Override
	protected View getItemView(int position, View convertView, ViewGroup parent) {
		TextView tv = new TextView(getContext());
		tv.setText(mAdapter.getItem(position));
		tv.setTextColor(Color.WHITE);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		int p = (int) DimensionUtls.getPixelFromDp(10);
		tv.setPadding(p, p, p, p);
		return tv;
	}

}
