package com.example.administrator.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.nfc.Tag;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.example.administrator.coolweather.model.City;
import com.example.administrator.coolweather.model.CoolWeatherDB;
import com.example.administrator.coolweather.model.Country;
import com.example.administrator.coolweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2016/12/10.
 */

public class Utility {

    public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB, String response ) {
        if( !TextUtils.isEmpty(response)) {
            Log.d("MainActivity test:", response);
            String[] allProvinces = response.split(",");
            if( allProvinces != null && allProvinces.length>0) {
                for (String p : allProvinces ) {
                    String[] array = p.split("\\|");
                    Log.d("handleProvinceResponse", array[0]+array[1]);
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
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
                    country.setCountryCode(array[0]);
                    country.setCountryName(array[1]);
                    country.setCityId(cityId);
                    coolWeatherDB.saveCountry(country);
                }
                return true;
            }
        }
        return false;
    }

    public static void handleWeatherResponse(Context context, String response) {
        parseXMLWithSAX( context, response );
//        try {
//            JSONObject jsonObject = new JSONObject(response);
//            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
//            String cityName = weatherInfo.getString("city");
//            String weatherCode = weatherInfo.getString("cityid");
//            String temp1 = weatherInfo.getString("temp1");
//            String temp2 = weatherInfo.getString("temp2");
//            String weatherDesp = weatherInfo.getString("weather");
//            String publicTime = weatherInfo.getString("ptime");
//            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publicTime );
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private static void parseXMLWithSAX( Context context, String xmlData ) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
//            ContentHandler handler = new ContentHandler();
            UtilContentHandler handler = new UtilContentHandler();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(new StringReader(xmlData)));
            Log.d(TAG, "parseXMLWithSAX: hello");

            String cityName = handler.getCityName();
            String weatherCode = UtilContentHandler.get_weather_code();
            String temp1 = handler.getTemp_low();
            String temp2 = handler.getTemp_high();
            String weatherDesp = handler.getWeather_desp();
            String publicTime = handler.getPublish_time();
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publicTime );
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1,
                                       String temp2, String weatherDesp, String publicTime ) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publicTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }
}
