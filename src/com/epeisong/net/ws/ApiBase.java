package com.epeisong.net.ws;

import com.epeisong.data.dao.UserDao;
import com.epeisong.net.ws.utils.Http;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SpUtils;
import com.google.gson.Gson;

/**
 * WebService请求基类
 * @author poet
 *
 */
public class ApiBase {

    protected <T> T getResult(String url, Class<T> clazz) throws Exception {
        LogUtils.d(this, url);
        String json = Http.request(url);
        Gson gson = new Gson();
        T t = gson.fromJson(json, clazz);
        return t;
    }

    public String getUname() {
        return UserDao.getInstance().getUser().getPhone();
    }

    public String getUpwd() {
        return SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null);
    }
}
