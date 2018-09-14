package onemessageui.chat.utils;

import java.util.HashMap;

import oneapp.onechat.oneandroid.onemessage.beanchat.util.PathUtil;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.UtilLog;

public class ImageUtils {
    public static final int IMG_STATUS_LOADING_NET = 1;
    public static final int IMG_STATUS_LOADING_BITMAP = 2;
    public static HashMap<String, Integer> mLoadingImgCatch = new HashMap<>();


    public static String getImagePath(String remoteUrl) {
        String imageName = remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1,
                remoteUrl.length());
        String path = PathUtil.getInstance().getImagePath() + "/" + imageName;
        UtilLog.d("msg", "image path:" + path);
        return path;

    }

    public static String getThumbnailImagePath(String thumbRemoteUrl) {
        if (thumbRemoteUrl == null) {
            return "";
        }
        String thumbImageName = thumbRemoteUrl.substring(
                thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
        String path = PathUtil.getInstance().getImagePath() + "/" + "th"
                + thumbImageName;
        UtilLog.d("msg", "thum image path:" + path);
        return path;
    }

}
