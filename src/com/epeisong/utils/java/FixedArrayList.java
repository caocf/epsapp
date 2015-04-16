package com.epeisong.utils.java;

import java.util.ArrayList;
import java.util.Collection;

public class FixedArrayList<E> extends ArrayList<E> {

	private static final long serialVersionUID = -3135918300249153468L;

	private int mFixedSize;

	public FixedArrayList(int fixedSize) {
		mFixedSize = fixedSize;
	}

	@Override
	public boolean add(E object) {
		while (size() >= mFixedSize) {
			remove(0);
		}
		return super.add(object);
	}

	@Override
	public void add(int index, E object) {
		boolean bottom = index >= size() / 2;
		while (size() >= mFixedSize) {
			if (bottom) {
				remove(0);
			} else {
				remove(size() - 1);
			}
		}
		super.add(index, object);
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		if (collection == null || collection.isEmpty()) {
			return false;
		}
		int newDataSize = collection.size();
		while (size() + newDataSize > mFixedSize) {
			remove(0);
		}
		return super.addAll(collection);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> collection) {
		if (collection == null || collection.isEmpty()) {
			return false;
		}
		boolean bottom = index >= size() / 2;
		int newDataSize = collection.size();
		while (size() + newDataSize > mFixedSize) {
			if (bottom) {
				remove(0);
			} else {
				remove(size() - 1);
			}
		}
		return super.addAll(index, collection);
	}
}
