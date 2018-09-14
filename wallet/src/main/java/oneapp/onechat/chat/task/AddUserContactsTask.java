package oneapp.onechat.chat.task;


import android.os.AsyncTask;

import java.util.List;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.network.RequestUtils;
import oneapp.onechat.chat.utils.BaseUtils;
import oneapp.onechat.chat.utils.jump.JumpAppPageUtil;
import oneapp.onechat.chat.view.BaseActivity;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onemessage.bean.PhoneContactBean;
import oneapp.onechat.oneandroid.onewallet.util.GsonUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onemessageui.dialog.DialogUtil;

/**
 * Created by 何帅 on 2018/2/12.
 */

public class AddUserContactsTask extends AsyncTask<String, Integer, List<PhoneContactBean>> {
    private BaseActivity activity;
    private boolean ifToast;
    private boolean ifJump;

    public AddUserContactsTask(BaseActivity activity, boolean ifToast, boolean ifJump) {
        this.activity = activity;
        this.ifJump = ifJump;
        this.ifToast = ifToast;
    }

    @Override
    protected void onPreExecute() {
        //这里是开始线程之前执行的,是在UI线程
        super.onPreExecute();
        activity.showLoadingDialog();
    }

    @Override
    protected List<PhoneContactBean> doInBackground(String... params) {
        //这是在后台子线程中执行的
        List<PhoneContactBean> mapList = null;
        try {
            mapList = BaseUtils.getPhoneContacts(activity);

        } catch (Exception e) {

        }
        return mapList;
    }

    @Override
    protected void onCancelled() {
        //当任务被取消时回调
        super.onCancelled();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(List<PhoneContactBean> list) {
        super.onPostExecute(list);
        if (list.size() > 0) {
            final String paramJson = GsonUtils.objToJson(list);

            RequestUtils.AddUserContactsRequest(paramJson, new RequestSuccessListener<Boolean>() {
                @Override
                public void onResponse(Boolean aBoolean) {
                    activity.hideLoadingDialog();
                    if (aBoolean) {
                        if (ifToast)
                            ToastUtils.simpleToast(R.string.success);
                        if (ifJump)
                            JumpAppPageUtil.jumpBindPhonePage(activity);
                    } else {
                        if (ifToast)
                            ToastUtils.simpleToast(R.string.erro);
                        if (ifJump)
                            DialogUtil.tipDialog(activity, activity.getString(R.string.error_uploading_bind_phone));
                    }
                }
            });
        } else {
            activity.hideLoadingDialog();
            DialogUtil.tipDialog(activity, activity.getString(R.string.check_null_or_permission));
        }
    }
}