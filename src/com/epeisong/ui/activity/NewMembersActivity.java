package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.AdjustHeightListView;
import com.epeisong.base.view.TitleParams;
import com.epeisong.model.Contacts;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 新会员请求页面
 * @author gnn
 *
 */
public class NewMembersActivity extends BaseActivity implements OnItemClickListener , OnItemLongClickListener {
	private ListView mListView;
	private HeadAdapter mHeadAdapter;
	private View mHeadView;
	private ExpandableListView mExpandableListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_memeber);
		mListView = (ListView)findViewById(R.id.elv);
		List<HeadItem> items = new ArrayList<HeadItem>();
        items.add(new HeadItem(R.drawable.icon_contacts_phone_contacts, "南京小夏信息部","我是小王"));
        items.add(new HeadItem(R.drawable.icon_contacts_phone_contacts, "南京小赵信息部","对方请求成为会员"));
        
        mHeadAdapter = new HeadAdapter();
        mHeadAdapter.addAll(items);
		mListView.setAdapter(mHeadAdapter);
		mListView.setOnItemLongClickListener(this);
//		setContentView(R.layout.activity_new_memeber);
//        mExpandableListView = (ExpandableListView)findViewById(R.id.elv);
//        mExpandableListView.setGroupIndicator(null);
//        
//        if (mExpandableListView.getHeaderViewsCount() == 0) {
//        	List<HeadItem> items = new ArrayList<HeadItem>();
//            items.add(new HeadItem(R.drawable.icon_contacts_phone_contacts, "南京小夏信息部","我是小王"));
//            items.add(new HeadItem(R.drawable.icon_contacts_phone_contacts, "南京小赵信息部","对方请求成为会员"));
//            
//            mHeadAdapter = new HeadAdapter();
//            mHeadAdapter.replaceAll(items);
//            AdjustHeightListView lv = new AdjustHeightListView(EpsApplication.getInstance());
//            lv.setDivider(null);
//            lv.setOnItemClickListener(this);
//            lv.setAdapter(mHeadAdapter);
//            mExpandableListView.addHeaderView(lv);
//            ToastUtils.showToast(mExpandableListView.getHeaderViewsCount()+"");
//        }
	}

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "新会员请求", null).setShowLogo(false);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		ToastUtils.showToast(position+"");
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		ToastUtils.showToast(position+"");
        Object tag1 = view.getTag(R.id.tag_contacts_group);
        TextView tv_name;
        TextView tv_delete;
        TextView tv_sign;
        if (tag1 != null && tag1 instanceof Integer) {
            int groupPos = (Integer) tag1;
            Object tag2 = view.getTag(R.id.tag_contacts_child);
            if (tag2 != null && tag2 instanceof Integer) {
                int childPos = (Integer) tag2;
                final HeadItem c = mHeadAdapter.getItem(position);
                if (c != null) {
//                	final Dialog dialog = new Dialog(getActivity(), R.style.selectorDialog); 
                	final Dialog dialog = new Dialog(this); 
                    View view1 = SystemUtils.inflate(R.layout.members_dialog);
                    tv_name = (TextView) view1.findViewById(R.id.tv_name);
                    tv_delete = (TextView) view1.findViewById(R.id.tv_delete);
                    tv_sign = (TextView) view1.findViewById(R.id.tv_sign);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(view1);
                    dialog.setCanceledOnTouchOutside(true);
                    tv_name.setText(c.name);
                    tv_delete.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							ToastUtils.showToast("删除");
						}
					});
                    tv_sign.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							ToastUtils.showToast("删除所有");
						}
					});
                    dialog.show();
                }
                return true;
            }
        }
        return false;
    
	}
	
	private class HeadAdapter extends HoldDataBaseAdapter<HeadItem> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HeadViewHolder holder;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.activity_new_members_item);
                holder = new HeadViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (HeadViewHolder) convertView.getTag();
            }
            holder.fillData(getItem(position));
            return convertView;
        }
    }

    private class HeadViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_content;

        public void findView(View v) {
            iv_icon = (ImageView) v.findViewById(R.id.iv_contacts_logo);
            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_content = (TextView) v.findViewById(R.id.tv_content);
        }

        public void fillData(HeadItem item) {
            iv_icon.setImageResource(item.iconResId);
            tv_name.setText(item.name);
            tv_content.setText(item.content);
        }
    }

    private class HeadItem {
        int iconResId;
        String name;
        String content;

        public HeadItem(int iconResId, String name,String content) {
            super();
            this.iconResId = iconResId;
            this.name = name;
            this.content = content;
        }

    }

	

}
