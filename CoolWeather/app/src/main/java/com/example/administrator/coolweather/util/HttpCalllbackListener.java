package com.example.administrator.coolweather.util;

/**
 * Created by Administrator on 2016/12/10.
 */
public interface HttpCalllbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
