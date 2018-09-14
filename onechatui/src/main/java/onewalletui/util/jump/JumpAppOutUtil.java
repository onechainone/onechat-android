package onewalletui.util.jump;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;

import java.io.File;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import onewalletui.service.DownAPKService;

import static oneapp.onechat.oneandroid.onewallet.util.permission.EasyPermissions.SETTINGS_REQ_CODE;


/**
 * 跳转到外部应用的工具类
 *
 * @author heshuai
 */
public class JumpAppOutUtil {
    /**
     * 跳转到打电话界面
     *
     * @param context
     * @param tel
     */
    public static void callPhone(Context context, String tel) {
        try {
            if (context != null && !StringUtils.equalsNull(tel)) {
                final Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + tel));
                if (context instanceof Application) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到外部浏览器
     *
     * @param context
     * @param url     要打开的升级网页
     */
    public static void jumpOutBrowser(Context context, String url) {
        try {
            final Uri uri = Uri.parse(url);
            final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (context == null) {
                context = OneAccountHelper.getContext();
            }
            if (context instanceof Application) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 后台下载
     *
     * @param context
     * @param download_url 下载链接
     */
    public static void jumpDownload(Context context, String download_url) {
        Intent intent = new Intent(context, DownAPKService.class);
        intent.putExtra(JumpParamsContants.DOWNLOAD_URL, download_url);
        context.startService(intent);
    }

    /**
     * 后台打开APK
     *
     * @param context
     * @param path
     */
    public static void jumpInstallAPK(Context context, File path, String downloadPath) {
        try {
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            Uri uri = BaseUtils.getUriForFile(context, path);
            installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 24) {
                installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                //installIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            context.startActivity(installIntent);// 下载完成之后自动弹出安装界面
        } catch (Exception e) {
            jumpOutBrowser(context, downloadPath);
        }

    }

    public static void jumpTakePhoto(Activity mActivity, String tempFileName, int code) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 判断存储卡是否可以用，可用进行存储
        if (BaseUtils.isMounted()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    BaseUtils.getUriForFile(mActivity, new File(tempFileName)));
        }
        mActivity.startActivityForResult(intent, code);

    }

    @TargetApi(11)
    public static void startAppSettingsScreen(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, SETTINGS_REQ_CODE);
    }
}
