package com.bizhub.entity;

import java.io.Serializable;

import com.bizhub.hbase.common.annotation.Column;
import com.bizhub.hbase.common.annotation.Table;

@Table(tableName = "item", columnFamilyName = "gi")
public class Item implements Serializable {

    private static final long serialVersionUID = -7214626835388438946L;

    @Column(columnName = "itemId", isRowName = true)
    private String itemId;

    @Column(columnName = "goodsName")
    private String goodsName;

    @Column(columnName = "brandName")
    private String brandName;

    @Column(columnName = "busiName")
    private String busiName;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBusiName() {
        return busiName;
    }

    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

}
