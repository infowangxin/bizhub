package com.bizhub.hbase.common;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.ResultsExtractor;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.bizhub.hbase.common.annotation.Column;
import com.bizhub.hbase.common.annotation.Table;
import com.bizhub.hbase.serialization.StringHbaseSerializer;

/**
 * hbase 通用处理方法
 * 
 * @author jannal
 */
@Service("hbaseService")
@SuppressWarnings("all")
public class HbaseServiceImpl implements HbaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String ROW_NAME = "rowName";
    @Autowired
    private HbaseTemplate hbaseTemplate;

    public <T extends Serializable> void save(final T t) {
        if (t == null) {
            throw new IllegalArgumentException(t.getClass().getName() + "对象不能为空");
        }
        final Table table = t.getClass().getAnnotation(Table.class);

        checkTableAnnotation(t, table);

        final String columnFamilyName = table.columnFamilyName();
        final String tableName = table.tableName();

        createTableIfNotExist(columnFamilyName, tableName);

        final Map<String/* columnName */, byte[]/* field值 */> map = new HashMap<String, byte[]>();

        // 遍历field
        ReflectionUtils.doWithLocalFields(t.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (field.isAnnotationPresent(Column.class)) {

                    fieldPreHandle(field);

                    Column column = field.getAnnotation(Column.class);
                    Object fieldValue = ReflectionUtils.getField(field, t);
                    if (fieldValue != null) {
                        map.put(column.columnName(), SerializationUtils.serialize((Serializable) fieldValue));
                    }
                    if (column.isRowName()) {
                        if (fieldValue == null) {
                            throw new IllegalArgumentException("rowkey:" + field.getName() + "不能为空,");
                        }
                        if (fieldValue.getClass() != String.class) {
                            throw new IllegalArgumentException("为了便于序列化统一管理与查看,目前rowkey仅仅支持字符串类型,而" + field.getName() + "是" + fieldValue.getClass());
                        }
                        // 此处不能使用jdk序列化，因为后面的其他方法的rowName参数是使用String
                        // rowName方式，如果是byte[]方式可以自定义序列化
                        StringHbaseSerializer stringHbaseSerializer = new StringHbaseSerializer();
                        map.put(ROW_NAME, stringHbaseSerializer.serialize(fieldValue.toString()));
                    }
                }
            }
        });

        byte[] rowKey = map.get(ROW_NAME);
        if (rowKey == null || rowKey.length == 0) {
            throw new IllegalArgumentException(t.getClass().getName() + "没有设置rowKey,请通过@Column的isRowName来指定是rowkey");
        }

        hbaseTemplate.execute(tableName, new TableCallback<T>() {
            public T doInTable(HTableInterface table) throws Throwable {
                Put p = new Put(rowKey);
                map.remove(ROW_NAME);
                Set<Entry<String, byte[]>> entrySet = map.entrySet();
                Iterator<Entry<String, byte[]>> iterator = entrySet.iterator();
                while (iterator.hasNext()) {
                    Entry<String, byte[]> entry = iterator.next();
                    p.addColumn(Bytes.toBytes(columnFamilyName), Bytes.toBytes(entry.getKey()), entry.getValue());
                }
                table.put(p);
                return t;
            }
        });
    }

    private <T extends Serializable> void checkTableAnnotation(final T t, final Table table) {
        if (table == null) {
            throw new IllegalArgumentException("请检查" + t.getClass().getName() + "注解@Table是否添加");
        }
    }

    public <T extends Serializable> void saveBatch(final List<T> list) {
        if (list == null || list.size() == 0) {
            throw new IllegalArgumentException("list不能为空");
        }
        int size = list.size();
        // 取第一个，获取表上的注解
        T first = list.get(0);
        final Table table = first.getClass().getAnnotation(Table.class);
        if (table == null) {
            throw new IllegalArgumentException("请检查" + first.getClass().getName() + "注解@Table是否添加");
        }

        final String columnFamilyName = table.columnFamilyName();
        final String tableName = table.tableName();

        createTableIfNotExist(columnFamilyName, tableName);

        List<Put> putList = new ArrayList<Put>();
        for (int i = 0; i < size; i++) {
            T t = list.get(i);

            final Map<String/* columnName */, byte[]/* field值 */> map = new HashMap<String, byte[]>();

            // 遍历field
            ReflectionUtils.doWithLocalFields(t.getClass(), new ReflectionUtils.FieldCallback() {
                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    if (field.isAnnotationPresent(Column.class)) {
                        fieldPreHandle(field);
                        Column column = field.getAnnotation(Column.class);
                        Object fieldValue = ReflectionUtils.getField(field, t);
                        if (fieldValue != null) {
                            map.put(column.columnName(), SerializationUtils.serialize((Serializable) fieldValue));
                        }
                        if (column.isRowName()) {
                            if (fieldValue == null) {
                                throw new IllegalArgumentException("rowkey:" + field.getName() + "不能为空,");
                            }
                            if (fieldValue.getClass() != String.class) {
                                throw new IllegalArgumentException("为了便于序列化统一管理与查看,目前rowkey仅仅支持字符串类型,而" + field.getName() + "是" + fieldValue.getClass());
                            }
                            // 此处不能使用jdk序列化，因为后面的其他方法的rowName参数是使用String
                            // rowName方式，如果是byte[]方式可以自定义序列化
                            StringHbaseSerializer stringHbaseSerializer = new StringHbaseSerializer();
                            map.put(ROW_NAME, stringHbaseSerializer.serialize(fieldValue.toString()));
                        }
                    }
                }
            });

            byte[] rowKey = map.get(ROW_NAME);
            if (rowKey == null || rowKey.length == 0) {
                throw new IllegalArgumentException(t.getClass().getName() + "没有设置rowKey,请通过@Column的isRowName来指定是rowkey");
            }

            Put p = new Put(rowKey);
            map.remove(ROW_NAME);
            Set<Entry<String, byte[]>> entrySet = map.entrySet();
            Iterator<Entry<String, byte[]>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Entry<String, byte[]> entry = iterator.next();
                p.addColumn(Bytes.toBytes(columnFamilyName), Bytes.toBytes(entry.getKey()), entry.getValue());
            }
            putList.add(p);
        }

        // 批量插入
        hbaseTemplate.execute(tableName, new TableCallback<T>() {
            public T doInTable(HTableInterface table) throws Throwable {
                table.put(putList);
                return null;
            }
        });
    }

    private void createTableIfNotExist(final String columnFamilyName, final String tableName) {
        // 判断表是否存在
        HBaseAdmin admin = null;
        try {
            admin = new HBaseAdmin(hbaseTemplate.getConfiguration());
            HColumnDescriptor columnDescriptor = null;
            if (!admin.tableExists(tableName)) {
                HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
                columnDescriptor = new HColumnDescriptor(columnFamilyName);
                tableDescriptor.addFamily(columnDescriptor);
                admin.createTable(tableDescriptor);
                checkTableAndEnable(tableName, admin);

            } else {
                HTableDescriptor tableDescriptor = admin.getTableDescriptor(TableName.valueOf(tableName));
                HColumnDescriptor[] columnFamilies = tableDescriptor.getColumnFamilies();
                if (columnFamilies == null || columnFamilies.length == 0) {
                    columnDescriptor = new HColumnDescriptor(columnFamilyName);
                } else {
                    String[] columnFamiliesNames = new String[columnFamilies.length];
                    for (int i = 0; i < columnFamilies.length; i++) {
                        columnFamiliesNames[i] = columnFamilies[i].getNameAsString();
                    }
                    if (Arrays.asList(columnFamiliesNames).contains(columnFamilyName)) {
                        // 不再添加已经存在的列
                        logger.info("{}增加新的ColumnFamily:[{}]已经存在，所以不再添加新的columFamily", tableName, columnFamilyName);
                        return;
                    } else {
                        // 修改表结构
                        columnDescriptor = new HColumnDescriptor(columnFamilyName);
                        tableDescriptor.addFamily(columnDescriptor);
                        admin.disableTable(tableName);
                        /**
                         * modifyTable只提供了异步的操作模式，如果需要确认修改是否已成功
                         * 需要在客户端代码中显示循环调用getTableDescriptor()获取元数据 知道结果与本地实例匹配
                         */
                        admin.modifyTable(tableName, tableDescriptor);
                        admin.enableTable(tableName);
                        // 获取远程元数据的HTableDescriptor对象
                        HTableDescriptor tableDescriptorFromMetaData = admin.getTableDescriptor(TableName.valueOf(tableName));
                        int count = 0;
                        while (true) {
                            /**
                             * 比较客户端本地的实例与从元数据获取的实例是否一致(包括所有列簇以及与他们相关的设置)
                             */
                            if (tableDescriptor.equals(tableDescriptorFromMetaData)) {
                                logger.info("{}增加新的ColumnFamily:[{}]修改成功", tableName, columnFamilyName);
                                break;
                            } else {
                                logger.warn("{}增加新的ColumnFamily:[{}]没有增加成功,继续循环等待异步返回", tableName, columnFamilyName);
                                try {
                                    count++;
                                    if (count == 10) {// 等待10秒如果还没有创建成功，就退出
                                        throw new RuntimeException(tableName + "{}增加新的ColumnFamily:[" + columnFamilyName + "]没有增加成功");
                                    }
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

            }

        } catch (MasterNotRunningException e) {
            throw new RuntimeException(e);
        } catch (ZooKeeperConnectionException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(admin);
        }
    }

    public void checkTableAndEnable(final String tableName, HBaseAdmin admin) throws IOException {
        boolean tableAvailable = admin.isTableAvailable(tableName);
        boolean tableEnabled = admin.isTableEnabled(tableName);
        if (tableAvailable && tableEnabled) {
            // 如果表不可用，先置为可用
            admin.enableTable(tableName);
        }
    }

    /**
     * 查找最近一个版本的一条数据 通过rowName的值查找 rowkey必须是字符串，并且是字符串序列化方式才可以使用此方法获取
     */
    public <T> T findOneByRowKeyValue(final Class<T> clazz, String rowNameValue) {
        final Table table = clazz.getAnnotation(Table.class);
        if (table != null) {
            try {
                final String columnFamilyName = table.columnFamilyName();
                final String tableName = table.tableName();
                final T newInstance = clazz.newInstance();
                final Map<Field/* field */, byte[]/* columnName值 */> map = new HashMap<Field, byte[]>();
                // 遍历field
                ReflectionUtils.doWithLocalFields(clazz, new ReflectionUtils.FieldCallback() {
                    @Override
                    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                        if (field.isAnnotationPresent(Column.class)) {
                            int modifiers = field.getModifiers();
                            if (Modifier.isStatic(modifiers)) {
                                return;
                            }
                            field.setAccessible(true);
                            Column column = field.getAnnotation(Column.class);
                            map.put(field, Bytes.toBytes(column.columnName()));
                        }
                    }
                });

                T t = hbaseTemplate.get(tableName, rowNameValue, columnFamilyName, new RowMapper<T>() {
                    @Override
                    public T mapRow(Result result, int rowNum) throws Exception {
                        byte[] row = result.getRow();
                        // 防止当T中的属性有初始化值时，是可以获取到的对象数据的，但是在hbase中是没有数据的
                        if (row == null && result.isEmpty()) {
                            return null;
                        }
                        Set<Entry<Field, byte[]>> entrySet = map.entrySet();
                        Iterator<Entry<Field, byte[]>> iterator = entrySet.iterator();
                        while (iterator.hasNext()) {
                            Entry<Field, byte[]> entry = iterator.next();
                            Field field = entry.getKey();
                            byte[] columnName = entry.getValue();
                            byte[] fieldValue = result.getValue(Bytes.toBytes(columnFamilyName), columnName);
                            if (fieldValue != null) {
                                field.set(newInstance, SerializationUtils.deserialize(fieldValue));
                            }
                        }
                        return newInstance;
                    }
                });
                return t;
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        } else {
            logger.warn("{}没有指定@Table注解", clazz.getName());
        }
        return null;
    }

    /**
     * 目前不支持静态属性
     */
    public <T> List<T> findAll(final Class<T> clazz) {
        final Table table = clazz.getAnnotation(Table.class);
        if (table != null) {
            final String columnFamilyName = table.columnFamilyName();
            final String tableName = table.tableName();

            final Map<String /* field 名称 */, byte[]/* field 值 */> map = new HashMap<String, byte[]>();
            List<T> list = hbaseTemplate.find(tableName, columnFamilyName, new RowMapper<T>() {
                @Override
                public T mapRow(Result result, int rowNum) throws Exception {
                    final T newInstance = clazz.newInstance();
                    ReflectionUtils.doWithLocalFields(clazz, new ReflectionUtils.FieldCallback() {
                        @Override
                        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                            int modifiers = field.getModifiers();
                            if (Modifier.isStatic(modifiers)) {
                                return;// 忽略静态属性
                            }
                            if (Modifier.isPrivate(modifiers)) {
                                field.setAccessible(true);
                            }

                            Column column = field.getAnnotation(Column.class);
                            map.put(field.getName(), result.getValue(Bytes.toBytes(columnFamilyName), Bytes.toBytes(column.columnName())));
                            // 属性赋值
                            byte[] fieldValue = result.getValue(Bytes.toBytes(columnFamilyName), Bytes.toBytes(field.getName()));
                            if (fieldValue != null) {
                                field.set(newInstance, SerializationUtils.deserialize(fieldValue));
                            }
                        }

                    });
                    return newInstance;
                }
            });

            return list;
        } else {
            logger.warn("{}没有指定@Table注解", clazz.getName());
        }
        return Collections.EMPTY_LIST;

    }

    public <T> List<T> findFromStartToEndRowKey(final Class<T> clazz, String startRowKey, String endRowKey) {
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes(startRowKey));
        scan.setStopRow(Bytes.toBytes(endRowKey));

        if (clazz == null) {
            throw new IllegalArgumentException(clazz + "对象不能为空");
        }
        final Table table = clazz.getAnnotation(Table.class);
        if (table == null) {
            throw new IllegalArgumentException("请检查" + clazz.getName() + "注解@Table是否添加");
        }

        List<T> list = new ArrayList<T>();
        final String columnFamilyName = table.columnFamilyName();
        final String tableName = table.tableName();
        final Map<String /* field 名称 */, byte[]/* field 值 */> map = new HashMap<String, byte[]>();

        hbaseTemplate.find(tableName, scan, new ResultsExtractor<T>() {
            @Override
            public T extractData(ResultScanner results) throws Exception {

                for (Result result : results) {
                    final T newInstance = clazz.newInstance();
                    ReflectionUtils.doWithLocalFields(clazz, new ReflectionUtils.FieldCallback() {
                        @Override
                        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                            int modifiers = field.getModifiers();
                            if (Modifier.isStatic(modifiers)) {
                                return;// 忽略静态属性
                            }
                            if (Modifier.isPrivate(modifiers)) {
                                field.setAccessible(true);
                            }

                            Column column = field.getAnnotation(Column.class);
                            map.put(field.getName(), result.getValue(Bytes.toBytes(columnFamilyName), Bytes.toBytes(column.columnName())));
                            // 属性赋值
                            byte[] fieldValue = result.getValue(Bytes.toBytes(columnFamilyName), Bytes.toBytes(field.getName()));
                            if (fieldValue != null) {
                                field.set(newInstance, SerializationUtils.deserialize(fieldValue));
                            }
                        }

                    });
                    list.add(newInstance);
                }
                return null;
            }

        });
        return list;
    }

    /**
     * 删除表前要将表设置为禁用 禁用可能会花费非常长的时间，甚至长达几分钟。这取决于在服务器内存中有多少近期更新的数据还没有 写入磁盘
     */
    public void dropTable(String tableName) {
        HBaseAdmin admin = null;
        try {
            admin = new HBaseAdmin(hbaseTemplate.getConfiguration());
            if (admin.tableExists(tableName)) {
                admin.disableTable(tableName);
                admin.deleteTable(tableName);
                logger.info("{}删除成功", tableName);
            } else {
                logger.warn("需要删除的表:{}不存在", tableName);
            }

        } catch (MasterNotRunningException e) {
            throw new RuntimeException(e);
        } catch (ZooKeeperConnectionException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(admin);
        }
    }

    public <T> void deleteColumnFamily(String tableName, String columnFamilyName) {
        HBaseAdmin admin = null;
        try {
            admin = new HBaseAdmin(hbaseTemplate.getConfiguration());
            HColumnDescriptor columnDescriptor = null;
            if (!admin.tableExists(tableName)) {
                throw new RuntimeException(tableName + "不存在,请先创建表再删除列簇");
            }

            HTableDescriptor tableDescriptor = admin.getTableDescriptor(TableName.valueOf(tableName));
            HColumnDescriptor[] columnFamilies = tableDescriptor.getColumnFamilies();
            String[] columnFamiliesNames = new String[columnFamilies.length];
            for (int i = 0; i < columnFamilies.length; i++) {
                columnFamiliesNames[i] = columnFamilies[i].getNameAsString();
            }
            if (columnFamiliesNames.length <= 1) {
                throw new RuntimeException(tableName + "必须至少有一个ColumnFamily,目前只有" + columnFamiliesNames[0] + "这一个,所以不能删除");
            }
            if (!Arrays.asList(columnFamiliesNames).contains(columnFamilyName)) {
                throw new RuntimeException("columnFamily:" + columnFamilyName + "不存在");
            }

            tableDescriptor.removeFamily(Bytes.toBytes(columnFamilyName));
            admin.disableTable(tableName);
            admin.modifyTable(tableName, tableDescriptor);
            admin.enableTable(tableName);

            // 获取远程元数据的HTableDescriptor对象
            HTableDescriptor tableDescriptorFromMetaData = admin.getTableDescriptor(TableName.valueOf(tableName));
            int count = 0;
            while (true) {
                /**
                 * 比较客户端本地的实例与从元数据获取的实例是否一致(包括所有列簇以及与他们相关的设置)
                 */
                if (tableDescriptor.equals(tableDescriptorFromMetaData)) {
                    logger.info("{}的ColumnFamily:[{}]删除成功", tableName, columnFamilyName);
                    break;
                } else {
                    logger.warn("{}的ColumnFamily:[{}]没有删除成功,继续循环等待异步返回", tableName, columnFamilyName);
                    try {
                        count++;
                        if (count == 10) {// 等待10秒如果还没有创建删除，就退出
                            throw new RuntimeException(tableName + "{}删除新的ColumnFamily:[" + columnFamilyName + "]没有删除成功");
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (MasterNotRunningException e) {
            throw new RuntimeException(e);
        } catch (ZooKeeperConnectionException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(admin);
        }
    }

    /**
     * 修改列簇前一定要确保表已经被禁用 列簇不能重命名，通常做法是新建一个列簇，然后使用API从旧的列簇中复制数据到新列簇
     */
    public <T> void modifyColumnFamily(String tableName, String columnFamily) {
        HBaseAdmin admin = null;
        try {
            admin = new HBaseAdmin(hbaseTemplate.getConfiguration());
            if (!admin.tableExists(tableName)) {
                throw new RuntimeException(tableName + "不存在,请先创建表再修改列簇");
            }
            admin.disableTable(tableName);
            HTableDescriptor table = admin.getTableDescriptor(TableName.valueOf(tableName));
            HColumnDescriptor existingColumn = new HColumnDescriptor(columnFamily);
            // 使用java提供的或者本地库提供的gzip压缩
            existingColumn.setCompactionCompressionType(Algorithm.GZ);
            existingColumn.setMaxVersions(HConstants.ALL_VERSIONS);
            table.modifyFamily(existingColumn);
            admin.modifyTable(tableName, table);
            admin.enableTable(tableName);
        } catch (MasterNotRunningException e) {
            throw new RuntimeException(e);
        } catch (ZooKeeperConnectionException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(admin);
        }
    }

    public void deleteRow(String tablename, String rowkey, String familyName) {
        hbaseTemplate.delete(tablename, rowkey, familyName);
    }

    private void fieldPreHandle(Field field) {
        int modifiers = field.getModifiers();
        if (Modifier.isStatic(modifiers)) {
            throw new IllegalStateException("@Column注解不被支持在static属性上，因为static无法序列化");
        }
        if (Modifier.isPrivate(modifiers)) {
            field.setAccessible(true);
        }
    }

}
