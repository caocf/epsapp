package com.epeisong.data.layer02;

import com.epeisong.data.dao.BulletinDao;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetDeleteBulletin;
import com.epeisong.logistics.proto.Eps.BulletinReq;
import com.epeisong.logistics.proto.Eps.BulletinResp;
import com.epeisong.model.Bulletin;
import com.epeisong.model.Contacts;

public class BulletinDetailProvider {

    // 通过id获取公告和联系人
    public Result getById(String cid, String bid) {

        Contacts c = ContactsDao.getInstance().queryById(cid);
        Bulletin b = BulletinDao.getInstance().queryById(bid);
        Result result = new Result();
        result.setBulletin(b);
        result.setContacts(c);
        return result;
    }

    // 通过id删除对应的公告
    public void delById(String bid) {

        Bulletin b = BulletinDao.getInstance().queryById(bid);
        BulletinDao.getInstance().delete(b);
    }

    // 删除自己发布的公告
    public boolean delOneSelfById(final String bid) {

        NetDeleteBulletin net = new NetDeleteBulletin() {
            @Override
            protected boolean onSetRequest(BulletinReq.Builder req) {
                req.setBulletinId(Integer.valueOf(bid));
                req.setNewStatus(Bulletin.status_web_deleted);
                return true;
            }
        };
        try {
            BulletinResp.Builder resp = net.request();
            if (resp != null && "SUCC".equals(resp.getResult())) {
                return true;
            }
        } catch (NetGetException e) {
            e.printStackTrace();
        }
        return false;
    }

    public class Result {

        private Contacts contacts;
        private Bulletin bulletin;

        public Contacts getContacts() {
            return contacts;
        }

        public void setContacts(Contacts contacts) {
            this.contacts = contacts;
        }

        public Bulletin getBulletin() {
            return bulletin;
        }

        public void setBulletin(Bulletin bulletin) {
            this.bulletin = bulletin;
        }
    }

}
