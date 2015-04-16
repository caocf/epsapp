package com.epeisong.data.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.epeisong.EpsApplication;
import com.epeisong.data.dao.helper.ContactsDaoHelper;
import com.epeisong.data.dao.helper.ContactsDaoHelper.t_contacts;
import com.epeisong.data.dao.helper.UserDaoHelper.t_user;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.Contacts;
import com.epeisong.model.Fans;
import com.epeisong.model.UserRole;
import com.epeisong.utils.HandlerUtils;

public class ContactsDao {

    private static ContactsDao dao = new ContactsDao();

    private ContactsDaoHelper mDaoHelper;
    private String mTableName;

    private List<ContactsObserver> mObservers;

    private ContactsDao() {
        mDaoHelper = new ContactsDaoHelper(EpsApplication.getInstance(), null, null, 0);
        mTableName = mDaoHelper.getTableName();
    }

    public static ContactsDao getInstance() {
        return dao;
    }

    private void notifyObserver() {
        if (mObservers != null && mObservers.size() > 0) {
            HandlerUtils.post(new Runnable() {
                @Override
                public void run() {
                    for (ContactsObserver o : mObservers) {
                        o.onContactsChange();
                    }
                }
            });

        }
    }

    private boolean replace(SQLiteDatabase db, Contacts contacts) {
        Cursor c = db.query(mTableName, null, t_contacts.FIELD.ID + "=?", new String[] { contacts.getId() }, null,
                null, null);
        boolean bReplace = false;
        if (c.getCount() <= 0) {
            long _id = db.insert(mTableName, null, contacts.getContentValues());
            if (_id > 0) {
                bReplace = true;
            }
        } else {
            int count = 0;
            if (contacts.getStatus() == Properties.CONTACT_STATUS_DELETED) {
                count = db.delete(mTableName, t_contacts.FIELD.ID + "=?", new String[] { contacts.getId() });
            } else {
                count = db.update(mTableName, contacts.getContentValues(), t_contacts.FIELD.ID + "=? and "
                        + t_contacts.FIELD.UPDATE_TIME + "<?",
                        new String[] { contacts.getId(), String.valueOf(contacts.getUpdate_time()) });
            }
            if (count > 0) {
                bReplace = true;
            }
        }
        c.close();
        return bReplace;
    }

    public synchronized boolean replace(Contacts contacts) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        boolean bNotify = replace(db, contacts);
        db.close();
        if (bNotify) {
            notifyObserver();
        }
        return bNotify;
    }

    public synchronized boolean replaceAll(List<Contacts> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        db.beginTransaction();
        for (Contacts c : list) {
            replace(db, c);
        }
        db.delete(mTableName, t_contacts.FIELD.STATUS + "=?", new String[] { String.valueOf(Contacts.STATUS_DELETED) });
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        notifyObserver();
        return true;
    }

    public synchronized boolean insert(Contacts contacts) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        long _id = db.insert(mTableName, null, contacts.getContentValues());
        db.close();
        if (_id > 0) {
            notifyObserver();
            return true;
        }
        return false;
    }

    public synchronized boolean insertAll(List<Contacts> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        db.beginTransaction();
        for (Contacts contacts : list) {
            long _id = db.insert(mTableName, null, contacts.getContentValues());
            if (_id == -1) {
                db.endTransaction();
                db.close();
                return false;
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        notifyObserver();
        return true;
    }

    public synchronized void delete(Contacts contacts) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        int count = db.delete(mTableName, t_contacts.FIELD.ID + "=?", new String[] { contacts.getId() });
        db.close();
        if (count > 0) {
            notifyObserver();
            FansDao.getInstance().changeStatus(contacts.getId(), Fans.status_read);
        }
    }

    public synchronized void update(Contacts contacts) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        int count = db.update(mTableName, contacts.getContentValues(), t_contacts.FIELD.ID + "=?",
                new String[] { contacts.getId() });
        db.close();
        if (count > 0) {
            notifyObserver();
        }
    }

    public synchronized Contacts queryNewest() {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, null, null, null, null, t_contacts.FIELD.UPDATE_TIME + " desc limit 0,1");
        Contacts contacts = null;
        if (c.moveToNext()) {
            contacts = parseCursor(c);
        }
        c.close();
        db.close();
        return contacts;
    }

    public synchronized Contacts queryById(String id) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        Cursor c = db.query(mTableName, null, "id=?", new String[] { id }, null, null, null);
        Contacts contacts = null;
        if (c != null) {
            if (c.moveToNext()) {
                contacts = parseCursor(c);
            }
            c.close();
        }
        db.close();
        return contacts;
    }

    public synchronized Contacts queryByPhone(String phone) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        Cursor c = db.query(mTableName, null, t_contacts.FIELD.PHONE + "=?", new String[] { phone }, null, null, null);
        Contacts contacts = null;
        if (c != null) {
            if (c.moveToNext()) {
                contacts = parseCursor(c);
            }
            c.close();
        }
        db.close();
        return contacts;
    }

    /**
     * 查询所有联系人（黑名单）
     * 
     * @param contactsStatus
     *            Contacts.STATUS_NORNAL、Contacts.STATUS_BLACKLIST
     * @return
     */
    public synchronized List<Contacts> queryAll(int contactsStatus) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_contacts.FIELD.STATUS + "=?",
                new String[] { String.valueOf(contactsStatus) }, null, null, t_contacts.FIELD.PINYIN);
        List<Contacts> result = new ArrayList<Contacts>();
        if (c != null) {
            while (c.moveToNext()) {
                result.add(parseCursor(c));
            }
            c.close();
        }
        db.close();
        return result;
    }

    public synchronized List<Contacts> queryContacts(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder("(");
        for (String i : ids) {
            sb.append("'" + i + "',");
        }
        sb.replace(sb.length() - 1, sb.length(), ")");
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_contacts.FIELD.STATUS + "=" + Contacts.STATUS_NORNAL + " and "
                + t_contacts.FIELD.ID + " in " + sb.toString(), null, null, null, null);
        List<Contacts> result = new ArrayList<Contacts>();
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        db.close();
        return result;
    }

    /**
     * 根据名字搜索
     * 
     * @param nameLike
     * @return
     */
    public synchronized List<Contacts> searchByNameOrPinyin(String nameOrPinyinLike) {
        if (nameOrPinyinLike == null) {
            nameOrPinyinLike = "";
        }
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db
                .query(mTableName, null, t_contacts.FIELD.STATUS + "=? and (" + t_contacts.FIELD.SHOW_NAME
                        + " like ? or " + t_contacts.FIELD.PINYIN + " like ?)",
                        new String[] { String.valueOf(Contacts.STATUS_NORNAL), nameOrPinyinLike + "%",
                                nameOrPinyinLike + "%" }, null, null, t_contacts.FIELD.PINYIN);
        List<Contacts> result = new ArrayList<Contacts>();
        if (c != null) {
            while (c.moveToNext()) {
                result.add(parseCursor(c));
            }
            c.close();
        }
        db.close();
        return result;
    }

    public synchronized List<Contacts> searchByLogisticType(int type_code) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_contacts.FIELD.STATUS + "=? and " + t_contacts.FIELD.LOGISTIC_TYPE_CODE
                + " =? ", new String[] { String.valueOf(Contacts.STATUS_NORNAL), String.valueOf(type_code) }, null,
                null, t_contacts.FIELD.PINYIN);
        List<Contacts> result = new ArrayList<Contacts>();
        if (c != null) {
            while (c.moveToNext()) {
                result.add(parseCursor(c));
            }
            c.close();
        }
        db.close();
        return result;
    }

    private Contacts parseCursor(Cursor c) {

        Contacts contacts = new Contacts();

        String id = c.getString(c.getColumnIndex(t_contacts.FIELD.ID));
        contacts.setId(id);
        String phone = c.getString(c.getColumnIndex(t_contacts.FIELD.PHONE));
        contacts.setPhone(phone);
        String logo_url = c.getString(c.getColumnIndex(t_contacts.FIELD.LOGO_URL));
        contacts.setLogo_url(logo_url);
        String show_name = c.getString(c.getColumnIndex(t_contacts.FIELD.SHOW_NAME));
        contacts.setShow_name(show_name);
        String pinyin = c.getString(c.getColumnIndex(t_contacts.FIELD.PINYIN));
        contacts.setPinyin(pinyin);
        String contacts_name = c.getString(c.getColumnIndex(t_contacts.FIELD.CONTACT_NAME));
        contacts.setContacts_name(contacts_name);
        String contacts_phone = c.getString(c.getColumnIndex(t_contacts.FIELD.CONTACT_PHONE));
        contacts.setContacts_phone(contacts_phone);
        String contacts_telephone = c.getString(c.getColumnIndex(t_contacts.FIELD.CONTACT_TELEPHONE));
        contacts.setContacts_telephone(contacts_telephone);
        String address = c.getString(c.getColumnIndex(t_contacts.FIELD.ADDRESS));
        contacts.setAddress(address);
        String qq = c.getString(c.getColumnIndex(t_contacts.FIELD.QQ));
        contacts.setQq(qq);
        String email = c.getString(c.getColumnIndex(t_contacts.FIELD.EMAIL));
        contacts.setEmail(email);
        String wechat = c.getString(c.getColumnIndex(t_contacts.FIELD.WECHAT));
        contacts.setWechat(wechat);
        int star_level = c.getInt(c.getColumnIndex(t_contacts.FIELD.STAR_LEVEL));
        contacts.setStar_level(star_level);

        String relation_id = c.getString(c.getColumnIndex(t_contacts.FIELD.RELATION_ID));
        contacts.setRelation_id(relation_id);
        long relation_time = c.getLong(c.getColumnIndex(t_contacts.FIELD.RELATION_TIME));
        contacts.setRelation_time(relation_time);
        long update_time = c.getLong(c.getColumnIndex(t_contacts.FIELD.UPDATE_TIME));
        contacts.setUpdate_time(update_time);
        String remark = c.getString(c.getColumnIndex(t_contacts.FIELD.REMARK));
        contacts.setRemark(remark);
        String remark_pinyin = c.getString(c.getColumnIndex(t_contacts.FIELD.REMARK_PINYIN));
        contacts.setRemark_pinyin(remark_pinyin);
        String remark_desc = c.getString(c.getColumnIndex(t_contacts.FIELD.REMARK_DESC));
        contacts.setRemark_desc(remark_desc);
        int status = c.getInt(c.getColumnIndex(t_contacts.FIELD.STATUS));
        contacts.setStatus(status);

        int type_code = c.getInt(c.getColumnIndex(t_contacts.FIELD.LOGISTIC_TYPE_CODE));
        contacts.setLogistic_type_code(type_code);
        String type_name = c.getString(c.getColumnIndex(t_contacts.FIELD.LOGISTIC_TYPE_NAME));
        contacts.setLogistic_type_name(type_name);
        contacts.setUserRole(parseRole(c));
        return contacts;
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

    public void addObserver(ContactsObserver observer) {
        if (observer == null) {
            return;
        }
        if (mObservers == null) {
            mObservers = new ArrayList<ContactsObserver>();
        }
        mObservers.add(observer);
    }

    public void removeObserver(ContactsObserver observer) {
        if (mObservers != null && mObservers.size() > 0 && observer != null) {
            mObservers.remove(observer);
        }
    }

    public interface ContactsObserver {
        void onContactsChange();
    }

    public static void groupByPinyin(List<Contacts> contactss, List<String> titles, List<List<Contacts>> lists) {
        Collections.sort(contactss);
        String temp = "#";
        List<Contacts> temps = new ArrayList<Contacts>();
        for (Contacts c : contactss) {
            String pinyin = c.getRemark_pinyin();
            if (TextUtils.isEmpty(pinyin)) {
                pinyin = c.getPinyin();
            }

            if (TextUtils.isEmpty(pinyin)) {
                temps.add(c);
                continue;
            }
            String ch = String.valueOf(pinyin.charAt(0)).toUpperCase(Locale.getDefault());
            if (!titles.contains(ch)) {
                titles.add(ch);
                List<Contacts> list = new ArrayList<Contacts>();
                lists.add(list);
            }
            int index = titles.indexOf(ch);
            lists.get(index).add(c);
        }
        if (!temps.isEmpty()) {
            titles.add(titles.size(), temp);
            lists.add(lists.size(), temps);
        }
    }
}
