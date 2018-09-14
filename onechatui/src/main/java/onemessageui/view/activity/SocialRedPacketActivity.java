package onemessageui.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.HashMap;

import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import onewalletui.onekeyshare.ShareUtils;
import oneapp.onechat.oneandroid.onewallet.util.UiUtils;
import onemessageui.dialog.DialogUtil;
import onemessageui.view.OneBaseActivity;
import onewalletui.util.jump.JumpParamsContants;

public class SocialRedPacketActivity extends OneBaseActivity implements OnClickListener {

    private TextView txt_title, tv_share;
    private ImageView img_back;
    private TextView mRedPacketUrl;

    private String redPacketId;
    private String redPacketUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_social_red_packet);
        readArguments();
    }

    private void readArguments() {
        final Intent intent = this.getIntent();
        final Serializable sear = intent
                .getSerializableExtra(JumpParamsContants.INTENT_PARAMS_MAP);
        if (sear != null) {
            final HashMap<String, String> paramMap = (HashMap<String, String>) sear;
            this.redPacketId = paramMap.get(JumpParamsContants.INTENT_RED_PACKET_ID);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            back();

        } else if (i == R.id.view_copy_share_url) {//复制邀请链接
            UiUtils.copy(context, getString(R.string.social_redpacket_url_start) + redPacketUrl);

        } else if (i == R.id.txt_right) {//分享邀请链接
            ShareUtils.showShareWebUrl(context, redPacketUrl, getString(R.string.social_redpacket_url_start) + redPacketUrl, redPacketUrl);

        } else {
        }
    }

    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        tv_share = (TextView) findViewById(R.id.txt_right);
        tv_share.setVisibility(View.VISIBLE);
        mRedPacketUrl = (TextView) findViewById(R.id.tv_red_packet_url);
    }

    @Override
    protected void initView() {
        tv_share.setText(getString(R.string.action_share));
        tv_share.setTextColor(ContextCompat.getColor(context, R.color.red_packet_text));
        findViewById(R.id.layout_title).setBackgroundResource(R.color.red_packet_color);
        txt_title.setTextColor(ContextCompat.getColor(context, R.color.red_packet_text));
        txt_title.setText(getString(R.string.social_redpacket));
        img_back.setImageResource(R.drawable.icon_back_red_packet);
    }

    void back() {
        DialogUtil.simpleDialog(context, getString(R.string.quit_social_redpacket_tip), new DialogUtil.ConfirmCallBackInf() {
            @Override
            public void onConfirmClick(String content) {
                Utils.finish(SocialRedPacketActivity.this);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initData() {

        redPacketUrl = ServiceConstants.GetSocialRedPacketUrl(redPacketId);
        mRedPacketUrl.setText(redPacketUrl);
    }


    @Override
    protected void setListener() {
        img_back.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        findViewById(R.id.view_copy_share_url).setOnClickListener(this);
    }

}
