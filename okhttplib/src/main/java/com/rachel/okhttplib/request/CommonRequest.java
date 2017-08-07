package com.rachel.okhttplib.request;

import android.os.Environment;

import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

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
            }
        }
        Request request = new Request.Builder()
                        .url(sb.substring(0,sb.length() - 1))
                        .get()
                        .build();
        return request;
    }

    /**
     * 用post 的方式 这里依旧是 key - value 的形式
     * @param url
     * @param params
     * @return
     */
    public static Request createPostRequest(String url,RequestParams params){
        FormBody.Builder mBodyBuilder = new FormBody.Builder();
        if (params != null){
            for (Map.Entry<String,Object> entry : params.mObjParams.entrySet()){
                mBodyBuilder.add(entry.getKey(), (String) entry.getValue());
            }
        }
        FormBody formBody = mBodyBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        return request;
    }

    public static Request createPostStringRequest(String url){
        RequestBody formBody = FormBody.create(MediaType.parse("text/plain;chaset-utf-8"),
                "{username:zhengshaorui,password:123}");
        Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

        return request;
    }

    public static Request createPostFileRequest(String url){

        File dir = new File(Environment.getExternalStorageDirectory().getPath());
        File file = new File(dir,"tvlog.jpg");
        RequestBody formBody = FormBody.create(MediaType.parse("image/pjpeg"),
                file);
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        return request;
    }



    /**
     * 不带参数的get方法,
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
