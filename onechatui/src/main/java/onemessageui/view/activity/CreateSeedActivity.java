package onemessageui.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.bitcoinj.crypto.MnemonicCode;

import oneapp.onechat.oneandroid.onewallet.util.WalletUtils;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.chatsdk.ConfigConstants;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onechat.oneandroid.onewallet.util.UiUtils;
import onemessageui.dialog.DialogUtil;
import onemessageui.view.OneBaseActivity;

public class CreateSeedActivity extends OneBaseActivity implements OnClickListener {

    private int mFrom = Constants.FROM_OTHER;

    boolean isInputSeed = false;

    private ImageView img_back;
    private TextView txt_title, languageTv;

    private TextView seedView, mSubmitTv, mCopyTv;

    private TextView mCreateSeedTv, mInputSeedTv;

//    private EditText mInputSeedEt;

    private String seed;
    private TextView mLanguageTv;
    private boolean firstSeed;
    private LinearLayout ll_word;
    private ImageView iv_img;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        ifUnlockThisActivity = false;
        ifCanScreenShot = false;

        super.onCreate(savedInstanceState);

    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_create_seed);
    }

    private void checkedSeed() {
        Intent intent = getIntent();
        seed = intent.getStringExtra(JumpParamsContants.INTENT_SEED);
        firstSeed = intent.getBooleanExtra(JumpParamsContants.INTENT_FIRST_SEED, false);
        if (!StringUtils.equalsNull(seed)) {
            isInputSeed = true;
//            mCopyTv.setVisibility(View.VISIBLE);
//            mInputSeedEt.setVisibility(View.VISIBLE);
            if (!firstSeed) {
                iv_img.setVisibility(View.GONE);
                ll_word.setVisibility(View.GONE);
                mCreateSeedTv.setText(getString(R.string.next_step));
            } else {
                mCreateSeedTv.setText(getString(R.string.action_ok));
                iv_img.setVisibility(View.VISIBLE);
                ll_word.setVisibility(View.VISIBLE);
            }
//                mSubmitTv.setVisibility(View.VISIBLE);
            seedView.setText(seed);
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            activityFinish();

        } else if (i == R.id.txt_right) {
            JumpAppPageUtil.jumpSetServiceNodePage(this);

        } else if (i == R.id.seed) {
            if (ConfigConstants.DEBUG) {
                onCopy();
            }

        } else if (i == R.id.tv_has_save) {
            submitSeed();

        } else if (i == R.id.tv_create_seed) {
            if (!StringUtils.equalsNull(seed)) {
                submitSeed();
            } else {
                isInputSeed = false;
                seed = generateNewMnemonic();
                seedView.setText(seed);

                DialogUtil.tipDialog(this, getString(R.string.no_screen_shoot));
//                    mCopyTv.setVisibility(View.VISIBLE);
//                mInputSeedEt.setVisibility(View.GONE);
                mCreateSeedTv.setText(getString(R.string.next_step));
            }


        } else if (i == R.id.tv_input_seed) {
            JumpAppPageUtil.jumpInputSeedPage(this, true, null);

        }
    }

    private void submitSeed() {

        if (isInputSeed) {
//            seed = mInputSeedEt.getText().toString();
            if (checkSeed(seed)) {
//                JumpAppPageUtil.jumpAccountCreatePage(context, seed, mFrom);
                JumpAppPageUtil.jumpMakeSureSeedPage(context, seed, firstSeed);
                if (!firstSeed) {
                    finish();
                }
            }
        } else {
            if (checkSeed(seed)) {
                DialogUtil.simpleDialog(context, getString(R.string.make_sure_save_seed), new DialogUtil.ConfirmCallBackInf() {
                    @Override
                    public void onConfirmClick(String content) {
                        JumpAppPageUtil.jumpMakeSureSeedPage(context, seed, firstSeed);
                    }
                });
            }
        }

    }

    private void activityFinish() {
        if (mFrom == Constants.FROM_OTHER) {
            finish();
        } else {
            Utils.finish(this);
        }
    }

    /**
     * 检查seed
     *
     * @return
     */
    private boolean checkSeed(String seed) {
        String errorString = null;
        if (StringUtils.equalsNull(seed)) {
            errorString = getString(R.string.please_enter_brainkey);
        } else if (!MnemonicCode.INSTANCE.check(seed)) {
            errorString = getString(R.string.error_invalid_account);
        }
        if (!StringUtils.equalsNull(errorString)) {
            ToastUtils.simpleToast(errorString);
            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        img_back = (ImageView) findViewById(R.id.img_back);
        languageTv = (TextView) findViewById(R.id.txt_right);
        seedView = (TextView) findViewById(R.id.seed);
        mCopyTv = (TextView) findViewById(R.id.tv_copy);
        mSubmitTv = (TextView) findViewById(R.id.tv_has_save);
//        mInputSeedEt = (EditText) findViewById(R.id.et_input_seed);

        mCreateSeedTv = (TextView) findViewById(R.id.tv_create_seed);
        mInputSeedTv = (TextView) findViewById(R.id.tv_input_seed);
        mLanguageTv = (TextView) findViewById(R.id.txt_left);
        mLanguageTv.setVisibility(View.GONE);
        ll_word = (LinearLayout) findViewById(R.id.ll_word);
        iv_img = (ImageView) findViewById(R.id.iv_img);
    }

    @Override
    protected void initView() {
        languageTv.setVisibility(View.VISIBLE);
        languageTv.setText(R.string.switch_service_node);
        mLanguageTv.setVisibility(View.GONE);
        mLanguageTv.setText("language");
        mLanguageTv.setPadding((int) getResources().getDimension(R.dimen.dimen_20), 0, 0, 0);

        txt_title.setText(getString(R.string.seed));

        if (mFrom == Constants.FROM_OTHER) {
            img_back.setVisibility(View.VISIBLE);
        } else {
            img_back.setVisibility(View.GONE);
        }

    }

    @Override
    protected void initData() {
        checkedSeed();
    }

    @Override
    protected void setListener() {

        img_back.setOnClickListener(this);
        languageTv.setOnClickListener(this);
        seedView.setOnClickListener(this);
        mSubmitTv.setOnClickListener(this);
        mCreateSeedTv.setOnClickListener(this);
        mInputSeedTv.setOnClickListener(this);
        mLanguageTv.setOnClickListener(this);
        findViewById(R.id.tv_account_restore).setOnClickListener(this);
    }

    private void onCopy() {
        try {
            UiUtils.copy(context, seed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成助记词
     *
     * @return
     */
    private String generateNewMnemonic() {
        String mnemonic;

        boolean hasExtraEntropy = false;

        if (hasExtraEntropy) {
            mnemonic = WalletUtils.generateMnemonicString(Constants.SEED_ENTROPY_EXTRA);
        } else {
            mnemonic = WalletUtils.generateMnemonicString(Constants.SEED_ENTROPY_DEFAULT);
        }

        return mnemonic;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activityFinish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * onActivityResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_INPUT_SEED_WORD) {
            switch (resultCode) {
                case Constants.RESULT_CODE_SEED_WORD: // 复制消息
                    seed = data.getStringExtra(JumpParamsContants.INTENT_SEED);
                    if (!StringUtils.equalsNull(seed)) {
                        isInputSeed = true;
//                        mCopyTv.setVisibility(View.VISIBLE);
//                mInputSeedEt.setVisibility(View.VISIBLE);
                        mCreateSeedTv.setText(getString(R.string.action_ok));
//                mSubmitTv.setVisibility(View.VISIBLE);
                        seedView.setText(seed);
                    } else {
                        isInputSeed = false;
//                        mCopyTv.setVisibility(View.VISIBLE);
//                mInputSeedEt.setVisibility(View.GONE);
                        mCreateSeedTv.setText(getString(R.string.action_have_saved));
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
