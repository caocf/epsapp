package com.epeisong.base.adapter.item;

import android.app.Activity;

/**
 * 图标 + 文字：ListView选项item的对象封装
 * 
 * @author poet
 * 
 */
public class IconTextItem {
	private int iconResId;
	private String name;
	private Class<? extends Activity> clazz;
	private Runnable runnable;
	private boolean selectable;
	private boolean selected;

	public IconTextItem(int iconResId, String name,
			Class<? extends Activity> clazz) {
		super();
		this.iconResId = iconResId;
		this.name = name;
		this.clazz = clazz;
	}

	public int getIconResId() {
		return iconResId;
	}

	public void setIconResId(int iconResId) {
		this.iconResId = iconResId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<? extends Activity> getClazz() {
		return clazz;
	}

	public void setClazz(Class<? extends Activity> clazz) {
		this.clazz = clazz;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	public void setRunnable(Runnable runnable) {
		this.runnable = runnable;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public boolean isSelected() {
		return selected;
	}

	public IconTextItem setSelected(boolean selected) {
		this.selected = selected;
		selectable = true;
		return this;
	}

}
