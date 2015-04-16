package com.epeisong.ui.view;

import android.view.View;

public interface Choosable_old {
	
	View getView();

	Choosion getDefaultChoosion();
	
	public class Choosion {
		private int code;
		private String name;

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
