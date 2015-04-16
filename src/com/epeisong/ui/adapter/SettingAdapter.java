package com.epeisong.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;

public class SettingAdapter extends HoldDataBaseAdapter<SettingItem> {

    private Context mContext;

    public SettingAdapter(Context context) {
        mContext = context;
    }

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
        return getItem(position).getType() != SettingItem.type_invalid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SettingItem item = getItem(position);
        if (item.getType() == SettingItem.type_invalid) {
            int h = (int) DimensionUtls.getPixelFromDp(10);
            LinearLayout ll = new LinearLayout(mContext);
            ll.setOrientation(LinearLayout.VERTICAL);
            View v = new View(mContext);
            v.setLayoutParams(new LinearLayout.LayoutParams(-1, 1));
            v.setBackgroundColor(Color.argb(0xff, 0xe6, 0xe6, 0xe6));
            ll.addView(v);
            TextView tv = new TextView(mContext);
            if (item.getName() != null) {
                tv.setPadding(h, h, h, h / 2);
                tv.setText(item.getName());
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                tv.setTextColor(Color.GRAY);
            } else {
                tv.setLayoutParams(new LinearLayout.LayoutParams(-1, h));
            }
            ll.addView(tv);
            return ll;
        }
        SettingViewHolder holder = null;
        if (convertView == null) {
            holder = new SettingViewHolder();
            convertView = holder.createView();
            convertView.setTag(holder);
        } else {
            holder = (SettingViewHolder) convertView.getTag();
        }
        holder.fillData(position > 0, item);
        return convertView;
    }

    private class SettingViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
        ImageView iv_new;
        ImageView iv_arrow;
        View line;

        public View createView() {
            View view = SystemUtils.inflate(R.layout.fragment_mine_item);
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_desc = (TextView) view.findViewById(R.id.tv_desc);
            iv_new = (ImageView) view.findViewById(R.id.iv_point);
            iv_arrow = (ImageView) view.findViewById(R.id.iv_arrow);
            line = view.findViewById(R.id.line);
            return view;
        }

        public void fillData(boolean showLine, SettingItem item) {
            tv_desc.setVisibility(View.INVISIBLE);
            if (item.getIconId() > 0) {
                iv_icon.setVisibility(View.VISIBLE);
                iv_icon.setImageResource(item.getIconId());
            } else {
                iv_icon.setVisibility(View.GONE);
            }
            tv_name.setText(item.getName());
            if (item.isHasNewMsg()) {
                iv_new.setVisibility(View.VISIBLE);
            } else {
                iv_new.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(item.getDesc())) {
                tv_desc.setVisibility(View.VISIBLE);
                tv_desc.setText(item.getDesc());
            }
            if (item.getArrowIconId() > 0) {
                iv_arrow.setImageResource(item.getArrowIconId());
            }
            if (showLine) {
                line.setVisibility(View.VISIBLE);
            } else {
                line.setVisibility(View.GONE);
            }
        }
    }
}
