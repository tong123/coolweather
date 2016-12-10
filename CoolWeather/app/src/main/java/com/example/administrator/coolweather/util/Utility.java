package com.example.administrator.coolweather.util;

import android.text.TextUtils;

import com.example.administrator.coolweather.model.City;
import com.example.administrator.coolweather.model.CoolWeatherDB;
import com.example.administrator.coolweather.model.Country;
import com.example.administrator.coolweather.model.Province;

/**
 * Created by Administrator on 2016/12/10.
 */

public class Utility {

    public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB, String response ) {
        if( !TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if( allProvinces != null && allProvinces.length>0) {
                for (String p : allProvinces ) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceName(array[0]);
                    province.setProvinceCode(array[1]);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
        if(!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if( allCities != null && allCities.length > 0 ) {
                for (String c: allCities ) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCountriesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
        if(!TextUtils.isEmpty(response)) {
            String[] allcountries = response.split(",");
            if(allcountries != null && allcountries.length>0 ) {
                for (String c: allcountries ) {
                    String[] array = c.split("\\|");
                    Country country = new Country();
                    country.setCountryName(array[0]);
                    country.setCountryCode(array[1]);
                    country.setCityId(cityId);
                    coolWeatherDB.saveCountry(country);
                }
                return true;
            }
        }
        return false;
    }

}
