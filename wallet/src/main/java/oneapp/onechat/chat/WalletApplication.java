package oneapp.onechat.chat;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.nostra13.universalimageloader.utils.L;
import com.umeng.analytics.MobclickAgent;

import oneapp.onechat.chat.view.BaseActivity;
import oneapp.onechat.oneandroid.chatsdk.ConfigConstants;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.RpcCallProxy;
import oneapp.onechat.oneandroid.onewallet.util.SystemLanguageUtils;
import onemessageui.OneChatUiHelper;
import onemessageui.community.PicassoImageLoader;
import onemessageui.utils.skin.SkinUtils;
import onewalletui.util.CustomCrashHandler;
import onewalletui.util.ImageLoaderHelper;
import onewalletui.util.jump.JumpParamsContants;
import skin.support.SkinCompatManager;


public class WalletApplication extends Application {

    public static boolean IF_GC_KILLED_APP = true;

    private BaseActivity mCurrentActivity;

    private long lastStop;

    private static WalletApplication instance;

    // 实例化一次
    public synchronized static WalletApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        mRefWatcher = LeakCanary.install(this);//内存泄漏检测

        instance = this;

        MultiDex.install(instance);

        OneAccountHelper.initOneChat(instance, "onechat");

        OneChatUiHelper.initOneChatUi(instance);

        // FIXME: 2017/11/6 hs
        initCrashCatch();

        initSkinLoad();

        initUmeng();

        Stetho.initializeWithDefaults(this);

        // hhr
        init();

        SystemLanguageUtils.setApplicationLanguage(this);

//        initFaceLib();
    }


    @Override
    protected void attachBaseContext(Context base) {
        SystemLanguageUtils.getSystemLocale();
        super.attachBaseContext(SystemLanguageUtils.setLocal(base));

        try {
            //        android4.4.4以下MultiDex方法
            MultiDex.install(this);
        } catch (Exception e) {

        }
    }

    /**
     * 初始化友盟
     */
    private void initUmeng() {
        /** 设置是否对日志信息进行加密, 默认false(不加密). */
        MobclickAgent.enableEncrypt(!ConfigConstants.DEBUG);//6.0.0版本及以后
    }

    //初始化换肤框架
    private void initSkinLoad() {
        SkinCompatManager.withoutActivity(this)                         // 基础控件换肤初始化
                .setSkinStatusBarColorEnable(true)                     // 关闭状态栏换肤，默认打开[可选]
                .setSkinWindowBackgroundEnable(false)                   // 关闭windowBackground换肤，默认打开[可选]
                .loadSkin();
        SkinUtils.configSkin();
    }

    /**
     * creash处理
     */
    private void initCrashCatch() {
        //crash报告
        CustomCrashHandler mCustomCrashHandler = CustomCrashHandler.getInstance();
        mCustomCrashHandler.setCustomCrashHanler(getApplicationContext());
    }

    private void init() {
        MultiDex.install(this);
//初始化ImageLoader
        ImageLoaderHelper.initImageLoader(this);
        L.writeDebugLogs(false);
        L.writeLogs(false);

        //配置语言
        SystemLanguageUtils.configLanguage(this);

        initImagePicker();
    }

    void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new PicassoImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(CommonConstants.MAX_SEND_WEIBO_IMG);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        RpcCallProxy.getInstance().onDestroy();
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //从横屏切换回来时会重置Config,所以要重新配置语言
        SystemLanguageUtils.configLanguage(this);
    }

    public void exitApp() {
        Intent intent = new Intent(this, MainActivity.class);
//传递退出所有Activity的Tag对应的布尔值为true
        intent.putExtra(JumpParamsContants.INTENT_EXIST_APP, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//启动BaseActivity
        startActivity(intent);
    }

    // 启动或者退出广播
    public void sendcastForAppStatus(int nAppStatus) {
        Intent intent = new Intent();
        intent.setAction(CommonConstants.BCAST_APPSTATUS_FROM_APP);
        intent.putExtra("appstatus", nAppStatus);

        sendBroadcast(intent);
    }

    public void sendcastForDataUpdate() {
        Intent intent = new Intent();
        intent.setAction(CommonConstants.BCAST_DATAUPDATE_FROM_APP);

        sendBroadcast(intent);
    }


    public BaseActivity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(BaseActivity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

}