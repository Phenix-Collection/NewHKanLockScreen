package com.haokan.pubic.http;

import com.haokan.hklockscreen.lockscreen.bean.RequestBody_Switch;
import com.haokan.hklockscreen.lockscreen.bean.ResponseBody_Switch;
import com.haokan.pubic.http.request.RequestEntity;
import com.haokan.pubic.http.response.ResponseEntity;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by xiefeng on 16/8/3.
 */
public interface RetrofitHttpService {
    /**
     * 基本的get请求
     */
    @GET
    Observable<Object> get(@Url String url);

    /**
     * 点击换一换
     */
    @POST
    Observable<ResponseEntity<ResponseBody_Switch>> getSwitchData(@Url String url, @Body RequestEntity<RequestBody_Switch> requestEntity);

//    /**
//     * 获取升级信息
//     */
//    @GET
//    Observable<ResponseEntity<InitResponseWrapperBean>> getUpdataInfo(@Url String url);
//
//    /**
//     * 下载大文件，用@Streaming实时传输流，但是需要用call.exeute在自己的子线程执行，否则会报错
//     */
//    @Streaming
//    @GET
//    Call<ResponseBody> downloadBigFile(@Url String fileUrl);
//
//    /**
//     * 获取config
//     */
//    @POST
//    Observable<ResponseEntity<ResponseBody_Config>> getConfig(@Url String url, @Body RequestEntity<RequestBody_Config> requestEntity);


}
