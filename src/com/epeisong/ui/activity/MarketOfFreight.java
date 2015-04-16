package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.epeisong.utils.android.AsyncTask;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.adapter.impl.IconTextAdapter;
import com.epeisong.base.adapter.item.IconTextItem;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.FreightOfContactsDao;
import com.epeisong.data.dao.FreightOfContactsDao.FreightOfContactsObserver;
import com.epeisong.model.Contacts;
import com.epeisong.model.Freight;
import com.epeisong.model.Market;
import com.epeisong.model.RegionResult;
import com.epeisong.ui.view.ChooseFreightTypeLayout;
import com.epeisong.ui.view.ChooseFreightTypeLayout.OnChooseFreightTypeListener;
import com.epeisong.ui.view.ChooseLineLayout;
import com.epeisong.ui.view.ChooseLineLayout.OnChooseLineListener;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
@Deprecated
public class MarketOfFreight extends BaseActivity implements OnClickListener, OnChooseLineListener,
        OnChooseFreightTypeListener {

    private class MyAdapter extends HoldDataBaseAdapter<Freight> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.activity_search_market_item);
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
        ImageView iv_freight_type;
        TextView tv_start_region;
        TextView tv_end_region;
        TextView tv_name;
        TextView tv_time;
        TextView tv_desc;

        public void fillData(Freight f) {
            if (f.getType() == Freight.TYPE_GOODS) {
                iv_freight_type.setImageResource(R.drawable.black_board_goods);
            } else {
                iv_freight_type.setImageResource(R.drawable.black_board_truck);
            }
            tv_start_region.setText(f.getStart_region());
            tv_end_region.setText(f.getEnd_region());
            Contacts c = ContactsDao.getInstance().queryById(f.getUser_id());
            if (c != null) {
                tv_name.setText(c.getShow_name());
            }
            tv_time.setText(DateUtil.long2vague(f.getUpdate_time()));
            tv_desc.setText(f.getDesc());
        }

        public void findView(View v) {
            iv_freight_type = (ImageView) v.findViewById(R.id.iv_freight_type);
            tv_start_region = (TextView) v.findViewById(R.id.tv_start_region);
            tv_end_region = (TextView) v.findViewById(R.id.tv_end_region);
            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_time = (TextView) v.findViewById(R.id.tv_time);
            tv_desc = (TextView) v.findViewById(R.id.tv_freight_desc);
        }
    }

    public static final String EXTRA_MARKET = "market";
    private Market mMarket;
    private TextView mTitleRightTv;

    private PopupWindow mPopupWindowMenu;
    private IconTextAdapter mIconTextAdapter;
    private TextView mChoosableTv01;
    private TextView mChoosableTv02;
    private View mUnderLine01;
    private View mUnderLine02;
    private View mChooseContainer;

    private ChooseFreightTypeLayout mChooseFreightTypeLayout;

    private ChooseLineLayout mChooseLineLayout;
    private MyAdapter mAdapter;
    private int mFreightType;

    private RegionResult mStartRegion;

    private RegionResult mEndRegion;

    @Override
    public void onChoosedFreightType(String name, int type, boolean change) {
        if (type != -1) {
            mChoosableTv01.setText(name);
            mFreightType = type;
            refreshData();
        }
        hideChooseFreightType();
    }

    @Override
    public void onChoosedLine(RegionResult start, RegionResult end) {
        if (start != null && end != null) {
            mChoosableTv02.setText(start.getFullName() + "-" + end.getFullName());
            mStartRegion = start;
            mEndRegion = end;
        } else {
            mStartRegion = null;
            mEndRegion = null;
            mChoosableTv02.setText("线路不限");
        }
        refreshData();
        hideChooseLine();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.fl_choose_container:
            mChooseContainer.setVisibility(View.GONE);
            hideChooseFreightType();
            hideChooseLine();
            break;
        case R.id.fl_choosable_01:
            if (mChooseFreightTypeLayout.getVisibility() == View.GONE) {
                showChooseFreightType();
            } else {
                hideChooseFreightType();
            }
            break;
        case R.id.fl_choosable_02:
            if (mChooseLineLayout.getVisibility() == View.GONE) {
                showChooseLine();
            } else {
                hideChooseLine();
            }
            break;
        }
    }

    private void hideChooseFreightType() {
        mChooseContainer.setVisibility(View.GONE);
        mChooseFreightTypeLayout.setVisibility(View.GONE);
        mChoosableTv01.setSelected(false);
        mUnderLine01.setSelected(false);
    }

    private void hideChooseLine() {
        mChooseContainer.setVisibility(View.GONE);
        mChooseLineLayout.setVisibility(View.GONE);
        mChoosableTv02.setSelected(false);
        mUnderLine02.setSelected(false);
    }

    private void initPopupWindowMenu() {

        List<IconTextItem> items = new ArrayList<IconTextItem>();

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

    private void initView() {
        View view01 = findViewById(R.id.fl_choosable_01);
        mChoosableTv01 = (TextView) view01.findViewById(R.id.tv_choosable);
        mChoosableTv01.setText("全部车源货源");
        mUnderLine01 = view01.findViewById(R.id.view_under_line);
        view01.setOnClickListener(this);
        View view02 = findViewById(R.id.fl_choosable_02);
        mChoosableTv02 = (TextView) view02.findViewById(R.id.tv_choosable);
        mChoosableTv02.setText("线路不限");
        mUnderLine02 = view02.findViewById(R.id.view_under_line);
        view02.setOnClickListener(this);

        mChooseContainer = findViewById(R.id.fl_choose_container);
        mChooseContainer.setOnClickListener(this);
        mChooseFreightTypeLayout = (ChooseFreightTypeLayout) findViewById(R.id.choose_freight_type_layout);
        mChooseFreightTypeLayout.setOnChooseFreightTypeListener(this);
        mChooseLineLayout = (ChooseLineLayout) findViewById(R.id.choose_line_layout);
        mChooseLineLayout.setActivity(this);
        mChooseLineLayout.setOnChooseLineListener(this);

        ListView lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(mAdapter = new MyAdapter());
    }

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
        task.execute();
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
                mAdapter.replaceAll(result);
            }
        };
        task.execute();
    }

    private void showChooseFreightType() {
        hideChooseLine();
        mChooseContainer.setVisibility(View.VISIBLE);
        mChooseFreightTypeLayout.setVisibility(View.VISIBLE);
        mChoosableTv01.setSelected(true);
        mUnderLine01.setSelected(true);
    }

    private void showChooseLine() {
        hideChooseFreightType();
        mChooseContainer.setVisibility(View.VISIBLE);
        mChooseLineLayout.setVisibility(View.VISIBLE);
        mChoosableTv02.setSelected(true);
        mUnderLine02.setSelected(true);
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
        return new TitleParams(getDefaultHomeAction(), "逛逛配货市场", null).setShowLogo(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mChooseLineLayout.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mMarket = (Market) getIntent().getSerializableExtra(EXTRA_MARKET);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_market);
        initView();
        loadData();
        FreightOfContactsDao.getInstance().addObserver((FreightOfContactsObserver) this);
    }
}
