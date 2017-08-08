package com.rachel.okhttplib.callback;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by zhengshaorui on 2017/8/8.
 * 字符串类
 */

public abstract class JsonResponse<T> extends BaseCallback<T> {
    private Class<T> mclazz;

    public JsonResponse(Class<T> mclazz) {
        this.mclazz = mclazz;
    }

    public JsonResponse() {
    }

    @Override
    public T transCallback(Response response) throws IOException {
        String rootJosn = response.body().string();
        Gson gson = new Gson();
        T bean = gson.fromJson(rootJosn,mclazz);
        return bean;
    }
}
