package oneapp.onechat.chat.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import javax.annotation.Nullable;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.MainActivity;
import oneapp.onechat.chat.WalletApplication;
import oneapp.onechat.oneandroid.chatsdk.ConfigConstants;
import oneapp.onechat.oneandroid.chatsdk.OneOpenHelper;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.RpcCallProxy;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.EasyUtils;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.NetUtils;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.util.Keyboard;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.SystemLanguageUtils;
import oneapp.onechat.oneandroid.onewallet.util.TimeUtils;
import oneapp.onechat.oneandroid.onewallet.util.permission.EasyPermissions;
import onemessageui.dialog.FlippingLoadingDialog;
import onemessageui.utils.CommonUtils;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;


/**
 * @author John L. Jegutanis
 */
abstract public class BaseActivity extends onewalletui.ui.BaseActivity implements EasyPermissions.PermissionCallbacks {
//
//    protected BaseActivity context;
//    private static final int notifyId = 11;
//    protected NotificationManager notificationManager;
//    protected FlippingLoadingDialog mLoadingDialog;
//
//    protected ProgressDialog mProgressLoadingDialog;
//    /**
//     * 部分界面从后台返回时不需要锁APP(如拍照)
//     */
//    protected boolean ifUnlockThisActivity = true;
//
//    /**
//     * 是否打开右滑返回
//     */
//    protected boolean ifOpenRightSlideBack = true;
//
//    /**
//     * 部分界面禁止截屏
//     */
//    protected boolean ifCanScreenShot = true;
//    /**
//     * 是否注册全局监听
//     */
//    protected boolean ifRegisterAllReceiver = false;
//
//
//    protected int mSystemStyleTypePos = 0;
//    /**
//     * 沉浸式状态栏
//     */
//    protected ImmersionBar mImmersionBar;
//    protected boolean ifConfigImmersionBar = false;
//    //状态栏是否深色主题
//    protected boolean IF_BAR_DARK_FONT = ConfigConstants.DEFAULT_BAR_DARK;
//    //导航栏是否深色主题
//    protected boolean IF_NAVIGATION_BAR_DARK_FONT = ConfigConstants.DEFAULT_NAVIGATION_BAR_DARK;
//
//    protected boolean isFront = false;
//    AllActivityReceiver allActivityReceiver;
//
//    protected boolean isInitBarOnCreate = false;
//
//    private boolean isCurrentRunningForeground = true;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        context = this;
////        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        // FIXME: 2017/11/21 hs
////        if (WalletApplication.IF_GC_KILLED_APP) {
////            //程序被gc回收重新加载APP,否则界面异常
////            finish();
////            Intent intent = new Intent(this, SplashActivity.class);
////            startActivity(intent);
//////            JumpAppPageUtil.jumpMainPage(this);
////        }
//        if (isInitBarOnCreate) {
//            initImmersionBar();
//        }
//        try {
//            if (ifRegisterAllReceiver) {
//                IntentFilter filter = new IntentFilter();
//                filter.addAction(CommonConstants.ALL_ACTIVITY_BROADCAST);
//                filter.setPriority(3);
//                allActivityReceiver = new AllActivityReceiver();
//                registerReceiver(allActivityReceiver, filter);
//            }
//        } catch (Exception e) {
//
//        }
//
//
//        super.onCreate(savedInstanceState);
//
//
////        mImmersionBar.navigationBarAlpha(0.9f);
//
////        MessageApp.getInstance2().addActivity(this);
//        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        getSwipeBackLayout().setEnableGesture(ifOpenRightSlideBack);//是否打开右滑返回上一级
//
//        if (!ifCanScreenShot) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
//        }
//
//        checkNetWork();
//    }
//
//    protected boolean canShowResetServiceDialog = false;
//
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(SystemLanguageUtils.attachBaseContext(newBase));
//    }
//
//    /**
//     * 广播接收者
//     */
//    private class AllActivityReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            try {
//                int type = intent.getIntExtra(Constants.ARG_TYPE, -1);
//                if (type == CommonConstants.BROADCAST_TYPE_RESET_SERVICE) {
////                    if (canShowResetServiceDialog) {
////                        canShowResetServiceDialog = false;
////                        if (!NetUtils.hasNetwork(getApplicationContext())) {
////                            ToastUtils.simpleToast(R.string.string_network_disconnect);
////                        } else {
////                            DialogUtil.simpleDialog(BaseActivity.this, getString(R.string.service_error_tip_switch_node), new DialogUtil.ConfirmCallBackInf() {
////                                @Override
////                                public void onConfirmClick(String content) {
////                                    JumpAppPageUtil.jumpSetServiceNodePage(BaseActivity.this);
////                                }
////                            });
////                        }
////                    }
//                } else if (type == CommonConstants.BROADCAST_TYPE_DIALOG_PUSH) {
//                    boolean ifNotifyPush = true;
//                    if (SharePreferenceUtils.contains(SharePreferenceUtils.SP_NEW_PUSH_NOTIFY)) {
//                        ifNotifyPush = (boolean) SharePreferenceUtils.getObject(SharePreferenceUtils.SP_NEW_PUSH_NOTIFY);
//                    }
//                    if (ifNotifyPush) {
//                        String pushId = intent.getStringExtra(JumpParamsContants.INTENT_PUSH_ID);
////                        if (pushId != null && BtsHelper.mAppStatus == CommonConstants.APP_STATUS_RUNING && isFront) {
////                            PushMsgBean pushMsgBean = BtsHelper.getDatabase().getPushMsgById(pushId);
////                            PushWindowUtils.pushDialog(context, pushMsgBean);
////                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
    public void exitApp() {
        Intent intent = new Intent(this, MainActivity.class);
//传递退出所有Activity的Tag对应的布尔值为true
        intent.putExtra(JumpParamsContants.INTENT_EXIST_APP, true);
//启动BaseActivity
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        WalletApplication.getInstance().setCurrentActivity(this);
//        isFront = true;
//        canShowResetServiceDialog = isFront;
//        //友盟统计
//        MobclickAgent.onResume(this);
//        MobclickAgent.onPageStart(getClass().getSimpleName());
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
//            mLoadingDialog.dismiss();
//        }
//        isFront = false;
//        canShowResetServiceDialog = isFront;
//        //友盟统计
//        MobclickAgent.onPause(this);
//        MobclickAgent.onPageEnd(getClass().getSimpleName());
//    }
//
//    @Override
//    public void finish() {
//        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
//            mLoadingDialog.dismiss();
//        }
//        Keyboard.hideKeyboard(context);
//        super.finish();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (!isCurrentRunningForeground) {
////            >>>>>>>>>>>>>>>>>>>切到前台 activity process
//            RpcCallProxy.getInstance().saveAppStatus(CommonConstants.APP_STATUS_RUNING);
//            long foregroundAppTime = TimeUtils.getTrueTime() - BtsHelper.mLastForegroundAppTime;
//            if (BtsHelper.mIsHasAccount && ifUnlockThisActivity && foregroundAppTime > Constants.NEED_INPUT_HAND_PSW_TIME) {
////                JumpAppPageUtil.jumpUnlockPage(this);
//            }
//            OneOpenHelper.GetTrueTimeRequest();//获取真实时间
//
//        }
//
//        if (BtsHelper.mLastCheckUpdateTime > 0 && TimeUtils.getTrueTime() - BtsHelper.mLastCheckUpdateTime > Constants.NEED_CHECK_UPDATE_TIME) {
//            //检查更新
//            oneapp.onechat.chat.network.RequestUtils.UpgradeCheckRequest(this, false);
//        }
//
//        BtsHelper.mLastForegroundAppTime = TimeUtils.getTrueTime();
//
//        if (!ifConfigImmersionBar) {
//            if (mImmersionBar == null) {
//                initImmersionBar();
//            }
//            try {
//                mImmersionBar.titleBar(R.id.layout_title_view);
//            } catch (Exception e) {
//            }
//            ifConfigImmersionBar = true;
//            mImmersionBar.init();   //所有子类都将继承这些相同的属性
//        }
//
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        isCurrentRunningForeground = isRunningForeground();
//        if (!isCurrentRunningForeground) {
////            >>>>>>>>>>>>>>>>>>>切到后台 activity process
//            BtsHelper.mLastForegroundAppTime = TimeUtils.getTrueTime();
//
//            RpcCallProxy.getInstance().saveAppStatus(CommonConstants.APP_STATUS_STOPED);
//
////            PushWindowUtils.hidePopupWindow();
//
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            if (ifRegisterAllReceiver) {
//                unregisterReceiver(allActivityReceiver);
//            }
//        } catch (Exception e) {
//
//        }
//        if (mImmersionBar != null)
//            mImmersionBar.destroy();  //必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
//    }
//
//    public boolean isRunningForeground() {
//        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = null;
//        if (activityManager != null) {
//            appProcessInfos = activityManager.getRunningAppProcesses();
//        }
//        // 枚举进程
//        if (appProcessInfos != null) {
//            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
//                if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    if (appProcessInfo.processName.equals(this.getApplicationInfo().processName)) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下 如果不需要，注释掉即可
//     *
//     * @param message
//     */
//    protected void notifyNewMessage(ItemMessage message) {
//        // 如果是设置了不提醒只显示数目的群组(这个是app里保存这个数据的，demo里不做判断)
//        // 以及设置了setShowNotificationInbackgroup:false(设为false后，后台时sdk也发送广播)
//        if (!EasyUtils.isAppRunningForeground(this)) {
//            return;
//        }
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
//                this).setSmallIcon(getApplicationInfo().icon)
//                .setWhen(TimeUtils.getTrueTime()).setAutoCancel(true);
//
//        String ticker = CommonUtils.getMessageDigest(message, this);
//        String st = getResources().getString(R.string.expression);
//        if (message.getType() == ItemMessage.Type.TXT)
//            ticker = ticker.replaceAll("\\[.{2,3}\\]", st);
//        // 设置状态栏提示
//        mBuilder.setTicker(message.getFrom() + ": " + ticker);
//
//        // 必须设置pendingintent，否则在2.3的机器上会有bug
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, notifyId,
//                intent, PendingIntent.FLAG_ONE_SHOT);
//        mBuilder.setContentIntent(pendingIntent);
//
//        Notification notification = mBuilder.build();
//        notificationManager.notify(notifyId, notification);
//        notificationManager.cancel(notifyId);
//    }
//
//    /**
//     * 返回
//     *
//     * @param view
//     */
//    public void back(View view) {
//        finish();
//    }
//
//    public void showLoadingDialog(String msg) {
////        showDialog("", msg);
//        if (mLoadingDialog != null) {
//            if (!mLoadingDialog.isShowing()) {
//                mLoadingDialog.show();
//            } else {
//                mLoadingDialog.setLoadingStatusText(msg);
//            }
//        } else {
//            mLoadingDialog = new FlippingLoadingDialog(this, msg);
//
//            mLoadingDialog.setLoadingStatusText(msg);
//            mLoadingDialog.setCancelable(true);
//            mLoadingDialog.show();
//        }
//    }
//
//    public void showLoadingDialog() {
//        showLoadingDialog("");
//    }
//
//    public void hideLoadingDialog() {
//        try {
//            if (mLoadingDialog == null)
//                return;
//            mLoadingDialog.dismiss();
//        } catch (Exception e) {
//        }
////        hideDialog();
//    }
//
//    protected void showDialog(String title, String msg) {
//        try {
////            if (StringUtils.equalsNull(msg)) {
////                msg = getString(R.string.loading);
////            }
//            if (mProgressLoadingDialog == null) {
//                mProgressLoadingDialog = new ProgressDialog(this);
//                final LayoutInflater inflater = LayoutInflater.from(this);
//                final View view = inflater.inflate(R.layout.dialog_loading, null);
//                mProgressLoadingDialog.setView(view);
//            }
//
//            if (!mProgressLoadingDialog.isShowing()) {
//                mProgressLoadingDialog.setTitle(title);
//                mProgressLoadingDialog.setMessage(msg);
//                mProgressLoadingDialog.show();
//            } else {
//                mProgressLoadingDialog.setTitle(title);
//                mProgressLoadingDialog.setMessage(msg);
//            }
//
//        } catch (Exception e) {
//
//        }
//    }
//
//    protected void hideDialog() {
//
//        if (mProgressLoadingDialog != null) {
//            if (mProgressLoadingDialog.isShowing()) {
//                mProgressLoadingDialog.cancel();
//            }
//        }
//
//    }
//
//    /**
//     * 关闭 Activity
//     *
//     * @param activity
//     */
//    public void finish(Activity activity) {
//        activity.finish();
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            finish();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//
//
//    public FragmentManager getFM() {
//        return getSupportFragmentManager();
//    }
//
//    public void replaceFragment(Fragment fragment, int container, @Nullable String tag) {
//        FragmentTransaction transaction = getFM().beginTransaction();
//
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        // Replace whatever is in the fragment_container view with this fragment,
//        // and add the transaction to the back stack so the user can navigate back
//        transaction.replace(container, fragment, tag);
//        transaction.addToBackStack(null);
//
//        // Commit the transaction
//        transaction.commit();
//    }
//
//    protected static final int RC_PERM = 123;
//
//    protected static int reSting = R.string.ask_again;//默认提示语句
//    /**
//     * 权限回调接口
//     */
//    private CheckPermListener mListener;
//
//    public interface CheckPermListener {
//        //权限通过后的回调方法
//        void superPermission();
//    }
//
//    public void checkPermission(CheckPermListener listener, int resString, String... mPerms) {
//        mListener = listener;
//        if (EasyPermissions.hasPermissions(this, mPerms)) {
//            if (mListener != null)
//                mListener.superPermission();
//        } else {
//            EasyPermissions.requestPermissions(this, String.format(getString(R.string.perm_tip_format), getString(resString)),
//                    RC_PERM, mPerms);
//        }
//    }
//
//    private String permTipString;
//
//    public void checkPermission(CheckPermListener listener, String tipString, String... mPerms) {
//        mListener = listener;
//        permTipString = tipString;
//        if (EasyPermissions.hasPermissions(this, mPerms)) {
//            if (mListener != null)
//                mListener.superPermission();
//        } else {
//            EasyPermissions.requestPermissions(this, tipString,
//                    RC_PERM, mPerms);
//        }
//    }
//
//    /**
//     * 用户权限处理,
//     * 如果全部获取, 则直接过.
//     * 如果权限缺失, 则提示Dialog.
//     *
//     * @param requestCode  请求码
//     * @param permissions  权限
//     * @param grantResults 结果
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        EasyPermissions.onRequestPermissionsResult(requestCode, permTipString, permissions, grantResults, this);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == EasyPermissions.SETTINGS_REQ_CODE) {
//            //设置返回
//        }
//    }
//
//
//    @Override
//    public void onPermissionsGranted(int requestCode, List<String> perms) {
//        //同意了某些权限可能不是全部
//    }
//
//    @Override
//    public void onPermissionsAllGranted() {
//        if (mListener != null)
//            mListener.superPermission();//同意了全部权限的回调
//    }
//
//    @Override
//    public void onPermissionsDenied(int requestCode, String tipString, List<String> perms) {
//
//        if (StringUtils.equalsNull(tipString)) {
//            tipString = getString(R.string.perm_tip);
//        }
//        EasyPermissions.checkDeniedPermissionsNeverAskAgain(this,
//                tipString,
//                R.string.action_settings, R.string.cancel, null, perms);
//    }
//
//    protected void checkNetWork() {
//        if (!NetUtils.hasNetwork(getApplicationContext())) {
//            Toast.makeText(
//                    getApplicationContext(),
//                    getString(R.string.string_network_disconnect),
//                    Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    protected void initImmersionBar() {
//        mImmersionBar = ImmersionBar.with(this);
//        mImmersionBar.statusBarDarkFont(IF_BAR_DARK_FONT, 0.2f); //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
//        mImmersionBar.keyboardEnable(true);
//        mImmersionBar.navigationBarWithKitkatEnable(false);
//        mImmersionBar.navigationBarEnable(!IF_NAVIGATION_BAR_DARK_FONT);
//        if (IF_NAVIGATION_BAR_DARK_FONT) {
//            mImmersionBar.navigationBarColor(R.color.base_bg_color_level1);
//        } else {
//            mImmersionBar.navigationBarColor(R.color.base_bg_color_level1);
//        }
//    }

}
