package com.epeisong.data.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.epeisong.EpsApplication;
import com.epeisong.MainActivity;
import com.epeisong.data.dao.helper.UserDaoHelper;
import com.epeisong.data.dao.helper.UserDaoHelper.t_user;
import com.epeisong.model.User;
import com.epeisong.model.UserRole;
import com.epeisong.ui.activity.temp.LoginActivity;
import com.epeisong.utils.EncodeUtils;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.SpUtils;

/**
 * UserDao
 * 
 * @author poet
 * 
 */
public class UserDao {

    private static UserDao dao;

    private UserDaoHelper mDaoHelper;
    private String mTableName;

    private List<UserObserver> mObservers;

    private User mUser;

    private UserDao() {
        mDaoHelper = new UserDaoHelper(EpsApplication.getInstance(), null, null, 0);
        mTableName = mDaoHelper.getTableName();
    }

    public synchronized static UserDao getInstance() {
        if (dao == null) {
            dao = new UserDao();
        }
        return dao;
    }

    public User getUser() {
        String phone = "";
        if (mUser == null) {
            phone = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PHONE, null);
            if (phone == null) {
                EpsApplication.exit(MainActivity.sMainActivity, LoginActivity.class);
            } else {
                mUser = queryByPhone(phone);
            }
        }
        if (mUser == null) {
            EpsApplication.exit(MainActivity.sMainActivity, LoginActivity.class);
            return null;
        }
        return mUser.cloneSelf();
    }

    public static void groupByPinyin(List<User> users, List<String> titles, List<List<User>> lists) {
        Collections.sort(users);
        String temp = "#";
        List<User> temps = new ArrayList<User>();
        for (User u : users) {
            String pinyin = u.getRemark_pinyin();
            if (TextUtils.isEmpty(pinyin)) {
                pinyin = u.getPinyin();
            }

            if (TextUtils.isEmpty(pinyin)) {
                temps.add(u);
                continue;
            }
            String ch = String.valueOf(pinyin.charAt(0)).toUpperCase(Locale.getDefault());
            if (!titles.contains(ch)) {
                titles.add(ch);
                List<User> list = new ArrayList<User>();
                lists.add(list);
            }
            int index = titles.indexOf(ch);
            lists.get(index).add(u);
        }
        if (!temps.isEmpty()) {
            titles.add(titles.size(), temp);
            lists.add(lists.size(), temps);
        }
    }

    public synchronized boolean replace(User user) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        db.delete(mTableName, null, null);
        long _id = db.replace(mTableName, null, user.getContentValues());
        db.close();
        if (_id > 0) {
            mUser = user;
            notifyObserver(user.cloneSelf());
            return true;
        }
        return false;
    }

    public synchronized User queryByPhone(String phone) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_user.FIELD.PHONE + "=?", new String[] { phone }, null, null, null);
        User user = null;
        if (c != null) {
            if (c.moveToNext()) {
                user = parseCursor(c);
            }
            c.close();
        }
        db.close();
        return user;
    }

    public synchronized User queryById(String id) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_user.FIELD.ID + "=?", new String[] { id }, null, null, null);
        User user = null;
        if (c != null) {
            if (c.moveToNext()) {
                user = parseCursor(c);
            }
            c.close();
        }
        db.close();
        return user;
    }

    public synchronized boolean insert(User user) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        long _id = db.insert(mTableName, null, user.getContentValues());
        db.close();
        if (_id > 0) {
            notifyObserver(user);
            return true;
        }
        return false;
    }

    private User parseCursor(Cursor c) {
        User user = new User();
        String id = c.getString(c.getColumnIndex(t_user.FIELD.ID));
        user.setId(id);
        String account_name = c.getString(c.getColumnIndex(t_user.FIELD.ACCOUNT_NAME));
        user.setAccount_name(account_name);
        String phone = c.getString(c.getColumnIndex(t_user.FIELD.PHONE));
        user.setPhone(phone);
        String logo_url = c.getString(c.getColumnIndex(t_user.FIELD.LOGO_URL));
        user.setLogo_url(logo_url);
        String show_name = c.getString(c.getColumnIndex(t_user.FIELD.SHOW_NAME));
        user.setShow_name(show_name);
        String pinyin = c.getString(c.getColumnIndex(t_user.FIELD.PINYIN));
        user.setPinyin(pinyin);
        String contacts_name = c.getString(c.getColumnIndex(t_user.FIELD.CONTACT_NAME));
        user.setContacts_name(contacts_name);
        String contacts_phone = c.getString(c.getColumnIndex(t_user.FIELD.CONTACT_PHONE));
        user.setContacts_phone(contacts_phone);
        String contacts_telephone = c.getString(c.getColumnIndex(t_user.FIELD.CONTACT_TELEPHONE));
        user.setContacts_telephone(contacts_telephone);
        String address = c.getString(c.getColumnIndex(t_user.FIELD.ADDRESS));
        user.setAddress(address);
        String qq = c.getString(c.getColumnIndex(t_user.FIELD.QQ));
        user.setQq(qq);
        String email = c.getString(c.getColumnIndex(t_user.FIELD.EMAIL));
        user.setEmail(email);
        String wechat = c.getString(c.getColumnIndex(t_user.FIELD.WECHAT));
        user.setWechat(wechat);
        String region = c.getString(c.getColumnIndex(t_user.FIELD.REGION));
        user.setRegion(region);
        int region_code = c.getInt(c.getColumnIndex(t_user.FIELD.REGION_CODE));
        user.setRegion_code(region_code);
        int user_type_code = c.getInt(c.getColumnIndex(t_user.FIELD.USER_TYPE_CODE));
        user.setUser_type_code(user_type_code);
        String user_type_name = c.getString(c.getColumnIndex(t_user.FIELD.USER_TYPE_NAME));
        user.setUser_type_name(user_type_name);
        int receive_contacts_freight = c.getInt(c.getColumnIndex(t_user.FIELD.RECEIVE_CONTACTS_FREIGHT));
        user.setReceive_contacts_freight(receive_contacts_freight);
        int status = c.getInt(c.getColumnIndex(t_user.FIELD.STATUS));
        user.setStatus(status);
        int is_hide = c.getInt(c.getColumnIndex(t_user.FIELD.IS_HIDE));
        user.setIs_hide(is_hide);
        int star_level = c.getInt(c.getColumnIndex(t_user.FIELD.STAR_LEVEL));
        user.setStar_level(star_level);
        String serve_id_a = c.getString(c.getColumnIndex(t_user.FIELD.SERVE_ID_A));
        user.setServe_id_a(serve_id_a);
        int serve_type_a = c.getInt(c.getColumnIndex(t_user.FIELD.SERVE_TYPE_A));
        user.setServe_type_a(serve_type_a);
        String serve_id_b = c.getString(c.getColumnIndex(t_user.FIELD.SERVE_ID_B));
        user.setServe_id_b(serve_id_b);
        int serve_type_b = c.getInt(c.getColumnIndex(t_user.FIELD.SERVE_TYPE_B));
        user.setServe_type_b(serve_type_b);
        int walletId = c.getInt(c.getColumnIndex(t_user.FIELD.WALLET_ID));
        user.setWalletId(walletId);
        user.setUserRole(parseRole(c));
        return user;
    }

    private UserRole parseRole(Cursor c) {
        UserRole role = new UserRole();
        role.setRegionCode(getInt(c, t_user.FIELD.role_region_code));
        role.setRegionName(getString(c, t_user.FIELD.role_region_name));
        role.setCurrentRegionCode(getInt(c, t_user.FIELD.role_cur_region_code));
        role.setCurrentRegionName(getString(c, t_user.FIELD.role_cur_region_name));
        role.setCurrent_longitude(getDouble(c, t_user.FIELD.role_cur_longitude));
        role.setCurrent_latitude(getDouble(c, t_user.FIELD.role_cur_latitude));
        role.setLineStartCode(getInt(c, t_user.FIELD.role_line_start_code));
        role.setLineStartName(getString(c, t_user.FIELD.role_line_start_name));
        role.setLineEndCode(getInt(c, t_user.FIELD.role_line_end_code));
        role.setLineEndName(getString(c, t_user.FIELD.role_line_end_name));
        role.setValidityCode(getInt(c, t_user.FIELD.role_validity_code));
        role.setValidityName(getString(c, t_user.FIELD.role_validity_name));
        role.setInsuranceCode(getInt(c, t_user.FIELD.role_insurance_code));
        role.setInsuranceName(getString(c, t_user.FIELD.role_insurance_name));
        role.setDeviceCode(getInt(c, t_user.FIELD.role_device_code));
        role.setDeviceName(getString(c, t_user.FIELD.role_device_code));
        role.setDepotCode(getInt(c, t_user.FIELD.role_depot_code));
        role.setDepotName(getString(c, t_user.FIELD.role_depot_name));
        role.setPackCode(getInt(c, t_user.FIELD.role_pack_code));
        role.setPackName(getString(c, t_user.FIELD.role_pack_name));
        role.setTruckLengthCode(getInt(c, t_user.FIELD.role_truck_len_code));
        role.setTruckLengthName(getString(c, t_user.FIELD.role_truck_len_name));
        role.setTruckTypeCode(getInt(c, t_user.FIELD.role_truck_type_code));
        role.setTruckTypeName(getString(c, t_user.FIELD.role_truck_type_name));
        role.setLoadTon(getInt(c, t_user.FIELD.role_load_ton));
        role.setGoodsTypeCode(getInt(c, t_user.FIELD.role_goods_type_code));
        role.setGoodsTypeName(getString(c, t_user.FIELD.role_goods_type_name));
        role.setTransportTypeCode(getInt(c, t_user.FIELD.role_transport_type_code));
        role.setTransportTypeName(getString(c, t_user.FIELD.role_transport_type_name));
        role.setWeight(getDouble(c, t_user.FIELD.role_weight));
        role.setIs_full_loaded(getInt(c, t_user.FIELD.role_is_full_loaded));
        return role;
    }

    private int getInt(Cursor c, String field) {
        return c.getInt(c.getColumnIndex(field));
    }

    private double getDouble(Cursor c, String field) {
        return c.getDouble(c.getColumnIndex(field));
    }

    private String getString(Cursor c, String field) {
        return c.getString(c.getColumnIndex(field));
    }

    private void notifyObserver(final User user) {
        if (mObservers != null && mObservers.size() > 0) {
            HandlerUtils.post(new Runnable() {
                @Override
                public void run() {
                    for (UserObserver ob : mObservers) {
                        ob.onUserChange(user);
                    }
                }
            });

        }
    }

    public void addObserver(UserObserver ob) {
        if (ob == null) {
            return;
        }
        if (mObservers == null) {
            mObservers = new ArrayList<UserDao.UserObserver>();
        }
        mObservers.add(ob);
    }

    public void removeObserver(UserObserver ob) {
        if (mObservers != null && ob != null) {
            mObservers.remove(ob);
        }
    }

    public interface UserObserver {
        void onUserChange(User user);
    }

    public static void saveAccount(String phone, String pwd) {
        SpUtils.put(SpUtils.KEYS_SYS.STRING_CURR_USER_PHONE, phone);
        SpUtils.put(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, pwd);
    }

    public static void saveHost(String ip, int port) {
        SpUtils.put(SpUtils.KEYS_SYS.STRING_CURR_CONN_IP, ip);
        SpUtils.put(SpUtils.KEYS_SYS.INT_CURR_CONN_PORT, port);
    }

    public static void saveLogin(User user) {
        String userIdMd5 = EncodeUtils.md5base64(user.getId());
        SpUtils.put(SpUtils.KEYS_SYS.STRING_CURR_USER_ID_MD5, userIdMd5);
        EpsApplication.setUserIdMd5(userIdMd5);

        UserDao.getInstance().replace(user);
    }
}
