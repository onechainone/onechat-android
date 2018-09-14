package onemessageui.mpush;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;

import com.mpush.api.Constants;
import com.mpush.api.http.HttpCallback;
import com.mpush.api.http.HttpMethod;
import com.mpush.api.http.HttpRequest;
import com.mpush.api.http.HttpResponse;
import com.mpush.client.ClientConfig;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import oneapp.onechat.oneandroid.chatsdk.ConfigConstants;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.onemessage.SystemInfo;
import sdk.android.onechatui.R;

public class PushUtils {

    ////////////////////type/////////////
    public static final String PUSH_TYPE_JOIN_GROUP = "join_group";
    public static final String PUSH_TYPE_WEIBO_REWARD = "reward_mpush";//赞赏
    public static final String PUSH_TYPE_WEIBO_COMMENT = "comment_mpush";//评论
    public static final String PUSH_TYPE_WEIBO_PAY = "zhifu_mpush";//支付
    public static final String PUSH_TYPE_URL = "url_mpush";//链接
    public static final String PUSH_TYPE_ADD_USER = "add_user";
    public static final String PUSH_TYPE_ADD_GROUP = "add_group";
    public static final String PUSH_TYPE_ORDER_PUSH = "MoneyBrokerOrderPushMessage";

    public static String WEIBO_ALL_MSG = "2";
    ////////////////////type/////////////

    ////////////////////key/////////////
    public static final String PUSH_KEY_GROUP_ID = "group_id";
    public static final String PUSH_KEY_ACCOUNT_NAME = "account_name";
    public static final String PUSH_KEY_URL = "url";
    public static final String PUSH_KEY_ORDER = "order";
    ////////////////////type/////////////

    //public static String serviceUrl = "ws://114.242.25.86:3000";
    public final static String serviceUrl;

    //公钥有服务端提供和私钥对应
    private final static String publicKey;

    static {
        switch (ConfigConstants.CURRENT_SERVICE_TYPE) {
            case ConfigConstants.SERVICE_TYPE_RELEASE://正式服务
            case ConfigConstants.SERVICE_TYPE_GRAY_TEST://灰度测试服务
                serviceUrl = "http://60.221.220.141:9999";//release
                publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCEiSNOG1yOshjlJM30164xARdn8jxW/dTmkmnz0WD21h3ee5HK1xkuCEn7BLpqaGi22IhMp6nwBSOiQ0O8TOomaLShl4B3hPUT2gm3+Dc07Xsfywio/YIIwsQe9Elwct7O50mSrTlvSOUYc0gnd8RGxYqGjHCx47SrJzKz3eUgKwIDAQAB";//release

                break;
            case ConfigConstants.SERVICE_TYPE_TEST://开发测试服务
            default:
                serviceUrl = "http://114.242.25.86:9999";
                publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCghPCWCobG8nTD24juwSVataW7iViRxcTkey/B792VZEhuHjQvA3cAJgx2Lv8GnX8NIoShZtoCg3Cx6ecs+VEPD2fBcg2L4JK7xldGpOJ3ONEAyVsLOttXZtNXvyDZRijiErQALMTorcgi79M5uVX9/jMv2Ggb2XAeZhlLD28fHwIDAQAB";
                break;
        }
    }


    private static void initPush(String allocServer, String userId) {


        ClientConfig cc = ClientConfig.build()
                .setPublicKey(publicKey)
                .setAllotServer(allocServer)
                .setDeviceId(SystemInfo.getUniqueID())
                .setClientVersion(ConfigConstants.VERSION_NAME)
//                .setLogEnabled(ConfigConstants.DEBUG)
                .setEnableHttpProxy(true)
                .setUserId(userId);
        MPush.I.checkInit(OneAccountHelper.getContext()).setClientConfig(cc);
    }


    public static void bindUser() {
        String userId = OneAccountHelper.getAccountId();
        if (!TextUtils.isEmpty(userId)) {
            MPush.I.bindAccount(userId, "mpush:" + (int) (Math.random() * 10));
        }
    }

    public static void startPush(String serviceUrl) {
        String userId = OneAccountHelper.getAccountId();

        initPush(serviceUrl, userId);

        MPush.I.checkInit(OneAccountHelper.getContext()).startPush();
    }

    public static void initNotify(Context context) {
        Notifications.I.init(context);
        Notifications.I.setSmallIcon(R.drawable.icon_notify);
        Notifications.I.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_launcher));
    }

    public static void sendPush(String serviceUrl) throws Exception {

        JSONObject params = new JSONObject();
        params.put("userId", OneAccountHelper.getAccountId());
//        params.put("hello", from + " say:" + hello);

        final Context context = OneAccountHelper.getContext();
        HttpRequest request = new HttpRequest(HttpMethod.POST, serviceUrl + "/push");
        byte[] body = params.toString().getBytes(Constants.UTF_8);
        request.setBody(body, "application/json; charset=utf-8");
        request.setTimeout((int) TimeUnit.SECONDS.toMillis(10));
        request.setCallback(new HttpCallback() {
            @Override
            public void onResponse(final HttpResponse httpResponse) {

            }

            @Override
            public void onCancelled() {

            }
        });
        MPush.I.sendHttpProxy(request);
    }

    public static void stopPush(View btn) {
        MPush.I.stopPush();
    }

    public static void pausePush(View btn) {
        MPush.I.pausePush();
    }

    public static void resumePush(View btn) {
        MPush.I.resumePush();
    }

    public static void unbindUser(View btn) {
        MPush.I.unbindAccount();
    }
}

