package com.rachel.okhttplib;

import android.os.Handler;
import android.os.Looper;

import com.rachel.okhttplib.callback.BaseCallback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zhengshaorui on 2017/8/8.
 */

public class HandleCallUtils {
    private static Handler sDeviderHandler = new Handler(Looper.getMainLooper());
    public static void enqueueCallBack(Call okhttpcall, final BaseCallback listener){
        okhttpcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {


                sDeviderHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailure(e);
                    }
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                  final Object responeObj =  listener.transCallback(response);

                sDeviderHandler.post(new Runnable() { //结果在主线程中
                    @Override
                    public void run() {
                        listener.onSuccess(responeObj);
                    }
                });
            }
        });
    }

    public static void executeCallBack(Call okhttpcall,final BaseCallback listener){
        if (Thread.currentThread() == Looper.getMainLooper().getThread()){
            sDeviderHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onFailure("NetWork handle in MainThread");
                }
            });
        }else{
            try {
                Response response = okhttpcall.execute();
                final Object responseObj = listener.transCallback(response);
                sDeviderHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSuccess(responseObj);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
