package onemessageui.view.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Charsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import oneapp.onechat.oneandroid.chatsdk.OneOpenHelper;
import oneapp.onechat.oneandroid.onewallet.util.WalletUtils;
import onemessageui.widght.SideBar;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.graphenechain.models.MemoMessage;
import oneapp.onechat.oneandroid.graphenechain.models.UserChatItem;
import oneapp.onechat.oneandroid.graphenechain.models.UserContactItem;
import oneapp.onechat.oneandroid.graphenechain.models.UserGroupInfoItem;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsApplication;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.Constants;
import oneapp.onechat.oneandroid.onemessage.bean.AddGroupMemberResult;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.DensityUtil;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.NetUtils;
import onemessageui.utils.CommonUtils;
import oneapp.onechat.oneandroid.onemessage.common.CommonHelperUtils;
import onemessageui.common.PingYinUtil;
import onemessageui.common.PinyinComparator;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import onemessageui.utils.ViewHolder;
import oneapp.onechat.oneandroid.onewallet.modle.MapResult;
import oneapp.onechat.oneandroid.onewallet.modle.UserInfoBean;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import onewalletui.util.ImageUtils;
import oneapp.onechat.oneandroid.onewallet.util.ListUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onechat.oneandroid.onewallet.util.UserInfoUtils;
import oneapp.onecore.graphenej.Util;
import onemessageui.view.OneBaseActivity;
import onewalletui.util.jump.JumpAppPageUtil;

//import oneapp.onemessage.GloableParams;

//import oneapp.onemessage.GloableParams;
//import oneapp.onemessage.bean.GroupInfo;
//import oneapp.onemessage.bean.User;

public class AddGroupChatActivity extends OneBaseActivity implements
        OnClickListener, OnItemClickListener {
    private ImageView iv_search, img_back;
    private TextView tv_header, txt_title, txt_right;

    private ListView listView;
    private EditText et_search;
    private SideBar indexBar;
    private TextView mDialogText;
    private WindowManager mWindowManager;
    /**
     * 是否为一个新建的群组
     */
    protected boolean isCreatingNewGroup;
    /**
     * 是否为单选
     */
    private boolean isSignleChecked;
    private ContactAdapter contactAdapter;
    /**
     * group中一开始就有的成员
     */
    private HashMap<String, String> exitingMembers = new HashMap<>();

    private HashMap<String, String> oldMembers = new HashMap<>();

    private List<UserContactItem> alluserList;// 好友列表

    // 可滑动的显示选中用户的View
    private LinearLayout menuLinerLayout;

    // 选中用户总数,右上角显示
    int total = 0;
    private String userId = null;
    private String groupUid = null;
    private String groupname;
    // 添加的列表
    private HashMap<String, String> addMap = new HashMap<>();
    private UserGroupInfoItem currentGroup;

    private View mSetPublicStatusView;
    private ImageView mPublicGrouopIv, mPrivateGroupIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_chatroom);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWindowManager.removeViewImmediate(mDialogText);
    }


    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_right = (TextView) this.findViewById(R.id.txt_right);
        txt_right.setText(R.string.action_ok);
//        txt_right.setTextColor(ContextCompat.getColor(context, R.color.base_color));
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        menuLinerLayout = (LinearLayout) this
                .findViewById(R.id.linearLayoutMenu);
        et_search = (EditText) this.findViewById(R.id.et_search);
        listView = (ListView) findViewById(R.id.list);
        iv_search = (ImageView) this.findViewById(R.id.iv_search);
        mDialogText = (TextView) LayoutInflater.from(this).inflate(
                R.layout.list_position, null);
        mDialogText.setVisibility(View.INVISIBLE);
        indexBar = (SideBar) findViewById(R.id.sideBar);
        indexBar.setListView(listView);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(mDialogText, lp);
        indexBar.setTextView(mDialogText);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View headerView = layoutInflater.inflate(R.layout.item_chatroom_header,
                null);
        tv_header = (TextView) headerView.findViewById(R.id.tv_header);
//        listView.addHeaderView(headerView);
        listView.setOnItemClickListener(this);

        mSetPublicStatusView = findViewById(R.id.view_set_public_status);
        mPublicGrouopIv = (ImageView) findViewById(R.id.iv_public_group);
        mPrivateGroupIv = (ImageView) findViewById(R.id.iv_private_group);
    }

    @Override
    protected void initView() {
        groupUid = getIntent().getStringExtra(Constants.GROUP_ID);
        userId = getIntent().getStringExtra(Constants.User_ID);
        if (StringUtils.equalsNull(userId)) {
            userId = OneAccountHelper.getAccountId();
        }
        if (!StringUtils.equalsNull(groupUid)) {
            txt_title.setText(R.string.group_invitation);
            isCreatingNewGroup = false;
            mSetPublicStatusView.setVisibility(View.GONE);

            currentGroup = OneAccountHelper.getDatabase().getUserGroupInfoItemById(groupUid);
            if (currentGroup != null) {

                List<String> tempMembers = CommonHelperUtils.jsonToMemberIdList(currentGroup.members);
                for (String memberBean : tempMembers) {
                    exitingMembers.put(memberBean, memberBean);
                    oldMembers.put(memberBean, memberBean);
                }
                groupname = currentGroup.group_name;
            }
        } else {
            txt_title.setText(R.string.open_group_chats);
            isCreatingNewGroup = true;
            mSetPublicStatusView.setVisibility(View.VISIBLE);
            exitingMembers.put(userId, userId);
            total = 1;
        }
        addMap.putAll(exitingMembers);
    }

    @Override
    protected void initData() {
        refreshList();
        OneAccountHelper.getFriendListWithCompletion(new RequestSuccessListener<List<UserInfoBean>>() {
            @Override
            public void onResponse(List<UserInfoBean> userInfoBeans) {
                refreshList();
            }
        });
    }

    private void refreshList() {
        try { // 获取好友列表
            alluserList = OneAccountHelper.getDatabase().getUserContacts(CommonConstants.MAX_LOAD_FRIEND_SIZE);

            contactAdapter = new ContactAdapter(AddGroupChatActivity.this,
                    alluserList);

            listView.setAdapter(contactAdapter);

            indexBar.setCharList(contactAdapter.getPinyinCharList());
        } catch (Exception e) {

        }
    }

    @Override
    protected void setListener() {
        img_back.setOnClickListener(this);
        tv_header.setOnClickListener(this);
        txt_right.setOnClickListener(this);
        et_search.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0) {
                    String str_s = et_search.getText().toString().trim();
                    List<UserContactItem> users_temp = ListUtils.searchUserList(alluserList, str_s);

                    contactAdapter = new ContactAdapter(
                            AddGroupChatActivity.this, users_temp, str_s);
                    listView.setAdapter(contactAdapter);
                } else {
                    contactAdapter = new ContactAdapter(
                            AddGroupChatActivity.this, alluserList);
                    listView.setAdapter(contactAdapter);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        findViewById(R.id.view_public_group).setOnClickListener(this);
        findViewById(R.id.view_private_group).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {

            finish();

        } else if (i == R.id.tv_header) {// TODO 打开群列表

        } else if (i == R.id.txt_right) {
            save();

        } else if (i == R.id.view_public_group) {
            mPublicGrouopIv.setImageResource(R.drawable.rb_set_sex_selected);
            mPrivateGroupIv.setImageResource(R.drawable.rb_set_sex_normal);
            group_public_status = CommonConstants.CHAT_GROUP_STATUS_PUBLIC;

        } else if (i == R.id.view_private_group) {
            mPublicGrouopIv.setImageResource(R.drawable.rb_set_sex_normal);
            mPrivateGroupIv.setImageResource(R.drawable.rb_set_sex_selected);
            group_public_status = CommonConstants.CHAT_GROUP_STATUS_PRIVATE;

        } else {
        }
    }

    /**
     * 确认选择的members
     *
     * @param
     */
    public void save() {

        if (addMap.size() == 0) {
            Utils.showLongToast(AddGroupChatActivity.this, "请选择用户");
            return;
        }

        if (!NetUtils.hasNetwork(getApplicationContext())) {
            Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.string_network_disconnect),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 如果只有一个用户说明只是单聊,并且不是从群组加人
        if (addMap.size() == 1 && isCreatingNewGroup) {
            Utils.showLongToast(AddGroupChatActivity.this, "请选择用户");
            return;
            // FIXME: 2017/12/18 hs
//            String userId = addList.get(0);
//            UserContactItem user = OneAccountHelper.getDatabase().getUserContactItemById(userId);
//
//            Intent intent = new Intent(AddGroupChatActivity.this,
//                    ChatActivity.class);
//            intent.putExtra(Constants.NAME, user.getUserName());
//            intent.putExtra(Constants.TYPE, ChatActivity.CHATTYPE_SINGLE);
//            intent.putExtra(Constants.User_ID, user.id);
//            startActivity(intent);
//            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else {
            if (isCreatingNewGroup) {
                showLoadingDialog(getString(R.string.create_groupchat_loading));
            } else {
                showLoadingDialog("正在加人...");
            }
            creatNewGroup(addMap);// 创建群组
        }
    }

    class ContactAdapter extends BaseAdapter implements SectionIndexer {
        private Context mContext;
        private boolean[] isCheckedArray;
        private Bitmap[] bitmaps;
        private List<UserContactItem> list = new ArrayList<UserContactItem>();
        private String searchStr = "";

        PinyinComparator pinyinComparator;

        @SuppressWarnings("unchecked")
        public ContactAdapter(Context mContext, List<UserContactItem> users) {
            this.mContext = mContext;
            this.list = users;
            bitmaps = new Bitmap[list.size()];
            isCheckedArray = new boolean[list.size()];
            // 排序(实现了中英文混排)
            pinyinComparator = new PinyinComparator();
            Collections.sort(list, pinyinComparator);
        }

        @SuppressWarnings("unchecked")
        public ContactAdapter(Context mContext, List<UserContactItem> users, String searchStr) {
            this.mContext = mContext;
            this.list = users;
            bitmaps = new Bitmap[list.size()];
            isCheckedArray = new boolean[list.size()];
            // 排序(实现了中英文混排)
            Collections.sort(list, new PinyinComparator());
            this.searchStr = searchStr;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final UserContactItem user = list.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.contact_item, null);
            }

            View contactView = ViewHolder.get(convertView, R.id.view_contact);
            ImageView ivAvatar = ViewHolder.get(convertView,
                    R.id.contactitem_avatar_iv);
            TextView tvCatalog = ViewHolder.get(convertView,
                    R.id.contactitem_catalog);
            TextView tvNick = ViewHolder
                    .get(convertView, R.id.contactitem_nick);
            final CheckBox checkBox = ViewHolder
                    .get(convertView, R.id.checkbox);
            checkBox.setVisibility(View.VISIBLE);
            String catalog = PingYinUtil.converterToFirstSpell(
                    user.getUserName()) + "";
            if (position == 0) {
                tvCatalog.setVisibility(View.VISIBLE);
                tvCatalog.setText(catalog);
            } else {
                UserContactItem Nextuser = list.get(position - 1);
                String lastCatalog = PingYinUtil.converterToFirstSpell(
                        Nextuser.getUserName()) + "";
                if (catalog.equals(lastCatalog)) {
                    tvCatalog.setVisibility(View.GONE);
                } else {
                    tvCatalog.setVisibility(View.VISIBLE);
                    tvCatalog.setText(catalog);
                }
            }
            ImageUtils.displayAvatarNetImage(context, user.avatar, ivAvatar, user.getSex());

            String username = user.getUserName().toLowerCase();
            int index = username.indexOf(searchStr);
            if (index >= 0 && !StringUtils.equalsNull(searchStr)) {
                SpannableString span = new SpannableString(username);
                span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.base_color)), index, index + searchStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvNick.setText(user.getUserName());
                tvNick.setText(span);  //设置字体变颜色
            } else
                tvNick.setText(user.getUserName());

            if (addMap != null && addMap.containsKey(user.id)) {
                checkBox.setChecked(true);
                isCheckedArray[position] = true;
            } else {
                checkBox.setChecked(false);
                isCheckedArray[position] = false;
            }
            // 群组中原来的成员一直设为选中状态
            if (exitingMembers != null
                    && exitingMembers.containsKey(user.id)) {
                checkBox.setChecked(true);
                isCheckedArray[position] = true;
            } else {
                checkBox.setChecked(isCheckedArray[position]);
            }

            if (contactView != null) {
                contactView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean isChecked = !checkBox.isChecked();
                        // 群组中原来的成员一直设为选中状态
                        if (exitingMembers.containsKey(user.id)) {
                            isChecked = true;
                            checkBox.setChecked(true);
                            isCheckedArray[position] = isChecked;
                            return;
                        }
                        isCheckedArray[position] = isChecked;
                        // 如果是单选模式
                        if (isSignleChecked && isChecked) {
                            for (int i = 0; i < isCheckedArray.length; i++) {
                                if (i != position) {
                                    isCheckedArray[i] = false;
                                }
                            }
                            contactAdapter.notifyDataSetChanged();
                        }

                        if (isChecked) {
                            // 选中用户显示在滑动栏显示
                            showCheckImage(user);
                        } else {
                            // 用户显示在滑动栏删除
                            deleteImage(user);
                        }
                        checkBox.setChecked(isChecked);
                    }
                });
            }

            return convertView;
        }

        @Override
        public int getPositionForSection(int section) {
            for (int i = 0; i < list.size(); i++) {
                UserContactItem user = list.get(i);
                char firstChar = PingYinUtil
                        .converterToFirstSpell(user.getUserName());
                if (firstChar == section) {
                    return i;
                }
            }
            return 0;
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

        @Override
        public Object[] getSections() {
            return null;
        }

        public List<Character> getPinyinCharList() {
            if (pinyinComparator == null) {
                return new ArrayList<>();
            }
            return pinyinComparator.getCharList();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
//        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
//        checkBox.toggle();
    }

    // 即时显示被选中用户的头像和昵称。
    private void showCheckImage(UserContactItem glufineid) {
        if (exitingMembers.containsKey(glufineid.getUserName()) && groupUid != null) {
            return;
        }
        if (addMap.containsKey(glufineid.id)) {
            return;
        }
        total++;

        final ImageView imageView = new ImageView(this);
        LayoutParams lp = new LayoutParams(
                DensityUtil.dip2px(this, 40), DensityUtil.dip2px(this, 40));
        lp.setMargins(0, 0, DensityUtil.dip2px(this, 5), 0);
        imageView.setLayoutParams(lp);

        // 设置id，方便后面删除
        imageView.setTag(glufineid);
        ImageUtils.displayAvatarNetImage(context, glufineid.avatar, imageView, glufineid.getSex());

        menuLinerLayout.addView(imageView);
        txt_right.setText(getString(R.string.action_ok) + "(" + total + ")");
        if (total > 0) {
            iv_search.setVisibility(View.GONE);
        } else iv_search.setVisibility(View.VISIBLE);
        addMap.put(glufineid.id, glufineid.id);
    }

    private void deleteImage(UserContactItem glufineid) {
        View view = menuLinerLayout.findViewWithTag(glufineid);

        menuLinerLayout.removeView(view);
        total--;
        txt_right.setText("确定(" + total + ")");
        addMap.remove(glufineid.id);
        if (total < 1) {
            if (iv_search.getVisibility() == View.GONE) {
                iv_search.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 创建新群组
     *
     * @param newmembers
     */
    String newmembers = "";
    String strMemberList = "";
    int group_public_status = CommonConstants.CHAT_GROUP_STATUS_PUBLIC;

    private void creatNewGroup(final HashMap<String, String> members) {
        // TODO 请求服务器创建群组，服务端实现接口
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 调用sdk创建群组方法
                try {
                    if (!members.containsKey(OneAccountHelper.getAccountId())) {
                        members.put(OneAccountHelper.getAccountId(), OneAccountHelper.getAccountId());
                    }

                    boolean ifHasGroupName = !StringUtils.equalsNull(groupname);
                    int tempNum = 0;
                    for (String uid : members.values()) {
                        UserContactItem user = OneAccountHelper.getDatabase().getUserContactItemById(uid);

                        if (user != null) {
                            if (!exitingMembers.containsKey(uid)) {
                                newmembers += user.getNickname() + " ";
                            }
                            if (!ifHasGroupName) {
                                if (tempNum < 3) {
                                    if (tempNum == 0)
                                        groupname = user.getNickname();
                                    else
                                        groupname += "、" + user.getNickname();
                                } else if (tempNum == 4) {
                                    groupname += "...";
//                                break;
                                }
                            }
                        }

                        tempNum++;
                    }


                    // 创建群
                    final long timeUpdate = BtsApplication.getAdjustTimeNowMillis();
                    long timeCreate = 0;

                    String encrypt_key = null;
                    if (StringUtils.equalsNull(groupUid)) {
                        timeCreate = timeUpdate;
                        groupUid = UUID.randomUUID().toString();
                        encrypt_key = WalletUtils.generateRandomId(CommonConstants.DEFAULTGROUP_PASSWORD_LENGTH);

                    } else {
                        if (currentGroup != null) {
                            timeCreate = currentGroup.create_time;
                            encrypt_key = currentGroup.encrypt_key;
                            group_public_status = currentGroup.public_status;
                        }
                    }

                    strMemberList = CommonHelperUtils.chatMemberMapToJson(members, oldMembers, encrypt_key, group_public_status);

                    final UserGroupInfoItem group = new UserGroupInfoItem(null,
                            timeCreate, timeUpdate, groupname, groupUid, "", userId,
                            group_public_status, "", members.size(), 0, strMemberList, encrypt_key, null);
                    currentGroup = group;
                    if (isCreatingNewGroup)
                        //添加群聊请求
                        OneGroupHelper.createGroupWithConfiguration(group, new RequestSuccessListener<Integer>() {
                            @Override
                            public void onResponse(Integer code) {
                                switch (code) {
                                    case ServiceConstants.REQUEST_RESULT_CODE_OK:

//                                        ToastUtils.simpleToast(R.string.pls_wait_apply);

                                        sendCreateMessage();
                                        break;
                                    case ServiceConstants.REQUEST_CODE_GROUP_HAS_EXIST://群已创建
                                        OneGroupHelper.GetItemGroupInfoRequest(groupUid, new RequestSuccessListener<UserGroupInfoItem>() {
                                            @Override
                                            public void onResponse(UserGroupInfoItem userGroupInfoItem) {
                                                if (userGroupInfoItem != null) {
                                                    if (!userGroupInfoItem.group_name.equals(groupname)) {
                                                        //更新群请求
                                                        userGroupInfoItem.group_name = groupname;
                                                        OneGroupHelper.updateGroupWithConfiguration(userGroupInfoItem, null);
                                                        sendCreateMessage();
                                                    }
                                                    OneAccountHelper.getDatabase().putGroupInfo(userGroupInfoItem);
                                                }
                                            }
                                        });
                                        break;
                                    case ServiceConstants.REQUEST_CODE_GROUP_COUNT_EXCEED:
                                        hideLoadingDialog();
                                        ToastUtils.simpleToast(R.string.group_created_count_exceed);
                                        break;
                                    default:
                                        hideLoadingDialog();
                                        ToastUtils.simpleToast(R.string.erro);
                                        break;
                                }
                            }
                        });
                    else
                        //添加群成员请求
                        OneGroupHelper.addOccupants(groupUid, strMemberList, new RequestSuccessListener<MapResult<AddGroupMemberResult>>() {
                            @Override
                            public void onResponse(MapResult<AddGroupMemberResult> addGroupMemberResult) {
                                List<String> members = null;
                                if (OneOpenHelper.ifMapBeanHasNoNull(addGroupMemberResult)) {
                                    members = addGroupMemberResult.getData().getMap().members;

                                    newmembers = "";
                                    if (members != null && members.size() > 0) {
                                        for (int i = 0; i < members.size(); i++) {
                                            if (i >= 20) {
                                                newmembers += "...";
                                                break;
                                            }
                                            UserContactItem user = OneAccountHelper.getDatabase().getUserContactItemById(members.get(i));
                                            if (user != null) {
                                                newmembers += user.getNickname() + " ";
                                            }
                                        }
                                    }
                                    if (!StringUtils.equalsNull(newmembers)) {
                                        sendCreateMessage();
                                    } else {
                                        ToastUtils.simpleToast(R.string.pls_wait_apply);
                                        hideLoadingDialog();
                                        finish();
                                    }
                                } else {
                                    hideLoadingDialog();
                                    if (addGroupMemberResult != null) {
                                        switch (addGroupMemberResult.getCode()) {
                                            case ServiceConstants.REQUEST_CODE_JOIN_GROUP_NEED_ADMIN:
                                                ToastUtils.simpleToast(R.string.pls_wait_examine);
                                                break;
                                            case ServiceConstants.REQUEST_CODE_GROUP_MAX_PERSON:
                                                ToastUtils.simpleToast(R.string.group_max_people);
                                                break;
                                            default:
                                                ToastUtils.simpleToast(R.string.erro);
                                                break;
                                        }
                                    } else {
                                        ToastUtils.simpleToast(R.string.erro);
                                    }
                                }
                            }
                        });
                    group.members = "";
                    OneAccountHelper.getDatabase().putGroupInfo(group);
//                        MessageSenderHandler.getInstance().startMessageSender();

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Utils.showLongToast(AddGroupChatActivity.this,
                                    "创建失败");
                            hideLoadingDialog();
                        }
                    });
                }

            }
        }).start();
    }

    void sendCreateMessage() {
        if (currentGroup == null) {
            return;
        }
        if (true) {
            sendFirstMessage();
            return;
        } else {
//            return;
        }

        try {
            // 发送建群的命令
            final String uuid = UUID.randomUUID().toString();
//            String jsonParam = CommonHelperUtils.objectToJsonString(currentGroup);
            String jsonParam = "";

            // send command to create group
            final MemoMessage tempMemo = new MemoMessage(currentGroup.update_time,
                    MemoMessage.MSG_TYPE_CMD, MemoMessage.CMD_CREATE_GROUP, null, null,
                    groupUid, null, jsonParam, uuid);

            String strJson = tempMemo.toString();

            // 保存聊天消息
            final UserChatItem chatItem = new UserChatItem(null, null, tempMemo.getTime(), tempMemo.getTime(),
                    tempMemo.getTime(), OneAccountHelper.getAccountId(), groupUid,
                    "", "", strJson,
                    CommonConstants.DEFAULT_DAO_CODE, "", uuid, 0,
                    ItemMessage.Status.CREATE.ordinal(), 0, 0, groupUid);

            String encryptStrJson = Util.bytesToHex(Util.encryptAES(strJson.getBytes(Charsets.UTF_8), currentGroup.encrypt_key.getBytes(Charsets.UTF_8)));
            String msgType = CommonUtils.getMsgTypeByMemoString(strJson);

            String messageContent = "";
            if (currentGroup.public_status == CommonConstants.CHAT_GROUP_STATUS_PUBLIC) {
                messageContent = jsonParam;
            }
            // FIXME: 2017/11/17 hs
            //添加群聊请求
            OneGroupHelper.AddGroupMessageInfo(groupUid, encryptStrJson, messageContent, msgType, uuid, null);
            OneAccountHelper.getDatabase().putUserChat(chatItem);
            sendFirstMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        runOnUiThread(new Runnable() {
//            public void run() {

//            }

//        });
    }

    private void sendFirstMessage() {
        String content;
        if (isCreatingNewGroup) {
            content = UserInfoUtils.getUserNick() + getResources().getString(R.string.create_a_group);
        } else {
            // 发一条第一条消息
            String strFormat = UserInfoUtils.getUserNick() + getResources().getString(R.string.default_groupchat_sentence);
            content = String.format(strFormat, newmembers);
        }

        String uuid = UUID.randomUUID().toString();
//                                String groupUid = groupUid;

//                                ItemMessage message = CommonHelperUtils.buildTextMessage(BtsHelper.mMeAccountId, groupUid,
//                                        content, ItemMessage.Direct.SEND, ItemMessage.ChatType.GroupChat, uuid);

        MemoMessage tempMemo = new MemoMessage(BtsApplication.getAdjustTimeNowMillis(),
                MemoMessage.MSG_TYPE_TXT, MemoMessage.CMD_SEND_MSG, content, null,
                groupUid, null, null, uuid);

        String strJson = tempMemo.toString();

        // 保存聊天消息
        final UserChatItem chatItem = new UserChatItem(null, null, tempMemo.getTime(), tempMemo.getTime(),
                tempMemo.getTime(), OneAccountHelper.getAccountId(), groupUid,
                "", "", strJson,
                CommonConstants.DEFAULT_DAO_CODE, "", uuid, 0,
                ItemMessage.Status.CREATE.ordinal(), 0, 0, groupUid);

//                                OneAccountHelper.getDatabase().putUserChat(chatItem);

//                                MessageSenderHandler.getInstance().startMessageSender();


        String encryptStrJson = Util.bytesToHex(Util.encryptAES(strJson.getBytes(Charsets.UTF_8), currentGroup.encrypt_key.getBytes(Charsets.UTF_8)));
        String msgType = CommonUtils.getMsgTypeByMemoString(strJson);

        String messageContent = "";
        if (currentGroup.public_status == CommonConstants.CHAT_GROUP_STATUS_PUBLIC) {
            messageContent = content;
        }
        // FIXME: 2017/11/17 hs
        //添加群聊请求
        OneGroupHelper.AddGroupMessageInfo(groupUid, encryptStrJson, messageContent, msgType, uuid, new RequestSuccessListener<MapResult>() {
            @Override
            public void onResponse(MapResult mapResult) {
                if (isCreatingNewGroup) {
                    JumpAppPageUtil.jumpGroupChatPage(context, groupUid, groupname);
                }
            }
        });
        OneAccountHelper.getDatabase().putUserChat(chatItem);

        hideLoadingDialog();
        finish();
    }
}
