package com.haokan.pubic.checkupdate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.App;
import com.haokan.pubic.http.HttpRetrofitManager;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.FileUtil;
import com.haokan.pubic.util.ToastManager;
import com.haokan.pubic.util.Values;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class ServiceUpdate extends Service {
    public static String TAG = "DownloadUpdateApkService";
    public static int NOTIFY_ID = 100;
    public static final String DOWNLOAD_INFO = "download_info";
    private NotificationManager mNotificationManager;
    private boolean mIsDownLoading; //正在下载
    private NumberFormat mFormat;
    private NotificationCompat.Builder mBuilder;
    private int mI;

    public ServiceUpdate() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFormat = NumberFormat.getPercentInstance();
        mFormat.setMaximumFractionDigits(0); //不要小数点，如58%。
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && !mIsDownLoading) {
            BeanUpdate updateBean = intent.getParcelableExtra(DOWNLOAD_INFO);
            if (updateBean == null) {
                LogHelper.e(TAG, "onStartCommand mUpdateBean is NUll! return");
                ServiceUpdate.this.stopSelf();
            } else {
                startDownload(updateBean);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startDownload(BeanUpdate updateBean) {
        try {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                mIsDownLoading = false;
                LogHelper.i("wangzixu", "startDownload Environment MEDIA_MOUNTED fail");
                return;
            }

            String url = updateBean.getVerDownUrl();
            String apkName = App.sPID + "_" + updateBean.getVerCode() + ".apk";

            final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + Values.Path.PATH_DOWNLOADAPK;
            final File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            final File file = new File(dir, apkName);

            //判断file是否存在, 存在的话直接安装
            if (file.exists() && file.length() > 0) { //存在，因为是下载完成了才改成这个名字，所以只要存在，就是下载完了
                mIsDownLoading = false;
                UpdateManager.installApp(file, getApplicationContext());
                App.sMainHanlder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ServiceUpdate.this.stopSelf();
                    }
                }, 1000);
                return;
            }

            FileUtil.deleteContents(dir, false); //清空一下文件夹
            final String temp_name = apkName + "_temp";
            final File fileTemp = new File(dir, temp_name);
            if (!fileTemp.exists()) {
                try {
                    if (!fileTemp.createNewFile()) {
                        LogHelper.i(TAG, "startDownload file.createNewFile() fail");
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    mIsDownLoading = false;
                    return;
                }
            }

            mI = 0;
            ToastManager.showShort(this, getString(R.string.begin_download));
            initNotification();

//            downLoadFileWithRetrofit(url, fileTemp, file);
            downloadFileWithUrlConn(url, fileTemp, file);
        } catch (Exception e) {
            mNotificationManager.cancel(NOTIFY_ID);
            mIsDownLoading = false;
            e.printStackTrace();
            this.stopSelf();
        }
    }

    private void initNotification() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder = new NotificationCompat.Builder(this); //获取一个Notification构造器
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher)) // 设置下拉列表中的图标(大图标)
                .setSmallIcon(R.drawable.icon_small_notifycation) // 设置状态栏内的小图标
                .setColor(0xffCA2D74)
                .setContentTitle("好看锁屏") // 设置下拉列表里的标题
                .setContentText("下载中, 请稍后...") // 设置上下文内容
                .setTicker("下载中...")
                .setProgress(100, 0, false)
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        Notification notification = mBuilder.build(); // 获取构建好的Notification
        mNotificationManager.notify(NOTIFY_ID, notification);

//        mBuilder = new NotificationCompat.Builder(this)
//                .setTicker("下载中...")
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setProgress(100, 0, true)
//                .setContentTitle("下载中, 请稍后...")
//                .setOngoing(true);
//        Notification notification = mBuilder.build();
//        mNotificationManager.notify(NOTIFY_ID, notification);
    }

    /**
     * Java原生的API可用于发送HTTP请求，即java.net.URL、java.net.URLConnection，这些API很好用、很常用，但不够简便；
     * 1.通过统一资源定位器（java.net.URL）获取连接器（java.net.URLConnection） 2.设置请求的参数 3.发送请求
     * 4.以输入流的形式获取返回内容 5.关闭输入流
     * <p>
     * 好处是可以监听到下载的进度, 用retrofit下载, 监听进度就很麻烦
     */
    public void downloadFileWithUrlConn(final String urlPath, final File fileTemp, final File file) {
        LogHelper.d("wangzixu", "downloadFileCall downloadFileWithUrlConn urlPath = " + urlPath);
        final Scheduler.Worker worker = Schedulers.io().createWorker();
        worker.schedule(new Action0() {
            @Override
            public void call() {
                try {
                    // 统一资源
                    URL url = new URL(urlPath);
                    //获取http的连接类
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    // 设定请求的方法，默认是GET
                    httpURLConnection.setRequestMethod("GET");
                    //连接的超时时间
                    httpURLConnection.setConnectTimeout(10000);
                    //读数据的超时时间
                    httpURLConnection.setReadTimeout(10000);
                    httpURLConnection.setInstanceFollowRedirects(true);
                    // 设置字符编码
                    //httpURLConnection.setRequestProperty("Charset", "UTF-8");

                    int responseCode = httpURLConnection.getResponseCode();
                    if (responseCode == 200) {
                        int fileLength = httpURLConnection.getContentLength();
                        InputStream inputStream = httpURLConnection.getInputStream();
                        FileUtil.writeInputStreamToFile(inputStream, fileTemp, fileLength, new FileUtil.ProgressListener() {
                            @Override
                            public void onStart(long total) {
                                mIsDownLoading = true;
                                LogHelper.d("wangzixu", "downloadFileCall onStart total = " + total);
                            }

                            @Override
                            public void onProgress(long current, long total) {
                                String text = mFormat.format(current * 1.0f / total);
                                LogHelper.d("okhttp", "downloadFileCall onProgress " + text);
                                if (mI++ % 100 == 0) {
                                    mBuilder.setSmallIcon(R.drawable.ic_launcher);
                                    mBuilder.setProgress((int) total, (int) current, false); //设置通知栏中的进度条
                                    mBuilder.setContentText(text); //设置进度条下面的文字信息
                                    mBuilder.setOngoing(true);
                                    Notification notification = mBuilder.build();
                                    mNotificationManager.notify(NOTIFY_ID, notification);
                                }
                            }

                            @Override
                            public void onSuccess() {
                                LogHelper.d("okhttp", "downloadFileCall onComplete");
                                mNotificationManager.cancel(NOTIFY_ID);
                                boolean b = fileTemp.renameTo(file);
                                if (b) {
                                    UpdateManager.installApp(file, getApplicationContext());
                                } else {
                                    UpdateManager.installApp(fileTemp, getApplicationContext());
                                }
                                mIsDownLoading = false;
                                App.sMainHanlder.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ServiceUpdate.this.stopSelf();
                                    }
                                }, 1000);
                                return;
                            }

                            @Override
                            public void onFailure(Exception e) {
                                e.printStackTrace();
                                mNotificationManager.cancel(NOTIFY_ID);
                                mIsDownLoading = false;
                                App.sMainHanlder.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastManager.showShort(getApplicationContext(), "下载失败");
                                        ServiceUpdate.this.stopSelf();
                                    }
                                });
                            }
                        });
                    } else {
                        //永久重定向和临时重定向
                        if (responseCode == 301 || responseCode == 302) {
                            LogHelper.d("wangzixu", "downloadFileCall 重定向");
                            String url302 = httpURLConnection.getHeaderField("Location");
                            if (TextUtils.isEmpty(url302)) {
                                url302 = httpURLConnection.getHeaderField("location"); //临时重定向和永久重定向location的大小写有区分
                            }
                            if (!url302.startsWith("http://") && !url302.startsWith("https://")) { //某些时候会省略host，只返回后面的path，所以需要补全url
                                url302 = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + url302;
                            }
                            downloadFileWithUrlConn(url302, fileTemp, file);
                        } else {
                            throw new Exception("responseCode = " + responseCode);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogHelper.d("wangzixu", "downloadFileCall Exception = " + e.getMessage());
                    mNotificationManager.cancel(NOTIFY_ID);
                    mIsDownLoading = false;
                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastManager.showShort(getApplicationContext(), "下载失败");
                            ServiceUpdate.this.stopSelf();
                        }
                    });
                }
                worker.unsubscribe();
            }
        });
    }

    public void downLoadFileWithRetrofit(String url, final File fileTemp, final File file) {
        LogHelper.d("okhttp", "downloadFileCall downLoadFileWithRetrofit url = " + url);
        //用retrofit实现的下载升级包
        final Call<ResponseBody> downloadFileCall = HttpRetrofitManager.getInstance().getRetrofitService().downloadBigFile(url);
        final Scheduler.Worker worker = Schedulers.io().createWorker();
        worker.schedule(new Action0() {
            @Override
            public void call() {
                try {
                    Response<ResponseBody> response = downloadFileCall.execute();
                    FileUtil.writeInputStreamToFile(response.body().byteStream(), fileTemp
                            , response.body().contentLength(), new FileUtil.ProgressListener() {
                                @Override
                                public void onStart(long total) {
                                    mIsDownLoading = true;
                                    LogHelper.d("okhttp", "downloadFileCall onStart total = " + total);
                                }

                                @Override
                                public void onProgress(long current, long total) {
                                    String text = mFormat.format(current * 1.0f / total);
                                    LogHelper.d("okhttp", "downloadFileCall onProgress " + text);
                                    if (mI++ % 80 == 0) {
                                        mBuilder.setProgress((int) total, (int) current, false); //设置通知栏中的进度条
                                        mBuilder.setContentText(text); //设置进度条下面的文字信息
                                        Notification notification = mBuilder.build();
                                        mNotificationManager.notify(NOTIFY_ID, notification);
                                    }
                                }

                                @Override
                                public void onSuccess() {
                                    LogHelper.d("okhttp", "downloadFileCall onComplete");
                                    mNotificationManager.cancel(NOTIFY_ID);
                                    boolean b = fileTemp.renameTo(file);
                                    if (b) {
                                        UpdateManager.installApp(file, getApplicationContext());
                                    } else {
                                        UpdateManager.installApp(fileTemp, getApplicationContext());
                                    }
                                    App.sMainHanlder.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ServiceUpdate.this.stopSelf();
                                        }
                                    }, 1000);
                                    mIsDownLoading = false;
                                    return;
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    e.printStackTrace();
                                    mNotificationManager.cancel(NOTIFY_ID);
                                    mIsDownLoading = false;
                                    App.sMainHanlder.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastManager.showShort(getApplicationContext(), "下载失败");
                                            ServiceUpdate.this.stopSelf();
                                        }
                                    });
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    mNotificationManager.cancel(NOTIFY_ID);
                    mIsDownLoading = false;
                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastManager.showShort(getApplicationContext(), "下载失败 Exception");
                            ServiceUpdate.this.stopSelf();
                        }
                    });
                }
                worker.unsubscribe();
            }
        });
    }
}
