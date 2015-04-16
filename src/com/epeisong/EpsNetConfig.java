package com.epeisong;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 常量
 * 
 * @author poet
 * 
 */
public class EpsNetConfig {

    public static String WALLET_SERVE_URL = "http://payment.epeisong.com:9999/payment-ws/rest/";
    public static String LOGISTICS_SERVE_URL = "https://logistics.epeisong.com:9997/logistics-ws/rest/";
    public static String TRANSACTION_URL = "https://transaction.epeisong.com:9998/transaction-ws/rest/";

    public static String HOST = "register.epeisong.com"; // 外网:短链接注册服务器
    public static int PORT = 20141;

    /**
     * 获取交易服务器路径
     * @return
     */
    public static String getTransactionUrl() {
        String host = getHost();
        if (host.contains("192.168.1")) {
            String url = TRANSACTION_URL.replace("transaction.epeisong.com", host);
            if (host.contains("192.168.1.32")) {
                url = url.replace("https", "http");
            }
            return url;
        }
        return TRANSACTION_URL;
    }

    /**
     * 获取业务服务器路径
     * @return
     */
    public static String getLogisticsServeUrl() {
        String host = getHost();
        if (host.contains("192.168.1")) {
            String url = LOGISTICS_SERVE_URL.replace("logistics.epeisong.com", host);
            if (host.contains("192.168.1.32")) {
                url = url.replace("https", "http");
            }
            return url;
        }
        return LOGISTICS_SERVE_URL;
    }

    public static String getHost() {
        SharedPreferences sp = EpsApplication.getInstance().getSharedPreferences("config", Context.MODE_PRIVATE);
        String host = sp.getString("host", null);
        if (host == null) {
            host = EpsNetConfig.HOST;
        }
        return host;
    }

    public static void setHost(String host) {
        SharedPreferences sp = EpsApplication.getInstance().getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putString("host", host).commit();
    }

}
