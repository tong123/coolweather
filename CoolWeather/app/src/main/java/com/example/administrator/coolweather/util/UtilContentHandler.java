package com.example.administrator.coolweather.util;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/13.
 */

public class UtilContentHandler extends DefaultHandler {
    private String nodeName;
    private static String weatherCode;
    private StringBuilder city_name;
    private StringBuilder temp_low;
    private StringBuilder temp_high;
    private StringBuilder weather_desp;
    private StringBuilder publish_time;

    private List<String> city_name_list;
    private List<String> temp_low_list;
    private List<String> temp_high_list;
    private List<String> weather_desp_list;
    private List<String> publish_time_list;

    public static void set_weather_code( String weather_code) {
        weatherCode = weather_code;
    }

    public static String get_weather_code() {
        return weatherCode;
    }

    @Override
    public void startDocument() throws SAXException {
        city_name = new StringBuilder();
        temp_low = new StringBuilder();
        temp_high = new StringBuilder();
        weather_desp = new StringBuilder();
        publish_time = new StringBuilder();
        city_name_list = new ArrayList<String>();
        temp_low_list = new ArrayList<String>();
        temp_high_list = new ArrayList<String>();
        weather_desp_list = new ArrayList<String>();
        publish_time_list = new ArrayList<String>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        nodeName = localName;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if ("city".equals(nodeName)) {
            city_name.append(ch, start, length);
            city_name_list.add(city_name.toString());
        } else if("low".equals(nodeName)) {
            temp_low.append(ch, start, length);
            temp_low_list.add(temp_low.toString());
        } else if ("high".equals(nodeName)) {
            temp_high.append(ch, start, length);
            temp_high_list.add(temp_high.toString());
        } else if("type".equals(nodeName)) {
            weather_desp.append(ch, start, length);
            weather_desp_list.add(weather_desp.toString());
        } else if ("updatetime".equals(nodeName)) {
            publish_time.append(ch, start, length);
            publish_time_list.add(publish_time.toString());
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        Log.d("ContentHandle", "cityName is "+city_name.toString().trim());
        Log.d("ContentHandle", "temp_low is "+temp_low.toString().trim());
        Log.d("ContentHandle", "temp_high is "+temp_high.toString().trim());
        Log.d("ContentHandle", "weather_desp is "+weather_desp.toString().trim());
        Log.d("ContentHandle", "publish_time is "+publish_time.toString().trim());
        city_name.setLength(0);
        temp_low.setLength(0);
        temp_high.setLength(0);
        weather_desp.setLength(0);
        publish_time.setLength(0);
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }


    public String getCityName() {
        return city_name_list.get(0);
    }

    public String getTemp_low() {
        return temp_low_list.get(0);
    }

    public String getTemp_high() {
        return temp_high_list.get(0);
    }

    public String getWeather_desp() {
        return weather_desp_list.get(0);
    }

    public String getPublish_time() {
        return publish_time_list.get(0);
    }
}
