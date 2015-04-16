package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.IndexBar;
import com.epeisong.base.view.IndexBar.OnChooseIndexListener;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.model.Contacts;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 选择联系人
 * 
 * @author poet
 * 
 */
public class ChooseContactsActivity extends BaseActivity implements OnChildClickListener, OnChooseIndexListener {

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
                convertView = SystemUtils.inflate(R.layout.item_contacts_child_checked);
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
                convertView = SystemUtils.inflate(R.layout.item_contacts_group);
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
        CheckBox cb;

        public void fillData(Contacts contacts) {
            // TODO 显示头像
            tv_user_name.setText(contacts.getShow_name());
            if (mContactsOriginalList != null && mContactsOriginalList.contains(contacts)) {
                cb.setChecked(true);
                cb.setEnabled(false);
            } else {
                cb.setEnabled(true);
                if (mContactsSelectedList.contains(contacts)) {
                    cb.setChecked(true);
                } else {
                    cb.setChecked(false);
                }
            }
        }

        public void findView(View v) {
            iv_head = (ImageView) v.findViewById(R.id.iv_head);
            tv_user_name = (TextView) v.findViewById(R.id.tv_user_name);
            cb = (CheckBox) v.findViewById(R.id.cb);
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
    private HorizontalScrollView mHsv;
    private LinearLayout mContactsContainerLl;

    private EditText mSearchEt;
    private ExpandableListView mExpandableListView;
    private MyAdapter mAdapter;

    private List<Contacts> mContactsList;

    private ArrayList<Contacts> mContactsOriginalList;

    private ArrayList<Contacts> mContactsSelectedList;

    private List<String> mContactsIdsFilter;

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Contacts c = mAdapter.getChild(groupPosition, childPosition);
        if (mContactsOriginalList != null && mContactsOriginalList.contains(c)) {
            return true;
        }
        if (mContactsSelectedList.contains(c)) {
            mContactsSelectedList.remove(c);
            int count = mContactsContainerLl.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = mContactsContainerLl.getChildAt(i);
                Object tag = child.getTag();
                if (tag != null && tag instanceof String) {
                    if (tag.equals(c.getShow_name())) {
                        mContactsContainerLl.removeView(child);
                        break;
                    }
                }
            }
        } else {
            mContactsSelectedList.add(c);
            ImageView iv = new ImageView(getApplicationContext());
            int w = (int) DimensionUtls.getPixelFromDp(35);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(w, w);
            p.rightMargin = (int) DimensionUtls.getPixelFromDp(5);
            iv.setLayoutParams(p);
            // TODO iv
            // iv.setTag(c.getId());
            iv.setTag(c.getShow_name());
            mContactsContainerLl.addView(iv);
            mHsv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mHsv.scrollTo(mContactsContainerLl.getMeasuredWidth(), 0);
                }
            }, 100);
        }
        mAdapter.notifyDataSetChanged();
        int selectedCount = mContactsSelectedList.size();
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
                if (mContactsSelectedList.size() > 0) {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_SELECTED_CONTACTS_LIST, mContactsSelectedList);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            }

            @Override
            public int getDrawable() {
                return 0;
            }

            @Override
            public View getView() {
                mTitleRightTv = new TextView(getApplicationContext());
                int padding = (int) DimensionUtls.getPixelFromDp(5);
                mTitleRightTv.setPadding(padding, padding, padding, padding);
                mTitleRightTv.setText("确定(0)");
                mTitleRightTv.setBackgroundColor(Color.BLUE);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_contacts);
        mHsv = (HorizontalScrollView) findViewById(R.id.hsv);
        mContactsContainerLl = (LinearLayout) findViewById(R.id.ll_contacts_container);
        mSearchEt = (EditText) findViewById(R.id.et_search);
        mExpandableListView = (ExpandableListView) findViewById(R.id.elv);
        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setOnChildClickListener(this);

        IndexBar bar = (IndexBar) findViewById(R.id.bar);
        bar.setIndexValues(IndexBar.getA_Z());
        bar.setOnChooseIndexListener(this);

        mContactsOriginalList = (ArrayList<Contacts>) getIntent().getSerializableExtra(EXTRA_ORIGINAL_CONTACTS_LIST);

        mContactsSelectedList = new ArrayList<Contacts>();

        mContactsIdsFilter = getIntent().getStringArrayListExtra(EXTRA_FILTER_CONTACTS_IDS);

        requestData();
    }

    @Override
    public void onChoosedIndex(String indexValue) {
        if (indexValue == null) {
            return;
        }
        int index = mAdapter.getGroupIndex(indexValue);
        if (index != -1) {
            mExpandableListView.setSelectedGroup(index);
        }
    }
}
