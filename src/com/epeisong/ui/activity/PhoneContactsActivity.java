package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import lib.pulltorefresh.PullToRefreshBase.Mode;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.PullToRefreshListViewActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.dialog.YesNoDialog;
import com.epeisong.base.dialog.YesNoDialog.OnYesNoDialogClickListener;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.PhoneContactsDao;
import com.epeisong.data.dao.PhoneContactsDao.PhoneContactsObserver;
import com.epeisong.data.dao.util.CRUD;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetCheckPhoneContacts;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.ContactReq;
import com.epeisong.logistics.proto.Eps.ShortLogistics;
import com.epeisong.model.PhoneContacts;
import com.epeisong.model.User;
import com.epeisong.net.request.NetSearchContacts;
import com.epeisong.net.request.OnNetRequestListenerImpl;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SpUtilsCur;
import com.epeisong.utils.SysUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 手机联系人
 * 
 * @author poet
 * 
 */
public class PhoneContactsActivity extends PullToRefreshListViewActivity implements PhoneContactsObserver,
        OnCancelListener {

    private HoldDataBaseAdapter<PhoneContacts> mAdapter;

    private AtomicBoolean mCanGoOn = new AtomicBoolean(true);
    private Runnable mRunnable = new Runnable() {

        int PAGE_SIZE = 100;
        String[] projection = { ContactsContract.Contacts._ID, "sort_key", ContactsContract.Contacts.DISPLAY_NAME };

        @Override
        public void run() {
            AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    int maxId = SpUtilsCur.getInt(SpUtilsCur.KEYS_SERVICE.INT_PHONE_CONTACTS_LOADED_MAX_ID, 0);
                    LogUtils.e("PhoneContactsActivity", "maxId:" + maxId);
                    Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection,
                            ContactsContract.Contacts._ID + ">?", new String[] { String.valueOf(maxId) },
                            ContactsContract.Contacts._ID + " limit 0," + PAGE_SIZE);
                    List<PhoneContacts> contactss = new ArrayList<PhoneContacts>();
                    int newMaxId = maxId;
                    while (cursor.moveToNext()) {
                        String sort_key = cursor.getString(cursor.getColumnIndex("sort_key"));
                        int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        newMaxId = id;
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        Cursor phoneCursor = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                        while (phoneCursor.moveToNext()) {
                            String number = phoneCursor.getString(phoneCursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            number = number.replaceAll(" ", "").replaceAll("-", "").replace("+86", "");
                            if (!number.startsWith("1") || number.length() != 11) {
                                continue;
                            }
                            PhoneContacts contacts = new PhoneContacts();
                            contacts.setId(String.valueOf(id) + ":" + number);
                            contacts.setContacts_id(id);
                            contacts.setName(name);
                            contacts.setPhone_num(number);
                            contacts.setSort_key(sort_key);
                            contacts.setStatus(PhoneContacts.STATUS_UN_SPECIFIED);
                            if (ContactsDao.getInstance().queryByPhone(number) != null) {
                                contacts.setStatus(PhoneContacts.STATUS_ADDED);
                            }
                            contactss.add(contacts);
                        }
                        phoneCursor.close();
                    }
                    cursor.close();
                    if (contactss.size() > 0) {
                        final List<String> nums = new ArrayList<String>();
                        for (PhoneContacts pc : contactss) {
                            nums.add(pc.getPhone_num());
                            LogUtils.d(null, "id:" + pc.getContacts_id() + "--phoneNum:" + pc.getPhone_num());
                        }

                        NetCheckPhoneContacts net = new NetCheckPhoneContacts() {
                            @Override
                            protected boolean onSetRequest(ContactReq.Builder req) {
                                req.addAllPhoneNumber(nums);
                                return true;
                            }
                        };
                        try {
                            CommonLogisticsResp.Builder resp = net.request();
                            if (net.isSuccess(resp)) {
                                List<String> registedList = resp.getRegisteredMobileList();
                                for (PhoneContacts item : contactss) {
                                    if (item.getStatus() == PhoneContacts.STATUS_UN_SPECIFIED) {
                                        if (registedList.contains(item.getPhone_num())) {
                                            item.setStatus(PhoneContacts.STATUS_UN_ADD);
                                        } else {
                                            item.setStatus(PhoneContacts.STATUS_UN_USE);
                                        }
                                    }
                                }
                                if (PhoneContactsDao.getInstance().insertAll(contactss)) {
                                    SpUtilsCur.put(SpUtilsCur.KEYS_SERVICE.INT_PHONE_CONTACTS_LOADED_MAX_ID, newMaxId);
                                    LogUtils.e("PhoneContactsActivity", "newMaxId:" + newMaxId);
                                    return true;
                                } else {
                                    LogUtils.e(null, "插入数据库失败");
                                }
                            }
                        } catch (NetGetException e) {
                            e.printStackTrace();
                            LogUtils.e(null, e);
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    LogUtils.e("PhoneContactsActivity", "result:" + result);
                    if (result != null && result && mCanGoOn.get()) {
                        HandlerUtils.postDelayed(mRunnable, 200);
                    } else {
                        dismissPendingDialog();
                    }
                };
            };
            task.execute();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMode(Mode.DISABLED);
        mListView.setFastScrollEnabled(true);

        if (SpUtilsCur.getInt(SpUtilsCur.KEYS_SERVICE.INT_PHONE_CONTACTS_LOADED_MAX_ID, 0) == 0) {
            showYesNoDialog("提示", "是否允许使用您的手机通讯录，以便查找更多好友", "否", "是", new OnYesNoDialogClickListener() {
                @Override
                public void onYesNoDialogClick(int yesOrNo) {
                    if (yesOrNo == YesNoDialog.BTN_NO) {
                        finish();
                    } else if (yesOrNo == YesNoDialog.BTN_YES) {
                        HandlerUtils.post(mRunnable);
                        showPendingDialog("正在获取朋友信息...", PhoneContactsActivity.this);
                    }
                }
            });
        } else {
            HandlerUtils.post(mRunnable);
            showPendingDialog("正在获取朋友信息...", PhoneContactsActivity.this);
            refreshData();
        }

        PhoneContactsDao.getInstance().addObserver(this);
    }

    @Override
    public void onPhoneContactsChange(CRUD crud) {
        refreshData();
    }

    private void refreshData() {
        AsyncTask<Void, Void, List<PhoneContacts>> task = new AsyncTask<Void, Void, List<PhoneContacts>>() {
            @Override
            protected List<PhoneContacts> doInBackground(Void... params) {
                return PhoneContactsDao.getInstance().queryAll();
            }

            @Override
            protected void onPostExecute(List<PhoneContacts> result) {
                mAdapter.replaceAll(result);
                setTitleText("手机联系人(" + result.size() + ")");
            }
        };
        task.execute();
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "手机联系人", null).setShowLogo(false);
    }

    @Override
    protected ListAdapter onCreateAdapter() {
        mAdapter = new MyAdapter();
        return mAdapter;
    }

    @Override
    protected void onDestroy() {
        mCanGoOn.set(false);
        PhoneContactsDao.getInstance().removeObserver(this);
        super.onDestroy();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mCanGoOn.set(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final PhoneContacts contacts = mAdapter.getItem(position - mListView.getHeaderViewsCount());
        switch (contacts.getStatus()) {
        case PhoneContacts.STATUS_UN_SPECIFIED:
        case PhoneContacts.STATUS_UN_USE:
            SysUtils.sendSms(this, contacts.getPhone_num(),
                    "我在用易配送物流平台，为方便我们做生意，请访问http://download.epeisong.com 下载注册后，通过手机号添加我为好友");
            break;
        case PhoneContacts.STATUS_UN_ADD:
            NetSearchContacts net = new NetSearchContacts(this) {
                @Override
                protected boolean onSetRequest(ContactReq.Builder req) {
                    req.setMobile(contacts.getPhone_num());
                    return true;
                }
            };
            net.request(new OnNetRequestListenerImpl<CommonLogisticsResp.Builder>() {

                @Override
                public void onSuccess(CommonLogisticsResp.Builder response) {
                    List<ShortLogistics> logisticsList = response.getShortLogisticsList();
                    if (logisticsList == null || logisticsList.isEmpty()) {
                        showMessageDialog(null, "用户不存在");
                        return;
                    }
                    List<User> users = new ArrayList<User>();
                    for (ShortLogistics logi : logisticsList) {
                        users.add(UserParser.parse(logi));
                    }
                    User user = users.get(0);
                    Intent intent = new Intent(getApplicationContext(), SearchUserDetailActivity.class);
                    intent.putExtra(SearchUserDetailActivity.EXTRA_USER, user);
                    startActivity(intent);
                }

            });
            break;
        default:
            break;
        }
    }

    private class MyAdapter extends HoldDataBaseAdapter<PhoneContacts> {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.item_phone_contacts);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.fillData(getItem(position));
            return convertView;
        }
    }

    private class ViewHolder {
        TextView tv_name;
        TextView tv_phone;
        TextView tv_show_name;
        ImageView iv_add;
        TextView tv_status;

        public void fillData(PhoneContacts contacts) {
            tv_name.setText(contacts.getName());
            tv_phone.setText(contacts.getPhone_num());
            tv_show_name.setVisibility(View.GONE);
            iv_add.setVisibility(View.INVISIBLE);
            switch (contacts.getStatus()) {
            case PhoneContacts.STATUS_UN_SPECIFIED:
            case PhoneContacts.STATUS_UN_USE:
                tv_status.setText("邀请");
                tv_status.setTextColor(Color.argb(0xff, 0x2b, 0x58, 0xd9));
                tv_status.setCompoundDrawables(null, null, null, null);
                break;
            case PhoneContacts.STATUS_UN_ADD:
                iv_add.setVisibility(View.VISIBLE);
                tv_status.setTextColor(Color.argb(0xff, 0x14, 0xcc, 0x0a));
                tv_status.setText("添加");
                break;
            case PhoneContacts.STATUS_ADDED:
                tv_status.setTextColor(Color.argb(0xff, 0x96, 0xa6, 0xd4));
                tv_status.setCompoundDrawables(null, null, null, null);
                tv_status.setText("已添加");
                // tv_show_name.setVisibility(View.VISIBLE);
                // tv_show_name.setText(ContactsDao.getInstance().queryByPhone(contacts.getPhone_num()).getShow_name());
                break;
            default:
                break;
            }
        }

        public void findView(View v) {
            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_phone = (TextView) v.findViewById(R.id.tv_phone);
            tv_show_name = (TextView) v.findViewById(R.id.tv_show_name);
            iv_add = (ImageView) v.findViewById(R.id.iv_add);
            tv_status = (TextView) v.findViewById(R.id.tv_status);
        }
    }
}
