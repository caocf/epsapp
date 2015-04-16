package com.bdmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.epeisong.model.User;
import com.epeisong.utils.DimensionUtls;

/**
 * 地图工具类
 * @author poet
 *
 */
public class MapUtils {

    public static List<ArrayList<User>> merge(List<User> list, int zoomTo) {
        int scale = getScale(zoomTo);
        List<ArrayList<User>> result = new ArrayList<ArrayList<User>>();
        if (list != null && !list.isEmpty()) {
            loop(result, list, scale);
        }
        return result;
    }

    private static void loop(List<ArrayList<User>> result, List<User> list, int scale) {
        Iterator<User> it = list.iterator();
        ArrayList<User> item = null;
        LatLng ll = null;
        while (it.hasNext()) {
            User user = it.next();
            if (user.getUserRole().getCurrent_latitude() == 0) {
                it.remove();
                continue;
            }
            if (item == null) {
                item = new ArrayList<User>();
                result.add(item);
                item.add(user);
                it.remove();
                ll = new LatLng(user.getUserRole().getCurrent_latitude(), user.getUserRole().getCurrent_longitude());
            } else {
                LatLng latLng = new LatLng(user.getUserRole().getCurrent_latitude(), user.getUserRole()
                        .getCurrent_longitude());
                if (DistanceUtil.getDistance(ll, latLng) < scale) {
                    item.add(user);
                    it.remove();
                }
            }
        }
        if (!list.isEmpty()) {
            loop(result, list, scale);
        }
    }

    // 视野半径转换缩放等级
    public static int getZoomTo(double screenWidthInMeter) {
        int scale = (int) (screenWidthInMeter / DimensionUtls.getScreenWidthInCm());
        if (scale <= 500) {
            return 15;
        } else if (scale <= 1000) {
            return 14;
        } else if (scale <= 2000) {
            return 13;
        } else if (scale <= 5000) {
            return 12;
        } else if (scale <= 10000) {
            return 11;
        } else if (scale <= 20000) {
            return 10;
        } else if (scale <= 25000) {
            return 9;
        } else if (scale <= 50000) {
            return 8;
        } else if (scale <= 100 * 1000) {
            return 7;
        } else if (scale <= 200 * 1000) {
            return 6;
        } else if (scale <= 500 * 1000) {
            return 5;
        } else if (scale <= 1000 * 1000) {
            return 4;
        } else if (scale <= 2000 * 1000) {
            return 3;
        } else {
            return 3;
        }
    }

    // 缩放等级转换为比例尺
    public static int getScale(int zoomTo) {
        switch (zoomTo) {
        case 19:
            return 20;
        case 18:
            return 50;
        case 17:
            return 100;
        case 16:
            return 200;
        case 15:
            return 500;
        case 14:
            return 1000;
        case 13:
            return 2 * 1000;
        case 12:
            return 5 * 1000;
        case 11:
            return 10 * 1000;
        case 10:
            return 20 * 1000;
        case 9:
            return 25 * 1000;
        case 8:
            return 50 * 1000;
        case 7:
            return 100 * 1000;
        case 6:
            return 200 * 1000;
        case 5:
            return 500 * 1000;
        case 4:
            return 1000 * 1000;
        case 3:
            return 2000 * 1000;
        default:
            return 0;
        }
    }
}
