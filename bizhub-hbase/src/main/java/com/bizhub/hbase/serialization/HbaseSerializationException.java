package com.bizhub.hbase.serialization;


public class HbaseSerializationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public HbaseSerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public HbaseSerializationException(String msg) {
        super(msg);
    }

}
