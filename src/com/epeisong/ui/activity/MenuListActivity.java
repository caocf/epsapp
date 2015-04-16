package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.SimpleListActivity;
import com.epeisong.ui.activity.MenuListActivity.Menu;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;

public abstract class MenuListActivity extends SimpleListActivity<Menu> {

	public static class Menu {
		public static final int TYPE_INVALID = -1;
		public static final int TYPE_NORMAL = 1;
		public static final int TYPE_NOICON = 2;

		private int type;
		private int resId;
		private String name;
		private boolean showPoint;
		private boolean showArrow;
		private Runnable runnable;
		private int flag;
		
		/**
		 * @param type
		 * @param resId
		 * @param showArrow
		 * @param flag 根据赋的值来判断最右侧显示什么样的图片 (1:大箭头 其他:小箭头)
		 * @param name
		 * @param runnable
		 */
		public Menu(int type, int resId, boolean showArrow, int flag, String name, Runnable runnable) {
			super();
			this.type = type;
			this.resId = resId;
			this.showArrow = showArrow;
			this.flag = flag;
			this.name = name;
			this.runnable = runnable;
		}

		public String getName() {
			return name;
		}

		public int getResId() {
			return resId;
		}

		public int getType() {
			return type;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setResId(int resId) {
			this.resId = resId;
		}

		public void setType(int type) {
			this.type = type;
		}

		public Menu setShowPoint(boolean show) {
			showPoint = show;
			return this;
		}

		public boolean getShowPoint() {
			return showPoint;
		}

		public boolean getShowArrow() {
			return showArrow;
		}

		public Menu setShowArrow(boolean Arrow) {
			showArrow = Arrow;
			return this;
		}

		public int getFlag() {
			return flag;
		}

		public void setFlag(int flag) {
			this.flag = flag;
		}

		public Runnable getRunnable() {
			return runnable;
		}

		public void setRunnable(Runnable runnable) {
			this.runnable = runnable;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<Menu> data = new ArrayList<Menu>();
		onSetData(data);
		mAdapter.replaceAll(data);
	}

	protected abstract void onSetData(List<Menu> data);
	
	@Override
	public final void onItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		Runnable runnable = mAdapter.getItem(position).getRunnable();
		if (runnable != null) {
			view.post(runnable);
		}
	}

	@Override
	protected Integer getAdapterViewType(int position) {
		return mAdapter.getItem(position).getType();
	}

	@Override
	protected Integer getAdapterViewTypeCount() {
		return 2;
	}

	@Override
	protected View getItemView(int position, View convertView, ViewGroup parent) {
		Menu menu = mAdapter.getItem(position);
		if (menu.getType() == Menu.TYPE_INVALID) {
			View v = new View(this);
			int h = (int) DimensionUtls.getPixelFromDp(15);
			v.setLayoutParams(new AbsListView.LayoutParams(-1, h));
			v.setBackgroundColor(Color.argb(0xED, 0xED, 0xED, 0xED));
			return v;
		}
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = SystemUtils.inflate(R.layout.activity_menu_list_item);
			holder = new ViewHolder();
			holder.findView(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.fillData(menu);
		return convertView;
	}

	private class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		ImageView iv_point;
		ImageView iv_arrow;

		public void findView(View v) {
			iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
			tv_name = (TextView) v.findViewById(R.id.tv_name);
			iv_point = (ImageView) v.findViewById(R.id.iv_point);
			iv_arrow = (ImageView) v.findViewById(R.id.iv_arrow);
		}

		public void fillData(Menu menu) {
			if (menu.getResId() > 0) {
				iv_icon.setImageResource(menu.getResId());
			}

			if (menu.getType() == Menu.TYPE_NOICON)
				iv_icon.setVisibility(View.GONE);

			tv_name.setText(menu.getName());
			if (menu.getShowPoint()) {
				iv_point.setVisibility(View.VISIBLE);
			} else {
				iv_point.setVisibility(View.GONE);
			}
			
			if(menu.getShowArrow()){
				iv_arrow.setVisibility(View.VISIBLE);
			}else{
				iv_arrow.setVisibility(View.GONE);
			}
			iv_arrow.setBackgroundResource(0);
			if(menu.getFlag() != 1){
				iv_arrow.setBackgroundResource(R.drawable.icon_arrow_right_small);
			}else{
				iv_arrow.setBackgroundResource(R.drawable.icon_arrow_right);
			}
		}
	}
}
