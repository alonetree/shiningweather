package cn.xdysite.shiningweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/3/7.
 */

public class Country extends DataSupport {
    private long id;  //主键
    private int countryCode;
    private String countryName;
    private int cityCode;
    private String weatherId;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(int countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
