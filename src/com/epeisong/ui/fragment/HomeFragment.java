package com.epeisong.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bdmap.epsloc.EpsLocation;
import com.bdmap.epsloc.EpsLocationHolder;
import com.bdmap.epsloc.EpsLocationHolder.EpsLocationHolderObserver;
import com.bdmap.epsloc.EpsLocationRequestor;
import com.bdmap.epsloc.EpsLocationRequestor.OnEpsLocationListener;
import com.bdmap.impl.NearByLogisticsMapActivity;
import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.data.dao.RegionDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.Region;
import com.epeisong.model.RegionResult;
import com.epeisong.speech.tts.TTSServiceFactory;
import com.epeisong.ui.activity.BlackBoardActivity;
import com.epeisong.ui.activity.EntireVehicleActivity;
import com.epeisong.ui.activity.MoreActivity;
import com.epeisong.ui.activity.SearchFreightActivity;
import com.epeisong.utils.SpUtilsCur;
import com.epeisong.utils.SpUtilsCur.KEYS_NOTIFY;
import com.epeisong.utils.SpUtilsCur.SpListener;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

public class HomeFragment extends Fragment implements OnItemClickListener, OnClickListener, SpListener,
        EpsLocationHolderObserver {

    private List<Item> mItems;

    private GridViewAdapter mGridAdapter;

    private TextView mRegionTv;
    private ImageView mTtsIv;

    EpsLocation mEpsLocation;
    RegionResult mRegionResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return SystemUtils.inflate(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRegionTv = (TextView) view.findViewById(R.id.tv_region_name);
        mRegionTv.setOnClickListener(this);
        mTtsIv = (ImageView) view.findViewById(R.id.iv_tts);
        mTtsIv.setOnClickListener(this);
        onSpChange(SpUtilsCur.KEYS_NOTIFY.BOOL_OPEN_TTS);
        SpUtilsCur.registerListener(SpUtilsCur.KEYS_NOTIFY.BOOL_OPEN_TTS, this);
        SpUtilsCur.registerListener(SpUtilsCur.KEYS_NOTIFY.BOOL_NO_DISTURB, this);

        mItems = new ArrayList<HomeFragment.Item>();
        mItems.add(new Item("整车运输", R.drawable.home_ftl).setUserTypeCode(Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE));
        mItems.add(new Item("零担专线", R.drawable.home_lcl)
                .setUserTypeCode(Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE));
        mItems.add(new Item("快递", R.drawable.home_fast_mail).setUserTypeCode(Properties.LOGISTIC_TYPE_EXPRESS));
        mItems.add(new Item("同城配送", R.drawable.more_citydistribution)
                .setUserTypeCode(Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION));
        mItems.add(new Item("第三方物流", R.drawable.home_third_part)
                .setUserTypeCode(Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS));
        mItems.add(new Item("配载信息部", R.drawable.home_information)
                .setUserTypeCode(Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT));
        mItems.add(new Item("物流园", R.drawable.icon_logistics_park)
                .setUserTypeCode(Properties.LOGISTIC_TYPE_LOGISTICS_PARK));
        mItems.add(new Item("更多", R.drawable.home_more).setClass(MoreActivity.class));

        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setAdapter(mGridAdapter = new GridViewAdapter());
        gridView.setOnItemClickListener(this);
        mGridAdapter.replaceAll(mItems);

        view.findViewById(R.id.btn_publish_freight).setOnClickListener(this);
        view.findViewById(R.id.btn_search_freight).setOnClickListener(this);
        view.findViewById(R.id.ll_nearby_logistics).setOnClickListener(this);

        EpsLocation epsLocation = EpsLocationHolder.getEpsLocation();
        if (epsLocation != null) {
            onReceiveEpsLocation(epsLocation);
        }

        EpsLocationHolder.addEpsLocationHolderObserver(this);
    }

    void refreshEpsLocation() {
        mRegionTv.setText("定位中");
        EpsLocationRequestor requestor = new EpsLocationRequestor();
        requestor.requestEpsLocation(new OnEpsLocationListener() {
            @Override
            public void onEpsLocation(EpsLocation epsLocation) {
                onReceiveEpsLocation(epsLocation);
            }
        });
    }

    @Override
    public void onDestroy() {
        EpsLocationHolder.removeEpsLocationHolderObserver(this);
        super.onDestroy();
    }

    @Override
    public void onEpsLocationHolderChange(EpsLocation epsLocation) {
        onReceiveEpsLocation(epsLocation);
    }

    void onReceiveEpsLocation(EpsLocation epsLocation) {
        mEpsLocation = epsLocation;
        if (mEpsLocation != null && !TextUtils.isEmpty(mEpsLocation.getCityName())) {
            mRegionTv.setText(mEpsLocation.getCityName());
            if (mRegionResult == null || mRegionResult.getCityName() == null
                    || !mRegionResult.getCityName().equals(mEpsLocation.getCityName())) {
                Region region = RegionDao.getInstance().queryByCityName(mEpsLocation.getCityName());
                mRegionResult = RegionDao.convertToResult(region);
            }
        } else {
            mRegionTv.setText("定位失败");
        }
    }

    @Override
    public void onSpChange(String key) {
        if (SpUtilsCur.KEYS_NOTIFY.BOOL_OPEN_TTS.equals(key) || SpUtilsCur.KEYS_NOTIFY.BOOL_NO_DISTURB.equals(key)) {
            if (SpUtilsCur.getBoolean(SpUtilsCur.KEYS_NOTIFY.BOOL_OPEN_TTS, true)) {
                if (SpUtilsCur.getBoolean(SpUtilsCur.KEYS_NOTIFY.BOOL_NO_DISTURB, false)) {
                    mTtsIv.setImageResource(R.drawable.icon_tts_on);
                } else {
                    mTtsIv.setImageResource(R.drawable.icon_tts_on);
                }
            } else {
                mTtsIv.setImageResource(R.drawable.icon_tts_off);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.iv_tts:
            boolean open = SpUtilsCur.getBoolean(KEYS_NOTIFY.BOOL_OPEN_TTS, true);
            SpUtilsCur.put(KEYS_NOTIFY.BOOL_OPEN_TTS, !open);
            if (open) {
                TTSServiceFactory.getInstance().clear();
            }
            break;
        case R.id.tv_region_name:
            if (mRegionTv.getText().equals("定位失败")) {
                refreshEpsLocation();
            }
            break;
        case R.id.btn_publish_freight:
            Intent intent = new Intent(getActivity(), BlackBoardActivity.class);
            startActivity(intent);
            break;
        case R.id.btn_search_freight:
            Intent intent2 = new Intent(getActivity(), SearchFreightActivity.class);
            int regionCode = 3201;
            if (mEpsLocation != null && mEpsLocation.getCityCode() > 0) {
                regionCode = mEpsLocation.getCityCode();
            }
            intent2.putExtra(SearchFreightFragment.EXTRA_REGION_CODE, regionCode);
            startActivity(intent2);
            break;
        case R.id.ll_nearby_logistics:
            Intent intent3 = new Intent(getActivity(), NearByLogisticsMapActivity.class);
            startActivity(intent3);
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = mGridAdapter.getItem(position);
        if (item.clazz != null) {
            Intent i = new Intent(getActivity(), item.clazz);
            startActivity(i);
            return;
        }
        Intent i = new Intent(getActivity(), EntireVehicleActivity.class);
        if (item.userTypeCode > 0) {
            i.putExtra(String.valueOf(R.string.usertypenum), item.userTypeCode);
        } else if (item.goodsTypeCode > 0) {
            i.putExtra(String.valueOf(R.string.producttypenum), item.goodsTypeCode);
        } else {
            ToastUtils.showToast("参数错误");
            return;
        }
        if (mRegionResult != null) {
            i.putExtra(EntireVehicleActivity.EXTRA_REGION_RESULT, mRegionResult);
        }
        startActivity(i);
    }

    private class GridViewAdapter extends HoldDataBaseAdapter<Item> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.fragment_home_gridview_item);
                holder = new GridViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (GridViewHolder) convertView.getTag();
            }
            holder.fillData(getItem(position));
            return convertView;
        }
    }

    private class GridViewHolder {
        ImageView iv;
        TextView tv;

        public void findView(View v) {
            iv = (ImageView) v.findViewById(R.id.iv);
            tv = (TextView) v.findViewById(R.id.tv);
        }

        public void fillData(Item item) {
            if (item.iconId > 0) {
                iv.setImageResource(item.iconId);
            }
            tv.setText(item.name);
        }
    }

    public static class Item {
        public int iconId;
        public String name;
        public int userTypeCode;
        public int goodsTypeCode;
        public Class<? extends Activity> clazz;

        public Item(String name, int iconId) {
            super();
            this.name = name;
            this.iconId = iconId;
        }

        public Item setUserTypeCode(int userTypeCode) {
            this.userTypeCode = userTypeCode;
            return this;
        }

        public Item setGoodsTypeCode(int code) {
            this.goodsTypeCode = code;
            return this;
        }

        public Item setClass(Class<? extends Activity> clazz) {
            this.clazz = clazz;
            return this;
        }
    }
}
