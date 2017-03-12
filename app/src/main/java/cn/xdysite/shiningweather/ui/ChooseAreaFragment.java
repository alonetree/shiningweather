package cn.xdysite.shiningweather.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.xdysite.shiningweather.MainActivity;
import cn.xdysite.shiningweather.R;
import cn.xdysite.shiningweather.WeatherActivity;
import cn.xdysite.shiningweather.db.City;
import cn.xdysite.shiningweather.db.Country;
import cn.xdysite.shiningweather.db.Province;
import cn.xdysite.shiningweather.util.HttpUtil;
import cn.xdysite.shiningweather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/7.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;

    private ProgressDialog progressDialog;
    private TextView titleView;
    private ListView listView;
    private Button backButton;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;

    /**
     * 县列表
     */
    private List<Country> countryList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;

    /**
     * 当前的选中的级别
     */
    private int currentLevel;

    /**
     * 当前选中的县
     */
    private Country selectedCountry;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleView = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvinces();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCountries();
                } else if (currentLevel == LEVEL_COUNTRY) {
                    if (getActivity() instanceof MainActivity) {
                        selectedCountry = countryList.get(position);
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", selectedCountry.getWeatherId());
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        selectedCountry = countryList.get(position);
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(selectedCountry.getWeatherId());
                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTRY)
                    queryCities();
                else if (currentLevel == LEVEL_CITY)
                    queryProvinces();
            }
        });
    }

    private void queryProvinces() {
        titleView.setText("中国");
        backButton.setVisibility(View.GONE);
        dataList.clear();
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            for (Province province: provinceList)
                dataList.add(province.getProvinceName());
            listView.setSelection(0);
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROVINCE;
        } else {
            String site = "http://guolin.tech/api/china";
            queryFromServer(site, "province");
        }
    }

    private void queryCities() {
        titleView.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        dataList.clear();
        cityList = DataSupport.where("provincecode=?", String.valueOf(selectedProvince.getProvinceCode())).find(City.class);
        if (cityList.size() > 0) {
            for (City city : cityList)
                dataList.add(city.getCityName());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            String site = "http://guolin.tech/api/china/"+selectedProvince.getProvinceCode();
            queryFromServer(site, "city");
        }
    }

    private void queryCountries() {
        titleView.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countryList = DataSupport.where("citycode=?", String.valueOf(selectedCity.getCityCode())).find(Country.class);
        if (countryList.size() > 0) {
            dataList.clear();
            for (Country country : countryList)
                dataList.add(country.getCountryName());
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_COUNTRY;
        } else {
            String site = "http://guolin.tech/api/china/" + selectedProvince.getProvinceCode() +"/" + selectedCity.getCityCode();
            queryFromServer(site, "country");
        }
    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                switch (type) {
                    case "province":
                        result = Utility.handleProvinceResponse(responseText);
                        break;
                    case "city":
                        result = Utility.handleCityResponse(responseText, selectedProvince.getProvinceCode());
                        break;
                    case "country":
                        result = Utility.handleCountryResponse(responseText, selectedCity.getCityCode());
                        break;
                    default:break;
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            switch (type) {
                                case "province":
                                    queryProvinces();
                                    break;
                                case  "city":
                                    queryCities();
                                    break;
                                case "country":
                                    queryCountries();
                                    break;
                                default:break;
                            }
                        }
                    });
                }
            }
        });
    }

    private void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
}
