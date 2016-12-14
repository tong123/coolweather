package com.example.administrator.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.coolweather.R;
import com.example.administrator.coolweather.service.AutoUpdateService;
import com.example.administrator.coolweather.util.HttpCalllbackListener;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.UtilContentHandler;
import com.example.administrator.coolweather.util.Utility;

public class WeatherActivity extends AppCompatActivity {

    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;
    private Button switchCity;
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
        cityNameText = (TextView)findViewById(R.id.city_name);
        publishText = (TextView)findViewById(R.id.publish_text);
        weatherDespText = (TextView)findViewById(R.id.weather_desp);
        temp1Text = (TextView)findViewById(R.id.temp1);
        temp2Text = (TextView)findViewById(R.id.temp2);
        currentDateText = (TextView)findViewById(R.id.current_date);
        switchCity = (Button)findViewById(R.id.switch_city);
        refreshWeather = (Button)findViewById(R.id.refresh_weather);
        switchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true );
                startActivity(intent);
                finish();
            }
        });
        refreshWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishText.setText("同步中...");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                String weatherCode = preferences.getString("weather_code", "");
                if(!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
            }
        });
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
        String countryCode = getIntent().getStringExtra("country_code");
        if( !TextUtils.isEmpty(countryCode) ) {
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countryCode);
        } else {
            showWeather();
        }
    }

    private void queryWeatherCode(String countryCode) {
        String address = "http://www.weather.com.cn/data/list3/city"+countryCode+".xml";
        queryFromServer(address, "countryCode");
    }

    private void queryWeatherInfo( String weatherCode ) {
//        String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+weatherCode;
        UtilContentHandler.set_weather_code( weatherCode );
        queryFromServer(address, "weatherCode");
    }

    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCalllbackListener() {
            @Override
            public void onFinish(String response) {
                if("countryCode".equals(type)) {
                    if(!TextUtils.isEmpty(response)) {
                        Log.d("queryFromServer test", "onFinish: "+response);
                        String[] array = response.split("\\|");
                        if( array != null && array.length == 2 ) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    Log.d("queryFromServer test", "weatherCode: "+response);
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    private void showWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(preferences.getString("city_name", ""));
        temp1Text.setText(preferences.getString("temp1", ""));
        temp2Text.setText(preferences.getString("temp2", "" ));
        weatherDespText.setText(preferences.getString("weather_desp", ""));
        publishText.setText("今天"+preferences.getString("publish_time", "")+"发布");
        currentDateText.setText(preferences.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }
}
