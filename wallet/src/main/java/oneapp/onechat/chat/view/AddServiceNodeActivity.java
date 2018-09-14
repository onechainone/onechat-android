package oneapp.onechat.chat.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.modle.ServiceBean;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onecore.graphenej.models.WitnessResponse;
import onemessageui.view.OneBaseActivity;

//import oneapp.graphenechain.smartcoinswallet.AddEditContacts;
//import oneapp.graphenechain.smartcoinswallet.ContactListAdapter;

//注册
public class AddServiceNodeActivity extends OneBaseActivity implements OnClickListener {
    private TextView txt_title;
    private ImageView img_back;
    private TextView btn_add_node, btn_clear_node;
    private EditText et_service_ip, et_service_node;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_add_service_node);
    }

    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);

        txt_title.setText(getString(R.string.add_network_sataus));
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        btn_add_node = (TextView) findViewById(R.id.btn_add_node);
        btn_clear_node = (TextView) findViewById(R.id.btn_clear_node);
        et_service_ip = (EditText) findViewById(R.id.et_service_ip);
        et_service_node = (EditText) findViewById(R.id.et_service_node);

    }

    @Override
    protected void initView() {
        ServiceBean serviceBean = ServiceConstants.GetUserAddService().get(ServiceConstants.SERVICE_CHAT_KEY);
        if (serviceBean != null) {
            et_service_ip.setText(serviceBean.getHost_ip());
            et_service_node.setText(serviceBean.getHost_port());
            submitButtonEnable(true);
        } else {
            et_service_ip.setText("");
            et_service_node.setText("");
            submitButtonEnable(false);
        }
    }

    @Override
    protected void initData() {

    }


    @Override
    protected void setListener() {
        img_back.setOnClickListener(this);
        btn_add_node.setOnClickListener(this);
        btn_clear_node.setOnClickListener(this);
        et_service_ip.addTextChangedListener(textWatcher);
        et_service_node.addTextChangedListener(textWatcher);
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
            String ip = et_service_ip.getText().toString();
            String node = et_service_node.getText().toString();

            submitButtonEnable(!StringUtils.equalsNull(ip) && !StringUtils.equalsNull(node));
        }
    };

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {

            finish();

        } else if (i == R.id.btn_add_node) {
            checkServiceNode();

        } else if (i == R.id.btn_clear_node) {//清除节点
            ServiceConstants.UserAddChatNode(null);
            initView();

        } else {
        }
    }

    private void checkServiceNode() {
        String accountName = OneAccountHelper.getMeAccountName();
        showLoadingDialog(getString(R.string.add_network_sataus));

        submitButtonEnable(false);
        String host_url = "ws://" + et_service_ip.getText().toString() + ":" + et_service_node.getText().toString() + "/";
        final ServiceBean serviceBean = new ServiceBean(host_url);
        serviceBean.setService_uuid(et_service_ip.getText().toString());
        serviceBean.setHost_ip(et_service_ip.getText().toString());
        serviceBean.setHost_port(et_service_node.getText().toString());

        OneAccountHelper.testServiceNode(accountName, host_url, new RequestSuccessListener<WitnessResponse>() {
            @Override
            public void onResponse(WitnessResponse response) {
                try {
                    if (response == null || response.result == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideLoadingDialog();
                                submitButtonEnable(true);
                                Toast.makeText(getApplicationContext(), R.string.add_error, Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }

                    if (response != null) {
                        ServiceConstants.UserAddChatNode(serviceBean);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideLoadingDialog();
                                submitButtonEnable(true);
                                Toast.makeText(getApplicationContext(), R.string.add_success, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void submitButtonEnable(boolean bEnable) {
        if (bEnable) {
            btn_add_node.setBackgroundResource(R.color.base_color);
            btn_add_node.setEnabled(true);
        } else {
            btn_add_node.setBackgroundResource(R.color.base_color_transparent);
            btn_add_node.setEnabled(false);
        }

    }

}
