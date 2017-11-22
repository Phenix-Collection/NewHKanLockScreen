package com.haokan.pubic.webview;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.CommonUtil;
import com.haokan.pubic.util.ToastManager;


public class ActivityWebview extends ActivityBase implements View.OnClickListener {
    public static final String KEY_INTENT_WEB_URL = "url";
    public static final String KEY_INTENT_WEB_TITLE = "title";
    private boolean mHasFixedTitle;
    private TextView mTvTitle;
    private String mTitleText = "";
    private ProgressBar mProgressHorizontal;
    private WebView mWebView;

    //分享用到的内容
    private String mWeb_Url;
    private Handler mHandler = new Handler();
    private View mTvClose;
    private ViewGroup mBigViedioParent;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
//        StatusBarUtil.setStatusBarWhiteBg_BlackText(this);
        assignViews();
        loadData();

        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
                overridePendingTransition(0,0);
            }
        };
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadData();
    }

    private void assignViews() {
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

        mTvClose = findViewById(R.id.close);
        mTvClose.setOnClickListener(this);

        mTvTitle = (TextView) findViewById(R.id.title);
        String title = getIntent().getStringExtra(KEY_INTENT_WEB_TITLE);
        if (!TextUtils.isEmpty(title)) {
            mTvTitle.setText(title);
            mHasFixedTitle = true;
        }

        mProgressHorizontal = (ProgressBar) findViewById(R.id.progress_horizontal);
        mWebView = (WebView) findViewById(R.id.webView);
        mBigViedioParent = (ViewGroup) findViewById(R.id.bigvideoview);
        initWebView();
    }

    /**
     * 如果给的链接不是http或者https，默认认为是打开本地应用的activity
     */
    private void loadLocalApp() {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri content_url = Uri.parse(mWeb_Url);
            intent.setData(content_url);
            startActivity(intent);
        }catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        if (getIntent().getData() != null) {
            Uri uri = getIntent().getData();
            String url = uri.getQueryParameter(KEY_INTENT_WEB_URL);
            mWeb_Url = url;
//            try {
//                mWeb_Url = "http://m.levect.com/appcpudetail.html?url=" + URLEncoder.encode(url, "UTF-8");
//            } catch (Exception e) {
//                e.printStackTrace();
//                mWeb_Url = url;
//            }
        } else {
            mWeb_Url = getIntent().getStringExtra(KEY_INTENT_WEB_URL);
        }
        if (TextUtils.isEmpty(mWeb_Url)) {
            ToastManager.showShort(this, "地址错误");
            finish();
            return;
        }

        LogHelper.i("WebViewActivity", "loadData mweburl = " + mWeb_Url);

        if (mWeb_Url.startsWith("www")) {
            mWeb_Url = "http://" + mWeb_Url;
        }
        if (mWeb_Url.startsWith("http") || mWeb_Url.startsWith("https")) {
            mProgressHorizontal.setVisibility(View.VISIBLE);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl(mWeb_Url);
                }
            }, 200);
        } else {
            loadLocalApp();
        }
    }

    private void initWebView() {
        mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAppCacheEnabled(true);
//        settings.setAppCachePath(CacheManager.getWebViewAppCacheDir(getApplicationContext()).getAbsolutePath());
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCacheMaxSize(1024 * 1024 * 100);
        settings.setAllowFileAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setDatabaseEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setGeolocationEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        //设置接受第三方的cooke, 很重要, 必须设置才能正确接受cookie
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebView.setDownloadListener(new DownloadListener() {//实现文件下载功能
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            //点击链接在此webView打开
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogHelper.i("WebViewActivity", "shouldOverrideUrlLoading mweburl = " + url);
                mWeb_Url = url;
                if (url.startsWith("http") || url.startsWith("https")) {
                    return false;
                } else {
                    loadLocalApp();
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogHelper.i("WebViewActivity", "onPageStarted mweburl = " + url);
//                showLoadingLayout();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mHasFixedTitle) {

                } else {
                    String title = mWebView.getTitle();
                    LogHelper.i("WebViewActivity", "onPageFinished mweburl = " + url + ", title = " + title);
                    mTvTitle.setText(title);
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//                if (url.contains("pos.baidu.com")) {
//                    return new WebResourceResponse(null, null, null);
//                }
//                LogHelper.i("WebViewActivity", "shouldInterceptRequest mweburl = " + url);
                return super.shouldInterceptRequest(view, url);
            }

            //可以加载https
            @Override
            public void onReceivedSslError(WebView view, @NonNull SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 0 && newProgress < 90) {
                    mProgressHorizontal.setVisibility(View.VISIBLE);
                    mProgressHorizontal.setProgress(newProgress);
                } else {
                    mProgressHorizontal.setVisibility(View.GONE);
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }

            //*******全屏播放视频设置相关begin*********
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
//                super.onShowCustomView(view, callback);
//                LogHelper.d("vedio", "onShowCustomView view = " + view + ", callback = " + callback);
                if (mBigVidioView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                mCustomViewCallback = callback;
                mBigVidioView = view;
                mBigViedioParent.setVisibility(View.VISIBLE);
                mBigViedioParent.addView(view);
                mWebView.setVisibility(View.GONE);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onHideCustomView() {
//                LogHelper.d("vedio", "onHideCustomView");
                mWebView.setVisibility(View.VISIBLE);
                if (mCustomViewCallback != null) {
                    mCustomViewCallback.onCustomViewHidden();
                    mCustomViewCallback = null;
                }
                if (mBigVidioView != null) {
                    mBigViedioParent.removeView(mBigVidioView);
                    mBigVidioView = null;
                }
                mBigViedioParent.setVisibility(View.GONE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });
    }
    private View mBigVidioView = null;
    private WebChromeClient.CustomViewCallback mCustomViewCallback = null;
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }
    //*******全屏播放视频设置相关end*********

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.close:
                finish();
                closeActivityAnim();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
            closeActivityAnim();
        }
    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        if(mWebView!=null){
            mWebView.destroy();
            mWebView.removeAllViews();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
