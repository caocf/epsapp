package com.epeisong.data.dao.util;

/**
 * 数据库操作的类型
 * @author poet
 *
 */
public enum CRUD {

    REPLACE {
        @Override
        public String stringValue() {
            return "replace";
        }
    },
    CREATE {
        @Override
        public String stringValue() {
            return "create";
        }
    },
    READ {
        @Override
        public String stringValue() {
            return "read";
        }
    },
    UPDATE {
        @Override
        public String stringValue() {
            return "update";
        }
    },
    DELETE {
        @Override
        public String stringValue() {
            return "delete";
        }
    };

    public abstract String stringValue();
}
