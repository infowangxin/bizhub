package com.bizhub.hbase.serialization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public abstract class HbaseSerializationUtils {

    static final byte[] EMPTY_ARRAY = new byte[0];

    static boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }

    @SuppressWarnings("unchecked")
    static <T extends Collection<?>> T deserializeValues(Collection<byte[]> rawValues, Class<T> type,
            HbaseSerializer<?> HbaseSerializer) {
        // connection in pipeline/multi mode
        if (rawValues == null) {
            return null;
        }

        Collection<Object> values = (List.class.isAssignableFrom(type) ? new ArrayList<Object>(rawValues.size())
                : new LinkedHashSet<Object>(rawValues.size()));
        for (byte[] bs : rawValues) {
            values.add(HbaseSerializer.deserialize(bs));
        }

        return (T) values;
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<T> deserialize(Set<byte[]> rawValues, HbaseSerializer<T> HbaseSerializer) {
        return deserializeValues(rawValues, Set.class, HbaseSerializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> deserialize(List<byte[]> rawValues, HbaseSerializer<T> HbaseSerializer) {
        return deserializeValues(rawValues, List.class, HbaseSerializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> deserialize(Collection<byte[]> rawValues, HbaseSerializer<T> HbaseSerializer) {
        return deserializeValues(rawValues, List.class, HbaseSerializer);
    }

    public static <T> Map<T, T> deserialize(Map<byte[], byte[]> rawValues, HbaseSerializer<T> HbaseSerializer) {
        if (rawValues == null) {
            return null;
        }
        Map<T, T> ret = new LinkedHashMap<T, T>(rawValues.size());
        for (Map.Entry<byte[], byte[]> entry : rawValues.entrySet()) {
            ret.put(HbaseSerializer.deserialize(entry.getKey()), HbaseSerializer.deserialize(entry.getValue()));
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    public static <HK, HV> Map<HK, HV> deserialize(Map<byte[], byte[]> rawValues, HbaseSerializer<HK> hashKeySerializer,
            HbaseSerializer<HV> hashValueSerializer) {
        if (rawValues == null) {
            return null;
        }
        Map<HK, HV> map = new LinkedHashMap<HK, HV>(rawValues.size());
        for (Map.Entry<byte[], byte[]> entry : rawValues.entrySet()) {
            // May want to deserialize only key or value
            HK key = hashKeySerializer != null ? (HK) hashKeySerializer.deserialize(entry.getKey()) : (HK) entry.getKey();
            HV value = hashValueSerializer != null ? (HV) hashValueSerializer.deserialize(entry.getValue()) : (HV) entry
                    .getValue();
            map.put(key, value);
        }
        return map;
    }
}
