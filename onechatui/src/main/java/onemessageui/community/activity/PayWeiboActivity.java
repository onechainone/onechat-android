package onemessageui.community.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneCommunityHelper;
import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import oneapp.onechat.oneandroid.chatsdk.OneRedpacketHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.bean.RedPacketAssetBean;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.NetUtils;
import oneapp.onechat.oneandroid.onemessage.community.bean.WeiboCatchModel;
import oneapp.onechat.oneandroid.onewallet.modle.AssetInfo;
import oneapp.onechat.oneandroid.onewallet.modle.MapResult;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestErroListener;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.util.AssetInfoUtils;
import oneapp.onechat.oneandroid.onewallet.util.CoinInfoUtils;
import oneapp.onechat.oneandroid.onewallet.util.RedPacketAccountUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import onemessageui.dialog.DialogUtil;
import onemessageui.view.OneBaseActivity;
import onewalletui.ui.BaseActivity;
import onewalletui.ui.widget.DecimalEditText;
import onewalletui.util.ImageUtils;
import onewalletui.util.ToastUtils;
import onewalletui.util.jump.JumpParamsContants;
import sdk.android.onechatui.R;


public class PayWeiboActivity extends OneBaseActivity implements View.OnClickListener {

    private final String Tag = "SetWeiboPayActivity";

    private TextView mTitleTv;
    private ImageView mBackIv;

    private BaseActivity mContext;

    List<RedPacketAssetBean> redPacketAssetList;

    private DecimalEditText mSetAmountEt;

    private String mAssetCode, mPayValue, mWeiboId, mGroupId;//赞赏微博

    private String mUserId, mMsgId;//赞赏消息

    private TextView mAssetCodeTv, mSubmitTv;

    private ImageView mCoinIconIv;

    private TextView mSelectAssetCodeTv;

    private boolean ifCanNotDiff = false;//是否能修改


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    protected void start() {
        mContext = this;
        setContentView(R.layout.activity_pay_weibo);
        readArguments();
    }

    private void readArguments() {
        Intent intent = getIntent();
        if (intent != null) {
            mAssetCode = intent.getStringExtra(JumpParamsContants.INTENT_ASSET_CODE);
            mPayValue = intent.getStringExtra(JumpParamsContants.INTENT_VALUE);
            mWeiboId = intent.getStringExtra(JumpParamsContants.WEIBO_ID);
            mGroupId = intent.getStringExtra(JumpParamsContants.INTENT_GROUP_ID);
            mUserId = intent.getStringExtra(JumpParamsContants.INTENT_USER_ACCOUNT_ID);
            mMsgId = intent.getStringExtra(JumpParamsContants.INTENT_MSG_ID);
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

        mSelectAssetCodeTv = (TextView) findViewById(R.id.tv_select_asset);

    }

    /**
     * 载入视图
     */
    protected void initView() {
        mTitleTv.setTextColor(ContextCompat.getColor(context, R.color.red_packet_text));
        mTitleTv.setText(getString(R.string.zanshang));
        mBackIv.setImageResource(R.drawable.icon_back_red_packet);
        findViewById(R.id.layout_title).setBackgroundResource(R.color.red_packet_color);
        AssetInfo assetInfo = AssetInfoUtils.getAssetInfoBySymbol(mAssetCode);
        if (!StringUtils.equalsNull(mAssetCode)) {
            if (assetInfo != null) {
                mSetAmountEt.setDecimalNumber(assetInfo.getPrecision());
                mAssetCodeTv.setText(assetInfo.getShort_name());
                ImageUtils.displayCoinIconImage(context, CoinInfoUtils.getCoinImgBySymbol(assetInfo.getBase_symbol()), mCoinIconIv);
            }
        }

        if (!StringUtils.equalsNull(mAssetCode) && !StringUtils.equalsNull(mPayValue)) {
            if (assetInfo != null) {
                mPayValue = OneAccountHelper.powerInStringSubstring(assetInfo.getPrecision() + "", mPayValue, true);
            }
            ifCanNotDiff = false;
            mSetAmountEt.setText(mPayValue);
            mSetAmountEt.setEnabled(false);
        } else {
            ifCanNotDiff = true;
        }

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
        mSelectAssetCodeTv.setOnClickListener(this);
        mSubmitTv.setOnClickListener(this);
        mSetAmountEt.addTextChangedListener(textWatcher);
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
        mPayValue = mSetAmountEt.getText().toString();

        if (!StringUtils.equalsNull(mPayValue)) {
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

        } else if (i == R.id.tv_select_asset) {
            if (!ifCanNotDiff) {
                return;
            }
            if (redPacketAssetList == null) {
                showLoadingDialog("");
                OneRedpacketHelper.getRedPacketAssetListWithCompletion(context, new RequestSuccessListener<List<RedPacketAssetBean>>() {
                    @Override
                    public void onResponse(List<RedPacketAssetBean> redPacketAssetBeans) {
                        hideLoadingDialog();
                        redPacketAssetList = RedPacketAccountUtils.getRedPacketAssetList();
                        selectAsset();
                    }
                }, new RequestErroListener() {
                    @Override
                    public void onResponse(int errorCode, Exception e) {
                        hideLoadingDialog();
                        ToastUtils.simpleToast(R.string.get_asset_list_error);
                    }
                });
            } else {
                selectAsset();
            }

        }
    }

    void selectAsset() {
        //选择币种
        DialogUtil.selectRedPacketAssetDialog(context, redPacketAssetList, new DialogUtil.ConfirmCallBackObject<String>() {
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

                        if (StringUtils.getDoubleValue(mPayValue) <= 0) {
                            ToastUtils.simpleToast(R.string.red_packet_value_cant_zero);
                        } else if (!StringUtils.equalsNull(mWeiboId)) {
                            //赞赏微博
                            String operation_type = ifCanNotDiff ? CommonConstants.WEIBO_PAY_TYPE_SHANG : CommonConstants.WEIBO_PAY_TYPE_PAY;
                            OneCommunityHelper.payOrRewordForArticle(mWeiboId, mAssetCode, mPayValue, operation_type, mGroupId, new RequestSuccessListener<MapResult>() {
                                @Override
                                public void onResponse(MapResult mapResult) {
                                    if (mapResult != null) {
                                        WeiboCatchModel.getUserIsPayMap().put(mWeiboId, CommonConstants.TRUE_VALUE);
                                        ToastUtils.shangToast("X" + mPayValue);
                                        final Intent result = new Intent();
                                        result.putExtra(JumpParamsContants.INTENT_WEIBO_ID, mWeiboId);
                                        result.putExtra(JumpParamsContants.INTENT_COIN_SYMBOL, mAssetCodeTv.getText().toString());
                                        result.putExtra(JumpParamsContants.INTENT_VALUE, mSetAmountEt.getText().toString());
                                        setResult(RESULT_OK, result);
                                        finish();
                                    } else {
                                        ToastUtils.simpleToast(R.string.failed_to_reward);
                                    }
                                }
                            });
                        } else if (!StringUtils.equalsNull(mMsgId)) {
                            //赞赏消息
                            OneGroupHelper.rewardGroupMessage(mUserId, mMsgId, mAssetCode, mPayValue, new RequestSuccessListener<MapResult>() {
                                @Override
                                public void onResponse(MapResult mapResult) {
                                    if (mapResult != null) {
                                        ToastUtils.shangToast("X" + mPayValue);
                                        finish();
                                    } else {
                                        ToastUtils.simpleToast(R.string.failed_to_reward);
                                    }
                                }
                            });
                        }

                    }
                });

            } else {
                ToastUtils.simpleToast(R.string.please_select_coin_type);
            }
        } catch (Exception e) {

        }
    }

}


