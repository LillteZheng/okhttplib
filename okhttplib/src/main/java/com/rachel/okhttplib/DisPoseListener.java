package com.rachel.okhttplib;

/**
 * Created by zhengshaorui on 2017/8/5.
 */

public interface DisPoseListener {
    void onSuccess(Object resposeObj);
    void onFailure(Object errorObj);
}
