package com.rachel.okhttplib.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * Created by zhengshaorui on 2017/8/8.
 */

public abstract class BitmapResponse extends BaseCallback<Bitmap>{
    private static final String TAG = "zsr";

    private int width = 0 ;
    private int height = 0;


    public BitmapResponse() {
    }
    //在这里可以设置图片大小，压缩之后再展示
    public BitmapResponse(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Bitmap transCallback(Response response) throws IOException {
        InputStream is = response.body().byteStream();
        if (width != 0 && height != 0){
            
            ByteArrayOutputStream bos = null;
            try {
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
                BitmapFactory.decodeStream(is,null,options);

                //计算比例
                options.inSampleSize = sampleSize(options,width,height);
                options.inJustDecodeBounds = false;
                is = new ByteArrayInputStream(bos.toByteArray());

                return BitmapFactory.decodeStream(is,null,options);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null){
                    bos.close();
                }
            }
        }
        return BitmapFactory.decodeStream(is);
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
        Log.d(TAG, "sampleSize() called with: option = " +
                "[" + option.outWidth + " "+option.outHeight+"], reWidth = [" + reWidth + "], reHeight = [" + reHeight + "]");
        int inSampleSize = 1;
        if (width > reWidth && height > reHeight) {
            int radioWidth = Math.round(width*1.0f/reWidth);
            int radioHeight = Math.round(height*1.0f/reHeight);
            inSampleSize = Math.max(radioWidth, radioHeight);
        }
        return inSampleSize;
    }
}
