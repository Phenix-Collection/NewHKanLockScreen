package com.haokan.pubic.checkupdate;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.util.LogHelper;
import com.haokan.pubic.util.ToastManager;

import java.io.File;

/**
 * Created by wangzixu on 2017/10/21.
 */
public class UpdateManager {
    public static void checkUpdate(final ActivityBase context, final boolean isAutoCheck) {
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) { //没有这个权限
            if (!isAutoCheck) {
                ToastManager.showCenter(context, "当前没有存储权限, 无法下载");
            }
            return;
        }

        new ModelCheckUpdata().checkUpdate(context, new onDataResponseListener<BeanUpdate>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(BeanUpdate updateBean) {
                int ver_code = updateBean.getVersonCode();
                int localVersionCode = App.APP_VERSION_CODE;
                LogHelper.d("wangzixu", "checkUpdata onDataSucess localVersionCode= " + localVersionCode + ", remotecode = " + ver_code);
                if (ver_code > localVersionCode) {
                    showUpdateDialog(context, updateBean);
                } else {
                    if (!isAutoCheck) {
                        ToastManager.showCenter(context, "当前已是最新版本");
                    }
                }
            }

            @Override
            public void onDataEmpty() {
                LogHelper.d("wangzixu", "checkUpdata onDataEmpty");
            }

            @Override
            public void onDataFailed(String errmsg) {
                LogHelper.d("wangzixu", "checkUpdata onDataFailed errmsg = " + errmsg);
            }

            @Override
            public void onNetError() {
                LogHelper.d("wangzixu", "checkUpdata onNetError");
            }
        });
    }

    /**
     * 显示自定义的对话框
     */
    private static void showUpdateDialog(final Context context, final BeanUpdate updateResponseBean) {
        if (updateResponseBean == null) {
            return;
        }
        View cv = LayoutInflater.from(context).inflate(R.layout.dialog_layout_update, null);
        TextView desc = (TextView) cv.findViewById(R.id.tv_desc);
        desc.setText(updateResponseBean.getAppDesc());
        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("新版更新")
                .setView(cv)
                .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, ServiceUpdate.class);
                        intent.putExtra(ServiceUpdate.DOWNLOAD_INFO, updateResponseBean);
                        context.startService(intent);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public static void installApp(File file, Context context) {
        try {
            String command = "chmod 777" + file.getAbsolutePath();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                Uri contentUri = FileProvider.getUriForFile(context, "com.haokanhaokan.lockscreen.fileProvider", file);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }

            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
