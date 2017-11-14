package com.haokan.pubic.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.haokan.hklockscreen.R;

/**
 * Created by wangzixu on 2017/11/14.
 */
public class MyDialogUtil {
    public interface myDialogOnClickListener {
        void onClickCancel();

        /**
         * 如果有checkbox, 是否选中了, 如果没有, 请忽略
         * @param checked
         */
        void onClickConfirm(boolean checked);
    }

    public static void showMyDialog(Activity activity, String title, String desc
            , String cancelChar, String confirmChar
            ,final boolean hasCheckBox, final myDialogOnClickListener listener) {
        if (listener == null) {
            return;
        }

        if (TextUtils.isEmpty(cancelChar)) {
            cancelChar = activity.getResources().getString(R.string.cancel);
        }

        if (TextUtils.isEmpty(confirmChar)) {
            confirmChar = activity.getResources().getString(R.string.confirm);
        }

        View cv = LayoutInflater.from(activity).inflate(R.layout.dialog_layout_nowifi_switch, null);

        final CheckBox checkBox = (CheckBox) cv.findViewById(R.id.checkbox);
        if (hasCheckBox) {
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }

        TextView tvTitle = (TextView) cv.findViewById(R.id.tv_title);
        tvTitle.setText(desc);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(cv)
                .setNegativeButton(cancelChar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onClickCancel();
                    }
                }).setPositiveButton(confirmChar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (hasCheckBox) {
                            listener.onClickConfirm(checkBox.isChecked());
                        } else {
                            listener.onClickConfirm(false);
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
