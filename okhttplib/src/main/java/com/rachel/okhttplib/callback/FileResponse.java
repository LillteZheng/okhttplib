package com.rachel.okhttplib.callback;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * Created by zhengshaorui on 2017/8/8.
 * 字符串类
 */

public abstract class FileResponse extends BaseCallback<String> {
    private static final String TAG = "zsr";
    private String downloadurl;
    private String path;

    public FileResponse(String downloadurl, String path) {
        this.downloadurl = downloadurl;
        this.path = path;
    }

    @Override
    public String transCallback(Response response) throws IOException {
        InputStream is = response.body().byteStream();
        final long contentlength = response.body().contentLength();
        Log.d(TAG, "contentlength "+contentlength);
        if (is == null){
            onFailure("filedown inputstream null");

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
                onProgress((int) (downloadlength*100/contentlength));

            }
            fos.flush();

            return path+"/"+name;

        } catch (IOException e) {
            e.printStackTrace();
            onFailure(e);
        }finally {
            if (fos != null){
                fos.close();
            }
        }
        return null;
    }

    public abstract void onProgress(int progress);
}
