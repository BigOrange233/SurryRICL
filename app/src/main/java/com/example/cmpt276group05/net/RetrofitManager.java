package com.example.cmpt276group05.net;

import com.example.cmpt276group05.constant.BusinessConstant;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

//RetrofitManager

public class RetrofitManager {
    private static Map<String,Retrofit> retrofitMap = new HashMap<>();

    public static Retrofit getIntance(String baseUrl){
        if(!retrofitMap.containsKey(baseUrl)){
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(new LogCatInterceptor())
                    .build();
            Retrofit.Builder builder = new Retrofit.Builder();
            builder.baseUrl(baseUrl);
            builder.addConverterFactory(GsonConverterFactory.create());
            builder.client(okHttpClient);
            builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());
            Retrofit retrofit = builder.build();
            retrofitMap.put(baseUrl,retrofit);
        }

        return retrofitMap.get(baseUrl);
    }

    public static class LogCatInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY).intercept(chain);
        }
    }
}
