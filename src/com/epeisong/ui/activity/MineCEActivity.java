package com.epeisong.ui.activity;

//import java.util.ArrayList;
//import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.epeisong.R;
//import com.epeisong.R.id;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;

/**
 * 我的保证
 * @author Jack
 *
 */
public class MineCEActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
    private ListView mListView;
    private MyAdapter mAdapter;
    private ImageView iv_minegua;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myce);
        iv_minegua = (ImageView) findViewById(R.id.iv_minegua);
        iv_minegua.setOnClickListener(this);
//        mListView = (ListView) findViewById(R.id.lv_myce_balance);
//        mListView.setOnItemClickListener(this);
//        List<Item> data = new ArrayList<MineCEActivity.Item>();
//        data.add(new Item(R.drawable.mine_wallet_guarantee, "订车配货保证金", new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(MineCEActivity.this, WalletGuaranteeActivity.class);
//                startActivity(intent);
//            }
//        }));
//        
//        mAdapter = new MyAdapter();
//        mListView.setAdapter(mAdapter);
//        mAdapter.replaceAll(data);
    }
    
    @Override
    public void onClick(View arg0) {
    	switch (arg0.getId()) {
		case R.id.iv_minegua:
			Intent intent = new Intent(MineCEActivity.this, WalletGuaranteeActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= mListView.getHeaderViewsCount();

        if (position >= 0) {
            Item item = mAdapter.getItem(position);
            if (item.getRunnable() != null) {
                view.post(item.getRunnable());
            }
        }
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "我的保证").setShowLogo(false);
    }
    
    private class MyAdapter extends HoldDataBaseAdapter<Item> {
        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getType();
        }

        @Override
        public boolean isEnabled(int position) {
            return getItem(position).getType() != Item.type_invalid;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Item last = null;
            if (position > 0) {
                last = getItem(position - 1);
            }
            Item item = getItem(position);
            if (item.getType() == Item.type_invalid) {
                View v = new View(getApplicationContext());
                int h = (int) DimensionUtls.getPixelFromDp(15);
                v.setLayoutParams(new AbsListView.LayoutParams(-1, h));
                v.setBackgroundColor(Color.argb(0xED, 0xED, 0xED, 0xED));
                return v;
            }
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.fragment_mine_item);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            boolean hideLine = last != null && last.getType() == Item.type_invalid;
            holder.fillData(!hideLine, item);
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
        ImageView iv_new;
        View line;

        public void findView(View v) {
            iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_desc = (TextView) v.findViewById(R.id.tv_desc);
            iv_new = (ImageView) v.findViewById(R.id.iv_point);
            line = v.findViewById(R.id.line);

        }

        public void fillData(boolean showLine, Item item) {
            tv_desc.setVisibility(View.INVISIBLE);
            iv_icon.setImageResource(item.getResId());
            tv_name.setText(item.getName());
            if (item.hasNewMsg) {
                iv_new.setVisibility(View.VISIBLE);
            } else {
                iv_new.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(item.getDesc())) {
                tv_desc.setVisibility(View.VISIBLE);
                tv_desc.setText(item.getDesc());
            }
            if (showLine) {
                line.setVisibility(View.VISIBLE);
            } else {
                line.setVisibility(View.GONE);
            }
        }
    }

    public static class Item {

        public static final int type_invalid = -1;
        public static final int type_normal = 0;

        private int type;
        private int resId;
        private String name;
        private String desc;
        private Runnable runnable;
        private boolean hasNewMsg;

        public Item() {
            this.type = type_invalid;
        }

        public Item(int resId, String name, Runnable runnable) {
            super();
            this.type = type_normal;
            this.resId = resId;
            this.name = name;
            this.runnable = runnable;
        }

        public int getType() {
            return type;
        }

        public Item setType(int type) {
            this.type = type;
            return this;
        }

        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public Item setDesc(String desc) {
            this.desc = desc;
            return this;
        }

        public Runnable getRunnable() {
            return runnable;
        }

        public Item setRunnable(Runnable runnable) {
            this.runnable = runnable;
            return this;
        }

        public boolean isHasNewMsg() {
            return hasNewMsg;
        }

        public Item setHasNewMsg(boolean hasNewMsg) {
            this.hasNewMsg = hasNewMsg;
            return this;
        }
    }

}
