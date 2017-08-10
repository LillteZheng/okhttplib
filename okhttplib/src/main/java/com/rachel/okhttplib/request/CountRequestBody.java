package com.rachel.okhttplib.request;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by zhengshaorui on 2017/8/10.
 */

public class CountRequestBody extends RequestBody {
    private static final String TAG = "zsr";
    private RequestBody requestBody;
    private UploadListener mUploadListener;
    public CountRequestBody(RequestBody requestBody,UploadListener listener) {
        this.requestBody = requestBody;
        this.mUploadListener = listener;
        Log.d(TAG, "CountRequestBody: "+mUploadListener);
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength()   {

        try {
            return requestBody.contentLength();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        CountSink countSink = new CountSink(sink);

        BufferedSink bufferedSink = Okio.buffer(countSink);
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    protected final class CountSink extends ForwardingSink{
        private long sum;
        public CountSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            sum += byteCount;
           // Log.d(TAG, "write: "+sum+" / "+contentLength());
            if (mUploadListener != null) {
                mUploadListener.onUploadProgress((int) (sum *100 / contentLength()));
            }
        }
    }
}
