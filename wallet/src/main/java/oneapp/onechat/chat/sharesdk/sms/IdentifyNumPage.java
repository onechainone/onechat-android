/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，
 * 也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2014年 mob.com. All rights reserved.
 */
package oneapp.onechat.chat.sharesdk.sms;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.tools.utils.DeviceHelper;
import com.mob.tools.utils.ResHelper;
import com.mob.tools.utils.UIHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.network.RequestUtils;
import oneapp.onechat.chat.sharesdk.sms.layout.BackVerifyDialogLayout;
import oneapp.onechat.chat.view.BaseActivity;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;


/**
 * 验证码输入页面
 */
public class IdentifyNumPage extends BaseActivity implements OnClickListener,
        TextWatcher {
    private static final int RETRY_INTERVAL = 60;
    private static final int MIN_REQUEST_VOICE_VERIFY_INTERVAL = 1000;
    public static final String TEMP_CODE = "1319972";
    private String phone;
    private String code;
    private String formatedPhone;
    private int time = RETRY_INTERVAL;
    private EventHandler handler;
    private Dialog pd;

    private EditText etIdentifyNum;
    private TextView tvTitle;
    private TextView tvPhone;
    private TextView tvIdentifyNotify;
    private TextView tvUnreceiveIdentify;
    private ImageView ivClear;
    private Button btnSubmit;
    private Button btnSounds;
    private BroadcastReceiver smsReceiver;
    private int showDialogType = 1;
    private long lastRequestVVTime;
    private HashMap<String, Object> result;
    private ImageView img_back;

    public final void setResult(HashMap<String, Object> result) {
        this.result = result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identify_num_page_layout);
//            findViewById(ResHelper.getIdRes(activity, "ll_back")).setOnClickListener(this);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        code = intent.getStringExtra("code");
        formatedPhone = intent.getStringExtra("formatedPhone");


        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
        btnSubmit.setEnabled(false);

        tvTitle = (TextView) findViewById(R.id.txt_title);
        int resId = ResHelper.getStringRes(IdentifyNumPage.this, "smssdk_write_identify_code");
        if (resId > 0) {
            tvTitle.setText(resId);
        }

        etIdentifyNum = (EditText) findViewById(R.id.et_put_identify);
        etIdentifyNum.addTextChangedListener(this);

        tvIdentifyNotify = (TextView) findViewById(R.id.tv_identify_notify);
        resId = ResHelper.getStringRes(IdentifyNumPage.this, "smssdk_send_mobile_detail");
        if (resId > 0) {
            String text = IdentifyNumPage.this.getString(resId);
            tvIdentifyNotify.setText(Html.fromHtml(text));
        }

        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvPhone.setText(formatedPhone);

        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        img_back.setOnClickListener(this);

        tvUnreceiveIdentify = (TextView) findViewById(R.id.tv_unreceive_identify);
        resId = ResHelper.getStringRes(IdentifyNumPage.this, "smssdk_receive_msg");
        if (resId > 0) {
            String unReceive = IdentifyNumPage.this.getString(resId, time);
            tvUnreceiveIdentify.setText(Html.fromHtml(unReceive));
        }
        tvUnreceiveIdentify.setOnClickListener(this);
        tvUnreceiveIdentify.setEnabled(false);

        ivClear = (ImageView) findViewById(R.id.iv_clear);
        ivClear.setOnClickListener(this);

        btnSounds = (Button) findViewById(R.id.btn_sounds);
        btnSounds.setOnClickListener(this);

        handler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    /** 提交验证码 */
                    afterSubmit(result, data);
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    /** 获取验证码成功后的执行动作 */
                    afterGet(result, data);
                }
//                    else if (event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE) {
//                        /** 获取语音版验证码成功后的执行动作 */
//                        afterGetVoice(result, data);
//                    }
            }
        };
        SMSSDK.registerEventHandler(handler);
        countDown();
//        }

		/* 注册短信接受Receiver
         * 如被某鹅误报，可去掉此处代码
		 */
        try {
            if (DeviceHelper.getInstance(IdentifyNumPage.this).checkPermission("android.permission.RECEIVE_SMS")) {
                smsReceiver = new SMSReceiver(new SMSSDK.VerifyCodeReadListener() {
                    public void onReadVerifyCode(final String verifyCode) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                etIdentifyNum.setText(verifyCode);
                            }
                        });
                    }
                });
                registerReceiver(smsReceiver, new IntentFilter(
                        "android.provider.Telephony.SMS_RECEIVED"));
            }
        } catch (Throwable t) {
            t.printStackTrace();
            smsReceiver = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onFinish() {
        SMSSDK.unregisterEventHandler(handler);
        if (smsReceiver != null) {
            try {
                unregisterReceiver(smsReceiver);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return onFinish();
    }

    public void runOnUIThread(final Runnable r, long delayMillis) {
        UIHandler.sendEmptyMessageDelayed(0, delayMillis, new Handler.Callback() {
            public boolean handleMessage(Message msg) {
                r.run();
                return false;
            }
        });
    }

    /**
     * 倒数计时
     */
    private void countDown() {
        runOnUIThread(new Runnable() {
            public void run() {
                time--;
                if (time == 0) {
                    int resId = ResHelper.getStringRes(IdentifyNumPage.this,
                            "smssdk_unreceive_identify_code");
                    if (resId > 0) {
                        String unReceive = IdentifyNumPage.this.getString(resId, time);
                        tvUnreceiveIdentify.setText(Html.fromHtml(unReceive));
                    }
                    tvUnreceiveIdentify.setVisibility(View.INVISIBLE);
                    tvUnreceiveIdentify.setEnabled(true);
                    btnSounds.setVisibility(View.GONE);
                    time = RETRY_INTERVAL;
                } else {
                    int resId = ResHelper.getStringRes(IdentifyNumPage.this, "smssdk_receive_msg");
                    if (resId > 0) {
                        String unReceive = IdentifyNumPage.this.getString(resId, time);
                        tvUnreceiveIdentify.setText(Html.fromHtml(unReceive));
                    }
//					if (time == 30){
//						btnSounds.setVisibility(View.VISIBLE);
//					}
                    tvUnreceiveIdentify.setEnabled(false);
                    runOnUIThread(this, 1000);
                }
            }
        }, 1000);
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 如果输入框木有，就隐藏delbtn
        if (s.length() > 0) {
            btnSubmit.setEnabled(true);
            ivClear.setVisibility(View.VISIBLE);
            int resId = ResHelper.getBitmapRes(IdentifyNumPage.this, "smssdk_btn_enable");
            if (resId > 0) {
                btnSubmit.setBackgroundResource(resId);
            }
        } else {
            btnSubmit.setEnabled(false);
            ivClear.setVisibility(View.GONE);
            int resId = ResHelper.getBitmapRes(IdentifyNumPage.this, "smssdk_btn_disenable");
            if (resId > 0) {
                btnSubmit.setBackgroundResource(resId);
            }
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    public void afterTextChanged(Editable s) {
//		btnSounds.setVisibility(View.GONE);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                // 提交验证码
                String verificationCode = etIdentifyNum.getText().toString().trim();
                if (!TextUtils.isEmpty(verificationCode)) {
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    pd = CommonDialog.ProgressDialog(IdentifyNumPage.this);
                    if (pd != null) {
                        pd.show();
                    }

                    RequestUtils.PhoneSendNum(phone, code, verificationCode, new RequestSuccessListener<Integer>() {
                        @Override
                        public void onResponse(Integer result) {
                            if (result == ServiceConstants.REQUEST_RESULT_CODE_OK) {
                                ToastUtils.simpleToast(R.string.smssdk_your_ccount_is_verified);
                                finish();
                            } else if (result == ServiceConstants.REQUEST_CODE_PHONE_HAS_BIND) {
                                ToastUtils.simpleToast(R.string.phone_has_bind);
                            } else {
                                ToastUtils.simpleToast(R.string.smssdk_virificaition_code_wrong);
                            }

                            if (pd != null) {
                                pd.dismiss();
                            }

                        }
                    });

//				SMSSDK.submitVerificationCode(code, phone, verificationCode);
//				btnSubmit.setEnabled(false);
                } else {
                    Toast.makeText(IdentifyNumPage.this, getString(R.string.smssdk_write_identify_code), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_unreceive_identify:
                showDialogType = 1;
                // 没有接收到短信
                showDialog(showDialogType);
                break;
            case R.id.iv_clear:
                etIdentifyNum.getText().clear();
                break;
            case R.id.img_back:
                finish();
                break;
        }
/*        int id = v.getId();
        int idLlBack = ResHelper.getIdRes(activity, "ll_back");
        int idBtnSubmit = ResHelper.getIdRes(IdentifyNumPage.this, "btn_submit");
        int idTvUnreceiveIdentify = ResHelper.getIdRes(IdentifyNumPage.this, "tv_unreceive_identify");
        int idIvClear = ResHelper.getIdRes(IdentifyNumPage.this, "iv_clear");*/
//        int idBtnSounds = ResHelper.getIdRes(activity, "btn_sounds");

//        if (id == idLlBack) {
//            runOnUIThread(new Runnable() {
//                public void run() {
//                    showNotifyDialog();
//                }
//            });
//        } else
/*        if (id == idBtnSubmit) {
            // 提交验证码
            String verificationCode = etIdentifyNum.getText().toString().trim();
            if (!TextUtils.isEmpty(code)) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                pd = CommonDialog.ProgressDialog(IdentifyNumPage.this);
                if (pd != null) {
                    pd.show();
                }

                RequestUtils.PhoneSendNum(phone, code, verificationCode, new RequestSuccessListener<Boolean>() {
                    @Override
                    public void onResponse(Boolean result) {
                        if (result) {
                            ToastUtils.simpleToast(R.string.smssdk_your_ccount_is_verified);
                            finish();
                        } else {
                            ToastUtils.simpleToast(R.string.smssdk_virificaition_code_wrong);
                        }
                    }
                });*/

//				SMSSDK.submitVerificationCode(code, phone, verificationCode);
//				btnSubmit.setEnabled(false);
/*            } else {
                int resId = ResHelper.getStringRes(IdentifyNumPage.this, "smssdk_write_identify_code");
                if (resId > 0) {
                    Toast.makeText(IdentifyNumPage.this, resId, Toast.LENGTH_SHORT).show();
                }
            }
        } else if (id == idTvUnreceiveIdentify) {
            showDialogType = 1;
            // 没有接收到短信
            showDialog(showDialogType);
        } else if (id == idIvClear) {
            etIdentifyNum.getText().clear();
        }*/
//		} else if (id == idBtnSounds) {
//			long time = System.currentTimeMillis();
//			if (time - lastRequestVVTime > MIN_REQUEST_VOICE_VERIFY_INTERVAL) {
//				lastRequestVVTime = time;
//				showDialogType = 2;
//				// 发送语音验证码
//				showDialog(showDialogType);
//			}
//		}
    }

//    /**
//     * 弹出重新发送短信对话框,或发送语音窗口
//     */
//    private void showDialog(int type) {
//        if (type == 1) {
//            int resId = ResHelper.getStyleRes(IdentifyNumPage.this, "ActionToastDialogStyle");
//            if (resId > 0) {
//                final Dialog dialog = new Dialog(IdentifyNumPage.this, resId);
//                TextView tv = new TextView(IdentifyNumPage.this);
//                if (type == 1) {
//                    resId = ResHelper.getStringRes(IdentifyNumPage.this,
//                            "smssdk_resend_identify_code");
//                } else {
//                    resId = ResHelper.getStringRes(IdentifyNumPage.this,
//                            "smssdk_send_sounds_identify_code");
//                }
//                if (resId > 0) {
//                    tv.setText(resId);
//                }
//                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//                resId = ResHelper.getColorRes(IdentifyNumPage.this, "smssdk_white");
//                if (resId > 0) {
//                    tv.setTextColor(IdentifyNumPage.this.getResources().getColor(resId));
//                }
//                int dp10 = ResHelper.dipToPx(IdentifyNumPage.this, 10);
//                tv.setPadding(dp10, dp10, dp10, dp10);
//
//                dialog.setContentView(tv);
//                tv.setOnClickListener(new OnClickListener() {
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        tvUnreceiveIdentify.setEnabled(false);
//
//                        if (pd != null && pd.isShowing()) {
//                            pd.dismiss();
//                        }
//                        pd = CommonDialog.ProgressDialog(IdentifyNumPage.this);
//                        if (pd != null) {
//                            pd.show();
//                        }
//                        // 重新获取验证码短信
//                        SMSSDK.getVerificationCode(code, phone.trim(), TEMP_CODE, null);
//                    }
//                });
//
//                dialog.setCanceledOnTouchOutside(true);
//                dialog.setOnCancelListener(new OnCancelListener() {
//
//                    @Override
//                    public void onCancel(DialogInterface dialog) {
//                        tvUnreceiveIdentify.setEnabled(true);
//                    }
//                });
//                dialog.show();
//            }
//        } else if (type == 2) {
//            int resId = ResHelper.getStyleRes(IdentifyNumPage.this, "ActionToastDialogStyle");
//            if (resId > 0) {
//                final Dialog dialog = new Dialog(IdentifyNumPage.this, resId);
//                LinearLayout layout = SendMsgDialogLayout.create(activity);
//
//                if (layout != null) {
//                    dialog.setContentView(layout);
//
//                    TextView tvTitle = (TextView) dialog.findViewById(ResHelper.getIdRes(activity, "tv_dialog_title"));
//                    resId = ResHelper.getStringRes(IdentifyNumPage.this,
//                            "smssdk_make_sure_send_sounds");
//                    if (resId > 0) {
//                        tvTitle.setText(resId);
//                    }
//
//                    TextView tv = (TextView) dialog.findViewById(ResHelper.getIdRes(activity, "tv_dialog_hint"));
//                    resId = ResHelper.getStringRes(IdentifyNumPage.this,
//                            "smssdk_send_sounds_identify_code");
//                    if (resId > 0) {
//                        String text = IdentifyNumPage.this.getString(resId);
//                        tv.setText(text);
//                    }
//
//                    ((Button) dialog.findViewById(ResHelper.getIdRes(IdentifyNumPage.this, "btn_dialog_ok"))).setOnClickListener(new OnClickListener() {
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                            SMSSDK.getVoiceVerifyCode(code, phone);
//                        }
//                    });
//
//                    ((Button) dialog.findViewById(ResHelper.getIdRes(IdentifyNumPage.this, "btn_dialog_cancel"))).setOnClickListener(new OnClickListener() {
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });
//                    dialog.setCanceledOnTouchOutside(true);
//                    dialog.show();
//                }
//            }
//        }
//
//    }

    /**
     * 提交验证码成功后的执行事件
     *
     * @param result
     * @param data
     */
    private void afterSubmit(final int result, final Object data) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }

                if (result == SMSSDK.RESULT_COMPLETE) {
                    HashMap<String, Object> res = new HashMap<>();
                    res.put("res", true);
                    res.put("page", 2);
                    res.put("phone", data);
                    setResult(res);
                    finish();
                } else {
                    ((Throwable) data).printStackTrace();
                    // 验证码不正确
                    String message = ((Throwable) data).getMessage();
                    int resId = 0;
                    try {
                        JSONObject json = new JSONObject(message);
                        int status = json.getInt("status");
                        resId = ResHelper.getStringRes(IdentifyNumPage.this,
                                "smssdk_error_detail_" + status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (resId == 0) {
                        resId = ResHelper.getStringRes(IdentifyNumPage.this, "smssdk_virificaition_code_wrong");
                    }
                    if (resId > 0) {
                        Toast.makeText(IdentifyNumPage.this, resId, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 获取验证码成功后,的执行动作
     *
     * @param result
     * @param data
     */
    private void afterGet(final int result, final Object data) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }

                if (result == SMSSDK.RESULT_COMPLETE) {
                    int resId = ResHelper.getStringRes(IdentifyNumPage.this,
                            "smssdk_virificaition_code_sent");
                    if (resId > 0) {
                        Toast.makeText(IdentifyNumPage.this, resId, Toast.LENGTH_SHORT).show();
                    }
                    resId = ResHelper.getStringRes(IdentifyNumPage.this, "smssdk_receive_msg");
                    if (resId > 0) {
                        String unReceive = IdentifyNumPage.this.getString(resId, time);
                        tvUnreceiveIdentify.setText(Html.fromHtml(unReceive));
                    }
                    btnSounds.setVisibility(View.GONE);
                    time = RETRY_INTERVAL;
                    countDown();
                } else {
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    // 根据服务器返回的网络错误，给toast提示
                    int status = 0;
                    try {
                        JSONObject object = new JSONObject(throwable.getMessage());
                        String des = object.optString("detail");
                        status = object.optInt("status");
                        if (!TextUtils.isEmpty(des)) {
                            Toast.makeText(IdentifyNumPage.this, des, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e) {
                        SMSLog.getInstance().w(e);
                    }
                    // / 如果木有找到资源，默认提示
                    int resId = 0;
                    if (status >= 400) {
                        resId = ResHelper.getStringRes(IdentifyNumPage.this, "smssdk_error_desc_" + status);
                    } else {
                        resId = ResHelper.getStringRes(IdentifyNumPage.this,
                                "smssdk_network_error");
                    }
                    if (resId > 0) {
                        Toast.makeText(IdentifyNumPage.this, resId, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

//    /**
//     * 获取语音验证码成功后,的执行动作
//     *
//     * @param result
//     * @param data
//     */
//    private void afterGetVoice(final int result, final Object data) {
//        runOnUIThread(new Runnable() {
//            public void run() {
//                if (pd != null && pd.isShowing()) {
//                    pd.dismiss();
//                }
//
//                if (result == SMSSDK.RESULT_COMPLETE) {
//                    int resId = ResHelper.getStringRes(activity, "smssdk_send_sounds_success");
//                    if (resId > 0) {
//                        Toast.makeText(IdentifyNumPage.this, resId, Toast.LENGTH_SHORT).show();
//                    }
//                    btnSounds.setVisibility(View.GONE);
//                } else {
//                    ((Throwable) data).printStackTrace();
//                    Throwable throwable = (Throwable) data;
//                    // 根据服务器返回的网络错误，给toast提示
//                    int status = 0;
//                    try {
//                        JSONObject object = new JSONObject(
//                                throwable.getMessage());
//                        String des = object.optString("detail");
//                        status = object.optInt("status");
//                        if (!TextUtils.isEmpty(des)) {
//                            Toast.makeText(IdentifyNumPage.this, des, Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                    } catch (JSONException e) {
//                        SMSLog.getInstance().w(e);
//                    }
//                    //  如果木有找到资源，默认提示
//                    int resId = 0;
//                    if (status >= 400) {
//                        resId = ResHelper.getStringRes(IdentifyNumPage.this, "smssdk_error_desc_" + status);
//                    } else {
//                        resId = ResHelper.getStringRes(activity,
//                                "smssdk_network_error");
//                    }
//
//                    if (resId > 0) {
//                        Toast.makeText(IdentifyNumPage.this, resId, Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//            }
//        });
//    }

    /**
     * 按返回键时，弹出的提示对话框
     */
    private void showNotifyDialog() {
        int resId = ResHelper.getStyleRes(IdentifyNumPage.this, "ActionToastDialogStyle");
        if (resId > 0) {
            final Dialog dialog = new Dialog(IdentifyNumPage.this, resId);

            LinearLayout layout = BackVerifyDialogLayout.create(IdentifyNumPage.this);

            if (layout != null) {
                dialog.setContentView(layout);

                resId = ResHelper.getIdRes(IdentifyNumPage.this, "tv_dialog_hint");
                TextView tv = (TextView) dialog.findViewById(resId);
                resId = ResHelper.getStringRes(IdentifyNumPage.this,
                        "smssdk_close_identify_page_dialog");
                if (resId > 0) {
                    tv.setText(resId);
                }
                resId = ResHelper.getIdRes(IdentifyNumPage.this, "btn_dialog_ok");
                Button waitBtn = (Button) dialog.findViewById(resId);
                resId = ResHelper.getStringRes(IdentifyNumPage.this, "smssdk_wait");
                if (resId > 0) {
                    waitBtn.setText(resId);
                }
                waitBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                resId = ResHelper.getIdRes(IdentifyNumPage.this, "btn_dialog_cancel");
                Button backBtn = (Button) dialog.findViewById(resId);
                resId = ResHelper.getStringRes(IdentifyNumPage.this, "smssdk_back");
                if (resId > 0) {
                    backBtn.setText(resId);
                }
                backBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        }
    }

    public boolean onKeyEvent(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            runOnUiThread(new Runnable() {
                public void run() {
                    showNotifyDialog();
                }
            });
            return true;
        } else {
            return false;
        }
    }

}
