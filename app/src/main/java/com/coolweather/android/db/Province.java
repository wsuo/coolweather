package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * 数据库表:对应的是省份
 * 要继承自DataSupport才能进行CRUD操作
 */
public class Province extends DataSupport {
    private int id;
    private String provinceName;    //省的名字
    private int provinceCode;   //省的代号

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
