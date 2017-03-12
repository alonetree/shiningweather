package cn.xdysite.shiningweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/3/7.
 */

public class City extends DataSupport {
    private int id;  //主键
    private int cityCode;
    private String cityName;
    private int provinceCode; //外键

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
