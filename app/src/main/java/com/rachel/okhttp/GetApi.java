package com.rachel.okhttp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by zhengshaorui on 2017/8/6.
 */

public interface GetApi {
   @GET("TvHouseManager/tvhousemanager.json")
   Call<Root> getInfo();

   @GET("v3/weather/now.json?key=m9datavogh53ftie")
   Call<ResponseBody> getWeather(@Query("location") String name);
}
