package com.epeisong.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.Freight;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.SystemUtils;

/**
 * 车源货源列表Adapter
 * @author poet
 *
 */
public class FreightListAdapter extends HoldDataBaseAdapter<Freight> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = onCreateViewHolder();
            convertView = holder.createView();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.fillData(getItem(position));
        return convertView;
    }

    protected ViewHolder onCreateViewHolder() {
        return new ViewHolder();
    }

    public class ViewHolder {
        View view;
        ImageView iv_freight_type;
        TextView tv_start_region;
        TextView tv_end_region;
        TextView tv_owner_name;
        TextView tv_freight_desc;
        TextView tv_time;
        ImageView iv_freight_status;

        public View createView() {
            view = SystemUtils.inflate(R.layout.activity_freight_of_contacts_item);
            iv_freight_type = (ImageView) view.findViewById(R.id.iv_freight_type);
            tv_start_region = (TextView) view.findViewById(R.id.tv_start_region);
            tv_end_region = (TextView) view.findViewById(R.id.tv_end_region);
            tv_owner_name = (TextView) view.findViewById(R.id.tv_name);
            tv_freight_desc = (TextView) view.findViewById(R.id.tv_freight_desc);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            iv_freight_status = (ImageView) view.findViewById(R.id.iv_status);
            return view;
        }

        public void fillData(Freight f) {
            view.setBackgroundResource(R.drawable.selector_item_white_gray);
            int status = f.getStatus();
            switch (status) {
			case Properties.FREIGHT_STATUS_NO_PROCESSED:
				if (f.getType() == Freight.TYPE_GOODS) {
                    iv_freight_type.setImageResource(R.drawable.black_board_goods);
                } else if (f.getType() == Freight.TYPE_TRUCK) {
                    iv_freight_type.setImageResource(R.drawable.black_board_truck);
                }
				break;
			case Properties.FREIGHT_STATUS_BOOK:
				if (f.getType() == Freight.TYPE_GOODS) {
                    iv_freight_type.setImageResource(R.drawable.bload_booked_goods);
                } else if (f.getType() == Freight.TYPE_TRUCK) {
                    iv_freight_type.setImageResource(R.drawable.bload_booked_truck);
                }
				break;
			default:
				if (f.getType() == Freight.TYPE_GOODS) {
                    iv_freight_type.setImageResource(R.drawable.black_board_goods);
                } else if (f.getType() == Freight.TYPE_TRUCK) {
                    iv_freight_type.setImageResource(R.drawable.black_board_truck);
                }
                view.setBackgroundResource(R.color.white_gray);
				break;
			}
            
            tv_start_region.setText(f.getStart_region());
            tv_end_region.setText(f.getEnd_region());
            tv_owner_name.setText(f.getOwner_name());
            tv_time.setText(DateUtil.long2vague(f.getUpdate_time()));
            tv_freight_desc.setText(f.getDesc());
        }
    }

}
