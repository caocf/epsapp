package com.epeisong.base.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Property {

    /**
     * 常量：表示数据库主键ID
     */
    String TYPE_FOR_ID = "INTEGER PRIMARY KEY AUTOINCREMENT";

    /**
     * 是否是主键，默认不是
     * @return
     */
    boolean primaryKey() default false;

    /**
     * 字段类型，包括自增长、非空限制等
     * @return
     */
    String type() default "TEXT";
}
