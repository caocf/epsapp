package com.epeisong.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lib.universal_image_loader.ImageLoaderUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.XBaseActivity.OnChooseDictionaryListener;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.adapter.EndlessAdapter.OnLoadMoreListener;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.EndlessEmptyListView;
import com.epeisong.base.view.PullableListView;
import com.epeisong.data.dao.DictionaryDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.Dictionary;
import com.epeisong.model.User;
import com.epeisong.ui.activity.ContactsDetailActivity;
import com.epeisong.ui.view.Choosable;
import com.epeisong.ui.view.ChooseServeRegionLayout;
import com.epeisong.utils.SystemUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class VerticalFilterFragment extends Fragment implements
OnClickListener, OnItemClickListener, OnLoadMoreListener{

	/**
	 * 自定义View：条件选择项
	 * 
	 * @author poet
	 * 
	 */
	public class VerticalFilterView extends FrameLayout {

		private TextView tv_filter_name;
		private TextView tv_filter_value;
		private ImageView iv_indicator;

		public VerticalFilterView(Context context) {
			this(context, null);
		}

		public VerticalFilterView(Context context, AttributeSet attrs) {
			super(context, attrs);
			LayoutInflater.from(context).inflate(
					R.layout.activity_vertical_filter_item, this);
			tv_filter_name = (TextView) findViewById(R.id.tv_filter_name);
			tv_filter_value = (TextView) findViewById(R.id.tv_filter_value);
			iv_indicator = (ImageView) findViewById(R.id.iv_indicator);
		}

		public void setFilterName(String name) {
			tv_filter_name.setText(name);
		}

		public void setFilterValue(String value) {
			tv_filter_value.setText(value);
			iv_indicator.setSelected(!TextUtils.isEmpty(value));
		}

		public void setFilterValueListener(int pos,
				View.OnClickListener listener) {
			tv_filter_value.setTag(pos);
			tv_filter_value.setOnClickListener(listener);
		}
	}

	private LinearLayout mFilterContainer;
	private FrameLayout mChoosableContainer;
	protected EndlessEmptyListView mListView;
	protected TextView mEmptyTv;
	private View mSearchBtn;

	protected EndlessAdapter mEndlessAdapter;
	protected MyAdapter mAdapter;
	protected static List<VerticalFilterView> mVerticalFilterViews;

	private List<View> mChoosableViews;
	private List<Choosable> mChoosables = new ArrayList<Choosable>();
	private int Logistic_type;

	// private int support_num;
	// private int un_support_num;

	public void hideChoosableView(int pos) {
		mChoosableContainer.setVisibility(View.GONE);
		mChoosableViews.get(pos).setVisibility(View.GONE);
	}

//	@Override
//	public void onBackPressed() {
//		if (!hideChoosableView()) {
//			super.onBackPressed();
//		}
//	}

	@Override
	public void onClick(View v) {
		// TODO
		switch (v.getId()) {
		case R.id.btn_search:
			onClickSearchBtn();
			return;
		case R.id.fl_choosable_container:
			hideChoosableView();
			return;
		}
		Object tag = v.getTag();
		if (tag != null && tag instanceof Integer) {
			int pos = (Integer) tag;
			showChoosableView(pos);

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		position -= mListView.getHeaderViewsCount();
		User user = mAdapter.getItem(position);
		Intent intent = new Intent(getActivity(), ContactsDetailActivity.class);
		intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, user.getId());
		startActivity(intent);
	}

	public void setFilterValue(int pos, String value) {
		mVerticalFilterViews.get(pos).setFilterValue(value);
	}

	private boolean hideChoosableView() {
		if (mChoosableContainer.getVisibility() == View.VISIBLE) {
			mChoosableContainer.setVisibility(View.GONE);
			for (View v : mChoosableViews) {
				v.setVisibility(View.GONE);
			}
			return true;
		}
		return false;
	}

	private void showChoosableView(int pos) {
		Choosable choosable = mChoosables.get(pos);
		int type = choosable.getChooseDictionaryType();
		if (type > 0) {
			OnChooseDictionaryListener listener = choosable
					.getOnChooseDictionaryListener();
			if (listener != null) {
				List<Dictionary> data = DictionaryDao.getInstance()
						.queryByType(type);
				Dictionary d = new Dictionary();
				d.setId(-1);

				// d.setName(choosable.getChooseTitle().substring(2)+"不限");//"车长不限");
				d.setName(choosable.getDefaultChoosion().getName());
				data.add(0, d);
//				showDictionaryListDialog(choosable.getChooseTitle(), data,
//						listener);
			}
		} else {
			View view = mChoosableViews.get(pos);
			if (!(view instanceof ChooseServeRegionLayout)) {
				mChoosableContainer.setVisibility(View.VISIBLE);
			}
			view.setVisibility(View.VISIBLE);
		}
	}

	protected abstract void onClickSearchBtn();
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO
		super.onCreate(savedInstanceState);
//		hideSearchBtn();
		View root = inflater.inflate(R.layout.activity_vertical_filter_layout, null);

		mChoosableContainer = (FrameLayout) root.findViewById(R.id.fl_choosable_container);
		mChoosableContainer.setOnClickListener(this);
		mListView = (EndlessEmptyListView) root.findViewById(R.id.lv);

		View head = SystemUtils.inflate(R.layout.activity_vertical_filter_head);
		head.setOnClickListener(this);
		mFilterContainer = (LinearLayout) head
				.findViewById(R.id.ll_filter_container);
		mSearchBtn = head.findViewById(R.id.btn_search);
		mSearchBtn.setVisibility(View.GONE);
		mSearchBtn.setOnClickListener(this);
		mListView.addHeaderView(head);
		mListView.setOnItemClickListener(this);

		mAdapter = new MyAdapter();
		if (mAdapter != null) {
			mEndlessAdapter = new EndlessAdapter(getActivity(), mAdapter);
			mEndlessAdapter.setIsAutoLoad(false);
			mEndlessAdapter.setOnLoadMoreListener(this);
			mListView.setAdapter(mEndlessAdapter);
		}

		Map<String, Choosable> map = onCreateFilterView();
		if (map != null && !map.isEmpty()) {
			mVerticalFilterViews = new ArrayList<VerticalFilterView>();
			mChoosableViews = new ArrayList<View>();
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1,
					-2);
			int index = 0;
			for (Map.Entry<String, Choosable> entry : map.entrySet()) {
				Choosable choosable = entry.getValue();
				VerticalFilterView v = new VerticalFilterView(getActivity());
				v.setFilterName(entry.getKey());
				v.setFilterValue(choosable.getDefaultChoosion().getName());
				v.setFilterValueListener(index++, this);
				mFilterContainer.addView(v);
				mVerticalFilterViews.add(v);

				View view = choosable.getView();
				if (view != null) {
					view.setVisibility(View.GONE);
					mChoosableContainer.addView(view, params);
					mChoosableViews.add(view);
				} else {
					mChoosableViews.add(new View(getActivity()));
				}

				mChoosables.add(choosable);
			}
		}

		mEmptyTv = (TextView) root.findViewById(R.id.view_empty);
		PullableListView.sMeasureView(head);
		FrameLayout.LayoutParams p = (LayoutParams) mEmptyTv.getLayoutParams();
		p.topMargin = head.getMeasuredHeight();
		mAdapter.setEmptyView(mEmptyTv);
		return root;
	}
	
protected abstract Map<String, Choosable> onCreateFilterView();

	protected void hideSearchBtn() {
		mSearchBtn.setVisibility(View.GONE);
	}

	protected User getLastItem() {
		if (mAdapter.getCount() == 0) {
			return null;
		}
		return mAdapter.getItem(mAdapter.getCount() - 1);
	}

	protected class MyAdapter extends HoldDataBaseAdapter<User> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils.inflate(R.layout.item_search_market);
				holder = new ViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.fillData(getItem(position), position);
			return convertView;
		}
	}

	private class ViewHolder {
		public ImageView iv_market_logo;
		public TextView tv_market_name;
		public TextView tv_market_intro;
		public TextView tv_market_addr;

		private RatingBar ratingBar;

		// TODO
		public void fillData(User u, int pos) {
            if (!TextUtils.isEmpty(u.getLogo_url())) {
                ImageLoader.getInstance().displayImage(u.getLogo_url(), iv_market_logo,
                        ImageLoaderUtils.getListOptionsForUserLogo());
            } else {
                iv_market_logo.setImageResource(User.getDefaultIcon(u.getUser_type_code(), true));
            	StowageInformationFragment.IV_setImageResource(iv_market_logo, u.getUser_type_code());
            }
            
			//参考 HomeFragment
//			switch (Logistic_type) {
//			case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
//				if (TextUtils.isEmpty(u.getLogo_url())) {
//					iv_market_logo.setImageResource(R.drawable.home_inv_goods);
//				}
//				break;
//			case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
//				// 整车运输
//				if (TextUtils.isEmpty(u.getLogo_url())) {
////					iv_market_logo.setBackgroundResource(R.drawable.home_ftl);
//					iv_market_logo.setImageResource(R.drawable.home_ftl);
//				} else {
//
//				}
//				break;
//			case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
//				// 零担专线
//				if (TextUtils.isEmpty(u.getLogo_url())) {
//					iv_market_logo.setImageResource(R.drawable.home_lcl);
//				} else {
//
//				}
//				break;
//			case Properties.LOGISTIC_TYPE_EXPRESS:
//				// 快递
//				if (TextUtils.isEmpty(u.getLogo_url())) {
//					iv_market_logo
//							.setImageResource(R.drawable.home_fast_mail);
//				} else {
//
//				}
//				break;
//			case Properties.LOGISTIC_TYPE_INSURANCE:
//				// 保险
//				if (TextUtils.isEmpty(u.getLogo_url())) {
//					iv_market_logo
//							.setImageResource(R.drawable.home_insurance);
//				} else {
//
//				}
//				break;
//			case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
//				// 配载信息部
//				if (TextUtils.isEmpty(u.getLogo_url())) {
//					iv_market_logo
//							.setImageResource(R.drawable.home_information);
//				} else {
//
//				}
//				break;
//			case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:
//				// 设备租赁
//				if (TextUtils.isEmpty(u.getLogo_url())) {
//					iv_market_logo
//							.setImageResource(R.drawable.home_device_lease);
//				} else {
//
//				}
//				break;
//			}

			tv_market_name.setText(u.getShow_name());
			tv_market_intro.setText(u.getuserintroducation());
			// tv_market_intro.setText(u.getSelf_intro());
			tv_market_addr.setText(u.getAddress());
			ratingBar.setProgress(u.getStar_level());
		}

		public void findView(View v) {
			iv_market_logo = (ImageView) SystemUtils.find(v, R.id.iv_user_logo);
			tv_market_name = (TextView) SystemUtils
					.find(v, R.id.tv_market_name);
			tv_market_intro = (TextView) SystemUtils.find(v,
					R.id.tv_user_intro_content);
			tv_market_addr = (TextView) SystemUtils
					.find(v, R.id.tv_market_addr);
			ratingBar = (RatingBar) SystemUtils.find(v, R.id.ratingBar);
		}
	}
}
