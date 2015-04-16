package com.epeisong.base.db;

/**
 * 条件
 *
 * @author poet
 * @date 2015-4-5 上午3:33:21
 */
public class Condition {

    private StringBuilder sb = new StringBuilder();

    private String orderBy;

    public String getCondition() {
        return sb.toString();
    }

    public String getOrderBy() {
        return orderBy;
    }

    public Condition begin() {
        sb.append("(");
        return this;
    }

    public Condition end() {
        sb.append(")");
        return this;
    }

    public Condition and() {
        sb.append(" and ");
        return this;
    }

    public Condition or() {
        sb.append(" or ");
        return this;
    }

    public Condition compare(String operator, String key, Object value) {
        if (value instanceof String) {
            sb.append(key + operator + "'" + value + "'");
        } else {
            sb.append(key + operator + value);
        }
        return this;
    }

    public Condition equal(String key, Object value) {
        return compare("=", key, value);
    }

    public Condition than(String key, Object value) {
        return compare(">", key, value);
    }

    public Condition thanAndEqual(String key, Object value) {
        return compare(">=", key, value);
    }

    public Condition less(String key, Object value) {
        return compare("<", key, value);
    }

    public Condition lessAndEqual(String key, Object value) {
        return compare("<=", key, value);
    }

    public Condition orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }
}
