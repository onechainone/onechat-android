package onemessageui.view.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onemessage.bean.LoginResultBean;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onemessageui.view.OneBaseActivity;
import onewalletui.ui.DialogBuilder;
import onewalletui.util.jump.JumpAppPageUtil;
import sdk.android.onechatui.R;

//import oneapp.graphenechain.smartcoinswallet.AddEditContacts;
//import oneapp.graphenechain.smartcoinswallet.ContactListAdapter;

//注册
public class CheckSignActivity extends OneBaseActivity implements OnClickListener {
    private TextView txt_title;
    private ImageView img_back;
    private Button btn_submit;
    //	btn_send;
    private EditText et_password;
    //et_password, et_code;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        BtsApplication.registerCallback(this);

//        myWebSocketHelper = new webSocketCallHelper(this);
    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_check_sign);
    }

    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);

        txt_title.setText(getString(R.string.check_sign));
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        et_password = (EditText) findViewById(R.id.et_password);

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
    }

    @Override
    protected void setListener() {
        img_back.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0) {
                    btn_submit.setBackgroundResource(R.color.base_color);
                    btn_submit.setEnabled(true);
                } else {
                    btn_submit.setBackgroundResource(R.color.base_color_transparent);
                    btn_submit.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {

            finish();

        } else if (i == R.id.btn_submit) {
            if (OneAccountHelper.checkPassword(et_password.getText().toString())) {
                showLoadingDialog("");
                OneAccountHelper.LoginOpenServiceRequest(new RequestSuccessListener<LoginResultBean>() {
                    @Override
                    public void onResponse(LoginResultBean loginResultBean) {

                        hideLoadingDialog();
                        if (loginResultBean != null) {
                            finish();
                        } else {
                            ToastUtils.simpleToast(String.format(getString(R.string.erro_format), getString(R.string.check_sign)));
                        }
                    }
                });
            } else {
                DialogBuilder.warn(context, R.string.unlocking_wallet_error_title)
                        .setMessage(R.string.unlocking_wallet_error_detail)
                        .setNegativeButton(R.string.button_cancel, null)
                        .setPositiveButton(R.string.button_retry, null).create().show();
            }


        } else {
        }
    }

    @Override
    public void finish() {
        JumpAppPageUtil.lastJumpSignPageTime = System.currentTimeMillis();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        JumpAppPageUtil.lastJumpSignPageTime = System.currentTimeMillis();
        super.onDestroy();
    }
}
