package com.epeisong.data.model;

/**
 * 列表统一的model
 * 
 * @author poet
 * 
 */
public class BaseListModel {

    protected String id;
    protected long idOrSerial;
    protected DataType dataType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getIdOrSerial() {
        return idOrSerial;
    }

    public void setIdOrSerial(long idOrSerial) {
        this.idOrSerial = idOrSerial;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public enum DataType {
        BULLETIN, CHAT, FREIGHT
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof BaseListModel)) {
            return false;
        }
        if (getId() == null) {
            return false;
        }
        BaseListModel model = (BaseListModel) o;
        return getId().equals(model.getId());
    }
}
