package oneapp.onechat.chat.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Charsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.utils.jump.JumpAppPageUtil;
import oneapp.onechat.chat.utils.jump.JumpParamsContants;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.interfaces.RecoverCallBack;
import oneapp.onechat.oneandroid.graphenechain.models.AccountDetails;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.NetUtils;
import oneapp.onechat.oneandroid.onemessage.common.ToolsLog;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onecore.graphenej.Util;
import onemessageui.dialog.DialogUtil;
import onemessageui.view.OneBaseActivity;

//import oneapp.graphenechain.smartcoinswallet.BackupBrainkeyActivity;
//import oneapp.graphenechain.smartcoinswallet.TabActivity;

//注册
public class AccountRestoreActivity extends OneBaseActivity implements OnClickListener {
    private final String TAG = this.getClass().getName();
    private static final Logger log = LoggerFactory.getLogger(AccountRestoreActivity.class);

    private TextView txt_title, txt_right;
    private ImageView img_back;
    private Button btn_ok;
    private Button btn_account_create;

    //用户协议
//    private CheckBox mUserContractCb;
    private ImageView mUserContractCb;

    private TextView et_brainkey;
    private EditText et_password, et_password2, et_encrypt_psw;

    private TextView tv_brainkey_error, tv_password2_erro, tv_password_erro;

    private TextView restoration_status;

    String seed;

    String mBrainKey = "";
    String mPassword = "";
    String mPassword2 = "";

    private int mFrom = Constants.FROM_OTHER;

    private String mChatNodeUid;
    private TextView brainkey_recover;
    private int mRecoverType = Constants.RECOVER_TYPE_BRANIKEY;
    private TextView pass_brainkey_recover;
    private View brainkey_recover_view, pass_brainkey_recover_view;
    private LinearLayout pass_brainkey_ll;
    private ScrollView brainkey_sv;
    private TextView bt_start_recover;
    private TextView tv_choose_file;
    private String file_path, file_content_string;
    private Boolean isAgreeContract = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {

            ifUnlockThisActivity = false;

            super.onCreate(savedInstanceState);

//            WalletApplication.getInstance().initWalletApp();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_account_restore);
    }

    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(R.string.accountname_restore_title);
        img_back = (ImageView) findViewById(R.id.img_back);
        if (mFrom == Constants.FROM_OTHER) {
            img_back.setVisibility(View.VISIBLE);
        } else {
            img_back.setVisibility(View.VISIBLE);
        }

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_account_create = (Button) findViewById(R.id.btn_account_create);
        et_brainkey = (TextView) findViewById(R.id.et_brainkey);
        et_password = (EditText) findViewById(R.id.et_password);
        et_password2 = (EditText) findViewById(R.id.et_password2);
        et_encrypt_psw = (EditText) findViewById(R.id.et_encrypt_password);

        tv_brainkey_error = (TextView) findViewById(R.id.tv_brainkey_erro);
        tv_password2_erro = (TextView) findViewById(R.id.tv_password2_erro);
        tv_password_erro = (TextView) findViewById(R.id.tv_password_erro);

        restoration_status = (TextView) findViewById(R.id.restoration_status);

//        mUserContractCb = (CheckBox) findViewById(R.id.rb_user_contract);
        mUserContractCb = (ImageView) findViewById(R.id.rb_user_contract);
        //加密验证新控件
        brainkey_recover = (TextView) findViewById(R.id.brainkey_recover);
        pass_brainkey_recover = (TextView) findViewById(R.id.pass_brainkey_recover);

        brainkey_recover_view = findViewById(R.id.view_brainkey_recover);
        pass_brainkey_recover_view = findViewById(R.id.view_pass_brainkey_recover);

        pass_brainkey_ll = (LinearLayout) findViewById(R.id.pass_brainkey_ll);
        brainkey_sv = (ScrollView) findViewById(R.id.brainkey_sv);
        bt_start_recover = (TextView) findViewById(R.id.bt_start_recover);
        tv_choose_file = (TextView) findViewById(R.id.tv_choose_file);
        txt_right = (TextView) findViewById(R.id.txt_right);
        txt_right.setVisibility(View.VISIBLE);
        txt_right.setText(R.string.switch_service_node);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
//        checkedSeed();
        mChatNodeUid = ServiceConstants.GetServerBeanByKey(ServiceConstants.SERVICE_CHAT_KEY, false, true).getService_uuid();
    }

    private void checkedSeed() {
        Intent data = getIntent();
        String seed = data.getStringExtra(JumpParamsContants.INTENT_SEED);
        if (!StringUtils.equalsNull(seed))
            et_brainkey.setText(seed);
        checkBtnEnable();
    }

    @Override
    protected void setListener() {
        img_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_account_create.setOnClickListener(this);
        et_brainkey.addTextChangedListener(new TextChange());
        et_brainkey.setOnClickListener(this);
        et_password.addTextChangedListener(new TextChange());
        et_password2.addTextChangedListener(new TextChange());
        et_encrypt_psw.addTextChangedListener(new TextChange2());
        mUserContractCb.setOnClickListener(this);
        txt_right.setOnClickListener(this);
//        mUserContractCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                checkBtnEnable();
//            }
//        });

        //加密验证新控件
        brainkey_recover.setOnClickListener(this);
        pass_brainkey_recover.setOnClickListener(this);
        bt_start_recover.setOnClickListener(this);
        tv_choose_file.setOnClickListener(this);
    }

    /**
     * 检查按钮是否可点击
     */
    void checkBtnEnable() {
        boolean Sign1 = et_brainkey.getText().length() > 0;
        boolean Sign2 = et_password.getText().length() >= CommonConstants.MIN_LENGTH_PASSWORD;
        boolean Sign3 = et_password2.getText().length() >= CommonConstants.MIN_LENGTH_PASSWORD;
        boolean agreeContract = isAgreeContract;
        boolean Sign4 = et_password2.getText().toString().equals(et_password.getText().toString());

//        if (Sign1)
//            tv_brainkey_error.setText(getString(R.string.account_name_length_erro));
//        else tv_brainkey_error.setText("");
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

        if (Sign1 && Sign2 && Sign3 && agreeContract && Sign4) {
            submitButtonEnable(true);
        } else {
            submitButtonEnable(false);
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            activityFinish();

        } else if (i == R.id.btn_ok) {
            if (!NetUtils.hasNetwork(getApplicationContext())) {
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.string_network_disconnect),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // 检查账户是否存在，如果存在，则先删除
            if (!BtsHelper.mIsHasAccount) {
                wallet();
            } else {
                new AlertDialog.Builder(this).setTitle(R.string.override_wallet_warning_title)
                        .setMessage(R.string.override_new_wallet_warning_message)
                        .setNegativeButton(R.string.button_cancel, null)
                        .setPositiveButton(R.string.button_confirm, new DeleteAccountConfirm()).create().show();
            }

        } else if (i == R.id.btn_account_create) {
            accountCreate();

        } else if (i == R.id.et_brainkey) {
            JumpAppPageUtil.jumpInputSeedPage(this, true, seed);

        } else if (i == R.id.brainkey_recover) {
            mRecoverType = Constants.RECOVER_TYPE_BRANIKEY;
            brainkey_recover_view.setVisibility(View.VISIBLE);
            pass_brainkey_recover_view.setVisibility(View.GONE);
            pass_brainkey_recover.setAlpha(0.7f);
            pass_brainkey_recover.setBackgroundResource(R.color.mnemonic_recovery_background);
            brainkey_recover.setAlpha(1f);
            brainkey_recover.setBackgroundResource(R.color.base_bg_color_level1);
            brainkey_sv.setVisibility(View.VISIBLE);
            pass_brainkey_ll.setVisibility(View.GONE);

        } else if (i == R.id.pass_brainkey_recover) {
            mRecoverType = Constants.RECOVER_TYPE_ENCRYPT_SEED;
            brainkey_recover_view.setVisibility(View.GONE);
            pass_brainkey_recover_view.setVisibility(View.VISIBLE);
            brainkey_recover.setAlpha(0.7f);
            brainkey_recover.setBackgroundResource(R.color.mnemonic_recovery_background);
            pass_brainkey_recover.setAlpha(1f);
            pass_brainkey_recover.setBackgroundResource(R.color.base_bg_color_level1);
            brainkey_sv.setVisibility(View.GONE);
            pass_brainkey_ll.setVisibility(View.VISIBLE);

        } else if (i == R.id.tv_choose_file) {
            checkPermission(new CheckPermListener() {
                @Override
                public void superPermission() {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    try {
                        startActivityForResult(intent, Constants.REQUEST_GET_FILE);
                    } catch (android.content.ActivityNotFoundException ex) {
                        ToastUtils.simpleToast(R.string.no_file_supervisor);
                    }
                }
            }, R.string.file, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        } else if (i == R.id.bt_start_recover) {
            DialogUtil.threeBtnDialog(this, getString(R.string.keep_account_password_in_mind), getString(R.string.button_ok), null, getString(R.string.button_cancel), new DialogUtil.ConfirmCallBackInf() {
                @Override
                public void onConfirmClick(String content) {
                    wallet();
                    SharePreferenceUtils.putObject(SharePreferenceUtils.SP_DERIVE_BRAINKEY, false);
                }
            }, null);

        } else if (i == R.id.rb_user_contract) {
            if (!isAgreeContract) {
                mUserContractCb.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton_select));
            } else {
                mUserContractCb.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton_normal));
            }

            isAgreeContract = !isAgreeContract;
            checkBtnEnable();

        } else if (i == R.id.txt_right) {
            JumpAppPageUtil.jumpSetServiceNodePage(this);

        } else {
        }
    }

    /**
     * onActivityResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE_INPUT_SEED_WORD) {
            switch (resultCode) {
                case Constants.RESULT_CODE_SEED_WORD:
                    seed = data.getStringExtra(JumpParamsContants.INTENT_SEED);
                    if (!StringUtils.equalsNull(seed))
                        et_brainkey.setText(seed);
                    checkBtnEnable();
                    break;
                default:
                    break;
            }
        }

        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (requestCode == Constants.REQUEST_GET_FILE) {
            Uri uri = data.getData();
//            Log.i(TAG, "------->" + uri.getPath());

            try {
                //dis.setText(readFileSdcardFile(SAVE_FILE_NAME));
                if (uri != null) {
                    file_path = tv_choose_file.getText().toString().trim();

                    String file = BaseUtils.getFromFileUri(this, uri);

                    tv_choose_file.setText(uri.getPath());
//                String file = BaseUtils.getPath(this, uri);
                    file_content_string = BaseUtils.readFileSdcardFile(file);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

//    public String readFileSdcardFile(String fileName) throws IOException{
//        String res="";
//        try{
//            FileInputStream fin = new FileInputStream(fileName);
//
//            int length = fin.available();
//
//            byte [] buffer = new byte[length];
//            fin.read(buffer);
//
//            res = EncodingUtils.getString(buffer, "UTF-8");
//
//            fin.close();
//        }
//
//        catch(Exception e){
//            e.printStackTrace();
//        }
//        return res;
//    }

    class DeleteAccountConfirm implements DialogInterface.OnClickListener {
        DeleteAccountConfirm() {
        }

        public void onClick(DialogInterface dialog, int which) {
            try {
                wallet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void activityFinish() {
        if (mFrom == Constants.FROM_OTHER) {
            finish();
        } else {
            finish();
//            ZanUtils.finish(MainActivity.getInstance());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activityFinish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void accountCreate() {
        finish();
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

    // EditText监听器
    class TextChange2 implements TextWatcher {

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
            if (!StringUtils.equalsNull(et_encrypt_psw.getText().toString())) {
                bt_start_recover.setAlpha(1f);
                bt_start_recover.setEnabled(true);
            } else {
                bt_start_recover.setAlpha(0.7f);
                bt_start_recover.setEnabled(false);
            }
        }
    }


    public void submitButtonEnable(boolean bEnable) {
        if (bEnable) {
            btn_ok.setBackgroundResource(R.color.base_color);
            btn_ok.setEnabled(true);
        } else {
            btn_ok.setBackgroundResource(R.color.base_color_transparent);
            btn_ok.setEnabled(false);
        }

    }


    /////////////////////////////////////////////////
    public void wallet() {

        BtsHelper.deleteWallet();
        if (mRecoverType == Constants.RECOVER_TYPE_BRANIKEY) {
            mBrainKey = et_brainkey.getText().toString().trim();
            mPassword = et_password.getText().toString().trim();
            mPassword2 = et_password2.getText().toString().trim();
        } else {
            mPassword = et_encrypt_psw.getText().toString();
            mPassword2 = mPassword;
            try {
                mBrainKey = new String(Util.decryptAES(Util.hexToBytes(file_content_string), mPassword.getBytes(Charsets.UTF_8)), Charsets.UTF_8);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), R.string.error_invalid_account_or_pass, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (mBrainKey.length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.please_enter_brainkey, Toast.LENGTH_SHORT).show();
        } else {
//            String trimmedBrainKey = et_brainkey.getText().toString().trim();
            et_brainkey.setText(mBrainKey);
            if (mPassword.length() == 0) {
                Toast.makeText(getApplicationContext(), R.string.please_enter_6_digit_pin, Toast.LENGTH_SHORT).show();
            }
            //PIN must have minimum of 6-digit
            else if (mPassword.length() < 6) {
                Toast.makeText(getApplicationContext(), R.string.pin_number_warning, Toast.LENGTH_SHORT).show();
            } else if (!mPassword2.equals(mPassword)) {
                Toast.makeText(getApplicationContext(), R.string.mismatch_pin, Toast.LENGTH_SHORT).show();
            } else {
                load();
            }
        }
    }

    void load() {
        if (mBrainKey.contains(" ")) {
            String arr[] = mBrainKey.split(" ");

            ToolsLog.logprintln("2222222222=" + arr.length);

            if (arr.length >= 12 && arr.length <= 25) {

                if (checkBrainKeyExist(mBrainKey)) {
                    Toast.makeText(getApplicationContext(), R.string.account_already_exist, Toast.LENGTH_SHORT).show();
                } else {
                    showLoadingDialog(this.getResources().getString(R.string.importing_your_wallet));

                    submitButtonEnable(false);

                    onewalletui.util.jump.JumpAppPageUtil.lastJumpSignPageTime = System.currentTimeMillis() + onewalletui.util.jump.JumpAppPageUtil.NOT_JUMP_CHECK_PAGE;
                    context.showLoadingDialog();
                    OneAccountHelper.recoverAccountFromBrainkey(mBrainKey, mPassword, new RecoverCallBack() {
                        @Override
                        public void onRecover(final boolean ifSuccess, int code, final String msg) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    context.hideLoadingDialog();
                                    if (ifSuccess) {
                                        context.finish();
                                        JumpAppPageUtil.jumpMainPage(context);
                                    } else {
                                        ToastUtils.simpleLongToast(msg);
                                    }
                                }
                            });

                        }
                    });
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.please_enter_correct_brainkey, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), R.string.please_enter_correct_brainkey, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkBrainKeyExist(String brainKey) {
        boolean isBrainKey = false;

//        List<AccountDetails> accountDetails = DbManager.getUserWalletContent(CommonConstants.DEFAULT_WALLET_NAME);
        List<AccountDetails> accountDetails = BtsHelper.getAccounts();

        for (int i = 0; i < accountDetails.size(); i++) {
            try {
                if (brainKey.equals(accountDetails.get(i).brain_key)) {
                    isBrainKey = true;
                    break;
                }
            } catch (Exception ignored) {
            }
        }
        return isBrainKey;

    }

}
