package com.epeisong.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import lib.pulltorefresh.PullToRefreshListView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.AdjustHeightListView;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.dao.UserDao.UserObserver;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetAddMembers;
import com.epeisong.data.net.NetAddRegionToMarket;
import com.epeisong.data.net.NetSearchUserList;
import com.epeisong.data.net.parser.RegionAreaParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder;
import com.epeisong.model.RegionArea;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.ui.activity.ChooseRegionActivity;
import com.epeisong.ui.activity.SearchContactsActivity;
import com.epeisong.ui.activity.SearchUserDetailActivity;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.zxing.CaptureActivity;

/**
 * 信息区域管理页面(配货市场左侧)
 * 
 * @author gnn
 * 
 */
public class InfoAreaManageFragment extends Fragment  implements
OnChildClickListener, OnClickListener, OnItemClickListener,
UserObserver, OnItemLongClickListener, OnRefreshListener2<ListView>{
	private static final int REQUEST_CODE_CHOOSE_REGION = 100;
	private int addRegionCode;
	private String resultContent;
	private EditText etSearch;
	private View line;
	private User mUser;
	private Button btn_search;
	private ImageView iv_search;
	private List<RegionArea> userList;
	private Context mContext;
	private PullToRefreshListView mPullToRefreshListView;
	private ListView mListView;
	private MyAdapter mAdapter;
	private HeadAdapter mHeadAdapter;
	private View mHeadView;
	private boolean mLoaded;
	private TextView mTextViewEmpty;

	// public static final String EXTRA_RESULT = "result";
	private String mResult;
	private ReceiveBroadCast receiveBroadCast;
	private User freshUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		userList = new ArrayList<RegionArea>();
		View root = SystemUtils.inflate(R.layout.fragment_members);
		etSearch = (EditText) root.findViewById(R.id.et_search);
		etSearch.setHint("点击添加");
		etSearch.setFocusable(false);
		etSearch.setOnClickListener(this);
		root.findViewById(R.id.tv_add_phone).setOnClickListener(this);
		root.findViewById(R.id.tv_add_code).setOnClickListener(this);
		iv_search = (ImageView) root.findViewById(R.id.iv_search);
		iv_search.setVisibility(View.GONE);
		btn_search = (Button) root.findViewById(R.id.btn_search);
		btn_search.setText("添加");
		btn_search.setOnClickListener(this);
		line = root.findViewById(R.id.view_line);
		line.setVisibility(View.VISIBLE);
		mPullToRefreshListView = (PullToRefreshListView) root
				.findViewById(R.id.elv);
		mPullToRefreshListView.setOnRefreshListener(this);
		mListView = mPullToRefreshListView.getRefreshableView();
		// addHeadView();
		mPullToRefreshListView.setAdapter(mAdapter = new MyAdapter());
		mPullToRefreshListView.setMode(Mode.PULL_FROM_END);
		mPullToRefreshListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		mUser = UserDao.getInstance().getUser();
		setEmptyView();
		receiveBroadCast = new ReceiveBroadCast();
		IntentFilter filter = new IntentFilter();
		// 增加会员
		filter.addAction("com.epeisong.ui.activity.refreshMember");
		this.getActivity().registerReceiver(receiveBroadCast, filter);

		UserDao.getInstance().addObserver(this);
		return root;
	}

	@Override
	public void onAttach(Activity activity) {
		/** 注册广播 */
		receiveBroadCast = new ReceiveBroadCast();
		IntentFilter filter = new IntentFilter();
		this.getActivity().registerReceiver(receiveBroadCast, filter);
		super.onAttach(activity);
	}

	class ReceiveBroadCast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
//			freshUser = (User) intent.getSerializableExtra("refreshMember"); // 添加用户
//			if (freshUser != null) {
//				onPullUpToRefresh(mPullToRefreshListView);
//				mAdapter.notifyDataSetChanged();
//			}
			onPullUpToRefresh(mPullToRefreshListView);
			mAdapter.notifyDataSetChanged();
		}
	}

	private void setEmptyView() {
		LinearLayout emptyLayout = new LinearLayout(getActivity());
		emptyLayout.setOrientation(LinearLayout.VERTICAL);
		emptyLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		emptyLayout.setPadding(0, DimensionUtls.getPixelFromDpInt(100), 0, 0);
		ImageView iv = new ImageView(getActivity());
		iv.setImageResource(R.drawable.nopeihuo);
		emptyLayout.addView(iv);
		mTextViewEmpty = new TextView(getActivity());
		mTextViewEmpty.setText("没有地址");
		mTextViewEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		mTextViewEmpty.setTextColor(Color.argb(0xff, 0xaa, 0xaa, 0xaa));
		mTextViewEmpty.setGravity(Gravity.CENTER);
		emptyLayout.addView(mTextViewEmpty);
		mPullToRefreshListView.setEmptyView(emptyLayout);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.et_search:
			ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_0_3, REQUEST_CODE_CHOOSE_REGION);
			break;
		case R.id.btn_search:
			mContext = getActivity();
			if (TextUtils.isEmpty(etSearch.getText().toString())) {
				ToastUtils.showToast("请输入添加地址");
				return;
			}
			//List<RegionArea> regionArea = mAdapter.getAllItem();
//			for(RegionArea area: userList){
//				if(area.getRegionCode() == addRegionCode){
//					ToastUtils.showToast("该地址已存在");
//					return;
//				}
//			}
			((XBaseActivity)getActivity()).showPendingDialog(null);
			AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>(){

				@Override
				protected Boolean doInBackground(Void... params) {
					NetAddRegionToMarket net = new NetAddRegionToMarket() {
						
						@Override
						protected boolean onSetRequest(Builder req) {
							req.setRegionCode(addRegionCode);
							req.setRegionName(etSearch.getText().toString());
							return true;
						}
					};
					try {
						CommonLogisticsResp.Builder resp = net.request();
						List<RegionArea> list = RegionAreaParser.parseList(resp);
						ToastUtils.showToast(list.size()+"");
						if (resp != null && "SUCC".equals(resp.getResult())){
							return true;
						}else{
							resultContent = resp.getDesc();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}
				@Override
				protected void onPostExecute(Boolean result) {
					((XBaseActivity)getActivity()).dismissPendingDialog();
					if(result){
						onPullUpToRefresh(mPullToRefreshListView);
						etSearch.setText("");
						ToastUtils.showToast("添加成功");
					}else{
						ToastUtils.showToast(resultContent);
					}
				}
			};
			task.execute();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		position -= mPullToRefreshListView.getRefreshableView()
				.getHeaderViewsCount();
		TextView tv_name;
		TextView tv_delete;
		TextView tv_sign;
		View line;
		final RegionArea area = mAdapter.getItem(position);
		
		if (area != null) {
			final AlertDialog builder = new AlertDialog.Builder(getActivity())
					.create();
			builder.show();
			Window window = builder.getWindow();
			window.setContentView(R.layout.members_dialog);
			tv_name = (TextView) window.findViewById(R.id.tv_name);
			tv_delete = (TextView) window.findViewById(R.id.tv_delete);
			tv_sign = (TextView) window.findViewById(R.id.tv_sign);
			line = window.findViewById(R.id.line);
			builder.setCanceledOnTouchOutside(true);
			tv_name.setText(area.getRegionName());
			tv_sign.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
			tv_delete.setText("删除");
			tv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					deleteRegionArea(area);
					builder.dismiss();
				}
			});
			
			return true;
		}

		return false;
	}

	@Override
	public void onUserChange(User user) {
		// TODO Auto-generated method stub
		// requestData(10, null);
	}

	private void requestData(final int size, final String edgeId) {
		mUser = UserDao.getInstance().getUser();
		AsyncTask<Void, Void, List<RegionArea>> task = new AsyncTask<Void, Void, List<RegionArea>>() {
			@Override
			protected List<RegionArea> doInBackground(Void... params) {
				NetSearchUserList net = new NetSearchUserList() {
					@Override
					protected int getCommandCode() {
						return CommandConstants.LIST_REGIONS_MARKET_CONTAINED_REQ;
					}

					@Override
					protected boolean onSetRequest(Builder req) {
						req.setMarketId(Integer.parseInt(mUser.getId()));
						req.setLimitCount(size);
						// req.setLogisticId(Integer.parseInt(mMarket.getId()));
						// req.setIsManageMembers(true);
						if (edgeId != null) {
							req.setId(Integer.parseInt(edgeId));
						}
						return true;
					}
				};
				try {
					CommonLogisticsResp.Builder resp = net.request();
					if (net.isSuccess(resp)) {
						return RegionAreaParser.parseList(resp);
					}
				} catch (NetGetException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<RegionArea> result) {
				mPullToRefreshListView.onRefreshComplete();
				// setListShown(true);
				//ToastUtils.showToast(result.size() + "");
				if (result != null) {
					if (result.size() > 0) {
						userList.addAll(result);
						mAdapter.addAll(result);
					} else {
						ToastUtils.showToast("没有更多地址");
					}
				}
//				ToastUtils.showToast(userList.size()+"");
				// mPullToRefreshListView.onRefreshComplete();
			}
		};
		task.execute();
	}

	private class MyAdapter extends HoldDataBaseAdapter<RegionArea> {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils.inflate(R.layout.activity_manage_member_item);
				holder = new ViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			RegionArea r = getItem(position);
			
			holder.fillData(r);

			return convertView;
		}
	}

	private class ViewHolder {
		TextView tv_region_name;
		

		public void fillData(RegionArea region) {
			tv_region_name.setText(region.getRegionName());
		}

		public void findView(View v) {
			tv_region_name =  (TextView) v.findViewById(R.id.tv_region);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
//
//		position -= mPullToRefreshListView.getRefreshableView()
//				.getHeaderViewsCount();
//		MarketMember m = mAdapter.getItem(position);
//		Intent intent = new Intent(getActivity(), ContactsDetailActivity.class);
//		intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, m.getUser()
//				.getId());
//		// User u = contacts.convertToUser();
//		intent.putExtra(ContactsDetailActivity.EXTRA_SHOW_PAGE_COUNT_OTHER, 3);
//		intent.putExtra(ContactsDetailActivity.EXTRA_USER, m.getUser());
//		intent.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, m.getUser()
//				.getUser_type_code());
//		startActivity(intent);
//
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		requestData(10, null);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		String edgeId = null;
		if (!mAdapter.isEmpty()) {
			edgeId = mAdapter.getItem(mAdapter.getCount() - 1).getId()+"";
		}
		requestData(10, edgeId);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isAdded() && !mLoaded) {
			mPullToRefreshListView.setRefreshing();
			mLoaded = true;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			if (requestCode == REQUEST_CODE_CHOOSE_REGION) {
                RegionResult result = (RegionResult) data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
                addRegionCode = result.getFullCode();
//            	serveRegionCode = result.getCode();
            	etSearch.setText(result.getShortNameFromDistrict());//.getGeneralName());getShortNameFromDistrict
            } 
			if (requestCode == 12) {
				User user = (User) data
						.getSerializableExtra(SearchUserDetailActivity.EXTRA_USER);
				data.putExtra("mUser", user);
				// mAdapter.addItem(user);
				onPullUpToRefresh(mPullToRefreshListView);
				Intent intent = new Intent("com.epeisong.ui.activity.refreshMember");
				intent.putExtra("refreshMember", user);
				getActivity().sendBroadcast(intent); // 发送广播
			}
			if (requestCode == 22) {
				mResult = data.getStringExtra(CaptureActivity.EXTRA_OUT_RESULT);
				if (TextUtils.isEmpty(mResult)) {
					ToastUtils.showToast("无结果");
				} else if (mResult.startsWith("http://www.epeisong.com/addcontact")) {
					addMembers(mResult);
				} else {
					ToastUtils.showToast("扫描失败，请重新扫描");
				}
	
			}
		}
	}
	
	private void deleteRegionArea(final RegionArea area){
				
		AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>(){

			@Override
			protected Boolean doInBackground(Void... params) {
				NetSearchUserList net = new NetSearchUserList() {
					@Override
					protected int getCommandCode() {
						return CommandConstants.DELETE_REGION_FROM_MARKET_REQ;
					}
					
					@Override
					protected boolean onSetRequest(Builder req) {
//						req.setLogisticId(area.getId());
						req.setId(area.getId());
						return true;
					}
				};
				 try {
					 CommonLogisticsResp.Builder resp = net.request();
			            if (resp != null && "SUCC".equals(resp.getResult())) {
			                return true;
			            }
			        } catch (NetGetException e) {
			            e.printStackTrace();
			        }
				return false;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					ToastUtils.showToast("删除成功");
					mAdapter.removeItem(area);
				}
			}
		};
		task.execute();
	}

	private void addMembers(final String qrUrl) {
		AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				NetAddMembers net = new NetAddMembers() {
					@Override
					protected boolean onSetRequest(Builder req) {
						req.setQrCodeAddContactURL(mResult);
						return true;
					}
				};
				try {
					CommonLogisticsResp.Builder resp = net.request();
					if (net.isSuccess(resp)) {
						onPullUpToRefresh(mPullToRefreshListView);
						return true;
					} else {
						LogUtils.e("", resp.getDesc());
					}
					ToastUtils.showToastInThread(resp.getDesc());
				} catch (NetGetException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtils.showToastInThread("解析失败");
				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					ToastUtils.showToast("添加会员成功");
				}
			}
		};
		task.execute();
	}

	private void addHeadView() {
		List<HeadItem> items = new ArrayList<HeadItem>();

		items.add(new HeadItem(R.drawable.white_bg, "手机号添加"));
		items.add(new HeadItem(R.drawable.white_bg, "二维码添加"));
		mHeadAdapter = new HeadAdapter();
		mHeadAdapter.replaceAll(items);
		AdjustHeightListView lv = new AdjustHeightListView(
				EpsApplication.getInstance());
		// ListView lv = new ListView(EpsApplication.getInstance());
		if (lv.getHeaderViewsCount() == 0) {
			LinearLayout ll = new LinearLayout(getActivity());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
			lp.setMargins(35, 10, 0, 3);
			TextView tv = new TextView(getActivity());
			tv.setText("添加会员");
			tv.setTextSize(17);
			tv.setTextColor(Color.parseColor("#7f7f7f"));
			tv.setLayoutParams(lp);
			ll.addView(tv);

			lv.addHeaderView(ll);
		}
		lv.setDivider(null);
		lv.setOnItemClickListener(this);
		lv.setAdapter(mHeadAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				position = position - 1;
				switch (position) {

				case 0:
					Intent search = new Intent(getActivity(),
							SearchContactsActivity.class);
					search.putExtra("members", "members");
					// startActivity(search);
					startActivityForResult(search, 12);
					break;
				case 1:
					ToastUtils.showToast("扫描二维码");
					break;
				default:
					// ToastUtils.showToast(position+"");
					break;
				}
			}
		});
		mHeadView = lv;
		mListView.addHeaderView(mHeadView);

	}

	private void addFootView(int count) {
		// mFootViewTv = new TextView(EpsApplication.getInstance());
		// mFootViewTv.setText(count + "位联系人");
		// mFootViewTv.setTextColor(getResources().getColor(R.color.light_gray));
		// int padding = (int) DimensionUtls.getPixelFromDp(10);
		// mFootViewTv.setPadding(0, padding, 0, padding);
		// mFootViewTv.setGravity(Gravity.CENTER);
		// mExpandableListView.addFooterView(mFootViewTv);
	}

	private class HeadAdapter extends HoldDataBaseAdapter<HeadItem> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HeadViewHolder holder;
			if (convertView == null) {
				convertView = SystemUtils.inflate(R.layout.item_members_head);
				holder = new HeadViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (HeadViewHolder) convertView.getTag();
			}
			holder.fillData(getItem(position));
			return convertView;
		}
	}

	private class HeadViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		View point;

		public void findView(View v) {
			iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
			tv_name = (TextView) v.findViewById(R.id.tv_name);
			point = v.findViewById(R.id.iv_point);
			iv_icon.setVisibility(View.INVISIBLE);
		}

		public void fillData(HeadItem item) {
			iv_icon.setImageResource(item.iconResId);
			tv_name.setText(item.name);
			if (item.showPoint) {
				point.setVisibility(View.VISIBLE);
			} else {
				point.setVisibility(View.GONE);
			}
		}
	}

	private class HeadItem {
		int iconResId;
		String name;
		boolean showPoint;

		public HeadItem(int iconResId, String name) {
			super();
			this.iconResId = iconResId;
			this.name = name;
		}

		public HeadItem setShowPoint(boolean show) {
			showPoint = show;
			return this;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	/**
     * 注销广播
     * */
	@Override
	public void onDestroy() {
		if (receiveBroadCast != null) {
            this.getActivity().unregisterReceiver(receiveBroadCast);
        }
		super.onDestroy();
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
		return false;
	}

}
