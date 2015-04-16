package com.epeisong.base.view;

import java.util.ArrayList;
import java.util.List;

import com.epeisong.base.view.CustomTitle.Action;

/**
 * 自定义标题栏的参数封装
 * @author poet
 * 
 */
public class TitleParams {
    private boolean homeActionEnable = true;
    private boolean showLogo = false;
    private Action homeAction;
    private String title;
    private boolean showLeftTitle;
    private List<Action> actions;

    private int resId;

    public TitleParams(Action homeAction, String title, List<Action> actions) {
        super();
        this.homeAction = homeAction;
        this.title = title;
        this.actions = actions;
    }

    public TitleParams(Action homeAction, String title) {
        super();
        this.homeAction = homeAction;
        this.title = title;
    }

    public Action getHomeAction() {
        return homeAction;
    }

    public String getTitle() {
        return title;
    }

    public List<Action> getActions() {
        return actions;
    }

    public TitleParams setAction(Action action) {
        actions = new ArrayList<CustomTitle.Action>();
        actions.add(action);
        return this;
    }

    public int getTitleBackgroudResource() {
        return resId;
    }

    public TitleParams setHomeActionEnable(boolean enable) {
        homeActionEnable = enable;
        return this;
    }

    public boolean isHomeActionEnable() {
        return homeActionEnable;
    }

    public TitleParams setShowLogo(boolean show) {
        showLogo = show;
        return this;
    }

    public boolean isShowLogo() {
        return showLogo;
    }

    public boolean isShowLeftTitle() {
        return showLeftTitle;
    }

    public TitleParams setShowLeftTitle() {
        this.showLeftTitle = true;
        return this;
    }
}
