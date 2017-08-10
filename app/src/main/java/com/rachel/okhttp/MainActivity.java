package com.rachel.okhttp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.rachel.okhttplib.OkHttpCommonClient;
import com.rachel.okhttplib.callback.BitmapResponse;
import com.rachel.okhttplib.callback.FileMultResponse;
import com.rachel.okhttplib.callback.FileResponse;
import com.rachel.okhttplib.callback.JsonResponse;
import com.rachel.okhttplib.callback.StringResponse;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "zsr";
    private static final int TIME_OUT = 20;
    private String imgurl = "https://user-gold-cdn.xitu.io/2017/8/8/cf98920251dae85bf76fbd4cfeec35ac";
    private String Weather_baseurl = "https://api.seniverse.com/v3/weather/now.json";
    private ImageView img ;
    private GetApi mWeather;

    //自己搭的本地的服务器，请自行替换自己的。
    private static final String BASEURL = "http://192.168.138.1:8080/http_server/";
    private String gsonurl = BASEURL+"files/tvhousemanager.json";
    private String fileurl = BASEURL+"files/tvlog.jpg";
    private OkHttpCommonClient mClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView) findViewById(R.id.img);

       OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(TIME_OUT+TIME_OUT,TimeUnit.SECONDS)
                    .cookieJar(new PersistentCookieJar(new SetCookieCache(),new SharedPrefsCookiePersistor(this)))
                    .build();



        mClient = OkHttpCommonClient.getInstance();
        mClient.setOkhttpClient(okHttpClient);

        Log.d(TAG, "onCreate: ");
    }

    public void doget(View view){



       
        mClient.getBuilder()
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




    public void bitmap(View view){
       

        mClient.getBuilder()
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
    //json 下载，Root.class 是我的实体类，这里使用的是gson
    public void gson(View view){
       

        mClient.getBuilder()
                .url(gsonurl)
                .builder()
                .enqueue(new JsonResponse(Root.class) { //这里的颜色区域是 jdk1.5的警告，
                                                        // 因为用的是泛型,直接关掉即可。
                    @Override
                    public void onSuccess(Object response) {
                        Root root = (Root) response;
                        Log.d(TAG, "onSuccess: "+root.getContent());
                    }

                    @Override
                    public void onFailure(Object errorObj) {

                    }
                });

    }

    public void file(View view){
       
        String path = Environment.getExternalStorageDirectory().getPath();
        mClient.getBuilder()
                .url(fileurl)
                .builder()
                .enqueue(new FileResponse(fileurl,path) {
                    @Override
                    public void onProgress(int progress) {
                        Log.d(TAG, "onProgress: "+progress);
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.d(TAG, "onSuccess: "+response);
                    }

                    @Override
                    public void onFailure(Object errorObj) {
                        Log.d(TAG, "onFailure: "+errorObj.toString());
                    }
                });

    }
    //多线程下载
    public void filemult(View view){
       
        String path = Environment.getExternalStorageDirectory().getPath();

        mClient.getBuilder()
                .url(fileurl)
                .builder()
                .enqueue(new FileMultResponse(fileurl,path,3) {
                    @Override
                    public void onProgress(int progress) {
                        Log.d(TAG, "onProgress: "+progress);
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.d(TAG, "onSuccess: "+response);
                    }

                    @Override
                    public void onFailure(Object errorObj) {
                        Log.d(TAG, "onFailure: "+errorObj.toString());
                    }
                });
    }


    public void post(View view){


       

        mClient.postBuilder()
                .url(BASEURL+"login")
                .putParams("username","zhengshaorui")
                .putParams("password","123456789")
                .builder()
                .enqueue(new StringResponse() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d(TAG, "onSuccess: "+response);
                    }

                    @Override
                    public void onFailure(Object errorObj) {
                        Log.d(TAG, "onFailure: "+errorObj.toString());
                    }
                });
    }
    public void poststring(View view){
       
        mClient.postStringBuilder()
                .url(BASEURL+"getString")
                .addMedieType("text/plain;chaset-utf-8","{username:rachel,password:123}")
                .builder()
                .enqueue(new StringResponse() {
                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onFailure(Object errorObj) {

                    }
                });

    }

    public void postfile(View view){
       
        File file = new File(Environment.getExternalStorageDirectory(),"TvHouseManager.apk");
        if (!file.exists()){
            Toast.makeText(this, "TvHouseManager.apk" + "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        mClient.postFileBuilder()
                .url(BASEURL+"getFile")
                .addMedieType("application/vnd.android.package-archive",file)
                .builder()
                .enqueue(new StringResponse() {
                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onFailure(Object errorObj) {

                    }

                    @Override
                    public void onUploadProgress(int progress) {
                        super.onUploadProgress(progress);
                        Log.d(TAG, "onUploadProgress: "+progress);
                    }
                });




    }


    public void postupload(View view){
       
        File file = new File(Environment.getExternalStorageDirectory(),"tvlog.jpg");
        if (!file.exists()){
            Toast.makeText(this, "tvlog.jpg" + "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        mClient.postUploadFile()
                .url(BASEURL+"UpdateInfo")
                .addFile("mPic","mTestPhone.jpg",file)
                .addPart("username","zhengshaorui")
                .addPart("password","10086")
                .builder()
                .enqueue(new StringResponse() {
                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onFailure(Object errorObj) {

                    }

                    @Override
                    public void onUploadProgress(int progress) {
                        super.onUploadProgress(progress);
                        Log.d(TAG, "onUploadProgress: "+progress);
                    }
                });


    }
}
