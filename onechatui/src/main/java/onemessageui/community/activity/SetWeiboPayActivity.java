package onemessageui.community.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.NetUtils;
import onemessageui.widght.switchbutton.SwitchButton;
import oneapp.onechat.oneandroid.onewallet.modle.AssetInfo;
import onewalletui.ui.widget.DecimalEditText;
import oneapp.onechat.oneandroid.onewallet.util.AssetInfoUtils;
import oneapp.onechat.oneandroid.onewallet.util.CoinInfoUtils;
import onewalletui.util.ImageUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onemessageui.dialog.DialogUtil;
import onemessageui.view.OneBaseActivity;
import onewalletui.ui.BaseActivity;
import onewalletui.util.jump.JumpParamsContants;


public class SetWeiboPayActivity extends OneBaseActivity implements View.OnClickListener {

    private final String Tag = "SetWeiboPayActivity";

    private TextView mTitleTv;
    private ImageView mBackIv;

    private BaseActivity mContext;

    private DecimalEditText mSetAmountEt;

    private String mAssetCode;
    private String mPayValue;
    private TextView mAssetCodeTv, mSubmitTv;

    private ImageView mCoinIconIv;

    private SwitchButton openSwitch;
    private boolean ifSetPay = false;
    private TextView txt_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void start() {
        mContext = this;
        setContentView(R.layout.activity_set_weibo_pay);
        readArguments();
    }

    private void readArguments() {
        final Intent intent = this.getIntent();
        if (intent != null) {
            mAssetCode = intent.getStringExtra(JumpParamsContants.INTENT_ASSET_CODE);
            mPayValue = intent.getStringExtra(JumpParamsContants.INTENT_VALUE);
        }

    }

    @Override
    protected void initControl() {
        mTitleTv = (TextView) findViewById(R.id.txt_title);

        mBackIv = (ImageView) findViewById(R.id.img_back);
        mBackIv.setVisibility(View.VISIBLE);

        mSetAmountEt = (DecimalEditText) findViewById(R.id.et_set_amount);

        mSubmitTv = (TextView) findViewById(R.id.tv_submit);

        mAssetCodeTv = (TextView) findViewById(R.id.tv_asset_code);
        mCoinIconIv = (ImageView) findViewById(R.id.iv_coin_icon);

        openSwitch = (SwitchButton) findViewById(R.id.sb_open);

        txt_right = (TextView) findViewById(R.id.txt_right);
        txt_right.setVisibility(View.VISIBLE);
        txt_right.setTextColor(ContextCompat.getColor(context, R.color.red_packet_text));
        txt_right.setText(getResources().getString(R.string.button_ok));
        txt_right.setOnClickListener(this);
    }

    /**
     * 载入视图
     */
    protected void initView() {
        if (!StringUtils.equalsNull(mAssetCode)) {
            ifSetPay = true;
        } else {
            ifSetPay = false;
        }
        mTitleTv.setTextColor(ContextCompat.getColor(context, R.color.red_packet_text));
        mTitleTv.setText(getString(R.string.set_pay_weibo));
        mBackIv.setImageResource(R.drawable.icon_back_red_packet);
        findViewById(R.id.layout_title).setBackgroundResource(R.color.red_packet_color);
        if (!StringUtils.equalsNull(mAssetCode)) {
            AssetInfo assetInfo = AssetInfoUtils.getAssetInfoBySymbol(mAssetCode);
            if (assetInfo != null) {
                mSetAmountEt.setDecimalNumber(assetInfo.getPrecision());
                mAssetCodeTv.setText(assetInfo.getShort_name());
                ImageUtils.displayCoinIconImage(context, CoinInfoUtils.getCoinImgBySymbol(assetInfo.getBase_symbol()), mCoinIconIv);
            }
        }
        mSetAmountEt.setText(mPayValue);
        openSwitch.setChecked(ifSetPay);
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        resetRedPacket();
    }


    void resetRedPacket() {
        checkSubmitBtn();
    }

    @Override
    protected void setListener() {
        mBackIv.setOnClickListener(this);
        mSubmitTv.setOnClickListener(this);
        findViewById(R.id.tv_select_asset).setOnClickListener(this);
        mSetAmountEt.addTextChangedListener(textWatcher);
        openSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ifSetPay = isChecked;
            }
        });
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkSubmitBtn();
        }
    };

    private void checkSubmitBtn() {
        String amountStr = mSetAmountEt.getText().toString();

        if (!StringUtils.equalsNull(amountStr)) {
            mSubmitTv.setAlpha(1);

            mSubmitTv.setEnabled(true);

        } else {
            mSubmitTv.setAlpha(oneapp.onechat.oneandroid.onewallet.Constants.DEFAULT_CAN_NOT_CLICK_ALPHA);

            mSubmitTv.setEnabled(false);
        }
    }

    @Override
    public void onClick(final View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            finish();

        } else if (i == R.id.tv_submit) {// 发送红包
            sendRedPacket();

        } else if (i == R.id.tv_select_asset) {//                if (redPacketAssetList == null) {
//                    showLoadingDialog("");
//                    RequestUtils.GetRedPacketAssetListRequest(context, true, new RequestSuccessListener<List<RedPacketAssetBean>>() {
//                        @Override
//                        public void onResponse(List<RedPacketAssetBean> redPacketAssetBeans) {
//                            hideLoadingDialog();
//                            redPacketAssetList = redPacketAssetBeans;
//                            selectAsset();
//                        }
//                    }, new RequestErroListener() {
//                        @Override
//                        public void onResponse(int errorCode, Exception e) {
//                            hideLoadingDialog();
//                            ToastUtils.simpleToast(R.string.get_asset_list_error);
//                        }
//                    });
//                } else {
            selectAsset();
//                }

        } else if (i == R.id.txt_right) {
            String value = mSetAmountEt.getText().toString();

            if (ifSetPay) {
                final Intent result = new Intent();
                result.putExtra(JumpParamsContants.INTENT_ASSET_CODE, mAssetCode);
                result.putExtra(JumpParamsContants.INTENT_VALUE, value);
                setResult(RESULT_OK, result);
                if (StringUtils.equalsNull(value) || value.startsWith(".") || new BigDecimal(value).compareTo(BigDecimal.ZERO) <= 0) {
                    ToastUtils.simpleToast(R.string.amount_error);
                } else {
                    finish();
                }
            } else {
                finish();
            }

        }
    }

    void selectAsset() {
        //选择币种
        DialogUtil.selectAssetDialog(context, AssetInfoUtils.getAssetInfoList(), new DialogUtil.ConfirmCallBackObject<String>() {
            @Override
            public void onConfirmClick(String assetCode) {
                mAssetCode = assetCode;
                AssetInfo assetInfo = AssetInfoUtils.getAssetInfoBySymbol(mAssetCode);
                if (assetInfo != null) {
                    mSetAmountEt.setDecimalNumber(assetInfo.getPrecision());
                    mAssetCodeTv.setText(assetInfo.getShort_name());
                    ImageUtils.displayCoinIconImage(context, CoinInfoUtils.getCoinImgBySymbol(assetInfo.getBase_symbol()), mCoinIconIv);
                }
            }
        });
    }

    void sendRedPacket() {
        if (!NetUtils.hasNetwork(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.string_network_disconnect),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (!StringUtils.equalsNull(mAssetCode)) {

                DialogUtil.checkPswDialog(context, getString(R.string.enter_password), new DialogUtil.ConfirmCallBackInf() {
                    @Override
                    public void onConfirmClick(String content) {

                        String mSendValue = mSetAmountEt.getText().toString();

                        if (StringUtils.getDoubleValue(mSendValue) <= 0) {
                            ToastUtils.simpleToast(R.string.red_packet_value_cant_zero);
                            return;
                        }

                    }
                });

            } else {
                ToastUtils.simpleToast(R.string.please_select_coin_type);
            }
        } catch (Exception e) {

        }
    }

//    @Override
//    public void finish() {
//        if (ifSetPay) {
//            final Intent result = new Intent();
//            result.putExtra(IntentParamsContants.INTENT_ASSET_CODE, mAssetCode);
//            result.putExtra(IntentParamsContants.INTENT_VALUE, mSetAmountEt.getText().toString());
//            setResult(RESULT_OK, result);
//        }
//        super.finish();
//    }
}


