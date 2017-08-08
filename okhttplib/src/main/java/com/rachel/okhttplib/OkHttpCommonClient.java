package com.rachel.okhttplib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.rachel.okhttplib.builder.GetBuilder;
import com.rachel.okhttplib.request.DownloadListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhengshaorui on 2017/8/5.
 * okhttp 的通用 client 类
 */

public class OkHttpCommonClient {
    private static final String TAG = "zsr";
    private static OkHttpClient mOkHttpClient;
    private static final int TIME_OUT = 20;
    private static Handler mDeliverHandler = null;
    private static List<DownloadTask> sDownloadTasks;//方便检测多线程是否下载完成
    private static long sFileDownloadSize = 0; //方便显示多线程的进度
    private static ExecutorService sExecutorService = Executors.newFixedThreadPool(5); //最大给5个线程
    private OkHttpCommonClient(){
        mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT+TIME_OUT,TimeUnit.SECONDS)
                .build();

        mDeliverHandler = new Handler(Looper.getMainLooper());
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

    public static OkHttpClient getOkhttpClient(){
        return mOkHttpClient;
    }



    public static GetBuilder getBuilder(){
        return new GetBuilder();
    }

    /**
     * 获取string,获取的结果在主线程显示
     * @param request
     * @param listener
     */
    public static void getString(final Request request, final DisPoseListener listener){
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mDeliverHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    final String string = response.body().string();
                    mDeliverHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(string);
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取bitmap，直接返回，不做处理
     * @param request
     * @param listener
     */
    public static void getBitmap(Request request, final DisPoseListener listener){
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call,final IOException e) {
                mDeliverHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    InputStream is = response.body().byteStream();

                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    mDeliverHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(bitmap);
                        }
                    });
                }

            }
        });
    }

    /**
     * 获取图片，并根据宽高压缩图片
     * @param request
     * @param width
     * @param height
     * @param listener
     */
    public static void getBitmap(Request request, final int width, final int height, final DisPoseListener listener){
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call,final IOException e) {
                mDeliverHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ByteArrayOutputStream bos = null;
                if (response.isSuccessful()){
                    InputStream is = response.body().byteStream();
                    bos = new ByteArrayOutputStream();
                    byte[] bytes = new byte[512];
                    int len = -1;
                    while ( (len = is.read(bytes)) != -1 ){
                        bos.write(bytes,0,len);
                    }
                    bos.flush();
                    //把 bytearrayinputstream 转换成inputstream
                    is = new ByteArrayInputStream(bos.toByteArray());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    Bitmap bitmap = BitmapFactory.decodeStream(is,null,options);

                    //计算比例
                    options.inSampleSize = sampleSize(options,width,height);
                    options.inJustDecodeBounds = false;
                    is = new ByteArrayInputStream(bos.toByteArray());
                    bitmap = BitmapFactory.decodeStream(is,null,options);

                    final Bitmap finalBitmap = bitmap;
                    mDeliverHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(finalBitmap);
                        }
                    });

                }

            }
        });
    }

    /**
     * 获取json数据
     * @param request
     * @param target
     * @param listener
     * @param <T>
     */
    public static <T> void getJson(Request request,final Class<T> target, final DisPoseListener listener){
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call,final IOException e) {
                mDeliverHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String rootjson = response.body().string();
                Gson gson = new Gson();
                final T bean = gson.fromJson(rootjson,target);
                mDeliverHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSuccess(bean);
                    }
                });

            }
        });
    }

    /**
     * 单线程下载
     * @param request
     * @param downloadurl
     * @param path
     * @param listener
     */
    public static void getFile(final Request request, final String downloadurl,
                               final String path, final DownloadListener listener){

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call,final IOException e) {
                mDeliverHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = response.body().byteStream();
                final long contentlength = response.body().contentLength();
                Log.d(TAG, "contentlength "+contentlength);
                if (is == null){
                    listener.onFailure("filedown inputstream null");
                    return;
                }
                FileOutputStream fos = null;

                try {
                    final String name = downloadurl.substring(downloadurl.lastIndexOf("/")+1);
                    File file = new File(path,name);

                    int len = -1;
                    byte[] bytes = new byte[1024*4];
                    fos = new FileOutputStream(file);
                    long downloadlength = 0;
                    while ( (len = is.read(bytes)) != -1 ){
                        fos.write(bytes,0,len);
                        downloadlength += len;
                        final  long length = downloadlength;
                        mDeliverHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onProgress((int) (length*100/contentlength));
                            }
                        });
                    }
                    fos.flush();
                    mDeliverHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(path+"/"+name);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onFailure(e);
                }finally {
                    if (fos != null){
                        fos.close();
                    }
                }

            }
        });
    }


    /**
     * 多线程下载
     * @param request
     * @param downloadurl
     * @param threadCount 3个线程的下载效率最佳
     * @param path
     * @param listener
     */
    public static void getFileMultThead(final Request request, final String downloadurl,
                                        final int threadCount, final String path, final DownloadListener listener){
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String name = downloadurl.substring(downloadurl.lastIndexOf("/")+1);
                long contentlength = response.body().contentLength();
                long blocksize = contentlength / threadCount;
                sDownloadTasks = new ArrayList<DownloadTask>();
                sFileDownloadSize = 0;
                for (int i = 0; i < threadCount; i++) {
                  DownloadTask task = new DownloadTask(downloadurl,path,name,blocksize,contentlength,i,
                            threadCount,listener);
                    sExecutorService.execute(task);
                    sDownloadTasks.add(task);
                }
            }
        });

    }





    /**
     * 自动压缩比例
     * @param option
     * @param reWidth
     * @param reHeight
     * @return
     */
    private static int sampleSize(BitmapFactory.Options option,int reWidth,int reHeight){
        int width = option.outWidth;
        int height = option.outHeight;
        //Log.d(TAG, "sampleSize() called with: option = " +
        //        "[" + option.outWidth + " "+option.outHeight+"], reWidth = [" + reWidth + "], reHeight = [" + reHeight + "]");
        int inSampleSize = 1;
        if (width > reWidth && height > reHeight) {
            int radioWidth = Math.round(width*1.0f/reWidth);
            int radioHeight = Math.round(height*1.0f/reHeight);
            inSampleSize = Math.max(radioWidth, radioHeight);
        }
        return inSampleSize;
    }


    static class DownloadTask extends Thread{
        String url,path,name;
        long blocksize,filelength;
        int threadid,threadCount;
        DownloadListener listener;
        boolean isTheadFinished = false;
        long downloadLength = 0; //单个下载的大小
        public DownloadTask(String url, String path, String name,
        long blocksize, long filelength, int threadId,int threadCount,DownloadListener listener) {
            this.url = url;
            this.path = path;
            this.name = name;
            this.blocksize = blocksize;
            this.filelength = filelength;
            this.threadid = threadId;
            this.threadCount = threadCount;
            this.listener = listener;
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            long startpos = blocksize * threadid;
            long endpos = blocksize * (threadid + 1) - 1;

            if (threadid == threadCount -1 ){ //最后一个除不尽，用文件长度代替
                endpos = filelength;
            }
            Request request = new Request.Builder()
                    .addHeader("RANGE","bytes="+startpos+"-"+endpos) //设置单个线程下载起始位置和结束位置
                    .url(url)
                    .build();
            InputStream is = null;
            RandomAccessFile raf = null;
            Response response = null;
            try {
                response = mOkHttpClient.newCall(request).execute();
                is = response.body().byteStream();
                if (is == null) {
                    mDeliverHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFailure("get noting inputstream");
                        }
                    });

                    return;
                }
                //设置本地的存储

                File file = new File(path,name);
                raf = new RandomAccessFile(file,"rwd");
                raf.seek(startpos);
                byte[] bytes = new byte[4 * 1024];
                int len;
                long total = 0;
                while ( (len = is.read(bytes)) != -1 ){
                    raf.write(bytes,0,len);
                    total += len;
                    sFileDownloadSize += len;
                    listener.onProgress((int)(sFileDownloadSize*100/filelength));
                }
                isTheadFinished = true;
                String downloadpath = path + "/" + name;
                checkComplete(listener,downloadpath);
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                mDeliverHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailure(e);
                    }
                });
            } finally {
                try {
                    if (is != null){
                        is.close();
                    }
                    if (raf != null){
                        raf.close();
                    }
                    if (response != null){
                        response.body().close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        private synchronized void checkComplete(final DownloadListener listener,final String path) {
            boolean allFinished = true;
            for (DownloadTask downloadTask : sDownloadTasks) {
                if (!downloadTask.isTheadFinished){
                    allFinished = false;
                }
            }
            if (allFinished){
                mDeliverHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSuccess(path);
                    }
                });
            }
        }
    }

}
