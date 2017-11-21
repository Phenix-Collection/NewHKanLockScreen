package com.haokan.hklockscreen.lockscreeninitset.activityprompt;

import android.os.Bundle;
import android.view.View;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.util.CommonUtil;
import com.haokan.pubic.util.StatusBarUtil;

/**
 * oppo手机关闭锁屏杂志的提示
 */
public class ActivityPrompt_CloseSysPswd_1 extends ActivityBase implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_closesyspswd_oppo);
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
