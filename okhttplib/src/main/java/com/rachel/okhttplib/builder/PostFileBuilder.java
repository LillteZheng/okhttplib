package com.rachel.okhttplib.builder;


import com.rachel.okhttplib.HandleCallUtils;
import com.rachel.okhttplib.OkHttpCommonClient;
import com.rachel.okhttplib.callback.BaseCallback;
import com.rachel.okhttplib.request.CountRequestBody;
import com.rachel.okhttplib.request.OkhttpRequestBuilder;
import com.rachel.okhttplib.request.UploadListener;

import java.io.File;
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

public  class PostFileBuilder extends OkhttpRequestBuilder<PostFileBuilder> {
    private static final String TAG = "zsr";
    private static final String MEDIATYPE_STRING = "application/octet-stream";
    private Call mCall;
    private String type;
    private File file;
    private BaseCallback mUploadListener;
    public PostFileBuilder(){

    }

    public PostFileBuilder addMedieType(String type, File file) {
        this.type = type;
        this.file = file;
        if (this.file == null){
            try {
                throw  new Exception("file can not be null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.type == null){
            this.type = MEDIATYPE_STRING;
        }
        return this;
    }

    public PostFileBuilder(String url, String tag, String type, File file, ConcurrentHashMap<String, String> params,
                           ConcurrentHashMap<String, String> headers) {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;
        this.type = type;
        this.file = file;


        RequestBody formBody = FormBody.create(MediaType.parse(this.type),file);

        Request.Builder builder = new Request.Builder();

        CountRequestBody countRequestBody = new CountRequestBody(formBody, new UploadListener() {
            @Override
            public void onUploadProgress(int progress) {
                mUploadListener.onUploadProgress(progress);
            }
        });

        builder.url(url).tag(tag).post(countRequestBody);

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
    public PostFileBuilder enqueue(final BaseCallback listener){
        if (mCall != null){
            mUploadListener = listener;
            HandleCallUtils.enqueueCallBack(mCall,listener);
        }
        return this;
    }

    /**
     * 同步方法，需要在子线程执行，不然提示错误
     * @param listener
     * @return
     */
    public PostFileBuilder execute(BaseCallback listener){
        if (mCall != null){
            mUploadListener = listener;
            HandleCallUtils.executeCallBack(mCall,listener);
        }
        return this;
    }

    @Override
    public PostFileBuilder builder() {

        return new PostFileBuilder(url,tag,this.type,this.file,params,headers);
    }

}
