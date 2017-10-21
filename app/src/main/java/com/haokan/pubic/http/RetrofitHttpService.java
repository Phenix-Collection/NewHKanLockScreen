package com.haokan.pubic.http;

import com.haokan.hklockscreen.lockscreen.RequestBody_Switch;
import com.haokan.hklockscreen.lockscreen.ResponseBody_Switch;
import com.haokan.hklockscreen.recommendpage.RequestBody_Recommend;
import com.haokan.hklockscreen.recommendpage.ResponseBody_Recommend;
import com.haokan.hklockscreen.timeline.RequestBody_Timelines;
import com.haokan.hklockscreen.timeline.ResponseBody_Timelines;
import com.haokan.pubic.checkupdate.RequestBody_Update;
import com.haokan.pubic.checkupdate.ResponseBody_Update;
import com.haokan.pubic.http.request.RequestEntity;
import com.haokan.pubic.http.response.ResponseEntity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
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
     * 自动更新
     */
    @POST
    Observable<ResponseEntity<ResponseBody_Update>> getUpdateData(@Url String url, @Body RequestEntity<RequestBody_Update> requestEntity);

    /**
     * 点击换一换
     */
    @POST
    Observable<ResponseEntity<ResponseBody_Switch>> getSwitchData(@Url String url, @Body RequestEntity<RequestBody_Switch> requestEntity);

    /**
     * 点击推荐数据
     */
    @POST
    Observable<ResponseEntity<ResponseBody_Recommend>> getRecommendData(@Url String url, @Body RequestEntity<RequestBody_Recommend> requestEntity);

    /**
     * 点击时间线
     */
    @POST
    Observable<ResponseEntity<ResponseBody_Timelines>> getTimelinesData(@Url String url, @Body RequestEntity<RequestBody_Timelines> requestEntity);

    /**
     * 下载大文件，用@Streaming实时传输流，但是需要用call.exeute在自己的子线程执行，否则会报错
     */
    @Streaming
    @GET
    Call<ResponseBody> downloadBigFile(@Url String fileUrl);
}
