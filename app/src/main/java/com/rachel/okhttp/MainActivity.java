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
import com.rachel.okhttplib.callback.BitmapResponse;
import com.rachel.okhttplib.callback.JsonResponse;
import com.rachel.okhttplib.callback.StringResponse;
import com.rachel.okhttplib.request.CommonRequest;
import com.rachel.okhttplib.request.DownloadListener;
import com.rachel.okhttplib.request.RequestParams;

import java.io.File;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "zsr";
    private String imgurl = "https://user-gold-cdn.xitu.io/2017/8/8/cf98920251dae85bf76fbd4cfeec35ac";
    private String gsonurl = "http://upgrade.toptech-developer.com/file/TvHouseManager/tvhousemanager.json";
    private String fileurl = "http://upgrade.toptech-developer.com/file/TvHouseManager/TvHouseManager.apk";
    private String Weather_baseurl = "https://api.seniverse.com/v3/weather/now.json";
    private ImageView img ;
    private GetApi mWeather;

    //自己搭的本地的服务器，请自行替换自己的。
    private static final String BASEURL = "http://192.168.138.1:8080/http_server/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView) findViewById(R.id.img);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.seniverse.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mWeather = retrofit.create(GetApi.class);


    }

    public void doget(View view){



        OkHttpCommonClient client = OkHttpCommonClient.getInstance();
        client.getBuilder()
                .url(Weather_baseurl)
                .putParams("key","m9datavogh53ftie")
                .putParams("location","shenzhen")
                .builder()
                .enqueue(new StringResponse() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d(TAG, "builder: "+response);
                    }

                    @Override
                    public void onFailure(Object errorObj) {

                    }
                });

    }

    public void post(View view){


        OkHttpCommonClient client = OkHttpCommonClient.getInstance();

        //获取心知天气
        RequestParams params = new  RequestParams();
        params.put("username","zhengshaorui");
        params.put("password","123456789");


        client.getString(CommonRequest.createPostFileRequest(BASEURL+"getFile"
                )
                , new DisPoseListener() {
                    @Override
                    public void onSuccess(Object resposeObj) {
                        //Log.d(TAG, "onSuccess: "+resposeObj.toString());
                       // Toast.makeText(MainActivity.this, resposeObj.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Object errorObj) {
                        Log.d(TAG, "onFailure: "+errorObj.toString());
                    }
                });
    }


    public void bitmap(View view){
        OkHttpCommonClient client = OkHttpCommonClient.getInstance();

        client.getBuilder()
                .url(imgurl)
                .builder()
                .enqueue(new BitmapResponse(230,150) { //这里可以设置图片的大小
                    @Override
                    public void onSuccess(Bitmap response) {
                        img.setImageBitmap(response);
                    }

                    @Override
                    public void onFailure(Object errorObj) {
                        Log.d(TAG, "onFailure: "+errorObj.toString());
                    }
                });
    }

    public void gson(View view){
        OkHttpCommonClient client = OkHttpCommonClient.getInstance();

        /*client.getJson(CommonRequest.createGetRequest(gsonurl),Root.class, new DisPoseListener() {
            @Override
            public void onSuccess(Object resposeObj) {
                Root root = (Root) resposeObj;
                Log.d(TAG, "onSuccess: "+root.getUrl());
            }

            @Override
            public void onFailure(Object errorObj) {

            }
        });*/
        JsonResponse<Root> jsonResponse = new JsonResponse<Root>() {
            @Override
            public void onSuccess(Root response) {
                Log.d(TAG, "onSuccess: "+response);
            }

            @Override
            public void onFailure(Object errorObj) {
                Log.d(TAG, "onFailure: "+errorObj.toString());
            }
        };

        client.getBuilder()
                .url(gsonurl)
                .builder()
                .enqueue(jsonResponse);


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
