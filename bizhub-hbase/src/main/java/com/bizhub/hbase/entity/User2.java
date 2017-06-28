package com.bizhub.hbase.entity;

import java.io.Serializable;

import com.bizhub.hbase.common.annotation.Column;
import com.bizhub.hbase.common.annotation.Table;

@Table(tableName = "user", columnFamilyName = "cfuser1")
public class User2 implements Serializable {

    private static final long serialVersionUID = 543577505515737444L;

    @Column(columnName = "userId", isRowName = true)
    private String userId;

    @Column(columnName = "age")
    private int age;

    @Column(columnName = "email")
    private String email;

    @Column(columnName = "car")
    private Car car = Car.newDefaultCar();

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", age=" + age + ", email=" + email + ", car=" + car + "]";
    }

}
