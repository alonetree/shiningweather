package cn.xdysite.shiningweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/9.
 */

public class AQI {

    @SerializedName("city")
    public AQICity city;


    public class AQICity {
        @SerializedName("aqi")
        public String aqi;

        @SerializedName("pm25")
        public String pm25;
    }
}
