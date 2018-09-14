package oneapp.onechat.chat.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.utils.jump.JumpAppPageUtil;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.graphenechain.models.UserContactItem;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.bean.ApplyNumResult;
import oneapp.onechat.oneandroid.onewallet.modle.UserInfoBean;
import oneapp.onechat.oneandroid.onewallet.util.ListUtils;
import onemessageui.adpter.ContactAdapter;
import onemessageui.view.fragment.MainImmersionFragment;
import onemessageui.widght.SideBar;

/**
 * Fragment that restores a wallet
 */
public class ContactFragment extends MainImmersionFragment implements View.OnClickListener {
    private ImageView img_back;
    private TextView txt_title;

    private ListView listView;
    private EditText et_search;
    private SideBar indexBar;
    private TextView mDialogText;
    private WindowManager mWindowManager;
    private ContactAdapter contactAdapter;

    private View layout;

    private List<UserContactItem> alluserList;// 好友列表

    private View newFriendsView, newGroupView, allGroupView;
    private TextView newFriendNumTv, newGroupNumTv;
    private View headerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        if (layout == null) {
        layout = getActivity().getLayoutInflater().inflate(R.layout.fragment_friends_list,
                null);

        try {
            IF_KEYBOARD_ENABLE = false;
            initImmersionBar();
            mImmersionBar.titleBar(layout.findViewById(R.id.layout_title_view));
            mImmersionBar.init();

        } catch (Exception e) {
            e.printStackTrace();
        }

        initControl();
        initData();
        setListener();

        return layout;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mWindowManager.removeViewImmediate(mDialogText);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
        getNewApplyNum();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initData();
            getNewApplyNum();
        }
    }

    private void initHeadView() {
        headerView = View.inflate(getActivity(), R.layout.head_friend_list, null);

        newFriendsView = headerView.findViewById(R.id.view_new_friends);
        newGroupView = headerView.findViewById(R.id.view_new_groups);
        allGroupView = headerView.findViewById(R.id.view_all_groups);

        newFriendNumTv = (TextView) headerView.findViewById(R.id.tv_new_friend_num);
        newGroupNumTv = (TextView) headerView.findViewById(R.id.tv_new_group_num);

        listView.addHeaderView(headerView);

    }

    private void refreshList() {
        try {
            alluserList = OneAccountHelper.getDatabase().getUserContacts(CommonConstants.MAX_LOAD_FRIEND_SIZE);

            if (contactAdapter == null) {
                contactAdapter = new ContactAdapter(getActivity(),
                        alluserList);
                listView.setAdapter(contactAdapter);
            } else {
                contactAdapter.refreshList(alluserList);
            }
            indexBar.setCharList(contactAdapter.getPinyinCharList());
        } catch (Exception e) {

        }
    }

    protected void initControl() {
        txt_title = (TextView) layout.findViewById(R.id.txt_title);
        txt_title.setText(R.string.contacts);
        img_back = (ImageView) layout.findViewById(R.id.img_back);
        img_back.setVisibility(View.GONE);
        listView = (ListView) layout.findViewById(R.id.list);
        et_search = (EditText) layout.findViewById(R.id.et_search);
        mDialogText = (TextView) LayoutInflater.from(getActivity()).inflate(
                R.layout.list_position, null);
        mDialogText.setVisibility(View.INVISIBLE);
        indexBar = (SideBar) layout.findViewById(R.id.sideBar);
        indexBar.setListView(listView);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mWindowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        if (mWindowManager != null) {
            mWindowManager.addView(mDialogText, lp);
        }
        indexBar.setTextView(mDialogText);

        initHeadView();
    }


    protected void initData() {

        OneAccountHelper.getFriendListWithCompletion(new RequestSuccessListener<List<UserInfoBean>>() {
            @Override
            public void onResponse(List<UserInfoBean> userInfoBeans) {
                refreshList();
            }
        });

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

    protected void setListener() {
        img_back.setOnClickListener(this);
        et_search.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0) {
                    String str_s = et_search.getText().toString().trim().toLowerCase();
                    List<UserContactItem> users_temp = ListUtils.searchUserList(alluserList, str_s);

                    contactAdapter = new ContactAdapter(
                            getContext(), users_temp, str_s);
                    listView.setAdapter(contactAdapter);

                    newFriendsView.setVisibility(View.GONE);
                    newGroupView.setVisibility(View.GONE);
                    allGroupView.setVisibility(View.GONE);
                } else {
                    contactAdapter = new ContactAdapter(
                            getContext(), alluserList);
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
                JumpAppPageUtil.jumpOtherUserInfoPage(getContext(), contactAdapter.getItem(position).getId());
            }
        });

        newFriendsView.setOnClickListener(this);
        newGroupView.setOnClickListener(this);
        allGroupView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_new_friends:
                JumpAppPageUtil.jumpFriendApplyPage(getContext());
                break;
            case R.id.view_new_groups:
                JumpAppPageUtil.jumpGroupApplyPage(getContext());
                break;
            case R.id.view_all_groups:
                JumpAppPageUtil.jumpGroupListPage(getContext());
                break;
            default:
                break;
        }
    }

//    //获取新申请数量
//    private void requestNewApplyNum() {
//        RequestUtils.GetApplyNumRequest(new RequestSuccessListener<ApplyNumResult>() {
//            @Override
//            public void onResponse(ApplyNumResult applyNumResult) {
//                if (applyNumResult != null) {
//
//                    long num = applyNumResult.getGroup_count() + applyNumResult.getUser_count();
//                    String unReadNumString = String.valueOf(num);
//                    if (num > CommonConstants.MIAX_SHOW_UNREAD_MSG_NUM) {
//                        unReadNumString = CommonConstants.MIAX_SHOW_UNREAD_MSG_NUM + getString(R.string.add_symbol);
//                    }
//                    if (num > 0) {
//                        mNewApplyTv.setVisibility(View.VISIBLE);
//                        mNewApplyTv.setText(unReadNumString);
//                    } else {
//                        mNewApplyTv.setVisibility(View.GONE);
//                    }
//                } else {
//                    mNewApplyTv.setVisibility(View.GONE);
//                }
//            }
//        });
//    }

}
