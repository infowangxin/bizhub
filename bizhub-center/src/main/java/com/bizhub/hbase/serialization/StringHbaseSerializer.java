package com.bizhub.hbase.serialization;

import java.nio.charset.Charset;


public class StringHbaseSerializer implements HbaseSerializer<String> {
    private final Charset charset;

    public StringHbaseSerializer() {
        this(Charset.forName("UTF8"));
    }

    public StringHbaseSerializer(Charset charset) {
        this.charset = charset;
    }

    public String deserialize(byte[] bytes) {
        return (bytes == null ? null : new String(bytes, charset));
    }

    public byte[] serialize(String string) {
        return (string == null ? null : string.getBytes(charset));
    }

}
