package com.haokan.pubic.maidian;

import android.text.TextUtils;
import com.haokan.pubic.http.HttpRetrofitManager;
import com.haokan.pubic.util.LogHelper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/7/7.
 */
public class MaidianManager {
//    private static MaidianManager instance = new MaidianManager();
//    private MaidianManager (){}
//    public static MaidianManager getInstance() {
//        return instance;
//    }

    public static void initUser(final String userInfo) {
        final MaidianUpBean requestEntity = new MaidianUpBean();
        requestEntity.data = userInfo;

        Observable<MaidianResponse> observable = HttpRetrofitManager.getInstance().getRetrofitService().maidianUpload("http://glog.gray.levect.com/api/user/log", requestEntity);
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<MaidianResponse>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onNext(MaidianResponse maidianResponse) {
                }
            });
    }

    /**
     * 单条报文之间的字段用逗号分隔，多条报文之间用^分割
     * 例：{'data':'10001,ud3d2d22sdss,1,1,1303343443338^10001,ud3d2d22sdss,1,1,1303343443338'}
     * @param itemId 物品id（组、单、CP）
     * @param userid 用户id
     * @param actype 图片操作。1曝光（单图、组图），2点赞（组图），3收藏（组图），4分享（组图），5点击组图（组图），6保存图片（单图），7图说显示（单图），8订阅CP（CP），9停留时间（单图），10 进入cp页
     * @param related 图片操作说明。曝光：1单图曝光，2组图曝光；点赞：1点赞，0取消点赞；收藏：1收藏，0取消收藏；分享：无；点击组图：1首页进入，2最鲜页进入，3我的收藏页进入，4cp进入，5榜单页进入，6详情推荐进入；保存图片：无；图说显示：1显示，0消失；订阅CP：1订阅，0取消订阅；停留时间：停留毫秒数；进入cp页：1首页进入，2最鲜页进入，3好伴页进入，4我的关注页进入
     * @param actime 操作时间戳（精确到毫秒13位）
     */
    public synchronized static void setAction(String itemId, String userid, int actype, String related, long actime) {
        StringBuilder builder;
        if (TextUtils.isEmpty(mActionInfo)) {
            builder = new StringBuilder();
        } else {
            builder = new StringBuilder(mActionInfo).append("^");
        }
        builder.append(itemId).append(",")
               .append(userid).append(",")
               .append(actype).append(",")
               .append(related).append(",")
               .append(actime);
        mActionInfo = builder.toString();
    }

    private volatile static String mActionInfo = "";
    private volatile static String mActionInfoTemp = "";
    public synchronized static void actionUpdate() {
        LogHelper.d("wangzixu", "hkmaidian actionUpdate called mActionInfo = " + mActionInfo);
        if (TextUtils.isEmpty(mActionInfo)) {
            return;
        }
        mActionInfoTemp = mActionInfo;
        mActionInfo = "";
        final MaidianUpBean requestEntity = new MaidianUpBean();
        requestEntity.data = mActionInfoTemp;

        Observable<MaidianResponse> observable = HttpRetrofitManager.getInstance().getRetrofitService().maidianUpload("http://glog.gray.levect.com/api/action/log", requestEntity);
        observable
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<MaidianResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogHelper.d("wangzixu", "hkmaidian upload failed e = " + e.getMessage());
                        e.printStackTrace();
                        if (TextUtils.isEmpty(mActionInfo)) {
                            mActionInfo = mActionInfoTemp;
                        } else {
                            mActionInfo = mActionInfoTemp + "^" + mActionInfo;
                        }
                        mActionInfoTemp = "";
                    }

                    @Override
                    public void onNext(MaidianResponse maidianResponse) {
                        if (maidianResponse.getErr_code() == 0) {
                            LogHelper.d("wangzixu", "hkmaidian upload success");
                        } else {
                            LogHelper.d("wangzixu", "hkmaidian upload failed maidianResponse = " + maidianResponse.getMessage());
                            if (TextUtils.isEmpty(mActionInfo)) {
                                mActionInfo = mActionInfoTemp;
                            } else {
                                mActionInfo = mActionInfoTemp + "^" + mActionInfo;
                            }
                        }
                        mActionInfoTemp = "";
                    }
                });
    }
}
