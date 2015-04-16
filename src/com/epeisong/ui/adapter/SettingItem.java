package com.epeisong.ui.adapter;

public class SettingItem {

    public static final int type_invalid = -1;
    public static final int type_normal = 0;

    private int type;
    private int iconId;
    private String name;
    private String desc;
    private int arrowIconId;
    private Runnable runnable;
    private boolean hasNewMsg;

    public SettingItem() {
        this.type = type_invalid;
    }

    public SettingItem(int resId, String name, Runnable runnable) {
        super();
        this.type = type_normal;
        this.iconId = resId;
        this.name = name;
        this.runnable = runnable;
    }

    public int getType() {
        return type;
    }

    public SettingItem setType(int type) {
        this.type = type;
        return this;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getName() {
        return name;
    }

    public SettingItem setName(String name) {
        this.name = name;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public SettingItem setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public int getArrowIconId() {
        return arrowIconId;
    }

    public SettingItem setArrowIconId(int arrowIconId) {
        this.arrowIconId = arrowIconId;
        return this;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public SettingItem setRunnable(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    public boolean isHasNewMsg() {
        return hasNewMsg;
    }

    public SettingItem setHasNewMsg(boolean hasNewMsg) {
        this.hasNewMsg = hasNewMsg;
        return this;
    }

}
