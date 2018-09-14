package onemessageui.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.bean.FriendApplyResult;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import onemessageui.adpter.ApplyFriendAdapter;
import onewalletui.ui.BaseActivity;

public class FriendApplyActivity extends BaseActivity implements View.OnClickListener {

    private ListView mLvFriendApply;
    private ImageView img_back;
    private TextView txt_title;
    private ApplyFriendAdapter adapter;
    private TextView mNoOrderTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_apply);

        initView();
        initData();
        setListener();
    }

    private void setListener() {
        img_back.setOnClickListener(this);
    }

    private void initData() {
        OneAccountHelper.getFriendApplyListWithCompletion(new RequestSuccessListener<List<FriendApplyResult>>() {
            @Override
            public void onResponse(List<FriendApplyResult> friendApplyResults) {
                if (friendApplyResults != null) {
                    if (adapter == null) {
                        adapter = new ApplyFriendAdapter(FriendApplyActivity.this, friendApplyResults);
                        mLvFriendApply.setAdapter(adapter);
                    } else {
                        adapter.refresh();
                    }
                }
            }
        });

    }

    private void initView() {
        mLvFriendApply = (ListView) findViewById(R.id.lv_friend_apply);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(getResources().getString(R.string.new_friend));

        mNoOrderTv = (TextView) findViewById(R.id.txt_no_data);
        mLvFriendApply.setEmptyView(mNoOrderTv);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            finish();

        }
    }
}
