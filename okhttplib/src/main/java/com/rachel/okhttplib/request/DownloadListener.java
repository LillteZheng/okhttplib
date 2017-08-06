package com.rachel.okhttplib.request;

import com.rachel.okhttplib.DisPoseListener;

/**
 * Created by zhengshaorui on 2017/8/6.
 */

public interface DownloadListener extends DisPoseListener{
    void onProgress(int progress);
}
