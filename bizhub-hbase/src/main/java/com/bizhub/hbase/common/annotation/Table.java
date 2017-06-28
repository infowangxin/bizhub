package com.bizhub.hbase.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jannal
 *表名注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {

	/**
	 * 表名
	 */
	String tableName();
	/**
	 * 列簇名
	 */
	String columnFamilyName();
	
}
