package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lib.universal_image_loader.ImageLoaderUtils;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import com.epeisong.utils.android.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.FlowTextLayout;
import com.epeisong.base.view.FlowTextLayout.Textable;
import com.epeisong.base.view.FlowTextLayout_old;
import com.epeisong.base.view.FlowTextLayout_old.Attr;
import com.epeisong.base.view.FlowTextLayout_old.OnFlowTextItemClickListener;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.ContactsTagDao;
import com.epeisong.data.dao.EpsTagDao;
import com.epeisong.model.Contacts;
import com.epeisong.model.EpsTag;
import com.epeisong.model.User;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 搜索特定联系人
 * 
 * @author Jack
 * 
 */
public class ChooseSSearchContactsActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    private class ListAdapter extends HoldDataBaseAdapter<Contacts> {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.activity_choose_contacts_item);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Contacts c = getItem(position);
            holder.fillData(c);
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView iv_head;
        TextView tv_user_name;
        TextView tv_phone;
        TextView tv_address;
        TextView tv_user_type;
        public CheckBox cb;

        public void fillData(Contacts contacts) {
        	//显示头像
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
            tv_user_type.setText(contacts.getLogistic_type_name());
            if (!TextUtils.isEmpty(flag) && flag.equals("contactsearch")) {
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
                cb.setTag(contacts);
            } else if (flag.equals("contactpage")) {
                cb.setVisibility(View.GONE);
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

    public static final String EXTRA_ORIGINAL_CONTACTS_LIST = "original_contacts_list";
    public static final String EXTRA_SELECTED_CONTACTS_LIST = "selected_contacts_list";
    public static final String EXTRA_FILTER_CONTACTS_IDS = "filter_contacts_ids";
    private TextView mTitleRightTv;

    private EditText mSearchEt;
    private ListAdapter lAdapter;
    private RelativeLayout mrl_tag;
    
    private View mSearchNothingView;
    private ListView lvSearch;
    // private RelativeLayout mrl_search;
    private RelativeLayout mrl_tag_contact;
    private LinearLayout ll_selectbutton;
    private Button bt_selectok;
    private CheckBox cb_selectall, cb_selectinverse;
    private FlowTextLayout textLayoutTag;
    private FlowTextLayout_old textLayoutType;

    private ArrayList<Contacts> mContactsOriginalList;
    private ArrayList<Contacts> mContactsSelectedList;
    private List<String> mContactsIdsFilter;
    // private int selectCount;
    private int selectedCount;
    private String userId;

    private String flag;
    private String bclick = "";

    private Action createTitleRight() {
        return new Action() {
            @Override
            public void doAction(View v) {
                if (mContactsSelectedList != null) {
                    if (mContactsSelectedList.size() > 0) {
                        Intent data = new Intent();
                        // if(mContactsOriginalList != null){
                        // mContactsSelectedList.addAll(mContactsOriginalList);
                        // }
                        data.putExtra("originalUserId", userId);
                        data.putExtra(ChooseSSearchContactsActivity.EXTRA_ORIGINAL_CONTACTS_LIST, mContactsSelectedList);
                        // ToastUtils.showToast(mContactsSelectedList.size()+"");
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
                mTitleRightTv.setClickable(false);
                mTitleRightTv.setTextColor(Color.WHITE);
                // mTitleRightTv.setBackgroundResource(R.drawable.contacts_head);
                return mTitleRightTv;
            }
        };
    }

    private void requestDataByTagId(final int tagId) {
        AsyncTask<Void, Void, List<Contacts>> task = new AsyncTask<Void, Void, List<Contacts>>() {
            @Override
            protected List<Contacts> doInBackground(Void... params) {
                List<String> contactsIds = ContactsTagDao.getInstance().queryContactsIds(tagId);
                List<Contacts> result = ContactsDao.getInstance().queryContacts(contactsIds);
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
                if (lAdapter == null) {
                    lAdapter = new ListAdapter();
                    lvSearch.setAdapter(lAdapter);
                }
                lAdapter.replaceAll(result);
                if (lAdapter.isEmpty()) {
                    mSearchNothingView.setVisibility(View.VISIBLE);
                } else {
                    mSearchNothingView.setVisibility(View.GONE);
                }
            }
        };
        task.execute();
    }

    private void requestData(final int typeCode) {
        AsyncTask<Void, Void, List<Contacts>> task = new AsyncTask<Void, Void, List<Contacts>>() {
            @Override
            protected List<Contacts> doInBackground(Void... params) {
                List<Contacts> result = ContactsDao.getInstance().searchByLogisticType(typeCode);
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
                lAdapter = new ListAdapter();
                lAdapter.notifyDataSetChanged();
                lvSearch.setAdapter(lAdapter);
                lAdapter.replaceAll(result);
                if (lAdapter.isEmpty()) {
                    mSearchNothingView.setVisibility(View.VISIBLE);
                } else {
                    mSearchNothingView.setVisibility(View.GONE);
                }
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
        mContactsOriginalList = (ArrayList<Contacts>) getIntent().getSerializableExtra(EXTRA_ORIGINAL_CONTACTS_LIST);
        mContactsIdsFilter = getIntent().getStringArrayListExtra(EXTRA_FILTER_CONTACTS_IDS);
        super.onCreate(savedInstanceState);
        // if(mContactsOriginalList != null){
        // ToastUtils.showToast("mContactsOriginalList = " +
        // mContactsOriginalList.size());
        // }
        mContactsSelectedList = new ArrayList<Contacts>();
        // if(mContactsOriginalList != null){
        // mContactsSelectedList.addAll(mContactsOriginalList);
        // }
        setContentView(R.layout.activity_choose_ssearch_contacts);
        userId = getIntent().getStringExtra("originalUserId");
        mSearchEt = (EditText) findViewById(R.id.et_search);
        mrl_tag = (RelativeLayout) findViewById(R.id.ll_tag_container);
        mSearchNothingView = findViewById(R.id.tv_search_nothing);
        lvSearch = (ListView) findViewById(R.id.search_lv);
        lvSearch.setAdapter(lAdapter = new ListAdapter());
        lvSearch.setOnItemClickListener(this);
        // mrl_search = (RelativeLayout) findViewById(R.id.rl_search);
        mrl_tag_contact = (RelativeLayout) findViewById(R.id.rl_tag_contact);
        ll_selectbutton = (LinearLayout) findViewById(R.id.ll_selectbutton); 
        textLayoutTag = (FlowTextLayout) findViewById(R.id.ftl_tag);
        int w = EpsApplication.getScreenWidth() - DimensionUtls.getPixelFromDpInt(30);
        textLayoutTag.setAttr(new com.epeisong.base.view.FlowTextLayout.Attr().setLayoutWidth(w).setTextBgResId(
                R.drawable.selector_main_btn_bg));

        List<Integer> tagIds = ContactsTagDao.getInstance().queryTagIds();
        if (tagIds != null && !tagIds.isEmpty()) {
            List<EpsTag> tags = EpsTagDao.getInstance().query(tagIds);
            textLayoutTag.setTextList(tags);
        }
        textLayoutTag
                .setOnFlowTextItemClickListener(new com.epeisong.base.view.FlowTextLayout.OnFlowTextItemClickListener() {
                    @Override
                    public void onFlowTextItemClick(Textable textable, boolean isSelected) {
                        mrl_tag.setVisibility(View.GONE);
                        mrl_tag_contact.setVisibility(View.VISIBLE);
                        ll_selectbutton.setVisibility(View.VISIBLE);
                        mSearchEt.setText(textable.getText());
                        // 把eidt输入框光标定位到内容的最后面
                        mSearchEt.setSelection(textable.getText().length());
                        // 点击之后执行代码
                        bclick = "click";
                        if (bclick.equals("click")) {
                            requestDataByTagId(((EpsTag) textable).getId());
                            bclick = "unclick";
                        }
                    }
                });

        textLayoutType = (FlowTextLayout_old) findViewById(R.id.ftl_type);
        textLayoutType.setAttr(new Attr().setLayoutWidth(w).setTextBgResId(R.drawable.selector_main_btn_bg));
        List<Contacts> contactsList = ContactsDao.getInstance().queryAll(Contacts.STATUS_NORNAL);
        final List<String> nameList = new ArrayList<String>();
        final List<Integer> codeList = new ArrayList<Integer>();
        if (contactsList != null && !contactsList.isEmpty()) {
            for (Contacts contacts : contactsList) {
                if (!codeList.contains(contacts.getLogistic_type_code())) {
                    codeList.add(contacts.getLogistic_type_code());
                    nameList.add(contacts.getLogistic_type_name());
                }
            }
        }
        textLayoutType.setTextList(nameList);
        textLayoutType.setOnFlowTextItemClickListener(new OnFlowTextItemClickListener() {
            @Override
            public void onFlowTextItemClick(String text) {
                mrl_tag.setVisibility(View.GONE);
                mrl_tag_contact.setVisibility(View.VISIBLE);
                ll_selectbutton.setVisibility(View.VISIBLE);
                mSearchEt.setText(text);
                // 把eidt输入框光标定位到内容的最后面
                mSearchEt.setSelection(text.length());
                int index = nameList.indexOf(text);
                if (index != -1) {
                    int code = codeList.get(index);
                    bclick = "click";
                    if (bclick.equals("click")) {
                        requestData(code);
                        bclick = "unclick";
                    }
                }
            }
        });
        mSearchEt.setOnClickListener(this);
        flag = getIntent().getStringExtra("contact");
        if (flag.equals("contactpage")) {
            mTitleRightTv.setVisibility(View.GONE);
        }

        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(final Editable edit) {
                if (TextUtils.isEmpty(edit.toString())) {
                    mrl_tag.setVisibility(View.VISIBLE);
                    mrl_tag_contact.setVisibility(View.GONE);
                    ll_selectbutton.setVisibility(View.GONE);
                    return;
                }
                mrl_tag.setVisibility(View.GONE);
                if (TextUtils.isEmpty(edit.toString())) {
                    return;
                } else {
                    mSearchEt.setTextColor(Color.BLACK);
                }
                if (TextUtils.isEmpty(bclick) || bclick.equals("unclick")) {
                    AsyncTask<Void, Void, List<Contacts>> task = new AsyncTask<Void, Void, List<Contacts>>() {
                        @Override
                        protected List<Contacts> doInBackground(Void... params) {
                            return ContactsDao.getInstance().searchByNameOrPinyin(edit.toString());
                        }

                        @Override
                        protected void onPostExecute(List<Contacts> result) {
                            mrl_tag.setVisibility(View.GONE);
                            mrl_tag_contact.setVisibility(View.VISIBLE);
                            ll_selectbutton.setVisibility(View.VISIBLE);
                            lAdapter = new ListAdapter();
                            lAdapter.notifyDataSetChanged();
                            lvSearch.setAdapter(lAdapter);
                            lAdapter.replaceAll(result);
                            if (lAdapter.isEmpty()) {
                                mSearchNothingView.setVisibility(View.VISIBLE);
                            } else {
                                mSearchNothingView.setVisibility(View.GONE);
                            }
                        }
                    };
                    task.execute();
                }
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }
        });
        
        
        bt_selectok = (Button) findViewById(R.id.bt_selectok);
        bt_selectok.setOnClickListener(this);
        cb_selectall = (CheckBox) findViewById(R.id.cb_selectall);
        cb_selectall.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				mContactsSelectedList.clear();
				if( arg1 ) {
					int count = lAdapter.getCount();
					for(int i=0;i<count;i++) {
							Contacts c=lAdapter.getItem(i);
							mContactsSelectedList.add(c);
					}
				}
				lAdapter.notifyDataSetChanged();
				selectedCount = mContactsSelectedList.size();
				bt_selectok.setText("确定(" + selectedCount + ")");
			}
		});
        
        cb_selectinverse = (CheckBox) findViewById(R.id.cb_selectinverse);
        cb_selectinverse.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				//mContactsSelectedList.clear();
					int count = lAdapter.getCount();
					for(int i=0;i<count;i++) {
							Contacts c=lAdapter.getItem(i);
							if(mContactsSelectedList.contains(c))
								mContactsSelectedList.remove(c);
							else
								mContactsSelectedList.add(c);
					}
					lAdapter.notifyDataSetChanged();
				selectedCount = mContactsSelectedList.size();
				bt_selectok.setText("确定(" + selectedCount + ")");
			}
		});
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.bt_selectok:
            if (mContactsSelectedList != null) {
                if (mContactsSelectedList.size() > 0) {
                    Intent data = new Intent();
                    data.putExtra("originalUserId", userId);
                    data.putExtra(ChooseSSearchContactsActivity.EXTRA_ORIGINAL_CONTACTS_LIST, mContactsSelectedList);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                } else {
                    //mTitleRightTv.setClickable(false);
                }
            }
        	break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        if (flag.equals("contactsearch")) {
            Contacts c = lAdapter.getItem(position);
            if (!TextUtils.isEmpty(userId) && c.getId().equals(userId)) {
                ToastUtils.showToast("不能把该信息转发给原始发布者");
                return;
            }
            if (mContactsOriginalList != null && mContactsOriginalList.contains(c)) {
                return;
            }
            if (mContactsSelectedList.contains(c)) {
                mContactsSelectedList.remove(c);
            } else {
                mContactsSelectedList.add(c);
            }
            lAdapter.notifyDataSetChanged();
            selectedCount = mContactsSelectedList.size();

            // if(mContactsOriginalList != null){
            // // ToastUtils.showToast(mContactsOriginalList.size()+"");
            // selectedCount = mContactsSelectedList.size() +
            // mContactsOriginalList.size();
            // }else{
            // selectedCount = mContactsSelectedList.size();
            // }
            selectedCount = mContactsSelectedList.size();
            mTitleRightTv.setText("确定(" + selectedCount + ")");
            bt_selectok.setText("确定(" + selectedCount + ")");
            if (selectedCount == 0) {
                mTitleRightTv.setClickable(false);
            } else {
                mTitleRightTv.setClickable(true);
            }
        } else {
            Contacts c = lAdapter.getItem(position);
            Intent intent = new Intent(this, ContactsDetailActivity.class);
            intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, c.getId());
            User u = c.convertToUser();
            intent.putExtra(ContactsDetailActivity.EXTRA_USER, u);
            startActivity(intent);
        }
    }

}
