package com.epeisong.base.view.statusview;

import java.util.ArrayList;
import java.util.List;

public abstract class StatusModel {

    private int onResId;
    private int offResId;
    private String text;
    private boolean isOn;

    public StatusModel(int onResId, int offResId, String text) {
        super();
        this.onResId = onResId;
        this.offResId = offResId;
        this.text = text;
        this.isOn = getIsOn();
    }

    protected abstract boolean getIsOn();

    public int getResId() {
        if (isOn) {
            return onResId;
        }
        return offResId;
    }

    public String getText() {
        return text;
    }

    public boolean isOn() {
        return isOn;
    }

    public static void clearInvalid(List<StatusModel> list) {
        List<StatusModel> invalidList = new ArrayList<StatusModel>();
        int onIndex = 0;
        for (int i = list.size() - 1; i >= 0; i--) {
            StatusModel model = list.get(i);
            if (model.isOn()) {
                onIndex = i;
            } else {
                if (i < onIndex) {
                    invalidList.add(model);
                }
            }
        }
        if (!invalidList.isEmpty()) {
            list.removeAll(invalidList);
        }
    }
}
