package com.test.request;

import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetSearchFreight;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.proto.Eps.FreightReq.Builder;
import com.epeisong.model.User;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.utils.SpUtils;
import com.test.request.RequestTestActivity.RequestResult;

public class RequestTest {

	public static long addnum=8600;
    public static RequestResult test(int requestCount, int index, int requestIndex) {
        return addUserRequest(requestCount, index, requestIndex);
//		return testSearchFreight();
    }
    /**
     * 
     * @param requestCount 该线程总请求次数
     * @param threadIndex  该线程序列号（第几个线程）
     * @param requestIndex 该线程请求序列号（第几次请求）
     * @return
     */
//    public static RequestResult test(int requestCount, int threadIndex, int requestIndex) {
//        return testSearchFreight();
//    }
    
    private static RequestResult addUserRequest(int requestCount, int index, int requestIndex) {
    	Resp resultResp=null;
    	String accountString2="";
    	RequestResult result = new RequestResult();

    	ApiExecutor api = new ApiExecutor();
        try {
        	//User user=UserDao.getInstance().getUser();
        	accountString2 = "1480000"+String.valueOf(addnum+(index-1)*requestCount+requestIndex-1);
        	resultResp=null;
        	//            resultResp = api.createAccountAndLogisticByPlatformManager(accountString2, 6, "物流园",
//            		"物流园添加", 3201, "南京", "13659230269",
//            		SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null), "111111"
//            		, Properties.OPERATION_TYPE_CREATE_ACCOUNT_AND_LOGISTIC_BY_PLATFORM_MANAGER, 
//            		Properties.APP_CLIENT_BIG_TYPE_PHONE, "123456");
            //addnum++;
        } catch (Exception e) {
            e.printStackTrace();
            result.log = accountString2+"NetGetException:" + e.getMessage();
        }
        
            if (resultResp == null) {
                result.log = accountString2+"resp == null";
            } else {
            	if (resultResp.getResult()==Resp.SUCC) {
                    result.success = true;
                    result.log = accountString2+"成功返回";
                } 
            	else
            		result.log = accountString2+"resp.desc:" + resultResp.getDesc();
            }
    	return result;
    }

    private static RequestResult testSearchFreight() {
        NetSearchFreight net = new NetSearchFreight() {
            @Override
            protected boolean onSetRequest(Builder req) {
                req.setLimitCount(10);
                req.setLogisticId(Integer.parseInt(UserDao.getInstance().getUser().getId()));
                return true;
            }
        };
        RequestResult result = new RequestResult();
        try {
            FreightResp.Builder resp = net.request();
            if (net.isSuccess(resp)) {
                result.success = true;
                result.log = "成功返回";
            } else {
                if (resp == null) {
                    result.log = "resp == null";
                } else {
                    result.log = "resp.desc:" + resp.getDesc();
                }
            }
        } catch (NetGetException e) {
            e.printStackTrace();
            result.log = "NetGetException:" + e.getMessage();
        }
        return result;
    }
}
