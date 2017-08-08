package com.rachel.okhttplib.builder;

import android.os.Handler;
import android.os.Looper;

import com.rachel.okhttplib.HandleCallUtils;
import com.rachel.okhttplib.OkHttpCommonClient;
import com.rachel.okhttplib.callback.BaseCallback;
import com.rachel.okhttplib.request.OkhttpRequestBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by zhengshaorui on 2017/8/8.
 */

public class GetBuilder extends OkhttpRequestBuilder<GetBuilder> {
    private static final String TAG = "zsr";
    private Call mCall;
    private Handler mDeviderHandler = new Handler(Looper.getMainLooper());
    public GetBuilder(){

    }

    public GetBuilder(String url, String tag, ConcurrentHashMap<String, String> params,
                      ConcurrentHashMap<String, String> headers) {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;

        StringBuffer sb = new StringBuffer(url).append("?");
        if (params != null){ //可能有多个params
            for (Map.Entry<String,String> entry : this.params.entrySet()){
                sb.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
        }
        String headkey = null,headvalue = null;
        if (headers != null){
            for (Map.Entry<String,String> entry : this.headers.entrySet()){
                headkey = entry.getKey();
                headvalue = entry.getValue();
            }

        }
        Request request = null;
        if (this.headers != null) {
            request = new Request.Builder()
                    .url(sb.substring(0, sb.length() - 1))
                    .get()
                    .tag(tag)
                    .addHeader(headkey, headvalue)
                    .build();
        }else{
            request = new Request.Builder()
                    .url(sb.substring(0, sb.length() - 1))
                    .get()
                    .tag(tag)
                    .build();
        }

        mCall = OkHttpCommonClient.getInstance().getOkhttpClient().newCall(request);
    }

    /**
     * 异步方法
     * @param listener
     * @return
     */
    public GetBuilder enqueue(final BaseCallback listener){
        if (mCall != null){
            HandleCallUtils.enqueueCallBack(mCall,listener);
        }
        return this;
    }

    /**
     * 同步方法，需要在子线程执行，不然提示错误
     * @param listener
     * @return
     */
    public GetBuilder execute(BaseCallback listener){
        if (mCall != null){
            HandleCallUtils.executeCallBack(mCall,listener);
        }
        return this;
    }

    @Override
    public GetBuilder builder() {

        return new GetBuilder(url,tag,params,headers);
    }


}
