package com.bizhub.hbase.entity;

import java.io.Serializable;

import com.bizhub.hbase.common.annotation.Column;
import com.bizhub.hbase.common.annotation.Table;

@Table(columnFamilyName = "cfperson", tableName = "person")
public class Person implements Serializable {

    private static final long serialVersionUID = 2293144801820666622L;

    @Column(columnName = "userId", isRowName = true)
    private String userId;

    @Column(columnName = "userName")
    private String userName;

    @Column(columnName = "password")
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Person [userId=" + userId + ", userName=" + userName + ", password=" + password + "]";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
