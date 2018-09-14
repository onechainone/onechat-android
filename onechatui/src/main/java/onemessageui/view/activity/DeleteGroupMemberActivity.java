package onemessageui.view.activity;

import android.content.Context;
import android.content.Intent;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import onemessageui.widght.SideBar;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.graphenechain.fragments.AccountDetailWebsocket;
import oneapp.onechat.oneandroid.graphenechain.models.UserContactItem;
import oneapp.onechat.oneandroid.graphenechain.models.UserGroupInfoItem;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsApplication;
import oneapp.onechat.oneandroid.onemessage.bean.GroupMemberBean;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.NetUtils;
import oneapp.onechat.oneandroid.onemessage.common.CommonHelperUtils;
import onemessageui.common.PingYinUtil;
import onemessageui.common.PinyinComparator;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import onemessageui.utils.ViewHolder;
import oneapp.onechat.oneandroid.onewallet.modle.Result;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.util.GsonUtils;
import onewalletui.util.ImageUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import onemessageui.dialog.DialogUtil;
import onemessageui.view.OneBaseActivity;
import onewalletui.util.jump.JumpParamsContants;

//import oneapp.onemessage.GloableParams;
//import oneapp.onemessage.bean.GroupInfo;
//import oneapp.onemessage.bean.User;

public class DeleteGroupMemberActivity extends OneBaseActivity implements
        OnClickListener, OnItemClickListener {
    private ImageView iv_search, img_back;
    private TextView tv_header, txt_title, txt_right;

    private ListView listView;
    private EditText et_search;
    private SideBar indexBar;
    private TextView mDialogText;
    private WindowManager mWindowManager;
    /**
     * 是否为单选
     */
    private boolean isSignleChecked;
    private ContactAdapter contactAdapter;

    private HashMap<String, String> oldMembers = new HashMap<>();


    // 可滑动的显示选中用户的View
    private LinearLayout menuLinerLayout;

    // 选中用户总数,右上角显示
    int total = 0;
    private String userId = null;
    private String groupUid = null;
    private String groupname;
    private List<UserContactItem> alluserList;// 好友列表

    // 添加的列表
    private List<UserContactItem> selectList = new ArrayList<>();
    private UserGroupInfoItem currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_group_member_list);
        readArguments();
    }

    private void readArguments() {
        final Intent intent = this.getIntent();
        final Serializable sear = intent
                .getSerializableExtra(JumpParamsContants.INTENT_PARAMS_MAP);
        if (sear != null) {
            final HashMap<String, Object> paramMap = (HashMap<String, Object>) sear;
            this.groupUid = (String) paramMap.get(JumpParamsContants.INTENT_GROUP_ID);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWindowManager.removeViewImmediate(mDialogText);
    }


    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(R.string.button_from_group_delete);
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

    }

    @Override
    protected void initView() {
        if (StringUtils.equalsNull(userId)) {
            userId = OneAccountHelper.getAccountId();
        }
        if (!StringUtils.equalsNull(groupUid)) {

            currentGroup = OneAccountHelper.getDatabase().getUserGroupInfoItemById(groupUid);
            if (currentGroup != null) {

                List<String> tempMembers = CommonHelperUtils.jsonToMemberIdList(currentGroup.members);
                for (String memberBean : tempMembers) {
                    oldMembers.put(memberBean, memberBean);
                }
                groupname = currentGroup.group_name;
            }
        } else {
            total = 1;
        }
    }

    @Override
    protected void initData() {
        // 获取好友列表
//		alluserList = new ArrayList<UserContactItem>();

        updateView();
        // FIXME: 2017/11/18 hs
        // 获取群用户列表
        List<String> missingAccountId = new ArrayList<>();
        for (UserContactItem memberBean : alluserList) {
            if (StringUtils.equalsNull(memberBean.memoKey)) {
                missingAccountId.add(memberBean.getId());
            }
        }
        AccountDetailWebsocket.getAccountDetail(missingAccountId);

//		for (User user : GloableParams.UserInfos) {
//			if (!user.getUserName().equals(Constant.NEW_FRIENDS_USERNAME)
//					& !user.getUserName().equals(Constant.GROUP_USERNAME))
//				alluserList.add(user);
//		}

        contactAdapter = new ContactAdapter(DeleteGroupMemberActivity.this,
                alluserList);

        listView.setAdapter(contactAdapter);
    }

    void updateView() {
        if (currentGroup == null) {
            return;
        }

        alluserList = new ArrayList<>();

        List<GroupMemberBean> tempMembers = CommonHelperUtils.jsonToChatMemberList(currentGroup.members);
        if (tempMembers != null && tempMembers.size() > 0) {
            int m_total = tempMembers.size();
            txt_title.setText(getString(R.string.group_members) + "(" + String.valueOf(m_total) + ")");
            // 解析群组成员信息
            for (int i = 0; i < m_total; i++) {
                UserContactItem user = OneAccountHelper.getDatabase().getUserContactItemById(tempMembers.get(i).getUid());
                if (user == null) {
                    //用户不存在
                    user = new UserContactItem();
                }
                alluserList.add(user);
            }

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
                    List<UserContactItem> users_temp = new ArrayList<UserContactItem>();
                    for (UserContactItem user : alluserList) {
                        String usernick = user.getUserName();
                        if (usernick.toLowerCase().contains(str_s.toLowerCase())) {
                            if (usernick.startsWith(str_s))
                                users_temp.add(0, user);
                            else users_temp.add(user);
                        }
                    }
                    contactAdapter = new ContactAdapter(
                            DeleteGroupMemberActivity.this, users_temp, str_s);
                    listView.setAdapter(contactAdapter);
                } else {
                    contactAdapter = new ContactAdapter(
                            DeleteGroupMemberActivity.this, alluserList);
                    listView.setAdapter(contactAdapter);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            Utils.finish(DeleteGroupMemberActivity.this);

        } else if (i == R.id.tv_header) {// TODO 打开群列表

        } else if (i == R.id.txt_right) {
            save();

        } else {
        }
    }

    /**
     * 确认选择的members
     *
     * @param
     */
    public void save() {

        if (selectList.size() == 0) {
            Utils.showLongToast(DeleteGroupMemberActivity.this, "请选择用户");
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
        DialogUtil.simpleDialog(context, getString(R.string.sure_delete_user), new DialogUtil.ConfirmCallBackInf() {
            @Override
            public void onConfirmClick(String content) {
                deleteMembersFromGroup(selectList);
            }
        });

    }

    private void deleteMembersFromGroup(List<UserContactItem> selectList) {

        List<GroupMemberBean> tempMembers = CommonHelperUtils.jsonToChatMemberList(currentGroup.members);

        for (GroupMemberBean memberBean : tempMembers) {
            if (memberBean.getUid().equals(userId)) {
                tempMembers.remove(memberBean);
                break;
            }
        }
        currentGroup.members = GsonUtils.objToJson(tempMembers);
        currentGroup.update_time = BtsApplication.getAdjustTimeNowMillis();
        OneAccountHelper.getDatabase().putGroupInfo(currentGroup);

        //更新群聊请求
        OneGroupHelper.updateGroupWithConfiguration(currentGroup, new RequestSuccessListener<Result>() {
            @Override
            public void onResponse(Result result) {
            }
        });
        updateView();
    }

    class ContactAdapter extends BaseAdapter implements SectionIndexer {
        private Context mContext;
        private boolean[] isCheckedArray;
        private Bitmap[] bitmaps;
        private List<UserContactItem> list = new ArrayList<UserContactItem>();
        private String searchStr = "";

        @SuppressWarnings("unchecked")
        public ContactAdapter(Context mContext, List<UserContactItem> users) {
            this.mContext = mContext;
            this.list = users;
            bitmaps = new Bitmap[list.size()];
            isCheckedArray = new boolean[list.size()];
            // 排序(实现了中英文混排)
            Collections.sort(list, new PinyinComparator());
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

            if (selectList != null && selectList.contains(user)) {
                checkBox.setChecked(true);
                isCheckedArray[position] = true;
            } else {
                checkBox.setChecked(false);
                isCheckedArray[position] = false;
            }

            checkBox.setChecked(isCheckedArray[position]);


            if (contactView != null) {
                contactView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean isChecked = !checkBox.isChecked();

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
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
//        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
//        checkBox.toggle();
    }


}
