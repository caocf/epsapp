package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.adapter.impl.IconTextAdapter;
import com.epeisong.base.adapter.item.IconTextItem;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.FreightOfContactsDao;
import com.epeisong.data.dao.FreightOfContactsDao.FreightOfContactsObserver;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.dao.util.CRUD;
import com.epeisong.logistics.proto.Base.ProtoEBizLogisticsSubscribe;
import com.epeisong.logistics.proto.Eps;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.Freight;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.net.request.NetUserConfig;
import com.epeisong.net.request.OnNetRequestListenerImpl;
import com.epeisong.ui.adapter.FreightListAdapter;
import com.epeisong.ui.view.ChooseFreightTypeLayout;
import com.epeisong.ui.view.ChooseFreightTypeLayout.OnChooseFreightTypeListener;
import com.epeisong.ui.view.ChooseLineLayout;
import com.epeisong.ui.view.ChooseLineLayout.OnChooseLineListener;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 朋友的车源货源
 * @author Administrator
 *
 */
public class FreightOfContactsActivity extends HorizontalFilterActivity implements FreightOfContactsObserver,
        OnClickListener, OnChooseLineListener, OnChooseFreightTypeListener, OnItemLongClickListener {

    private TextView mTitleRightTv;

    private PopupWindow mPopupWindowMenu;
    private IconTextAdapter mIconTextAdapter;

    private ChooseFreightTypeLayout mChooseFreightTypeLayout;

    private ChooseLineLayout mChooseLineLayout;
    private FreightListAdapter mAdapter;
    private int mFreightType;

    private RegionResult mStartRegion;

    private RegionResult mEndRegion;
    private Freight refreshFreight;
    private ReceiveBroadCast receiveBroadCast;

    @Override
    public void onAttachFragment(Fragment fragment) {
        /** 注册广播 */
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        // filter.addAction("com.epeisong.ui.activity.SearchTheSourceSupplyOCarsActivity");
        // //只有持有相同的action的接受者才能接收此广播
        // filter.addAction("com.gasFragment"); // 只有持有相同的action的接受者才能接收此广播
        this.registerReceiver(receiveBroadCast, filter);
        super.onAttachFragment(fragment);
    }

    class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshFreight = (Freight) intent.getSerializableExtra("freshList");
            FreightOfContactsDao.getInstance().update(refreshFreight);
            FreightOfContactsDao.getInstance().queryAll();
            // ToastUtils.showToast(refreshFreight.getOrder_status()+"");
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        loadData();
        FreightOfContactsDao.getInstance().addObserver(this);
    }

    @Override
    public void onChoosedFreightType(String name, int type, boolean change) {
        if (change) {
            setFilterTitile(0, name);
            mFreightType = type;
            refreshData();
        }
        hideFilter(0);
    }

    @Override
    public void onChoosedLine(RegionResult start, RegionResult end) {
        if (start != null && end != null) {
            setFilterTitile(1, start.getShortNameFromDistrict() + "-" + end.getShortNameFromDistrict());
            mStartRegion = start;
            mEndRegion = end;
        } else {
            mStartRegion = null;
            mEndRegion = null;
            setFilterTitile(1, "线路不限");
        }
        refreshData();
        hideFilter(1);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
        // case R.id.iv_advisory:
        // Object tag = v.getTag();
        // if (tag != null && tag instanceof Freight) {
        // Freight f = (Freight) tag;
        // ChatRoomActivity.launch(this, f.getUser_id(),
        // ChatMsg.business_type_freight, f.getId());
        // }
        // break;
        }
    }

    @Override
    public void onFreightOfContactsChange(Freight foc, CRUD crud) {
        refreshData();
    }

    private void changeReceive(final int newStatus) {
        final User user = UserDao.getInstance().getUser();
        if (user.isReceive_contacts_freight() == newStatus) {
            return;
        }
        NetUserConfig net = new NetUserConfig(this) {

            @Override
            protected boolean onSetRequest(LogisticsReq.Builder req) {
                ProtoEBizLogisticsSubscribe.Builder value = ProtoEBizLogisticsSubscribe.newBuilder();
                value.setWhetherReceiveFreightsOfFriends(newStatus);
                req.setLogisticsSubscribe(value);
                return true;
            }
        };
        net.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {
            @Override
            public void onSuccess(CommonLogisticsResp.Builder response) {
                user.setReceive_contacts_freight(newStatus);
                UserDao.getInstance().replace(user);
                mTitleRightTv.setSelected(newStatus == User.CONFIG_RECEIVE_CONTACTS_FREIGHT_YES);
                if (newStatus == User.CONFIG_RECEIVE_CONTACTS_FREIGHT_YES) {
                    mTitleRightTv.setText("接收中");
                } else {
                    mTitleRightTv.setText("断开中");
                }
                mIconTextAdapter.getItem(0).setSelected(newStatus == User.CONFIG_RECEIVE_CONTACTS_FREIGHT_YES);
                mIconTextAdapter.getItem(1).setSelected(newStatus == User.CONFIG_RECEIVE_CONTACTS_FREIGHT_NO);
            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Freight f = mAdapter.getItem(position);
        String[] items = { "删除", "删除全部无效" };
        showListDialog(null, items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    FreightOfContactsDao.getInstance().delete(f);
                } else if (which == 1) {
                }
            }
        });
        return true;
    }

    private Action createAction() {
        return new ActionImpl() {

            @Override
            public void doAction(View v) {
                showMenuPopupWindow();
            }

            @Override
            public View getView() {
                mTitleRightTv = new TextView(getApplicationContext());
                mTitleRightTv.setBackgroundResource(R.drawable.selector_common_bg_green_red);
                mTitleRightTv.setTextColor(Color.WHITE);
                mTitleRightTv.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
                boolean receiving = UserDao.getInstance().getUser().isReceive_contacts_freight() == User.CONFIG_RECEIVE_CONTACTS_FREIGHT_YES;
                if (receiving) {
                    mTitleRightTv.setText("接收中");
                } else {
                    mTitleRightTv.setText("断开中");
                }
                mTitleRightTv.setSelected(receiving);
                return mTitleRightTv;
            }
        };
    }

    private void initPopupWindowMenu() {
        int received = UserDao.getInstance().getUser().isReceive_contacts_freight();
        List<IconTextItem> items = new ArrayList<IconTextItem>();
        items.add(new IconTextItem(R.drawable.selector_common_hook, "开始接收", null)
                .setSelected(received == User.CONFIG_RECEIVE_CONTACTS_FREIGHT_YES));
        items.add(new IconTextItem(R.drawable.selector_common_hook, "暂停接收", null)
                .setSelected(received == User.CONFIG_RECEIVE_CONTACTS_FREIGHT_NO));
        mIconTextAdapter = new IconTextAdapter(getApplicationContext(), 50) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                return super.getView(position, convertView, parent);
            }
        };
        mIconTextAdapter.setIconRight();
        mIconTextAdapter.replaceAll(items);
        ListView lv = new ListView(getApplicationContext());
        lv.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        lv.setAdapter(mIconTextAdapter);
        lv.setBackgroundColor(Color.argb(0xFF, 0x2D, 0x30, 0x37));
        mPopupWindowMenu = new PopupWindow(getApplicationContext());
        mPopupWindowMenu.setContentView(lv);
        mPopupWindowMenu.setWidth(EpsApplication.getScreenWidth() / 2);
        mPopupWindowMenu.setHeight(LayoutParams.WRAP_CONTENT);
        mPopupWindowMenu.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        mPopupWindowMenu.setFocusable(true);
        mPopupWindowMenu.setOutsideTouchable(true);
        mPopupWindowMenu.setAnimationStyle(R.style.popup_window_menu);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPopupWindowMenu.dismiss();

                if (position == 0) {
                    changeReceive(User.CONFIG_RECEIVE_CONTACTS_FREIGHT_YES);
                } else if (position == 1) {
                    changeReceive(User.CONFIG_RECEIVE_CONTACTS_FREIGHT_NO);
                }
            }
        });
        lv.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP
                        && mPopupWindowMenu.isShowing()) {
                    mPopupWindowMenu.dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    @SuppressLint("NewApi")
    private void loadData() {
        AsyncTask<Void, Void, List<Freight>> task = new AsyncTask<Void, Void, List<Freight>>() {

            @Override
            protected List<Freight> doInBackground(Void... params) {
                return FreightOfContactsDao.getInstance().queryAll();
            }

            @Override
            protected void onPostExecute(List<Freight> result) {
                if (result != null && result.size() > 0) {
                    mAdapter.replaceAll(result);
                }
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void refreshData() {
        AsyncTask<Void, Void, List<Freight>> task = new AsyncTask<Void, Void, List<Freight>>() {
            @Override
            protected List<Freight> doInBackground(Void... params) {
                int type = -1;
                if (mFreightType == OnChooseFreightTypeListener.FREIGHT_TYPE_GOODS) {
                    type = Freight.TYPE_GOODS;
                } else if (mFreightType == OnChooseFreightTypeListener.FREIGHT_TYPE_TRUCK) {
                    type = Freight.TYPE_TRUCK;
                }
                int startCode = -1, endCode = -1;
                if (mStartRegion != null && mEndRegion != null) {
                    startCode = mStartRegion.getCode();
                    endCode = mEndRegion.getCode();
                }
                return FreightOfContactsDao.getInstance().queryAll(type, startCode, endCode);
            }

            @Override
            protected void onPostExecute(List<Freight> result) {
                if (result != null && result.size() > 0) {
                    mAdapter.replaceAll(result);
                    return;
                } else {
                    mAdapter.clear();
                    ToastUtils.showToast("请仔细核对搜索信息");
                    return;
                }
            }
        };
        task.execute();
    }

    private void showMenuPopupWindow() {
        if (mPopupWindowMenu == null) {
            initPopupWindowMenu();
        }
        int statusBar = SystemUtils.getStatusBarHeight(this);
        int y = getResources().getDimensionPixelSize(R.dimen.custom_title_height) + statusBar + 1;
        mPopupWindowMenu.showAtLocation(mCustomTitle, Gravity.TOP | Gravity.RIGHT,
                (int) DimensionUtls.getPixelFromDp(10), y);
    }

    @Override
    protected TitleParams getTitleParams() {
        List<Action> actions = new ArrayList<Action>();
        actions.add(createAction());
        return new TitleParams(getDefaultHomeAction(), "朋友的车源货源", actions).setShowLogo(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mChooseLineLayout.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    @Override
    protected View onCreateContentView() {
        ListView lv = new ListView(getApplicationContext());
        lv.setCacheColorHint(Color.TRANSPARENT);
        lv.setBackgroundResource(R.color.white);
        // lv.setDivider(new ColorDrawable(0x000000));
        lv.setDividerHeight(0);
        lv.setAdapter(mAdapter = new FreightListAdapter());
        lv.setOnItemLongClickListener(this);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                Freight f = mAdapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), FreightDetailActivity.class);
                intent.putExtra(FreightDetailActivity.EXTRA_FREIGHT, f);
                BusinessChatModel model = new BusinessChatModel();
                model.setBusiness_type(ChatMsg.business_type_freight);
                model.setBusiness_id(f.getId());
                model.setBusiness_desc(f.getStart_region() + "-" + f.getEnd_region());
                model.setBusiness_extra(String.valueOf(f.getType()));
                model.setBusiness_owner_id(f.getUser_id());
                intent.putExtra(FreightDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
                intent.putExtra(FreightDetailActivity.EXTRA_CAN_DELETE, false);
                intent.putExtra("flag", "FreightOfContacts");
                startActivity(intent);
                return;
            }
        });
        return lv;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册广播
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.epeisong.ui.activity.FreightOfContacts"); // 只有持有相同的action的接受者才能接收此广播
        this.registerReceiver(receiveBroadCast, filter);
    }

    @Override
    protected Map<String, ? extends View> onCreateFilterTitle() {
        Map<String, View> map = new LinkedHashMap<String, View>();
        map.put("全部车源货源", mChooseFreightTypeLayout = new ChooseFreightTypeLayout(this));
        map.put("路线不限", mChooseLineLayout = new ChooseLineLayout(this));
        mChooseFreightTypeLayout.setOnChooseFreightTypeListener(this);
        mChooseLineLayout.setActivity(this);
        // mChooseLineLayout.setFilter(ChooseRegionActivity.FILTER_0_2);
        mChooseLineLayout.setOnChooseLineListener(this);
        return map;
    }

    @Override
    protected void onDestroy() {
        // 注销广播
        if (receiveBroadCast != null) {
            this.unregisterReceiver(receiveBroadCast);
        }
        super.onDestroy();
        FreightOfContactsDao.getInstance().removeObserver(this);
    }
}
