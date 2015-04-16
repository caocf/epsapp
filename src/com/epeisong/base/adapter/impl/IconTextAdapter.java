package com.epeisong.base.adapter.impl;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.adapter.item.IconTextItem;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.ToastUtils;

/**
 * 自动处理 图标+文字 的条目项的adapter
 * 
 * @author poet
 * 
 */
public class IconTextAdapter extends HoldDataBaseAdapter<IconTextItem> {

    private Context mContext;
    private int mItemHeight;
    private boolean mIconLeft = true;
    private int mItemBgResId;
    private Integer mTextColor;
    private float mTextSize;

    public IconTextAdapter(Context context, int itemHeightInDp) {
        mContext = context;
        mItemHeight = (int) DimensionUtls.getPixelFromDp(itemHeightInDp);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = holder.createView(mContext);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.fillData(getItem(position));
        return convertView;
    }

    public void setIconRight() {
        mIconLeft = false;
    }

    public void setItemBgResId(int id) {
        mItemBgResId = id;
    }

    public void setTextColor(int colorId) {
        mTextColor = colorId;
    }
    
    public void setTextSize(float TextSize){
    	mTextSize = TextSize;
    }

    private class ViewHolder {
        ImageView iv;
        TextView tv;

        public View createView(Context context) {
            LinearLayout ll = new LinearLayout(context);
            if (mItemBgResId > 0) {
                ll.setBackgroundResource(mItemBgResId);
            }
            ll.setLayoutParams(new AbsListView.LayoutParams(-1, mItemHeight));
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setGravity(Gravity.CENTER_VERTICAL);
            int w = (int) (mItemHeight - DimensionUtls.getPixelFromDp(20));
            LinearLayout.LayoutParams pIv = new LayoutParams(w, w);
            LinearLayout.LayoutParams pTv = new LayoutParams(-2, -2);
            pTv.leftMargin = (int) DimensionUtls.getPixelFromDp(10);
            pIv.leftMargin = (int) DimensionUtls.getPixelFromDp(10);
            iv = new ImageView(context);
            iv.setScaleType(ScaleType.CENTER_CROP);
            tv = new TextView(context);
            if (mTextColor != null) {
                tv.setTextColor(mTextColor);
            } else {
                tv.setTextColor(Color.WHITE);
            }
            if(mTextSize > 0){
            	tv.setTextSize(mTextSize);
            }else{
            	tv.setTextSize(14);
            }
            if (mIconLeft) {
                ll.addView(iv, pIv);
                ll.addView(tv, pTv);
            } else {
                ll.addView(tv, pTv);
                pIv.leftMargin = (int) DimensionUtls.getPixelFromDp(20);
                ll.addView(iv, pIv);
            }
            return ll;
        }

        public void fillData(IconTextItem item) {
            if (item.getIconResId() == 0) {
                iv.setVisibility(View.GONE);
            } else {
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(item.getIconResId());
                if (item.isSelectable()) {
                    iv.setSelected(item.isSelected());
                }
            }
            tv.setText(item.getName());
        }
    }
}
