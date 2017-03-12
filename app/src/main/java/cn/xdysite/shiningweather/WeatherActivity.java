package cn.xdysite.shiningweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;

import cn.xdysite.shiningweather.gson.Forecast;
import cn.xdysite.shiningweather.gson.Weather;
import cn.xdysite.shiningweather.service.AutoUpdateService;
import cn.xdysite.shiningweather.util.HttpUtil;
import cn.xdysite.shiningweather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degree;
    private TextView weatherInfo;
    private LinearLayout forecast;
    private TextView aqi;
    private TextView pm25;
    private TextView comfortable;
    private TextView carwash;
    private TextView sport;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawerLayout;
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degree = (TextView) findViewById(R.id.degree_text);
        weatherInfo = (TextView) findViewById(R.id.weather_info_text);
        forecast = (LinearLayout) findViewById(R.id.forecast_layout);
        aqi = (TextView) findViewById(R.id.aqi);
        pm25 = (TextView) findViewById(R.id.pm25);
        comfortable = (TextView) findViewById(R.id.comfortable);
        carwash = (TextView) findViewById(R.id.carwash);
        sport = (TextView) findViewById(R.id.sport);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        String weatherId = getIntent().getStringExtra("weather_id");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        final String weatherId2;
        if (weatherId == null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String str = sp.getString("allInfo", null);
            Weather weather = Utility.handleWeatherReponse(str);
            weatherId2 = weather.basic.cityId;
            showWeatherInfo(weather);
        }
        else {
            weatherId2 = weatherId;
            requestWeather(weatherId);
        }
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        loadBingPic();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId2);
            }
        });
    }

    public void requestWeather(final String weatherId) {
        final String weatherUrl = "https://free-api.heweather.com/x3/weather?cityid=" + weatherId +"&key=48a44b7f86a641d0b6a4514aef6dd9ae";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherReponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                            editor.putString("allInfo", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });

            }
        });
    }


    private void showWeatherInfo(Weather weather) {
        titleCity.setText(weather.basic.cityName);
        titleUpdateTime.setText(weather.basic.update.updateTime.split(" ")[1]);
        degree.setText(weather.now.tmp+"℃");
        weatherInfo.setText(weather.now.more.info);
        aqi.setText(weather.aqi.city.aqi);
        pm25.setText(weather.aqi.city.pm25);
        comfortable.setText("舒适度: "+weather.suggestion.comf.info);
        carwash.setText("洗车指数: "+weather.suggestion.carWash.info);
        sport.setText("运动建议: " +weather.suggestion.sport.info);
        forecast.removeAllViews();
        for (Forecast forecast1 : weather.forecastList) {
            View v = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecast, false);
            TextView dateText = (TextView) v.findViewById(R.id.date_text);
            TextView infoText = (TextView) v.findViewById(R.id.info_text);
            TextView maxText = (TextView) v.findViewById(R.id.max_text);
            TextView minText = (TextView) v.findViewById(R.id.min_text);

            dateText.setText(forecast1.date);
            infoText.setText(forecast1.more.day);
            maxText.setText(forecast1.temperature.max);
            minText.setText(forecast1.temperature.min);
            forecast.addView(v);
        }
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private void loadBingPic() {
        String requestUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
}
