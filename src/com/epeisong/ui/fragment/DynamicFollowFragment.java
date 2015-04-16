package com.epeisong.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.utils.SystemUtils;

/**
 * 动态跟踪
 * 
 * @author Jack
 * 
 */

public class DynamicFollowFragment extends Fragment {

    private ListView mListView;
    private MyAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListView = new ListView(getActivity());

        mListView.setBackgroundResource(R.color.page_bg);
        return mListView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Item> data = new ArrayList<DynamicFollowFragment.Item>();


        Item temp1=new Item();
        temp1.setDateStr("12月01日");
        temp1.setIconId(R.drawable.more_move_house);
        temp1.setTaskStr("下单");
        temp1.setTimeStr("12:01");
        temp1.setTaskIconId(7);
        data.add(temp1.setType(0));
        
        Item temp2=new Item();
        temp2.setTaskStr("装车");
        temp2.setTimeStr("14:22");
        temp2.setTaskIconId(1);
        data.add(temp2.setType(-1));
        
        data.add(new Item().setType(0));
        
        Item ddd;
        ddd=new Item();
        data.add(ddd);
        ddd.setTaskStr("已发送运输途中");
        ddd=new Item();
        data.add(ddd);
        ddd.setTaskStr("到达中转点,分拣中");
        ddd=new Item();
        data.add(ddd);
        ddd.setTaskStr("卸货中");
        ddd=new Item();
        data.add(ddd);
        //ddd.setTaskStr("派送中,派送员:小马 电话18913968200");
        
        data.add(new Item(0, "", "派送中", null));
        data.add(new Item(0, "", "派送中,派送员:小马 电话18913968200", null));

        mListView.setDividerHeight(0);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        mAdapter.replaceAll(data);

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
			ViewHolder holder = null;
			if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.fragment_dynfollow_item);
				holder = new ViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.fillData(position, getItem(position));
            return convertView;
        }
    }

    private class ViewHolder {        
        TextView tv_date;
        TextView tv_task;
        TextView tv_time;
        
        TextView date_top;
        ImageView task_icon;
        ImageView iv_arrow;
        
        ImageView taskicon1, taskicon2,taskicon3;
        TextView tasktext1, tasktext2, tasktext3;
        
        LinearLayout ll_task1, ll_task2;
        public void findView(final View v) {
            date_top = (TextView) v.findViewById(R.id.date_topline);
            tv_date = (TextView) v.findViewById(R.id.tv_date);
//            tv_date.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					if(tv_task.getVisibility()==View.VISIBLE)
//						tv_task.setVisibility(View.GONE);
//					else {
//						tv_task.setVisibility(View.VISIBLE);
//					}
//				}
//			});
            tv_task = (TextView) v.findViewById(R.id.tv_task);
            tv_time = (TextView) v.findViewById(R.id.tv_time);
            
            task_icon = (ImageView) v.findViewById(R.id.task_icon);
            iv_arrow = (ImageView) v.findViewById(R.id.iv_arrow);
            
            taskicon1 = (ImageView) v.findViewById(R.id.task_icon1);
            taskicon2 = (ImageView) v.findViewById(R.id.task_icon2);
            taskicon3 = (ImageView) v.findViewById(R.id.task_icon3);
            tasktext1 = (TextView) v.findViewById(R.id.tv_time1);
            tasktext2 = (TextView) v.findViewById(R.id.tv_time2);
            tasktext3 = (TextView) v.findViewById(R.id.tv_time3);
            
            ll_task1 = (LinearLayout) v.findViewById(R.id.ll_task1);
            ll_task2 = (LinearLayout) v.findViewById(R.id.ll_task2);
        }

		public void fillData(int position, Item item) {
        	if(position==0)//top
        	{
        		date_top.setVisibility(View.GONE);
        	}
        	else {
	        	if(item.getType()==-1)//no date
	        	{
	        		date_top.setVisibility(View.GONE);
	        		tv_date.setVisibility(View.GONE);
	        		iv_arrow.setVisibility(View.GONE);
	        	}

	        }
        	
        	task_icon.setImageResource(item.getIconId());
        	SetTaskIcon(item.getTaskIconId());

        	String namestr;
        	namestr=item.getTaskStr();
        	if(namestr!=null && !namestr.isEmpty())
        		tv_task.setText(namestr);
        	namestr=item.getDateStr();
        	if(namestr!=null && !namestr.isEmpty())
        		tv_date.setText(namestr);
        	namestr=item.getTimeStr();
        	if(namestr!=null && !namestr.isEmpty())
        		tv_time.setText(namestr);
        }
		
		void SetTaskIcon(int iconid)
		{
			switch(iconid)
			{
			case 1:
				taskicon1.setImageResource(R.drawable.more_storage);
				tasktext1.setText("已签收");
				break;
			case 2:
				taskicon2.setImageResource(R.drawable.more_move_house);
				tasktext2.setText("运输中");
				break;
			case 3:
				taskicon1.setImageResource(R.drawable.more_storage);
				taskicon2.setImageResource(R.drawable.more_move_house);
				tasktext1.setText("已签收");
				tasktext2.setText("运输中");
				break;
			case 4:
				taskicon3.setImageResource(R.drawable.more_refrigerated);
				tasktext3.setText("装卸中");
				break;
			case 7:
				taskicon3.setImageResource(R.drawable.more_refrigerated);
				taskicon2.setImageResource(R.drawable.more_move_house);
				taskicon1.setImageResource(R.drawable.more_storage);
				tasktext1.setText("已签收");
				tasktext2.setText("运输中");
				tasktext3.setText("装卸中");
				break;
			default:
				ll_task1.setVisibility(View.GONE);
				LinearLayout.LayoutParams  lyparams=(LinearLayout.LayoutParams ) ll_task2.getLayoutParams();
				lyparams.width+=600;
				ll_task2.setLayoutParams(lyparams);
				break;
			}
		}
    }

    public static class Item
    {
        public static final int type_invalid = -1;
        public static final int type_normal = 0;

        private int type;
        private int resId;
        private String name;
        private Runnable runnable;
        
        private String taskstr, datestr, timestr;
        private int iconid;
        private int taskiconid;
        
        public Item() {
            this.type = type_invalid;
            taskiconid=-1;
        }

        public Item(int resId, String name, String taskstr,        		
        		Runnable runnable) {
            super();
            this.type = type_normal;
            this.resId = resId;
            this.name = name;
            this.runnable = runnable;
            
            //add new 
            this.taskstr = taskstr;
            this.type = type_invalid;
            taskiconid=-1;
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

        public int getIconId() {
            return iconid;
        }

        public void setTaskIconId(int taskiconid) {
            this.taskiconid = taskiconid;
        }
        
        public int getTaskIconId() {
            return taskiconid;
        }

        public void setIconId(int iconid) {
            this.iconid = iconid;
        }
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTaskStr() {
            return taskstr;
        }

        public void setTaskStr(String taskstr) {
            this.taskstr = taskstr;
        }
        
        public String getDateStr() {
            return datestr;
        }

        public void setDateStr(String datestr) {
            this.datestr = datestr;
        }
        
        public String getTimeStr() {
            return timestr;
        }

        public void setTimeStr(String timestr) {
            this.timestr = timestr;
        }
        
        public Runnable getRunnable() {
            return runnable;
        }

        public Item setRunnable(Runnable runnable) {
            this.runnable = runnable;
            return this;
        }
    }
}
