package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Author: wang shuo
 * Date: on 2020/1/31
 * Email: ws2821@yeah.net
 * 基本的天气信息
 */
public class Basic {

    //SerializedName注解,让JSON字段和java字段建立映射关系
    @SerializedName("city")
    public String cityName;//城市名

    @SerializedName("id")
    public String weatherId;//对应天气的id

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;//天气的更新时间
    }
}
