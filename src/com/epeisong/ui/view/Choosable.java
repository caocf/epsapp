package com.epeisong.ui.view;

import android.view.View;

import com.epeisong.base.activity.XBaseActivity.OnChooseDictionaryListener;

public interface Choosable {

	View getView();
	
	OnChooseDictionaryListener getOnChooseDictionaryListener();
	
	int getChooseDictionaryType();
	
	String getChooseTitle();

	Choosion getDefaultChoosion();

	public class Choosion {
		private int code;
		private String name;

		public Choosion() {

		}

		public Choosion(int code, String name) {
			super();
			this.code = code;
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
}
