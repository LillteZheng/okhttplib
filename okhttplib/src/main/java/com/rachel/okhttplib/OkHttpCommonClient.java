package com.rachel.okhttplib;

import com.rachel.okhttplib.builder.GetBuilder;
import com.rachel.okhttplib.builder.PostBuilder;
import com.rachel.okhttplib.builder.PostFileBuilder;
import com.rachel.okhttplib.builder.PostFormBuilder;
import com.rachel.okhttplib.builder.PostStringBuilder;

import okhttp3.OkHttpClient;

/**
 * Created by zhengshaorui on 2017/8/5.
 * okhttp 的通用 client 类
 */

public class OkHttpCommonClient {
    private static final String TAG = "zsr";
    private OkHttpClient mOkHttpClient;


    private OkHttpCommonClient(){
    }

    public static OkHttpCommonClient getInstance(){
        return  SingleTon.INSTANCE.getSingleTon();
    }

    enum  SingleTon{
        INSTANCE;
        private OkHttpCommonClient client;
        SingleTon(){
            client = new OkHttpCommonClient();
        }
        public OkHttpCommonClient getSingleTon(){
            return client;
        }
    }

    public void setOkhttpClient(OkHttpClient client){
        mOkHttpClient = client;
    }

    public  OkHttpClient getOkhttpClient(){
        return mOkHttpClient;
    }



    public GetBuilder getBuilder(){
        return new GetBuilder();
    }

    public PostBuilder postBuilder(){
        return new PostBuilder();
    }

    public PostStringBuilder postStringBuilder(){
        return new PostStringBuilder();
    }

    public PostFileBuilder postFileBuilder(){
        return new PostFileBuilder();
    }
    public PostFormBuilder postUploadFile(){
        return new PostFormBuilder();
    }




}
