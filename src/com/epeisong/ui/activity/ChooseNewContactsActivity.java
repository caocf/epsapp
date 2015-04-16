package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import lib.universal_image_loader.ImageLoaderUtils;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.IndexBar;
import com.epeisong.base.view.IndexBar.OnChooseIndexListener;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.model.Contacts;
import com.epeisong.model.User;
import com.epeisong.utils.SpUtilsCur;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.epeisong.utils.java.JavaUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 选择联系人
 * @author gnn
 *
 */
public class ChooseNewContactsActivity extends BaseActivity implements OnChildClickListener, OnClickListener,
        OnChooseIndexListener {

    private class MyAdapter extends BaseExpandableListAdapter {

        private List<String> titles;
        private List<List<Contacts>> contactss;

        public MyAdapter(List<String> titles, List<List<Contacts>> contactss) {
            this.titles = titles;
            this.contactss = contactss;
        }

        @Override
        public Contacts getChild(int groupPosition, int childPosition) {
            return contactss.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return contactss.get(groupPosition).size();
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
            holder.fillData(getChild(groupPosition, childPosition));
            return convertView;
        }

        @Override
        public String getGroup(int groupPosition) {
            return titles.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return titles.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        public int getGroupIndex(String title) {
            return titles.indexOf(title);
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.item_contacts_letter);
                holder = new GroupViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }
            holder.fillData(getGroup(groupPosition));
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    class ChildViewHolder {
        ImageView iv_head;
        TextView tv_user_name;
        TextView tv_phone;
        TextView tv_address;
        TextView tv_user_type;
        CheckBox cb;

        public void fillData(Contacts contacts) {
            // TODO 显示头像
            if (!TextUtils.isEmpty(contacts.getLogo_url())) {
                ImageLoader.getInstance().displayImage(contacts.getLogo_url(), iv_head,
                        ImageLoaderUtils.getListOptionsForUserLogo());
            } else {
                int defaultIcon = User.getDefaultIcon(contacts.getLogistic_type_code(), true);
                iv_head.setImageResource(defaultIcon);
            }
            tv_user_name.setText(contacts.getShow_name());
            if (!TextUtils.isEmpty(contacts.getContacts_phone()))
                tv_phone.setText(contacts.getContacts_phone());
            else {
                tv_phone.setText(contacts.getContacts_telephone());
            }
            tv_address.setText(contacts.getUserRole().getRegionName());
            // User user =
            // UserDao.getInstance().queryByPhone(contacts.getContacts_phone());
            tv_user_type.setText(contacts.getLogistic_type_name());
            cb.setBackgroundResource(R.drawable.checkbox_nomal);
            if (mContactsOriginalList != null && mContactsOriginalList.contains(contacts)) {
                // cb.setChecked(true);
                cb.setBackgroundResource(R.drawable.checkbox_icon);
                cb.setEnabled(false);

            } else {
                cb.setEnabled(true);
                if (mContactsSelectedList.contains(contacts)) {
                    cb.setBackgroundResource(R.drawable.checkbox_select_icon);
                    cb.setChecked(true);
                } else {
                    cb.setBackgroundResource(R.drawable.checkbox_nomal);
                    cb.setChecked(false);
                }
            }
        }

        public void findView(View v) {
            iv_head = (ImageView) v.findViewById(R.id.iv_contacts_logo);
            tv_user_name = (TextView) v.findViewById(R.id.tv_contacts_name);
            tv_phone = (TextView) v.findViewById(R.id.tv_contacts_phone);
            tv_address = (TextView) v.findViewById(R.id.tv_contacts_address);
            cb = (CheckBox) v.findViewById(R.id.cb_contacts);
            tv_user_type = (TextView) v.findViewById(R.id.tv_contacts_type);
        }
    }

    class GroupViewHolder {
        TextView tv_title;

        public void fillData(String title) {
            tv_title.setText(title);
        }

        public void findView(View v) {
            tv_title = (TextView) v.findViewById(R.id.tv_title);
        }
    }

    public static final String EXTRA_ORIGINAL_CONTACTS_LIST = "original_contacts_list";
    public static final String EXTRA_SELECTED_CONTACTS_LIST = "selected_contacts_list";
    public static final String EXTRA_FILTER_CONTACTS_IDS = "filter_contacts_ids";
    private TextView mTitleRightTv;
    // private HorizontalScrollView mHsv;
    // private LinearLayout mContactsContainerLl;

    private EditText mSearchEt;
    private ExpandableListView mExpandableListView;
    // private ExpandableListView mSearchListView;
    private MyAdapter mAdapter;
    private View mSearchNothingView;
    // private List<Contacts> mContactsList;
    // private RelativeLayout mLayout;

    private ArrayList<Contacts> mContactsOriginalList;
    private ArrayList<Contacts> mContactsSelectedList;
    private ArrayList<String> mContactsIdsFilter;
    private int selectedCount;
    private String userId;

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Contacts c = mAdapter.getChild(groupPosition, childPosition);
        if (!TextUtils.isEmpty(userId) && c.getId().equals(userId)) {
            ToastUtils.showToast("不能把该信息转发给原始发布者");
            return true;
        }
        if (mContactsOriginalList != null && mContactsOriginalList.contains(c)) {
            return true;
        }
        if (mContactsSelectedList.contains(c)) {
            mContactsSelectedList.remove(c);
        } else {
            mContactsSelectedList.add(c);
        }
        mAdapter.notifyDataSetChanged();
        // selectedCount = mContactsSelectedList.size();
        // if(mContactsOriginalList != null){
        // // ToastUtils.showToast(mContactsOriginalList.size()+"");
        // selectedCount = mContactsSelectedList.size() +
        // mContactsOriginalList.size();
        // }else{
        // selectedCount = mContactsSelectedList.size();
        // }
        selectedCount = mContactsSelectedList.size();
        mTitleRightTv.setText("确定(" + selectedCount + ")");
        if (selectedCount == 0) {
            mTitleRightTv.setClickable(false);
        } else {
            mTitleRightTv.setClickable(true);
        }
        return true;
    }

    private Action createTitleRight() {
        return new Action() {
            @Override
            public void doAction(View v) {
                if (mContactsSelectedList != null) {
                    if (mContactsSelectedList.size() > 0) {
                        commonUseContacts(mContactsSelectedList);
                        Intent data = new Intent();
                        // if(mContactsOriginalList != null){
                        // mContactsSelectedList.addAll(mContactsOriginalList);
                        // }
                        data.putExtra("originalUserId", userId);
                        // ToastUtils.showToast(userId);
                        data.putExtra(EXTRA_SELECTED_CONTACTS_LIST, mContactsSelectedList);
                        setResult(Activity.RESULT_OK, data);
                        finish();
                    } else {
                        mTitleRightTv.setClickable(false);
                    }
                }
            }

            @Override
            public int getDrawable() {
                return 0;
            }

            @Override
            public View getView() {
                mTitleRightTv = getRightTextView("", R.drawable.shape_frame_black_content_trans);
                mTitleRightTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                // if(mContactsOriginalList != null){
                // mTitleRightTv.setText("确定("+ mContactsOriginalList.size()
                // +")");
                // mTitleRightTv.setClickable(true);
                // }else{
                // mTitleRightTv.setText("确定(0)");
                // }

                mTitleRightTv.setText("确定(0)");
                mTitleRightTv.setTextColor(Color.WHITE);
                return mTitleRightTv;
            }
        };
    }

    private void requestData() {
        AsyncTask<Void, Void, List<Contacts>> task = new AsyncTask<Void, Void, List<Contacts>>() {
            @Override
            protected List<Contacts> doInBackground(Void... params) {
                List<Contacts> result = ContactsDao.getInstance().queryAll(Contacts.STATUS_NORNAL);
                if (result == null || result.isEmpty()) {
                    return result;
                }
                if (mContactsIdsFilter != null && !mContactsIdsFilter.isEmpty()) {
                    Iterator<Contacts> it = result.iterator();
                    while (it.hasNext()) {
                        Contacts c = it.next();
                        if (mContactsIdsFilter.contains(c.getId())) {
                            it.remove();
                        }
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(List<Contacts> result) {
                List<String> titles = new ArrayList<String>();
                List<List<Contacts>> lists = new ArrayList<List<Contacts>>();
                String ids = SpUtilsCur.getString(SpUtilsCur.KEYS_NORMAL.STRING_COMMON_USE_CONTACTS_IDS, null);
                if (!TextUtils.isEmpty(ids)) {
                    titles.add("常用联系人");
                    List<Contacts> commonUseContactsList = new ArrayList<Contacts>();
                    String[] split = ids.split(",");
                    for (String id : split) {
                        in: for (Contacts c : result) {
                            if (c.getId().equals(id)) {
                                commonUseContactsList.add(c);
                                break in;
                            }
                        }
                    }
                    lists.add(commonUseContactsList);
                }
                ContactsDao.groupByPinyin(result, titles, lists);
                mAdapter = new MyAdapter(titles, lists);
                mExpandableListView.setAdapter(mAdapter);
                for (int i = 0; i < mAdapter.getGroupCount(); i++) {
                    mExpandableListView.expandGroup(i);
                }
                mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        return true;
                    }
                });
            }
        };
        task.execute();
    }

    @Override
    protected TitleParams getTitleParams() {
        List<Action> actions = new ArrayList<Action>();
        actions.add(createTitleRight());
        return new TitleParams(getDefaultHomeAction(), "选择联系人", actions).setShowLogo(false);
    }

    // TODO
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContactsOriginalList = (ArrayList<Contacts>) getIntent().getSerializableExtra(EXTRA_ORIGINAL_CONTACTS_LIST);
        mContactsIdsFilter = getIntent().getStringArrayListExtra(EXTRA_FILTER_CONTACTS_IDS);
        super.onCreate(savedInstanceState);
        // if(mContactsOriginalList != null){
        // ToastUtils.showToast(mContactsOriginalList.size()+"");
        // }
        userId = getIntent().getStringExtra("originalUserId");
        setContentView(R.layout.activity_choose_contactsinfo);
        mSearchEt = (EditText) findViewById(R.id.et_search);
        mSearchNothingView = findViewById(R.id.tv_search_nothing);
        // mLayout = (RelativeLayout) findViewById(R.id.ll_lv_container);
        mExpandableListView = (ExpandableListView) findViewById(R.id.elv);
        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setOnChildClickListener(this);
        // mSearchListView = (ExpandableListView)
        // findViewById(R.id.e_search_lv);
        // mSearchListView.setGroupIndicator(null);
        // mSearchListView.setOnChildClickListener(this);
        mSearchEt.setOnClickListener(this);

        IndexBar bar = (IndexBar) findViewById(R.id.bar);
        bar.setIndexValues(IndexBar.getA_Z());
        bar.setOnChooseIndexListener(this);

        mContactsSelectedList = new ArrayList<Contacts>();

        requestData();
    }

    @Override
    public void onChoosedIndex(String indexValue) {
        showPinyinPopup(indexValue);
        if (indexValue == null) {
            return;
        }
        int index = mAdapter.getGroupIndex(indexValue);
        if (index != -1) {
            mExpandableListView.setSelectedGroup(index);
        }
    }

    // TODO
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.et_search:
            Intent intent = new Intent(this, ChooseSearchContactsActivity.class);
            // if(mContactsOriginalList != null){
            // mContactsOriginalList.addAll(mContactsSelectedList);
            // intent.putExtra(ChooseSearchContactsActivity.EXTRA_ORIGINAL_CONTACTS_LIST,mContactsOriginalList);
            // //
            // intent.putExtra(ChooseSearchContactsActivity.EXTRA_SELECTED_CONTACTS_LIST,mContactsSelectedList);
            // }else{
            // intent.putExtra(ChooseSearchContactsActivity.EXTRA_ORIGINAL_CONTACTS_LIST,mContactsSelectedList);
            // }
            intent.putExtra(ChooseSearchContactsActivity.EXTRA_ORIGINAL_CONTACTS_LIST, mContactsOriginalList);
            intent.putExtra(ChooseSearchContactsActivity.EXTRA_FILTER_CONTACTS_IDS, mContactsIdsFilter);
            intent.putExtra("originalUserId", userId);
            intent.putExtra("contact", "contactsearch");
            startActivityForResult(intent, 1);
            return;
        }
    }

    private void commonUseContacts(List<Contacts> list) {
        // 常用联系人处理
        String ids = SpUtilsCur.getString(SpUtilsCur.KEYS_NORMAL.STRING_COMMON_USE_CONTACTS_IDS, null);
        List<String> idList = new ArrayList<String>();
        if (ids != null) {
            idList.addAll(Arrays.asList(ids.split(",")));
        }
        for (Contacts c : list) {
            if (idList.contains(c.getId())) {
                idList.remove(c.getId());
            }
            idList.add(0, c.getId());
        }
        if (idList.size() > 10) {
            idList = idList.subList(0, 10);
        }
        ids = JavaUtils.joinString(",", ids);
        SpUtilsCur.put(SpUtilsCur.KEYS_NORMAL.STRING_COMMON_USE_CONTACTS_IDS, ids);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1 && data != null) {
            ArrayList<Contacts> mContactsSelectedList = (ArrayList<Contacts>) data
                    .getSerializableExtra(ChooseSearchContactsActivity.EXTRA_ORIGINAL_CONTACTS_LIST);

            commonUseContacts(mContactsSelectedList);

            data.putExtra(EXTRA_SELECTED_CONTACTS_LIST, mContactsSelectedList);
            data.putExtra("originalUserId", userId);
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }
}
