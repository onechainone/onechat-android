package oneapp.onechat.chat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.utils.jump.JumpAppPageUtil;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.RpcCallProxy;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.TimeUtils;

public class SplashActivity extends Activity {

    private String TAG = "SplashActivity";
//    ReturnAccountBroadcastReceiver returnAccountBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        WalletApplication.IF_GC_KILLED_APP = false;

        BtsHelper.mLastCheckUpdateTime = TimeUtils.getTrueTime();

        RpcCallProxy.getInstance().saveAppStatus(CommonConstants.APP_STATUS_RUNING);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
//        startPushService();

//        IntentFilter filter = new IntentFilter();
//        filter.addAction(CommonConstants.BCAST_RETURN_ACCOUNT_INFO_FROM_APP);
//        returnAccountBroadcastReceiver = new ReturnAccountBroadcastReceiver();
//        registerReceiver(returnAccountBroadcastReceiver, filter);

//        BtsHelper.requestAccountInfo();

        moveToMain();
//        Timer timer = new Timer();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                moveToMain();
//            }
//        };
//        timer.schedule(task, 500); //0.5秒后
    }


    public void startPushService() {
        try {
//            stopPushService();

            Intent mIntent = new Intent();
            mIntent.setAction(CommonConstants.SERVICE_BACKEND_WATCHDOG);//你定义的service的action
            mIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
            ComponentName comName = this.startService(mIntent);
            if (comName != null) {
                // 通知后台
                this.sendcastForAppStatus(CommonConstants.APP_STATUS_RUNING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 启动或者退出广播
    public void sendcastForAppStatus(int nAppStatus) {
        Intent intent = new Intent();
        intent.setAction(CommonConstants.BCAST_APPSTATUS_FROM_APP);
        intent.putExtra("appstatus", nAppStatus);

        this.sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (returnAccountBroadcastReceiver != null)
//            unregisterReceiver(returnAccountBroadcastReceiver);
    }

//    /**
//     * 新消息广播接收者
//     */
//    private class ReturnAccountBroadcastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            try {
//                moveToMain();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    void moveToMain() {
        if (!ServiceConstants.ifInitServices) {
            ServiceConstants.initPublicInfo();
        }
        ServiceConstants.ifInitServices = false;

        OneAccountHelper.setActionIntentData(getIntent().getDataString());
        JumpAppPageUtil.jumpMainPage(this);
        finish();
    }

}
