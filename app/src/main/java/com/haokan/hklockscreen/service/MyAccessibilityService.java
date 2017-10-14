package com.haokan.hklockscreen.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.haokan.hklockscreen.App;
import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.activity.ActivitySetLockScreen;
import com.haokan.hklockscreen.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzixu on 2017/10/10.
 */
public class MyAccessibilityService extends AccessibilityService {
    /**
     * 系统会在成功连接上你的服务的时候调用这个方法，在这个方法里你可以做一下初始化工作，例如设备的声音震动管理，也可以调用setServiceInfo()进行配置工作。
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    /**
     * 在系统将要关闭这个AccessibilityService会被调用。在这个方法中进行一些释放资源的工作。
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * 这个在系统想要中断AccessibilityService返给的响应时会调用。在整个生命周期里会被调用多次。
     */
    @Override
    public void onInterrupt() {

    }

    /**
     * 通过这个函数可以接收系统发送来的AccessibilityEvent，接收来的AccessibilityEvent是经过过滤的，过滤是在配置工作时设置的。
     * @param event
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (ActivitySetLockScreen.sIsAutoSet) {
            try {
                int eventType = event.getEventType();
                if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//                    LogHelper.d("wangzixu", "onAccessibilityEvent getEventType = " + event);
                    CharSequence className = event.getClassName();

                    final AccessibilityNodeInfo source = event.getSource();
                    if ("com.miui.permcenter.autostart.AutoStartManagementActivity".equals(className)) {//小米自动启动管理界面
                        App.sMainHanlder.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                autoStart_xiaomi(source, getApplicationContext().getResources().getString(R.string.app_name));
                            }
                        }, 500);
                    } else if ("com.miui.permcenter.autostart.AutoStartDetailManagementActivity".equals(className)) {//小米自动启动管理详情界面, 把里面的两个条目都选中
                        App.sMainHanlder.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                List<AccessibilityNodeInfo> infos = source.findAccessibilityNodeInfosByViewId("com.miui.securitycenter:id/auto_start_sliding_button");
                                for (int i = 0; i < infos.size(); i++) {
                                    AccessibilityNodeInfo info = infos.get(i);
                                    if (!info.isChecked()) {
                                        AccessibilityNodeInfo clickableNode = info.getParent();
                                        if (clickableNode != null) {
                                            LogHelper.d("wangzixu", "onAccessibilityEvent 自动启动详情页 条目点击 i = " + i + " , clickableNode clickable = " + clickableNode.isClickable());
                                            clickableNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                        }
//                                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    }
                                }
                                ActivitySetLockScreen.sIsAutoSet = false;
                            }
                        }, 500);


                        App.sMainHanlder.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                performGlobalAction(GLOBAL_ACTION_BACK);
                            }
                        }, 1000);

                        App.sMainHanlder.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                performGlobalAction(GLOBAL_ACTION_BACK);
                            }
                        }, 1500);
                    }
                }
            } catch (Exception e) {
                ActivitySetLockScreen.sIsAutoSet = false;
                LogHelper.d("wangzixu", "onAccessibilityEvent error e = " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    //****小米miui begin******
    /**
     * 小米手机设置自动启动
     */
    private boolean autoStart_xiaomi(AccessibilityNodeInfo source, String target) {
        //1, 寻找到目标节点
        AccessibilityNodeInfo nodeInfo = findTargetStr(source, target);

//        //2, 找到目标节点所在的条目
        final AccessibilityNodeInfo clickableNode = findClickableParentNode(nodeInfo, 1);
//
//        //3, 点击这个条目
        App.sMainHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                clickableNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }, 300);

//        //2根据目标节点寻找到其相应的chekbox
//        final AccessibilityNodeInfo checkBoxNode = findCheckBoxNode(nodeInfo, 1);
//        //3改变checkbox的选中状态
//        if (checkBoxNode != null) {
//            LogHelper.d("wangzixu", "onAccessibilityEvent 找到了目标的checkbox checkBoxNode checkBoxNode.isChecked() = " + checkBoxNode.isChecked());
//            if (checkBoxNode.isChecked()) {
////                checkBoxNode.setChecked(true);
////                checkBoxNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
////                checkBoxNode.refresh();
//            } else {
////                Class<? extends AccessibilityNodeInfo> aClass = checkBoxNode.getClass();
////                try {
////                    Field mSealed = aClass.getDeclaredField("mSealed");
////                    mSealed.setAccessible(true);
////                    mSealed.set(checkBoxNode, false);
////                } catch (Exception e) {
////                    LogHelper.d("wangzixu", "onAccessibilityEvent mSealed NoSuchFieldException e = " + e.getMessage());
////                    e.printStackTrace();
////                }
//
////                checkBoxNode.setChecked(true);
//                checkBoxNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
////                checkBoxNode.refresh();
////                checkBoxNode.recycle();
//            }
//            LogHelper.d("wangzixu", "onAccessibilityEvent 找到了目标的checkbox checkBoxNode --- checkBoxNode.isChecked() = " + checkBoxNode.isChecked());
//            return true;
//        }
        return false;
    }

    /**
     * 寻找目标节点
     * @param source
     * @param target
     * @return
     */
    private AccessibilityNodeInfo findTargetStr(AccessibilityNodeInfo source, String target) {
        if (source == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = source.findAccessibilityNodeInfosByText(target);
        if (nodes != null && nodes.size() > 0) {
            LogHelper.d("wangzixu", "onAccessibilityEvent 找到了 " + target);
            return nodes.get(0);
        }

        if (source.isScrollable()) {
            boolean b = source.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD); //前滚一屏幕
            if (b) {
                return findTargetStr(source, target);
            } else {
                return null;
            }
        } else {
            ArrayList<AccessibilityNodeInfo> list = new ArrayList<>();
            findCanScrollNode(source, list);

            for (int i = 0; i < list.size(); i++) {
                AccessibilityNodeInfo canScrollNode = list.get(i);
                AccessibilityNodeInfo nodeInfo = findTargetStr(canScrollNode, target);
                if (nodeInfo != null) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 寻找可以滚动的节点
     * @return
     */
    private void findCanScrollNode(AccessibilityNodeInfo source, ArrayList<AccessibilityNodeInfo> list) {
        //miui对应的listview: com.miui.securitycenter:id/list_view
        if (source == null) {
            return;
        }
        int childCount = source.getChildCount();
        LogHelper.d("wangzixu", "onAccessibilityEvent findCanScrollNode childCount = " + childCount + ", source = " + source.getClassName());
        if (childCount <= 0) {
            return;
        }
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = source.getChild(i);
            if (child != null) { //有时候寻找的child会是null
                if (child.isScrollable()) {
                    list.add(child);
                } else {
                    findCanScrollNode(child, list);
                }
            }
        }
    }

    /**
     * 递归寻找checkbox, 为防止整个界面没有checkbox而死循环, 添加最大递归层限制
     * @param nodeInfo
     * @return
     */
    private AccessibilityNodeInfo findCheckBoxNode(AccessibilityNodeInfo nodeInfo, int count) {
        LogHelper.d("wangzixu", "onAccessibilityEvent findCheckBoxNode nodeInfo = " + nodeInfo + ", count = " + count);
        if (nodeInfo == null || count > 5) {
            return null;
        }
        int childCount = nodeInfo.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
//            if (child.getClassName().toString().contains("CheckBox")) {
//                return child;
//            }
            if (child.isCheckable()) {
                return child;
            }
        }
        return findCheckBoxNode(nodeInfo.getParent(), count+1);
    }

    /**
     * 由当前节点向上查找, 寻找能点击的节点
     * @param nodeInfo
     * @param count
     * @return
     */
    private AccessibilityNodeInfo findCheckableNode(AccessibilityNodeInfo nodeInfo, int count) {
        LogHelper.d("wangzixu", "onAccessibilityEvent findCheckBoxNode nodeInfo = " + nodeInfo + ", count = " + count);
        if (nodeInfo == null || count > 5) {
            return null;
        }
        if (nodeInfo.isCheckable()) {
            return nodeInfo;
        }
        int childCount = nodeInfo.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            if (child.isCheckable()) {
                return child;
            }
        }
        return findCheckableNode(nodeInfo.getParent(), count+1);
    }

    /**
     * 由当前节点向上查找, 寻找能点击的父节点
     * @param nodeInfo
     * @param count
     * @return
     */
    private AccessibilityNodeInfo findClickableParentNode(AccessibilityNodeInfo nodeInfo, int count) {
        LogHelper.d("wangzixu", "onAccessibilityEvent findClickableParentNode nodeInfo = " + nodeInfo + ", count = " + count);
        if (nodeInfo == null || count > 5) {
            return null;
        }
        if (nodeInfo.isClickable()) {
            return nodeInfo;
        }
        return findClickableParentNode(nodeInfo.getParent(), count+1);
    }
    //****小米miui end********
}
