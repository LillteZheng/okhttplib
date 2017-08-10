package com.rachel.okhttplib.callback;

import com.rachel.okhttplib.request.UploadListener;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by zhengshaorui on 2017/8/8.
 * 抽象基类
 */

public abstract class BaseCallback<T> implements UploadListener{
    public abstract T transCallback(Response response) throws IOException;
    public abstract void onSuccess(T response);
    public abstract void onFailure(Object errorObj);

    @Override
    public void onUploadProgress(int progress) {

    }
}
