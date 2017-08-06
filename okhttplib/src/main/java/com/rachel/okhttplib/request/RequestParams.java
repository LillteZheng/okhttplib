package com.rachel.okhttplib.request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhengshaorui on 2017/8/6.
 * 提供params的参数
 */

public class RequestParams {

    ConcurrentHashMap<String,Object> mObjParams = new ConcurrentHashMap<String,Object>();

    public RequestParams() {
        this(null);
    }
    public RequestParams(Map<String,Object> source){
        if (source != null){
            for (Map.Entry<String,Object> entry : source.entrySet()){
                put(entry.getKey(),entry.getValue());
            }
        }

    }
    /**
     * put 方法
     * @param key
     * @param obj
     */
    public void put(String key,Object obj){
        if (key != null){
            mObjParams.put(key,obj);
        }
    }
}
