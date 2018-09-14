package onemessageui.view.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneChatHelper;
import oneapp.onechat.oneandroid.graphenechain.models.UserContactItem;
import oneapp.onechat.oneandroid.onemessage.bean.ApplyNumResult;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.onewallet.modle.UserInfoBean;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.util.ListUtils;
import onemessageui.adpter.ContactAdapter;
import onemessageui.view.OneBaseActivity;
import onemessageui.widght.SideBar;
import onewalletui.util.jump.JumpAppPageUtil;
import sdk.android.onechatui.R;


public class FriendsListActivity extends OneBaseActivity implements OnClickListener {
    private ImageView img_back;
    private TextView txt_title;

    private ListView listView;
    private EditText et_search;
    private SideBar indexBar;
    private TextView mDialogText;
    private WindowManager mWindowManager;
    private ContactAdapter contactAdapter;

    private List<UserContactItem> alluserList;// 好友列表

    private View newFriendsView, newGroupView, allGroupView;
    private TextView newFriendNumTv, newGroupNumTv;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_friends_list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWindowManager.removeViewImmediate(mDialogText);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
        getNewApplyNum();
    }

    private void initHeadView() {
        headerView = View.inflate(this, R.layout.head_friend_list, null);

        newFriendsView = headerView.findViewById(R.id.view_new_friends);
        newGroupView = headerView.findViewById(R.id.view_new_groups);
        allGroupView = headerView.findViewById(R.id.view_all_groups);

        newFriendNumTv = (TextView) headerView.findViewById(R.id.tv_new_friend_num);
        newGroupNumTv = (TextView) headerView.findViewById(R.id.tv_new_group_num);

        listView.addHeaderView(headerView);

    }

    private void refreshList() {
        try {
            alluserList = OneChatHelper.getContactsList();

            if (contactAdapter == null) {
                contactAdapter = new ContactAdapter(FriendsListActivity.this,
                        alluserList);
                listView.setAdapter(contactAdapter);
            } else {
                contactAdapter.refreshList(alluserList);
            }
            indexBar.setCharList(contactAdapter.getPinyinCharList());
        } catch (Exception e) {

        }
    }

    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(R.string.contacts);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        listView = (ListView) findViewById(R.id.list);
        et_search = (EditText) findViewById(R.id.et_search);
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

        initHeadView();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

        OneAccountHelper.getFriendListWithCompletion(new RequestSuccessListener<List<UserInfoBean>>() {
            @Override
            public void onResponse(List<UserInfoBean> userInfoBeans) {
                refreshList();
            }
        });

//        new RequstFriendListTask().execute();
    }

    void getNewApplyNum() {
        OneAccountHelper.GetApplyNumRequest(new RequestSuccessListener<ApplyNumResult>() {
            @Override
            public void onResponse(ApplyNumResult applyNumResult) {
                if (applyNumResult != null) {
                    if (applyNumResult.getUser_count() > 0) {
                        newFriendNumTv.setVisibility(View.VISIBLE);
                        newFriendNumTv.setText(String.valueOf(applyNumResult.getUser_count()));
                    } else {
                        newFriendNumTv.setVisibility(View.GONE);
                    }
                    if (applyNumResult.getGroup_count() > 0) {
                        newGroupNumTv.setVisibility(View.VISIBLE);
                        newGroupNumTv.setText(String.valueOf(applyNumResult.getGroup_count()));
                    } else {
                        newGroupNumTv.setVisibility(View.GONE);
                    }
                } else {
                    newFriendNumTv.setVisibility(View.GONE);
                    newGroupNumTv.setVisibility(View.GONE);
                }
            }
        });
    }


    public class RequstFriendListTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            //这里是开始线程之前执行的,是在UI线程
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            OneAccountHelper.getFriendListWithCompletion(new RequestSuccessListener<List<UserInfoBean>>() {
                @Override
                public void onResponse(List<UserInfoBean> userInfoBeans) {
                    try {
                        refreshList();
                    } catch (Exception e) {

                    }
                }
            });
            return true;
        }

        @Override
        protected void onCancelled() {
            //当任务被取消时回调
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Boolean bitmap) {
            super.onPostExecute(bitmap);
        }
    }

    @Override
    protected void setListener() {
        img_back.setOnClickListener(this);
        et_search.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0) {
                    String str_s = et_search.getText().toString().trim().toLowerCase();
                    List<UserContactItem> users_temp = ListUtils.searchUserList(alluserList, str_s);

                    contactAdapter = new ContactAdapter(
                            FriendsListActivity.this, users_temp, str_s);
                    listView.setAdapter(contactAdapter);

                    newFriendsView.setVisibility(View.GONE);
                    newGroupView.setVisibility(View.GONE);
                    allGroupView.setVisibility(View.GONE);
                } else {
                    contactAdapter = new ContactAdapter(
                            FriendsListActivity.this, alluserList);
                    listView.setAdapter(contactAdapter);

                    newFriendsView.setVisibility(View.VISIBLE);
                    newGroupView.setVisibility(View.VISIBLE);
                    allGroupView.setVisibility(View.VISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = i - listView.getHeaderViewsCount();
                if (position >= contactAdapter.getCount()) {
                    return;
                }
                JumpAppPageUtil.jumpOtherUserInfoPage(context, contactAdapter.getItem(position).getId());
            }
        });

        newFriendsView.setOnClickListener(this);
        newGroupView.setOnClickListener(this);
        allGroupView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            Utils.finish(FriendsListActivity.this);

        } else if (i == R.id.view_new_friends) {
            JumpAppPageUtil.jumpFriendApplyPage(this);

        } else if (i == R.id.view_new_groups) {
            JumpAppPageUtil.jumpGroupApplyPage(this);

        } else if (i == R.id.view_all_groups) {
            JumpAppPageUtil.jumpGroupListPage(this);

        } else {
        }
    }


}
