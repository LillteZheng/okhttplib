package com.rachel.okhttp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.rachel.okhttplib.OkHttpCommonClient;
import com.rachel.okhttplib.callback.BitmapResponse;
import com.rachel.okhttplib.callback.FileMultResponse;
import com.rachel.okhttplib.callback.FileResponse;
import com.rachel.okhttplib.callback.JsonResponse;
import com.rachel.okhttplib.callback.StringResponse;

import java.io.File;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "zsr";
    private String imgurl = "https://user-gold-cdn.xitu.io/2017/8/8/cf98920251dae85bf76fbd4cfeec35ac";


    private String Weather_baseurl = "https://api.seniverse.com/v3/weather/now.json";
    private ImageView img ;
    private GetApi mWeather;

    //自己搭的本地的服务器，请自行替换自己的。
    private static final String BASEURL = "http://192.168.138.1:8080/http_server/";
    private String gsonurl = BASEURL+"files/tvhousemanager.json";
    private String fileurl = BASEURL+"files/leibao.apk";

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

        client.postBuilder()
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
        OkHttpCommonClient client = OkHttpCommonClient.getInstance();
        client.postStringBuilder()
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
        OkHttpCommonClient client = OkHttpCommonClient.getInstance();
        File file = new File(Environment.getExternalStorageDirectory(),"TvHouseManager.apk");

        client.postFileBuilder()
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

        client.getBuilder()
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
        OkHttpCommonClient client = OkHttpCommonClient.getInstance();
        String path = Environment.getExternalStorageDirectory().getPath();
        client.getBuilder()
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

    public void filemult(View view){
        OkHttpCommonClient client = OkHttpCommonClient.getInstance();
        String path = Environment.getExternalStorageDirectory().getPath();
        client.getBuilder()
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
       /* client.getFileMultThead(CommonRequest.createGetRequest(fileurl),
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
                });*/
    }
}
