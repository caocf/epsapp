package com.epeisong.base.dialog;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.utils.DimensionUtls;

/**
 * 列表dialog的基类
 * 
 * @author poet
 * 
 * @param <T>
 */
public abstract class ListDialog<T> extends AlertDialog implements
		OnItemClickListener {

	private TextView mTitleTv;
	private View mLine;
	private ListView mListView;
	protected MyAdapter mAdapter;

	private OnItemClickListener mOnItemClickListener;

	public ListDialog(Activity activity) {
		super(activity);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_list);
		mTitleTv = (TextView) findViewById(R.id.tv_title);
		mLine= findViewById(R.id.title_line);
		mListView = (ListView) findViewById(R.id.lv);
		mListView.setOnItemClickListener(this);
		LayoutParams p = getWindow().getAttributes();
		p.width = (int) (EpsApplication.getScreenWidth() - DimensionUtls
				.getPixelFromDp(40));
		p.height = LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(p);
	}

	public void setData(List<T> data, OnItemClickListener listener) {
		if (mAdapter == null) {
			mAdapter = new MyAdapter();
			mListView.setAdapter(mAdapter);
		}
		mAdapter.replaceAll(data);
		mOnItemClickListener = listener;
	}
	
	public void showTitle(String title) {
		mTitleTv.setVisibility(View.VISIBLE);
		mLine.setVisibility(View.VISIBLE);
		mTitleTv.setText(title);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		dismiss();
		if (mOnItemClickListener != null) {
			mOnItemClickListener.onItemClick(parent, view, position, id);
		}
	}
	
	protected abstract View getItemView(int position, View convertView,
			ViewGroup parent);
	
	protected class MyAdapter extends HoldDataBaseAdapter<T> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getItemView(position, convertView, parent);
		}
	}
}
