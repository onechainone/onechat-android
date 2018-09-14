package oneapp.onechat.chat.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.network.RequestUtils;
import oneapp.onechat.chat.utils.BaseUtils;
import oneapp.onechat.chat.utils.jump.JumpAppPageUtil;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.CoinRateUtils;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import oneapp.onechat.oneandroid.onewallet.util.SystemLanguageUtils;
import onemessageui.dialog.DialogUtil;
import onemessageui.widght.switchbutton.SwitchButton;


public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private final String Tag = "MsgTipActivity";

    private Context mContext;

    private ImageView mBackIv;
    private TextView mTitle_tv;

    private TextView mLocalLanguageTv, mLocalExchangeTypeTv, mLocalVersionTv, mChatServiceNodeTv;

    //语言
    private View mChangeLockPswView;
    //换算类型
    private View mSwitchExchangeTypeLl, mSelectLanguageView, mCheckUpdateView, mSwitchServiceView, mSwitchSkinView;
    private SwitchButton notifySwitch;
    private LinearLayout mNewNatification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mContext = this;
        initView();
        initListener();
    }

    /***
     * 载入视图
     */
    private void initView() {
        mBackIv = (ImageView) findViewById(R.id.img_back);
        mBackIv.setVisibility(View.VISIBLE);
        mTitle_tv = (TextView) findViewById(R.id.txt_title);
        mTitle_tv.setText(getString(R.string.action_settings));

        mLocalLanguageTv = (TextView) findViewById(R.id.tv_local_language);
        mLocalExchangeTypeTv = (TextView) findViewById(R.id.tv_local_exchange_type);
        mLocalVersionTv = (TextView) findViewById(R.id.tv_local_version);

        mSwitchExchangeTypeLl = findViewById(R.id.view_switch_exchange_type);
        mSelectLanguageView = findViewById(R.id.view_switch_language);
        mChangeLockPswView = findViewById(R.id.view_change_hand_lock);
        mCheckUpdateView = findViewById(R.id.view_check_update);
        mSwitchServiceView = findViewById(R.id.view_switch_service);
        mSwitchSkinView = findViewById(R.id.view_switch_skin);

        mNewNatification = (LinearLayout) findViewById(R.id.new_natification);

        mChatServiceNodeTv = (TextView) findViewById(R.id.tv_chat_service_uid);
    }

    void initDate() {
        mLocalExchangeTypeTv.setText(CoinRateUtils.getCurrentRateType());

        int languagePosition = SystemLanguageUtils.DEFAULT_LANGUAGE_POSITION;
        if (SharePreferenceUtils.contains(SharePreferenceUtils.SP_LANGUAGE_TYPE_POSITION)) {
            try {
                languagePosition = (int) SharePreferenceUtils.getObject(SharePreferenceUtils.SP_LANGUAGE_TYPE_POSITION);
            } catch (Exception e) {
            }
        }
        mLocalLanguageTv.setText(SystemLanguageUtils.SUPPORTED_LANGUAGES.get(languagePosition).getName());

        mLocalVersionTv.setText(Utils.getVersionName(context));

        mChatServiceNodeTv.setText(ServiceConstants.GetServerBeanByKey(ServiceConstants.SERVICE_CHAT_KEY, false, true).getService_uuid());
    }

    private void initListener() {
        mBackIv.setOnClickListener(this);
        mChangeLockPswView.setOnClickListener(this);
        mSelectLanguageView.setOnClickListener(this);
        mCheckUpdateView.setOnClickListener(this);
        mNewNatification.setOnClickListener(this);
        mSwitchExchangeTypeLl.setOnClickListener(this);

        mSwitchServiceView.setOnClickListener(this);
        mSwitchSkinView.setOnClickListener(this);


        findViewById(R.id.btn_exit_wallet).setOnClickListener(this);
        findViewById(R.id.view_add_service_node).setOnClickListener(this);
        findViewById(R.id.view_update_contacts).setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initDate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.view_change_hand_lock:
                //修改手势密码
                JumpAppPageUtil.jumpResetUnlockPage(context);
                break;
            case R.id.view_switch_exchange_type:
                break;
            case R.id.view_switch_language:
                JumpAppPageUtil.jumpSelectLanguragePage(context);
                break;
            case R.id.btn_exit_wallet:
                //退出当前钱包
                new AlertDialog.Builder(this).setTitle(R.string.override_wallet_warning_title)
                        .setMessage(R.string.exit_wallet_warning_message)
                        .setNegativeButton(R.string.button_cancel, null)
                        .setPositiveButton(R.string.button_confirm, new ExitAccountConfirm()).create().show();
                break;
            case R.id.view_check_update:
                //检查更新
                RequestUtils.UpgradeCheckRequest(this, true);
                break;
            case R.id.view_switch_service:
                JumpAppPageUtil.jumpSetServiceNodePage(context);
                break;
            case R.id.new_natification:
//                Intent intent = new Intent(this, NotificationActivity.class);
//                startActivity(intent);
                JumpAppPageUtil.jumpNewNatification(this);
                break;
            case R.id.view_add_service_node:
                JumpAppPageUtil.jumpAddServiceNodePage(this);
                break;
            case R.id.view_update_contacts:
                //同步通讯录
                BaseUtils.AddUserContacts(this, true, false);
                break;
            case R.id.view_switch_skin:
                //切换皮肤
                JumpAppPageUtil.jumpSelectSkinPage(context);
                break;
        }
    }

    class ExitAccountConfirm implements DialogInterface.OnClickListener {
        ExitAccountConfirm() {
        }

        public void onClick(DialogInterface dialog, int which) {

            DialogUtil.importentTipDialog(context, getString(R.string.exit_wallet_warning_message_second), new DialogUtil.ConfirmCallBackInf() {
                @Override
                public void onConfirmClick(String content) {
                    try {
                        OneAccountHelper.deleteWallet();

                        JumpAppPageUtil.jumpNewMainPage(context);
//                        SharePreferenceUtils.remove(SharePreferenceUtils.SP_IFHAVE_SAVE_SEED);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }
}
