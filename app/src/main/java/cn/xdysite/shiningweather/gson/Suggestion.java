package cn.xdysite.shiningweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/9.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comf comf;

    @SerializedName("sport")
    public Sport sport;

    @SerializedName("cw")
    public CarWash carWash;

    public class Comf {
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("txt")
        public String info;
    }
}
