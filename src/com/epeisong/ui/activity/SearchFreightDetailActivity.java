package com.epeisong.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.layer02.UserProvider;
import com.epeisong.model.Freight;
import com.epeisong.model.User;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.LogUtils;

/**
 * 搜索车源货源详情页面
 * 
 * @author gnn
 * 
 */
public class SearchFreightDetailActivity extends BaseActivity implements OnClickListener {

    public static final String EXTRA_FREIGHT = "freight";
    public static final String EXTRA_MARKET = "market";

    private Freight mFreight;
    private User mMarket;
    private TextView tv_search_public;
    private ImageView iv_search_car;
    private TextView tv_search_place;
    private TextView tv_search_content;
    private TextView tv_search_person;
    private TextView tv_search_phone;
    private TextView tv_search_tel;
    private Button btn_shielding_user;
    private Button btn_shielding_message;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.btn_shielding_user:
//            NetAddBanned net = new NetAddBanned(this, Integer.parseInt(mFreight.getUser_id()));
//            net.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {
//
//                @Override
//                public void onSuccess(Builder response) {
//                    // TODO Auto-generated method stub
//                    btn_shielding_user.setText("该用户已屏蔽");
//                }
//
//            });
            break;
        case R.id.btn_shielding_message:
//            NetUpdateIsAllowToShow netShow = new NetUpdateIsAllowToShow(this,
//                    Properties.MARKET_SCREEN_NOT_ALLOW_TO_SHOW, Integer.parseInt(mFreight.getId()));
//            netShow.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {
//
//                @Override
//                public void onSuccess(
//                        com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder response) {
//                    // TODO Auto-generated method stub
//                    btn_shielding_message.setText("该信息已被屏蔽");
//                }
//            });
            break;
        default:
            break;
        }
    }

    @Override
    protected TitleParams getTitleParams() {
        String title = mFreight.getType() == Freight.TYPE_GOODS ? "货源详情" : "车源详情";
        return new TitleParams(getDefaultHomeAction(), title, null).setShowLogo(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFreight = (Freight) getIntent().getSerializableExtra(EXTRA_FREIGHT);
        mMarket = (User) getIntent().getSerializableExtra(EXTRA_MARKET);
        LogUtils.d("mMarket.getId", mMarket.getId());
        LogUtils.d("UserDao", UserDao.getInstance().getUser().getId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_freight_detail);
        tv_search_public = (TextView) findViewById(R.id.tv_search_public);
        iv_search_car = (ImageView) findViewById(R.id.iv_search_car);
        tv_search_place = (TextView) findViewById(R.id.tv_search_place);
        tv_search_content = (TextView) findViewById(R.id.tv_search_content);

        tv_search_person = (TextView) findViewById(R.id.tv_search_person);
        tv_search_phone = (TextView) findViewById(R.id.tv_search_phone);
        tv_search_tel = (TextView) findViewById(R.id.tv_search_tel);

        btn_shielding_user = (Button) findViewById(R.id.btn_shielding_user);
        btn_shielding_message = (Button) findViewById(R.id.btn_shielding_message);

        btn_shielding_user.setText("屏蔽该用户");
        btn_shielding_message.setText("屏蔽该消息");

        btn_shielding_user.setOnClickListener(this);
        btn_shielding_message.setOnClickListener(this);

        if (Integer.parseInt(mMarket.getId()) == Integer.parseInt(UserDao.getInstance().getUser().getId())) {
            btn_shielding_user.setVisibility(View.VISIBLE);
            btn_shielding_message.setVisibility(View.VISIBLE);
        } else {
            btn_shielding_user.setVisibility(View.GONE);
            btn_shielding_message.setVisibility(View.GONE);
        }

        if (mFreight.getType() == Freight.TYPE_GOODS) {
            iv_search_car.setImageResource(R.drawable.icon_freight_goods);
        } else if (mFreight.getType() == Freight.TYPE_TRUCK) {
            iv_search_car.setImageResource(R.drawable.icon_freight_truck);
        }
        tv_search_public.setText(DateUtil.long2YMDHM(mFreight.getCreate_time()));
        tv_search_place.setText(mFreight.getStart_region() + " 到 " + mFreight.getEnd_region());
        tv_search_content.setText(mFreight.getDesc());

        String uid = mFreight.getUser_id();
        User user = UserProvider.provideById(uid);
        tv_search_person.setText(user.getShow_name());
        tv_search_phone.setText(user.getContacts_phone());
        tv_search_tel.setText(user.getContacts_telephone());
    }

}
