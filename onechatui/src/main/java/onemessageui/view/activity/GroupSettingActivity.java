package onemessageui.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneChatHelper;
import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.graphenechain.models.UserContactItem;
import oneapp.onechat.oneandroid.graphenechain.models.UserGroupInfoItem;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsApplication;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.Constants;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.NetUtils;
import oneapp.onechat.oneandroid.onemessage.common.CommonHelperUtils;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.onewallet.modle.ListResult;
import oneapp.onechat.oneandroid.onewallet.modle.MapResult;
import oneapp.onechat.oneandroid.onewallet.modle.UploadImgResultBean;
import oneapp.onechat.oneandroid.onewallet.modle.UserInfoBean;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onechat.oneandroid.onewallet.util.UiUtils;
import onemessageui.chat.widght.ExpandGridView;
import onemessageui.dialog.DialogUtil;
import onemessageui.view.OneBaseActivity;
import onemessageui.widght.switchbutton.SwitchButton;
import onewalletui.onekeyshare.ShareUtils;
import onewalletui.util.ImageUtils;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;
import sdk.android.onechatui.R;


//群设置
public class GroupSettingActivity extends OneBaseActivity implements
        OnClickListener {
    private ImageView img_back;
    private TextView tv_groupname;
    private TextView txt_title;// 标题，成员总数
    private ExpandGridView gridview;// 成员列表
    // 修改群名称、置顶、、、、
    private View re_change_groupname, re_change_groupavatar, view_share_group, view_copy_group_url, view_private_group;
    private ImageView mGroupAvatarIv;
    private RelativeLayout rl_switch_chattotop;
    private RelativeLayout rl_switch_block_groupmsg;
    private RelativeLayout re_clear;

    private TextView tv_group_url;

    // 状态变化
    private CheckBox check_top, check_closetip;
    // 删除并退出

    private Button exitBtn;
    private String hxid;
    boolean is_admin = false;// 是否是群主
    String longClickUsername = null;

    private String groupId;
    private UserGroupInfoItem group;
    private GridAdapter adapter;

    private TextView mLookAllMembersTv;

    private String mAddGroupUrl;
    private LinearLayout wait_review_user;
    private LinearLayout ll_group_manage;
    private RelativeLayout re_group_id;
    private RelativeLayout re_group_nick;
    private RelativeLayout re_group_qrcode;
    private RelativeLayout re_group_notice;
    private SwitchButton sb_take_push;
    private TextView mTvGroupid;
    private TextView mTvGroupnick;
    private boolean isAdmin = false; //是否是群管理
    private ImageView mIvGroupAvatar;
    private TextView mTvGroupName;
    private TextView mTvGroupUid;
    private String id = "";
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ifUnlockThisActivity = false;

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_groupsetting);
    }

    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(getString(R.string.chat_info));
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        tv_groupname = (TextView) findViewById(R.id.txt_groupname);
        gridview = (ExpandGridView) findViewById(R.id.gridview);

        re_change_groupname = findViewById(R.id.re_change_groupname);
        re_change_groupavatar = findViewById(R.id.re_change_groupavatar);
        mGroupAvatarIv = (ImageView) findViewById(R.id.iv_group_avatar);
        rl_switch_chattotop = (RelativeLayout) findViewById(R.id.rl_switch_chattotop);
        rl_switch_block_groupmsg = (RelativeLayout) findViewById(R.id.rl_switch_block_groupmsg);
        re_clear = (RelativeLayout) findViewById(R.id.re_clear);
        view_share_group = findViewById(R.id.view_share_group);
        view_copy_group_url = findViewById(R.id.view_copy_group_url);
        view_private_group = findViewById(R.id.view_private_group);
        tv_group_url = (TextView) findViewById(R.id.tv_group_url);

        exitBtn = (Button) findViewById(R.id.btn_exit_grp);

        mLookAllMembersTv = (TextView) findViewById(R.id.tv_look_all_members);
        wait_review_user = (LinearLayout) findViewById(R.id.wait_review_user);

        //群设置新更改
        ll_group_manage = (LinearLayout) findViewById(R.id.ll_group_manage);
        re_group_id = (RelativeLayout) findViewById(R.id.re_group_id);
        re_group_nick = (RelativeLayout) findViewById(R.id.re_group_nick);
        re_group_qrcode = (RelativeLayout) findViewById(R.id.re_group_qrcode);
        re_group_notice = (RelativeLayout) findViewById(R.id.re_group_notice);
        sb_take_push = (SwitchButton) findViewById(R.id.sb_take_push);
        mTvGroupid = (TextView) findViewById(R.id.txt_groupid);
        mTvGroupnick = (TextView) findViewById(R.id.txt_groupnick);
        mIvGroupAvatar = (ImageView) findViewById(R.id.iv_group_avatar);
        mTvGroupName = (TextView) findViewById(R.id.tv_group_name);
        mTvGroupUid = (TextView) findViewById(R.id.tv_group_uid);
    }

    @Override
    protected void initView() {
        hxid = OneAccountHelper.getAccountId();
    }

    @Override
    protected void initData() {
    }

    private void initGroupInfo() {
        // 获取传过来的groupid
        groupId = getIntent().getStringExtra(Constants.GROUP_ID);
// 获取本地该群数据
        group = OneAccountHelper.getDatabase().getUserGroupInfoItemById(groupId);
        if (group == null) {
            // 去网络中查找该群
            OneGroupHelper.GetItemGroupInfoRequest(groupId, new RequestSuccessListener<UserGroupInfoItem>() {
                @Override
                public void onResponse(UserGroupInfoItem userGroupInfoItem) {
                    if (userGroupInfoItem == null) {
                        finish();
                        ToastUtils.simpleToast(getString(R.string.you_are_group));
                    } else {
                        initGroupInfo();
                    }
                }
            });
        }

        OneGroupHelper.GetItemGroupInfoRequest(groupId, new RequestSuccessListener<UserGroupInfoItem>() {
            @Override
            public void onResponse(UserGroupInfoItem userGroupInfoItem) {
                if (userGroupInfoItem == null) {
                    finish();
                    ToastUtils.simpleToast(getString(R.string.you_are_group));
                } else {
                    group = OneAccountHelper.getDatabase().getUserGroupInfoItemById(groupId);
                    description = group.description;
                    id = userGroupInfoItem.id;
                    updateView();
                }
            }
        });

        updateView();

        // TODO: 2017/12/12 hs
        mAddGroupUrl = ServiceConstants.GetShareGroupUrl(groupId);
        tv_group_url.setText(mAddGroupUrl);

        if (group.getGroupAdminMap() != null && group.getGroupAdminMap().containsKey(OneAccountHelper.getAccountId())) {
            isAdmin = true;
        } else {
            isAdmin = false;
        }

//        RequestUtils.GetSingleGroupMessageRequest(groupId, new RequestSuccessListener<SingleGroupMessageBean>() {
//
//            @Override
//            public void onResponse(SingleGroupMessageBean singleGroupMessageBean) {
//                if(singleGroupMessageBean != null){
//                    description = singleGroupMessageBean.getDescription();
//                    id = singleGroupMessageBean.getId();
//                }
//            }
//        });


        OneGroupHelper.getMemberListFromGroup(groupId, new RequestSuccessListener<List<String>>() {
            @Override
            public void onResponse(List<String> membersId) {
                showMembers(membersId);
            }
        });

    }

    void updateView() {
        if (group == null) {
            return;
        }
        if (group.public_status == CommonConstants.CHAT_GROUP_STATUS_PUBLIC) {
            view_share_group.setVisibility(View.VISIBLE);
            view_copy_group_url.setVisibility(View.VISIBLE);
            view_private_group.setVisibility(View.GONE);
        } else {
            view_share_group.setVisibility(View.GONE);
            view_copy_group_url.setVisibility(View.GONE);
            view_private_group.setVisibility(View.VISIBLE);
        }


        // 获取封装的群名（里面封装了显示的群名和群组成员的信息）
        String group_name = group.group_name;
        // 获取群成员信息
        tv_groupname.setText(group_name);
        txt_title.setText(getString(R.string.chat_info) + "(" + group.getMembers_size() + ")");

        ImageUtils.displayCircleNetImage(context, group.getGroupAvatarUrl(), mGroupAvatarIv, R.drawable.default_group);

        List<String> tempMembers = CommonHelperUtils.jsonToMemberIdList(group.members);

        if (tempMembers != null && tempMembers.size() > 0) {

            // 显示群组成员头像和昵称
            showMembers(tempMembers);
        }
        // 判断是否是群主，是群主有删成员的权限，并显示减号按钮
        if (null != group.owner && null != hxid
                && hxid.equals(group.owner)) {
            is_admin = true;
        }

        if (group != null && is_admin || isAdmin) {
//            wait_review_user.setVisibility(View.VISIBLE);
            ll_group_manage.setVisibility(View.VISIBLE);
        }

        mTvGroupUid.setText(getResources().getString(R.string.group_id) + ":" + id);
        mTvGroupName.setText(group.group_name);
        ImageUtils.displayCircleNetImage(context, group.getGroupAvatarUrl(), mIvGroupAvatar, R.drawable.default_group);

        if (group.public_status != CommonConstants.CHAT_GROUP_STATUS_PUBLIC) {
            re_group_qrcode.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initGroupInfo();
    }

    // 显示群成员头像昵称的gridview
    private void showMembers(List<String> members) {
        if (members == null) {
            return;
        }
        int subInt = members.size() > CommonConstants.MAX_GROUP_SHOW_MEMBERS ? CommonConstants.MAX_GROUP_SHOW_MEMBERS : members.size();
        List<String> showMembers = members.subList(0, subInt);
        OneChatHelper.GetOtherUserInfoListRequest(showMembers, new RequestSuccessListener<ListResult<UserInfoBean>>() {
            @Override
            public void onResponse(ListResult<UserInfoBean> userInfoBeanListResult) {
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new GridAdapter(this, showMembers);
        gridview.setAdapter(adapter);


        // 设置OnTouchListener,为了让群主方便地推出删除模》
        gridview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (adapter.isInDeleteMode) {
                            adapter.isInDeleteMode = false;
                            adapter.notifyDataSetChanged();
                            return true;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void setListener() {
        re_change_groupname.setOnClickListener(this);
        re_change_groupavatar.setOnClickListener(this);
        rl_switch_chattotop.setOnClickListener(this);
        rl_switch_block_groupmsg.setOnClickListener(this);
        re_clear.setOnClickListener(this);
        exitBtn.setOnClickListener(this);
        img_back.setOnClickListener(this);
        view_share_group.setOnClickListener(this);
        view_copy_group_url.setOnClickListener(this);
        mLookAllMembersTv.setOnClickListener(this);
        wait_review_user.setOnClickListener(this);

        //群设置新更改
        ll_group_manage.setOnClickListener(this);
        re_group_id.setOnClickListener(this);
        re_group_nick.setOnClickListener(this);
        re_group_notice.setOnClickListener(this);
        re_group_qrcode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            Utils.finish(GroupSettingActivity.this);

        } else if (i == R.id.btn_exit_grp) {
            DialogUtil.simpleDialog(context, getString(R.string.action_ok) + getString(R.string.out_chat_group), new DialogUtil.ConfirmCallBackInf() {
                @Override
                public void onConfirmClick(String content) {
                    deleteMembersFromGroup(hxid);
                }
            });

        } else if (i == R.id.re_change_groupname) {//修改群名称
            if (group != null && is_admin) {
                DialogUtil.editNameDialog(context, getString(R.string.change_groupname), group.group_name, new DialogUtil.ConfirmCallBackInf() {
                    @Override
                    public void onConfirmClick(String content) {
                        changeGroupName(content);
                    }
                });
            } else {
                return;
            }

        } else if (i == R.id.re_change_groupavatar) {//修改群头像
            if (group != null && is_admin) {
                tempSaveFilePath = BaseUtils.getSaveBitmapFile() + ImageUtils.createPhotoFileName();
                DialogUtil.chooceAndCropImageDialog(this, tempSaveFilePath);
            } else {
                return;
            }

        } else if (i == R.id.re_clear) {
            if (group == null) {
                return;
            }
            DialogUtil.simpleDialog(context, getString(R.string.clear_mesage_tip), new DialogUtil.ConfirmCallBackInf() {
                @Override
                public void onConfirmClick(String content) {
                    OneAccountHelper.getDatabase().deleteMessageByGroupid(group.group_uid);
                    ToastUtils.simpleToast(R.string.clear_mesage_success);
                }
            });


        } else if (i == R.id.view_share_group) {//分享群
            ShareUtils.showShareWebUrl(context, getString(R.string.share_group), getString(R.string.group_url_start) + mAddGroupUrl, mAddGroupUrl);

        } else if (i == R.id.view_copy_group_url) {//复制群链接
            UiUtils.copy(context, getString(R.string.group_url_start) + mAddGroupUrl);

        } else if (i == R.id.tv_look_all_members) {//查看群成员
            // TODO 打开群组详情页面
            JumpAppPageUtil.jumpAllGroupMember(context, groupId, false);

        } else if (i == R.id.wait_review_user) {//等待审核成员
            JumpAppPageUtil.jumpWaitReviewPage(context);

        } else if (i == R.id.ll_group_manage) {//                JumpAppPageUtil.jumpGroupManagePage(context,groupId);
            JumpAppPageUtil.jumpNativeWebView(context, ServiceConstants.GetGroupManageUrl().getHost_url(), "", CommonConstants.H5_TYPE_GROUP, groupId);

        } else if (i == R.id.re_group_notice) {
            JumpAppPageUtil.jumpNativeWebView(context, ServiceConstants.GetGroupNaticeUrl().getHost_url(), getString(R.string.group_notice), CommonConstants.H5_TYPE_GROUP, groupId);

        } else if (i == R.id.re_group_qrcode) {
            JumpAppPageUtil.jumpGroupQrCodePage(context, groupId, mAddGroupUrl, id, description);

        } else if (i == R.id.re_group_id) {
        } else {
        }
    }


    // TODO: 2017/10/24
    private void changeGroupName(String newGroupName) {
        group.group_name = newGroupName;
        group.update_time = BtsApplication.getAdjustTimeNowMillis();
        OneAccountHelper.getDatabase().putGroupInfo(group);
        //更新群请求
        OneGroupHelper.updateGroupWithConfiguration(group, null);
        tv_groupname.setText(group.group_name);
    }

    // 群组成员gridadapter
    private class GridAdapter extends BaseAdapter {

        public boolean isInDeleteMode;
        private List<String> objects;
        Context context;

        public GridAdapter(Context context, List<String> objects) {

            this.objects = objects;
            this.context = context;
            isInDeleteMode = false;
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
            ImageView badge_delete = (ImageView) convertView
                    .findViewById(R.id.badge_delete);
            ImageView avatar_bg = (ImageView) convertView
                    .findViewById(R.id.iv_userhead_bg);

            // 最后一个item，减人按钮
            if (position == getCount() - 1 && is_admin) {
                tv_username.setText("");
                badge_delete.setVisibility(View.GONE);
                iv_avatar.setImageResource(R.drawable.icon_btn_deleteperson);

                if (isInDeleteMode) {
                    // 正处于删除模式下，隐藏删除按钮
                    convertView.setVisibility(View.GONE);
                } else {

                    convertView.setVisibility(View.VISIBLE);
                }

                iv_avatar.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
//                        isInDeleteMode = true;
//                        notifyDataSetChanged();
                        JumpAppPageUtil.jumpAllGroupMember(context, groupId, true);

                    }
                });

            } else if ((is_admin && position == getCount() - 2)
                    || (!is_admin && position == getCount() - 1)) { // 添加群组成员按钮
                tv_username.setText("");
                badge_delete.setVisibility(View.GONE);
                iv_avatar.setImageResource(R.drawable.icon_btn_add_person);
                // 正处于删除模式下,隐藏添加按钮
                if (isInDeleteMode) {
                    convertView.setVisibility(View.GONE);
                } else {
                    convertView.setVisibility(View.VISIBLE);
                }
                iv_avatar.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 进入选人页面
                        startActivity((new Intent(GroupSettingActivity.this,
                                AddGroupChatActivity.class).putExtra(
                                Constants.GROUP_ID, groupId).putExtra(Constants.User_ID, group.owner)));
                    }
                });
            } else { // 普通item，显示群组成员
                final String userid = objects.get(position);
                final UserContactItem user = OneAccountHelper.getDatabase().getUserContactItemById(userid);
                if (user != null) {
                    String useravatar = user.getAvatar();
                    tv_username.setText(user.getUserName());
                    ImageUtils.displayAvatarNetImage(context, useravatar, iv_avatar, user.getSex());
                    iv_avatar.setTag(useravatar);
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

                            // 如果是删除自己，return
                            if (OneAccountHelper.getAccountId()
                                    .equals(userid)) {
                                Utils.showLongToast(GroupSettingActivity.this,
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
                            String usernike = "";
                            if (user != null) {
                                usernike = user.getUserName();
                            }
                            DialogUtil.simpleDialog(context, getString(R.string.sure_delete_user) + usernike, new DialogUtil.ConfirmCallBackInf() {
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
            }
            return convertView;
        }

        @Override
        public int getCount() {
            if (is_admin) {
                return objects.size() + 2;
            } else {

                return objects.size() + 1;
            }
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

    private void deleteMembersFromGroup(String userId) {
        final boolean ifOutGroup = userId.equals(hxid);

        List<String> deleteMemberIds = new ArrayList<>();
        deleteMemberIds.add(userId);

        //更新群聊请求
        OneGroupHelper.removeOccupants(groupId, deleteMemberIds, new RequestSuccessListener<Boolean>() {
            @Override
            public void onResponse(Boolean success) {
                if (ifOutGroup) {
                    if (success) {
                        //退群
                        OneAccountHelper.getDatabase().deleteMessageByGroupid(group.group_uid);
                        OneAccountHelper.getDatabase().deleteUserGroupByGrouid(group.group_uid);
                        Intent intent = new Intent();
                        intent.setAction(CommonHelperUtils.getFinishBroadcastAction());
                        intent.putExtra(JumpParamsContants.INTENT_ACTIVITY, "NewChatActivity");
                        sendBroadcast(intent);
                        finish();
                    } else {
                        ToastUtils.simpleToast(R.string.group_leave_fail);
                    }
                } else {
                    if (success) {
                        ToastUtils.simpleToast(R.string.delete_success);
                    } else {
                        ToastUtils.simpleToast(R.string.delete_failed);
                    }
                }
            }
        });

        if (!ifOutGroup) {
            updateView();
        }
    }

    private String tempSaveFilePath;
    Uri uri;

    /**
     * 获取照片
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == DialogUtil.PHOTO_REQUEST_GALLERY) {
                if (data != null) {
                    // 得到图片的全路径
                    uri = data.getData();

                    uri = UiUtils.crop(uri, this);
                }

            } else if (requestCode == DialogUtil.PHOTO_REQUEST_CAMERA) {
                if (BaseUtils.isMounted()) {
                    File tempFile = new File(tempSaveFilePath);
                    uri = BaseUtils.getImageUriForFile(context, tempFile);

                    uri = UiUtils.crop(uri, this);

                } else {
                    Toast.makeText(this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == DialogUtil.PHOTO_REQUEST_CUT) {
                try {

                    File file = BaseUtils.uri2File(this, uri);
                    OneGroupHelper.uploadGroupAvatar(groupId, file, new RequestSuccessListener<MapResult<UploadImgResultBean>>() {
                        @Override
                        public void onResponse(MapResult<UploadImgResultBean> avatarBeanResult) {
                            OneGroupHelper.GetItemGroupInfoRequest(groupId, new RequestSuccessListener<UserGroupInfoItem>() {
                                @Override
                                public void onResponse(UserGroupInfoItem userGroupInfoItem) {
                                    if (userGroupInfoItem != null) {
                                        ImageUtils.displayCircleNetImage(context, userGroupInfoItem.getGroupAvatarUrl(), mGroupAvatarIv, R.drawable.default_group);
                                    }
                                }
                            });
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
