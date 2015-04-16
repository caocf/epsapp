package com.epeisong.base.adapter;

import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.view.AdjustHeightGridView;
import com.epeisong.base.view.AdjustHeightGridView.OnBlankTouchListener;

/**
 * 可编辑的GridView的adapter：加减按钮
 * 
 * @author poet
 * 
 * @param <T>
 */
public abstract class GridViewEditableAdapter<T> extends HoldDataBaseAdapter<T>
		implements OnClickListener, OnBlankTouchListener, OnItemClickListener {

	private OnGridViewAddListener mOnGridViewAddLinstener;
	private AdjustHeightGridView mGridView;

	private int mItemWidth;
	private boolean mIsEditable;

	public GridViewEditableAdapter(int itemWidth) {
		mItemWidth = itemWidth;
	}

	public int getRealCount() {
		return super.getCount();
	}

	@Override
	public int getCount() {
		return super.getCount() + 2;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == getCount() - 1) {
			return 2;
		} else if (position == getCount() - 2) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public void onClick(View v) {
		Object tag = v.getTag();
		if (tag != null && tag instanceof String) {
			if ("add".equals(tag)) {
				if (mOnGridViewAddLinstener != null) {
					mOnGridViewAddLinstener.onGridViewAdd();
				}
			} else if ("remove".equals(tag)) {
				mIsEditable = true;
				notifyDataSetChanged();
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position < getCount() - 2) {
			FrameLayout layout = new FrameLayout(EpsApplication.getInstance());
			layout.addView(getRealView(position, convertView, parent));
			if (mIsEditable) {
				ImageView iv = new ImageView(EpsApplication.getInstance());
				iv.setImageResource(R.drawable.ic_red_cycle);
				FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(-2,
						-2);
				p.gravity = Gravity.TOP | Gravity.LEFT;
				iv.setLayoutParams(p);
				layout.addView(iv);
			}
			return layout;
		}
		ImageView iv = new ImageView(EpsApplication.getInstance());
		if (position == getCount() - 2) {
			iv.setImageResource(R.drawable.btn_add_contacts);
			iv.setTag("add");
		} else if (position == getCount() - 1) {
			iv.setImageResource(R.drawable.btn_remove_contacts);
			iv.setTag("remove");
		}
		iv.setOnClickListener(this);
		if (mIsEditable) {
			iv.setVisibility(View.GONE);
		} else {
			if (position == getCount() - 2) {
				iv.setVisibility(View.VISIBLE);
			} else {
				if (getCount() > 2) {
					iv.setVisibility(View.VISIBLE);
				} else {
					iv.setVisibility(View.GONE);
				}
			}
		}
		// int padding = (int) DimensionUtls.getPixelFromDp(10);
		// iv.setPadding(padding, padding, padding, padding);
		LinearLayout ll = new LinearLayout(EpsApplication.getInstance());
		ll.setLayoutParams(new AbsListView.LayoutParams(mItemWidth, mItemWidth));
		ll.addView(iv);
		return ll;
	}

	protected abstract View getRealView(int position, View convertView,
			ViewGroup parent);

	/**
	 * 编辑状态下，去删除选中项。子类若不做处理，返回false，父类会默认删除该item
	 * 
	 * @param pos
	 * @return
	 */
	protected abstract boolean onDoRemoteItem(int pos);

	@Override
	public boolean onBlackTouch(GridView gridView) {
		if (mIsEditable) {
			mIsEditable = false;
			notifyDataSetChanged();
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position > getRealCount() - 1) {
			onBlackTouch(mGridView);
			return;
		}
		if (mIsEditable) {
			if (!onDoRemoteItem(position)) {
				removeItem(getItem(position));
			}
		}
	}

	public void setGridView(AdjustHeightGridView gridView) {
		if (gridView == null || !(gridView instanceof AdjustHeightGridView)) {
			throw new RuntimeException(
					"the gridview can not be null and must be a AdjustHeightGridView");
		}
		mGridView = gridView;
		mGridView.setOnBlankTouchListener(this);
		mGridView.setOnItemClickListener(this);
		mGridView.setAdapter(this);
	}

	public void setOnGridViewAddListener(OnGridViewAddListener listener) {
		mOnGridViewAddLinstener = listener;
	}

	public interface OnGridViewAddListener {
		void onGridViewAdd();
	}
}
