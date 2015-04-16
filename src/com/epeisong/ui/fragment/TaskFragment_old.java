//package com.epeisong.ui.fragment;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Color;
//import com.epeisong.utils.android.AsyncTask;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.TypedValue;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.AbsListView;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.epeisong.PointObserver;
//import com.epeisong.R;
//import com.epeisong.base.adapter.HoldDataBaseAdapter;
//import com.epeisong.base.fragment.NetBaseFragment;
//import com.epeisong.data.dao.FreightOfContactsDao;
//import com.epeisong.data.dao.FreightOfContactsDao.FreightOfContactsObserver;
//import com.epeisong.data.dao.OperationDao;
//import com.epeisong.data.dao.OperationDao.OperationObserver;
//import com.epeisong.data.dao.util.CRUD;
//import com.epeisong.data.model.Operation;
//import com.epeisong.data.model.Operation.OperationType;
//import com.epeisong.model.FreightOfContacts;
//import com.epeisong.ui.activity.BlackBoardActivity;
//import com.epeisong.ui.activity.FreightOfContactsActivity;
//import com.epeisong.ui.activity.ManageInfoActivity;
//import com.epeisong.ui.activity.NearbyMarketActivity;
//import com.epeisong.ui.activity.SearchFreightActivity;
//import com.epeisong.utils.DimensionUtls;
//import com.epeisong.utils.SystemUtils;
//import com.epeisong.utils.ToastUtils;
//
///**
// * 工作台
// * 
// * @author poet
// * 
// */
//public class TaskFragment_old extends NetBaseFragment implements
//		OnItemClickListener, OperationObserver, OnClickListener,
//		FreightOfContactsObserver {
//
//	private static final int OPERATION_COUNT = 10;
//
//	private ListView mListView;
//	private MyAdapter mAdapter;
//
//	@Override
//	protected View onChildCreateView(LayoutInflater inflater,
//			ViewGroup container, Bundle savedInstanceState) {
//		mListView = new ListView(getActivity());
//		mListView.setDivider(null);
//		mListView.setCacheColorHint(Color.TRANSPARENT);
//		mListView.setOnItemClickListener(this);
//		mListView.setBackgroundColor(Color.argb(0xED, 0xED, 0xED, 0xED));
//		return mListView;
//	}
//
//	@Override
//	public void onViewCreated(View view, Bundle savedInstanceState) {
//		super.onViewCreated(view, savedInstanceState);
//		View headTop = new View(getActivity());
//		int h = (int) DimensionUtls.getPixelFromDp(15);
//		headTop.setBackgroundColor(Color.argb(0xED, 0xED, 0xED, 0xED));
//		headTop.setLayoutParams(new AbsListView.LayoutParams(-1, h));
//		FrameLayout fl = new FrameLayout(getActivity());
//		fl.addView(headTop);
//		fl.setBackgroundColor(Color.TRANSPARENT);
//		mListView.addHeaderView(fl);
//		View head = SystemUtils.inflate(R.layout.fragment_task_head);
//		mListView.addHeaderView(head);
//		head.findViewById(R.id.iv_logo).setOnClickListener(this);
//
//		AsyncTask<Void, Void, List<Operation>> task = new AsyncTask<Void, Void, List<Operation>>() {
//			@Override
//			protected List<Operation> doInBackground(Void... params) {
//				return OperationDao.getInstance().queryAll();
//			}
//
//			@Override
//			protected void onPostExecute(List<Operation> result) {
//				result = handleResult(result);
//				mAdapter = new MyAdapter();
//				mListView.setAdapter(mAdapter);
//				mAdapter.replaceAll(result);
//				onRequestComplete(true);
//			}
//		};
//		task.execute();
//
//		OperationDao.getInstance().addObserver(this);
//		FreightOfContactsDao.getInstance().addObserver(this);
//	}
//
//	@Override
//	public void onDestroyView() {
//		super.onDestroyView();
//		OperationDao.getInstance().removeObserver(this);
//		FreightOfContactsDao.getInstance().removeObserver(this);
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.iv_logo:
//
//			break;
//		}
//	}
//
//	@Override
//	public void onFreightOfContactsChange(Freight f, CRUD crud) {
//		if (crud == CRUD.CREATE) {
////			mFreightOfContacts = foc;
//			mAdapter.notifyDataSetChanged();
//		}
//		if (foc.getStatus() == FreightOfContacts.STATUS_READED) {
//			mFreightOfContacts = null;
//			mAdapter.notifyDataSetChanged();
//		}
//		if (getActivity() instanceof PointObserver) {
//			PointObserver ob = (PointObserver) getActivity();
//			ob.onPointChange(PointObserver.who_task, mFreightOfContacts != null);
//		}
//	}
//
//	@Override
//	public void onOperationChange() {
//		AsyncTask<Void, Void, List<Operation>> task = new AsyncTask<Void, Void, List<Operation>>() {
//			@Override
//			protected List<Operation> doInBackground(Void... params) {
//				return OperationDao.getInstance().queryAll();
//			}
//
//			@Override
//			protected void onPostExecute(List<Operation> result) {
//				result = handleResult(result);
//				mAdapter.replaceAll(result);
//			}
//		};
//		task.execute();
//	}
//
//	@Override
//	protected void onFailViewClick() {
//	}
//
//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position,
//			long id) {
//		position -= mListView.getHeaderViewsCount();
//		if (position == -1) {
//			ToastUtils.showToast("head");
//			return;
//		} else if (position < 0) {
//			return;
//		}
//		Class<? extends Activity> clazz = null;
//		Operation opera = mAdapter.getItem(position);
//		switch (opera.getOperationType()) {
//		case OperationType.BLACKBORAD:
//			clazz = BlackBoardActivity.class;
//			break;
//		case OperationType.SEARCH_FREIGHT:
//			clazz = SearchFreightActivity.class;
//			break;
//		case OperationType.CONTACTS_FREIGHT:
//			clazz = FreightOfContactsActivity.class;
//			if (mFreightOfContacts != null) {
//				mFreightOfContacts = null;
//				mAdapter.notifyDataSetChanged();
//				if (getActivity() instanceof PointObserver) {
//					PointObserver ob = (PointObserver) getActivity();
//					ob.onPointChange(PointObserver.who_task, false);
//				}
//			}
//			break;
//		case OperationType.NEARBY:
//			clazz = NearbyMarketActivity.class;
//			break;
//		case OperationType.MANAGE:
//			clazz = ManageInfoActivity.class;
//			break;
//		case -1:
//			if (mAdapter.getCount() >= OPERATION_COUNT) {
//				ToastUtils.showToast("已超过最大限制，请先移除不需要的项目");
//				return;
//			}
//			Operation op = new Operation();
//			op.setTagId("new");
//			op.setName("新功能");
//			op.setSerial(10);
//			op.setOperationType(-2);
//			OperationDao.getInstance().insert(op);
//			break;
//		default:
//			break;
//		}
//		if (clazz != null) {
//			Intent i = new Intent(getActivity(), clazz);
//			getActivity().startActivity(i);
//		}
//	}
//
//	private class MyAdapter extends HoldDataBaseAdapter<Operation> {
//
//		@Override
//		public int getViewTypeCount() {
//			return 3;
//		}
//
//		@Override
//		public int getItemViewType(int position) {
//			Operation op = getItem(position);
//			if (op.getOperationType() != OperationType.INVALID) {
//				return 0;
//			}
//			if (TextUtils.isEmpty(op.getName())) {
//				return -1;
//			}
//			return 1;
//		}
//
//		@Override
//		public boolean isEnabled(int position) {
//			Operation op = getItem(position);
//			return op.getOperationType() != OperationType.INVALID;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			Operation last = null;
//			if (position > 0) {
//				last = getItem(position - 1);
//			}
//			Operation op = getItem(position);
//			if (op.getOperationType() == OperationType.INVALID) {
//				if (TextUtils.isEmpty(op.getName())) {
//					View v = new View(getActivity());
//					int h = (int) DimensionUtls.getPixelFromDp(20);
//					v.setLayoutParams(new AbsListView.LayoutParams(-1, h));
//					v.setBackgroundColor(Color.argb(0xED, 0xED, 0xED, 0xED));
//					return v;
//				} else {
//					TextView tv = new TextView(getActivity());
//					tv.setLayoutParams(new AbsListView.LayoutParams(-1, -2));
//					tv.setText(op.getName());
//					tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
//					tv.setTextColor(Color.argb(0xFF, 0x73, 0x7A, 0x85));
//					tv.setBackgroundColor(Color.argb(0xED, 0xED, 0xED, 0xED));
//					int left = (int) DimensionUtls.getPixelFromDp(10);
//					tv.setPadding(left, left, left, left);
//					return tv;
//				}
//			}
//			ViewHolder holder = null;
//			if (convertView == null) {
//				convertView = SystemUtils.inflate(R.layout.fragment_task_item);
//				holder = new ViewHolder();
//				holder.findView(convertView);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}
//			boolean hideLine = last != null
//					&& last.getOperationType() == OperationType.INVALID;
//			holder.fillData(!hideLine, op);
//			return convertView;
//		}
//
//	}
//
//	class ViewHolder {
//		ImageView iv_icon;
//		TextView tv_name;
//		View line;
//
//		View point;
//		ImageView iv_logo;
//
//		public void findView(View v) {
//			iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
//			tv_name = (TextView) v.findViewById(R.id.tv_name);
//			line = v.findViewById(R.id.line);
//			point = v.findViewById(R.id.iv_point);
//			iv_logo = (ImageView) v.findViewById(R.id.iv_freight_contacts_logo);
//		}
//
//		public void fillData(boolean showLine, Operation opera) {
//			point.setVisibility(View.GONE);
//			iv_logo.setVisibility(View.GONE);
//			if (showLine) {
//				line.setVisibility(View.VISIBLE);
//			} else {
//				line.setVisibility(View.GONE);
//			}
//			// TODO icon
//			int type = opera.getOperationType();
//			int iconId = 0;
//			switch (type) {
//			case OperationType.BLACKBORAD:
//				iconId = R.drawable.task_blackboard;
//				break;
//			case OperationType.SEARCH_FREIGHT:
//				iconId = R.drawable.task_search_freight;
//				break;
//			case OperationType.CONTACTS_FREIGHT:
//				iconId = R.drawable.task_contacts_freight;
//				if (mFreightOfContacts != null) {
//					point.setVisibility(View.VISIBLE);
//					iv_logo.setVisibility(View.VISIBLE);
//				}
//				break;
//			case OperationType.NEARBY:
//				iconId = R.drawable.task_nearby;
//				break;
//			case OperationType.DEAL:
//				iconId = R.drawable.task_deal;
//				break;
//			case OperationType.MORE:
//				iconId = R.drawable.task_more;
//				break;
//			default:
//				iconId = R.drawable.ic_launcher;
//				break;
//			}
//			if (iconId > 0) {
//				iv_icon.setImageResource(iconId);
//			}
//			tv_name.setText(opera.getName());
//		}
//	}
//
//	private List<Operation> handleResult(List<Operation> data) {
//		List<Operation> result = new ArrayList<Operation>();
//		for (int i = 0; i < data.size(); i++) {
//			if (i == 0) {
//				Operation top = new Operation();
//				top.setOperationType(OperationType.INVALID);
//				Operation op0 = data.get(i);
//				if (!TextUtils.isEmpty(op0.getTagId())) {
//					top.setName(op0.getTagId());
//				}
//				result.add(top);
//				result.add(op0);
//			} else {
//				Operation last = data.get(i - 1);
//				Operation cur = data.get(i);
//				if (TextUtils.isEmpty(cur.getTagId())
//						|| !cur.getTagId().equals(last.getTagId())) {
//					Operation extra = new Operation();
//					extra.setOperationType(OperationType.INVALID);
//					if (!TextUtils.isEmpty(cur.getTagId())) {
//						extra.setName(cur.getTagId());
//					}
//					result.add(extra);
//				}
//				result.add(cur);
//			}
//		}
//		return result;
//	}
//}
