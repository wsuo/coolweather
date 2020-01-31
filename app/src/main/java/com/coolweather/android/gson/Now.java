package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Author: wang shuo
 * Date: on 2020/1/31
 * Email: ws2821@yeah.net
 * 当前的天气信息
 */
public class Now {
    @SerializedName("tmp")
    public String temperature;//温度

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;//晴,多云等...
    }
}
