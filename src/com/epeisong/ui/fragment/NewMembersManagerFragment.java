//package com.epeisong.ui.fragment;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import lib.pulltorefresh.PullToRefreshExpandableListView;
//import lib.universal_image_loader.ImageLoaderUtils;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AbsListView.OnScrollListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.AdapterView.OnItemLongClickListener;
//import android.widget.BaseExpandableListAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ExpandableListView;
//import android.widget.ExpandableListView.OnChildClickListener;
//import android.widget.ExpandableListView.OnGroupClickListener;
//import android.widget.AbsListView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.epeisong.EpsApplication;
//import com.epeisong.R;
//import com.epeisong.R.color;
//import com.epeisong.base.activity.ShowImagesActivity;
//import com.epeisong.base.activity.XBaseActivity;
//import com.epeisong.base.adapter.HoldDataBaseAdapter;
//import com.epeisong.base.fragment.NetBaseFragment;
//import com.epeisong.base.view.AdjustHeightListView;
//import com.epeisong.base.view.IndexBar;
//import com.epeisong.base.view.IndexBar.OnChooseIndexListener;
//import com.epeisong.data.dao.PointDao;
//import com.epeisong.data.dao.PointDao.PointObserver;
//import com.epeisong.data.dao.UserDao;
//import com.epeisong.data.dao.UserDao.UserObserver;
//import com.epeisong.data.exception.NetGetException;
//import com.epeisong.data.net.NetSearchUserList;
//import com.epeisong.data.net.parser.UserParser;
//import com.epeisong.logistics.common.CommandConstants;
//import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
//import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder;
//import com.epeisong.model.Contacts;
//import com.epeisong.model.Point;
//import com.epeisong.model.Point.PointCode;
//import com.epeisong.model.User;
//import com.epeisong.model.UserRole;
//import com.epeisong.net.request.NetDeleteMembers;
//import com.epeisong.net.request.OnNetRequestListenerImpl;
//import com.epeisong.ui.activity.ContactsDetailActivity;
//import com.epeisong.ui.activity.SearchContactsActivity;
//import com.epeisong.utils.DimensionUtls;
//import com.epeisong.utils.SystemUtils;
//import com.epeisong.utils.ToastUtils;
//import com.epeisong.utils.android.AsyncTask;
//import com.nostra13.universalimageloader.core.ImageLoader;
//
///**
// * 会员管理页面
// * @author gnn
// *
// */
//public class NewMembersManagerFragment extends NetBaseFragment implements OnChildClickListener, OnClickListener,
//OnItemClickListener, UserObserver, OnItemLongClickListener, PointObserver, OnChooseIndexListener {
//
//    private String[] pinyin = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
//            "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#" };
//    private String last;
//    private PopupWindow mPopupWindow;
//    private TextView mPinyinTv;
//    private View mHeadView;
//    private TextView mFootViewTv;
//    private EditText etSearch;
////    private TextView tv_add;
//    private User mMarket;
//    private Button btn_search;
//    private List<User> userList;
////    private View listline;
//
//    private HeadAdapter mHeadAdapter;
//    private PullToRefreshExpandableListView mPullToRefreshExpandableListView;
//    private ExpandableListView mExpandableListView;
//    private MyAdapter mAdapter;
//
//    protected View onChildCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View root = SystemUtils.inflate(R.layout.fragment_members);
//        etSearch = (EditText) root.findViewById(R.id.et_search);
//        btn_search = (Button) root.findViewById(R.id.btn_search);
////        tv_add = (TextView) root.findViewById(R.id.tv_add);
////        tv_add.setVisibility(View.VISIBLE);
////        listline = root.findViewById(R.id.view_listline);
////        listline.setVisibility(View.VISIBLE);
//        etSearch.addTextChangedListener(new TextWatcher() {
//			
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void afterTextChanged(final Editable edit) {
//				if(TextUtils.isEmpty(edit.toString())){
//					requestData(10, null);
//				}
//				
//			}
//		});
////        ll_manager = (LinearLayout) root.findViewById(R.id.ll_member_manager);
////        ll_add = (LinearLayout) root.findViewById(R.id.ll_member_add);
////        ll_manager.setOnClickListener(this);
////        ll_add.setOnClickListener(this);
//        btn_search.setOnClickListener(this);
//        mPullToRefreshExpandableListView = (PullToRefreshExpandableListView) root.findViewById(R.id.elv);
//        mExpandableListView = mPullToRefreshExpandableListView.getRefreshableView();
//        mExpandableListView.setGroupIndicator(null);
//        mExpandableListView.setOnChildClickListener(this);
//        mExpandableListView.setOnItemLongClickListener(this);
//        mExpandableListView.setOnScrollListener(new OnScrollListener() {
//			
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				if (view.getLastVisiblePosition() == (view.getCount() - 1)){
////					ToastUtils.showToast("滚动到底部了");
//					
//				}
//				
//			}
//			
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//        IndexBar bar = (IndexBar) root.findViewById(R.id.bar);
//        bar.setOnChooseIndexListener(this); 
//        List<String> list = IndexBar.getA_Z();
//        list.add(0, "↑");
//        bar.setIndexValues(list);
//        return root;
//    }
//
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        showLoadingView();
//        requestData(10,null);
//        UserDao.getInstance().addObserver(this);
//    }
//
//
//    @Override
//    protected View onCreateLoadingView(LayoutInflater inflater) {
//        LinearLayout ll = new LinearLayout(getActivity());
//        ll.setGravity(Gravity.CENTER);
//        ProgressBar bar = new ProgressBar(getActivity());
//        ll.addView(bar);
//        return ll;
//    }
//
//    @Override
//    public void onPointChange(Point p) {
//        boolean show = p.isShow();
//        PointCode pointCode = PointCode.convertFromValue(p.getCode());
//        switch (pointCode) {
//        case Code_Contacts_Fans:
//            mHeadAdapter.getItem(2).setShowPoint(show);
//            mHeadAdapter.notifyDataSetChanged();
//            break;
//        default:
//            break;
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//    	List<User> slist = new ArrayList<User>();
//        switch (v.getId()) {
//        case R.id.btn_search:
//        	Context mContext = getActivity();
//        	if(TextUtils.isEmpty(etSearch.getText().toString())){
//        		ToastUtils.showToast("请输入搜索姓名");
//        		return;
//        	}
//        	if(userList != null){
//        		for(User user : userList){
//        			if(user.getShow_name().contains(etSearch.getText().toString())){
//        				slist.add(user);
//        			}
//        		}
//        		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(mContext.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//        		if(slist.size() > 0){
//        			handleData(slist);
//        		}else{
//        			ToastUtils.showToast("没有您要搜索的会员");
//        		}
//        		
//        	}
//        	break;
//        default:
//            Object tag = v.getTag();
//            if (tag != null && tag instanceof Contacts) {
//                Contacts c = (Contacts) tag;
//                if (!TextUtils.isEmpty(c.getLogo_url())) {
//                    ArrayList<String> urls = new ArrayList<String>();
//                    urls.add(c.getLogo_url());
//                    ShowImagesActivity.launch(getActivity(), urls, 0);
//                }
//            }
//            break;
//        }
//    }
//
//    @Override
//    protected void onFailViewClick() {
//        requestData(10,null);
//    }
//
//    @Override
//    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//    	User u = mAdapter.getChild(groupPosition, childPosition);
//        Intent intent = new Intent(getActivity(), ContactsDetailActivity.class);
//        intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, u.getId());
////        User u = contacts.convertToUser();
//        intent.putExtra(ContactsDetailActivity.EXTRA_SHOW_PAGE_COUNT_OTHER, 3);
//        intent.putExtra(ContactsDetailActivity.EXTRA_USER, u);
//        intent.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, u.getUser_type_code());
//        startActivity(intent);
//        return true;
//    }
//
//    @Override
//    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        Object tag1 = view.getTag(R.id.tag_contacts_group);
//        TextView tv_name;
//        TextView tv_delete;
//        TextView tv_sign;
//        if (tag1 != null && tag1 instanceof Integer) {
//            int groupPos = (Integer) tag1;
//            Object tag2 = view.getTag(R.id.tag_contacts_child);
//            if (tag2 != null && tag2 instanceof Integer) {
//                int childPos = (Integer) tag2;
//                final User u = mAdapter.getChild(groupPos, childPos);
//                if (u != null) {
//                	final AlertDialog builder = new AlertDialog.Builder(getActivity()).create();  
//                	builder.show(); 
//                	Window window = builder.getWindow();
//                	window.setContentView(R.layout.members_dialog);
////                	final Dialog dialog = new Dialog(getActivity(), R.style.selectorDialog); 
////                	final Dialog dialog = new Dialog(getActivity()); 
////                    View view1 = SystemUtils.inflate(R.layout.members_dialog);
//                    tv_name = (TextView) window.findViewById(R.id.tv_name);
//                    tv_delete = (TextView) window.findViewById(R.id.tv_delete);
//                    tv_sign = (TextView) window.findViewById(R.id.tv_sign);
////                    dialog.setContentView(view1);
////                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    
////                    dialog.setView(view1);
//                    builder.setCanceledOnTouchOutside(true);
//                    tv_name.setText(u.getShow_name());
//                    tv_delete.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							NetDeleteMembers net = new NetDeleteMembers((XBaseActivity)getActivity(), u.getId());
//							net.request(new OnNetRequestListenerImpl<CommonLogisticsResp.Builder>() {
//								@Override
//								public void onSuccess(CommonLogisticsResp.Builder response) {
//									ToastUtils.showToast("删除成功");
//									requestData(10, null);
//								}
//							});
//							builder.dismiss();
//						}
//					});
//                    tv_sign.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
////							EditTagActivity.launch(getActivity(), u);
//						}
//					});
////                    dialog.show();
//                }
//                return true;
//            }
//        }
//        return false;
//    }
//
//    // 删除联系人
////    private void deleteContacts(Contacts c) {
////        ContactsDao.getInstance().delete(c);
////    }
//
//    @Override
//    public void onChoosedIndex(String indexValue) {
//        if (indexValue == null) {
//            if (mPopupWindow != null) {
//                mPopupWindow.dismiss();
//                mPopupWindow = null;
//            }
//        } else {
//            if (indexValue.equals("↑")) {
//                mExpandableListView.setSelection(0);
//            } else {
//                int index = mAdapter.getGroupIndex(indexValue);
//                if (index != -1) {
//                    mExpandableListView.setSelectedGroup(index);
//                }
//            }
//            if (mPopupWindow == null) {
//                View view = SystemUtils.inflate(R.layout.popup_window_pinyin);
//                mPinyinTv = (TextView) view.findViewById(R.id.tv_pinyin);
//                int w = (int) DimensionUtls.getPixelFromDp(80);
//                mPopupWindow = new PopupWindow(view, w, w);
//                mPopupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
//            }
//            mPinyinTv.setText(indexValue);
//        }
//    }
//
//    @Deprecated
//    public boolean onTouch_old(View v, MotionEvent event) {
//        switch (event.getAction()) {
//        case MotionEvent.ACTION_DOWN:
//        case MotionEvent.ACTION_MOVE:
//            ((ImageView) v).setBackgroundResource(R.drawable.pinyin_pressed);
//
//            int i = (int) (event.getY() / v.getHeight() * pinyin.length);
//            if (i < 0 || i > pinyin.length - 1) {
//                if (i < 0) {
//                    i = 0;
//                } else if (i > pinyin.length - 1) {
//                    i = pinyin.length - 1;
//                }
//            }
//            if (pinyin[i].equals(last)) {
//                return false;
//            }
//            last = pinyin[i];
//            int index = mAdapter.getGroupIndex(last);
//            if (index != -1) {
//                mExpandableListView.setSelectedGroup(index);
//            }
//            if (mPopupWindow == null) {
//                View view = SystemUtils.inflate(R.layout.popup_window_pinyin);
//                mPinyinTv = (TextView) view.findViewById(R.id.tv_pinyin);
//                int w = (int) DimensionUtls.getPixelFromDp(80);
//                mPopupWindow = new PopupWindow(view, w, w);
//                mPopupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
//            }
//            mPinyinTv.setText(last);
//            break;
//        case MotionEvent.ACTION_CANCEL:
//        case MotionEvent.ACTION_OUTSIDE:
//        case MotionEvent.ACTION_UP:
//            ((ImageView) v).setBackgroundResource(R.drawable.pinyin_normal);
//            if (mPopupWindow != null) {
//                mPopupWindow.dismiss();
//                mPopupWindow = null;
//            }
//            break;
//        }
//        return false;
//    }
//    
//    @Override
//	public void onUserChange(User user) {
//		// TODO Auto-generated method stub
//		requestData(10, null);
//	}
//
//    private void requestData(final int size, final String edgeId) {
//        mMarket = UserDao.getInstance().getUser();
//        AsyncTask<Void, Void, List<User>> task = new AsyncTask<Void, Void, List<User>>() {
//            @Override
//            protected List<User> doInBackground(Void... params) {
//                NetSearchUserList net = new NetSearchUserList() {
//                    @Override
//                    protected int getCommandCode() {
//                        return CommandConstants.MANAGE_MEMBERS_REQ;
//                    }
//
//                    @Override
//                    protected boolean onSetRequest(Builder req) {
//                        req.setMarketId(Integer.parseInt(mMarket.getId()));
////                    	req.setLogisticId(Integer.parseInt(mMarket.getId()));
//                        req.setLimitCount(size);
////                        req.setIsManageMembers(true);
//                        if (edgeId != null) {
//                            req.setId(Integer.parseInt(edgeId));
//                        }
//                        return true;
//                    }
//                };
//                try {
//                    CommonLogisticsResp.Builder resp = net.request();
//                    if (net.isSuccess(resp)) {
//                        return UserParser.parseMember(resp);
//                    }
//                } catch (NetGetException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(List<User> result) {
////                mPullToRefreshExpandableListView.onRefreshComplete();
//            	userList = new ArrayList<User>();
//                if (result != null) {
//                	userList.addAll(result);
//                	handleData(result);
//                	
//                }else{
//                	onRequestComplete(false);
//                }
//            }
//        };
//        task.execute();
//    }
//
//    private synchronized void handleData(List<User> result) {
//        if (getActivity() == null) {
//            return;
//        }
//        int count = result == null ? 0 : result.size();
//        if (mExpandableListView.getHeaderViewsCount() == 0) {
//            addHeadView();
//        }
//        if (mExpandableListView.getFooterViewsCount() == 0) {
//            addFootView(count);
//        } else if (mFootViewTv != null) {
//            mFootViewTv.setText(count + "位会员");
//        }
//
//        List<String> titles = new ArrayList<String>();
//        List<List<User>> lists = new ArrayList<List<User>>();
//        
////        ContactsDao.groupByPinyin(result, titles, lists);
//        UserDao.groupByPinyin(result, titles, lists);
//
//        if (mAdapter == null) {
//            mAdapter = new MyAdapter(titles, lists);
//            mExpandableListView.setAdapter(mAdapter);
//            mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {
//
//                @Override
//                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//
//                    return true;
//                }
//            });
//            onRequestComplete(true);
//        } else {
//            mAdapter.refershData(titles, lists);
//        }
//        for (int i = 0; i < mAdapter.getGroupCount(); i++) {
//            mExpandableListView.expandGroup(i);
//        }
//    }
//
//    private class MyAdapter extends BaseExpandableListAdapter {
//
//        private List<String> titles;
//        private List<List<User>> stationList;
//        
//        public MyAdapter(List<String> titles, List<List<User>> stationList) {
//            this.titles = titles;
//            this.stationList = stationList;
//        }
//        
//        public void refershData(List<String> titles, List<List<User>> stationList) {
//            this.titles.clear();
//            this.stationList.clear();
//            this.notifyDataSetChanged();
//            this.titles = titles;
//            this.stationList = stationList;
//            this.notifyDataSetChanged();
//        }
//
//        public int getGroupIndex(String title) {
//            return titles.indexOf(title);
//        }
//
//        @Override
//        public int getGroupCount() {
//            return titles.size();
//        }
//
//        @Override
//        public int getChildrenCount(int groupPosition) {
//            return stationList.get(groupPosition).size();
//        }
//
//        @Override
//        public String getGroup(int groupPosition) {
//            return titles.get(groupPosition);
//        }
//
//        @Override
//        public User getChild(int groupPosition, int childPosition) {
//            return stationList.get(groupPosition).get(childPosition);
//        }
//
//        @Override
//        public long getGroupId(int groupPosition) {
//            return 0;
//        }
//
//        @Override
//        public long getChildId(int groupPosition, int childPosition) {
//            return 0;
//        }
//
//        @Override
//        public boolean hasStableIds() {
//            return false;
//        }
//
//        @Override
//        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//            GroupViewHolder holder = null;
//            if (convertView == null) {
//                convertView = SystemUtils.inflate(R.layout.item_members_group);
//                holder = new GroupViewHolder();
//                holder.findView(convertView);
//                convertView.setTag(holder);
//            } else {
//                holder = (GroupViewHolder) convertView.getTag();
//            }
//            holder.fillData(getGroup(groupPosition));
//            convertView.setClickable(false);
//            return convertView;
//        }
//
//        @Override
//        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
//                ViewGroup parent) {
//            ChildViewHolder holder = null;
//            if (convertView == null) {
//                convertView = SystemUtils.inflate(R.layout.activity_choose_members_item);
//                holder = new ChildViewHolder();
//                holder.findView(convertView);
//                convertView.setTag(holder);
//            } else {
//                holder = (ChildViewHolder) convertView.getTag();
//            }
//            boolean showLine = childPosition != stationList.get(groupPosition).size() - 1;
//            holder.fillData(getChild(groupPosition, childPosition), showLine);
//            convertView.setTag(R.id.tag_contacts_group, groupPosition);
//            convertView.setTag(R.id.tag_contacts_child, childPosition);
//            return convertView;
//        }
//
//        @Override
//        public boolean isChildSelectable(int groupPosition, int childPosition) {
//            return true;
//        }
//
//    }
//
//    class GroupViewHolder {
//        TextView tv_title;
//
//        public void findView(View v) {
//            tv_title = (TextView) v.findViewById(R.id.tv_title);
//        }
//
//        public void fillData(String title) {
//            tv_title.setText(title);
//        }
//    }
//
//    class ChildViewHolder {
//        ImageView iv_head;
//        TextView tv_user_name;
//        TextView tv_address;
//        TextView tv_members_sign;
//        View view;
//
//        public void fillData(User users, boolean showLine) {
//            if (!TextUtils.isEmpty(users.getLogo_url())) {
//                ImageLoader.getInstance().displayImage(users.getLogo_url(), iv_head,
//                        ImageLoaderUtils.getListOptionsForUserLogo());
//            } else {
//                int defaultIcon = User.getDefaultIcon(users.getUser_type_code(), true);
//                iv_head.setImageResource(defaultIcon);
//            }
//            iv_head.setTag(users);
//            tv_user_name.setText(users.getShow_name());
//            UserRole userRole = users.getUserRole();
//            if (userRole != null) {
//                tv_address.setText(userRole.getRegionName());// .getAddress());
//            }
//            
//            if (showLine) {
//                view.setVisibility(View.VISIBLE);
//            } else {
//                view.setVisibility(View.GONE);
//            }
//        }
//
//        public void findView(View v) {
//            iv_head = (ImageView) v.findViewById(R.id.iv_contacts_logo);
//            tv_user_name = (TextView) v.findViewById(R.id.tv_contacts_name);
//            tv_address = (TextView) v.findViewById(R.id.tv_address);
//            tv_members_sign = (TextView) v.findViewById(R.id.tv_members_sign);
//            view = v.findViewById(R.id.child_view);
//            iv_head.setOnClickListener(NewMembersManagerFragment.this);
//        }
//    }
//    
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//    	position = position -1;
//        switch (position) {
//
//        case 0:
//        	Intent search = new Intent(getActivity(), SearchContactsActivity.class);
//        	search.putExtra("members", "members");
////            startActivity(search);
//            startActivityForResult(search, 12);
//            break;
//        case 1:
//        	ToastUtils.showToast("扫描二维码");
//        	break;
//        default:
//            // ToastUtils.showToast(position+"");
//            break;
//        }
//    }
//    
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
////    	ToastUtils.showToast("到这");
////    	requestData(10, null);
//    	
//    	if (requestCode == 12) {
//            requestData(10, null);
//        }
//    	super.onActivityResult(requestCode, resultCode, data);
//    }
//    
//    private void addHeadView() {
////        boolean showFansPoint = PointDao.getInstance().query(PointCode.Code_Contacts_Fans).isShow();
//        List<HeadItem> items = new ArrayList<HeadItem>();
//        
//        items.add(new HeadItem(R.drawable.white_bg, "手机号添加"));
//        items.add(new HeadItem(R.drawable.white_bg, "二维码添加"));
//        mHeadAdapter = new HeadAdapter();
//        mHeadAdapter.replaceAll(items);
//        AdjustHeightListView lv = new AdjustHeightListView(EpsApplication.getInstance());
////        ListView lv = new ListView(EpsApplication.getInstance());
//        if(lv.getHeaderViewsCount() == 0){
//        	LinearLayout ll = new LinearLayout(getActivity());
//        	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);  
//        	lp.setMargins(35, 10, 0, 3);
//        	TextView tv = new TextView(getActivity());
//        	tv.setText("添加会员");
//        	tv.setTextSize(17);
//        	tv.setTextColor(Color.parseColor("#7f7f7f"));
//        	tv.setLayoutParams(lp);
//        	ll.addView(tv);
//        	
//        	lv.addHeaderView(ll);
//        }
//        lv.setDivider(null);
//        lv.setOnItemClickListener(this);
//        lv.setAdapter(mHeadAdapter);
//        
//        mHeadView = lv;
//        mExpandableListView.addHeaderView(mHeadView);
//
//        PointDao.getInstance().addObserver(PointCode.Code_Contacts_Fans, this);
//    }
//
//    private void addFootView(int count) {
////        mFootViewTv = new TextView(EpsApplication.getInstance());
////        mFootViewTv.setText(count + "位联系人");
////        mFootViewTv.setTextColor(getResources().getColor(R.color.light_gray));
////        int padding = (int) DimensionUtls.getPixelFromDp(10);
////        mFootViewTv.setPadding(0, padding, 0, padding);
////        mFootViewTv.setGravity(Gravity.CENTER);
////        mExpandableListView.addFooterView(mFootViewTv);
//    }
//
//    private class HeadAdapter extends HoldDataBaseAdapter<HeadItem> {
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            HeadViewHolder holder;
//            if (convertView == null) {
//                convertView = SystemUtils.inflate(R.layout.item_members_head);
//                holder = new HeadViewHolder();
//                holder.findView(convertView);
//                convertView.setTag(holder);
//            } else {
//                holder = (HeadViewHolder) convertView.getTag();
//            }
//            holder.fillData(getItem(position));
//            return convertView;
//        }
//    }
//
//    private class HeadViewHolder {
//        ImageView iv_icon;
//        TextView tv_name;
//        View point;
//
//        public void findView(View v) {
//            iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
//            tv_name = (TextView) v.findViewById(R.id.tv_name);
//            point = v.findViewById(R.id.iv_point);
//            iv_icon.setVisibility(View.INVISIBLE);
//        }
//
//        public void fillData(HeadItem item) {
//            iv_icon.setImageResource(item.iconResId);
//            tv_name.setText(item.name);
//            if (item.showPoint) {
//                point.setVisibility(View.VISIBLE);
//            } else {
//                point.setVisibility(View.GONE);
//            }
//        }
//    }
//
//    private class HeadItem {
//        int iconResId;
//        String name;
//        boolean showPoint;
//
//        public HeadItem(int iconResId, String name) {
//            super();
//            this.iconResId = iconResId;
//            this.name = name;
//        }
//
//        public HeadItem setShowPoint(boolean show) {
//            showPoint = show;
//            return this;
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//    }
//
//}
