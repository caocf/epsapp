package com.epeisong.model;

import java.io.File;
import java.io.Serializable;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import com.epeisong.R;
import com.epeisong.data.dao.helper.UserDaoHelper.t_user;
import com.epeisong.logistics.common.Properties;
import com.epeisong.utils.BitmapUtils;
import com.epeisong.utils.EncodeUtils;

/**
 * 用户
 * 
 * @author poet
 * 
 */
public class User implements Serializable, Cloneable, Comparable<User> {

    private static final long serialVersionUID = 676269091065902185L;

    public static final int CONFIG_RECEIVE_CONTACTS_FREIGHT_YES = Properties.LOGISTIC_SUBSCRIBE_RECEIVE_FREIGHTS_FROM_FRIENDS;
    public static final int CONFIG_RECEIVE_CONTACTS_FREIGHT_NO = Properties.LOGISTIC_SUBSCRIBE_NOT_RECEIVE_FREIGHTS_FROM_FRIENDS;

    public static final int CONFIG_HIDE = Properties.LOGISTIC_HIDE;
    public static final int CONFIG_NO_HIDE = Properties.LOGISTIC_NOT_HIDE;

    public static final int STATUS_SHIELDING = Properties.MARKET_MEMBER_IS_BANNED; // 会员被屏蔽(1)
    public static final int STATUS_NOSHIELDING = Properties.MARKET_MEMBER_IS_NOT_BANNED; // 会员不被屏蔽(2)

    private String id;
    private String account_name; // 账户名，目前和phone一样
    private String phone;
    private String logo_url;
    private String remark_pinyin;
    private String show_name;
    private String pinyin;
    private String contacts_name;
    private String contacts_phone;
    private String contacts_telephone;
    private String address;
    private String qq;
    private String email;
    private String wechat;
    private String region;
    private int region_code;
    private String self_intro;
    private int status;

    private int user_type_code;
    private String user_type_name;
    private int receive_contacts_freight;
    private int is_hide;
    private int star_level;

    private String serve_id_a;
    private int serve_type_a;
    private String serve_id_b;
    private int serve_type_b;

    private int recommend;
    private int unrecommend;

    private int walletId;

    private UserRole userRole;

    protected String externalLogisticsId; // 外部物流业务id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getRemark_pinyin() {
        return remark_pinyin;
    }

    public void setRemark_pinyin(String remark_pinyin) {
        this.remark_pinyin = remark_pinyin;
    }

    public String getShow_name() {
        return show_name;
    }

    public void setShow_name(String show_name) {
        this.show_name = show_name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getContacts_name() {
        return contacts_name;
    }

    public void setContacts_name(String contacts_name) {
        this.contacts_name = contacts_name;
    }

    public String getContacts_phone() {
        return contacts_phone;
    }

    public void setContacts_phone(String contacts_phone) {
        this.contacts_phone = contacts_phone;
    }

    public String getContacts_telephone() {
        return contacts_telephone;
    }

    public void setContacts_telephone(String contacts_telephone) {
        this.contacts_telephone = contacts_telephone;
    }

    public String getAddress() {
        if (address == null) {
            return "";
        }
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getRegion_code() {
        return region_code;
    }

    public void setRegion_code(int region_code) {
        this.region_code = region_code;
    }

    public String getSelf_intro() {
        // if (TextUtils.isEmpty(self_intro) && userRole != null) {
        // return userRole.getDesc();
        // }
        return self_intro;
    }

    public void setSelf_intro(String self_intro) {
        this.self_intro = self_intro;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUser_type_code() {
        return user_type_code;
    }

    public void setUser_type_code(int user_type_code) {
        this.user_type_code = user_type_code;
    }

    public String getUser_type_name() {
        return user_type_name;
    }

    public void setUser_type_name(String user_type_name) {
        this.user_type_name = user_type_name;
    }

    public int isReceive_contacts_freight() {
        return receive_contacts_freight;
    }

    public void setReceive_contacts_freight(int receive_contacts_freight) {
        this.receive_contacts_freight = receive_contacts_freight;
    }

    public int getIs_hide() {
        return is_hide;
    }

    public void setIs_hide(int is_hide) {
        this.is_hide = is_hide;
    }

    public int getStar_level() {
        return star_level;
    }

    public void setStar_level(int star_level) {
        this.star_level = star_level;
    }

    public String getServe_id_a() {
        return serve_id_a;
    }

    public void setServe_id_a(String serve_id_a) {
        this.serve_id_a = serve_id_a;
    }

    public int getServe_type_a() {
        return serve_type_a;
    }

    public void setServe_type_a(int serve_type_a) {
        this.serve_type_a = serve_type_a;
    }

    public String getServe_id_b() {
        return serve_id_b;
    }

    public void setServe_id_b(String serve_id_b) {
        this.serve_id_b = serve_id_b;
    }

    public int getServe_type_b() {
        return serve_type_b;
    }

    public void setServe_type_b(int serve_type_b) {
        this.serve_type_b = serve_type_b;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public int getUnrecommend() {
        return unrecommend;
    }

    public void setUnrecommend(int unrecommend) {
        this.unrecommend = unrecommend;
    }

    public String getExternalLogisticsId() {
        return externalLogisticsId;
    }

    public void setExternalLogisticsId(String externalLogisticsId) {
        this.externalLogisticsId = externalLogisticsId;
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(t_user.FIELD.ID, id);
        values.put(t_user.FIELD.ACCOUNT_NAME, account_name);
        values.put(t_user.FIELD.PHONE, phone);
        values.put(t_user.FIELD.LOGO_URL, logo_url);
        values.put(t_user.FIELD.SHOW_NAME, show_name);
        values.put(t_user.FIELD.PINYIN, pinyin);
        values.put(t_user.FIELD.CONTACT_NAME, contacts_name);
        values.put(t_user.FIELD.CONTACT_PHONE, contacts_phone);
        values.put(t_user.FIELD.CONTACT_TELEPHONE, contacts_telephone);
        values.put(t_user.FIELD.ADDRESS, address);
        values.put(t_user.FIELD.QQ, qq);
        values.put(t_user.FIELD.EMAIL, email);
        values.put(t_user.FIELD.WECHAT, wechat);
        values.put(t_user.FIELD.REGION, region);
        values.put(t_user.FIELD.REGION_CODE, region_code);
        values.put(t_user.FIELD.USER_TYPE_CODE, user_type_code);
        values.put(t_user.FIELD.USER_TYPE_NAME, user_type_name);
        values.put(t_user.FIELD.RECEIVE_CONTACTS_FREIGHT, receive_contacts_freight);
        values.put(t_user.FIELD.IS_HIDE, is_hide);
        values.put(t_user.FIELD.STAR_LEVEL, star_level);
        values.put(t_user.FIELD.SERVE_ID_A, serve_id_a);
        values.put(t_user.FIELD.SERVE_TYPE_A, serve_type_a);
        values.put(t_user.FIELD.SERVE_ID_B, serve_id_b);
        values.put(t_user.FIELD.SERVE_TYPE_B, serve_type_b);
        values.put(t_user.FIELD.WALLET_ID, walletId);
        values.put(t_user.FIELD.REMARK_PINYIN, remark_pinyin);
        values.put(t_user.FIELD.STATUS, status);

        if (userRole != null) {
            values.put(t_user.FIELD.role_region_code, userRole.getRegionCode());
            values.put(t_user.FIELD.role_region_name, userRole.getRegionName());
            values.put(t_user.FIELD.role_cur_region_code, userRole.getCurrentRegionCode());
            values.put(t_user.FIELD.role_cur_region_name, userRole.getCurrentRegionName());
            values.put(t_user.FIELD.role_cur_longitude, userRole.getCurrent_longitude());
            values.put(t_user.FIELD.role_cur_latitude, userRole.getCurrent_latitude());
            values.put(t_user.FIELD.role_line_start_code, userRole.getLineStartCode());
            values.put(t_user.FIELD.role_line_start_name, userRole.getLineStartName());
            values.put(t_user.FIELD.role_line_end_code, userRole.getLineEndCode());
            values.put(t_user.FIELD.role_line_end_name, userRole.getLineEndName());
            values.put(t_user.FIELD.role_validity_code, userRole.getValidityCode());
            values.put(t_user.FIELD.role_validity_name, userRole.getValidityName());
            values.put(t_user.FIELD.role_insurance_code, userRole.getInsuranceCode());
            values.put(t_user.FIELD.role_insurance_name, userRole.getInsuranceName());
            values.put(t_user.FIELD.role_device_code, userRole.getDeviceCode());
            values.put(t_user.FIELD.role_device_name, userRole.getDeviceName());
            values.put(t_user.FIELD.role_depot_code, userRole.getDepotCode());
            values.put(t_user.FIELD.role_depot_name, userRole.getDepotName());
            values.put(t_user.FIELD.role_pack_code, userRole.getPackCode());
            values.put(t_user.FIELD.role_pack_name, userRole.getPackName());
            values.put(t_user.FIELD.role_truck_len_code, userRole.getTruckLengthCode());
            values.put(t_user.FIELD.role_truck_len_name, userRole.getTruckLengthName());
            values.put(t_user.FIELD.role_truck_type_code, userRole.getTruckTypeCode());
            values.put(t_user.FIELD.role_truck_type_name, userRole.getTruckTypeName());
            values.put(t_user.FIELD.role_load_ton, userRole.getLoadTon());
            values.put(t_user.FIELD.role_goods_type_code, userRole.getGoodsTypeCode());
            values.put(t_user.FIELD.role_goods_type_name, userRole.getGoodsTypeName());
            values.put(t_user.FIELD.role_transport_type_code, userRole.getTransportTypeCode());
            values.put(t_user.FIELD.role_transport_type_name, userRole.getTransportTypeName());
            values.put(t_user.FIELD.role_weight, userRole.getWeight());
            values.put(t_user.FIELD.role_is_full_loaded, userRole.getIs_full_loaded());
        }
        return values;
    }

    public String getQrCodePath(Context context) {
        String path = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + context.getPackageName()
                + "/" + EncodeUtils.md5base64(id) + "_qrcode.png";
        return path;
    }

    public boolean saveQrCode(Context context, Bitmap bmp) {
        BitmapUtils.saveBitmapToFile(getQrCodePath(context), bmp);
        return true;
    }

    public boolean hasQrCode(Context context) {
        File f = new File(getQrCodePath(context));
        return f.exists();
    }

    public Bitmap getQrCode(Context context, int w, int h) {
        return BitmapUtils.decodeSampledBitmapFromFilePath(getQrCodePath(context), w, h);
    }

    public User cloneSelf() {
        try {
            return (User) clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getuserintroducation() {
        if (this.userRole != null) {
            String roleinforStr;
            roleinforStr = new StringBuilder()
                    .append(hasaddseparator(user_type_name))
                    .append(hasaddseparator(userRole.getRegionName()))
                    // .append(hasaddseparator(userRole.getCurrentRegionName()))
                    .append(hasaddseparator(userRole.getTruckLengthName()))
                    .append(hasaddseparator(userRole.getTruckTypeName()))
                    .append(hasaddseparator(userRole.getline()))
                    // 整车运输
                    .append(hasaddseparator(userRole.getValidityName()))
                    .append(hasaddseparator(userRole.getInsuranceName()))
                    .append(hasaddseparator(userRole.getDeviceName())).append(hasaddseparator(userRole.getDepotName()))
                    .append(hasaddseparator(userRole.getPackName()))
                    .append(hasaddseparator(userRole.getGoodsTypeName())).toString();
            roleinforStr.trim();
            if (roleinforStr.length() > 1)// del last ','
                roleinforStr = roleinforStr.substring(0, roleinforStr.length() - 2);

            return roleinforStr;
        }
        return "";
    }

    public String hasaddseparator(String str) {
        if (null == str || 0 == str.trim().length())
            return "";
        else
            str = str + ", ";
        return str;
    }

    /**
     * 如果用户没有设置头像，根据其角色类型显示对应图标，找不到对应图标，则显示系统默认图标
     * @param logistic_type
     * @return
     */
    public static int getDefaultIcon(int logistic_type, boolean isContacts) {
        switch (logistic_type) {
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            return R.drawable.home_ftl;
        case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
            return R.drawable.home_lcl;
        case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
            return R.drawable.home_information;
        case Properties.LOGISTIC_TYPE_COURIER:
            return R.drawable.more_courier;
        default:
            if (isContacts) {
                return R.drawable.user_logo_default;
            } else {
                return R.drawable.user_logo_unknown;
            }
        }
    }

    @Override
    public int compareTo(User another) {
        String anPinyin = null;
        if (another != null) {
            anPinyin = another.getRemark_pinyin();
            if (TextUtils.isEmpty(anPinyin)) {
                anPinyin = another.getPinyin();
            }
        }
        if (TextUtils.isEmpty(anPinyin)) {
            return 1;
        }

        String mPinyin = getRemark_pinyin();
        if (TextUtils.isEmpty(mPinyin)) {
            mPinyin = getPinyin();
        }

        if (TextUtils.isEmpty(mPinyin)) {
            return -1;
        }
        return mPinyin.toUpperCase().compareTo(anPinyin.toUpperCase());
    }

}
