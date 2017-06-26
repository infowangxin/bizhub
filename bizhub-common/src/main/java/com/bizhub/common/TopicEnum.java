package com.bizhub.common;

public enum TopicEnum {

        SALES("sales"), ITEM("item");

    TopicEnum(String key) {
        this.key = key;
    }

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
