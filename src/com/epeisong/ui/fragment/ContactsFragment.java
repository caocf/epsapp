package com.epeisong.ui.fragment;

import java.util.ArrayList;
import java.util.List;
 

import lib.universal_image_loader.ImageLoaderUtils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.ShowImagesActivity;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.fragment.NetBaseFragment;
import com.epeisong.base.view.AdjustHeightListView;
import com.epeisong.base.view.IndexBar;
import com.epeisong.base.view.IndexBar.OnChooseIndexListener;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.ContactsDao.ContactsObserver;
import com.epeisong.data.dao.PointDao;
import com.epeisong.data.dao.PointDao.PointObserver;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.model.Contacts;
import com.epeisong.model.Point;
import com.epeisong.model.Point.PointCode;
import com.epeisong.model.User;
import com.epeisong.model.UserRole;
import com.epeisong.service.notify.MenuBean;
import com.epeisong.service.notify.MenuEnum;
import com.epeisong.service.notify.NotifyService;
import com.epeisong.ui.activity.BlacklistActivity;
import com.epeisong.ui.activity.ChooseNewContactsActivity;
import com.epeisong.ui.activity.ChooseSearchContactsActivity;
import com.epeisong.ui.activity.ContactsDetailActivity;
import com.epeisong.ui.activity.EditTagActivity;
import com.epeisong.ui.activity.FansActivity;
import com.epeisong.ui.activity.PhoneContactsActivity;
import com.epeisong.ui.activity.SearchContactsActivity;
import com.epeisong.utils.ContactsUtils;
import com.epeisong.utils.ContactsUtils.OnContactsUtilsListener;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.ShapeUtils;
import com.epeisong.utils.ShapeUtils.ShapeParams;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.android.AsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zxing.CaptureActivity;

/**
 * 联系人
 * 
 * @author poet
 * 
 */
@SuppressWarnings("deprecation")
public class ContactsFragment extends NetBaseFragment implements OnChildClickListener, OnClickListener,
        OnItemClickListener, ContactsObserver, OnItemLongClickListener, PointObserver, OnChooseIndexListener {

    private String[] pinyin = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#" };
    private String last;
    private PopupWindow mPopupWindow;
    private TextView mPinyinTv;
    private View mHeadView;
    private TextView mFootViewTv;
    private EditText etSearch;

    private HeadAdapter mHeadAdapter;

    private ExpandableListView mExpandableListView;
    private MyAdapter mAdapter;
    private ArrayList<Contacts> mContactsSelectedList;
    
    
  

    protected View onChildCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = SystemUtils.inflate(R.layout.fragment_contacts);
        etSearch = (EditText) root.findViewById(R.id.et_search);
        etSearch.setOnClickListener(this);
        mExpandableListView = (ExpandableListView) root.findViewById(R.id.elv);
        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setOnChildClickListener(this);
        mExpandableListView.setOnItemLongClickListener(this);
        IndexBar bar = (IndexBar) root.findViewById(R.id.bar);
        bar.setOnChooseIndexListener(this);
        List<String> list = IndexBar.getA_Z();
        list.add(0, "↑");
        bar.setIndexValues(list);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoadingView();
        requestData();
        ContactsDao.getInstance().addObserver(this);
     
    }

    @Override
    protected View onCreateLoadingView(LayoutInflater inflater) {
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setGravity(Gravity.CENTER);
        ProgressBar bar = new ProgressBar(getActivity());
        ll.addView(bar);
        return ll;
    }
  
     
    public void refreshPoint(int  show) {
    	if(NotifyService.isShow == show){
    		 mHeadAdapter.getItem(2).setShowPoint(true);
    	} else {
    		mHeadAdapter.getItem(2).setShowPoint(false);
    	}
    	
         mHeadAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void onPointChange(Point p) {
        boolean show = p.isShow();
        PointCode pointCode = PointCode.convertFromValue(p.getCode());
        switch (pointCode) {
        case Code_Contacts_Fans:
            mHeadAdapter.getItem(2).setShowPoint(show);
            mHeadAdapter.notifyDataSetChanged();
            break;
        default:
            break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.et_search:
            Intent intent = new Intent(getActivity(), ChooseSearchContactsActivity.class);
            intent.putExtra("contact", "contactpage");
            intent.putExtra(ChooseNewContactsActivity.EXTRA_ORIGINAL_CONTACTS_LIST, mContactsSelectedList);
            startActivity(intent);
            break;
        default:
            Object tag = v.getTag();
            if (tag != null && tag instanceof Contacts) {
                Contacts c = (Contacts) tag;
                if (!TextUtils.isEmpty(c.getLogo_url())) {
                    ArrayList<String> urls = new ArrayList<String>();
                    urls.add(c.getLogo_url());
                    ShowImagesActivity.launch(getActivity(), urls, 0);
                }
            }
            break;
        }
    }

    @Override
    protected void onFailViewClick() {
        requestData();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Contacts contacts = mAdapter.getChild(groupPosition, childPosition);
        Intent intent = new Intent(getActivity(), ContactsDetailActivity.class);
        intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, contacts.getId());
        User u = contacts.convertToUser();
        intent.putExtra(ContactsDetailActivity.EXTRA_SHOW_PAGE_COUNT_OTHER, 3);
        intent.putExtra(ContactsDetailActivity.EXTRA_USER, u);
        intent.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, u.getUser_type_code());
        startActivity(intent);
        return true;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Object tag1 = view.getTag(R.id.tag_contacts_group);
        if (tag1 != null && tag1 instanceof Integer) {
            int groupPos = (Integer) tag1;
            Object tag2 = view.getTag(R.id.tag_contacts_child);
            if (tag2 != null && tag2 instanceof Integer) {
                int childPos = (Integer) tag2;
                final Contacts c = mAdapter.getChild(groupPos, childPos);
                if (c != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(c.getShow_name());
                    final String[] items = { "删除", "加入黑名单", "添加标签(备注)" };
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                deleteContacts(c);
                            } else if (which == 1) {
                                addBlackList(c);
                            } else if (which == 2) {
                                EditTagActivity.launch(getActivity(), c);
                            }
                        }
                    });
                    builder.create().show();
                }
                return true;
            }
        }
        return false;
    }

    // 删除联系人
    private void deleteContacts(final Contacts c) {
        final XBaseActivity a = (XBaseActivity) getActivity();
        a.showPendingDialog(null);
        ContactsUtils.delete(c.getId(), new OnContactsUtilsListener() {
            @Override
            public void onContactsUtilsComplete(int option, boolean success) {
                a.dismissPendingDialog();
            }
        });
    }

    // 加入黑名单
    private void addBlackList(final Contacts c) {
        final XBaseActivity a = (XBaseActivity) getActivity();
        a.showPendingDialog(null);
        ContactsUtils.black(c.getId(), new OnContactsUtilsListener() {
            @Override
            public void onContactsUtilsComplete(int option, boolean success) {
                a.dismissPendingDialog();
            }
        });
    }

    @Override
    public void onChoosedIndex(String indexValue) {
        if (indexValue == null) {
            if (mPopupWindow != null) {
                mPopupWindow.dismiss();
                mPopupWindow = null;
            }
        } else {
            if (indexValue.equals("↑")) {
                mExpandableListView.setSelection(0);
            } else {
                int index = mAdapter.getGroupIndex(indexValue);
                if (index != -1) {
                    mExpandableListView.setSelectedGroup(index);
                }
            }
            if (mPopupWindow == null) {
                View view = SystemUtils.inflate(R.layout.popup_window_pinyin);
                view.setBackgroundDrawable(ShapeUtils.getShape(new ShapeParams()
                        .setBgColor(Color.argb(0x88, 0x00, 0x00, 0x00)).setCorner(DimensionUtls.getPixelFromDp(5))
                        .setStrokeWidth(0)));
                mPinyinTv = (TextView) view.findViewById(R.id.tv_pinyin);
                int w = (int) DimensionUtls.getPixelFromDp(100);
                mPopupWindow = new PopupWindow(view, w, w);
                mPopupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            }
            mPinyinTv.setText(indexValue);
        }
    }

    @Deprecated
    public boolean onTouch_old(View v, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
            ((ImageView) v).setBackgroundResource(R.drawable.pinyin_pressed);

            int i = (int) (event.getY() / v.getHeight() * pinyin.length);
            if (i < 0 || i > pinyin.length - 1) {
                if (i < 0) {
                    i = 0;
                } else if (i > pinyin.length - 1) {
                    i = pinyin.length - 1;
                }
            }
            if (pinyin[i].equals(last)) {
                return false;
            }
            last = pinyin[i];
            int index = mAdapter.getGroupIndex(last);
            if (index != -1) {
                mExpandableListView.setSelectedGroup(index);
            }
            if (mPopupWindow == null) {
                View view = SystemUtils.inflate(R.layout.popup_window_pinyin);
                mPinyinTv = (TextView) view.findViewById(R.id.tv_pinyin);
                int w = (int) DimensionUtls.getPixelFromDp(80);
                mPopupWindow = new PopupWindow(view, w, w);
                mPopupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            }
            mPinyinTv.setText(last);
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_OUTSIDE:
        case MotionEvent.ACTION_UP:
            ((ImageView) v).setBackgroundResource(R.drawable.pinyin_normal);
            if (mPopupWindow != null) {
                mPopupWindow.dismiss();
                mPopupWindow = null;
            }
            break;
        }
        return false;
    }

    @Override
    public void onContactsChange() {
        requestData();
    }

    private synchronized void requestData() {
        if (getActivity() == null) {
            return;
        }
        AsyncTask<Void, Void, List<Contacts>> task = new AsyncTask<Void, Void, List<Contacts>>() {
            @Override
            protected List<Contacts> doInBackground(Void... params) {
                SystemClock.sleep(100);
                return ContactsDao.getInstance().queryAll(Contacts.STATUS_NORNAL);
            }

            @Override
            protected void onPostExecute(List<Contacts> result) {
                handleData(result);
            }
        };
        task.execute();
    }

    private synchronized void handleData(List<Contacts> result) {
        if (getActivity() == null) {
            return;
        }
        int count = result == null ? 0 : result.size();
        if (mExpandableListView.getHeaderViewsCount() == 0) {
            addHeadView();
        }
        if (mExpandableListView.getFooterViewsCount() == 0) {
            addFootView(count);
        } else if (mFootViewTv != null) {
            mFootViewTv.setText(count + "位联系人");
        }

        List<String> titles = new ArrayList<String>();
        List<List<Contacts>> lists = new ArrayList<List<Contacts>>();

        ContactsDao.groupByPinyin(result, titles, lists);

        if (mAdapter == null) {
            mAdapter = new MyAdapter(titles, lists);
            mExpandableListView.setAdapter(mAdapter);
            mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                    return true;
                }
            });
            onRequestComplete(true);
        } else {
            mAdapter.refershData(titles, lists);
        }
        for (int i = 0; i < mAdapter.getGroupCount(); i++) {
            mExpandableListView.expandGroup(i);
        }
    }

    private class MyAdapter extends BaseExpandableListAdapter {

        private List<String> titles;
        private List<List<Contacts>> contactss;

        public MyAdapter(List<String> titles, List<List<Contacts>> contactss) {
            this.titles = titles;
            this.contactss = contactss;
        }

        public void refershData(List<String> titles, List<List<Contacts>> contactss) {
            this.titles.clear();
            this.contactss.clear();
            this.notifyDataSetChanged();
            this.titles = titles;
            this.contactss = contactss;
            this.notifyDataSetChanged();
        }

        public int getGroupIndex(String title) {
            return titles.indexOf(title);
        }

        @Override
        public int getGroupCount() {
            return titles.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return contactss.get(groupPosition).size();
        }

        @Override
        public String getGroup(int groupPosition) {
            return titles.get(groupPosition);
        }

        @Override
        public Contacts getChild(int groupPosition, int childPosition) {
            return contactss.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.item_contacts_group);
                holder = new GroupViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }
            holder.fillData(getGroup(groupPosition));
            convertView.setClickable(false);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                ViewGroup parent) {
            ChildViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.activity_choose_contacts_item);
                holder = new ChildViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }
            boolean showLine = childPosition != contactss.get(groupPosition).size() - 1;
            holder.fillData(getChild(groupPosition, childPosition), showLine);
            convertView.setTag(R.id.tag_contacts_group, groupPosition);
            convertView.setTag(R.id.tag_contacts_child, childPosition);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    class GroupViewHolder {
        TextView tv_title;

        public void findView(View v) {
            tv_title = (TextView) v.findViewById(R.id.tv_title);
        }

        public void fillData(String title) {
            tv_title.setText(title);
        }
    }

    class ChildViewHolder {
        ImageView iv_head;
        TextView tv_user_name;
        TextView tv_phone;
        TextView tv_address;
        TextView tv_user_type;
        CheckBox cb;
        View view;

        public void fillData(Contacts contacts, boolean showLine) {
            if (!TextUtils.isEmpty(contacts.getLogo_url())) {
                ImageLoader.getInstance().displayImage(contacts.getLogo_url(), iv_head,
                        ImageLoaderUtils.getListOptionsForUserLogo());
            } else {
                int defaultIcon = User.getDefaultIcon(contacts.getLogistic_type_code(), true);
                iv_head.setImageResource(defaultIcon);
            }
            iv_head.setTag(contacts);
            tv_user_name.setText(contacts.getShow_name());
            if (!TextUtils.isEmpty(contacts.getContacts_phone()))
                tv_phone.setText(contacts.getContacts_phone());
            else {
                tv_phone.setText(contacts.getContacts_telephone());
            }
            UserRole userRole = contacts.getUserRole();
            if (userRole != null) {
                tv_address.setText(userRole.getRegionName());// .getAddress());
            }
            // User user =
            // UserDao.getInstance().queryByPhone(contacts.getContacts_phone());
            tv_user_type.setText(contacts.getLogistic_type_name());
            if (showLine) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }

        public void findView(View v) {
            iv_head = (ImageView) v.findViewById(R.id.iv_contacts_logo);
            tv_user_name = (TextView) v.findViewById(R.id.tv_contacts_name);
            tv_phone = (TextView) v.findViewById(R.id.tv_contacts_phone);
            tv_address = (TextView) v.findViewById(R.id.tv_contacts_address);
            cb = (CheckBox) v.findViewById(R.id.cb_contacts);
            cb.setVisibility(View.GONE);
            tv_user_type = (TextView) v.findViewById(R.id.tv_contacts_type);
            view = v.findViewById(R.id.child_view);
            iv_head.setOnClickListener(ContactsFragment.this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
        case 0:
            Intent phoneContacts = new Intent(getActivity(), PhoneContactsActivity.class);
            startActivity(phoneContacts);
            break;
        case 1:
            Intent blacklist = new Intent(getActivity(), BlacklistActivity.class);
            startActivity(blacklist);
            break;
        case 2:
        	NotifyService.showNoUiByFans(CommandConstants.ADD_CONTACT_SERVER_PUSH_REQ );
            Intent fansIntent = new Intent(getActivity(), FansActivity.class);
            startActivity(fansIntent);
            break;
        case 3:
        	
            Intent intent3 = new Intent(getActivity(), SearchContactsActivity.class);
            startActivity(intent3);
            break;
        case 4:
            CaptureActivity.launchDefaultResult(this);
            break;
        default:
            // ToastUtils.showToast(position+"");
            break;
        }
    }

    private void addHeadView() {
        boolean showFansPoint = PointDao.getInstance().query(PointCode.Code_Contacts_Fans).isShow();
        List<HeadItem> items = new ArrayList<HeadItem>();
        items.add(new HeadItem(R.drawable.icon_contacts_phone_contacts, "手机卡上的通讯录"));
        items.add(new HeadItem(R.drawable.icon_contacts_blacklist, "黑名单"));
        items.add(new HeadItem(R.drawable.icon_contacts_myfans, "关注我的人").setShowPoint(showFansPoint));
        items.add(new HeadItem(R.drawable.icon_contacts_add_contacts, "添加联系人"));
        items.add(new HeadItem(R.drawable.icon_contacts_scan, "扫一扫"));
        mHeadAdapter = new HeadAdapter();
        mHeadAdapter.replaceAll(items);
        AdjustHeightListView lv = new AdjustHeightListView(EpsApplication.getInstance());
        lv.setDivider(null);
        lv.setOnItemClickListener(this);
        lv.setAdapter(mHeadAdapter);
        mHeadView = lv;
        mExpandableListView.addHeaderView(mHeadView);

       // PointDao.getInstance().addObserver(PointCode.Code_Contacts_Fans, this);
        initDbByUi();
    }

    private void addFootView(int count) {
        mFootViewTv = new TextView(EpsApplication.getInstance());
        mFootViewTv.setText(count + "位联系人");
        mFootViewTv.setTextColor(getResources().getColor(R.color.light_gray));
        int padding = (int) DimensionUtls.getPixelFromDp(10);
        mFootViewTv.setPadding(0, padding, 0, padding);
        mFootViewTv.setGravity(Gravity.CENTER);
        mExpandableListView.addFooterView(mFootViewTv);
    }

    private class HeadAdapter extends HoldDataBaseAdapter<HeadItem> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HeadViewHolder holder;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.item_contacts_head);
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
    
  //初始化时判断是否需要更新UI
  	public void initDbByUi() {
  		String filter  = "Where menuCode='" +MenuEnum.ContList.getMenuCode()+"'";
  		 MenuBean  bean =  NotifyService.getMenuBean(filter);
  			if(bean.getIsShow() == NotifyService.isShow) {
  				 mHeadAdapter.getItem(2).setShowPoint(true);
                 mHeadAdapter.notifyDataSetChanged();
  		}
  		
  	}
}
