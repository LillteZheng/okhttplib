package com.rachel.okhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.rachel.okhttplib.DisPoseListener;
import com.rachel.okhttplib.OkHttpCommonClient;
import com.rachel.okhttplib.request.CommonRequest;
import com.rachel.okhttplib.request.DownloadListener;
import com.rachel.okhttplib.request.RequestParams;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "zsr";
    private String imgurl = "http://upgrade.toptech-developer.com//file//launcher3-img//tvlog.jpg";
    private String gsonurl = "http://upgrade.toptech-developer.com/file/TvHouseManager/tvhousemanager.json";
    private String fileurl = "http://upgrade.toptech-developer.com/file/TvHouseManager/TvHouseManager.apk";
    private ImageView img ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView) findViewById(R.id.img);

    }

    public void get(View view){
        OkHttpCommonClient client = OkHttpCommonClient.getInstance();

        //获取心知天气
        RequestParams params = new  RequestParams();
        params.put("key","m9datavogh53ftie");
        params.put("location","shenzhen");
        params.put("language","zh-Hans");
        params.put("unit","c");

        client.getString(CommonRequest.createGetRequest("https://api.seniverse.com/v3/weather/now.json",
                params)
                , new DisPoseListener() {
                    @Override
                    public void onSuccess(Object resposeObj) {
                        Log.d(TAG, "onSuccess: "+resposeObj.toString());
                    }

                    @Override
                    public void onFailure(Object errorObj) {
                        Log.d(TAG, "onFailure: "+errorObj.toString());
                    }
                });
    }


    public void bitmap(View view){
        OkHttpCommonClient client = OkHttpCommonClient.getInstance();

        client.getBitmap(CommonRequest.createGetRequest(imgurl),
               200,100 , new DisPoseListener() {
                    @Override
                    public void onSuccess(Object resposeObj) {
                        img.setImageBitmap((Bitmap) resposeObj);
                    }

                    @Override
                    public void onFailure(Object errorObj) {
                        Log.d(TAG, "onFailure: "+errorObj.toString());
                    }
                });
    }

    public void gson(View view){
        OkHttpCommonClient client = OkHttpCommonClient.getInstance();

        client.getJson(CommonRequest.createGetRequest(gsonurl),Root.class, new DisPoseListener() {
            @Override
            public void onSuccess(Object resposeObj) {
                Root root = (Root) resposeObj;
                Log.d(TAG, "onSuccess: "+root.getUrl());
            }

            @Override
            public void onFailure(Object errorObj) {

            }
        });

    }

    public void file(View view){
        OkHttpCommonClient client = OkHttpCommonClient.getInstance();
        String path = Environment.getExternalStorageDirectory().getPath();
        Log.d(TAG, "path: "+path);
        client.getFile(CommonRequest.createGetRequest(fileurl), fileurl, path, new DownloadListener() {
            @Override
            public void onProgress(int progress) {
                Log.d(TAG, "onProgress: "+progress);
            }

            @Override
            public void onSuccess(Object resposeObj) {
                String path = (String) resposeObj;
                File file = new File(path);
                if (file.exists()){
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    img.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onFailure(Object errorObj) {
                Log.d(TAG, "onFailure: "+errorObj.toString());
            }
        });

    }

    public void filemult(View view){
        OkHttpCommonClient client = OkHttpCommonClient.getInstance();
        String path = Environment.getExternalStorageDirectory().getPath();

        client.getFileMultThead(CommonRequest.createGetRequest(fileurl),
                fileurl, 3, path, new DownloadListener() {
                    @Override
                    public void onProgress(int progress) {
                        Log.d(TAG, "onProgress: "+progress);
                    }

                    @Override
                    public void onSuccess(Object resposeObj) {
                        String path = (String) resposeObj;
                        File file = new File(path);
                        if (file.exists()){
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            img.setImageBitmap(bitmap);
                        }
                    }

                    @Override
                    public void onFailure(Object errorObj) {

                    }
                });
    }
}
