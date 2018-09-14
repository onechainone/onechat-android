package oneapp.onechat.chat.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.HashMap;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.WalletApplication;
import oneapp.onechat.chat.utils.jump.JumpAppOutUtil;
import oneapp.onechat.chat.utils.jump.JumpAppPageUtil;
import oneapp.onechat.chat.utils.jump.JumpParamsContants;
import oneapp.onechat.oneandroid.graphenechain.interfaces.AccountDelegate;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsApplication;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onechat.oneandroid.onewallet.util.UserInfoUtils;
import oneapp.onechat.oneandroid.onewallet.util.permission.EasyPermissions;
import onemessageui.view.OneBaseActivity;

//import oneapp.graphenechain.smartcoinswallet.AccountCreate;

//注册
public class AccountCreateActivity extends OneBaseActivity implements OnClickListener, AccountDelegate {
    private static final int RESTORE_STATUS_UPDATE = 0;
    private static final int RESTORE_FINISHED = 1;

    private TextView txt_title, languageTv;
    private ImageView img_back;
    private Button btn_register;
    private Button btn_account_restore;
    private EditText et_usertel, et_nickname, et_password, et_password2, et_referrer_account;
    private TextView tv_error_accountname, tv_password2_erro, tv_password_erro;
    private ImageView mManIv, mWomenIv;

    private TextView create_status;


    //用户协议
//    private CheckBox mUserContractCb;
    private ImageView mUserContractCb;

    private AccountCreateWebsocket accountCreate = null;

    private String mBrainKey = "";
    private String mSex = UserInfoUtils.USER_SEX_MAN;
    private String mNickname = "";
    private String mPassword = "";
    private String mAccountName = "";
    private boolean isAgreeContract = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        ifUnlockThisActivity = false;

        super.onCreate(savedInstanceState);

        checkPermission(new CheckPermListener() {
            @Override
            public void superPermission() {

            }
        }, R.string.file, Manifest.permission.READ_PHONE_STATE);

        try {
//            WalletApplication.getInstance().initWalletApp();

            accountCreate = new AccountCreateWebsocket();

            accountCreate.onCreate(this, et_usertel, et_nickname, tv_error_accountname,
                    et_password, et_password2, et_referrer_account, btn_register,
                    this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void start() {
        readArguments();
        setContentView(R.layout.activity_account_create);

    }

    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(R.string.accountname_create_title);
        languageTv = (TextView) findViewById(R.id.txt_right);
        languageTv.setVisibility(View.VISIBLE);
        languageTv.setText("language");

        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);

        btn_register = (Button) findViewById(R.id.btn_register);
        btn_account_restore = (Button) findViewById(R.id.btn_account_restore);

        et_usertel = (EditText) findViewById(R.id.et_usertel);
        tv_error_accountname = (TextView) findViewById(R.id.tv_error_accountname);
        tv_password2_erro = (TextView) findViewById(R.id.tv_password2_erro);
        tv_password_erro = (TextView) findViewById(R.id.tv_password_erro);

        et_nickname = (EditText) findViewById(R.id.et_nickname);

        mManIv = (ImageView) findViewById(R.id.iv_man);
        mWomenIv = (ImageView) findViewById(R.id.iv_women);

        et_password = (EditText) findViewById(R.id.et_password);
        et_password2 = (EditText) findViewById(R.id.et_password2);

        et_referrer_account = (EditText) findViewById(R.id.et_referrer_account);

        create_status = (TextView) findViewById(R.id.create_status);

        mUserContractCb = (ImageView) findViewById(R.id.rb_user_contract);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {

    }

    private void readArguments() {
        final Intent intent = this.getIntent();
        final Serializable sear = intent
                .getSerializableExtra(JumpParamsContants.INTENT_PARAMS_MAP);
        if (sear != null) {
            final HashMap<String, Object> paramMap = (HashMap<String, Object>) sear;
            mBrainKey = (String) paramMap.get(JumpParamsContants.INTENT_SEED);
        }
    }

    @Override
    protected void setListener() {
        img_back.setOnClickListener(this);
        languageTv.setOnClickListener(this);
        //hhrbtn_send.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_account_restore.setOnClickListener(this);
        et_usertel.addTextChangedListener(new TextChange());
        et_password.addTextChangedListener(new TextChange());
        et_password2.addTextChangedListener(new TextChange());
        et_referrer_account.addTextChangedListener(new TextChange());

        mWomenIv.setOnClickListener(this);
        mManIv.setOnClickListener(this);
        mUserContractCb.setOnClickListener(this);
//        mUserContractCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                checkBtnEnable();
//            }
//        });

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {//                SharePreferenceUtils.putObject(SharePreferenceUtils.SP_FIRST_SEED,true);
            activityFinish();

        } else if (i == R.id.txt_right) {
            JumpAppPageUtil.jumpSelectLanguragePage(context);

        } else if (i == R.id.btn_register) {
            if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_PHONE_STATE)) {
                new AlertDialog.Builder(context)
                        .setMessage(getString(R.string.pls_open_device_permission))
                        .setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JumpAppOutUtil.startAppSettingsScreen(context);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getRegister();
                            }
                        })
                        .create().show();
            } else {
                getRegister();
            }

        } else if (i == R.id.btn_account_restore) {
            accountRestore();

        } else if (i == R.id.iv_man) {
            mManIv.setImageResource(R.drawable.rb_set_sex_selected);
            mWomenIv.setImageResource(R.drawable.rb_set_sex_normal);
            mSex = UserInfoUtils.USER_SEX_MAN;

        } else if (i == R.id.iv_women) {
            mWomenIv.setImageResource(R.drawable.rb_set_sex_selected);
            mManIv.setImageResource(R.drawable.rb_set_sex_normal);
            mSex = UserInfoUtils.USER_SEX_WOMAN;

        } else if (i == R.id.rb_user_contract) {
            if (!isAgreeContract) {
                mUserContractCb.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton_select));
            } else {
                mUserContractCb.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton_normal));
            }
            isAgreeContract = !isAgreeContract;
            checkBtnEnable();

        } else {
        }
    }

    private void activityFinish() {
        finish();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activityFinish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void accountRestore() {
        JumpAppPageUtil.jumpAccountRestorePage(context);
    }

    private void getRegister() {
        final String name = et_usertel.getText().toString();
        final String nickname = et_nickname.getText().toString();
        final String pwd = et_password.getText().toString();
        final String pwd2 = et_password2.getText().toString();
        final String invitationCode = et_referrer_account.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)
                || TextUtils.isEmpty(pwd2)) {
            Utils.showLongToast(AccountCreateActivity.this, "请填写核心信息！");
            return;
        }
        if (Character.isDigit(name.charAt(0))) {
            ToastUtils.simpleToast(R.string.account_name_cannot_number_start);
            return;
        }

        if (TextUtils.isEmpty(nickname)) {
            Utils.showLongToast(AccountCreateActivity.this, "请填写核心信息！");
            return;
        }

//        if (TextUtils.isEmpty(invitationCode)) {
//            Utils.showLongToast(AccountCreateActivity.this, "请填写核心信息！");
//            return;
//        }

        mAccountName = name;
//        mBrainKey = generateNewMnemonic();
        mNickname = nickname;
        mPassword = pwd;

        // 检查账户是否存在，如果存在，则先删除
        if (!BtsHelper.mIsHasAccount) {
            createAccount();
        } else {
            new AlertDialog.Builder(this).setTitle(R.string.override_wallet_warning_title)
                    .setMessage(R.string.override_new_wallet_warning_message)
                    .setNegativeButton(R.string.button_cancel, null)
                    .setPositiveButton(R.string.button_confirm, new DeleteAccountConfirm()).create().show();
        }
    }

    public void createAccount() {
        try {
            btn_register.setBackgroundResource(R.color.base_color_transparent);
            btn_register.setEnabled(false);

            // 每次生成新的，避免重复的私钥
            create_status.setText(getString(R.string.wallet_restoration_master_key));
            accountCreate.create(mBrainKey, mAccountName, mNickname, mSex);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    class DeleteAccountConfirm implements DialogInterface.OnClickListener {
        DeleteAccountConfirm() {
        }

        public void onClick(DialogInterface dialog, int which) {
            try {

                BtsHelper.deleteWallet();

                createAccount();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // EditText监听器
    class TextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before,
                                  int count) {
            checkBtnEnable();
        }
    }

    /**
     * 检查按钮是否可点击
     */
    void checkBtnEnable() {
        boolean Sign2 = et_password.getText().length() >= CommonConstants.MIN_LENGTH_PASSWORD;
        boolean Sign3 = et_password2.getText().length() >= CommonConstants.MIN_LENGTH_PASSWORD;
        boolean Sign4 = et_password2.getText().toString().equals(et_password.getText().toString());
        boolean tel = et_usertel.getText().length() >= CommonConstants.MIN_LENGTH_ACCOUNT_NAME;
        boolean Sign5 = et_referrer_account.getText().length() > CommonConstants.MIN_LENGTH_INVITA_CODE;
        boolean agreeContract = isAgreeContract;

        if (!tel && et_usertel.getText().length() > 0) {
            tv_error_accountname.setVisibility(View.VISIBLE);
            tv_error_accountname.setText(getString(R.string.account_name_length_erro));
        } else {
            tv_error_accountname.setVisibility(View.GONE);
            tv_error_accountname.setText("");
        }
        if (!Sign2 && et_password.getText().length() > 0) {
            tv_password_erro.setVisibility(View.VISIBLE);
            tv_password_erro.setText(getString(R.string.password_length_erro));
        } else {
            tv_password_erro.setVisibility(View.GONE);
            tv_password_erro.setText("");
        }

        if (!Sign4 && et_password2.getText().length() > 0) {
            tv_password2_erro.setVisibility(View.VISIBLE);
            tv_password2_erro.setText(getString(R.string.password_second_erro));
        } else {
            tv_password2_erro.setVisibility(View.GONE);
            tv_password2_erro.setText("");
        }

//        && Sign5
        if (Sign2 && Sign3 && tel && agreeContract && Sign4) {
            btn_register.setBackgroundResource(R.color.base_color);
            btn_register.setEnabled(true);
        } else {
            btn_register.setBackgroundResource(R.color.base_color_transparent);
            btn_register.setEnabled(false);
        }
    }

    @Override
    public void onPasswordSet(Bundle args) {
        try {
            BtsApplication.getInstance().shortWebsocketStart();

            WalletApplication.getInstance().sendcastForDataUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
