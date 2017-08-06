package com.rachel.okhttplib.request;

import android.util.Log;

import java.util.Map;

import okhttp3.Request;

/**
 * Created by zhengshaorui on 2017/8/6.
 */

public class CommonRequest {
    private static final String TAG = "zsr";
    /**
     * 带参数的 get 方法
     * @param url
     * @param params
     * @return
     */
    public static Request createGetRequest(String url,RequestParams params){
        StringBuffer sb = new StringBuffer(url).append("?");
        if (params != null){
            for (Map.Entry<String,Object> entry : params.mObjParams.entrySet()){
               sb.append(entry.getKey())
                       .append("=")
                       .append(entry.getValue())
                       .append("&");
                Log.d(TAG, "createGetRequest: "+ entry.getKey()+" "+entry.getValue());
            }
        }
        Request request = new Request.Builder()
                        .url(sb.substring(0,sb.length() - 1))
                        .get()
                        .build();
        return request;

    }

    /**
     * 不带参数的get方法
     * @param url
     * @return
     */
    public static Request createGetRequest(String url){
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return request;

    }
}
