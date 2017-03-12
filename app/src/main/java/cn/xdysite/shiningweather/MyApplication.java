package cn.xdysite.shiningweather;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePalApplication;

/**
 * Created by Administrator on 2017/3/7.
 */

public class MyApplication  extends Application{
    private static Context context;

    public MyApplication() {
        super();
        context = getApplicationContext();
    }

    public static Context  getContext() {
        return context;
    }
}
