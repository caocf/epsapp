package com.epeisong.base.dialog;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.dialog.ListNormalDialog.NormalItem;
import com.epeisong.utils.SystemUtils;

/**
 * 普通的列表dialog
 * 
 * @author poet
 * 
 */
public class ListNormalDialog extends ListDialog<NormalItem> {

	public ListNormalDialog(Activity activity) {
		super(activity);
	}

	@Override
	protected View getItemView(int position, View convertView, ViewGroup parent) {
		NormalItem item = mAdapter.getItem(position);
		if (convertView == null) {
			convertView = SystemUtils.inflate(R.layout.dialog_list_normal);
		}
		ImageView iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
		TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
		TextView tv_sub_title = (TextView) convertView
				.findViewById(R.id.tv_sub_title);
		iv_icon.setImageResource(item.getIconResId());
		tv_title.setText(item.getTitle());
		tv_sub_title.setText(item.getSubTitle());
		return convertView;
	}

	public static class NormalItem {
		private int iconResId;
		private String title;
		private String subTitle;

		public NormalItem(int iconResId, String title, String subTitle) {
			super();
			this.iconResId = iconResId;
			this.title = title;
			this.subTitle = subTitle;
		}

		public int getIconResId() {
			return iconResId;
		}

		public String getTitle() {
			return title;
		}

		public String getSubTitle() {
			return subTitle;
		}
	}
}
