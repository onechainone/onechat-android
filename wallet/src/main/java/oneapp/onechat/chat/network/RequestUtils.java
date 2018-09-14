package oneapp.onechat.chat.network;

import android.util.Log;

import okhttp3.Request;
import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.utils.BaseUtils;
import oneapp.onechat.chat.view.BaseActivity;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneOpenHelper;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onemessage.bean.UpgradeBean;
import oneapp.onechat.oneandroid.onewallet.modle.MapResult;
import oneapp.onechat.oneandroid.onewallet.modle.Result;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.TimeUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;

/**
 * Created by 何帅 on 2017/9/19.
 */

public class RequestUtils {
    /**
     * 升级检查请求
     */
    public static void UpgradeCheckRequest(final BaseActivity context, final boolean ifToastMsg) {

        try {

            OkHttpClientManager.getAsyn(ServiceConstants.GetUpdateServer(), new OkHttpClientManager.ResultCallback<MapResult<UpgradeBean>>() {
                @Override
                public void onError(Request request, Exception e) {
                    Log.e("http-->", e.getMessage());
                }

                @Override
                public void onResponse(MapResult<UpgradeBean> result) {
                    if (result != null) {
                        TimeUtils.setTrueTime(result.getTime());
                        if (result.getData() != null && result.getData() != null && result.getData().getMap() != null) {
                            BaseUtils.checkUpgrade(context, result.getData().getMap());
                        } else if (ifToastMsg) {
                            ToastUtils.simpleToast(R.string.app_not_need_update);
                        }
                    }
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传通讯录
     */
    public static void AddUserContactsRequest(String paramJson, final RequestSuccessListener<Boolean> successListener) {
        try {
            OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                    new OkHttpClientManager.Param(ServiceConstants.KEY_PARAM, paramJson),
                    new OkHttpClientManager.Param(ServiceConstants.KEY_ACCOUNT_ID, OneAccountHelper.getAccountId())
            };
            OkHttpClientManager.postAsyn(ServiceConstants.AddUserContacts(), new OkHttpClientManager.ResultCallback<MapResult>() {
                @Override
                public void onError(Request request, Exception e) {
                    Log.e("http-->", e.getMessage());
                    if (successListener != null)
                        successListener.onResponse(false);
                }

                @Override
                public void onResponse(MapResult result) {
                    if (result != null && OneOpenHelper.checkResultCode(result.getCode())) {
                        if (successListener != null)
                            successListener.onResponse(true);
                    } else {
                        if (successListener != null)
                            successListener.onResponse(false);
                    }
                }
            }, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 手机短信验证码验证
     */
    public static void PhoneSendNum(String phone, String zone,
                                    String code, final RequestSuccessListener<Integer> successListener) {
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param(ServiceConstants.KEY_PHONE, phone),
                new OkHttpClientManager.Param(ServiceConstants.KEY_ZONE, zone),
                new OkHttpClientManager.Param(ServiceConstants.KEY_CODE, code),
                new OkHttpClientManager.Param(ServiceConstants.KEY_ACCOUNT_ID, BtsHelper.mMeAccountId)
        };

        OkHttpClientManager.postAsyn(ServiceConstants.PostPhoneSendNum(),
                new OkHttpClientManager.ResultCallback<Result>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Log.e("http-->", e.getMessage());
                        if (successListener != null)
                            successListener.onResponse(ServiceConstants.REQUEST_ERROR);
                    }

                    @Override
                    public void onResponse(Result result) {
                        int code = ServiceConstants.REQUEST_ERROR;
                        if (result != null) {
                            code = result.getCode();
                        }
                        if (successListener != null)
                            successListener.onResponse(code);
                    }
                }, params);
    }
}
