package com.epeisong.model;

import android.content.ContentValues;

import com.epeisong.base.view.FlowTextLayout.Textable;
import com.epeisong.data.dao.helper.EpsTagDaoHelper.t_eps_tag;

/**
 * 标签
 * @author poet
 *
 */
public class EpsTag implements Textable {

    private int id;
    private String name;

    public EpsTag(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getText() {
        return getName();
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(t_eps_tag.FIELD.ID, id);
        values.put(t_eps_tag.FIELD.NAME, name);
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof EpsTag)) {
            return false;
        }
        return ((EpsTag) o).getId() == id;
    }
}
