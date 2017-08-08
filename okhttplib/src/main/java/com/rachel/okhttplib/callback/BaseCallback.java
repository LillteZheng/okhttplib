package com.rachel.okhttplib.callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by zhengshaorui on 2017/8/8.
 * 抽象基类
 */

public abstract class BaseCallback<T> {
    public abstract T transCallback(Response response) throws IOException;
    public abstract void onSuccess(T response);
    public abstract void onFailure(Object errorObj);
}
