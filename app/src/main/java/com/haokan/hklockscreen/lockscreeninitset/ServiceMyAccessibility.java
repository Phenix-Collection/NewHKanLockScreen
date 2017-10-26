package com.haokan.hklockscreen.lockscreeninitset;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzixu on 2017/10/10.
 */
public class ServiceMyAccessibility extends AccessibilityService {
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

    public int mStepDuration = 500;
    /**
     * 通过这个函数可以接收系统发送来的AccessibilityEvent，接收来的AccessibilityEvent是经过过滤的，过滤是在配置工作时设置的。
     * @param event
     */
    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        LogHelper.d("wangzixu", "onAccessibilityEvent getEventType = " + event + ", CV_LockInitSetView.sIsAutoSet = " + CV_LockInitSetView.sIsAutoSet);
        if (CV_LockInitSetView.sIsAutoSet) {
            try {
                int eventType = event.getEventType();
                if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                    CharSequence className = event.getClassName();
                    final AccessibilityNodeInfo source = getRootInActiveWindow();

                    if ("com.miui.permcenter.autostart.AutoStartManagementActivity".equals(className)) {//小米自动启动管理界面
//                        List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByViewId("com.miui.securitycenter:id/list_view");//小米6自启动界面的listview
                        ArrayList<AccessibilityNodeInfo> list = new ArrayList<>();
                        findCanScrollNode(source, list);//小米6自启动界面的listview
                        LogHelper.d("wangzixu", "xiaomi6 listSize = " + list.size());
                        if (list != null && list.size() > 0) {
                            Message msg = Message.obtain();
                            msg.what = 11;
                            msg.obj = list.get(0);
                            LogHelper.d("wangzixu", "xiaomi6 list = " + list.get(0).isScrollable());
                            mHandler.sendMessageDelayed(msg, mStepDuration);
                        } else {
                            CV_LockInitSetView.sAutoSuccess = false;
                            Message msg = Message.obtain();
                            msg.what = 101;
                            mHandler.sendMessageDelayed(msg, mStepDuration);
                        }
                    } else if ("com.miui.permcenter.autostart.AutoStartDetailManagementActivity".equals(className)) {//小米自动启动管理详情界面, 点击了条目后会跳转一个新界面, 把里面的两个条目都选中
                        CV_LockInitSetView.sIsAutoSet = false;
                        Message msg = Message.obtain();
                        msg.what = 12;
                        msg.obj = source;
                        mHandler.sendMessageDelayed(msg, mStepDuration);
                    } else if ("com.coloros.safecenter.startupapp.StartupAppListActivity".equals(className)) { //oppo手机的自启动界面
                        CV_LockInitSetView.sIsAutoSet = false;
                        List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByViewId("android:id/list");//oppo自启动界面的listview
                        if (list != null && list.size() > 0) {
                            Message msg = Message.obtain();
                            msg.what = 1;
                            msg.obj = list.get(0);
                            mHandler.sendMessageDelayed(msg, mStepDuration);
                        } else {
                            CV_LockInitSetView.sAutoSuccess = false;
                            Message msg = Message.obtain();
                            msg.what = 101;
                            mHandler.sendMessageDelayed(msg, mStepDuration);
                        }
                    }
                }
            } catch (Exception e) {
                CV_LockInitSetView.sIsAutoSet = false;
                LogHelper.d("wangzixu", "onAccessibilityEvent error e = " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100: //node点击事件
                {
                    Object obj = msg.obj;
                    AccessibilityNodeInfo source = (AccessibilityNodeInfo) obj;
                    source.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    LogHelper.d("wangzixu", "handleMessage 100 点击了条目");
                    break;
                }
                case 101: //全局后退事件
                {
                    performGlobalAction(GLOBAL_ACTION_BACK);
                    LogHelper.d("wangzixu", "handleMessage 101 后退");
                    break;
                }
                case 1: //oppo R9s 设置自动开机启动
                    autoStart_oppoR9s(msg);
                    break;
                case 11: //小米6开机启动列表页, 找到好看锁屏并点击
                    autoStart_xiaomi6_1(msg);
                    break;
                case 12://小米6开机启详情页
                    autoStart_xiaomi6_2(msg);
                    break;
                default:
                    break;
            }
        }
    };

    public void autoStart_oppoR9s(Message msg) {
        Object obj = msg.obj;
        AccessibilityNodeInfo source = (AccessibilityNodeInfo) obj;
        List<AccessibilityNodeInfo> nodes = source.findAccessibilityNodeInfosByText(getApplicationContext().getResources().getString(R.string.app_name));
        if (nodes != null && nodes.size() > 0) {
            //找到了好看锁屏这个节点
            AccessibilityNodeInfo hkNode = nodes.get(0);
            LogHelper.d("wangzixu", "oppoauto handleMessage 1 找到了 hkNode");
            AccessibilityNodeInfo parent = findClickableParentNode(hkNode, 1);
            if (parent != null) {
                LogHelper.d("wangzixu", "oppoauto handleMessage 1 找到了 hkNode的条目");
                List<AccessibilityNodeInfo> list = parent.findAccessibilityNodeInfosByViewId("android:id/switchWidget");
                if (list != null && list.size() > 0) {
                    LogHelper.d("wangzixu", "oppoauto handleMessage 1 找到了 hkNode的条目 的开关");
                    AccessibilityNodeInfo info = list.get(0);
                    CV_LockInitSetView.sAutoSuccess = true;
                    if (!info.isChecked()) {
                        Message message = Message.obtain();
                        message.what = 100; //点击
                        message.obj = parent;
                        mHandler.sendMessage(message);

                        Message messageBack = Message.obtain();
                        messageBack.what = 101; //后退
                        mHandler.sendMessageDelayed(messageBack, mStepDuration);
                    } else {
                        Message messageBack = Message.obtain();
                        messageBack.what = 101; //后退
                        mHandler.sendMessage(messageBack);
                    }
                } else {
                    CV_LockInitSetView.sAutoSuccess = false;
                    LogHelper.d("wangzixu", "oppoauto handleMessage 1 没找到条目中的开关");

                    Message messageBack = Message.obtain();
                    messageBack.what = 101; //后退
                    mHandler.sendMessageDelayed(messageBack, mStepDuration);
                }
            } else {
                CV_LockInitSetView.sAutoSuccess = false;
                LogHelper.d("wangzixu", "oppoauto handleMessage 1 没找到可以点击的条目");

                Message messageBack = Message.obtain();
                messageBack.what = 101; //后退
                mHandler.sendMessageDelayed(messageBack, mStepDuration);
            }
        } else {
            boolean b = source.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD); //前滚一屏幕
            if (b) {
                LogHelper.d("wangzixu", "oppoauto handleMessage 1 前滚一屏幕, 再寻找");
                Message message = Message.obtain();
                message.what = 1;
                message.obj = source;
                mHandler.sendMessageDelayed(message, mStepDuration);
            } else {
                //滚动到底了, 还没找到目标节点
                LogHelper.d("wangzixu", "oppoauto handleMessage 1 滚动到底了, 还没找到目标节点 ");
                CV_LockInitSetView.sAutoSuccess = false;

                Message messageBack = Message.obtain();
                messageBack.what = 101; //后退
                mHandler.sendMessage(messageBack);
            }
        }
    }

    private void autoStart_xiaomi6_1(Message msg) {
        Object obj = msg.obj;
        AccessibilityNodeInfo source = (AccessibilityNodeInfo) obj;
        List<AccessibilityNodeInfo> nodes = source.findAccessibilityNodeInfosByText(getApplicationContext().getResources().getString(R.string.app_name));
        if (nodes != null && nodes.size() > 0) {
            //找到了好看锁屏这个节点
            AccessibilityNodeInfo hkNode = nodes.get(0);
            LogHelper.d("wangzixu", "xiaomi6 handleMessage 1 找到了 hkNode");
            AccessibilityNodeInfo parent = findClickableParentNode(hkNode, 1);
            if (parent != null) {
                LogHelper.d("wangzixu", "xiaomi6 handleMessage 1 找到了 hkNode的条目");
                List<AccessibilityNodeInfo> list = parent.findAccessibilityNodeInfosByViewId("com.miui.securitycenter:id/sliding_button");
                if (list != null && list.size() > 0) {
                    LogHelper.d("wangzixu", "xiaomi6 handleMessage 1 找到了 hkNode的条目 的开关");
                    AccessibilityNodeInfo info = list.get(0);
                    if (!info.isChecked()) {
                        Message message = Message.obtain();
                        message.what = 100; //点击
                        message.obj = parent;
                        mHandler.sendMessage(message);
                    } else {
                        CV_LockInitSetView.sAutoSuccess = true;
                        Message messageBack = Message.obtain();
                        messageBack.what = 101; //后退
                        mHandler.sendMessage(messageBack);
                    }
                } else {
                    CV_LockInitSetView.sAutoSuccess = false;
                    LogHelper.d("wangzixu", "xiaomi6 handleMessage 1 没找到条目中的开关");

                    Message messageBack = Message.obtain();
                    messageBack.what = 101; //后退
                    mHandler.sendMessageDelayed(messageBack, mStepDuration);
                }
            } else {
                CV_LockInitSetView.sAutoSuccess = false;
                LogHelper.d("wangzixu", "xiaomi6 handleMessage 1 没找到可以点击的条目");

                Message messageBack = Message.obtain();
                messageBack.what = 101; //后退
                mHandler.sendMessageDelayed(messageBack, mStepDuration);
            }
        } else {
            boolean b = source.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD); //前滚一屏幕
            if (b) {
                LogHelper.d("wangzixu", "xiaomi6 handleMessage 1 前滚一屏幕, 再寻找");
                Message message = Message.obtain();
                message.what = 11;
                message.obj = source;
                mHandler.sendMessageDelayed(message, mStepDuration);
            } else {
                //滚动到底了, 还没找到目标节点
                LogHelper.d("wangzixu", "xiaomi6 handleMessage 1 滚动到底了, 还没找到目标节点 ");
                CV_LockInitSetView.sAutoSuccess = false;

                Message messageBack = Message.obtain();
                messageBack.what = 101; //后退
                mHandler.sendMessage(messageBack);
            }
        }
    }

    private void autoStart_xiaomi6_2(Message msg) {
        Object obj = msg.obj;
        AccessibilityNodeInfo source = (AccessibilityNodeInfo) obj;
        List<AccessibilityNodeInfo> infos = source.findAccessibilityNodeInfosByViewId("com.miui.securitycenter:id/auto_start_sliding_button");
        if (infos != null && infos.size() > 0) {

            for (int i = 0; i < infos.size(); i++) {
                AccessibilityNodeInfo info = infos.get(i);
                if (!info.isChecked()) {
                    AccessibilityNodeInfo clickableNode = findClickableParentNode(info, 1);
                    if (clickableNode != null) {
                        LogHelper.d("wangzixu", "xiaomi6 handleMessage 12 自动启动详情页 条目点击");
                        clickableNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }

            CV_LockInitSetView.sAutoSuccess = true;
            Message messageBack = Message.obtain();
            messageBack.what = 101; //后退
            mHandler.sendMessageDelayed(messageBack, mStepDuration);

            Message messageBack2 = Message.obtain();
            messageBack2.what = 101; //后退
            mHandler.sendMessageDelayed(messageBack2, 900);
        } else {
            LogHelper.d("wangzixu", "xiaomi6 handleMessage 12 自动启动详情页 没有找到checkbox");
            CV_LockInitSetView.sAutoSuccess = false;

            Message messageBack = Message.obtain();
            messageBack.what = 101; //后退
            mHandler.sendMessageDelayed(messageBack, mStepDuration);
        }
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
     * 由当前节点向上查找, 寻找能点击的父节点
     * @param nodeInfo
     * @param count
     * @return
     */
    private AccessibilityNodeInfo findClickableParentNode(AccessibilityNodeInfo nodeInfo, int count) {
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
