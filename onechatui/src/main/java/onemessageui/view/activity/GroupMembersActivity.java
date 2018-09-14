package onemessageui.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneChatHelper;
import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import oneapp.onechat.oneandroid.graphenechain.models.UserContactItem;
import oneapp.onechat.oneandroid.graphenechain.models.UserGroupInfoItem;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsApplication;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.NetUtils;
import oneapp.onechat.oneandroid.onemessage.common.CommonHelperUtils;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.modle.ListResult;
import oneapp.onechat.oneandroid.onewallet.modle.UserInfoBean;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.util.GsonUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onechat.oneandroid.onewallet.util.UserInfoUtils;
import onemessageui.dialog.DialogUtil;
import onemessageui.view.OneBaseActivity;
import onewalletui.util.ImageUtils;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;
import sdk.android.onechatui.R;


//群设置
public class GroupMembersActivity extends OneBaseActivity implements
        OnClickListener {
    private ImageView img_back;
    private TextView txt_title;// 标题，成员总数
    private int page = 0;
    public static int ITEM_PAGE_SIZE = 40;
    private SmartRefreshLayout swipeRefreshLayout;
    private GridView gridview;// 成员列表
    List<String> memberIds;
    List<String> requestMembers;

    private List<String> showMembers = new ArrayList<>();

    private String groupId;
    private boolean isInDeleteMode;
    private UserGroupInfoItem group;
    private GridAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_group_members);
    }

    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(getString(R.string.group_members));
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        gridview = (GridView) findViewById(R.id.gridview);

        swipeRefreshLayout = (SmartRefreshLayout) findViewById(R.id.swipeContainer);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        readArguments();
// 获取本地该群数据
        group = OneAccountHelper.getDatabase().getUserGroupInfoItemById(groupId);

        updateView();

    }

    private void readArguments() {
        final Intent intent = this.getIntent();
        final Serializable sear = intent
                .getSerializableExtra(JumpParamsContants.INTENT_PARAMS_MAP);
        if (sear != null) {
            final HashMap<String, Object> paramMap = (HashMap<String, Object>) sear;
            this.groupId = (String) paramMap.get(JumpParamsContants.INTENT_GROUP_ID);
            isInDeleteMode = (boolean) paramMap.get(JumpParamsContants.INTENT_ISINDELETEMODE);
        }
    }


    void updateView() {
        if (group == null) {
            return;
        }

        txt_title.setText(getString(R.string.group_members) + "(" + group.getMembers_size() + ")");


        memberIds = CommonHelperUtils.jsonToMemberIdList(group.members);
        if (memberIds != null && memberIds.size() > 0) {
            // 显示群组成员头像和昵称

            requestNextPageMembers();
//            showMembers(memberIds);
        } else {
            OneGroupHelper.getMemberListFromGroup(groupId, new RequestSuccessListener<List<String>>() {
                @Override
                public void onResponse(List<String> membersId) {
                    if (membersId != null) {
                        memberIds = membersId;
//                    showMembers(membersId);
                        requestNextPageMembers();
                    }
                }
            });
        }
    }


    // 显示群成员头像昵称的gridview
    private void showMembers(List<String> members) {
        if (adapter == null) {
            adapter = new GridAdapter(this, members);
            gridview.setAdapter(adapter);
        } else {
            adapter.refresh(members);
        }
    }

    @Override
    protected void setListener() {
        img_back.setOnClickListener(this);

        swipeRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                requestNextPageMembers();
            }
        });

    }

    void requestNextPageMembers() {
        if (memberIds == null) {
            return;
        }
        int start = memberIds.size();
        int stop = memberIds.size();
        if (memberIds.size() > (page + 1) * ITEM_PAGE_SIZE) {
            stop = (page + 1) * ITEM_PAGE_SIZE;
        } else {
            swipeRefreshLayout.setEnableLoadMore(false);
        }
        if (memberIds.size() > page * ITEM_PAGE_SIZE) {
            start = page * ITEM_PAGE_SIZE;
        }

        if (start != stop) {
            requestMembers = memberIds.subList(start, stop);
            OneChatHelper.GetOtherUserInfoListRequest(requestMembers, new RequestSuccessListener<ListResult<UserInfoBean>>() {
                @Override
                public void onResponse(ListResult<UserInfoBean> userInfoBeanListResult) {
                    showMembers.addAll(requestMembers);
                    showMembers(showMembers);
                    swipeRefreshLayout.finishLoadMore();
//                            adapter.notifyDataSetChanged();
                }
            });
        }
        page++;
        swipeRefreshLayout.finishLoadMore(Constants.MAX_REFRESH_LOADING_TIME);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            Utils.finish(GroupMembersActivity.this);

        } else {
        }
    }


    // 群组成员gridadapter
    private class GridAdapter extends BaseAdapter {

        private List<String> objects;
        Context context;

        public GridAdapter(Context context, List<String> objects) {

            this.objects = objects;
            this.context = context;
        }

        public void refresh(List<String> objects) {
            this.objects = objects;
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView,
                            final ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.item_chatsetting_gridview, null);
            }
            ImageView iv_avatar = (ImageView) convertView
                    .findViewById(R.id.iv_avatar);
            TextView tv_username = (TextView) convertView
                    .findViewById(R.id.tv_username);

            ImageView avatar_bg = (ImageView) convertView
                    .findViewById(R.id.iv_userhead_bg);
            // 普通item，显示群组成员
            final String userid = objects.get(position);
            final UserContactItem user = OneAccountHelper.getDatabase().getUserContactItemById(userid);
            if (user != null) {
                String useravatar = user.getAvatar();
                tv_username.setText(user.getUserName());
                ImageUtils.displayAvatarNetImage(context, useravatar, iv_avatar, user.getSex());
                iv_avatar.setTag(useravatar);
            } else {
                tv_username.setText("");
                ImageUtils.displayAvatarNetImage(context, "", iv_avatar, UserInfoUtils.USER_SEX_UNKNOWN);
            }
            if (group.owner.equals(userid)) {
                avatar_bg.setVisibility(View.VISIBLE);
                avatar_bg.setImageResource(R.drawable.group_owner_avatar_bg);
            } else if (group.getGroupAdminMap().containsKey(userid)) {
                avatar_bg.setVisibility(View.VISIBLE);
                avatar_bg.setImageResource(R.drawable.group_admin_avatar_bg);
            } else {
                avatar_bg.setVisibility(View.GONE);
            }

            if (isInDeleteMode) {
                // 如果是删除模式下，显示减人图标
                convertView.findViewById(R.id.badge_delete).setVisibility(
                        View.VISIBLE);
            } else {
                convertView.findViewById(R.id.badge_delete).setVisibility(
                        View.INVISIBLE);
            }
            iv_avatar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isInDeleteMode) {
                        // TODO 打开群组详情页面
                        JumpAppPageUtil.jumpAllGroupMember(context, groupId, true);

                        // 如果是删除自己，return
                        if (OneAccountHelper.getAccountId()
                                .equals(userid)) {
                            Utils.showLongToast(GroupMembersActivity.this,
                                    "不能删除自己");
                            return;
                        }
                        if (!NetUtils.hasNetwork(getApplicationContext())) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    getString(R.string.string_network_disconnect),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        DialogUtil.simpleDialog(context, getString(R.string.sure_delete_user), new DialogUtil.ConfirmCallBackInf() {
                            @Override
                            public void onConfirmClick(String content) {
                                deleteMembersFromGroup(userid);
                            }
                        });
                    } else {
                        JumpAppPageUtil.jumpOtherUserInfoPage(context, userid);
                    }
                }
            });

            return convertView;
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public String getItem(int position) {
            // TODO Auto-generated method stub
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
    }

    private void deleteMembersFromGroup(final String userId) {

        List<String> deleteMemberIds = new ArrayList<>();
        deleteMemberIds.add(userId);

        //更新群聊请求
        OneGroupHelper.removeOccupants(groupId, deleteMemberIds, new RequestSuccessListener<Boolean>() {
            @Override
            public void onResponse(Boolean success) {
                if (success) {
                    if (memberIds == null) {
                        return;
                    }
                    memberIds.remove(userId);
                    group.members = GsonUtils.objToJson(memberIds);
                    group.update_time = BtsApplication.getAdjustTimeNowMillis();
                    group.members_size--;
                    OneAccountHelper.getDatabase().putGroupInfo(group);

                    showMembers.remove(userId);
                    showMembers(showMembers);
                    ToastUtils.simpleToast(R.string.delete_success);
                } else {
                    ToastUtils.simpleToast(R.string.delete_failed);
                }
            }
        });


    }
}
