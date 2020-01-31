package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Author: wang shuo
 * Date: on 2020/1/31
 * Email: ws2821@yeah.net
 */
public class County extends DataSupport {
    private int id;
    private String countyName;  //县的名字
    private String weatherId;   //县的天气ID
    private int cityId; //记录当前所属市的id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
