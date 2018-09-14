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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.tools.utils.ResHelper;

import org.json.JSONObject;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.UserInterruptException;
import cn.smssdk.utils.SMSLog;
import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.view.BaseActivity;
import onemessageui.dialog.DialogUtil;

import static com.mob.MobSDK.getContext;

/**
 * 短信注册页面
 */
public class RegisterPage extends BaseActivity implements OnClickListener,
        TextWatcher {

    // 默认使用中国区号
    private static final String DEFAULT_COUNTRY_ID = "42";

    private EventHandler callback;

    // 国家
    private TextView tvCountry;
    // 手机号码
    private EditText etPhoneNum;
    // 国家编号
    private TextView tvCountryNum;
    // clear 号码
    private ImageView ivClear;
    // 下一步按钮
    private Button btnNext;

    private String currentId;
    private String currentCode;
    private EventHandler handler;
    private Dialog pd;
    private OnSendMessageHandler osmHandler;

    public static final int INTENT_COUNTRY = 1;
    private LinearLayout viewCountry;
    private ImageView img_back;
    private TextView txt_title;

    public void setRegisterCallback(EventHandler callback) {
        this.callback = callback;
    }

    public void setOnSendMessageHandler(OnSendMessageHandler h) {
        osmHandler = h;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page_layout);
        currentId = DEFAULT_COUNTRY_ID;

        //同步通讯录
//        BaseUtils.AddUserContacts(this, false);

//			View llBack = activity.findViewById(ResHelper.getIdRes(activity, "ll_back"));
//			TextView tv = (TextView) findViewById(R.id.tv_title);
//			int resId = ResHelper.getStringRes(activity, "smssdk_regist");
//			if (resId > 0) {
//				tv.setText(resId);
//			}

        viewCountry = (LinearLayout) findViewById(R.id.rl_country);
        btnNext = (Button) findViewById(R.id.btn_next);
        tvCountry = (TextView) findViewById(R.id.tv_country);

        String[] country = getCurrentCountry();
        // String[] country = SMSSDK.getCountry(currentId);
        if (country != null) {
            currentCode = country[1];
            tvCountry.setText(country[0]);
        }

        tvCountryNum = (TextView) findViewById(R.id.tv_country_num);
        tvCountryNum.setText("+" + currentCode);

        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);

        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(R.string.user_tel_code);

        etPhoneNum = (EditText) findViewById(R.id.et_write_phone);
        etPhoneNum.setText("");
        etPhoneNum.addTextChangedListener(this);
        etPhoneNum.requestFocus();
        if (etPhoneNum.getText().length() > 0) {
            btnNext.setEnabled(true);

            ivClear = (ImageView) findViewById(R.id.iv_clear);
            ivClear.setVisibility(View.VISIBLE);
//				resId = ResHelper.getBitmapRes(activity, "smssdk_btn_enable");
//				if (resId > 0) {
//					btnNext.setBackgroundResource(resId);
//				}
        }

        ivClear = (ImageView) findViewById(R.id.iv_clear);

//			llBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        ivClear.setOnClickListener(this);
        viewCountry.setOnClickListener(this);
        img_back.setOnClickListener(this);

        handler = new EventHandler() {
            public void afterEvent(final int event, final int result,
                                   final Object data) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                // 请求验证码后，跳转到验证码填写页面
                                boolean smart = (Boolean) data;
                                afterVerificationCodeRequested(smart);
                                finish();
                            }
                        } else {
                            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE
                                    && data != null
                                    && (data instanceof UserInterruptException)) {
                                // 由于此处是开发者自己决定要中断发送的，因此什么都不用做
                                return;
                            }

                            int status = 0;
                            // 根据服务器返回的网络错误，给toast提示
                            try {
                                ((Throwable) data).printStackTrace();
                                Throwable throwable = (Throwable) data;

                                JSONObject object = new JSONObject(
                                        throwable.getMessage());
                                String des = object.optString("detail");
                                status = object.optInt("status");
                                if (!TextUtils.isEmpty(des)) {
                                    Toast.makeText(getBaseContext(), des, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (Exception e) {
                                SMSLog.getInstance().w(e);
                            }
                            // 如果木有找到资源，默认提示
                            int resId = 0;
                            if (status >= 400) {
                                resId = ResHelper.getStringRes(getBaseContext(), "smssdk_error_desc_" + status);
                            } else {
                                resId = ResHelper.getStringRes(getBaseContext(),
                                        "smssdk_network_error");
                            }

                            if (resId > 0) {
                                Toast.makeText(getBaseContext(), resId, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        };

    }

    private String[] getCurrentCountry() {
        String mcc = getMCC();
        String[] country = null;
        if (!TextUtils.isEmpty(mcc)) {
            country = SMSSDK.getCountryByMCC(mcc);
        }

        if (country == null) {
            SMSLog.getInstance().d("no country found by MCC: " + mcc);
            country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);
        }
        return country;
    }

    private String getMCC() {
        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        // 返回当前手机注册的网络运营商所在国家的MCC+MNC. 如果没注册到网络就为空.
        String networkOperator = tm.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            return networkOperator;
        }

        // 返回SIM卡运营商所在国家的MCC+MNC. 5位或6位. 如果没有SIM卡返回空
        return tm.getSimOperator();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SMSSDK.registerEventHandler(handler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(handler);
    }

    //	public void onResume() {
//		SMSSDK.registerEventHandler(handler);
//    }

//	public void onDestroy() {
//		SMSSDK.unregisterEventHandler(handler);
//	}

    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            btnNext.setEnabled(true);
            ivClear.setVisibility(View.VISIBLE);
            int resId = ResHelper.getBitmapRes(getBaseContext(), "smssdk_btn_enable");
            if (resId > 0) {
                btnNext.setBackgroundResource(resId);
            }
        } else {
            btnNext.setEnabled(false);
            ivClear.setVisibility(View.GONE);
            int resId = ResHelper.getBitmapRes(getBaseContext(), "smssdk_btn_disenable");
            if (resId > 0) {
                btnNext.setBackgroundResource(resId);
            }
        }
    }

    public void afterTextChanged(Editable s) {

    }

    public void onClick(View v) {
//        int id = v.getId();
//		int idLlBack = ResHelper.getIdRes(activity, "ll_back");
//        int idRlCountry = ResHelper.getIdRes(getBaseContext(), "rl_country");
//        int idBtnNext = ResHelper.getIdRes(getBaseContext(), "btn_next");
//        int idIvClear = ResHelper.getIdRes(getBaseContext(), "iv_clear");
        switch (v.getId()) {
            case R.id.rl_country:
                // 国家列表
                Intent intent = new Intent(this, CountryPage.class);
                intent.putExtra("currentId", currentId);
                startActivityForResult(intent, INTENT_COUNTRY);
                break;
            case R.id.btn_next:
                // 请求发送短信验证码
                String phone = etPhoneNum.getText().toString().trim().replaceAll("\\s*", "");
                String code = tvCountryNum.getText().toString().trim();

                showDialog(phone, code);
                break;
            case R.id.iv_clear:
                // 清除电话号码输入框
                etPhoneNum.getText().clear();
                break;
            case R.id.img_back:
                finish();
                break;
        }
//		if (id == idLlBack) {
//			finish();
//		} else
//        if (id == idRlCountry) {
        // 国家列表
//			CountryPage countryPage = new CountryPage();
//			countryPage.setCountryId(currentId);
        //			countryPage.showForResult(getContext(), null, this);
//            Intent intent = new Intent(this, CountryPage.class);
//            intent.putExtra("currentId", currentId);
//            startActivityForResult(intent, INTENT_COUNTRY);
//        } else if (id == idBtnNext) {
//            // 请求发送短信验证码
//            String phone = etPhoneNum.getText().toString().trim().replaceAll("\\s*", "");
//            String code = tvCountryNum.getText().toString().trim();
//
//            showDialog(phone, code);
//        } else if (id == idIvClear) {
//            // 清除电话号码输入框
//            etPhoneNum.getText().clear();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_COUNTRY && resultCode == 4) {
// 国家列表返回
            currentId = (String) data.getStringExtra("id");
            String[] country = SMSSDK.getCountry(currentId);
            if (country != null) {
                currentCode = country[1];
                tvCountryNum.setText("+" + currentCode);
                tvCountry.setText(country[0]);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void onResult(HashMap<String, Object> data) {
        if (data != null) {
            int page = (Integer) data.get("page");
            if (page == 1) {
                // 国家列表返回
                currentId = (String) data.get("id");
                String[] country = SMSSDK.getCountry(currentId);
                if (country != null) {
                    currentCode = country[1];
                    tvCountryNum.setText("+" + currentCode);
                    tvCountry.setText(country[0]);
                }
            } else if (page == 2) {
                // 验证码校验返回
                Object res = data.get("res");
                //Object smart = data.get("smart");

                HashMap<String, Object> phoneMap = (HashMap<String, Object>) data.get("phone");
                if (res != null && phoneMap != null) {
                    int resId = ResHelper.getStringRes(getContext(), "smssdk_your_ccount_is_verified");
                    if (resId > 0) {
                        Toast.makeText(getBaseContext(), resId, Toast.LENGTH_SHORT).show();
                    }

                    if (callback != null) {
                        callback.afterEvent(
                                SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE,
                                SMSSDK.RESULT_COMPLETE, phoneMap);
                    }
                    finish();
                }
            }
        }
    }

    /**
     * 分割电话号码
     */
    private String splitPhoneNum(String phone) {
        StringBuilder builder = new StringBuilder(phone);
        builder.reverse();
        for (int i = 4, len = builder.length(); i < len; i += 5) {
            builder.insert(i, ' ');
        }
        builder.reverse();
        return builder.toString();
    }

    /**
     * 是否请求发送验证码，对话框
     */
    public void showDialog(final String phone, final String code) {
        DialogUtil.simpleDialog(this, getString(R.string.smssdk_make_sure_mobile_detail) + phone, new DialogUtil.ConfirmCallBackInf() {
            @Override
            public void onConfirmClick(String content) {
                SMSSDK.getVerificationCode(code, phone.trim(), null, osmHandler);
            }
        });
//        int resId = ResHelper.getStyleRes(getContext(), "ActionToastDialogStyle");
//        if (resId > 0) {
//            final String phoneNum = code + " " + splitPhoneNum(phone);
//            final Dialog dialog = new Dialog(getContext(), resId);
//
//            LinearLayout layout = SendMsgDialogLayout.create(getContext());
//
//            if (layout != null) {
//                dialog.setContentView(layout);
//
//                ((TextView) dialog.findViewById(R.id.tv_phone)).setText(phoneNum);
//                TextView tv = (TextView) dialog.findViewById(R.id.tv_dialog_hint);
//                resId = ResHelper.getStringRes(getBaseContext(), "smssdk_make_sure_mobile_detail");
//                if (resId > 0) {
//                    String text = getContext().getString(resId);
//
//                    tv.setText(Html.fromHtml(text));
//                }
//
//                ((Button) dialog.findViewById(ResHelper.getIdRes(getContext(), "btn_dialog_ok"))).setOnClickListener(
//                        new OnClickListener() {
//                            public void onClick(View v) {
//                                // 跳转到验证码页面
//                                dialog.dismiss();
//
//                                if (pd != null && pd.isShowing()) {
//                                    pd.dismiss();
//                                }
//                                pd = CommonDialog.ProgressDialog(getContext());
//                                if (pd != null) {
//                                    pd.show();
//                                }
//                                SMSLog.getInstance().i("verification phone ==>>" + phone);
//                                SMSLog.getInstance().i("verification tempCode ==>>" + IdentifyNumPage.TEMP_CODE);
//                                SMSSDK.getVerificationCode(code, phone.trim(), null, osmHandler);
//                            }
//                        });
//
//
//                ((Button) dialog.findViewById(ResHelper.getIdRes(getContext(), "btn_dialog_cancel"))).setOnClickListener(
//                        new OnClickListener() {
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                            }
//                        });
//                dialog.setCanceledOnTouchOutside(true);
//                dialog.show();
//            }
//        }
    }

    /**
     * 请求验证码后，跳转到验证码填写页面
     */
    private void afterVerificationCodeRequested(boolean smart) {
        String phone = etPhoneNum.getText().toString().trim().replaceAll("\\s*", "");
        String code = tvCountryNum.getText().toString().trim();
        if (code.startsWith("+")) {
            code = code.substring(1);
        }
        String formatedPhone = "+" + code + " " + splitPhoneNum(phone);

        // 验证码页面
        if (smart) {
//			SmartVerifyPage smartPage = new SmartVerifyPage();
//			smartPage.setPhone(phone, code, formatedPhone);
//			smartPage.showForResult(getBaseContext(), null, this);
        } else {
            Intent intent = new Intent(this, IdentifyNumPage.class);
            intent.putExtra("phone", phone);
            intent.putExtra("code", code);
            intent.putExtra("formatedPhone", formatedPhone);
            startActivity(intent);
//			IdentifyNumPage page = new IdentifyNumPage();
//			page.setPhone(phone, code, formatedPhone);
//			page.showForResult(getBaseContext(), null, this);
        }
    }

}
