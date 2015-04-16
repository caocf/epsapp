package com.epeisong.utils;

import android.text.TextUtils;

import com.epeisong.utils.android.AsyncTask;

import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.PhoneContactsDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetAddContacts;
import com.epeisong.data.net.NetContactsUpdateStatus;
import com.epeisong.data.net.parser.ContactsParser;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEBizLogistics;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.ContactReq.Builder;
import com.epeisong.model.Contacts;

/**
 * 联系人操作工具类
 * @author poet
 *
 */
public class ContactsUtils {

    public static final String STR_ADD_CONTACTS = "添加联系人";
    public static final String STR_RM_CONTACTS = "删除联系人 ";
    public static final String STR_ADD_BLACK = "加入黑名单";
    public static final String STR_RM_BLACK = "移出黑名单";
    public static final String STR_COMPLAIN = "投诉";

    public static void onContactsOption(String optionName, String id, OnContactsUtilsListener listener) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        if (STR_ADD_CONTACTS.equals(optionName)) {
            ContactsUtils.add(id, listener);
        } else if (STR_ADD_BLACK.equals(optionName)) {
            ContactsUtils.black(id, listener);
        } else if (STR_RM_CONTACTS.equals(optionName)) {
            ContactsUtils.delete(id, listener);
        } else if (STR_RM_BLACK.equals(optionName)) {
            ContactsUtils.delete(id, listener);
        }
    }

    public static void add(final String id, final OnContactsUtilsListener listener) {
        Contacts c = ContactsDao.getInstance().queryById(id);
        if (c != null) {
            update(id, Contacts.STATUS_NORNAL, listener);
            return;
        }
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                NetAddContacts net = new NetAddContacts() {
                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setContactId(Integer.parseInt(id));
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        ProtoEBizLogistics logistics = resp.getBizLogistics(0);
                        if (logistics != null) {
                            Contacts c = ContactsParser.parse(logistics);
                            c.setStatus(Contacts.STATUS_NORNAL);
                            c.setUpdate_time(resp.getUpdateTime());
                            ContactsDao.getInstance().replace(c);
                            PhoneContactsDao.getInstance().updateAdded(c.getPhone());

                            return true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                boolean success = result != null && result;
                callListener(listener, OnContactsUtilsListener.option_add, success);
            }
        };
        task.execute();
    }

    public static void delete(String id, OnContactsUtilsListener listener) {
        update(id, Properties.CONTACT_STATUS_DELETED, listener);
    }

    public static void black(String id, OnContactsUtilsListener listener) {
        update(id, Properties.CONTACT_STATUS_ISBLACK, listener);
    }

    private static void update(final String id, final int newStatus, final OnContactsUtilsListener listener) {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                NetContactsUpdateStatus net = new NetContactsUpdateStatus() {
                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setContactId(Integer.parseInt(id));
                        req.setNewStatus(newStatus);
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        Contacts contacts = ContactsDao.getInstance().queryById(id);
                        if (contacts == null) {
                            return false;
                        }
                        if (newStatus == Properties.CONTACT_STATUS_DELETED) {
                            ContactsDao.getInstance().delete(contacts);
                            PhoneContactsDao.getInstance().updateUnAdd(contacts.getPhone());
                        } else if (newStatus == Properties.CONTACT_STATUS_ISBLACK) {
                            contacts.setStatus(newStatus);
                            contacts.setUpdate_time(resp.getUpdateTime()); // 接口中没有返回，这里加100，仅为了replace成功。最终需要调试接口。
                            ContactsDao.getInstance().replace(contacts);
                        } else if (newStatus == Properties.CONTACT_STATUS_NORMAL) {
                            contacts.setStatus(newStatus);
                            contacts.setUpdate_time(resp.getUpdateTime());
                            ContactsDao.getInstance().replace(contacts);
                            PhoneContactsDao.getInstance().updateAdded(contacts.getPhone());
                        }
                        return true;
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                boolean success = result != null && result;
                if (newStatus == Properties.CONTACT_STATUS_DELETED) {
                    callListener(listener, OnContactsUtilsListener.option_delete, success);
                } else if (newStatus == Properties.CONTACT_STATUS_ISBLACK) {
                    callListener(listener, OnContactsUtilsListener.option_black, success);
                } else if (newStatus == Properties.CONTACT_STATUS_NORMAL) {
                    callListener(listener, OnContactsUtilsListener.option_add, success);
                }
            }
        };
        task.execute();
    }

    private static void callListener(OnContactsUtilsListener listener, int option, boolean success) {
        if (listener != null) {
            listener.onContactsUtilsComplete(option, success);
        }
    }

    public static interface OnContactsUtilsListener {
        public static final int option_add = 1;
        public static final int option_delete = 2;
        public static final int option_black = 3;

        void onContactsUtilsComplete(int option, boolean success);
    }
}
