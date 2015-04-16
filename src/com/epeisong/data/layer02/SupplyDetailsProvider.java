package com.epeisong.data.layer02;

import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetFreightDetail;
import com.epeisong.data.net.NetLogisticsInfo;
import com.epeisong.data.net.parser.FreightParser;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.model.Freight;
import com.epeisong.model.User;

/**
 * 
 * @author 孙灵洁
 * 
 */
public class SupplyDetailsProvider {

    public ProvideResult provide(String user_id, String freight_id) {

        User user = null;
        if (user == null) {
            user = getUserFromNet(user_id);
        }

        Freight f = getFreightFromNet(freight_id);
        ProvideResult result = new ProvideResult();

        result.setUser(user);
        result.setFreight(f);
        return result;
    }

    private User getUserFromNet(final String id) {

        NetLogisticsInfo net = new NetLogisticsInfo() {

            @Override
            protected boolean onSetRequest(LogisticsReq.Builder req) {
                // TODO Auto-generated method stub

                req.setLogisticsId(Integer.parseInt(id));
                return true;
            }

        };

        try {
            CommonLogisticsResp.Builder resp = net.request();
            if (resp != null) {
                return UserParser.parseSingleUser(resp);
            }
        } catch (NetGetException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private Freight getFreightFromNet(final String id) {
        NetFreightDetail net = new NetFreightDetail() {

            @Override
            protected boolean onSetRequest(FreightReq.Builder req) {
                req.setFreightId(Integer.parseInt(id));
                return true;
            }
        };
        try {
            FreightResp.Builder resp = net.request();
            return FreightParser.parseSingle(resp);
        } catch (NetGetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class ProvideResult {
        private User user;
        private Freight mFreight;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Freight getmFreight() {
            return mFreight;
        }

        public void setFreight(Freight mFreight) {
            this.mFreight = mFreight;
        }

    }

}
