package oneapp.onechat.chat.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.MainActivity;
import oneapp.onechat.chat.network.RequestUtils;
import oneapp.onechat.chat.utils.jump.JumpAppPageUtil;
import oneapp.onechat.chat.utils.jump.JumpParamsContants;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.RpcCallProxy;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.onewallet.Constants;
import onewalletui.keepservicealive.IntentWrapper;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RegisterGuideActivity extends BaseActivity implements View.OnClickListener {

    private int mFrom = Constants.FROM_OTHER;
    //    新注册用户
    private ImageView mNewUserRegister;
    //    恢复账号
    private ImageView mRestoreAccount;
    private TextView mServiceNode;
    private TextView tv_language, tv_version;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ifOpenRightSlideBack = false;

        super.onCreate(savedInstanceState);

//        WalletApplication.getInstance().initWalletApp();

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE},
                0);
        checkUpdate();

        readArguments();
        setContentView(R.layout.activity_register_guide);
        initView();

        // FIXME: 2017/11/24 hs
        if (!SharePreferenceUtils.contains(SharePreferenceUtils.SP_HAS_SET_KEEP_SERVICE_ALIVE)) {
            SharePreferenceUtils.putObject(SharePreferenceUtils.SP_HAS_SET_KEEP_SERVICE_ALIVE, CommonConstants.DEFAULT_DAO_CODE);
            try {
                IntentWrapper.whiteListMatters(this, null);
            } catch (Exception e) {
            }
        }
    }

    private void readArguments() {
        final Intent intent = this.getIntent();
        final Serializable sear = intent
                .getSerializableExtra(JumpParamsContants.INTENT_PARAMS_MAP);
        if (sear != null) {
            final HashMap<String, Object> paramMap = (HashMap<String, Object>) sear;
            mFrom = (int) paramMap.get(Constants.ARG_FROM);
        }
    }

    private void checkUpdate() {
        //检查更新
        RequestUtils.UpgradeCheckRequest(this, false);
    }

    private void initView() {
        mNewUserRegister = (ImageView) findViewById(R.id.new_user_register);
        mNewUserRegister.setOnClickListener(this);
        mRestoreAccount = (ImageView) findViewById(R.id.restore_account);
        mRestoreAccount.setOnClickListener(this);
//        usersign = getSharedPreferences(SharePreferenceUtils.USER_SIGN, MODE_PRIVATE);
        mServiceNode = (TextView) findViewById(R.id.txt_test_node);
        mServiceNode.setOnClickListener(this);
        tv_language = (TextView) findViewById(R.id.tv_language);
        tv_language.setOnClickListener(this);
        tv_language.setText("language");
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText(Utils.getVersionName(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.new_user_register:
                JumpAppPageUtil.jumpAccountCreatePage(context, OneAccountHelper.generateNewMnemonic());
                break;
            case R.id.restore_account:
                SharePreferenceUtils.putObject(SharePreferenceUtils.SP_IFHAVE_SAVE_SEED, true);
                JumpAppPageUtil.jumpAccountRestorePage(context);
                break;
            case R.id.tv_language:
                JumpAppPageUtil.jumpSelectLanguragePage(context);
                break;
            case R.id.txt_test_node:
                JumpAppPageUtil.jumpSetServiceNodePage(this);
                break;
        }
    }

    private int keyBackClickCount = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (keyBackClickCount++) {
                case 0:
                    Toast.makeText(this, getString(R.string.click_back_again), Toast.LENGTH_SHORT).show();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            keyBackClickCount = 0;
                        }
                    }, 3000);
                    break;
                case 1:
                    RpcCallProxy.getInstance().saveAppStatus(CommonConstants.APP_STATUS_EXIT);
                    exitApp();
                    break;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            activityFinish();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    private void activityFinish() {
        if (mFrom == Constants.FROM_OTHER) {
            finish();
        } else {
            Utils.finish(this);
            Utils.finish(MainActivity.getInstance());
        }
    }
}
