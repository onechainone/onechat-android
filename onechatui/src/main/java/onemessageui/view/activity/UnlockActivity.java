package onemessageui.view.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.Constants;
import oneapp.onechat.oneandroid.onemessage.RpcCallProxy;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.UserInfoUtils;
import oneapp.onecore.graphenej.Util;
import onemessageui.dialog.DialogUtil;
import onemessageui.view.OneBaseActivity;
import onemessageui.widght.lockpattern.LockPatternUtil;
import onemessageui.widght.lockpattern.LockPatternView;
import onewalletui.ui.DialogBuilder;
import onewalletui.util.ImageUtils;
import onewalletui.util.jump.JumpAppPageUtil;
import sdk.android.onechatui.R;

//import oneapp.graphenechain.utils.BtsHelperBackend;

//浏览器
public class UnlockActivity extends OneBaseActivity {

    private TextView mLockStatusTv, mResetPswTv;

    private int inputPswTime = 0;
    private int inputHandLockTime = 0;
    private final int MAX_INPUT_TIMES = 3;

    Dialog dialog;

    private static final int MODLE_CREATE_LOCK = 0;
    private static final int MODLE_CREATE_SURE_LOCK = 1;
    private static final int MODLE_CORRECT_LOCK = 2;
    //    public static final int MODLE_RESET_LOCK = 2;
    private static final int MODLE_COMPARE_LOCK = 3;
    private static final int MODLE_WRONG_LOCK = 4;
    private static final int MODLE_SECOND_WRONG_LOCK = 5;

    private static final long WRONG_CLEAR_TIME = 1000;
    private static final long BORMAL_CLEAR_TIME = 100;

    private int mType;
    private boolean isResetPsw = false;
    private String tempPsw = null;

    LockPatternView lockView;

    private View unlockView;
    private TextView mUnLockIv;

    ImageView mAvatarIv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ifOpenRightSlideBack = false;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_unlock);
    }

    @Override
    protected void initControl() {
        lockView = (LockPatternView) findViewById(R.id.lock_view);
        mLockStatusTv = (TextView) findViewById(R.id.tv_lock_statue);
        mResetPswTv = (TextView) findViewById(R.id.tv_reset_lock_psw);
        mAvatarIv = (ImageView) findViewById(R.id.iv_avatar);
        unlockView = findViewById(R.id.seed_encrypted_layout);
        mUnLockIv = (TextView) findViewById(R.id.lock_icon);
//        Fonts.setTypeface(mUnLockIv, Fonts.Font.ONEAPP_FONT_ICONS);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initView() {
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
//                    MessageApp.getInstance2().exit();

                    break;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initData() {

        ImageUtils.displayAvatarNetImage(context, UserInfoUtils.getUserAvatar(), mAvatarIv, UserInfoUtils.getUserInfo().getSex());

        if (StringUtils.equalsNull(getIntent().getStringExtra(Constants.TYPE)))
            isResetPsw = false;
        else
            isResetPsw = true;

        if (isResetPsw) {
            resetLockPsw();
            mResetPswTv.setText(getString(R.string.reset));
        } else {
            mResetPswTv.setText(getString(R.string.forget_lock));
            checkIfHasPsw();
        }

    }

    void resetLockPsw() {
        OneAccountHelper.clearPassword();
        mType = MODLE_CREATE_LOCK;
        updateStatus();
        checkIfHasPsw();
    }

    private boolean checkIfHasPsw() {
        boolean ifSuccess = OneAccountHelper.ifHasAccountInfo();
        detailResult(ifSuccess);
        return ifSuccess;
    }


    @SuppressLint("StringFormatInvalid")
    void updateStatus() {

        switch (mType) {
            case MODLE_CREATE_LOCK:
                mLockStatusTv.setText(getString(R.string.lock_status_create));
                mLockStatusTv.setTextColor(ContextCompat.getColor(context, R.color.blue_simple));
                lockView.setPattern(LockPatternView.DisplayMode.NORMAL);
                lockView.postClearPatternRunnable(BORMAL_CLEAR_TIME);
                break;
            case MODLE_CREATE_SURE_LOCK:
                mLockStatusTv.setText(getString(R.string.lock_status_create_sure));
                mLockStatusTv.setTextColor(ContextCompat.getColor(context, R.color.blue_simple));
                lockView.setPattern(LockPatternView.DisplayMode.NORMAL);
                lockView.postClearPatternRunnable(BORMAL_CLEAR_TIME);
                break;
            case MODLE_CORRECT_LOCK:
                mLockStatusTv.setText(getString(R.string.lock_status_correct));
                mLockStatusTv.setTextColor(ContextCompat.getColor(context, R.color.blue_simple));
                lockView.setPattern(LockPatternView.DisplayMode.NORMAL);
                lockView.postClearPatternRunnable(BORMAL_CLEAR_TIME);
                break;
//            case MODLE_RESET_LOCK:
//                mLockStatusTv.setText(getString(R.string.lock_status_create));
//                break;
            case MODLE_COMPARE_LOCK:
                mLockStatusTv.setText(getString(R.string.lock_status_input));
                mLockStatusTv.setTextColor(ContextCompat.getColor(context, R.color.blue_simple));
                lockView.setPattern(LockPatternView.DisplayMode.NORMAL);
                lockView.postClearPatternRunnable(BORMAL_CLEAR_TIME);
                break;
            case MODLE_WRONG_LOCK:
                mLockStatusTv.setText(String.format(getString(R.string.lock_status_wrong), MAX_INPUT_TIMES - inputHandLockTime));
                mLockStatusTv.setTextColor(ContextCompat.getColor(context, R.color.base_color));
                lockView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockView.postClearPatternRunnable(WRONG_CLEAR_TIME);

                mType = MODLE_COMPARE_LOCK;
                if (inputHandLockTime >= MAX_INPUT_TIMES) {
                    resetLockPsw();
                    mLockStatusTv.setText("");
                }
                break;
            case MODLE_SECOND_WRONG_LOCK:
                mLockStatusTv.setText(getString(R.string.lock_status_second_wrong));
                mLockStatusTv.setTextColor(ContextCompat.getColor(context, R.color.base_color));
                lockView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockView.postClearPatternRunnable(WRONG_CLEAR_TIME);

                mType = MODLE_CREATE_LOCK;
                break;
        }

    }

    @Override
    protected void setListener() {
        mResetPswTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                resetLockPsw();
            }
        });

        unlockView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showUnlockDialog();
            }
        });

        lockView.setOnPatternListener(new LockPatternView.OnPatternListener() {

            @Override
            public void onPatternStart() {
                lockView.removePostClearPatternRunnable();
                //updateStatus(Status.DEFAULT, null);
                lockView.setPattern(LockPatternView.DisplayMode.DEFAULT);
            }

            @Override
            public void onPatternComplete(List<LockPatternView.Cell> cells) {

                String hashString = Util.bytesToHex(LockPatternUtil.patternToHash(cells));

                switch (mType) {
                    case MODLE_CREATE_LOCK:
                        if (cells.size() < CommonConstants.DEFAULT_HAND_PSW_MIN_NUM) {
                            mLockStatusTv.setText(String.format(getString(R.string.lock_status_too_short), CommonConstants.DEFAULT_HAND_PSW_MIN_NUM));
                            mLockStatusTv.setTextColor(ContextCompat.getColor(context, R.color.base_color));
                            lockView.setPattern(LockPatternView.DisplayMode.ERROR);
                            lockView.postClearPatternRunnable(WRONG_CLEAR_TIME);
                            return;
                        }
                        tempPsw = hashString;
                        mType = MODLE_CREATE_SURE_LOCK;

//                        lockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                        break;
                    case MODLE_CREATE_SURE_LOCK:
                        if (hashString.equals(tempPsw)) {
                            SharePreferenceUtils.putObject(context, SharePreferenceUtils.SP_USER_HAND_LOCK_HASH, hashString);
                            mType = MODLE_COMPARE_LOCK;
                            initApp();
                        } else {
                            mType = MODLE_SECOND_WRONG_LOCK;
                        }
//                        lockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                        break;
//                    case MODLE_RESET_LOCK:
//                        if (!hashString.equals(SharePreferenceUtils.getObject(context, SharePreferenceUtils.SP_USER_HAND_LOCK_HASH))) {
//                            mType = MODLE_WRONG_LOCK;
//                            lockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
//                        } else {
//                            mType = MODLE_CREATE_SURE_LOCK;
////                            lockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
//                        }
//                        break;
                    case MODLE_COMPARE_LOCK:
                        if (!hashString.equals(SharePreferenceUtils.getObject(SharePreferenceUtils.SP_USER_HAND_LOCK_HASH))) {
                            mType = MODLE_WRONG_LOCK;
                            inputHandLockTime++;
                        } else {
//                            if (isResetPsw)
//                                mType = MODLE_CREATE_LOCK;
//                            else {
                            initApp();
                            mType = MODLE_CORRECT_LOCK;
//                            }
                        }
                        break;
                }

                updateStatus();

            }

        });
    }

//    private class MyHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case RESULT_HAS_PASSWORD:
//                    hideLoadingDialog();
//
//                    if (SharePreferenceUtils.contains(context, SharePreferenceUtils.SP_USER_HAND_LOCK_HASH) && inputPswTime > 0) {
//                        initApp();
//                    } else {
//                        lockView.setVisibility(View.VISIBLE);
//                        unlockView.setVisibility(View.GONE);
//
//                        if (SharePreferenceUtils.contains(context, SharePreferenceUtils.SP_USER_HAND_LOCK_HASH))
//                            mType = MODLE_COMPARE_LOCK;
//                        else
//                            mType = MODLE_CREATE_LOCK;
//
//                        updateStatus();
//
//                    }
//
//                    break;
//                case RESULT_NO_PASSWORD:
//
//                    hideLoadingDialog();
//                    lockView.setVisibility(View.GONE);
//                    unlockView.setVisibility(View.VISIBLE);
//
//                    if (inputPswTime > 0)
//                        DialogBuilder.warn(context, R.string.unlocking_wallet_error_title)
//                                .setMessage(R.string.unlocking_wallet_error_detail)
//                                .setNegativeButton(R.string.button_cancel, null)
//                                .setPositiveButton(R.string.button_retry, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        showUnlockDialog();
//                                    }
//                                }).create().show();
//                    else
//                        showUnlockDialog();
//
//                    break;
//            }
//        }
//    }


    private void detailResult(boolean ifPasswordSuccess) {
        if (ifPasswordSuccess) {
            initApp();
//            if ((!SharePreferenceUtils.contains(SharePreferenceUtils.SP_IF_SET_HAND_LOCK) || SharePreferenceUtils.contains(context, SharePreferenceUtils.SP_USER_HAND_LOCK_HASH)) && inputPswTime > 0) {
//                initApp();
//            } else {
//                hideLoadingDialog();
//
//                lockView.setVisibility(View.VISIBLE);
//                unlockView.setVisibility(View.GONE);
//
//                if (SharePreferenceUtils.contains(context, SharePreferenceUtils.SP_USER_HAND_LOCK_HASH))
//                    mType = MODLE_COMPARE_LOCK;
//                else
//                    mType = MODLE_CREATE_LOCK;
//
//                updateStatus();
//
//            }
        } else {

            hideLoadingDialog();

            lockView.setVisibility(View.GONE);
            unlockView.setVisibility(View.VISIBLE);

            if (inputPswTime > 0) {
                DialogBuilder.warn(context, R.string.unlocking_wallet_error_title)
                        .setMessage(R.string.unlocking_wallet_error_detail)
                        .setNegativeButton(R.string.button_cancel, null)
                        .setPositiveButton(R.string.button_retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showUnlockDialog();
                            }
                        }).create().show();
            } else {
                showUnlockDialog();
            }

        }

    }


    private void initApp() {
        try {

//            BtsHelper.afterLoadAccountInfo();

//            JumpAppPageUtil.jumpMainPage(context);

            if (StringUtils.equalsNull(RpcCallProxy.getInstance().getPassword())) {
                RpcCallProxy.getInstance().savePassword(OneAccountHelper.getMePasswordBackend());
            }
            if (!StringUtils.equalsNull(OneAccountHelper.getActionIntentData())) {
                JumpAppPageUtil.detailIntentDataJump(context, OneAccountHelper.getActionIntentData(), false);
            }
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showUnlockDialog() {
        dialog = DialogUtil.inputPswDialog(context, getString(R.string.enter_password), new DialogUtil.ConfirmCallBackInf() {
            @Override
            public void onConfirmClick(String password) {
//                // FIXME: 2017/10/30 hs

//                showLoadingDialog(getString(R.string.importing_your_wallet));
                showLoadingDialog(getString(R.string.validation_in_progress));

                inputPswTime++;

                new LoadWalletTask(password).execute();
//                BtsHelper.setMePasswordBackend(password);
//// FIXME: 2017/11/3 hs
//                RpcCallProxy.getInstance().savePassword(password);
//
//
////                if (!BtsHelper.ifHasRequestAccountInfo() && !RpcCallProxy.getInstance().checkServiceStatus()) {
//                if (!BtsHelper.ifHasRequestAccountInfo()) {
//                    //如果解密失败,前台解密
//                    BtsHelper.decryptAccountInfo(password);
//                }
//                checkIfHasPsw();

            }
        });
    }

    class LoadWalletTask extends AsyncTask<String, Integer, Boolean> {

        private String password;

        public LoadWalletTask(String password) {
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            //这里是开始线程之前执行的,是在UI线程
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            OneAccountHelper.savePwd(password);
            return true;
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
        protected void onPostExecute(Boolean bitmap) {
            super.onPostExecute(bitmap);
            checkIfHasPsw();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {

        }
        super.onDestroy();
    }
}
