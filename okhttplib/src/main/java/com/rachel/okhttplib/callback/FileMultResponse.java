package com.rachel.okhttplib.callback;

import com.rachel.okhttplib.OkHttpCommonClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhengshaorui on 2017/8/8.
 * 字符串类
 */

public abstract class FileMultResponse extends BaseCallback<String> {
    private static final String TAG = "zsr";
    private List<DownloadTask> sDownloadTasks;//方便检测多线程是否下载完成
    private long sFileDownloadSize = 0; //方便显示多线程的进度
    private ExecutorService sExecutorService = Executors.newFixedThreadPool(8); //最大给8个线程
    private String downloadurl;
    private String path;
    private int threadCount;

    public FileMultResponse(String downloadurl, String path, int threadCount) {
        this.downloadurl = downloadurl;
        this.path = path;
        this.threadCount = threadCount;
    }

    @Override
    public String transCallback(Response response) throws IOException {
        String name = downloadurl.substring(downloadurl.lastIndexOf("/")+1);
        long contentlength = response.body().contentLength();
        long blocksize = contentlength / threadCount;
        sDownloadTasks = new ArrayList<DownloadTask>();
        sFileDownloadSize = 0;
        for (int i = 0; i < threadCount; i++) {
            DownloadTask task = new DownloadTask(downloadurl,path,name,blocksize,contentlength,i,
                    threadCount,this);
            sExecutorService.execute(task);
            sDownloadTasks.add(task);
        }
        return null;
    }

   class DownloadTask extends Thread {
       String url, path, name;
       long blocksize, filelength;
       int threadid, threadCount;
       BaseCallback listener;
       boolean isTheadFinished = false;
       long downloadLength = 0; //单个下载的大小

       public DownloadTask(String url, String path, String name,
                           long blocksize, long filelength, int threadId, int threadCount,
                           BaseCallback listener) {
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

           if (threadid == threadCount - 1) { //最后一个除不尽，用文件长度代替
               endpos = filelength;
           }
           Request request = new Request.Builder()
                   .addHeader("RANGE", "bytes=" + startpos + "-" + endpos) //设置单个线程下载起始位置和结束位置
                   .url(url)
                   .build();
           InputStream is = null;
           RandomAccessFile raf = null;
           Response response = null;
           try {
               response = OkHttpCommonClient.getInstance().getOkhttpClient().newCall(request).execute();
               is = response.body().byteStream();
               if (is == null) {
                   listener.onFailure("get noting inputstream");
               }
               //设置本地的存储

               File file = new File(path, name);
               raf = new RandomAccessFile(file, "rwd");
               raf.seek(startpos);
               byte[] bytes = new byte[4 * 1024];
               int len;
               long total = 0;
               while ((len = is.read(bytes)) != -1) {
                   raf.write(bytes, 0, len);
                   total += len;
                   sFileDownloadSize += len;
                   onProgress((int) (sFileDownloadSize * 100 / filelength));
               }
               isTheadFinished = true;
               String downloadpath = path + "/" + name;
                 checkComplete(listener,downloadpath);
           } catch (final IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();

               listener.onFailure(e);

           } finally {
               try {
                   if (is != null) {
                       is.close();
                   }
                   if (raf != null) {
                       raf.close();
                   }
                   if (response != null) {
                       response.body().close();
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }

       }
   }

    private synchronized void checkComplete(BaseCallback listener,final String path) {
        boolean allFinished = true;
        for (DownloadTask downloadTask : sDownloadTasks) {
            if (!downloadTask.isTheadFinished){
                allFinished = false;
            }
        }
        if (allFinished){

            listener.onSuccess(path);

        }
    }

    public abstract void onProgress(int progress);
}
