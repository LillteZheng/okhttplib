package com.rachel.okhttplib.callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by zhengshaorui on 2017/8/8.
 * 字符串类
 */

public abstract class StringResponse extends BaseCallback<String> {
    @Override
    public String transCallback(Response response) throws IOException {
        return response.body().string();
    }
}
