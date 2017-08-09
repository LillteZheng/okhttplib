package com.rachel.okhttplib.request;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhengshaorui on 2017/8/8.
 */

public abstract class OkhttpRequestBuilder<T extends OkhttpRequestBuilder> {
    private static final String TAG = "zsr";
    protected String url;
    protected String tag;
    protected ConcurrentHashMap<String, String> params;
    protected ConcurrentHashMap<String, String> headers;

    public abstract T builder();


    public T url(String url) {
        this.url = url;
        return (T) this;
    }

    public T tag(String tag){
        this.tag = tag;
        return (T) this;
    }

    public T putParams(String key,String value){
        if (this.params == null){
            this.params = new ConcurrentHashMap<String,String >();
        }
        this.params.put(key,value);
        return (T) this;
    }

    public T addHeaders(String key,String value){
        if (this.headers == null){
            this.headers = new ConcurrentHashMap<String,String>();
        }
        this.headers.put(key,value);
        return (T)this;
    }

    public T params(ConcurrentHashMap<String,String> params){
        this.params = params;
        return (T)this;
    }

    public T headers(ConcurrentHashMap<String,String> headers){
        this.headers = headers;
        return (T) this;
    }


}
