package oneapp.onechat.chat.view;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.RpcCallProxy;
import oneapp.onechat.oneandroid.onewallet.util.NotifyUtil;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import onemessageui.widght.switchbutton.SwitchButton;


public class NotificationActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mBackIv;
    private TextView mTitle_tv;
    private SwitchButton notifySwitch, voiceSwitch, shakeSwitch, pushSwitch;
    private LinearLayout voice_ll, shake_ll;
    boolean ifNotify, voice, vibrate;
    boolean ifNotifyPush = true;
    int notifyType = CommonConstants.MSG_NOTIFY_ALL_OPEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_natification);

        initViews();
        initListener();
    }

    public void initViews() {
        mBackIv = (ImageView) findViewById(R.id.img_back);
        mBackIv.setVisibility(View.VISIBLE);
        mTitle_tv = (TextView) findViewById(R.id.txt_title);
        mTitle_tv.setText(getString(R.string.action_natification));

        notifySwitch = (SwitchButton) findViewById(R.id.sb_new_msg);
        voiceSwitch = (SwitchButton) findViewById(R.id.sb_voice);
        shakeSwitch = (SwitchButton) findViewById(R.id.sb_shake);
        pushSwitch = (SwitchButton) findViewById(R.id.sb_new_push);

        voice_ll = (LinearLayout) findViewById(R.id.voice_ll);
        shake_ll = (LinearLayout) findViewById(R.id.shake_ll);
    }

    void initDate() {
        if (SharePreferenceUtils.contains(SharePreferenceUtils.SP_NEW_PUSH_NOTIFY)) {
            ifNotifyPush = (boolean) SharePreferenceUtils.getObject(SharePreferenceUtils.SP_NEW_PUSH_NOTIFY);
        }
        pushSwitch.setChecked(ifNotifyPush);

        if (SharePreferenceUtils.contains(SharePreferenceUtils.SP_NEW_MSG_NOTIFY_TYPE)) {
            notifyType = (int) SharePreferenceUtils.getObject(SharePreferenceUtils.SP_NEW_MSG_NOTIFY_TYPE);
        }
        if (!NotifyUtil.isNotificationEnabled(context)) {
            notifyType = CommonConstants.MSG_NOTIFY_NO_NOTIFY;
        }

        switch (notifyType) {
            case CommonConstants.MSG_NOTIFY_ALL_OPEN:
                voice = true;
                vibrate = true;
                ifNotify = true;
                break;
            case CommonConstants.MSG_NOTIFY_VOICE:
                voice = true;
                vibrate = false;
                ifNotify = true;
                break;
            case CommonConstants.MSG_NOTIFY_SHAKE:
                voice = false;
                vibrate = true;
                ifNotify = true;
                break;
            case CommonConstants.MSG_NOTIFY_ALL_CLOSE:
                ifNotify = true;
                voice = false;
                vibrate = false;
                break;
            case CommonConstants.MSG_NOTIFY_NO_NOTIFY:
            default:
                ifNotify = false;
                voice = false;
                vibrate = false;
                break;
        }
        notifySwitch.setChecked(ifNotify);
        voiceSwitch.setChecked(voice);
        shakeSwitch.setChecked(vibrate);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initDate();
    }

    private void initListener() {
        mBackIv.setOnClickListener(this);

        notifySwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ifNotify = isChecked;
                if (isChecked) {
                    voice_ll.setVisibility(View.VISIBLE);
                    shake_ll.setVisibility(View.VISIBLE);
                } else {
                    voice_ll.setVisibility(View.GONE);
                    shake_ll.setVisibility(View.GONE);
                }
                if (ifNotify && !NotifyUtil.isNotificationEnabled(context)) {
                    ifNotify = false;
                    notifySwitch.setChecked(ifNotify);
                }
                saveNotifyType();
            }
        });

        voiceSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                voice = isChecked;

                saveNotifyType();
            }
        });

        shakeSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vibrate = isChecked;

                saveNotifyType();
            }
        });

        pushSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharePreferenceUtils.putObject(SharePreferenceUtils.SP_NEW_PUSH_NOTIFY, isChecked);
            }
        });
    }

    /**
     * 保存消息通知类型
     */
    void saveNotifyType() {

        if (ifNotify) {
            if (voice && vibrate) {
                notifyType = CommonConstants.MSG_NOTIFY_ALL_OPEN;
            } else if (voice) {
                notifyType = CommonConstants.MSG_NOTIFY_VOICE;
            } else if (vibrate) {
                notifyType = CommonConstants.MSG_NOTIFY_SHAKE;
            } else {
                notifyType = CommonConstants.MSG_NOTIFY_ALL_CLOSE;
            }
        } else {
            notifyType = CommonConstants.MSG_NOTIFY_NO_NOTIFY;
        }
        RpcCallProxy.getInstance().switchMsgNotify(notifyType);
        SharePreferenceUtils.putObject(SharePreferenceUtils.SP_NEW_MSG_NOTIFY_TYPE, notifyType);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }
}
