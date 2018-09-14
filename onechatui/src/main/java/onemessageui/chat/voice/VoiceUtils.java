package onemessageui.chat.voice;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import oneapp.onechat.oneandroid.onewallet.Constants;

/**
 * Created by zyx on 2018/3/2.
 */

public class VoiceUtils {
    /**
     * SD卡目录
     */
    public static final String SDCARD_MADER = Environment
            .getExternalStorageDirectory() + Constants.SAVE_FILE_NAME + "voice/";

    /**
     * 语音格式
     */
    public static final String DEFAULT_VOICE_FORMAT = ".amr";

    /**
     * 获取指定文件夹下的所有文件路径
     *
     * @param root 指定文件夹路径
     * @return 指定文件夹下的所有文件
     */
    public static ArrayList<String> getVideoFiles(String root) {
        if (root == null || root == "")
            return null;

        ArrayList<String> list = new ArrayList<>();
        File file = new File(root);
        File[] fileList = file.listFiles();

        for (File f : fileList) {
            list.add(f.getPath());
        }

        return list;
    }

    /**
     * 获取声音文件名字
     *
     * @return 假如当前录制声音时间是2016年4月29号14点30分30秒。得到的文件名字就是20160429143030.这样保证文件名的唯一性
     */
    public static String getVoiceFile() {
        return SDCARD_MADER;
    }

    /**
     * 获取声音文件名字
     *
     * @return 假如当前录制声音时间是2016年4月29号14点30分30秒。得到的文件名字就是20160429143030.这样保证文件名的唯一性
     */
    public static String getVoiceFileName() {
        long getNowTimeLong = System.currentTimeMillis();
        SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");
        String result = time.format(getNowTimeLong);
        return result;
    }
}
