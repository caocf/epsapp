package com.epeisong.data.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.model.Region;
import com.epeisong.model.RegionResult;
import com.epeisong.ui.activity.ChooseRegionActivity;

public class RegionDao {

    private static RegionDao dao = new RegionDao();

    private static String mDbPath;

    private static String mTableName = "sys_region";

    private RegionDao() {
    }

    public static RegionDao getInstance() {
        Context context = EpsApplication.getInstance();
        mDbPath = context.getFilesDir() + "/sys_region.db";
        File db = new File(mDbPath);
        if (!db.exists()) {
            InputStream in = context.getResources().openRawResource(R.raw.sys_region);
            FileOutputStream fos = null;
            try {
                fos = context.openFileOutput("sys_region.db", Context.MODE_PRIVATE);
                int len = 0;
                byte[] bys = new byte[1024];
                while ((len = in.read(bys)) != -1) {
                    fos.write(bys, 0, len);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.flush();
                    fos.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dao;
    }

    /**
     * 查询指定范围的地区，key为空时，显示全部
     * 
     * @param filter
     * @param key
     * @return
     */
    public List<Region> query(int filter, String key) {
        String selection = "(" + Field.TYPE;
        List<String> args = new ArrayList<String>();
        if (filter == ChooseRegionActivity.FILTER_0_3) {
            selection += "<=? or " + Field.TYPE + "=?";
            args.add("3");
            args.add("11");
        } else if (filter == ChooseRegionActivity.FILTER_0_2) {
            selection += "<=? or " + Field.TYPE + "=?";
            args.add("2");
            args.add("11");
        } else if (filter == ChooseRegionActivity.FILTER_2) {
            selection += "=? or " + Field.TYPE + "=?";
            args.add("2");
            args.add("11");
        } else if (filter == ChooseRegionActivity.FILTER_1_3) {
            selection += ">=? or " + Field.TYPE + "=?";
            args.add("1");
            args.add("11");
        } else {
            return null;
        }
        selection += ")";
        if (!TextUtils.isEmpty(key)) {
            selection += " and (" + Field.NAME + " like ? or " + Field.SPELLING + " like ? or " + Field.SPELLING_ABBR
                    + " like ?" + ")";
            args.add(key + "%");
            args.add(key + "%");
            args.add(key + "%");
        }
        String[] selectionArgs = new String[args.size()];
        args.toArray(selectionArgs);
        return query(selection, selectionArgs);
    }

    private synchronized List<Region> query(String selection, String[] selectionArgs) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.query(mTableName, null, selection, selectionArgs, null, null, Field.SPELLING);
        List<Region> list = new ArrayList<Region>();
        while (c.moveToNext()) {
            list.add(parseCursor(c));
        }
        c.close();
        db.close();
        return list;
    }

    public synchronized Region queryByCode(int code) {
        return queryByCodePrivate(code);
    }

    public synchronized Region queryByCityName(String cityName) {
        if (cityName == null) {
            return null;
        }
        SQLiteDatabase db = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.query(mTableName, null, Field.NAME + "=?", new String[] { cityName }, null, null, null);
        Region region = null;
        if (c.moveToNext()) {
            region = parseCursor(c);
        }
        c.close();
        db.close();
        return region;
    }

    public synchronized Region queryByDistrictNameAndCityCode(String districtName, int cityCode) {
        int startCode = cityCode * 100;
        int endCode = (cityCode + 1) * 100;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.query(mTableName, null, Field.NAME + "=? and " + Field.CODE + " >? and " + Field.CODE + "<?",
                new String[] { districtName, String.valueOf(startCode), String.valueOf(endCode) }, null, null, null);
        Region region = null;
        if (c.moveToNext()) {
            region = parseCursor(c);
        }
        c.close();
        db.close();
        return region;
    }

    public synchronized Region queryParent(int code) {
        Region parent = null;
        while (code > 10) {
            Region region = null;
            if (code > 1000) {
                code = code / 100;
                region = queryByCodePrivate(code);
            } else {
                if (code < 100 && code > 10) {
                    region = queryByCodePrivate(code);
                    code = code / 10;
                }
            }
            if (region != null) {
                if (parent == null) {
                    parent = region;
                } else {
                    parent.setParent(region);
                }
            } else {
                break;
            }
        }
        return parent;
    }

    private Region queryByCodePrivate(int code) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.query(mTableName, null, Field.CODE + "=?", new String[] { String.valueOf(code) }, null, null,
                null);
        Region region = null;
        if (c.moveToNext()) {
            region = parseCursor(c);
        }
        c.close();
        db.close();
        return region;
    }

    private Region parseCursor(Cursor c) {
        Region region = new Region();
        region.setCode(c.getInt(c.getColumnIndex(Field.CODE)));
        region.setName(c.getString(c.getColumnIndex(Field.NAME)));
        region.setNote(c.getString(c.getColumnIndex(Field.NOTE)));
        region.setSpelling(c.getString(c.getColumnIndex(Field.SPELLING)));
        region.setSpelling_abbr(c.getString(c.getColumnIndex(Field.SPELLING_ABBR)));
        region.setType(c.getInt(c.getColumnIndex(Field.TYPE)));
        return region;
    }

    public static class Field {
        public static String CODE = "code";
        public static String NAME = "name";
        public static String NOTE = "note";
        public static String SPELLING = "spelling";
        public static String SPELLING_ABBR = "spelling_abbr";
        public static String TYPE = "type";
    }

    public static void groupByPinyin(List<Region> data, List<String> titles, List<List<Region>> lists) {
        for (Region region : data) {
            String ch = String.valueOf(region.getSpelling().charAt(0)).toUpperCase(Locale.getDefault());
            if (!titles.contains(ch)) {
                titles.add(ch);
                List<Region> list = new ArrayList<Region>();
                lists.add(list);
            }
            int index = titles.indexOf(ch);
            lists.get(index).add(region);
        }
    }

    public static RegionResult convertToResult(Region region) {
        if (region == null) {
            return null;
        }
        if (region.getType() > 0) {
            region.setParent(RegionDao.getInstance().queryParent(region.getCode()));
        }
        RegionResult result = new RegionResult();
        result.setType(region.getType());
        if (region.getCode() == -1) {
            result.setCode(region.getCode());
            result.setRegionName(region.getName());
        } else {
            result.setCode(region.getCode());
            while (region != null) {
                if (region.getType() == 3) {
                    result.setDistrictName(region.getName());
                } else if (region.getType() == 2 || region.getType() == 11) {
                    result.setCityName(region.getName());
                } else if (region.getType() == 1 || region.getType() == 11) {
                    result.setProvinceName(region.getName());
                } else if (region.getType() == 0) {
                    result.setRegionName(region.getName());
                }
                region = region.getParent();
            }
        }
        return result;
    }
}
