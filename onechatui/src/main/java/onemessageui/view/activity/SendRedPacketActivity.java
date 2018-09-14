package onemessageui.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Charsets;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import oneapp.onechat.oneandroid.chatsdk.OneRedpacketHelper;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestErroListener;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.graphenechain.models.MemoMessage;
import oneapp.onechat.oneandroid.graphenechain.models.UserChatItem;
import oneapp.onechat.oneandroid.graphenechain.models.UserGroupInfoItem;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsApplication;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.Constants;
import oneapp.onechat.oneandroid.onemessage.bean.RedPacketAssetBean;
import oneapp.onechat.oneandroid.onemessage.bean.RedPacketBean;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemRedPacketMessageBody;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.NetUtils;
import oneapp.onechat.oneandroid.onewallet.modle.AssetInfo;
import oneapp.onechat.oneandroid.onewallet.util.AssetInfoUtils;
import oneapp.onechat.oneandroid.onewallet.util.BigDecimalUtils;
import oneapp.onechat.oneandroid.onewallet.util.CoinInfoUtils;
import oneapp.onechat.oneandroid.onewallet.util.GsonUtils;
import oneapp.onechat.oneandroid.onewallet.util.RedPacketAccountUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onecore.graphenej.Util;
import onemessageui.dialog.DialogUtil;
import onemessageui.view.OneBaseActivity;
import onewalletui.ui.BaseActivity;
import onewalletui.ui.widget.DecimalEditText;
import onewalletui.util.ImageUtils;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;
import sdk.android.onechatui.R;


public class SendRedPacketActivity extends OneBaseActivity implements View.OnClickListener {

    private final String Tag = "SetUserInfoActivity";

    private int redPacketType;

    private String groupUid;
    private UserGroupInfoItem mGroupInfo;

    private TextView mTitleTv;
    private ImageView mBackIv;

    private BaseActivity mContext;

    private EditText mMessageEt, mRedPacketNumEt;
    private DecimalEditText mItemAmountEt, mTotalAmountEt;

    private String mAssetCode;
    private TextView mAssetCodeTv, mSubmitTv;

    private ImageView mCoinIconIv;

    private String mRedPacketType = Constants.RED_PACKET_TYPE_NOTEQAUL;

    private TextView mRedPacketTypeTv, mChangeRedPacketTypeTv;
    private View mTotalAmountView, mItemAmountView;

    private TextView mGroupMemberNumTv;

    String message;

    List<RedPacketAssetBean> redPacketAssetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void start() {

        mContext = this;

        setContentView(R.layout.activity_send_red_packet);

        readArguments();

    }

    private void readArguments() {
        final Intent intent = this.getIntent();
        final Serializable sear = intent
                .getSerializableExtra(JumpParamsContants.INTENT_PARAMS_MAP);
        if (sear != null) {
            final HashMap<String, Object> paramMap = (HashMap<String, Object>) sear;
            redPacketType = (int) paramMap.get(JumpParamsContants.INTENT_RED_PACKET_TYPE);
            mAssetCode = (String) paramMap.get(JumpParamsContants.INTENT_ASSET_CODE);
            switch (redPacketType) {
                case CommonConstants.RED_PACKET_TYPE_GROUP:
                    this.groupUid = (String) paramMap.get(JumpParamsContants.INTENT_GROUP_ID);
                    if (!StringUtils.equalsNull(groupUid)) {
                        mGroupInfo = OneAccountHelper.getDatabase().getUserGroupInfoItemById(groupUid, false);
                    } else {
                        redPacketType = CommonConstants.RED_PACKET_TYPE_SOCIAL;
                    }
                    break;
            }
        }
    }

    @Override
    protected void initControl() {
        mTitleTv = (TextView) findViewById(R.id.txt_title);

        mBackIv = (ImageView) findViewById(R.id.img_back);
        mBackIv.setVisibility(View.VISIBLE);

        mMessageEt = (EditText) findViewById(R.id.et_message);
        mRedPacketNumEt = (EditText) findViewById(R.id.et_red_packet_num);
        mTotalAmountEt = (DecimalEditText) findViewById(R.id.et_total_amount);
        mItemAmountEt = (DecimalEditText) findViewById(R.id.et_item_amount);

        mSubmitTv = (TextView) findViewById(R.id.tv_submit);

        mAssetCodeTv = (TextView) findViewById(R.id.tv_asset_code);
        mCoinIconIv = (ImageView) findViewById(R.id.iv_coin_icon);

        mRedPacketTypeTv = (TextView) findViewById(R.id.tv_red_packet_type);
        mChangeRedPacketTypeTv = (TextView) findViewById(R.id.tv_change_red_packet_type);

        mTotalAmountView = findViewById(R.id.view_total_amount);
        mItemAmountView = findViewById(R.id.view_item_amount);

        mGroupMemberNumTv = (TextView) findViewById(R.id.tv_group_member_num);
    }

    /**
     * 载入视图
     */
    protected void initView() {
        mTitleTv.setTextColor(ContextCompat.getColor(context, R.color.red_packet_text));
        mTitleTv.setText(getString(R.string.send_red_packet));
        mBackIv.setImageResource(R.drawable.icon_back_red_packet);
        findViewById(R.id.layout_title).setBackgroundResource(R.color.red_packet_color);
        switch (redPacketType) {
            case CommonConstants.RED_PACKET_TYPE_GROUP:
                mGroupMemberNumTv.setVisibility(View.VISIBLE);
                if (mGroupInfo != null) {
                    mGroupMemberNumTv.setText(String.format(getString(R.string.group_member_num), mGroupInfo.getMembers_size()));
                }
                break;
            case CommonConstants.RED_PACKET_TYPE_SOCIAL:
                mGroupMemberNumTv.setVisibility(View.INVISIBLE);
                break;
        }

        mRedPacketNumEt.requestFocus();
        if (!StringUtils.equalsNull(mAssetCode)) {
            AssetInfo assetInfo = AssetInfoUtils.getAssetInfoBySymbol(mAssetCode);
            if (assetInfo != null) {
                mAssetCodeTv.setText(assetInfo.getShort_name());
                ImageUtils.displayCoinIconImage(context, CoinInfoUtils.getCoinImgBySymbol(assetInfo.getBase_symbol()), mCoinIconIv);
            }
        }
    }


    @Override
    protected void initData() {

    }

    boolean hasCheckToken = false;

    @Override
    protected void onResume() {
        super.onResume();
        getRedPacketAssetList();
        resetRedPacket();
    }

    void getRedPacketAssetList() {
        redPacketAssetList = RedPacketAccountUtils.getRedPacketAssetList();

        OneRedpacketHelper.getRedPacketAssetListWithCompletion(context, new RequestSuccessListener<List<RedPacketAssetBean>>() {
            @Override
            public void onResponse(List<RedPacketAssetBean> redPacketAssetBeans) {
                redPacketAssetList = RedPacketAccountUtils.getRedPacketAssetList();
                ;
            }
        }, null);

        hasCheckToken = true;
    }

    void resetRedPacket() {
        switch (mRedPacketType) {
            case Constants.RED_PACKET_TYPE_EQUAL:
                mItemAmountView.setVisibility(View.VISIBLE);
                mTotalAmountView.setVisibility(View.GONE);
                mRedPacketTypeTv.setText(getString(R.string.simple_red_packet_tip));
                mChangeRedPacketTypeTv.setText(getString(R.string.set_lucky_red_packet));
                break;
            case Constants.RED_PACKET_TYPE_NOTEQAUL:
                mItemAmountView.setVisibility(View.GONE);
                mTotalAmountView.setVisibility(View.VISIBLE);
                mRedPacketTypeTv.setText(getString(R.string.lucky_red_packet_tip));
                mChangeRedPacketTypeTv.setText(getString(R.string.set_simple_red_packet));
                break;
        }
        checkSubmitBtn();
    }

    @Override
    protected void setListener() {
        mBackIv.setOnClickListener(this);
        mChangeRedPacketTypeTv.setOnClickListener(this);
        mSubmitTv.setOnClickListener(this);
        findViewById(R.id.tv_select_asset).setOnClickListener(this);
        mRedPacketNumEt.addTextChangedListener(textWatcher);
        mTotalAmountEt.addTextChangedListener(textWatcher);
        mItemAmountEt.addTextChangedListener(textWatcher);
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
        String numStr = mRedPacketNumEt.getText().toString();
        String amountStr = null;
        switch (mRedPacketType) {
            case Constants.RED_PACKET_TYPE_EQUAL:
                amountStr = mItemAmountEt.getText().toString();
                break;
            case Constants.RED_PACKET_TYPE_NOTEQAUL:
                amountStr = mTotalAmountEt.getText().toString();
                break;
        }

        if (!StringUtils.equalsNull(numStr) && !StringUtils.equalsNull(amountStr)) {
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

        } else if (i == R.id.tv_change_red_packet_type) {
            if (mRedPacketType.equals(Constants.RED_PACKET_TYPE_NOTEQAUL)) {
                mRedPacketType = Constants.RED_PACKET_TYPE_EQUAL;
            } else {
                mRedPacketType = Constants.RED_PACKET_TYPE_NOTEQAUL;
            }
            resetRedPacket();

        } else if (i == R.id.tv_submit) {// 发送红包
            sendRedPacket();

        } else if (i == R.id.tv_select_asset) {
            if (redPacketAssetList == null) {
                showLoadingDialog("");
                OneRedpacketHelper.getRedPacketAssetListWithCompletion(context, new RequestSuccessListener<List<RedPacketAssetBean>>() {
                    @Override
                    public void onResponse(List<RedPacketAssetBean> redPacketAssetBeans) {
                        hideLoadingDialog();
                        redPacketAssetList = RedPacketAccountUtils.getRedPacketAssetList();
                        ;
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

                        message = mMessageEt.getText().toString();
                        if (StringUtils.equalsNull(message)) {
                            message = context.getString(R.string.red_packet_default_msg);
                        }

                        String redPacketNum = mRedPacketNumEt.getText().toString();
                        if (StringUtils.getIntValue(redPacketNum) <= 0) {
                            ToastUtils.simpleToast(R.string.red_packet_num_cant_zero);
                            return;
                        }

                        String mTotalValue = mTotalAmountEt.getText().toString();
                        String mItemValue = mItemAmountEt.getText().toString();

                        switch (mRedPacketType) {
                            case Constants.RED_PACKET_TYPE_EQUAL:
                                mTotalValue = BigDecimalUtils.multiply(new BigDecimal(mItemValue), new BigDecimal(redPacketNum)).toString();
                                break;
                            case Constants.RED_PACKET_TYPE_NOTEQAUL:
                                mTotalValue = mTotalAmountEt.getText().toString();
                                break;
                        }

                        if (StringUtils.getDoubleValue(mTotalValue) <= 0) {
                            ToastUtils.simpleToast(R.string.red_packet_value_cant_zero);
                            return;
                        }
                        if (StringUtils.equalsNull(mItemValue)) {
                            mItemValue = mTotalValue;
                        }

                        showLoadingDialog("");
                        OneRedpacketHelper.sendRedpacket(context, mAssetCode, message, mRedPacketType, redPacketNum, mTotalValue, mItemValue, new RequestSuccessListener<RedPacketBean>() {
                            @Override
                            public void onResponse(RedPacketBean redPacketBean) {
                                try {

                                    switch (redPacketType) {
                                        case CommonConstants.RED_PACKET_TYPE_GROUP:
                                            sendGroupRedPacketMsg(redPacketBean.getUni_uuid(), message);
                                            break;
                                        case CommonConstants.RED_PACKET_TYPE_SOCIAL:
                                            sendSocialRedPacket(redPacketBean.getUni_uuid());
                                            break;
                                    }
                                    mItemAmountEt.setText("");
                                    mRedPacketNumEt.setText("");
                                    mTotalAmountEt.setText("");
                                } catch (Exception e) {
                                    hideLoadingDialog();
                                    ToastUtils.simpleToast(R.string.redpacket_send_failed);
                                }
                            }
                        }, new RequestErroListener() {
                            @Override
                            public void onResponse(int errorCode, Exception e) {
                                hideLoadingDialog();
                                ToastUtils.simpleToast(R.string.redpacket_send_failed);
                            }
                        });
                    }
                });

            } else {
                ToastUtils.simpleToast(R.string.please_select_coin_type);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 发送群红包信息
     */
    private void sendGroupRedPacketMsg(String redPacketId, String msg) {
        if (StringUtils.equalsNull(redPacketId)) {
            return;
        }

        ItemMessage message = ItemMessage
                .createSendMessage(ItemMessage.Type.LOCATION);
        // 如果是小密圈，设置chattype,默认是单聊


        ItemRedPacketMessageBody redPacketBody = new ItemRedPacketMessageBody(redPacketId, msg);
        message.direct = ItemMessage.Direct.SEND;
        message.setFrom(OneAccountHelper.getAccountId());
        message.setTo(groupUid);
        message.addBody(redPacketBody);
        message.setReceipt(groupUid);
        String uuid = UUID.randomUUID().toString();
        message.setMsgId(uuid);

        String jsonParam = GsonUtils.objToJson(redPacketBody);

        if (!StringUtils.equalsNull(groupUid) && mGroupInfo != null) {
            //群聊
            message.setChatType(ItemMessage.ChatType.GroupChat);
            MemoMessage tempMemo = new MemoMessage(BtsApplication.getAdjustTimeNowMillis(),
                    MemoMessage.MSG_TYPE_RED_PACKET, MemoMessage.CMD_SEND_MSG, "", null,
                    groupUid, null, jsonParam, uuid);
            String strJson = tempMemo.toString();

            // FIXME: 2017/11/17 hs
            // 保存聊天消息
            final UserChatItem chatItem = new UserChatItem(null, null, tempMemo.getTime(), tempMemo.getTime(),
                    tempMemo.getTime(), message.getFrom(), message.getTo(),
                    "", "", strJson,
                    CommonConstants.DEFAULT_DAO_CODE, "", uuid, 0,
                    ItemMessage.Status.CREATE.ordinal(), 0, 0, groupUid);

            String encryptStrJson = Util.bytesToHex(Util.encryptAES(strJson.getBytes(Charsets.UTF_8), mGroupInfo.encrypt_key.getBytes(Charsets.UTF_8)));

            String msgType = "";

            String messageContent = "";
            if (mGroupInfo.public_status == CommonConstants.CHAT_GROUP_STATUS_PUBLIC) {
//                messageContent = jsonParam;
            }
            //添加群聊请求
            OneGroupHelper.AddGroupMessageInfo(groupUid, encryptStrJson, messageContent, msgType, uuid, null);
            OneAccountHelper.getDatabase().putUserChat(chatItem);

        }
//        else {
//            MemoMessage tempMemo = new MemoMessage(BtsApplication.getAdjustTimeNowMillis(),
//                    MemoMessage.MSG_TYPE_RED_PACKET, MemoMessage.CMD_SEND_MSG, "", null,
//                    null, null, jsonParam, uuid);
//            message.setChatType(ItemMessage.ChatType.Chat);
//            String strJson = tempMemo.toString();
//
//            // 保存聊天消息
//            UserChatItem chatItem = new UserChatItem(null, null, tempMemo.getTime(), tempMemo.getTime(),
//                    tempMemo.getTime(), message.getFrom(), message.getTo(),
//                    "", "", strJson,
//                    CommonConstants.DEFAULT_DAO_CODE, "", uuid, 0,
//                    ItemMessage.Status.CREATE.ordinal(), 0, 0, "");
//
//            OneAccountHelper.getDatabase().putUserChat(chatItem);
//
//            MessageSenderHandler.getInstance().startMessageSender();
//        }
        hideLoadingDialog();
        finish();
    }

    /**
     * 发送社交红包
     *
     * @param redPacketId
     */
    private void sendSocialRedPacket(String redPacketId) {
        hideLoadingDialog();
        if (StringUtils.equalsNull(redPacketId)) {
            return;
        }
        JumpAppPageUtil.jumpSocialRedPacketPage(context, redPacketId);
        finish();
    }
}


