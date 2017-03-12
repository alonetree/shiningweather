package cn.xdysite.shiningweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/9.
 */

public class Forecast {

    @SerializedName("date")
    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature {
        @SerializedName("max")
        public String max;

        @SerializedName("min")
        public String min;
    }

    public class More {
        @SerializedName("txt_d")
        public String day;

        @SerializedName("txt_n")
        public String night;
    }
}
