package com.haokan.pubic.maidian;

import android.content.Context;
import android.os.Build;

import com.haokan.pubic.App;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.UrlsUtil;
import com.haokan.statistics.HaokanStatistics;
import com.haokan.statistics.bean.UserBaseLogBean;

import java.util.Locale;

/**
 * Created by wangzixu on 2017/7/7.
 * 好看自己的埋点
 */
public class MaidianManager {
//    private static MaidianManager instance = new MaidianManager();
//    private MaidianManager (){}
//    public static MaidianManager getInstance() {
//        return instance;
//    }

    public static void initUser(Context context) {
        HaokanStatistics instance = HaokanStatistics.getInstance(context);
        instance.init(App.sDID, App.sPID, App.sEID, "0");

        UserBaseLogBean logBean = new UserBaseLogBean();
        logBean.setUserid(App.sDID);
        logBean.setProid(UrlsUtil.COMPANYID);
        logBean.setEid(App.sEID);
        logBean.setPid(App.sPID);
        logBean.setSdkver(App.APP_VERSION_NAME);
        logBean.setOsver(String.valueOf(Build.VERSION.SDK_INT));
        logBean.setIp(HttpStatusManager.getIPAddress(context));
        logBean.setModels(App.sPhoneModel);
        logBean.setNet(HttpStatusManager.getNetworkType(context));
        Locale aDefault = Locale.getDefault();
        logBean.setSyscountry(aDefault.getCountry());
        logBean.setSyslanguage(aDefault.getLanguage());
        instance.setBaseDataLog(logBean);

//        final MaidianUpBean requestEntity = new MaidianUpBean();
//        requestEntity.data = userInfo;
//
//        Observable<MaidianResponse> observable = HttpRetrofitManager.getInstance().getRetrofitService().maidianUpload("http://glog.levect.com/10000/user/log", requestEntity);
//        observable
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(new Subscriber<MaidianResponse>() {
//                @Override
//                public void onCompleted() {
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    e.printStackTrace();
//                }
//
//                @Override
//                public void onNext(MaidianResponse maidianResponse) {
//                }
//            });
    }

    /**
     * 单条报文之间的字段用逗号分隔，多条报文之间用^分割
     * 例：{'data':'10001,ud3d2d22sdss,1,1,1303343443338^10001,ud3d2d22sdss,1,1,1303343443338'}
     * @param itemId 物品id（组、单、CP）图片id
     * @param actype 图片操作。1曝光；2点赞；3收藏；4分享；5点击链接；6保存图片；7主动图说显示；8订阅；9取消订阅；
     *               10图片停留时间；11落地页停留时间；12不喜欢；13删除；14设为桌面；15更新锁屏图；16钉住；17点击设置
     * @param related 图片操作详细说明（对应actype）1曝光：0默认，1被动曝光，2主动曝光；2点赞：1点赞，0取消点赞；
     *                3收藏：1收藏，0取消收藏；4分享：0其他，1分享到微信，2分享到QQ，3分享到微博；5点击链接：默认0；
     *                6保存图片：默认0；7主动图说显示：1显示，0消失；8订阅：订阅分类列表（多个分号分隔）；
     *                9取消订阅：订阅分类列表（多个分号分隔）；10图片停留时间：停留毫秒数；
     *                11落地页停留时间：停留毫秒数；12不喜欢：默认0；13 删除：默认0；14 设为桌面：默认0；
     *                15更新锁屏图：0默认，1被动更新，2主动更新；16钉住：1钉住，0取消钉住；17点击设置：默认0
     *
     *
     */
    public synchronized static void setAction(Context context, String itemId, int actype, String related) {
        HaokanStatistics instance = HaokanStatistics.getInstance(context);
        instance.setActionLog(itemId, actype, related);

//        StringBuilder builder;
//        if (TextUtils.isEmpty(mActionInfo)) {
//            builder = new StringBuilder();
//        } else {
//            builder = new StringBuilder(mActionInfo).append("^");
//        }
//        builder.append(itemId).append(",")
//               .append(userid).append(",")
//               .append(actype).append(",")
//               .append(related).append(",")
//               .append(actime);
//        mActionInfo = builder.toString();
    }

//    private volatile static String mActionInfo = "";
//    private volatile static String mActionInfoTemp = "";
//    public synchronized static void actionUpdate() {
//        LogHelper.d("wangzixu", "hkmaidian actionUpdate called mActionInfo = " + mActionInfo);
//        if (TextUtils.isEmpty(mActionInfo)) {
//            return;
//        }
//        mActionInfoTemp = mActionInfo;
//        mActionInfo = "";
//        final MaidianUpBean requestEntity = new MaidianUpBean();
//        requestEntity.data = mActionInfoTemp;
//
//        Observable<MaidianResponse> observable = HttpRetrofitManager.getInstance().getRetrofitService().maidianUpload("http://glog.levect.com/10000/action/log", requestEntity);
//        observable
//                .subscribeOn(Schedulers.io())
//                .subscribe(new Subscriber<MaidianResponse>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        LogHelper.d("wangzixu", "hkmaidian upload failed e = " + e.getMessage());
//                        e.printStackTrace();
//                        if (TextUtils.isEmpty(mActionInfo)) {
//                            mActionInfo = mActionInfoTemp;
//                        } else {
//                            mActionInfo = mActionInfoTemp + "^" + mActionInfo;
//                        }
//                        mActionInfoTemp = "";
//                    }
//
//                    @Override
//                    public void onNext(MaidianResponse maidianResponse) {
//                        if (maidianResponse.getErr_code() == 0) {
//                            LogHelper.d("wangzixu", "hkmaidian upload success");
//                        } else {
//                            LogHelper.d("wangzixu", "hkmaidian upload failed maidianResponse = " + maidianResponse.getMessage());
//                            if (TextUtils.isEmpty(mActionInfo)) {
//                                mActionInfo = mActionInfoTemp;
//                            } else {
//                                mActionInfo = mActionInfoTemp + "^" + mActionInfo;
//                            }
//                        }
//                        mActionInfoTemp = "";
//                    }
//                });
//    }
}
