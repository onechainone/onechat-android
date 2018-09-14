package onemessageui.view.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Charsets;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onecore.graphenej.Util;
import onewalletui.ui.BaseActivity;

public class DeriveBrainkeyActivity extends BaseActivity implements View.OnClickListener {

    private TextView txt_title;
    private ImageView img_back;

    private TextView mSaveTv, mSavePathTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_derive_brainkey);

        initView();
        setListener();
    }

    private void setListener() {
        img_back.setOnClickListener(this);
    }

    private void initView() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        txt_title.setText(R.string.export_encrypted_seed);
        mSaveTv = (TextView) findViewById(R.id.tv_save_seed_file);
        mSaveTv.setOnClickListener(this);
        mSavePathTv = (TextView) findViewById(R.id.tv_seed_path);
        mSavePathTv.setText(Constants.SAVE_FILE_NAME + OneAccountHelper.getMeAccountName() + Constants.SAVE_SEED_FILE_NAME);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            finish();

        } else if (i == R.id.tv_save_seed_file) {
            checkPermission(new CheckPermListener() {
                @Override
                public void superPermission() {
                    String encryptString = Util.bytesToHex(Util.encryptAES(OneAccountHelper.getDefaultAccount().brain_key.getBytes(Charsets.UTF_8), OneAccountHelper.getMePasswordBackend().getBytes(Charsets.UTF_8)));
                    BaseUtils.saveStringToSD(encryptString, OneAccountHelper.getMeAccountName() + Constants.SAVE_SEED_FILE_NAME);

                    SharePreferenceUtils.putObject(SharePreferenceUtils.SP_DERIVE_BRAINKEY, false);
                    ToastUtils.simpleToast(R.string.export_seed_success);
                }
            }, R.string.file, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        }
    }
}
