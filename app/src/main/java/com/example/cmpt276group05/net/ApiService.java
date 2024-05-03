package com.example.cmpt276group05.net;

import com.example.cmpt276group05.model.InspectionEntry;
import com.example.cmpt276group05.model.RestaurantEntry;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {
    //get restaurant json data
    @GET("api/3/action/package_show")
    Call<RestaurantEntry> getData(@Query("id") String id);

    //get inspection json data
    @GET("api/3/action/package_show")
    Call<InspectionEntry> getInspectionData(@Query("id") String id);

    //get csv file
    @Streaming
    @GET
    Call<ResponseBody> download(@Url String url);
}
