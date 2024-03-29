package com.haokan.pubic.http;


import com.haokan.pubic.logsys.LogHelper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xiefeng on 16/8/3.
 */
public class HttpRetrofitManager {
    private RetrofitHttpService mRetrofitHttpService;

    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final HttpRetrofitManager INSTANCE = new HttpRetrofitManager();
    }

    public RetrofitHttpService getRetrofitService() {
        return mRetrofitHttpService;
    }

    //获取单例
    public static HttpRetrofitManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private HttpRetrofitManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("Connection", "close")
                        .build();
                return chain.proceed(request);
            }
        });

        if (LogHelper.DEBUG) { //okttp显示log
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
        }

        OkHttpClient build = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(build)
                .baseUrl("http://levect.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        mRetrofitHttpService = retrofit.create(RetrofitHttpService.class);
    }
}
