package com.rachel.okhttplib.builder;

import com.rachel.okhttplib.HandleCallUtils;
import com.rachel.okhttplib.OkHttpCommonClient;
import com.rachel.okhttplib.callback.BaseCallback;
import com.rachel.okhttplib.request.OkhttpRequestBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zhengshaorui on 2017/8/8.
 */

public class PostStringBuilder extends OkhttpRequestBuilder<PostStringBuilder> {
    private static final String TAG = "zsr";
    private static final String MEDIATYPE_STRING = "text/plain;chaset-utf-8";
    private Call mCall;
    private String type;
    private String content;
    public PostStringBuilder(){

    }

    public PostStringBuilder addMedieType(String type, String content) {
        this.type = type;
        this.content = content;
        if (this.content == null){
            try {
                throw  new Exception("content can not be null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.type == null){
            this.type = MEDIATYPE_STRING;
        }
        return this;
    }

    public PostStringBuilder(String url, String tag, String type, String content, ConcurrentHashMap<String, String> params,
                             ConcurrentHashMap<String, String> headers) {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;
        this.type = type;
        this.content = content;


        //设置成字符串形式，一般为发送一个 json
        RequestBody formBody = FormBody.create(MediaType.parse(this.type),content);

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
    public PostStringBuilder enqueue(final BaseCallback listener){
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
    public PostStringBuilder execute(BaseCallback listener){
        if (mCall != null){
            HandleCallUtils.executeCallBack(mCall,listener);
        }
        return this;
    }

    @Override
    public PostStringBuilder builder() {

        return new PostStringBuilder(url,tag,this.type,this.content,params,headers);
    }


}
