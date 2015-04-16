package com.epeisong.ui.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.LogisticsProducts;
import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.logistics.common.Properties;
import com.epeisong.utils.DimensionUtls;

/**
 * 按物流类型选择分类
 * @author poet
 *
 */
public class ChooseLogisticsTypeLayout extends RelativeLayout {

    public static final int TYPE_TRANSPORT = -100; // 运输
    public static final int TYPE_EXPRESS = -200; // 快递
    public static final int TYPE_SPECIAL = -400; // 特种运输
    public static final int TYPE_OTHER = -500; // 其他

    int selectedColor = Color.argb(0xff, 0xe5, 0xe7, 0xed);

    List<LogisticsType> data;

    ListView mListView1, mListView2;
    MyAdapter adapter1;
    MyAdapter adapter2;

    int selectedPos1, selectedPos2 = -1;

    int width = DimensionUtls.getPixelFromDpInt(120);

    OnChooseLogisticsTypeListener listener;

    public ChooseLogisticsTypeLayout(Context context) {
        super(context);
        init(context);
    }

    public ChooseLogisticsTypeLayout setChoosedType(LogisticsType type) {
        if (type == null) {
            return this;
        }
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).name.equals(type.name)) {
                selectedPos1 = i;
            } else if (data.get(i).children != null) {
                for (LogisticsType item : data.get(i).children) {
                    if (item.name.equals(type.name)) {
                        selectedPos1 = i;
                    }
                }
            }
        }
        return this;
    }

    void init(final Context context) {
        setBackgroundColor(Color.argb(0x88, 0, 0, 0));
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callListener(null);
            }
        });

        data = new ArrayList<LogisticsType>();
        data.add(new LogisticsType(-1, "全部"));
        data.add(new LogisticsType("运输", createChildren(TYPE_TRANSPORT)));
        data.add(new LogisticsType(Properties.LOGISTIC_TYPE_EXPRESS, "快递"));
        data.add(new LogisticsType(Properties.LOGISTIC_TYPE_STORAGE, "仓储"));
        data.add(new LogisticsType("特种运输", createChildren(TYPE_SPECIAL)));
        data.add(new LogisticsType("其他", createChildren(TYPE_OTHER)));

        mListView1 = new ListView(context);
        mListView1.setSelector(R.drawable.selector_item_white_gray);
        mListView1.setBackgroundColor(Color.WHITE);
        mListView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView1.setOnItemClickListener(new AbsListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPos1 = position;
                selectedPos2 = -1;
                adapter1.notifyDataSetChanged();
                LogisticsType item = adapter1.getItem(position);
                if (item.children == null) {
                    callListener(item);
                    if (mListView2 != null) {
                        mListView2.setVisibility(View.GONE);
                    }
                } else {
                    if (adapter2 == null) {
                        mListView2 = new ListView(context);
                        mListView2.setBackgroundColor(Color.WHITE);
                        mListView2.setSelector(R.drawable.selector_item_white_gray);
                        mListView2.setOnItemClickListener(new AbsListView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                selectedPos2 = position;
                                adapter2.notifyDataSetChanged();
                                callListener(adapter2.getItem(position));
                            }
                        });
                        mListView2.setAdapter(adapter2 = new MyAdapter());
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, -2);
                        params.leftMargin = width;
                        addView(mListView2, params);
                    } else {
                        mListView2.setVisibility(View.VISIBLE);
                    }
                    adapter2.replaceAll(item.children);
                }
            }
        });
        mListView1.setAdapter(adapter1 = new MyAdapter());
        adapter1.replaceAll(data);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, -2);
        this.addView(mListView1, params);
    }

    List<LogisticsType> createChildren(int type) {
        List<LogisticsType> children = new ArrayList<LogisticsType>();
        // children.add(new LogisticsType(type, "全部"));
        switch (type) {
        case TYPE_TRANSPORT:
            children.add(new LogisticsType(Properties.LOGISTIC_TYPE_LOGISTICS_PARK, "物流园"));
            children.add(new LogisticsType(Properties.LOGISTIC_TYPE_TRANSHIP_GOODS, "驳货"));
            children.add(new LogisticsType(Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE, "整车运输"));
            children.add(new LogisticsType(Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE, "零担专线"));
            children.add(new LogisticsType(Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS, "第三方物流"));
            children.add(new LogisticsType(Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT, "配载信息部"));
            break;
        case TYPE_EXPRESS:
            children.add(new LogisticsType(Properties.LOGISTIC_TYPE_EXPRESS, "快递"));
            children.add(new LogisticsType(Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION, "同城配送"));
            children.add(new LogisticsType(Properties.LOGISTIC_TYPE_COURIER, "快递员"));
            children.add(new LogisticsType(Properties.LOGISTIC_TYPE_PICK_UP_POINT, "收发点"));
            break;
        case TYPE_SPECIAL:
            children.add(new LogisticsType(LogisticsProducts.PRODUCTS_LARGETRANSPORT, "大件运输", false));
            children.add(new LogisticsType(LogisticsProducts.PRODUCTS_DANGEROUS, "危险品", false));
            children.add(new LogisticsType(LogisticsProducts.PRODUCTS_REFRIGERATED, "冷藏品", false));
            children.add(new LogisticsType(LogisticsProducts.PRODUCTS_FRESHPERISHABLE, "鲜活易腐", false));
            break;
        case TYPE_OTHER:
            children.add(new LogisticsType(Properties.LOGISTIC_TYPE_LOAD_UNLOAD, "装卸"));
            children.add(new LogisticsType(Properties.LOGISTIC_TYPE_MOVE_HOUSE, "搬家"));
            children.add(new LogisticsType(Properties.LOGISTIC_TYPE_PACKAGING, "包装"));
            children.add(new LogisticsType(Properties.LOGISTIC_TYPE_PARKING_LOT, "停车场"));
            children.add(new LogisticsType(Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING, "设备租赁"));
            children.add(new LogisticsType(Properties.LOGISTIC_TYPE_VEHICLE_REPAIR, "汽车修理"));
            break;
        }
        return children;
    }

    void callListener(LogisticsType type) {
        if (listener != null) {
            listener.onChoosedLogisticsType(type);
        }
    }

    public void setListener(OnChooseLogisticsTypeListener l) {
        listener = l;
    }

    class MyAdapter extends HoldDataBaseAdapter<LogisticsType> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = holder.createView(getContext());
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.fillData(this, position);
            return convertView;
        }
    }

    class ViewHolder {
        RelativeLayout root;
        TextView tv;
        ImageView iv_arrow;

        int p10 = DimensionUtls.getPixelFromDpInt(10);

        View createView(Context context) {
            root = new RelativeLayout(context);
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(p10 / 5, -1);
            p.topMargin = p.bottomMargin = p10 / 2;
            p.leftMargin = 1;
            tv = new TextView(context);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setTextColor(Color.BLACK);
            tv.setPadding(p10, p10, p10, p10);
            ll.addView(tv);
            root.addView(ll);
            iv_arrow = new ImageView(context);
            iv_arrow.setImageResource(R.drawable.icon_arrow_right_small);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.rightMargin = p10;
            root.addView(iv_arrow, params);
            return root;
        }

        void fillData(MyAdapter adapter, int pos) {
            LogisticsType item = adapter.getItem(pos);
            root.setBackgroundColor(Color.TRANSPARENT);
            tv.setTextColor(Color.BLACK);
            tv.setText(item.name);
            if ((adapter == adapter1 && pos == selectedPos1) || (adapter == adapter2 && pos == selectedPos2)) {
                root.setBackgroundColor(selectedColor);
                tv.setTextColor(Color.argb(0xff, 0x1d, 0x5c, 0xeb));
            }
            if (adapter == adapter2) {
                root.setBackgroundColor(selectedColor);
            }
            if (item.children == null || item.children.isEmpty()) {
                iv_arrow.setVisibility(View.GONE);
            } else {
                iv_arrow.setVisibility(View.VISIBLE);
            }
        }
    }

    public static class LogisticsType implements Serializable {
        private static final long serialVersionUID = -5973980372699713748L;

        public int role_type;
        public int goods_type;
        public String name;

        List<LogisticsType> children;
        public LogisticsType parent;

        public LogisticsType(String name, List<LogisticsType> children) {
            this.name = name;
            this.children = children;
            for (LogisticsType type : children) {
                type.parent = this;
            }
        }

        public LogisticsType(int type, String name) {
            this(type, name, true);
        }

        public LogisticsType(int type, String name, boolean isRole) {
            if (isRole) {
                this.role_type = type;
            } else {
                this.goods_type = type;
            }
            this.name = name;
        }

        public boolean hasLine() {
            switch (role_type) {
            case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
            case Properties.LOGISTIC_TYPE_EXPRESS:
            case Properties.LOGISTIC_TYPE_COURIER:
            case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:
            case Properties.LOGISTIC_TYPE_MOVE_HOUSE:
            case Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS:
                return true;
            }
            switch (goods_type) {
            case LogisticsProducts.PRODUCTS_DANGEROUS:
            case LogisticsProducts.PRODUCTS_FRESHPERISHABLE:
            case LogisticsProducts.PRODUCTS_LARGETRANSPORT:
            case LogisticsProducts.PRODUCTS_REFRIGERATED:
                return true;
            }
            return false;
        }
    }

    public interface OnChooseLogisticsTypeListener {
        void onChoosedLogisticsType(LogisticsType type);
    }
}
