package com.epeisong.ui.activity.user;

import java.util.ArrayList;
import java.util.List;

/**
 * TempActivity子类的管理者
 * @author poet
 *
 */
public class TempActivityManager {

	private static TempActivityManager instance;

	private List<TempActivity> activities;

	private TempActivityManager() {
		activities = new ArrayList<TempActivity>();
	}

	public static TempActivityManager getInstance() {
		if (instance == null) {
			synchronized (TempActivityManager.class) {
				if (instance == null) {
					instance = new TempActivityManager();
				}
			}
		}
		return instance;
	}

	public void add(TempActivity activity) {
		if (!activities.contains(activity)) {
			activities.add(activity);
		}
	}

	public void remove(TempActivity activity) {
		activities.remove(activity);
	}

	public void clear() {
		for(TempActivity a : activities) {
			a.finish();
		}
	}
}
