package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bdmap.epsloc.EpsLocation;
import com.bdmap.epsloc.EpsLocationHolder;
import com.epeisong.EpsApplication;
import com.epeisong.LogisticsProducts;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.ui.fragment.HomeFragment.Item;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 更多 - 
 * @author Jack
 *
 */
public class MoreActivity extends BaseActivity implements OnItemClickListener {

    private GridView mGridView;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGridView = new GridView(this);
        mGridView.setNumColumns(4);
        int p = DimensionUtls.getPixelFromDpInt(10);
        mGridView.setPadding(0, p, 0, 0);
        mGridView.setSelector(R.color.transparent);
        mGridView.setBackgroundColor(Color.WHITE);
        mGridView.setAdapter(mAdapter = new MyAdapter());
        mGridView.setOnItemClickListener(this);
        setContentView(mGridView);

        List<Item> items = new ArrayList<Item>();
        items.add(new Item("仓储", R.drawable.more_storage).setUserTypeCode(Properties.LOGISTIC_TYPE_STORAGE));
        items.add(new Item("包装", R.drawable.more_packaging).setUserTypeCode(Properties.LOGISTIC_TYPE_PACKAGING));
        items.add(new Item("搬家", R.drawable.more_move_house).setUserTypeCode(Properties.LOGISTIC_TYPE_MOVE_HOUSE));
        items.add(new Item("设备租赁", R.drawable.home_device_lease)
                .setUserTypeCode(Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING));
        // items.add(new Item("快递员",
        // R.drawable.more_courier).setUserTypeCode(Properties.LOGISTIC_TYPE_COURIER));
        items.add(new Item("危险品", R.drawable.more_dangerous).setGoodsTypeCode(LogisticsProducts.PRODUCTS_DANGEROUS));
        items.add(new Item("冷藏品", R.drawable.more_refrigerated)
                .setGoodsTypeCode(LogisticsProducts.PRODUCTS_REFRIGERATED));
        items.add(new Item("鲜活易腐", R.drawable.more_freshperishable)
                .setGoodsTypeCode(LogisticsProducts.PRODUCTS_FRESHPERISHABLE));
        items.add(new Item("大件运输", R.drawable.more_largetransport)
                .setGoodsTypeCode(LogisticsProducts.PRODUCTS_LARGETRANSPORT));
        items.add(new Item("保险", R.drawable.home_insurance).setUserTypeCode(Properties.LOGISTIC_TYPE_INSURANCE));
        if (EpsApplication.DEBUGGING) {
            items.add(new Item("车源货源", R.drawable.home_search_freight)
                    .setClass(SearchTheSourceSupplyOCarsActivity.class));
        } else if (EpsApplication.DEBUGGING) {
            User user = UserDao.getInstance().getUser();
            if (user != null) {
                switch (user.getUser_type_code()) {
                case Properties.LOGISTIC_TYPE_MARKET:
                case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
                case Properties.LOGISTIC_TYPE_PLATFORM_CUSTOMER_SERVICE:
                    items.add(new Item("车源货源", R.drawable.home_search_freight)
                            .setClass(SearchTheSourceSupplyOCarsActivity.class));
                    break;

                default:
                    break;
                }
            }
        }

        mAdapter.addAll(items);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "更多", null).setShowLogo(false);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = mAdapter.getItem(position);
        if (item.clazz != null) {
            Intent i = new Intent(this, item.clazz);
            startActivity(i);
            return;
        }
        Intent i = new Intent(this, EntireVehicleActivity.class);
        if (item.userTypeCode > 0) {
            i.putExtra(String.valueOf(R.string.usertypenum), item.userTypeCode);
        } else if (item.goodsTypeCode > 0) {
            i.putExtra(String.valueOf(R.string.producttypenum), item.goodsTypeCode);
        } else {
            ToastUtils.showToast("参数错误");
            return;
        }
        EpsLocation epsLocation = EpsLocationHolder.getEpsLocation();
        if (epsLocation != null) {
            RegionResult result = epsLocation.convertToResult();
            if (result != null) {
                i.putExtra(EntireVehicleActivity.EXTRA_REGION_RESULT, result);
            }
        }
        startActivity(i);
    }

    private class MyAdapter extends HoldDataBaseAdapter<Item> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.fragment_home_gridview_item);
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

}
