package onemessageui.utils.skin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.ConfigConstants;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import sdk.android.onechatui.R;
import skin.support.utils.SkinPreference;


/**
 * Created by 何帅 on 2018/7/24.
 */

public class SkinUtils {
    /**
     * 支持语言
     */
    public static final String DEFAULT_SKIN = "default";
    public static final String BLACK_SKIN = "blackskin";
    private static final HashMap<String, SkinBean> SUPPORTED_SKINS = new HashMap<String, SkinBean>() {
        {
            put(DEFAULT_SKIN, new SkinBean(DEFAULT_SKIN, R.string.default_skin, true));
            put(BLACK_SKIN, new SkinBean(BLACK_SKIN, R.string.black_skin, false));
        }
    };

    public static void configSkin() {
        String skin = SkinPreference.getInstance().getSkinName();
        if (StringUtils.equalsNull(skin)) {
            skin = DEFAULT_SKIN;
        }
        if (SUPPORTED_SKINS.containsKey(skin)) {
            ConfigConstants.DEFAULT_BAR_DARK = SUPPORTED_SKINS.get(skin).isIfDarkBar();
            ConfigConstants.DEFAULT_NAVIGATION_BAR_DARK = SUPPORTED_SKINS.get(skin).isIfDarkBar();
        }

    }

    public static String getSkinName() {
        String skin = SkinPreference.getInstance().getSkinName();
        if (StringUtils.equalsNull(skin)) {
            skin = DEFAULT_SKIN;
        }
        return skin;
    }

    public static List<SkinBean> getSkinList() {
        List<SkinBean> skinBeanList = new ArrayList<>(SUPPORTED_SKINS.values());
        Collections.reverse(skinBeanList);
        return skinBeanList;
    }

    //是否深色主题
    public static boolean getIfDarkSkin() {
        return StringUtils.equals(getSkinName(), BLACK_SKIN);
    }
}
