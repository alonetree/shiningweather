package cn.xdysite.shiningweather.util;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.xdysite.shiningweather.db.City;
import cn.xdysite.shiningweather.db.Country;
import cn.xdysite.shiningweather.db.Province;
import cn.xdysite.shiningweather.gson.Weather;

/**
 * Created by Administrator on 2017/3/7.
 */

public class Utility {
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i=0; i<allProvinces.length(); i++) {
                    JSONObject provinceJsonObj = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(provinceJsonObj.getInt("id"));
                    province.setProvinceName(provinceJsonObj.getString("name"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response, final int provinceCode) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray citys = new JSONArray(response);
                for (int i=0; i<citys.length(); i++) {
                    JSONObject ciytJsonObj = citys.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(ciytJsonObj.getInt("id"));
                    city.setCityName(ciytJsonObj.getString("name"));
                    city.setProvinceCode(provinceCode);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountryResponse(String response, final int cityCode) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray countries = new JSONArray(response);
                for (int i=0; i<countries.length(); i++) {
                    JSONObject countryJsonObj = countries.getJSONObject(i);
                    Country country = new Country();
                    country.setCityCode(cityCode);
                    country.setCountryCode(countryJsonObj.getInt("id"));
                    country.setCountryName(countryJsonObj.getString("name"));
                    country.setWeatherId(countryJsonObj.getString("weather_id"));
                    country.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherReponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather data service 3.0");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
