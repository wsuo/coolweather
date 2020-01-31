package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public class WeatherActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;//滑动菜单

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefreshLayout;
    private String mWeatherId;
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*********************************************************************************************************/
        /*
        发现 背景图没有和状态栏融为一体,怎么办呢?
        可以引入Material Design库,但是那样太麻烦了
        如下更简单
         */
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);//将状态栏设置成透明色
//        }

/*********************************************************************************************************/
//但是我发现一个很严重的问题,使用这破玩意儿我的背景图片就显示不出来了,我吐了
//算了,我还是使用比较麻烦的方法吧,告辞


//经过我的一番努力发现,那个方法也不能显示图片...

        setContentView(R.layout.activity_weather);
        //初始化各控件
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        bingPicImg = findViewById(R.id.bing_pic_img);
        //下拉刷新
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        //滑动菜单功能
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);

        //尝试从本地缓存读取天气数据
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //无缓存时去服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            //请求数据的时候先将ScrollView进行隐藏,因为空数据看起来很奇怪
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);//请求天气数据
        }
        //设置刷新事件
        swipeRefreshLayout.setOnRefreshListener(() -> {
            requestWeather(mWeatherId);
        });

        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
//一打开这个应用就闪退

        navButton.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);//开启滑动菜单
        });
    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                //放入缓存中
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(() -> {
                    Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                });
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息
     *
     * @param weatherId
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId
                + "&key=f47b9ff6d9ff4109bfcf61d7100f5206";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(WeatherActivity.this, "获取天气信息失败1", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);//表示刷新结束,隐藏进度条
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //记录一个错误:后期调试的时候
                /*
                虽说不是自己原创的,但是做出来的时候还是挺激动的
                也没检查直接启动了,于是毫不意外的启动失败,调试了半天将错误原因锁定在了这里
                发现把string()写成toString()了
                 */
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(() -> {
                    if (weather != null && "ok".equals(weather.status)) {
                        //将获取的数据缓存起来
                        SharedPreferences.Editor editor =
                                PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                        showWeatherInfo(weather);
                    } else {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败2", Toast.LENGTH_SHORT).show();
                    }
                    swipeRefreshLayout.setRefreshing(false);//表示刷新结束,隐藏进度条
                });
            }
        });
        loadBingPic();
    }

    /**
     * 处理并展示Weather实体类中的数据
     *
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        degreeText.setText(degree);
        titleUpdateTime.setText(updateTime);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数: " + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);//数据已经加载完成了,变为可见
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);   //启动服务!!!
    }
}
