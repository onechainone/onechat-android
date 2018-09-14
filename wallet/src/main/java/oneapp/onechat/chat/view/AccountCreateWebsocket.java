package oneapp.onechat.chat.view;

import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.utils.jump.JumpAppPageUtil;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.interfaces.RegisterCallBack;
import oneapp.onechat.oneandroid.graphenechain.interfaces.AccountDelegate;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.WeakHandler;
import onewalletui.ui.BaseActivity;

//import oneapp.graphenechain.utils.TinyDB;
//import oneapp.onechat.androidapp.ui.SetPasswordFragment;

public class AccountCreateWebsocket {

    private static final int RESTORE_STATUS_UPDATE = 0;
    private static final int RESTORE_FINISHED = 1;

    private String chatServiceNode;

    private final String TAG = this.getClass().getName();
//    TinyDB tinyDB;

    //    Boolean settingScreen = false;
    Boolean validAccount = false;
    Boolean checkingValidation = false;

    Gson gson;

    String mAddress;
    String wifPrivKey;
    String brainPrivKey;
    Boolean hasNumber;

    private TextView tvErrorAccountName;
    private EditText etAccountName;
    private EditText etNickname;
    private EditText etPin;
    private EditText etPinConfirmation;
    private EditText etReferrerAccount;
    private Button btn_register;

    private String mAccountName = "";
    private String mNickname = "";
    private String mSex = "";

    private final Handler handler = new MyHandler(this);

    BaseActivity m_pContext = null;

    //    @Override
    public void onCreate(BaseActivity appContext, EditText accountName, EditText nickname, TextView errorAccountName,
                         EditText password1, EditText password2, EditText et_referrer_account, Button btn_register,
                         AccountDelegate listener) {

        m_pContext = appContext;
        etAccountName = accountName;
        etNickname = nickname;
        tvErrorAccountName = errorAccountName;
        etPin = password1;
        etPinConfirmation = password2;
        this.btn_register = btn_register;
        etReferrerAccount = et_referrer_account;

        gson = new Gson();

        chatServiceNode = ServiceConstants.GetServerBeanByKey(ServiceConstants.SERVICE_CHAT_KEY, false, true).getService_uuid();
        hasNumber = true;
    }

    Boolean checkLastIndex() {
        String name = etAccountName.getText().toString();
        String lastWord = String.valueOf(name.charAt(name.length() - 1));
        return lastWord.equals("-");
    }


    public void create(String strBrainkey, String accountName, String nickname, String sex) {
        if (checkingValidation) {
            Toast.makeText(m_pContext.getApplicationContext(), R.string.validation_in_progress, Toast.LENGTH_SHORT).show();
        } else if (etAccountName.getText().toString().length() == 0) {
            Toast.makeText(m_pContext.getApplicationContext(), R.string.kindly_create_account, Toast.LENGTH_SHORT).show();
        } else if (etAccountName.getText().toString().length() < CommonConstants.MIN_LENGTH_ACCOUNT_NAME) {
            Toast.makeText(m_pContext.getApplicationContext(), R.string.account_name_should_be_longer, Toast.LENGTH_SHORT).show();
            tvErrorAccountName.setVisibility(View.VISIBLE);
            tvErrorAccountName.setText(R.string.account_name_should_be_longer);
            tvErrorAccountName.setTextColor(ContextCompat.getColor(m_pContext, R.color.base_color));
        } else if (checkLastIndex()) {
            tvErrorAccountName.setVisibility(View.VISIBLE);
            tvErrorAccountName.setText(R.string.last_letter_cannot);
            tvErrorAccountName.setTextColor(ContextCompat.getColor(m_pContext, R.color.base_color));
            // FIXME: 2017/8/10
//        } else if (!checkHyphen()) {
//            tvErrorAccountName.setVisibility(View.VISIBLE);
//            tvErrorAccountName.setText(R.string.account_name_must_include_dash_and_a_number);
//        } else if (!containsDigit(etAccountName.getText().toString())) {
//            tvErrorAccountName.setVisibility(View.VISIBLE);
//            tvErrorAccountName.setText(R.string.account_name_must_include_dash_and_a_number);
        } else {
            if (etPin.getText().length() == 0) {
                Toast.makeText(m_pContext.getApplicationContext(), R.string.please_enter_6_digit_pin, Toast.LENGTH_SHORT).show();
            }
            //PIN must have minimum of 6-digit
            else if (etPin.getText().length() < 6) {
                Toast.makeText(m_pContext.getApplicationContext(), R.string.pin_number_warning, Toast.LENGTH_SHORT).show();
            } else if (!etPinConfirmation.getText().toString().equals(etPin.getText().toString())) {
                Toast.makeText(m_pContext.getApplicationContext(), R.string.mismatch_pin, Toast.LENGTH_SHORT).show();
            } else {
//                if (validAccount) {
//                    if (!checkingValidation) {
//                        showDialog("", "");
//                        generateKeys(strBrainkey);
//                    }
//                } else {
//                    Log.d(TAG, "Not a valid account");
//                    Toast.makeText(m_pContext, m_pContext.getResources().getString(R.string.error_invalid_account), Toast.LENGTH_SHORT).show();
//                }

                String strTitle = m_pContext.getResources().getString(R.string.accountname_create_title);
                String strLoading = m_pContext.getResources().getString(R.string.accountname_create_loading);

                if (StringUtils.equalsNull(mAccountName) || !mAccountName.equals(accountName)) {
                }
                mAccountName = accountName;
                mNickname = nickname;
                mSex = sex;
                    m_pContext.showLoadingDialog();
                    //邀请人accountName
                    String referrerAccountName = etReferrerAccount.getText().toString();

                    onewalletui.util.jump.JumpAppPageUtil.lastJumpSignPageTime = System.currentTimeMillis() + onewalletui.util.jump.JumpAppPageUtil.NOT_JUMP_CHECK_PAGE;
                    OneAccountHelper.createAccount("",etAccountName.getText().toString(), nickname, sex, referrerAccountName, etPin.getText().toString(), new RegisterCallBack() {
                        @Override
                        public void onRegister(final boolean ifSuccess, final int code, String msg) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    m_pContext.hideLoadingDialog();
                                    if (ifSuccess) {
                                        m_pContext.finish();
                                        JumpAppPageUtil.jumpMainPage(m_pContext);
                                    } else if (ServiceConstants.REGISTER_REQUEST_CODE_STRING.containsKey(code)) {
                                        Toast.makeText(m_pContext.getApplicationContext(), chatServiceNode + m_pContext.getString(ServiceConstants.REGISTER_REQUEST_CODE_STRING.get(code)), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(m_pContext.getApplicationContext(), chatServiceNode + m_pContext.getString(R.string.sync_account_fail), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
        }
    }


    private static class MyHandler extends WeakHandler<AccountCreateWebsocket> {
        public MyHandler(AccountCreateWebsocket ref) {
            super(ref);
        }

        @Override
        protected void weakHandleMessage(AccountCreateWebsocket ref, Message msg) {
        }
    }

}
