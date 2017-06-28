package com.bizhub.hbase.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * hbase 列名
 * @author jannal
 */
@Target({ElementType.FIELD})  
@Retention(RetentionPolicy.RUNTIME)  
public @interface Column {

    /**
     * Column name
     * @return
     */
	String columnName();
	
	/**
	 * 是否是作为rowkey，暂时rowkey只能使用字符串
	 * @return
	 */
	boolean isRowName() default false;
	
}
