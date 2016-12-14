package com.example.administrator.coolweather.service;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.administrator.coolweather.receiver.AutoUpdateReceiver;
import com.example.administrator.coolweather.util.HttpCalllbackListener;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.UtilContentHandler;
import com.example.administrator.coolweather.util.Utility;

/**
 * Created by Administrator on 2016/12/14.
 */

public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime()+20000;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = preferences.getString("weather_code", "");
        String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+weatherCode;
        UtilContentHandler.set_weather_code( weatherCode );

        HttpUtil.sendHttpRequest(address, new HttpCalllbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
