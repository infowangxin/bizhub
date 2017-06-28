package com.bizhub.hbase.common;

import java.io.Serializable;
import java.util.List;

public interface HbaseService {

    public <T extends Serializable> void save(final T t);
    
    public <T extends Serializable> void saveBatch(final List<T> list);
    
    public <T> List<T> findAll(final Class<T> clazz) ;
    
    public <T> T findOneByRowKeyValue(final Class<T> clazz,String rowNameValue) ;
    
    public <T> List<T> findFromStartToEndRowKey(final Class<T> clazz, String startRowKey, String endRowKey);
    
    public void deleteRow(String tablename, String rowkey,String  familyName);
    
    public void dropTable(String tableName);
    
    public <T> void deleteColumnFamily(String tableName, String columnName);
    
    public <T> void modifyColumnFamily(String tableName, String columnName);
    
}
