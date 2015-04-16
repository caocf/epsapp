package com.epeisong.model;

import java.io.Serializable;
import java.util.Locale;

public class Region implements Comparable<Region>, Serializable {

	private static final long serialVersionUID = -2826454401438999978L;
	
	public static int CATEGORY_LOC_ING = 1;	// 定位的region
	public static int CATEGORY_LOC_FAIL = 2;	// 定位的region
	public static int CATEGORY_LOC_SUCCESS = 3;	// 定位的region
	

	private int code;
	private String name;
	private String note;
	private String spelling;
	private String spelling_abbr;
	private int type;
	private int category;

	private Region parent;

	public int getFullCode() {
		if (code > 0) {
			if (code < 100000) {
				if (code > 1000) {
					return code * 100;
				} else if (code > 10) {
					return code * 10000;
				} else {
					return code * 100000;
				}
			}
		}
		return 0;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getSpelling() {
		return spelling;
	}

	public void setSpelling(String spelling) {
		this.spelling = spelling;
	}

	public String getSpelling_abbr() {
		return spelling_abbr;
	}

	public void setSpelling_abbr(String spelling_abbr) {
		this.spelling_abbr = spelling_abbr;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public Region getParent() {
		return parent;
	}

	public void setParent(Region parent) {
		this.parent = parent;
	}

	@Override
	public int compareTo(Region another) {
		if (another == null) {
			return 1;
		}
		return this
				.getSpelling()
				.toLowerCase(Locale.getDefault())
				.compareTo(
						another.getSpelling().toLowerCase(Locale.getDefault()));
	}
}
