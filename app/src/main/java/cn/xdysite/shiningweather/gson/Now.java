package cn.xdysite.shiningweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/9.
 */

public class Now {
    @SerializedName("tmp")
    public String tmp;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
