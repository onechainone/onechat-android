package oneapp.onechat.chat.view;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.utils.jump.JumpAppPageUtil;
import onemessageui.utils.skin.SkinUtils;
import oneapp.onechat.chat.view.adapter.SkinTypeAdapter;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onemessageui.view.OneBaseActivity;
import skin.support.SkinCompatManager;


public class SelectSkinActivity extends OneBaseActivity implements OnClickListener {
    private ImageView img_back;
    private TextView txt_title, txt_submit;

    private ListView listView;
    private SkinTypeAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_listview);
    }

    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(R.string.select_skin);
        txt_submit = (TextView) findViewById(R.id.txt_right);
        txt_submit.setText(getString(R.string.action_ok));
//        txt_submit.setTextColor(ContextCompat.getColor(context, R.color.base_color));
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        listView = (ListView) findViewById(R.id.listview);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

        mAdapter = new SkinTypeAdapter(SelectSkinActivity.this);

        listView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        img_back.setOnClickListener(this);
        txt_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                Utils.finish(SelectSkinActivity.this);
                break;
            case R.id.txt_right:
                String skinName = mAdapter.getSelectSkin();
                if (StringUtils.equals(skinName, SkinUtils.getSkinName())) {
                    finish();
                    return;
                }
                int strategy = SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN;
                if (StringUtils.equals(skinName, SkinUtils.DEFAULT_SKIN)) {
                    skinName = "";
                    strategy = SkinCompatManager.SKIN_LOADER_STRATEGY_NONE;
                }
                SkinCompatManager.getInstance().loadSkin(skinName, new SkinCompatManager.SkinLoaderListener() {
                    @Override
                    public void onStart() {
                        showLoadingDialog();
                    }

                    @Override
                    public void onSuccess() {
                        hideLoadingDialog();
                        SkinUtils.configSkin();
                        ToastUtils.simpleToast(R.string.success);
                        JumpAppPageUtil.jumpNewMainPage(context);
                    }

                    @Override
                    public void onFailed(String errMsg) {
                        hideLoadingDialog();
                        ToastUtils.simpleToast(errMsg);
                    }
                }, strategy); // 后缀加载

                break;
            default:
                break;
        }
    }

}
