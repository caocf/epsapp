package com.epeisong.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import lib.pulltorefresh.PullToRefreshListView;
import lib.universal_image_loader.ImageLoaderUtils;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.SlipButton;
import com.epeisong.base.view.SlipButton.SlipButtonChangeListener;
import com.epeisong.data.dao.PointDao.PointObserver;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.dao.UserDao.UserObserver;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetSearchUserList;
import com.epeisong.data.net.NetUserUpdateStatus;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder;
import com.epeisong.model.MarketMember;
import com.epeisong.model.Point;
import com.epeisong.model.Point.PointCode;
import com.epeisong.model.User;
import com.epeisong.model.UserRole;
import com.epeisong.ui.activity.ContactsDetailActivity;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 会员管理页面(右侧)
 * @author gnn
 *
 */
public class MembersScreenFragment extends Fragment implements OnClickListener,
		OnItemClickListener, UserObserver, PointObserver,
		SlipButtonChangeListener, OnRefreshListener2<ListView> {

	private EditText etSearch;
	private View line;
	private MarketMember mMarket;
	private User mUser;
	private Button btn_search;
	private List<MarketMember> userList;
	private Context mContext;
	// private LinearLayout ll_manager;
	// private LinearLayout ll_add;
	private PullToRefreshListView mPullToRefreshListView;
	// private ExpandableListView mExpandableListView;
	private ListView mListView;
	private MyAdapter mAdapter;
	private boolean mLoaded;
	private TextView mTextViewEmpty;
	private ReceiveBroadCast receiveBroadCast;
	private MarketMember freshMember;
	private User freshUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		userList = new ArrayList<MarketMember>();
		View root = SystemUtils.inflate(R.layout.fragment_members);
		etSearch = (EditText) root.findViewById(R.id.et_search);
		root.findViewById(R.id.ll_item).setVisibility(View.GONE);
		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(final Editable edit) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(edit.toString())) {
					mAdapter.removeAllItem(userList);
					userList.clear();
					requestData(10, null);
				}
			}
		});
		btn_search = (Button) root.findViewById(R.id.btn_search);
		btn_search.setOnClickListener(this);
		line = root.findViewById(R.id.view_line);
		line.setVisibility(View.VISIBLE);
		mPullToRefreshListView = (PullToRefreshListView) root
				.findViewById(R.id.elv);
		mPullToRefreshListView.setOnRefreshListener(this);
		mListView = mPullToRefreshListView.getRefreshableView();
		mPullToRefreshListView.setAdapter(mAdapter = new MyAdapter());
		mPullToRefreshListView.setMode(Mode.PULL_FROM_END);
		mPullToRefreshListView.setOnItemClickListener(this);
		mUser = UserDao.getInstance().getUser();
//		requestData(10, null);
		setEmptyView();
		UserDao.getInstance().addObserver(this);
		
		//删除会员
		receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.epeisong.ui.activity.romeveLish"); // 只有持有相同的action的接受者才能接收此广播
        this.getActivity().registerReceiver(receiveBroadCast, filter);
        
        //增加会员
        filter.addAction("com.epeisong.ui.activity.refreshMember");
        this.getActivity().registerReceiver(receiveBroadCast, filter);

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
        	freshMember = (MarketMember) intent.getSerializableExtra("romeveLish"); // 删除用户
        	freshUser = (User) intent.getSerializableExtra("refreshMember"); // 添加用户
            if(freshMember != null){
//	            mAdapter.removeItem(freshMember);
//	            mAdapter.notifyDataSetChanged();
            	List<MarketMember> mlist = mAdapter.getAllItem();
            	for(MarketMember m :mlist){
            		if(m.getId().equals(freshMember.getId())){
            			mAdapter.removeItem(m);
            		}
            	}
            	mAdapter.notifyDataSetChanged();
            }
            if(freshUser != null){
            	onPullUpToRefresh(mPullToRefreshListView);
            	mAdapter.notifyDataSetChanged();
            }
        }
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
	
	private void setEmptyView() {
        LinearLayout emptyLayout = new LinearLayout(getActivity());
        emptyLayout.setOrientation(LinearLayout.VERTICAL);
        emptyLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        emptyLayout.setPadding(0, DimensionUtls.getPixelFromDpInt(100), 0, 0);
        ImageView iv = new ImageView(getActivity());
        iv.setImageResource(R.drawable.nopeihuo);
        emptyLayout.addView(iv);
        mTextViewEmpty = new TextView(getActivity());
        mTextViewEmpty.setText("没有会员");
        mTextViewEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mTextViewEmpty.setTextColor(Color.argb(0xff, 0xaa, 0xaa, 0xaa));
        mTextViewEmpty.setGravity(Gravity.CENTER);
        emptyLayout.addView(mTextViewEmpty);
        mPullToRefreshListView.setEmptyView(emptyLayout);
    }

	@Override
	public void onPointChange(Point p) {
		boolean show = p.isShow();
		PointCode pointCode = PointCode.convertFromValue(p.getCode());
		switch (pointCode) {
		case Code_Contacts_Fans:
			// mHeadAdapter.getItem(2).setShowPoint(show);
			// mHeadAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		List<MarketMember> slist;
		switch (v.getId()) {
		case R.id.btn_search:
			mContext = getActivity();
			// etSearch.setFocusable(true);
			// etSearch.setFocusableInTouchMode(true);
			// etSearch.requestFocus();
			if (TextUtils.isEmpty(etSearch.getText().toString())) {
				ToastUtils.showToast("请输入搜索姓名");
				return;
			}
			slist = new ArrayList<MarketMember>();
			if (userList != null) {
				for (MarketMember member : userList) {
					if (member.getUser().getShow_name()
							.contains(etSearch.getText().toString())) {
						slist.add(member);
					}
				}
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(mContext.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				if (slist.size() > 0) {
					mAdapter.replaceAll(slist);
				} else {
					ToastUtils.showToast("没有您要搜索的会员");
				}

			}
			break;
		default:
//			Object tag = v.getTag();
//			if (tag != null && tag instanceof Contacts) {
//				Contacts c = (Contacts) tag;
//				if (!TextUtils.isEmpty(c.getLogo_url())) {
//					ArrayList<String> urls = new ArrayList<String>();
//					urls.add(c.getLogo_url());
//					ShowImagesActivity.launch(getActivity(), urls, 0);
//				}
//			}
			break;
		}
	}


	@Override
	public void onUserChange(User user) {
		// TODO Auto-generated method stub
//		requestData(10, null);
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
	public void OnChanged(boolean CheckState, SlipButton btn) {
		Object tag = btn.getTag();
		if (tag != null && tag instanceof MarketMember) {
			changeState((MarketMember) tag, btn);
		}
	}

	private void changeState(final MarketMember member, final SlipButton btn) {
		final int curStatus = member.getIsBanned();
		((XBaseActivity) getActivity()).showPendingDialog(null);
		AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>(){

			@Override
			protected Boolean doInBackground(Void... params) {
				NetUserUpdateStatus net = new NetUserUpdateStatus() {
					
					@Override
					protected boolean onSetRequest(Builder req) {
						req.setLogisticId(Integer.parseInt(member.getUser().getId()));
						if (curStatus == User.STATUS_SHIELDING) {
							req.setNewStatus(User.STATUS_NOSHIELDING);
							req.setIsBanned(User.STATUS_NOSHIELDING);
						} else {
							req.setNewStatus(User.STATUS_SHIELDING);
							req.setIsBanned(User.STATUS_SHIELDING);
						}
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
				((XBaseActivity)getActivity()).dismissPendingDialog();
				if(result){
//					 ToastUtils.showToast("修改成功");
					if (curStatus == User.STATUS_SHIELDING) {
						member.setIsBanned(User.STATUS_NOSHIELDING);
						// user.setIs_hide(User.STATUS_NOSHIELDING);
					} else {
						member.setIsBanned(User.STATUS_SHIELDING);
						// user.setIs_hide(User.STATUS_SHIELDING);
					}
					mAdapter.notifyDataSetChanged();
					// UserDao.getInstance().
					// ToastUtils.showToast("修改成功" +user.getIs_hide());
					// mAdapter.notifyDataSetChanged();
				}
			}
		};
		task.execute();

	}

	private void requestData(final int size, final String edgeId) {
		mUser = UserDao.getInstance().getUser();
		AsyncTask<Void, Void, List<MarketMember>> task = new AsyncTask<Void, Void, List<MarketMember>>() {
			@Override
			protected List<MarketMember> doInBackground(Void... params) {
				NetSearchUserList net = new NetSearchUserList() {
					@Override
					protected int getCommandCode() {
						return CommandConstants.MANAGE_MEMBERS_REQ;
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
						// List<LogisticAndRMarketMember> logi =
						// resp.getLogisticAndRMarketMemberList();
						return UserParser.parseMember(resp);
					}
				} catch (NetGetException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<MarketMember> result) {
				mPullToRefreshListView.onRefreshComplete();
				if (result != null) {
					if (result.size() > 0) {
						userList.addAll(result);
						mAdapter.addAll(result);
					} else {
						ToastUtils.showToast("没有更多会员");
					}
				}
			}
		};
		task.execute();
	}

	private class MyAdapter extends HoldDataBaseAdapter<MarketMember> {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils
						.inflate(R.layout.activity_choose_members_item);
				holder = new ViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			MarketMember m = getItem(position);
			holder.fillData(m);

			return convertView;
		}
	}

	private class ViewHolder {
		ImageView iv_head;
		TextView tv_user_name;
		TextView tv_address;
		TextView tv_members_sign;
		public SlipButton cb_switch;
		View view;

		public void fillData(MarketMember member) {
			cb_switch.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(member.getUser().getLogo_url())) {
				ImageLoader.getInstance().displayImage(
						member.getUser().getLogo_url(), iv_head,
						ImageLoaderUtils.getListOptionsForUserLogo());
			} else {
				int defaultIcon = User.getDefaultIcon(member.getUser()
						.getUser_type_code(), true);
				iv_head.setImageResource(defaultIcon);
			}
			if (member.getIsBanned() == User.STATUS_SHIELDING) {
				cb_switch.setDefaultOpen(false);
			} else {
				cb_switch.setDefaultOpen(true);
			}
			iv_head.setTag(member);
			tv_user_name.setText(member.getUser().getShow_name());
			// tv_user_name.setText(users.getIs_hide()+"");
			UserRole userRole = member.getUser().getUserRole();
			if (userRole != null) {
				tv_address.setText(userRole.getRegionName());// .getAddress());
			}

			cb_switch.setTag(member);
		}

		public void findView(View v) {
			iv_head = (ImageView) v.findViewById(R.id.iv_contacts_logo);
			tv_user_name = (TextView) v.findViewById(R.id.tv_contacts_name);
			tv_address = (TextView) v.findViewById(R.id.tv_address);
			tv_members_sign = (TextView) v.findViewById(R.id.tv_members_sign);
			view = v.findViewById(R.id.child_view);
			cb_switch = (SlipButton) v.findViewById(R.id.iv_switch);
			cb_switch.SetOnChangedListener(MembersScreenFragment.this);
			iv_head.setOnClickListener(MembersScreenFragment.this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		position -= mPullToRefreshListView.getRefreshableView()
				.getHeaderViewsCount();
		MarketMember m = mAdapter.getItem(position);
		Intent intent = new Intent(getActivity(), ContactsDetailActivity.class);
		intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, m.getUser()
				.getId());
		// User u = contacts.convertToUser();
		intent.putExtra(ContactsDetailActivity.EXTRA_SHOW_PAGE_COUNT_OTHER, 3);
		intent.putExtra(ContactsDetailActivity.EXTRA_USER, m.getUser());
		intent.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, m.getUser()
				.getUser_type_code());
		startActivity(intent);

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

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		requestData(10, null);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		String edgeId = null;
		if (!mAdapter.isEmpty()) {
			edgeId = mAdapter.getItem(mAdapter.getCount() - 1).getId();
		}
		requestData(10, edgeId);
	}

}
