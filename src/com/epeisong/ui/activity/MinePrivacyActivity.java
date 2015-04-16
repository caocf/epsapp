package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.SimpleListActivity;
import com.epeisong.base.adapter.impl.IconTextAdapter;
import com.epeisong.base.adapter.item.IconTextItem;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetPrivacyGet;
import com.epeisong.data.net.NetPrivacySet;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEPrivacy;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq.Builder;
import com.epeisong.model.Privacy;
import com.epeisong.ui.activity.MinePrivacyActivity.Item;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 我的隐私
 * 
 * @author poet
 * 
 */
public class MinePrivacyActivity extends SimpleListActivity<Item> {

    private List<String> mData1;

    private Privacy mPrivacy;

    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mData1 = new ArrayList<String>();
        mData1.add("允许所有人");
        mData1.add("只允许联系人");
        mData1.add("拒绝任何人");

        List<Item> data = new ArrayList<MinePrivacyActivity.Item>();
        data.add(new Item(Item.type_invalid, "消息"));
        data.add(new Item(Item.type_advisory_chat, "咨询聊天"));
        mAdapter.replaceAll(data);

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                NetPrivacyGet net = new NetPrivacyGet() {
                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setLogisticsId(Integer.parseInt(UserDao.getInstance().getUser().getId()));
                        req.setPrivacyType(Properties.PRIVACY_TYPE_CHAT);
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        ProtoEPrivacy privacy = resp.getPrivacy();
                        mPrivacy = new Privacy();
                        if (privacy.getType() == Properties.PRIVACY_TYPE_CHAT) {
                            mPrivacy.setChat(privacy.getStatus());
                        }
                        return true;
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissPendingDialog();
                if (result) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        };
        task.execute();
        showPendingDialog(null);
    }

    private void setPrivacy(final int type, final int value) {
        showPendingDialog(null);
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                NetPrivacySet net = new NetPrivacySet() {
                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setPrivacyType(type);
                        req.setPrivacyStatus(value);
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        if (mPrivacy == null) {
                            mPrivacy = new Privacy();
                        }
                        if (type == Properties.PRIVACY_TYPE_CHAT) {
                            mPrivacy.setChat(value);
                        }
                        return true;
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissPendingDialog();
                if (result) {
                    mAdapter.notifyDataSetChanged();
                } else {

                }
            }
        };
        task.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = mAdapter.getItem(position);
        if (item.type == Item.type_advisory_chat) {
            showPopupWindow(view);
        }
    }

    private void showPopupWindow(View v) {
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(getApplicationContext());
            final IconTextAdapter mIconTextAdapter = new IconTextAdapter(getApplicationContext(), 40);
            List<IconTextItem> data = new ArrayList<IconTextItem>();
            data.add(new IconTextItem(0, "允许所有人", null));
            data.add(new IconTextItem(0, "只允许联系人", null));
            data.add(new IconTextItem(0, "拒绝任何人", null));
            mIconTextAdapter.replaceAll(data);
            ListView lv = new ListView(getApplicationContext());
            lv.setAdapter(mIconTextAdapter);
            lv.setBackgroundColor(Color.argb(0xFF, 0x2D, 0x30, 0x37));
            mPopupWindow.setContentView(lv);
            mPopupWindow.setWidth(EpsApplication.getScreenWidth() / 2 - 50);
            mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
            lv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mPopupWindow.dismiss();
                    int newStatus = -1;
                    if (position == 0) {
                        newStatus = Properties.PRIVACY_STATUS_ALLOW_ALL;
                    } else if (position == 1) {
                        newStatus = Properties.PRIVACY_STATUS_ALLOW_FRIENDS;
                    } else if (position == 2) {
                        newStatus = Properties.PRIVACY_STATUS_ALLOW_SELF_ONLY;
                    }
                    if (mPrivacy == null || mPrivacy.getChat() != newStatus) {
                        setPrivacy(Properties.PRIVACY_TYPE_CHAT, newStatus);
                    }
                }
            });
        }
        mPopupWindow.showAsDropDown(v, EpsApplication.getScreenWidth() / 2 + 40, 0);
    }

    @Override
    protected Integer getAdapterViewType(int position) {
        int type = mAdapter.getItem(position).getType();
        if (type == Item.type_invalid) {
            return -1;
        }
        return 1;
    }

    @Override
    protected Integer getAdapterViewTypeCount() {
        return 2;
    }

    @Override
    protected View getItemView(int position, View convertView, ViewGroup parent) {
        Item item = mAdapter.getItem(position);
        if (item.getType() == Item.type_invalid) {
            TextView tv = new TextView(getApplicationContext());
            tv.setText(item.getName());
            tv.setTextColor(Color.argb(0xFF, 0x87, 0x87, 0x87));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            int p = (int) DimensionUtls.getPixelFromDp(10);
            int p2 = (int) DimensionUtls.getPixelFromDp(6);
            tv.setPadding(p, p2, 0, p2);
            return tv;
        }
        ViewHolder holder = null;
        if (convertView == null || !(convertView instanceof RelativeLayout)) {
            convertView = SystemUtils.inflate(R.layout.activity_mine_privacy_item);
            holder = new ViewHolder();
            holder.findView(convertView, item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.fillData(item);
        return convertView;
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "我的隐私", null).setShowLogo(false);
    }

    @Override
    protected Boolean isAdapterItemEnabled(int position) {
        return mAdapter.getItem(position).getType() != Item.type_invalid;
    }

    public static class Item {

        public static final int type_invalid = -1;
        public static final int type_advisory_chat = 1;
        public static final int type_mine_freight = 2;
        public static final int type_received_freight = 3;
        public static final int type_task = 4;
        public static final int type_task_outer = 5;
        public static final int type_task_flow = 6;

        private int type;
        private String name;

        public Item(int type, String name) {
            super();
            this.type = type;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getType() {
            return type;
        }

    }

    private class ViewHolder {
        TextView tv_name;
        TextView tv_sp_left;
        ImageView iv_arrow;

        public void fillData(Item item) {
            tv_name.setText(item.getName());
            if (item.getType() == Item.type_advisory_chat) {
                String s = "拒绝任何人";
                if (mPrivacy != null) {
                    switch (mPrivacy.getChat()) {
                    case Properties.PRIVACY_STATUS_ALLOW_ALL:
                        s = "允许所有人";
                        break;
                    case Properties.PRIVACY_STATUS_ALLOW_FRIENDS:
                        s = "只允许联系人";
                        break;
                    case Properties.PRIVACY_STATUS_ALLOW_SELF_ONLY:
                    case Properties.PRIVACY_STATUS_UNDEFINED: // 拒绝线上咨询（该用户是客服添加的）
                        s = "拒绝任何人";
                        break;
                    }
                }
                tv_sp_left.setText(s);
            } else {
                tv_sp_left.setText("允许所有人");
            }
            iv_arrow.setTag(item.getType());
        }

        public void findView(View v, Item item) {
            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_sp_left = (TextView) v.findViewById(R.id.tv_sp_left);
            iv_arrow = (ImageView) v.findViewById(R.id.iv_arrow);
        }
    }
}
