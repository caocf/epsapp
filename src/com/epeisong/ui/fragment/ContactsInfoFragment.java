package com.epeisong.ui.fragment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.bdmap.impl.FixedLocActivity;
import com.epeisong.LogisticsProducts;
import com.epeisong.R;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.fragment.PendingFragment;
import com.epeisong.base.view.AdjustHeightGridView;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.ContactsTagDao;
import com.epeisong.data.dao.EpsTagDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetGuarantee;
import com.epeisong.data.net.parser.GuaranteeParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.RecommendReq.Builder;
import com.epeisong.logistics.proto.Eps.RecommendResp;
import com.epeisong.model.Contacts;
import com.epeisong.model.EpsTag;
import com.epeisong.model.Guarantee;
import com.epeisong.model.User;
import com.epeisong.model.UserRole;
import com.epeisong.net.request.NetCreateOrUpdateRecommend;
import com.epeisong.net.request.NetGetRecommend;
import com.epeisong.net.request.OnNetRequestListenerImpl;
import com.epeisong.ui.activity.BlackBoardOtherActivity;
import com.epeisong.ui.activity.BulletinListActivity;
import com.epeisong.ui.activity.ContactsDetailActivity;
import com.epeisong.ui.activity.EditOtherAddressActivity;
import com.epeisong.ui.activity.InMarketActivity;
import com.epeisong.ui.activity.SetGetGoodsActivity;
import com.epeisong.ui.activity.VechileInforActivity;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 联系人 - 基本信息
 * 
 * @author Jack
 * 
 */
public class ContactsInfoFragment extends PendingFragment implements OnClickListener {
    // public static final int CERTIFICATE_MAX_NUM = 3;
    private static final int REQUEST_CODE_SERVICE_ADDRESS = 101;

    private TextView tv_contacts_region;
    private View mContentView;
    private User mUser;
    private String mUserId;
    private TextView tv_recommend_count;
    private TextView tv_no_recommend_count;
    private TextView tv_bottomtel;
    // private TextView recommendText;
    // private TextView unrecommendText;
    // private TextView tv_cancel;
    private int Logistic_type;
    private int Product_type;
    private ImageView iv;
    // private int tagdis;
    Gallery gallery;
    // private Guarantee Guaranteef[]=new Guarantee[CERTIFICATE_MAX_NUM];
    private Boolean isintroduceopen;
    private TextView tv_contacts_tvdesc;
    // private ImageView tv_certificate_0[]=new ImageView[CERTIFICATE_MAX_NUM];

    private AdjustHeightGridView mGridView;
    private MyAdapter mAdapter;

    private int[] images = { R.drawable.home_ftl, R.drawable.home_lcl, R.drawable.home_fast_mail,
            R.drawable.home_insurance, R.drawable.home_information, R.drawable.home_device_lease };

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        Bundle bundle = getArguments();
        mUser = (User) bundle.getSerializable(ContactsDetailActivity.EXTRA_USER);
        mUserId = bundle.getString(ContactsDetailActivity.EXTRA_USER_ID);
        // tagdis = bundle.getInt(ContactsDetailActivity.EXTRA_TAGNODIS_STRING);
        Logistic_type = bundle.getInt(ContactsDetailActivity.EXTRA_USER_TYPEID);
        Product_type = bundle.getInt(String.valueOf(R.string.producttypenum));

        return mContentView = inflater.inflate(R.layout.fragment_contacts_info, null);
    }

    @Override
    protected void onPendingSuccess(Bundle bundle) {
        User result = (User) bundle.getSerializable("bundle");
        // if(result==null)
        // {
        // ToastUtils.showToast("参数错误");
        // return;
        // }
        Contacts contacts = null;

        if (result != null)
            contacts = ContactsDao.getInstance().queryById(result.getId());

        // Logistic_type=result.getUser_type_code();
        // /temp set dismiss
        images[0] = -1;
        if (images[0] == -1) {
            mContentView.findViewById(R.id.l_galley).setVisibility(View.GONE);
            // mContentView.findViewById(R.id.v_level).setVisibility(View.GONE);
            // gallery.setVisibility(View.GONE);
        } else {
            gallery = (Gallery) mContentView.findViewById(R.id.gallery);
            gallery.setAdapter(new ImageAdapter(getActivity()));
            // gallery.setSelection(1);
            gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // imageSwitcher.setImageResource(images[position%images.length]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            final LinearLayout imageViewContainer = (LinearLayout) mContentView.findViewById(R.id.l_galley);
            imageViewContainer.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // 直接移除吧
                    imageViewContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    // 现在布局全部完成，可以获取到任何View组件的宽度、高度、左边、右边等信息
                    alignGalleryToLeft(imageViewContainer, gallery);
                    // Log.i("CDH",
                    // "Global W:"+imageViewContainer.getMeasuredWidth()+"  H:"+imageViewContainer.getMeasuredHeight());
                }
            });
        }
        // LinearLayout layout =
        // (LinearLayout)getActivity().findViewById(R.id.l_galley);
        // int ii = layout.getWidth();
        // / alignGalleryToLeft(layout, gallery);

        // int img_certificate_num[]={R.id.tv_certificate_01,
        // R.id.tv_certificate_02, R.id.tv_certificate_03};
        // for(int i=0;i<CERTIFICATE_MAX_NUM;i++)
        // {
        // tv_certificate_0[i] = (ImageView)
        // mContentView.findViewById(img_certificate_num[i]);
        // tv_certificate_0[i].setOnClickListener(ContactsInfoFragment.this);
        // }
        mGridView = (AdjustHeightGridView) mContentView.findViewById(R.id.gv_img);
        mGridView.setNumColumns(4);
        int p = DimensionUtls.getPixelFromDpInt(10);
        mGridView.setPadding(0, p, 0, 0);
        mGridView.setSelector(R.color.transparent);
        mGridView.setBackgroundColor(Color.WHITE);
        mGridView.setAdapter(mAdapter = new MyAdapter());
        // mGridView.setOnItemClickListener(this);

        mContentView.findViewById(R.id.tv_contacts_tag).setOnClickListener(ContactsInfoFragment.this);

        if (result != null) {
            XBaseActivity a = null;
            if (getActivity() instanceof XBaseActivity) {
                a = (XBaseActivity) getActivity();
            }

            mUser = result;
            RatingBar ratingBar = (RatingBar) mContentView.findViewById(R.id.ratingBar);
            ratingBar.setProgress(result.getStar_level());
            tv_recommend_count = (TextView) mContentView.findViewById(R.id.tv_recommend_count);
            tv_no_recommend_count = (TextView) mContentView.findViewById(R.id.tv_unrecommend_count);
            // tv_cancel = (TextView) mContentView.findViewById(R.id.tv_cancel);
            // recommendText = (TextView) mContentView.findViewById(R.id.v_ce);
            // unrecommendText = (TextView)
            // mContentView.findViewById(R.id.v_un_ce);
            // recommendText.setOnClickListener(this);
            // unrecommendText.setOnClickListener(this);
            // tv_cancel.setOnClickListener(this);
            // tv_recommend_count.setText("1");
            NetGetRecommend net = new NetGetRecommend(a) {
                @Override
                protected boolean onSetRequest(Builder req) {
                    req.setPresenteeId(Integer.parseInt(mUser.getId()));
                    return true;
                }
            };
            net.request(new OnNetRequestListenerImpl<RecommendResp.Builder>() {
                @Override
                public void onSuccess(RecommendResp.Builder response) {
                    tv_recommend_count.setText(response.getRecommendedCount() + "");
                    tv_no_recommend_count.setText(response.getNotRecommendedCount() + "");
                }

            });

            if (contacts != null) {
                ratingBar.setProgress(contacts.getStar_level());
            }

            List<Integer> tagIds = ContactsTagDao.getInstance().queryTagIds(mUserId);
            List<EpsTag> tags = EpsTagDao.getInstance().query(tagIds);
            if (tags != null && !tags.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (EpsTag tag : tags) {
                    sb.append(tag.getName() + " ");
                }
                TextView tv_tag = (TextView) mContentView.findViewById(R.id.tv_contacts_tag);
                tv_tag.setText(sb.toString());
            }

            if (!TextUtils.isEmpty(result.getContacts_name())) {
                TextView tv_contacts_name = (TextView) mContentView.findViewById(R.id.tv_contacts_name);
                tv_contacts_name.setText(result.getContacts_name());
            }
            if (!TextUtils.isEmpty(result.getContacts_phone())) {
                TextView tv_contacts_phone = (TextView) mContentView.findViewById(R.id.tv_contacts_phone);
                tv_contacts_phone.setText(result.getContacts_phone());
            }

            if (!TextUtils.isEmpty(result.getContacts_telephone())) {
                TextView tv_contacts_telephone = (TextView) mContentView.findViewById(R.id.tv_contacts_telephone);
                tv_contacts_telephone.setText(result.getContacts_telephone());
            }

            // 浮动
            String contactString = "";// "拨打电话: ";
            tv_bottomtel = ((TextView) mContentView.findViewById(R.id.tv_bottomtel));
            if (!TextUtils.isEmpty(result.getContacts_phone())) {
                tv_bottomtel.setText(contactString + result.getContacts_phone());
                (mContentView.findViewById(R.id.ll_bottomtel)).setOnClickListener(this);
            } else if (!TextUtils.isEmpty(result.getContacts_telephone())) {
                tv_bottomtel.setText(contactString + result.getContacts_telephone());
                (mContentView.findViewById(R.id.ll_bottomtel)).setOnClickListener(this);
            } else {
                (mContentView.findViewById(R.id.ll_bottomtel)).setVisibility(View.GONE);
            }
            // 线路 1.位置 2.地址
            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:// 驳货
            case Properties.LOGISTIC_TYPE_COURIER:// 快递员
            //case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:// 设备租赁
                mContentView.findViewById(R.id.ll_address).setVisibility(View.GONE);

                TextView tv_center = (TextView) mContentView.findViewById(R.id.tv_noloc);
                if (TextUtils.isEmpty(result.getRegion())) {// &&
                } else {
                    tv_center.setText(result.getRegion());
                }
                mContentView.findViewById(R.id.ll_location).setOnClickListener(this);
                break;

            default:
                User user = UserDao.getInstance().getUser();
                switch (user.getUser_type_code()) {
                case Properties.LOGISTIC_TYPE_PLATFORM_CUSTOMER_SERVICE:
                    mContentView.findViewById(R.id.ll_location).setVisibility(View.GONE);
                    // if (!TextUtils.isEmpty(result.getAddress())) {
                    tv_contacts_region = (TextView) mContentView.findViewById(R.id.tv_region);
                    tv_contacts_region.setTextColor(Color.BLUE);
                    mContentView.findViewById(R.id.ll_address).setOnClickListener(this);
                    tv_contacts_region.setText(result.getRegion() + result.getAddress());
                    // tv_contacts_region.setText(result.getAddress());
                    break;
                default:
                    mContentView.findViewById(R.id.ll_location).setVisibility(View.GONE);
                    // if (!TextUtils.isEmpty(result.getAddress())) {
                    tv_contacts_region = (TextView) mContentView.findViewById(R.id.tv_region);
                    tv_contacts_region.setText(result.getRegion() + result.getAddress());
                    // tv_contacts_region.setText(result.getAddress());
                    // }
                    break;
                }
                break;
            }

            mContentView.findViewById(R.id.rl_introduce).setOnClickListener(this);

            isintroduceopen = false;
            iv = (ImageView) mContentView.findViewById(R.id.iv);

            tv_contacts_tvdesc = (TextView) mContentView.findViewById(R.id.tv_desc);
            if (!TextUtils.isEmpty(result.getSelf_intro())) {
                tv_contacts_tvdesc.setText("\u3000\u3000" + result.getSelf_intro());// getuserintroducation());
            }
            // if(tagdis==1)
            mContentView.findViewById(R.id.ll_contacts_tag).setVisibility(View.GONE);

            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_EXPRESS:// 快递
            case Properties.LOGISTIC_TYPE_PICK_UP_POINT:// 收发网点
            case Properties.LOGISTIC_TYPE_COURIER:// 快递员
            case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:// 同城配送

                if (!TextUtils.isEmpty(result.getUserRole().getrangedeliver())) {
                    TextView tv_contacts_searchzone = (TextView) mContentView.findViewById(R.id.tv_searchzone);
                    tv_contacts_searchzone.setText(result.getUserRole().getrangedeliver());// getuserintroducation());
                }
                break;
            default:
                mContentView.findViewById(R.id.ll_searchzone).setVisibility(View.GONE);
                break;
            }

            mContentView.findViewById(R.id.iv_action_phone).setOnClickListener(ContactsInfoFragment.this);
            mContentView.findViewById(R.id.iv_action_telephone).setOnClickListener(ContactsInfoFragment.this);
            mContentView.findViewById(R.id.btn_bulletin).setOnClickListener(ContactsInfoFragment.this);
            switch (Logistic_type) {
            case Properties.LOGISTIC_TYPE_MARKET:// 配货市场
                TextView btnadd = (TextView) mContentView.findViewById(R.id.btn_blackboard);
                btnadd.setText("如何成为配货市场会员");
                btnadd.setOnClickListener(ContactsInfoFragment.this);
                break;
            case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:// 配载信息部
                // 14.11.26
                mContentView.findViewById(R.id.rl_contacts_searchblank).setVisibility(View.GONE);
                break;
            default:
                mContentView.findViewById(R.id.btn_blackboard).setOnClickListener(ContactsInfoFragment.this);
                break;
            }

            switch (Logistic_type) {
            case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
                mContentView.findViewById(R.id.btn_parkadd).setOnClickListener(ContactsInfoFragment.this);
                break;
            default:
                mContentView.findViewById(R.id.rl_contacts_parkadd).setVisibility(View.GONE);
                break;
            }

            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
                break;
            default:
                //
                mContentView.findViewById(R.id.rl_contacts_searchsend).setVisibility(View.GONE);
                mContentView.findViewById(R.id.rl_contacts_searchget).setVisibility(View.GONE);
                break;
            }
            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:// 驳货
                break;
            default:
                //
                mContentView.findViewById(R.id.rl_contacts_searchvechile).setVisibility(View.GONE);
                break;
            }
            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_EXPRESS:// 快递
            case Properties.LOGISTIC_TYPE_PICK_UP_POINT:// 收发网点
            case Properties.LOGISTIC_TYPE_COURIER:// 快递员
            case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:// 同城配送
                mContentView.findViewById(R.id.rl_contacts_searchzone).setVisibility(View.GONE);
                // mContentView.findViewById(R.id.v_search_zone).setVisibility(View.GONE);
                break;
            default:
                //
                mContentView.findViewById(R.id.rl_contacts_searchzone).setVisibility(View.GONE);
                // mContentView.findViewById(R.id.v_search_zone).setVisibility(View.GONE);
                break;
            }
            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:// 驳货
            case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
                // case Properties.LOGISTIC_TYPE_EXPRESS://快递
                // case Properties.LOGISTIC_TYPE_PICK_UP_POINT://收发网点
                // case Properties.LOGISTIC_TYPE_COURIER://快递员
                // case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION://同城配送
                break;
            default:
                // 加入了公告和小黑板，不能全部了
                // mContentView.findViewById(R.id.ll_btmbtn).setVisibility(View.GONE);
                mContentView.findViewById(R.id.rl_contacts_searchsend).setVisibility(View.GONE);
                mContentView.findViewById(R.id.rl_contacts_searchget).setVisibility(View.GONE);
                mContentView.findViewById(R.id.rl_contacts_searchvechile).setVisibility(View.GONE);
                mContentView.findViewById(R.id.rl_contacts_searchzone).setVisibility(View.GONE);
                break;
            }
            // 2014/12/15 全部隐掉
            mContentView.findViewById(R.id.rl_contacts_searchvechile).setVisibility(View.GONE);

            mContentView.findViewById(R.id.rl_contacts_searchzone).setVisibility(View.GONE);

            mContentView.findViewById(R.id.v_search_send).setOnClickListener(ContactsInfoFragment.this);
            mContentView.findViewById(R.id.v_search_get).setOnClickListener(ContactsInfoFragment.this);
            mContentView.findViewById(R.id.v_search_vechile).setOnClickListener(ContactsInfoFragment.this);
            mContentView.findViewById(R.id.v_search_zone).setOnClickListener(ContactsInfoFragment.this);
        }
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("类别:", mUser.getUser_type_name());
        UserRole userRole = mUser.getUserRole();

        switch (Product_type) {
        case LogisticsProducts.PRODUCTS_DANGEROUS:// 8
        case LogisticsProducts.PRODUCTS_FRESHPERISHABLE:// 32
        case LogisticsProducts.PRODUCTS_LARGETRANSPORT:// 4
        case LogisticsProducts.PRODUCTS_REFRIGERATED:// 16
            map.put("地区:", userRole.getRegionName());
            // map.put("服务区域:", userRole.getRegionName());
            // map.put("货物:", userRole.getGoodsTypeName());
        }

        if (userRole != null) {
            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
            case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
            case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:
            case Properties.LOGISTIC_TYPE_STORAGE:
            case Properties.LOGISTIC_TYPE_PACKAGING:

            case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
                // map.put("常驻地区:", userRole.getRegionName());
            case Properties.LOGISTIC_TYPE_INSURANCE:
            case Properties.LOGISTIC_TYPE_EXPRESS:
            case Properties.LOGISTIC_TYPE_MOVE_HOUSE:
            case Properties.LOGISTIC_TYPE_COURIER:
            case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:
                map.put("地区:", userRole.getRegionName());
                // map.put("所在地:", userRole.getRegionName());
                break;
            }

            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_MARKET:
            case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
            case Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS:
                // map.put("选择城市:", userRole.getRegionName());
                map.put("地区:", userRole.getRegionName());
                break;
            }

            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE://
            case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE://
            case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
            case Properties.LOGISTIC_TYPE_EXPRESS://
            case Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS://
            case Properties.LOGISTIC_TYPE_STORAGE://
            case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION://
                // case Properties.LOGISTIC_TYPE_COURIER:
                // case Properties.LOGISTIC_TYPE_MOVE_HOUSE:
                // case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
                // case Properties.LOGISTIC_TYPE_PARKING_LOT:
                // case Properties.LOGISTIC_TYPE_MARKET:

            case Properties.LOGISTIC_TYPE_PACKAGING:// 包装
                map.put("货物:", userRole.getGoodsTypeName());
                break;
            }

            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
                map.put("车长:", userRole.getTruckLengthName());
                break;
            }
            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
                if (userRole.getLoadTon() > 0)
                    map.put("核载:", String.valueOf(userRole.getLoadTon()) + " 吨");
                else
                    map.put("核载:", "");
                break;
            }

            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
                map.put("车型:", userRole.getTruckTypeName());
                break;
            }
            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
            case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
                // others add line 2015/1/7
            case Properties.LOGISTIC_TYPE_EXPRESS:
            case Properties.LOGISTIC_TYPE_COURIER:
            case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:
            case Properties.LOGISTIC_TYPE_MOVE_HOUSE:
                // case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
                // case Properties.LOGISTIC_TYPE_PARKING_LOT:
                // case Properties.LOGISTIC_TYPE_MARKET:
            case Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS:
                // case Properties.LOGISTIC_TYPE_PACKAGING://包装
                // 快递 快递员 收发网点 同城配送
                // 暂不加， 这个传过来当作服务区域，老的服务区域就不传过来了
                // case Properties.LOGISTIC_TYPE_PICK_UP_POINT:
                map.put("线路:", userRole.getline());
                break;
            }
            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
                map.put("时效:", userRole.getValidityName());
                break;
            }
            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
                map.put("配载线路:", userRole.getline());
                break;
            }

            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_EXPRESS:
                map.put("险种:", userRole.getInsuranceName());
                break;
            }
            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:
                map.put("设备类别:", userRole.getDeviceName());
                break;
            }
            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_STORAGE:
                map.put("仓库类别:", userRole.getDepotName());
                break;
            }
            switch (mUser.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_PACKAGING:
                map.put("包装类别:", userRole.getPackName());
                break;
            }

        }
        Paint paint = new Paint();
        float strwidth = paint.measureText("选择城市:");
        LinearLayout ll_attr = (LinearLayout) mContentView.findViewById(R.id.ll_attr);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            View item = SystemUtils.inflate(R.layout.fragment_contacts_info_attr_item);
            ll_attr.addView(item);
            TextView tv_key = (TextView) item.findViewById(R.id.tv_key);
            tv_key.setText(entry.getKey());
            tv_key.setWidth((int) strwidth);
            TextView tv_value = (TextView) item.findViewById(R.id.tv_value);
            tv_value.setText(entry.getValue());
        }

        ViewTreeObserver vto = iv.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (tv_contacts_tvdesc.getLineCount() <= 3)
                    iv.setVisibility(View.GONE);
                return true;
            }
        });

        loadData(-1, "0", 0, true);
    }

    public void UpdateAddress() {
        Intent intent = new Intent(getActivity(), EditOtherAddressActivity.class);
        intent.putExtra(EditOtherAddressActivity.EXTRA_ORIGINAL_REGION_CODE, mUser.getRegion_code());
        intent.putExtra(EditOtherAddressActivity.EXTRA_ORIGINAL_REGION_NAME, mUser.getRegion());
        intent.putExtra(EditOtherAddressActivity.EXTRA_ORIGINAL_ADDRESS, mUser.getAddress());
        intent.putExtra("service_update", 1);
        intent.putExtra("user", mUser);
        startActivityForResult(intent, REQUEST_CODE_SERVICE_ADDRESS);
        // startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        XBaseActivity a = null;
        if (getActivity() instanceof XBaseActivity) {
            a = (XBaseActivity) getActivity();
        }
        switch (v.getId()) {
        case R.id.ll_address:
            User user = UserDao.getInstance().getUser();
            switch (user.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_PLATFORM_CUSTOMER_SERVICE:
                UpdateAddress();
                break;
            default:
                break;
            }
            break;
        case R.id.ll_bottomtel:
            Intent intentt = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_bottomtel.getText().toString()));
            getActivity().startActivity(intentt);
            break;
        case R.id.ll_location:
            // UpdateAddress();
            UserRole userRole = mUser.getUserRole();
            if (userRole == null)
                break;
            // double longitude=118.784638, latitude=31.979053;
            double longitude = userRole.getCurrent_longitude();
            double latitude = userRole.getCurrent_latitude();
            if (longitude != 0.0 && latitude != 0.0) {
                Intent intentloc = new Intent(getActivity(), FixedLocActivity.class);
                intentloc.putExtra(FixedLocActivity.EXTRA_LONGITUDE, longitude);
                intentloc.putExtra(FixedLocActivity.EXTRA_LATITUDE, latitude);
                intentloc.putExtra(FixedLocActivity.EXTRA_DESC, mUser.getRegion());// +mUser.getAddress());
                startActivity(intentloc);
            }
            break;
        case R.id.rl_introduce:
            if (tv_contacts_tvdesc.getLineCount() <= 3)
                break;
            if (isintroduceopen) {

                iv.setImageResource(R.drawable.hui_down);
                tv_contacts_tvdesc.setMaxLines(3);
                isintroduceopen = false;
            } else {
                iv.setImageResource(R.drawable.blue_up);
                tv_contacts_tvdesc.setMaxLines(100);
                isintroduceopen = true;
            }
            break;
        case R.id.tv_contacts_tag:
            if (!TextUtils.isEmpty(((TextView) v).getText().toString()))
                ToastUtils.showToast(((TextView) v).getText().toString());
            break;
        case R.id.iv_action_phone:
            if (!TextUtils.isEmpty(mUser.getContacts_phone())) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mUser.getContacts_phone()));
                getActivity().startActivity(intent);
            }
            break;
        case R.id.iv_action_telephone:
            if (!TextUtils.isEmpty(mUser.getContacts_telephone())) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mUser.getContacts_telephone()));
                getActivity().startActivity(intent);
            }
            break;
        case R.id.btn_bulletin:
            Intent bulletin;
            bulletin = new Intent(getActivity(), BulletinListActivity.class);
            bulletin.putExtra(BulletinListActivity.EXTRA_USER, mUser);
            startActivity(bulletin);
            break;
        case R.id.btn_parkadd:
            if (Properties.LOGISTIC_TYPE_LOGISTICS_PARK == Logistic_type) {
                Intent intent = new Intent();

                intent.setClass(this.getActivity(), InMarketActivity.class);
                intent.putExtra(ContactsDetailActivity.EXTRA_USER, mUser);
                intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, mUserId);
                intent.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, Logistic_type);
                startActivityForResult(intent, InMarketActivity.REQUEST_BACK_CONSULT);
            }
            break;
        case R.id.btn_blackboard:
            if (Properties.LOGISTIC_TYPE_MARKET == Logistic_type) {
                Intent intent = new Intent();

                intent.setClass(this.getActivity(), InMarketActivity.class);
                intent.putExtra(ContactsDetailActivity.EXTRA_USER, mUser);
                intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, mUserId);
                intent.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, Logistic_type);
                startActivityForResult(intent, InMarketActivity.REQUEST_BACK_CONSULT);
            } else {
                Intent blackact = new Intent(getActivity(), BlackBoardOtherActivity.class);
                blackact.putExtra(ContactsDetailActivity.EXTRA_USER_ID, mUserId);
                blackact.putExtra(ContactsDetailActivity.EXTRA_USER, mUser);
                startActivity(blackact);
            }
            break;
        case R.id.v_search_send:// "查看发货网点"
            Intent intent = new Intent();
            intent.setClass(getActivity(), SetGetGoodsActivity.class);
            intent.putExtra(ContactsDetailActivity.EXTRA_USER, mUser);
            if (mUser != null) {
                intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, mUser.getId());
                intent.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, Logistic_type);
            }
            intent.putExtra(SetGetGoodsActivity.EXTRA_GOODS_TYPE, "0");
            startActivity(intent);
            break;
        case R.id.v_search_get:// "查看提货网点"
            Intent intentset = new Intent();
            intentset.setClass(getActivity(), SetGetGoodsActivity.class);
            intentset.putExtra(ContactsDetailActivity.EXTRA_USER, mUser);
            intentset.putExtra(ContactsDetailActivity.EXTRA_USER_ID, mUser.getId());
            intentset.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, mUser.getUser_type_code());
            intentset.putExtra(SetGetGoodsActivity.EXTRA_GOODS_TYPE, "1");
            startActivity(intentset);
            break;
        case R.id.v_search_vechile:// "查看车辆信息"
            Intent intentvec = new Intent();
            intentvec.setClass(getActivity(), VechileInforActivity.class);
            intentvec.putExtra(ContactsDetailActivity.EXTRA_USER, mUser);
            intentvec.putExtra(ContactsDetailActivity.EXTRA_USER_ID, mUser.getId());
            //
            intentvec.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, mUser.getUser_type_code());
            startActivity(intentvec);
            break;
        case R.id.v_search_zone:
            ToastUtils.showToast("查看收派范围");
            break;
        // case R.id.tv_cancel:
        // NetCreateOrUpdateRecommend netcancel = new
        // NetCreateOrUpdateRecommend(a) {
        //
        // @Override
        // protected boolean onSetRequest(Builder req) {
        // req.setPresenteeId(Integer.parseInt(mUser.getId()));
        // req.setStatus(Properties.RECOMMEND_STATUS_CANCEL_RECOMMEND);
        // return true;
        // }
        // };
        // netcancel.request(new
        // OnNetRequestListenerImpl<RecommendResp.Builder>() {
        //
        // @Override
        // public void onSuccess(RecommendResp.Builder response) {
        // tv_recommend_count.setText(response.getRecommendedCount() + "");
        // tv_no_recommend_count.setText(response.getNotRecommendedCount() +
        // "");
        // }
        //
        // });
        // break;
        case R.id.v_ce:// "推荐"
            NetCreateOrUpdateRecommend net = new NetCreateOrUpdateRecommend(a) {

                @Override
                protected boolean onSetRequest(Builder req) {
                    req.setPresenteeId(Integer.parseInt(mUser.getId()));
                    req.setStatus(Properties.RECOMMEND_STATUS_RECOMMEND);
                    return true;
                }
            };
            net.request(new OnNetRequestListenerImpl<RecommendResp.Builder>() {

                @Override
                public void onSuccess(RecommendResp.Builder response) {
                    tv_recommend_count.setText(response.getRecommendedCount() + 1 + "");
                    tv_no_recommend_count.setText(response.getNotRecommendedCount() + "");
                }
            });
            break;
        case R.id.v_un_ce:// "不推荐"
            NetCreateOrUpdateRecommend un_net = new NetCreateOrUpdateRecommend(a) {

                @Override
                protected boolean onSetRequest(Builder req) {
                    req.setPresenteeId(Integer.parseInt(mUser.getId()));
                    req.setStatus(Properties.RECOMMEND_STATUS_NOT_RECOMMEND);
                    return true;
                }
            };
            un_net.request(new OnNetRequestListenerImpl<RecommendResp.Builder>() {

                @Override
                public void onSuccess(RecommendResp.Builder response) {
                    tv_no_recommend_count.setText(response.getNotRecommendedCount() + 1 + "");
                    tv_recommend_count.setText(response.getRecommendedCount() + "");
                }
            });
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == InMarketActivity.REQUEST_BACK_CONSULT) {
            ((ContactsDetailActivity) getActivity()).setFragmenttoChat();
        }

        else if (resultCode == Activity.RESULT_OK && REQUEST_CODE_SERVICE_ADDRESS == requestCode) {
            User user = UserDao.getInstance().getUser();
            switch (user.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_PLATFORM_CUSTOMER_SERVICE:
                if (TextUtils.isEmpty(tv_contacts_region.getText().toString())) {
                    String addString = data.getStringExtra("address");
                    if (!TextUtils.isEmpty(addString))
                        tv_contacts_region.setText(addString);
                    // mUser.setAddress(address)
                }
                break;
            default:
                break;
            }
        }
    }

    class ImageAdapter extends BaseAdapter {
        private Context context;

        public ImageAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return images.length;// Integer.MAX_VALUE;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv = new ImageView(context);
            if (images[position % images.length] != -1)
                iv.setImageResource(images[position % images.length]);
            iv.setAdjustViewBounds(true);

            iv.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            return iv;
        }
    }

    class MyViewFactory implements ViewFactory {
        private Context context;

        public MyViewFactory(Context context) {
            this.context = context;
        }

        @Override
        public View makeView() {
            ImageView iv = new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.FIT_START);// .FIT_CENTER);

            iv.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            return iv;
        }
    }

    private void alignGalleryToLeft(View parentView, Gallery gallery) {
        int galleryWidth = parentView.getWidth();

        // We are taking the item widths and spacing from a dimension resource
        // because:
        // 1. No way to get spacing at runtime (no accessor in the Gallery
        // class)
        // 2. There might not yet be any item view created when we are calling
        // this
        // function
        int itemWidth = 150;// 60;//Context.getResources().getDimensionPixelSize(R.dimen.gallery_item_width);
        int spacing = 100;// 16;//Context.getResources().getDimensionPixelSize(R.dimen.gallery_spacing);

        // The offset is how much we will pull the gallery to the left in order
        // to simulate
        // left alignment of the first item
        int offset;
        if (galleryWidth <= itemWidth) {
            offset = galleryWidth / 2 - itemWidth / 2 - spacing;
        } else {
            offset = galleryWidth - itemWidth - 2 * spacing;
        }
        offset = 500;

        // Now update the layout parameters of the gallery in order to set the
        // left margin
        MarginLayoutParams mlp = (MarginLayoutParams) gallery.getLayoutParams();
        offset = mlp.leftMargin;
        mlp.setMargins(-offset, mlp.topMargin, mlp.rightMargin, mlp.bottomMargin);
    }

    private void loadData(final int size, final String edge_id, final double weight, final boolean bFirst) {
        AsyncTask<Void, Void, List<Guarantee>> task = new AsyncTask<Void, Void, List<Guarantee>>() {

            @Override
            protected List<Guarantee> doInBackground(Void... params) {

                NetGuarantee netGuarantee = new NetGuarantee() {

                    @Override
                    protected int getCommandCode() {
                        return CommandConstants.LIST_GUARANTEE_PRODUCT_CUSTOMER_CONTACT_REQ;// LIST_GUARANTEE_PRODUCT_REQ;
                    }

                    @Override
                    protected boolean onSetRequest(com.epeisong.logistics.proto.Eps.GuaranteeReq.Builder req) {
                        req.setLimitCount(size);
                        // req.setStatus(1);//all
                        // req.setId(id);
                        req.setCustomerId(Integer.valueOf(mUser.getId()));
                        // req.setGuaranteeId(Integer.valueOf(300001));
                        // req.setProductType(0);
                        return true;
                    }
                };
                try {
                    com.epeisong.logistics.proto.Eps.GuaranteeResp.Builder resp = netGuarantee.request();
                    return GuaranteeParser.parse(resp);
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Guarantee> result) {
                if (result != null && !result.isEmpty()) {
                    mAdapter.addAll(result);
                }
            }
        };
        task.execute();
    }

    private class MyAdapter extends HoldDataBaseAdapter<Guarantee> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.fragment_guarantee_gridview_item);
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

        public void findView(View v) {
            iv = (ImageView) v.findViewById(R.id.iv);
        }

        public void fillData(final Guarantee item) {
            if (!UserDao.getInstance().getUser().getId().equals(mUserId)) {
                if (TextUtils.isEmpty(item.getMark_url2()))
                    return;
                ImageLoader.getInstance().displayImage(item.getMark_url2(), iv);
            } else {
                if (TextUtils.isEmpty(item.getMark_url1()))
                    return;
                ImageLoader.getInstance().displayImage(item.getMark_url1(), iv);
            }
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (item != null) {
                        if (UserDao.getInstance().getUser().getId().equals(mUserId))
                            Toast.makeText(
                                    getActivity(),
                                    "您已开启" + item.getName() + "服务，若您在交易中违约，您将赔付"
                                            + String.valueOf(item.getAccount() / 100) + "元", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(
                                    getActivity(),
                                    "对方已开启" + item.getName() + "服务，若对方在交易中违约，对方将赔付"
                                            + String.valueOf(item.getAccount() / 100) + "元", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
