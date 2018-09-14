package onemessageui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.HashMap;

import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import onemessageui.utils.CommonUtils;
import onemessageui.view.OneBaseActivity;
import onewalletui.ui.ScanActivity;
import onewalletui.util.jump.JumpParamsContants;
import sdk.android.onechatui.R;

//import oneapp.graphenechain.smartcoinswallet.AddEditContacts;
//import oneapp.graphenechain.smartcoinswallet.ContactListAdapter;

//注册
public class AddFriendActivity extends OneBaseActivity implements OnClickListener {
    private TextView txt_title;
    private ImageView img_back;
    private Button btn_add_friend;
    //	btn_send;
    private EditText et_usertel, et_nickname;
    //et_password, et_code;

    ImageButton btn_scan_qr_code;
//    webSocketCallHelper myWebSocketHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        BtsApplication.registerCallback(this);

//        myWebSocketHelper = new webSocketCallHelper(this);
    }

    @Override
    protected void start() {
        readArgument();
        setContentView(R.layout.activity_addfriend);
    }

    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);

        txt_title.setText(getString(R.string.action_add_friend));
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        btn_add_friend = (Button) findViewById(R.id.btn_add_friend);
        et_usertel = (EditText) findViewById(R.id.et_usertel);
        et_nickname = (EditText) findViewById(R.id.et_nickname);

        btn_scan_qr_code = (ImageButton) findViewById(R.id.btn_scan_qr_code);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    void readArgument() {
        final Serializable sear = getIntent()
                .getSerializableExtra(JumpParamsContants.INTENT_PARAMS_MAP);
        if (sear != null) {
            final HashMap<String, String> paramMap = (HashMap<String, String>) sear;
            String account_name = paramMap.get(JumpParamsContants.INTENT_ACCOUNT_NAME);
            if (!StringUtils.equalsNull(account_name)) {
                et_usertel.setText(account_name);
            }
        }
    }

    @Override
    protected void setListener() {
        img_back.setOnClickListener(this);
        btn_add_friend.setOnClickListener(this);
        et_usertel.addTextChangedListener(new TelTextChange());
        btn_scan_qr_code.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            finish();

        } else if (i == R.id.btn_add_friend) {
            createBitShareAN(false);

        } else if (i == R.id.btn_scan_qr_code) {
            startActivityForResult(new Intent(this, ScanActivity.class), ScanActivity.REQUEST_CODE_SCAN);

        } else {
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == ScanActivity.REQUEST_CODE_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                String resultString = intent.getStringExtra(ScanActivity.INTENT_EXTRA_RESULT);

                if ((resultString.startsWith(Constants.ACTION_INTENT_STRING_START) || resultString.startsWith(Constants.ACTION_INTENT_STRING_START2)) && !StringUtils.equalsNull(BaseUtils.getUrlValueByName(resultString, Constants.ACTION_INTENT_KEY_ACTION))) {
                    String account_name = BaseUtils.getUrlValueByName(resultString, Constants.ACTION_INTENT_KEY_ACCOUNT_NAME);
                    if (!StringUtils.equalsNull(account_name)) {
                        et_usertel.setText(account_name);
                    }
//                    switch (BaseUtils.getUrlValueByName(resultString, Constants.ACTION_INTENT_KEY_ACTION)) {
//                        case Constants.INTENT_ACTION_TYPE_ADD_FRIEND:
//                            String account_name=BaseUtils.getUrlValueByName(resultString, Constants.ACTION_INTENT_KEY_ACCOUNT_NAME);
//                            if (!StringUtils.equalsNull(account_name)) {
//                                et_usertel.setText(account_name);
//                            }
//                            break;
//                    }
                }

//                Bundle res = intent.getExtras();
//                decodeInvoiceData(res.getString(ScanActivity.INTENT_EXTRA_RESULT));
            }
        }
    }

    // 手机号 EditText监听器
    class TelTextChange implements TextWatcher {

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
            String phone = et_usertel.getText().toString();
            if (phone.length() >= CommonConstants.MIN_LENGTH_ACCOUNT_NAME) {
                submitButtonEnable(true);
            } else {
                submitButtonEnable(false);
            }
        }
    }


    public void createBitShareAN(boolean focused) {
        String accountName = et_usertel.getText().toString().trim();
        CommonUtils.addFriend(context, accountName, true);
        submitButtonEnable(false);

    }


    public void submitButtonEnable(boolean bEnable) {
        if (bEnable) {
            btn_add_friend.setAlpha(1);
            btn_add_friend.setEnabled(true);
        } else {
            btn_add_friend.setAlpha(Constants.DEFAULT_CAN_NOT_CLICK_ALPHA);
            btn_add_friend.setEnabled(false);
        }

    }

}
