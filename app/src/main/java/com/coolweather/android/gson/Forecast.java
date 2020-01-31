package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Author: wang shuo
 * Date: on 2020/1/31
 * Email: ws2821@yeah.net
 * 未来几天的天气信息
 */
public class Forecast {

    public String date;//日期

    @SerializedName("tmp")
    public Temperature temperature;//气温

    @SerializedName("cond")
    public More more;

    public class Temperature {
        public String max;//最高温
        public String min;//最低温
    }

    public class More {
        @SerializedName("txt_d")
        public String info;//描述
    }
}
