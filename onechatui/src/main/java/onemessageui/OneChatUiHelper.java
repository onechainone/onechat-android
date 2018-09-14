package onemessageui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.umeng.analytics.MobclickAgent;

import oneapp.onechat.oneandroid.chatsdk.ConfigConstants;
import oneapp.onechat.oneandroid.onemessage.common.CommonHelperUtils;
import onewalletui.util.jump.JumpAppPageUtil;

/**
 * Created by 何帅 on 2018/9/3.
 */

public class OneChatUiHelper {
    private static Context mContext;

    public static void initOneChatUi(Context context) {
        mContext = context;
        initUmeng();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonHelperUtils.getCheckSignBroadcastAction());
        CheckSignBroadcastReceiver checkSignBroadcastReceiver = new CheckSignBroadcastReceiver();
        mContext.registerReceiver(checkSignBroadcastReceiver, filter);

    }

    /**
     * 初始化友盟
     */
    private static void initUmeng() {
        /** 设置是否对日志信息进行加密, 默认false(不加密). */
        MobclickAgent.enableEncrypt(!ConfigConstants.DEBUG);//6.0.0版本及以后
    }
    /**
     * 验证签名广播接收者
     */
    private static class CheckSignBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            JumpAppPageUtil.jumpCheckSignPage(context);
        }
    }
}
