package oneapp.onechat.chat.view;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import oneapp.onechat.chat.WalletApplication;
import oneapp.onechat.chat.utils.jump.JumpAppPageUtil;
import oneapp.onechat.androidapp.R;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.onewallet.util.SystemLanguageUtils;
import onemessageui.adpter.LanguageTypeAdapter;
import onemessageui.view.OneBaseActivity;


public class SelectLangurageActivity extends OneBaseActivity implements OnClickListener {
    private ImageView img_back;
    private TextView txt_title, txt_submit;

    private ListView listView;
    private LanguageTypeAdapter mAdapter;


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
        txt_title.setText(R.string.select_langurafe);
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

        mAdapter = new LanguageTypeAdapter(SelectLangurageActivity.this);

        listView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        img_back.setOnClickListener(this);
        txt_submit.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAdapter.refreshSelect(i);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            Utils.finish(SelectLangurageActivity.this);

        } else if (i == R.id.txt_right) {
            SystemLanguageUtils.resetLanguage(WalletApplication.getInstance(), mAdapter.getSelectLanguage());
            SystemLanguageUtils.setApplicationLanguage(WalletApplication.getInstance());
            JumpAppPageUtil.jumpNewMainPage(context);


        } else {
        }
    }

}
