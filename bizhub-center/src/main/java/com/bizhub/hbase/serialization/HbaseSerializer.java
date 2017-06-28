package com.bizhub.hbase.serialization;

public interface HbaseSerializer<T> {

    byte[] serialize(T t) throws HbaseSerializationException;

    T deserialize(byte[] bytes) throws HbaseSerializationException;
}
