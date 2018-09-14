package onemessageui.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.bean.GroupApplyResult;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import onemessageui.adpter.ApplyGroupAdapter;
import onewalletui.ui.BaseActivity;

public class GroupApplyActivity extends BaseActivity implements View.OnClickListener {

    private ListView mLvGroupApply;
    private ImageView img_back;
    private TextView txt_title;
    private ApplyGroupAdapter adapter;
    private TextView mNoOrderTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_apply);

        initView();
        initData();
        setListener();
    }

    private void setListener() {
        img_back.setOnClickListener(this);
    }

    private void initData() {
        OneGroupHelper.getGroupInvitationListWithCompletion(new RequestSuccessListener<List<GroupApplyResult>>() {
            @Override
            public void onResponse(List<GroupApplyResult> groupApplyResults) {
                if (groupApplyResults != null) {
                    if (adapter == null) {
                        adapter = new ApplyGroupAdapter(GroupApplyActivity.this, groupApplyResults);
                        mLvGroupApply.setAdapter(adapter);
                    } else {
                        adapter.refresh();
                    }
                }
            }
        });
    }

    private void initView() {
        mLvGroupApply = (ListView) findViewById(R.id.lv_group_apply);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(getResources().getString(R.string.group_invitation));

        mNoOrderTv = (TextView) findViewById(R.id.txt_no_data);
        mLvGroupApply.setEmptyView(mNoOrderTv);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            finish();

        }
    }
}
