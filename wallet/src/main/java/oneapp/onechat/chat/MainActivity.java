package oneapp.onechat.chat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.ActionMode;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.network.RequestUtils;
import oneapp.onechat.chat.utils.jump.JumpAppPageUtil;
import oneapp.onechat.chat.utils.jump.JumpParamsContants;
import oneapp.onechat.chat.view.BaseActivity;
import oneapp.onechat.chat.view.ContactFragment;
import oneapp.onechat.chat.view.Fragment_Profile;
import oneapp.onechat.oneandroid.chatsdk.ConfigConstants;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneChatHelper;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsApplication;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.RpcCallProxy;
import oneapp.onechat.oneandroid.onemessage.SystemInfo;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemCmdItemMessageBody;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.PathUtil;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.UtilLog;
import oneapp.onechat.oneandroid.onemessage.common.CommonHelperUtils;
import oneapp.onechat.oneandroid.onemessage.common.ToolsLog;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onemessageui.dialog.DialogUtil;
import onemessageui.dialog.WarnTipDialog;
import onemessageui.view.fragment.Fragment_Msg;
import onewalletui.ui.ScanActivity;

//import oneapp.graphenechain.utils.BtsHelperBackend;

public class MainActivity extends BaseActivity implements OnClickListener {
    private WarnTipDialog Tipdialog;
    private NewMessageBroadcastReceiver msgReceiver;
    protected static final String TAG = "MainActivity";
    private TextView unreadMsgLabel;// 未读消息textview
    //    private TextView unreadAddressLable;// 未读通讯录textview
    private Fragment[] fragments;
    public Fragment_Msg homefragment;
    private ContactFragment contactFragment;//通讯录
    private Fragment_Profile profilefragment;
    private ImageView[] imagebuttons;
    private TextView[] textviews;
    private String connectMsg = "";

    private final int TAB_MSG = 0;
    private final int TAB_CONTACT = 1;
    private final int TAB_MY = 2;

    private final int TOTAL_TAB_NUM = 3;
    private final int DEFAULT_TAB = 0;

    private int index;
    private int currentTabIndex;// 当前fragment的index

    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);

    private static MainActivity m_pThis = null;

    public static MainActivity getInstance() {
        return m_pThis;
    }

    private boolean m_running = false;

    // 后台运行状态
    public int m_backendServiceStatus = CommonConstants.APP_STATUS_RUNING;

    public boolean m_exitall = false;

    boolean m_bOpenService = false;

    public boolean m_isForeground = false;


    public boolean m_is_unlock = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        IF_KEYBOARD_ENABLE = false;

        RpcCallProxy.getInstance().saveAppStatus(CommonConstants.APP_STATUS_RUNING);
        ifOpenRightSlideBack = false;
        isInitBarOnCreate = true;

        super.onCreate(savedInstanceState);

        m_pThis = this;

        if (ConfigConstants.DEBUG) {
            ToastUtils.simpleToast("当前为调试模式!");
        }

        setContentView(R.layout.activity_main);
        readArguments(getIntent());

//        MessageApp.getInstance2().addActivity(this);
        initTabView();
        initReceiver();

        checkBasePermission();

        checkUpdate();
        try {
            if (OneChatHelper.getMessageSender() != null) {
                OneChatHelper.initMessageSender(this);
                OneChatHelper.startMessageSender();
            }

            RegistBroadcastListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void checkBasePermission() {
        checkPermission(new CheckPermListener() {
            @Override
            public void superPermission() {
                PathUtil.getInstance().initDirs(context);
            }
        }, R.string.file, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void checkUpdate() {
        //检查更新
        RequestUtils.UpgradeCheckRequest(MainActivity.this, false);
//        DialogUtil.upgradeDialog(this, "APP\u7248\u672c\u66f4\u65b0\u5185\u5bb9\\n    1.\u652f\u6301618\u79cd\u6570\u5b57\u8d44\u4ea7\\n    2.\u5f00\u653e\u62b5\u62bc\u548c\u62c5\u4fdd\n    3.\u624b\u673a\u6316\u77ff\\n    4.\u7ec6\u8282\u4f18\u5316\n    5.\u5f00\u653e\u8282\u70b9\u8ba1\u5212\\n" , "https://app1.haoduobi.cn/android/one127.apk");

    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            startApp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        //Log.v("LH", "onSaveInstanceState"+outState);
        //super.onSaveInstanceState(outState);   //将这一行注释掉，阻止activity保存fragment的状态
//        if (outState != null) {//存在Bundle数据,去除fragments的状态保存，解决Fragment错乱问题。
//            String FRAGMENTS_TAG = "android:support:fragments";
//            outState.remove(FRAGMENTS_TAG);
//        }
    }

    private void checkWhereToGo() {
        // fixme:need to check wallet and chat account
        if (OneAccountHelper.isHasAccount()) {
            BtsHelper.requestAccountInfo();
//            if (Helper.fetchBoolianSharePref(this, CommonConstants.PREF_IF_SAVE_WALLET_SEED)) {
            moveToUnlockPage();
//            } else {
//                JumpAppPageUtil.jumpShowSeedPage(this, true);
//            }
            m_is_unlock = true;
        } else {
            moveToRegist();
            if (!SharePreferenceUtils.contains(this, SharePreferenceUtils.SP_IF_FIRST_IN_APP)) {
                SharePreferenceUtils.putObject(this, SharePreferenceUtils.SP_IF_FIRST_IN_APP, CommonConstants.DEFAULT_DAO_CODE);
                JumpAppPageUtil.jumpConfigServiceNodePage(context);
            }
        }

    }


    void moveToUnlockPage() {
        JumpAppPageUtil.jumpUnlockPage(this);
    }

    private void moveToRegist() {
        JumpAppPageUtil.jumpRegisterGuidePage(context, Constants.FROM_MAIN);
    }

    // ////////////////////////////////////////////////
    // 程序启动
    // ////////////////////////////////////////////////

//    private void initApp() {
//        try {
//            WalletApplication application = WalletApplication.getInstance();
//            application.initMessageApp();
//            application.initBtsApp();
//            application.initWalletApp();
//
////            checkWhereToGo();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    protected void startApp() {
        if (!m_running) {
//            startPushService();

            m_running = true;

            SystemInfo.saveAppStatus(this, CommonConstants.STRING_RMSKEY_APP_STATUS, CommonConstants.APP_STATUS_RUNING);
        }
    }

//    public void startPushService() {
//        try {
////            stopPushService();
//
//            Intent mIntent = new Intent();
//            mIntent.setAction(CommonConstants.SERVICE_BACKEND_WATCHDOG);//你定义的service的action
//            mIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
//            ComponentName comName = this.startService(mIntent);
//            if (comName != null) {
//                // 通知后台
//                this.sendcastForAppStatus(CommonConstants.APP_STATUS_RUNING);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    /***
//     * Android L (lollipop, API 21) introduced a new problem when trying to invoke implicit intent,
//     * "java.lang.IllegalArgumentException: Service Intent must be explicit"
//     *
//     * If you are using an implicit intent, and know only 1 target would answer this intent,
//     * This method will help you turn the implicit intent into the explicit form.
//     *
//     * Inspired from SO answer: http://stackoverflow.com/a/26318757/1446466
//     * @param context
//     * @param implicitIntent - The original implicit intent
//     * @return Explicit Intent created from the implicit original intent
//     */
//    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
//        // Retrieve all services that can match the given intent
//        PackageManager pm = context.getPackageManager();
//        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
//
//        // Make sure only one match was found
//        if (resolveInfo == null || resolveInfo.size() != 1) {
//            return null;
//        }
//
//        // Get component info and create ComponentName
//        ResolveInfo serviceInfo = resolveInfo.get(0);
//        String packageName = serviceInfo.serviceInfo.packageName;
//        String className = serviceInfo.serviceInfo.name;
//        ComponentName component = new ComponentName(packageName, className);
//
//        // Create a new intent. Use the old one for extras and such reuse
//        Intent explicitIntent = new Intent(implicitIntent);
//
//        // Set the component to be explicit
//        explicitIntent.setComponent(component);
//
//        return explicitIntent;
//    }

    public void stopPushService() {
        try {
            // 启动后台
            Intent intent = new Intent();
            intent.setAction(CommonConstants.SERVICE_BACKEND_WATCHDOG);//你定义的service的action
            intent.setPackage(getPackageName());//这里你需要设置你应用的包名
            this.stopService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ////////////////////////////////////////////////
    // 程序停止
    // ////////////////////////////////////////////////

    public void exitMyApp(boolean arg0) {
        try {
            if (arg0) {
                m_exitall = true;
            }

            // 记录app状态，通知后台服务
            notifyAppStatusToService(CommonConstants.APP_STATUS_EXIT);

            RpcCallProxy.getInstance().saveAppStatus(CommonConstants.APP_STATUS_EXIT);

            ToolsLog.logprintln("MainActivity exitMyApp = ");

//            m_backendLocation.exitMyApp(true);
//
//            // 停止数据库
//            databaseClose();
//
////			if(mCallMainEngine != null)
////			{
////				mCallMainEngine.exit();
////				mCallMainEngine = null;
////			}
        } catch (Exception e) {
        } finally {
//            System.exit(0);
        }
    }

    public void deriveDialog() {
        if (!OneAccountHelper.ifHasAccountInfo()) {
            return;
        }
        boolean ifShowDialog = false;
        if (!SharePreferenceUtils.contains(SharePreferenceUtils.SP_DERIVE_BRAINKEY)) {
            ifShowDialog = true;
        } else {
            try {
                ifShowDialog = (boolean) SharePreferenceUtils.getObject(SharePreferenceUtils.SP_DERIVE_BRAINKEY);
            } catch (Exception e) {

            }
        }

        if (ifShowDialog) {
            DialogUtil.threeBtnDialog(this, getString(R.string.whether_export_immediately), getString(R.string.export_immediately), getString(R.string.never_remind), getString(R.string.button_cancel), new DialogUtil.ConfirmCallBackInf() {
                @Override
                public void onConfirmClick(String content) {
                    JumpAppPageUtil.jumpDeriveBrainkeyPage(MainActivity.this);
                }
            }, new DialogUtil.RemindCallBackInf() {
                @Override
                public void onRemindClick(String content) {
                    SharePreferenceUtils.putObject(SharePreferenceUtils.SP_DERIVE_BRAINKEY, false);
                }
            });
            ifDeriveDialog = false;
        }
    }

    // 用户退出
    public void userExit() {
        if (m_exitall) {
            finish();
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    public void notifyAppStatusToService(int status) {
        try {
            // 将程序状态写入
            SystemInfo.saveAppStatus(this, CommonConstants.STRING_RMSKEY_APP_STATUS, status);

            // 通知后台
            WalletApplication.getInstance().sendcastForAppStatus(status);
        } catch (Exception e) {
        }
    }


    private void initTabView() {
        try {
            //包含fragment必需先初始化
            mImmersionBar.init();
        } catch (Exception e) {
        }

        unreadMsgLabel = (TextView) findViewById(R.id.unread_msg_number);
//        unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
        homefragment = new Fragment_Msg();
        profilefragment = new Fragment_Profile();
        contactFragment = new ContactFragment();

        fragments = new Fragment[TOTAL_TAB_NUM];
        fragments[TAB_MSG] = homefragment;
        fragments[TAB_CONTACT] = contactFragment;
        fragments[TAB_MY] = profilefragment;

        imagebuttons = new ImageView[TOTAL_TAB_NUM];
        imagebuttons[TAB_MSG] = (ImageView) findViewById(R.id.ib_home);
        imagebuttons[TAB_CONTACT] = (ImageView) findViewById(R.id.ib_contact);
        imagebuttons[TAB_MY] = (ImageView) findViewById(R.id.ib_profile);
        imagebuttons[DEFAULT_TAB].setSelected(true);

        textviews = new TextView[TOTAL_TAB_NUM];
        textviews[TAB_MSG] = (TextView) findViewById(R.id.tv_home);
        textviews[TAB_CONTACT] = (TextView) findViewById(R.id.tv_contact);
        textviews[TAB_MY] = (TextView) findViewById(R.id.tv_profile);
        textviews[DEFAULT_TAB].setTextAppearance(context, R.style.base_color);

        // 添加显示第一个fragment
//        getSupportFragmentManager().beginTransaction()
//                .icon_recive_red(R.id.fragment_container, homefragment)
//                .icon_recive_red(R.id.fragment_container, overviewFragment)
//                .icon_recive_red(R.id.fragment_container, assetTradeFragment)
//                .icon_recive_red(R.id.fragment_container, profilefragment)
//                .hide(overviewFragment).hide(assetTradeFragment).hide(profilefragment)
//                .show(homefragment).commit();
        if (!fragments[DEFAULT_TAB].isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragments[DEFAULT_TAB])
//                .icon_recive_red(R.id.fragment_container, overviewFragment)
//                .icon_recive_red(R.id.fragment_container, assetTradeFragment)
//                .icon_recive_red(R.id.fragment_container, profilefragment)
//                .hide(overviewFragment).hide(assetTradeFragment).hide(profilefragment)
                    .show(fragments[DEFAULT_TAB]).commitAllowingStateLoss();
        }
        updateUnreadLabel();

    }

    public void onTabClicked(View view) {
//        img_right.setVisibility(View.GONE);
        switch (view.getId()) {
            case R.id.re_home:
                index = TAB_MSG;
                break;
            case R.id.re_contact:
                index = TAB_CONTACT;
                break;
            case R.id.re_wallet:
//                if (!ConfigConstants.DEBUG && !WalletUtils.checkSaveSeed(this)) {
//                    return;
//                } else {
//                    index = TAB_WALLET;
//                }
                break;
            case R.id.re_exchange:
//                index = TAB_ASSET_TRADE;
                break;
            case R.id.re_profile:
                index = TAB_MY;
                break;
        }

        if (currentTabIndex != index) {
            try {
                FragmentTransaction trx = getSupportFragmentManager()
                        .beginTransaction();
                trx.hide(fragments[currentTabIndex]);
                if (!fragments[index].isAdded()) {
                    trx.add(R.id.fragment_container, fragments[index]);
                }

                for (int i = 0; i < TOTAL_TAB_NUM; i++) {
                    if (i != index && i != currentTabIndex) {
                        if (fragments[i].isAdded()) {
                            trx.hide(fragments[i]);
                        }
                    }
                }
                trx.show(fragments[index]).commitAllowingStateLoss();
            } catch (Exception e) {

            }
        }
        imagebuttons[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        imagebuttons[index].setSelected(true);

        textviews[currentTabIndex].setTextAppearance(context, R.style.bottom_text_normal);
        textviews[index].setTextAppearance(context, R.style.base_color);
        currentTabIndex = index;
        detailIndexFragment(currentTabIndex);

    }

    private void detailIndexFragment(int currentTabIndex) {
        switch (currentTabIndex) {
            case TAB_MSG:
                if (homefragment != null) {
                    // FIXME: 2017/11/13 hs
//                    BtsApplication.getInstance().shortWebsocketStart();
//                    homefragment.refresh();
                }
                break;
            case TAB_MY:
                if (profilefragment != null) {
                    profilefragment.updateView();
                }

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            OneChatHelper.stopMessageSender();

            try {
                unregisterReceiver(m_boradcastReceiver);
            } catch (Exception e) {
            }
            try {
                unregisterReceiver(cmdMessageReceiver);
            } catch (Exception e) {
            }
            try {
                unregisterReceiver(ackMessageReceiver);
            } catch (Exception e) {
            }
            try {
                unregisterReceiver(msgReceiver);
            } catch (Exception e) {
            }

            BtsApplication.getInstance().onStop();

            exitMyApp(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //导出加密助记词Dialog是否第一次弹出
    boolean ifDeriveDialog = true;

    @Override
    protected void onResume() {
        super.onResume();
        if (OneAccountHelper.ifHasAccountInfo() && !StringUtils.equalsNull(OneAccountHelper.getActionIntentData())) {
            onewalletui.util.jump.JumpAppPageUtil.detailIntentDataJump(context, OneAccountHelper.getActionIntentData(), false);
        }
        if (ifDeriveDialog) {
            deriveDialog();
        }

        updateUnreadLabel();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {//判断其他Activity启动本Activity时传递来的intent是否为空
//获取intent中对应Tag的布尔值
            boolean isExist = intent.getBooleanExtra(JumpParamsContants.INTENT_EXIST_APP, false);
            //如果为真则退出本Activity
            if (isExist) {
                this.finish();
                return;
            }
            readArguments(intent);
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    String resultString = intent.getStringExtra(ScanActivity.INTENT_EXTRA_RESULT);
                    onewalletui.util.jump.JumpAppPageUtil.detailIntentDataJump(context, resultString, true);

                } catch (final Exception e) {
                    String error = getResources().getString(R.string.qr_code_nonsupport);
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private void readArguments(Intent intent) {

        final Serializable sear = intent
                .getSerializableExtra(JumpParamsContants.INTENT_PARAMS_MAP);
        if (sear != null) {
            checkWhereToGo();

        }
    }

    private int keyBackClickCount = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (keyBackClickCount++) {
                case 0:
                    Toast.makeText(this, getString(R.string.click_back_again), Toast.LENGTH_SHORT).show();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            keyBackClickCount = 0;
                        }
                    }, 3000);
                    break;
                case 1:
                    RpcCallProxy.getInstance().saveAppStatus(CommonConstants.APP_STATUS_EXIT);

                    exitApp();
//                    MessageApp.getInstance2().exit();
//                    finish();
//                    overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                    break;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    private DialogInterface.OnClickListener onclick = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Utils.showLongToast(MainActivity.this, "正在下载...");// TODO
            Tipdialog.dismiss();
        }
    };

    private void initReceiver() {
//        Intent intent = new Intent(this, UpdateService.class);
//        startService(intent);
//
//        registerReceiver(new MyBroadcastReceiver(), new IntentFilter(
//                "oneapp.onechat.androidapp.Brodcast"));

        // 注册一个接收消息的BroadcastReceiver
        msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(CommonHelperUtils.getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);

        // 注册一个ack回执消息的BroadcastReceiver
        IntentFilter ackMessageIntentFilter = new IntentFilter(CommonHelperUtils.getAckMessageBroadcastAction());
        ackMessageIntentFilter.setPriority(3);
        registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

        // 注册一个透传消息的BroadcastReceiver
        IntentFilter cmdMessageIntentFilter = new IntentFilter(CommonHelperUtils.getCmdMessageBroadcastAction());
        cmdMessageIntentFilter.setPriority(3);
        registerReceiver(cmdMessageReceiver, cmdMessageIntentFilter);
        // setContactListener监听联系人的变化等
        // getInstance().setContactListener(
        // new MyContactListener());
        // 注册一个监听连接状态的listener
        // getInstance().addConnectionListener(
        // new MyConnectionListener());

//        // // 注册小密圈相关的listener
//        getInstance().addGroupChangeListener(
//                new MyItemGroupChangeListener());
//        // 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
//        getInstance().setAppInited();
    }

    // 自己联系人 群组数据返回监听
    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Bundle bundle = intent.getExtras();
            homefragment.refresh();
        }
    }

    /**
     * 新消息广播接收者
     */
    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看

            String from = intent.getStringExtra("from");
            // 消息id
            String msgId = intent.getStringExtra(JumpParamsContants.INTENT_MSG_IDS);
//			// 刷新bottom bar消息未读数
            updateUnreadLabel();
        }
    }

    /**
     * 消息回执BroadcastReceiver
     */
    private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
//            abortBroadcast();
//            // 刷新bottom bar消息未读数
//            updateUnreadLabel();
//            String msgid = intent.getStringExtra(IntentParamsContants.INTENT_MSG_IDS);
//            String from = intent.getStringExtra("from");
//
//            ItemConversation conversation = getInstance()
//                    .getConversation(from);
//            if (conversation != null) {
//                // 把message设为已读
//                ItemMessage msg = conversation.getMessage(msgid);
//
//                if (msg != null) {
//
//                    if (ChatActivity.activityInstance != null) {
//                        if (msg.getChatType() == ChatType.Chat) {
//                            if (from.equals(ChatActivity.activityInstance
//                                    .getToChatUsername()))
//                                return;
//                        }
//                    }
//
//                    msg.isAcked = true;
//                }
//            }
        }
    };

    /**
     * 透传消息BroadcastReceiver
     */
    private BroadcastReceiver cmdMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();
            // 刷新bottom bar消息未读数
            updateUnreadLabel();
            UtilLog.d(TAG, "收到透传消息");
            // 获取cmd message对象
            String msgId = intent.getStringExtra(JumpParamsContants.INTENT_MSG_IDS);
            ItemMessage message = intent.getParcelableExtra("message");
            // 获取消息body
            ItemCmdItemMessageBody cmdMsgBody = (ItemCmdItemMessageBody) message.getBody();
            String action = cmdMsgBody.action;// 获取自定义action

            // 获取扩展属性 此处省略
            // message.getStringAttribute("");
            UtilLog.d(TAG,
                    String.format("透传消息：action:%s,message:%s", action,
                            message.toString()));
            String st9 = getResources().getString(
                    R.string.receive_the_passthrough);
            Toast.makeText(MainActivity.this, st9 + action, Toast.LENGTH_SHORT)
                    .show();
        }
    };

    /**
     * 获取未读消息数
     */
    public void updateUnreadLabel() {
        try {
            if (unreadMsgLabel == null) {
                return;
            }
            int unReadNum = OneAccountHelper.getDatabase().getUnReadMsgNum();
            String unReadNumString = String.valueOf(unReadNum);
            if (unReadNum > CommonConstants.MIAX_SHOW_UNREAD_MSG_NUM) {
                unReadNumString = CommonConstants.MIAX_SHOW_UNREAD_MSG_NUM + context.getString(R.string.add_symbol);
            }
            if (unReadNum > 0) {
                unreadMsgLabel.setText(unReadNumString);
                unreadMsgLabel.setVisibility(View.VISIBLE);
            } else {
                unreadMsgLabel.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {

        }
    }

    //////////////////////////////


    public static final int REQUEST_CODE_SCAN = 0;

    private ActionMode lastActionMode;

    ///////////////////////
    @Override
    public void onBackPressed() {
        finishActionMode();
        super.onBackPressed();
    }


    //////////////////////

    private void finishActionMode() {
        if (lastActionMode != null) {
            lastActionMode.finish();
            lastActionMode = null;
        }
    }


    // 注册广播
    public void RegistBroadcastListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonConstants.BCAST_DATAUPDATE_FROM_SERVICE);
        filter.addAction(CommonConstants.BCAST_SERVICESTATUS_FROM_SERVICE);

        registerReceiver(m_boradcastReceiver, filter);
    }

    // 广播处理
    private BroadcastReceiver m_boradcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (CommonConstants.BCAST_DATAUPDATE_FROM_SERVICE.equals(action)) {
                    int dataupdatetype = intent.getIntExtra(CommonConstants.DATA_UPDATE_TYPE, -1);
                    switch (dataupdatetype) {
                        case CommonConstants.DATA_UPDATE_TYPE_USER_INFO: {
                            String strTemp = "";

                            break;
                        }
                        default:
                            break;

                    }
                } else if (CommonConstants.BCAST_MSGUPDATE_FROM_SERVICE.equals(action)) {
                    int pushmsgid = intent.getIntExtra("pushmsgid", -1);
                    int update_type = intent.getIntExtra("update_type", -1);
//                    sendcastForMsgUpdate(pushmsgid, update_type);
                } else if (CommonConstants.BCAST_SERVICESTATUS_FROM_SERVICE.equals(action)) {
                    // 服务启动或退出
                    int appstatus = intent.getIntExtra("appstatus", -1);
                    if (appstatus == CommonConstants.APP_STATUS_RUNING) {
                        // 服务启动
                        m_backendServiceStatus = CommonConstants.APP_STATUS_RUNING;
                    } else if (appstatus == CommonConstants.APP_STATUS_STOPED) {
                        // 服务停止，停止发送数据更新广播
                        m_backendServiceStatus = CommonConstants.APP_STATUS_STOPED;
                        RpcCallProxy.getInstance().init(context);
                    } else if (appstatus == CommonConstants.APP_STATUS_EXIT_APP) {
                        // 退出主界面
                        m_exitall = true;
                        userExit();
                    }
                }
//                else if(CommonConstants.BCAST_GETCONTACTS_FROM_SERVICE.equals(action))
//                {
////					//上传联系人
////					uploadKeywordListToServer_Hot("");
//////					uploadKeywordListToServer("");
//
//                    // 更新词表
//                    buildGrammarLocalAndCloud("");
//                }
//				else if(CommonConstants.BCAST_SAVECONTACTS_FROM_SERVICE.equals(action))
//				{
//					// load contact data
//					UserConfigPreferences.loadContactsList(true);
//				}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


}