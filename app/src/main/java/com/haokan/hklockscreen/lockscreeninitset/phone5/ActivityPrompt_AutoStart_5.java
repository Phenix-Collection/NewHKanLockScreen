package com.haokan.hklockscreen.lockscreeninitset.phone5;

import android.os.Bundle;
import android.view.View;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.util.CommonUtil;
import com.haokan.pubic.util.StatusBarUtil;

public class ActivityPrompt_AutoStart_5 extends ActivityBase implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_autostart_5);
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
