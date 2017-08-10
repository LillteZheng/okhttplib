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
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zhengshaorui on 2017/8/8.
 * 提交表单，比如说头像，名字，等等一起上传
 */

public class PostFormBuilder extends OkhttpRequestBuilder<PostFormBuilder> {
    private static final String TAG = "zsr";
    private static final String MEDIATYPE_STRING = "application/octet-stream";
    private Call mCall;
    private String type;
    private File file;
    private String name; //表达域的key
    private String formname; //w文件传过去命名的名字
    private ConcurrentHashMap<String,String> multiPart;
    private BaseCallback mUploadListener;
    public PostFormBuilder(){

    }

    public PostFormBuilder addFile(String name, String formname, File file){
        this.name = name;
        this.formname = formname;
        this.file = file;
        return this;
    }



    public PostFormBuilder addMultPart(ConcurrentHashMap<String,String> multiPart){
        this.multiPart = multiPart;

        return this;
    }

    public PostFormBuilder addPart(String key,String value){
        if (this.multiPart == null){
            this.multiPart = new ConcurrentHashMap<String,String>();
        }
        this.multiPart.put(key,value);
        return this;
    }

    public PostFormBuilder(String url, String tag, String name, String formname, File file,
                           ConcurrentHashMap<String, String> params,
                           ConcurrentHashMap<String, String> headers,
                           ConcurrentHashMap<String, String> multiPart) {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;
        this.name = name;
        this.formname = formname;
        this.file = file;
        this.multiPart = multiPart;


        MultipartBody.Builder multBuilder = new MultipartBody.Builder();
        multBuilder.setType(MultipartBody.FORM);
        if (this.multiPart != null){
            for (Map.Entry<String,String> entry : this.multiPart.entrySet()){
                multBuilder.addFormDataPart(entry.getKey(),entry.getValue());
            }
        }
        multBuilder.addFormDataPart(this.name,this.formname,FormBody.create(MediaType.parse(MEDIATYPE_STRING),file));

        RequestBody formBody = multBuilder.build();

        CountRequestBody countRequestBody = new CountRequestBody(formBody, new UploadListener() {
            @Override
            public void onUploadProgress(int progress) {
                mUploadListener.onUploadProgress(progress);
            }
        });

        Request.Builder builder = new Request.Builder();


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
    public PostFormBuilder enqueue(final BaseCallback listener){
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
    public PostFormBuilder execute(BaseCallback listener){
        if (mCall != null){
            mUploadListener = listener;
            HandleCallUtils.executeCallBack(mCall,listener);
        }
        return this;
    }

    @Override
    public PostFormBuilder builder() {

        return new PostFormBuilder(url,tag,name,formname,file,params,headers,multiPart);
    }


}
