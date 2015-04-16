package com.epeisong.plug;

import java.util.HashMap;
import java.util.Map;

/**
 * 提醒功能表描述
 * @author poet
 *
 */
public class TabPoint {

    public static final String NAME = "point";

    public static String getSql() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(FIELD.CODE, "TEXT UNIQUE PRIMARY KEY");
        map.put(FIELD.SHOW, "TEXT");
        map.put(FIELD.EXTRA01, "TEXT");
        map.put(FIELD.EXTRA02, "TEXT");
        map.put(FIELD.EXTRA03, "TEXT");
        map.put(FIELD.EXTRA04, "TEXT");

        StringBuilder sb = new StringBuilder("CREATE TABLE " + NAME + " (");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey() + " " + entry.getValue() + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.append(" );").toString();
    }

    public static interface FIELD {
        String CODE = "code";
        String SHOW = "show";
        String EXTRA01 = "extra01";
        String EXTRA02 = "extra02";
        String EXTRA03 = "extra03";
        String EXTRA04 = "extra04";
    }
}
