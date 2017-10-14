package com.haokan.hklockscreen.activity;

import android.os.Bundle;
import android.view.View;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.util.CommonUtil;
import com.haokan.hklockscreen.util.StatusBarUtil;

public class ActivityPrompt_AutoStart extends ActivityBase implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_autostart);
        StatusBarUtil.setStatusBarTransparnet(this);
        initView();
    }

    private void initView() {
        findViewById(R.id.root).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        onBackPressed();
    }
}
