package com.rachel.okhttplib.builder;

import com.rachel.okhttplib.HandleCallUtils;
import com.rachel.okhttplib.OkHttpCommonClient;
import com.rachel.okhttplib.callback.BaseCallback;
import com.rachel.okhttplib.request.OkhttpRequestBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;

/**
 * Created by zhengshaorui on 2017/8/8.
 */

public class PostBuilder extends OkhttpRequestBuilder<PostBuilder> {
    private static final String TAG = "zsr";
    private Call mCall;
    public PostBuilder(){

    }


    public PostBuilder(String url, String tag, ConcurrentHashMap<String, String> params,
                       ConcurrentHashMap<String, String> headers) {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;

        FormBody.Builder mFromBuilder = new FormBody.Builder();
        if (params != null){ //可能有多个params
            for (Map.Entry<String,String> entry : this.params.entrySet()){
                mFromBuilder.add(entry.getKey(),entry.getValue());
            }
        }

        FormBody formBody = mFromBuilder.build();

        Request.Builder builder = new Request.Builder();

        builder.url(url).tag(tag).post(formBody);

        if (this.headers != null && !this.headers.isEmpty()){
            for (Map.Entry<String,String> entry : this.headers.entrySet()){
                builder.addHeader(entry.getKey(),entry.getKey());
            }
        }


        Request request = builder.build();


        mCall = OkHttpCommonClient.getInstance().getOkhttpClient().newCall(request);
    }

    /**
     * 异步方法
     * @param listener
     * @return
     */
    public PostBuilder enqueue(final BaseCallback listener){
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
    public PostBuilder execute(BaseCallback listener){
        if (mCall != null){
            HandleCallUtils.executeCallBack(mCall,listener);
        }
        return this;
    }

    @Override
    public PostBuilder builder() {

        return new PostBuilder(url,tag,params,headers);
    }


}
