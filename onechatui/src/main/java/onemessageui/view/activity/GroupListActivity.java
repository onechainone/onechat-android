package onemessageui.view.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.graphenechain.models.UserGroupInfoItem;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import onemessageui.adpter.GroupItemAdapter;
import onemessageui.view.OneBaseActivity;
import onewalletui.util.jump.JumpAppPageUtil;
import sdk.android.onechatui.R;


public class GroupListActivity extends OneBaseActivity implements OnClickListener {
    private ImageView img_back;
    private TextView txt_title;

    private ListView listView;
    private GroupItemAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_listview);
    }

    private void refreshList() {
        showLoadingDialog();
        OneGroupHelper.GetMyGroupListRequest(new RequestSuccessListener<List<UserGroupInfoItem>>() {
            @Override
            public void onResponse(List<UserGroupInfoItem> groupInfoItems) {
                hideLoadingDialog();
                if (groupInfoItems != null) {
                    if (groupAdapter == null) {
                        groupAdapter = new GroupItemAdapter(context, groupInfoItems);
                        listView.setAdapter(groupAdapter);
                    } else {
                        groupAdapter.refresh(groupInfoItems);
                    }
                }
            }
        });
    }

    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(R.string.my_group_chat);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        listView = (ListView) findViewById(R.id.listview);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
//        List<UserGroupInfoItem> groupInfoItems = BtsHelper.getDatabase().getUserGroupInfo(CommonConstants.MAX_LOAD_MESSAGE_SIZE);
//        groupAdapter = new GroupItemAdapter(context, groupInfoItems);
//        listView.setAdapter(groupAdapter);

        refreshList();
    }


    @Override
    protected void setListener() {
        img_back.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = i - listView.getHeaderViewsCount();
                JumpAppPageUtil.jumpGroupChatPage(context, groupAdapter.getItem(position).group_uid, groupAdapter.getItem(position).group_name);
            }
        });

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            Utils.finish(GroupListActivity.this);

        } else if (i == R.id.view_new_friends) {
            JumpAppPageUtil.jumpFriendApplyPage(this);

        } else if (i == R.id.view_new_groups) {
            JumpAppPageUtil.jumpGroupApplyPage(this);

        } else {
        }
    }


}
