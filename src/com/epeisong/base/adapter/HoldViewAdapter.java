package com.epeisong.base.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 缓存View的Adapter
 * 
 * @author poet
 * @date 2014-12-13 下午11:24:11
 * @param <T>
 */
public abstract class HoldViewAdapter<T> extends HoldDataBaseAdapter<T> {

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder<T> holder = null;
		if (convertView == null) {
			holder = onCreateViewHolder();
			convertView = holder.createView(parent.getContext());
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder<T>) convertView.getTag();
		}
		holder.fillData(getItem(position));
		return convertView;
	}

	protected abstract ViewHolder<T> onCreateViewHolder();

	protected interface ViewHolder<T> {
		View createView(Context context);

		void fillData(T t);
	}
}
