package onewalletui.onekeyshare;

import android.content.Context;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import oneapp.onechat.oneandroid.R;

/**
 * Created by 何帅 on 2017/12/12.
 */

public class ShareUtils {
    public static void showShareWebUrl(Context context, String title, String text, String url) {
        showShare(context, title, text, null, null, url, null, url);
    }

    public static void showShare(Context context, String title, String text, String imagePath, String titleUrl, String url, String comment, String siteUrl) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(titleUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        if (StringUtils.equalsNull(imagePath)) {
//            Bitmap bitmap = ImageUtils.readBitmap(context, R.drawable.share_icon);
//            File file = BaseUtils.saveBitmapFile(bitmap, "share_icon.jpg");
//            imagePath = file.getPath();
//        }
        oks.setImagePath(imagePath);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(comment);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(context.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(siteUrl);
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        oks.setSilent(true);  // 隐藏编辑页面

//        oks.setPlatform(QQ.NAME);
//        oks.setPlatform(Wechat.NAME);
//        oks.setPlatform(Twitter.NAME);
//        oks.setPlatform(Telegram.NAME);
//        oks.setPlatform(SinaWeibo.NAME);
        // 启动分享GUI
        oks.show(context);
    }
}
