package com.coolweather.android.gson;

/**
 * Author: wang shuo
 * Date: on 2020/1/31
 * Email: ws2821@yeah.net
 * 表示当前空气质量情况
 */
public class AQI {

    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
