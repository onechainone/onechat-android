package onewalletui.service;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;


import java.io.File;

import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.download.OnDownloadListener;
import onewalletui.util.jump.JumpAppOutUtil;
import onewalletui.util.jump.JumpParamsContants;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onewallet.util.download.DownloadUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;

/**
 * @author LEI-LEI
 * @version 1.0
 *          2016-7-6 下午4:54:16
 * @Title:DownAPKService.java
 * @Description:专用下载APK文件Service工具类,通知栏显示进度,下载完成震动提示,并自动打开安装界面(配合xUtils快速开发框架) 需要添加权限：
 * <uses-permission android:name="android.permission.INTERNET" />
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.VIBRATE" />
 * <p>
 * 需要在<application></application>标签下注册服务
 * <p>
 * 可以在142行代码：builder.setSmallIcon(R.drawable.ic_launcher);中修改自己应用的图标
 */

public class DownAPKService extends Service {

    private final int NotificationID = 0x10000;
    private NotificationManager mNotificationManager = null;
    private NotificationCompat.Builder builder;


    // 文件下载路径
    private String APK_url = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 接收Intent传来的参数:
        if (intent != null) {
            APK_url = intent.getStringExtra(JumpParamsContants.DOWNLOAD_URL);
        }
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (!StringUtils.equalsNull(APK_url))
            DownloadUtils.download(APK_url, true, new OnDownloadListener() {
                @Override
                public void onDownloadSuccess(File file) {
                    downloadSuccess(file);
                }

                @Override
                public void onDownloading(final int progress) {
                    downloading(progress);

                }

                @Override
                public void onDownloadFailed() {
                    mNotificationManager.cancel(NotificationID);
                    ToastUtils.simpleToast(getString(R.string.download_error));
                    JumpAppOutUtil.jumpOutBrowser(DownAPKService.this, APK_url);
                }

                @Override
                public void onStartDownload() {
                    startDownload();
                }
            });

        return super.onStartCommand(intent, flags, startId);
    }


    private void downloadSuccess(File file) {
        try {

            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            Uri uri = BaseUtils.getUriForFile(this, file);
            installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 24) {
                installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                //installIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            PendingIntent mPendingIntent = PendingIntent.getActivity(DownAPKService.this, 0, installIntent, 0);
            builder.setContentText(getString(R.string.download_ok));
            builder.setContentIntent(mPendingIntent);
            mNotificationManager.notify(NotificationID, builder.build());
            // 震动提示
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000L);// 参数是震动时间(long类型)
            stopSelf();
            startActivity(installIntent);// 下载完成之后自动弹出安装界面
            mNotificationManager.cancel(NotificationID);

        } catch (Exception e) {
            JumpAppOutUtil.jumpOutBrowser(this, APK_url);
        }
    }

    private void downloading(int progress) {
        try {
            if (progress % 10 != 0) {
                return;
            }
            builder.setProgress(100, progress, false);
            builder.setContentInfo(progress + "%");
//        builder.setContentInfo(getPercent(x, totalS));
            mNotificationManager.notify(NotificationID, builder.build());
        } catch (Throwable e) {
            JumpAppOutUtil.jumpOutBrowser(this, APK_url);
        }
    }

    private void startDownload() {
        builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.icon_notify);
        builder.setTicker(getString(R.string.downloading_new_apk));
        builder.setContentTitle(getApplicationName());
        builder.setContentText(getString(R.string.downloading));
        builder.setNumber(0);
        builder.setAutoCancel(true);
        mNotificationManager.notify(NotificationID, builder.build());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    /**
     * @return
     * @Description:获取当前应用的名称
     */
    private String getApplicationName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }


}
