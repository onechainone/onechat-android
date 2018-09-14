package onemessageui.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import oneapp.onechat.oneandroid.chatsdk.MessageSender;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneChatHelper;
import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import oneapp.onechat.oneandroid.chatsdk.OneOpenHelper;
import oneapp.onechat.oneandroid.graphenechain.database.HistoricalTransferEntry;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.graphenechain.interfaces.ViewDelegate;
import oneapp.onechat.oneandroid.graphenechain.models.UserContactItem;
import oneapp.onechat.oneandroid.graphenechain.models.UserGroupInfoItem;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsApplication;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.Constants;
import oneapp.onechat.oneandroid.onemessage.RpcCallProxy;
import oneapp.onechat.oneandroid.onemessage.bean.ItemConversationListBean;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemConversation;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemGroupReomveListener;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage.ChatType;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemNormalItemFileItemMessageBody;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemTextItemMessageBody;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemVideoItemMessageBodyItem;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.PathUtil;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.UtilLog;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.VoiceRecorder;
import oneapp.onechat.oneandroid.onemessage.common.CommonHelperUtils;
import oneapp.onechat.oneandroid.onewallet.modle.UserInfoBean;
import oneapp.onechat.oneandroid.onewallet.util.GsonUtils;
import oneapp.onechat.oneandroid.onewallet.util.Keyboard;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.TimeUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onechat.oneandroid.onewallet.util.UiUtils;
import onemessageui.chat.adpter.MessageAdapter;
import onemessageui.chat.voice.MediaRecorderUtils;
import onemessageui.chat.voice.MyChronometer;
import onemessageui.chat.voice.VoicePlayClickListener;
import onemessageui.dialog.DialogUtil;
import onemessageui.utils.CommonUtils;
import onemessageui.view.activity.TencentMapActivity;
import onemessageui.widght.AtMsgEditText;
import onemessageui.widght.BaseSoftInputLayout;
import onewalletui.ui.BaseActivity;
import onewalletui.ui.BaseFragment;
import onewalletui.util.jump.JumpAppOutUtil;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;
import sdk.android.onechatui.R;

import static android.app.Activity.RESULT_OK;

//聊天页面
public class ChatFragment extends BaseFragment implements OnClickListener, ViewDelegate, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private final int REQUEST_CODE_EMPTY_HISTORY = 2;
    private final int REQUEST_CODE_MAP = 4;
    private final int REQUEST_CODE_TEXT = 5;
    private final int REQUEST_CODE_VOICE = 6;
    private final int REQUEST_CODE_PICTURE = 7;
    private final int REQUEST_CODE_LOCATION = 8;
    private final int REQUEST_CODE_NET_DISK = 9;
    private final int REQUEST_CODE_FILE = 10;
    private final int REQUEST_CODE_PICK_VIDEO = 12;
    private final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
    private final int REQUEST_CODE_VIDEO = 14;
    private final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
    private final int REQUEST_CODE_SELECT_USER_CARD = 16;
    private final int REQUEST_CODE_SEND_USER_CARD = 17;
    private final int REQUEST_CODE_CAMERA = 18;
    private final int REQUEST_CODE_LOCAL = 19;
    private final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
    private final int REQUEST_CODE_GROUP_DETAIL = 21;
    private final int REQUEST_CODE_SELECT_VIDEO = 23;
    private final int REQUEST_CODE_SELECT_FILE = 24;
    private final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;
//    public static final int REQUEST_CODE_HEADIMG_MENU = 26;

    private final int BOTTOM_SCROLL_NUM = 3;
    private final int SHOW_TOP_SCROLL_NEW_MSG_NUM = 8;

    private BaseActivity mActivity;
    public static final String COPY_IMAGE = "ONECHATIMG";
    private View recordingContainer;
    private ImageView micImage;
    private TextView recordingHint;
    private ListView listView;
    // FIXME: 2017/12/11 hs
//    private PasteEditText mEditTextContent;
    private View mEditView;
    private AtMsgEditText mEditTextContent;
    private View buttonSetModeKeyboard;
    private View buttonSetModeVoice;
    private ImageView buttonSend;
    private View buttonPressToSpeak;
    private TextView tv_press_to_speak;
    // private ViewPager expressionViewpager;
    private LinearLayout emojiIconContainer;
    private LinearLayout btnContainer;
    // private ImageView locationImgview;
    private View more;
    private int position;
    private ClipboardManager clipboard;
    //    private InputMethodManager manager;
    private Drawable[] micImages;
    private int chatType;
    private ItemConversation conversation;
    private NewMessageBroadcastReceiver receiver;
    private NewPushBroadcastReceiver pushReceiver;
    private MessageStatusBroadcastReceiver receiverMessageStatus;

    private Bundle bundle;
    // 给谁发送消息
    private String Name;
    private String toChatUsername;
    private VoiceRecorder voiceRecorder;
    private MessageAdapter adapter;
    private String cameraSaveFile;
    boolean isFirstInPage = true;
    boolean ifInitLittle = true;

    private BaseSoftInputLayout softInputLayout;

    private ItemGroupListener groupListener;
    private ImageView iv_emoticons_normal;
    private ImageView iv_emoticons_checked;
    //    private RelativeLayout edittext_layout;
    private ProgressBar loadmorePB;
    private boolean isloading;
    private final int pagesize = 20;
    private boolean haveMoreData = true;
    private ImageView btnMore;
    public String playMsgId;
    private AnimationDrawable animationDrawable;
    //	private NetClient netClient;

    MessageSender messageSender;

    UserGroupInfoItem mGroupInfo = null;

    private TextView mBottomNewMsgTv, mTopNewMsgTv;
    private boolean isAdmin = false;

    private View inputView, addFriendView;

    //	public SendWebsocket mSendWebsocket = new SendWebsocket();

    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
        }
    };

    private View layout;
    private LinearLayout view_camera;
    //    private DrawerLayout msgDrawerLayout;
    private LinearLayout mLlGroupAlpha;
    private View mShowPushView, mLeftView;
    private TextView mPushNumTv, mClearPushTv;
    private ListView mPushLv;

    private ImageView mIvGroupCommunity;
    private LinearLayout mLlGroupCommunity;
    private LinearLayout mLlOwner;
    private LinearLayout mLlAllBannedtopost;
    private MessageAdapter.MyDialogListener myDialogListener = new MessageAdapter.MyDialogListener() {
        @Override
        public void refreshActivity(final String fromId) {

            if (chatType == Constants.CHATTYPE_GROUP) {
                if (StringUtils.equals(mGroupInfo.owner, OneAccountHelper.getAccountId())) {
                    DialogUtil.chooceGroupUserAvatarDialog(mActivity, fromId, toChatUsername, true, new DialogUtil.ConfirmCallBackInf() {
                        @Override
                        public void onConfirmClick(String type) {
                            if (type.equals(DialogUtil.CALL_BACK_TYPE_ADD_ADMIN)) {
                                OneGroupHelper.getGroupAdminList(mGroupInfo.group_uid, new RequestSuccessListener<List<String>>() {
                                    @Override
                                    public void onResponse(List<String> stringList) {
                                        if (stringList != null && stringList.size() >= 0) {
                                            mGroupInfo.setAdmins(GsonUtils.objToJson(stringList));
                                        }
                                        if (adapter != null)
                                            adapter.refreshGroupInfo();
                                    }
                                });
                            }
                            if (type.equals(DialogUtil.CALL_BACK_TYPE_AT_USER)) {
                                addAtOtherUser(fromId);
                            }
                        }
                    });
                } else if (isAdmin) {
                    DialogUtil.chooceGroupUserAvatarDialog(mActivity, fromId, toChatUsername, true, new DialogUtil.ConfirmCallBackInf() {
                        @Override
                        public void onConfirmClick(String content) {
                            addAtOtherUser(fromId);
                        }
                    });
                } else {
                    DialogUtil.chooceGroupUserAvatarDialog(mActivity, fromId, toChatUsername, false, new DialogUtil.ConfirmCallBackInf() {
                        @Override
                        public void onConfirmClick(String content) {
                            addAtOtherUser(fromId);
                        }
                    });
                }
            } else {
                JumpAppPageUtil.jumpOtherUserInfoPage(mActivity, fromId);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mActivity = (BaseActivity) getActivity();
        super.onCreate(savedInstanceState);
    }

    public static ChatFragment newInstance(int type, String mUserId, String mGroupId, String name) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        if (!StringUtils.equalsNull(mGroupId)) {
            args.putString(Constants.GROUP_ID, mGroupId);
        }
        args.putString(Constants.NAME, name);
        args.putInt(Constants.TYPE, type);
        if (!StringUtils.equalsNull(mUserId)) {
            args.putString(Constants.User_ID, mUserId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        layout = mActivity.getLayoutInflater().inflate(R.layout.fragment_chat,
                null);

        initView();
        initData();
        setListener();
        return layout;
    }


    private void addAtOtherUser(String fromId) {
        if (!StringUtils.equalsNull(fromId)) {
            UserContactItem userContactItem = OneAccountHelper.getDatabase().getUserContactItemById(fromId);
            String nickname = userContactItem.getNickname();
            mEditTextContent.addAtSpan(CommonConstants.MASK_STR, nickname, fromId);
        } else {
            mEditTextContent.addAtSpan(CommonConstants.MASK_STR, getString(R.string.chat_owner), CommonConstants.AT_ALL_ID);
        }
    }

    /**
     * initView
     */
    protected void initView() {

//        msgDrawerLayout = (DrawerLayout) layout.findViewById(R.id.msg_drawer);
        mLlGroupAlpha = (LinearLayout) layout.findViewById(R.id.ll_group_alpha);
        mShowPushView = layout.findViewById(R.id.view_push_msg);
        mLeftView = layout.findViewById(R.id.view_left_msg);

        mPushNumTv = (TextView) layout.findViewById(R.id.notification_number);
        mClearPushTv = (TextView) layout.findViewById(R.id.clean_group_notification);
        mPushLv = (ListView) layout.findViewById(R.id.notification_lv);

        mIvGroupCommunity = (ImageView) layout.findViewById(R.id.iv_group_community);
        mLlGroupCommunity = (LinearLayout) layout.findViewById(R.id.ll_group_community);
        mLlAllBannedtopost = (LinearLayout) layout.findViewById(R.id.view_all_banned_topost);
        mLlOwner = (LinearLayout) layout.findViewById(R.id.view_owner);
        TextView mTvOwner = (TextView) layout.findViewById(R.id.tv_owner);
        mTvOwner.setText("@" + getResources().getString(R.string.chat_owner));

        recordingContainer = layout.findViewById(R.id.view_talk);
        micImage = (ImageView) layout.findViewById(R.id.mic_image);
        animationDrawable = (AnimationDrawable) micImage.getBackground();
        animationDrawable.setOneShot(false);
        recordingHint = (TextView) layout.findViewById(R.id.recording_hint);
        listView = (ListView) layout.findViewById(R.id.list);
        // FIXME: 2017/12/11 hs
//        mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
        mEditTextContent = (AtMsgEditText) layout.findViewById(R.id.et_sendmessage);
        mEditTextContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(CommonConstants.MAX_SEND_MSG_SIZE)});
        mEditView = layout.findViewById(R.id.edittext_layout);
        buttonSetModeKeyboard = layout.findViewById(R.id.btn_set_mode_keyboard);
        buttonSetModeVoice = layout.findViewById(R.id.btn_set_mode_voice);
        buttonSend = (ImageView) layout.findViewById(R.id.btn_send);
        buttonPressToSpeak = layout.findViewById(R.id.btn_press_to_speak);
        tv_press_to_speak = (TextView) layout.findViewById(R.id.tv_press_to_speak);
        emojiIconContainer = (LinearLayout) layout.findViewById(R.id.ll_face_container);
        btnContainer = (LinearLayout) layout.findViewById(R.id.ll_btn_container);
        // locationImgview = (ImageView) findViewById(R.id.btn_location);
        iv_emoticons_normal = (ImageView) layout.findViewById(R.id.iv_emoticons_normal);
        iv_emoticons_checked = (ImageView) layout.findViewById(R.id.iv_emoticons_checked);
        loadmorePB = (ProgressBar) layout.findViewById(R.id.pb_load_more);
        btnMore = (ImageView) layout.findViewById(R.id.btn_more);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.GONE);
        more = layout.findViewById(R.id.more);
//        edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);

        mBottomNewMsgTv = (TextView) layout.findViewById(R.id.new_msg_bottom);
        mTopNewMsgTv = (TextView) layout.findViewById(R.id.new_msg_top);

        inputView = layout.findViewById(R.id.bar_container);
        addFriendView = layout.findViewById(R.id.tv_add_friend);


        voiceRecorder = new VoiceRecorder(micImageHandler);
//        buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());

        mEditTextContent.requestFocus();
        mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    edittext_layout
//                            .setBackgroundResource(R.drawable.input_bar_bg_active);
//                } else {
//                    edittext_layout
//                            .setBackgroundResource(R.drawable.input_bar_bg_normal);
//                }

            }
        });
//        mEditTextContent.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                more.setVisibility(View.GONE);
//                iv_emoticons_normal.setVisibility(View.VISIBLE);
//                iv_emoticons_checked.setVisibility(View.INVISIBLE);
//                emojiIconContainer.setVisibility(View.GONE);
//                btnContainer.setVisibility(View.GONE);
//            }
//        });
        // 监听文字框
        mEditTextContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!TextUtils.isEmpty(s)) {
                    btnMore.setVisibility(View.GONE);
                    buttonSend.setImageResource(R.drawable.chat_send_btn);
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    btnMore.setVisibility(View.VISIBLE);
                    buttonSend.setImageResource(R.drawable.chat_send_btn_gray);
                    buttonSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //表情
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(false))
                .commit();

        softInputLayout = (BaseSoftInputLayout) layout.findViewById(R.id.softinputLayout);

        View frame = layout.findViewById(R.id.frame);
        softInputLayout.init(mActivity, more, frame, mEditTextContent, btnMore, iv_emoticons_checked, btnContainer, iv_emoticons_normal, emojiIconContainer);

        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                hideKeyboard();
//                more.setVisibility(View.GONE);
//                iv_emoticons_normal.setVisibility(View.VISIBLE);
//                iv_emoticons_checked.setVisibility(View.INVISIBLE);
//                emojiIconContainer.setVisibility(View.GONE);
//                btnContainer.setVisibility(View.GONE);
                softInputLayout.hideAllView();
                return false;
            }
        });

        listView.setOnScrollListener(new ListScrollListener());

        buttonSetModeVoice.setVisibility(View.VISIBLE);
        buttonSetModeVoice.setOnClickListener(this);

        buttonPressToSpeak.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, final MotionEvent event) {

                //避免短时间里频繁操作
                if (!getTimeTF(System.currentTimeMillis()) && event.getAction() == MotionEvent.ACTION_DOWN) {
                    ToastUtils.simpleToast("操作过于频繁");
                    return true;
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setTime(System.currentTimeMillis());

                    VoicePlayClickListener.stopCurrentPlay();
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        buttonPressToSpeak.setBackgroundResource(R.drawable.chat_speak_btn_press);
                        tv_press_to_speak.setText(R.string.release_to_close);
                        int_x = event.getRawX();
                        int_y = event.getRawY();
                        VoicePopupWindow();
                        mychronometer.setBase(SystemClock.elapsedRealtime());
                        mychronometer.start();
                        MediaRecorderUtils.getInstence(voice_shengyin).MediaRecorderStart();
                        flag = true;
                        mychronometer.setOnMyChronometerTickListener(new MyChronometer.OnMyChronometerTickListener() {
                            @Override
                            public void onMyChronometerTick(int time) {
                                if (time >= CommonConstants.VOICE_MAX_RECORD_TIME) {
                                    mychronometer.setText("60");
                                    setVoiceToUp();
                                }
                            }
                        });
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (voice_popupWindow != null && voice_popupWindow.isShowing()) {
                            flag = true;

                            if (Math.abs(int_y) - Math.abs(event.getRawY()) > 100.0) {
                                cancelRecordView.setVisibility(View.VISIBLE);
                                startRecordView.setVisibility(View.GONE);
                                flag = false;
                            } else {
                                cancelRecordView.setVisibility(View.GONE);
                                startRecordView.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if (flag) {
                            if (voice_popupWindow != null) {
                                voice_popupWindow.dismiss();
                                mychronometer.stop();
                            }
                            MediaRecorderUtils.getInstence(voice_shengyin).MediaRecorderStop();
                        }

                        tv_press_to_speak.setText(R.string.button_pushtotalk);
                        buttonPressToSpeak.setBackgroundResource(R.drawable.chat_speak_btn_normal);

                        break;
                    case MotionEvent.ACTION_UP:
                        if (voice_popupWindow != null && voice_popupWindow.isShowing()) {
                            if (flag) {
                                setVoiceToUp();
                            } else {
                                if (voice_popupWindow != null) {
                                    voice_popupWindow.dismiss();
                                    mychronometer.stop();
                                }
                                MediaRecorderUtils.getInstence(voice_shengyin).MediaRecorderStop();
                                MediaRecorderUtils.getInstence(voice_shengyin).MediaRecorderDelete();
                            }
                        }
                        tv_press_to_speak.setText(R.string.button_pushtotalk);
                        buttonPressToSpeak.setBackgroundResource(R.drawable.chat_speak_btn_normal);

                        break;
                }
                return true;
            }
        });

    }

    private void initData() {
        bundle = getArguments();
        Name = bundle.getString(Constants.NAME);
        // 判断单聊还是小密圈
        chatType = bundle.getInt(Constants.TYPE, Constants.CHATTYPE_SINGLE);
        if (chatType == Constants.CHATTYPE_SINGLE) { // 单聊
            toChatUsername = bundle.getString(Constants.User_ID);
            messageSender = MessageSender.getSingleMessageSender(getActivity(), toChatUsername);
        } else {
            toChatUsername = bundle.getString(Constants.GROUP_ID);
            messageSender = MessageSender.getGroupMessageSender(getActivity(), toChatUsername);
        }

//        iv_emoticons_normal.setOnClickListener(this);
//        iv_emoticons_checked.setOnClickListener(this);
        // position = getIntent().getIntExtra("position", -1);
        clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
//        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        wakeLock = ((PowerManager) mActivity.getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
        mLlOwner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addAtOtherUser(null);
                Keyboard.changeKeyboard(mActivity);
            }
        });

        new LoadMsgListTask().execute();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (chatType == Constants.CHATTYPE_SINGLE) {
                    OneAccountHelper.getDatabase().deleteSingleChatOutOfNum(toChatUsername);
                } else {
                    OneAccountHelper.getDatabase().deleteGroupChatOutOfNum(toChatUsername);
                }
            }
        }).start();
//        new CleanMsgTask().execute();
    }

    private void setUpView() {

        if (chatType == Constants.CHATTYPE_SINGLE) { // 单聊
            mShowPushView.setVisibility(View.GONE);
            OneChatHelper.startMessageSender();
            mIvGroupCommunity.setVisibility(View.GONE);
            layout.findViewById(R.id.view_asset).setVisibility(View.GONE);
            layout.findViewById(R.id.view_red_packet).setVisibility(View.GONE);

            toChatUsername = bundle.getString(Constants.User_ID);
            OneChatHelper.RequestUserInfoById(toChatUsername, new RequestSuccessListener<UserInfoBean>() {
                @Override
                public void onResponse(UserInfoBean userInfoBean) {
                    UserContactItem userContactItem = OneAccountHelper.getDatabase().getUserContactItemById(toChatUsername);
                    if (userContactItem != null) {
                        Name = userContactItem.getUserName();
                        mEditTextContent.setHint(String.format(getString(R.string.hint_chat_edittext), Name));
                        if (userContactItem.statusFriend == UserContactItem.StatusFriend.FRIEND.ordinal()) {
                            inputView.setVisibility(View.VISIBLE);
                            addFriendView.setVisibility(View.GONE);
                        } else {
                            inputView.setVisibility(View.GONE);
                            addFriendView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
            UserContactItem userContactItem = OneAccountHelper.getDatabase().getUserContactItemById(toChatUsername);
            if (userContactItem != null) {
                Name = userContactItem.getUserName();
                if (userContactItem.statusFriend == UserContactItem.StatusFriend.FRIEND.ordinal()) {
                    inputView.setVisibility(View.VISIBLE);
                    addFriendView.setVisibility(View.GONE);
                } else {
                    inputView.setVisibility(View.GONE);
                    addFriendView.setVisibility(View.VISIBLE);
                }
            }
            addFriendView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    JumpAppPageUtil.jumpOtherUserInfoPage(getContext(), toChatUsername);
                }
            });

            if (TextUtils.isEmpty(Name)) {
                initUserInfo();
            } else {
                mEditTextContent.setHint(String.format(getString(R.string.hint_chat_edittext), Name));
            }
        } else {
            // 小密圈
            if (TextUtils.isEmpty(Name)) {
                initGroupInfo();
            }

//            mShowPushView.setVisibility(View.VISIBLE);

            layout.findViewById(R.id.view_menu_asset).setVisibility(View.GONE);
            layout.findViewById(R.id.view_asset).setVisibility(View.GONE);
            layout.findViewById(R.id.view_red_packet).setVisibility(View.VISIBLE);

            toChatUsername = bundle.getString(Constants.GROUP_ID);

            mGroupInfo = OneAccountHelper.getDatabase().getUserGroupInfoItemById(toChatUsername, false);

            if (mGroupInfo != null) {
                if (mGroupInfo.public_status == CommonConstants.CHAT_GROUP_STATUS_PUBLIC) {
                    mIvGroupCommunity.setVisibility(View.GONE);
                } else {
                    mIvGroupCommunity.setVisibility(View.GONE);
                }
                if (StringUtils.equals(mGroupInfo.owner, OneAccountHelper.getAccountId()) || isAdmin) {
//                    mLlAllBannedtopost.setVisibility(View.VISIBLE);
                    mLlOwner.setVisibility(View.VISIBLE);
                }

                if (StringUtils.equalsNull(mGroupInfo.encrypt_key)) {

                    //获取群密码
                    //如果是公开群,直接获取群聊天密码
                    if (mGroupInfo.public_status == CommonConstants.CHAT_GROUP_STATUS_PUBLIC) {
                        mGroupInfo.encrypt_key = mGroupInfo.group_encrypt_key;
                        mGroupInfo.update_time = BtsApplication.getAdjustTimeNowMillis();
                        OneAccountHelper.getDatabase().putGroupInfo(mGroupInfo);
                    } else {
                        String myInviterUid = mGroupInfo.account_id_invite;

                        UserContactItem userFromItem = OneAccountHelper.getDatabase().getUserContactItemById(myInviterUid);
                        if (userFromItem == null || StringUtils.equalsNull(userFromItem.getMemoKey())) {

                            OneChatHelper.RequestUserInfoById(myInviterUid, new RequestSuccessListener<UserInfoBean>() {
                                @Override
                                public void onResponse(UserInfoBean userInfoBean) {
                                    try {
                                        mGroupInfo.encrypt_key = OneOpenHelper.decryptMessage(userInfoBean.getMemo_key(), mGroupInfo.group_encrypt_key);
                                        mGroupInfo.update_time = BtsApplication.getAdjustTimeNowMillis();
                                        OneAccountHelper.getDatabase().putGroupInfo(mGroupInfo);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            try {
                                mGroupInfo.encrypt_key = OneOpenHelper.decryptMessage(userFromItem.getMemoKey(), mGroupInfo.group_encrypt_key);
                                mGroupInfo.update_time = BtsApplication.getAdjustTimeNowMillis();
                                OneAccountHelper.getDatabase().putGroupInfo(mGroupInfo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }

                OneGroupHelper.GetItemGroupInfoRequest(mGroupInfo.group_uid, new RequestSuccessListener<UserGroupInfoItem>() {
                    @Override
                    public void onResponse(UserGroupInfoItem userGroupInfoItem) {
                        if (userGroupInfoItem != null) {
                            mGroupInfo = OneAccountHelper.getDatabase().getUserGroupInfoItemById(toChatUsername, false);

                        } else {
                            ToastUtils.simpleToast(R.string.you_are_group);
                            mActivity.finish();
                        }
                    }
                });
                OneGroupHelper.getGroupAdminList(mGroupInfo.group_uid, new RequestSuccessListener<List<String>>() {
                    @Override
                    public void onResponse(List<String> strings) {
                        if (strings != null && strings.size() >= 0) {
                            mGroupInfo.setAdmins(GsonUtils.objToJson(strings));
                        }
                        if (adapter != null)
                            adapter.refreshGroupInfo();
                        if (mGroupInfo.getGroupAdminMap() != null && mGroupInfo.getGroupAdminMap().containsKey(OneAccountHelper.getAccountId())) {
                            isAdmin = true;
                        } else {
                            isAdmin = false;
                        }
                    }
                });

            }

            mEditTextContent.setHint(getString(R.string.hint_groupchat_edittext));

        }

        if (StringUtils.equalsNull(toChatUsername))

        {
            ToastUtils.simpleToast(R.string.null_this_friend);
            mActivity.finish();
        }

//        if (adapter == null) {
//            if (chatType == Constants.CHATTYPE_SINGLE) {
//                conversation = DbManager.getConversationsById(OneAccountHelper.getAccountId(),
//                        toChatUsername, CommonConstants.DEFAULT_PAGE_SIZE / 2);
//            } else {
//                conversation = DbManager.getConversationsByGroupUid(OneAccountHelper.getAccountId(),
//                        toChatUsername, CommonConstants.DEFAULT_PAGE_SIZE / 2);
//            }
//            refreshMsgList();
//        }
//        conversation = MsgCatchUtils.getConversationByUsername(toChatUsername);
//        if (conversation != null) {
//            refreshMsgList();
//        }

        if (!isFirstInPage) {
            new LoadMsgListTask().execute();
        }

        // 监听当前会话的小密圈解散被T事件
//		groupListener = new ItemGroupListener();
//		getInstance().addGroupChangeListener(groupListener);

        // show forward message if the message is not null
        String forward_msg_id = bundle.getString("forward_msg_id");
        if (forward_msg_id != null)

        {
            // 显示发送要转发的消息
            forwardMessage(forward_msg_id);
        }

    }

    public class LoadMsgListTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            //这里是开始线程之前执行的,是在UI线程
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (StringUtils.equalsNull(toChatUsername)) {
                return false;
            }
            conversation = OneChatHelper.getChatConversation(chatType, toChatUsername, ifInitLittle ? CommonConstants.DEFAULT_PAGE_SIZE / 2 : CommonConstants.MAX_LOAD_MESSAGE_SIZE);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (!b || getActivity() == null) {
                return;
            }
            try {
                refreshMsgList();
                isFirstInPage = ifInitLittle;
                if (ifInitLittle) {
                    ifInitLittle = false;
                    new LoadMsgListTask().execute();
                }

            } catch (Exception e) {

            }
        }
    }

    private class CleanMsgTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... params) {

            RpcCallProxy.getInstance().deleteOutOfMsg(chatType != Constants.CHATTYPE_SINGLE, toChatUsername);
//            if (chatType == Constants.CHATTYPE_SINGLE) {
//                OneAccountHelper.getDatabase().deleteSingleChatOutOfNum(toChatUsername);
//            } else {
//                OneAccountHelper.getDatabase().deleteGroupChatOutOfNum(toChatUsername);
//            }
            return null;
        }
    }


    private int mLastUnReadPosition;

    private void refreshMsgList() {

//        if (chatType == Constants.CHATTYPE_SINGLE) {
//            conversation = DbManager.getConversationsById(OneAccountHelper.getAccountId(),
//                    toChatUsername, CommonConstants.MAX_LOAD_MESSAGE_SIZE);
//        } else {
//            conversation = DbManager.getConversationsByGroupUid(OneAccountHelper.getAccountId(),
//                    toChatUsername, CommonConstants.DEFAULT_PAGE_SIZE);
//        }

        if (adapter == null) {
            adapter = new MessageAdapter(mActivity, toChatUsername, chatType, conversation);
            listView.setAdapter(adapter);
        } else {
            adapter.replace(conversation);
        }
        if (listView.getAdapter() == null) {
            listView.setAdapter(adapter);
        }

        if (myDialogListener != null) {
            adapter.setMyDialogListener(myDialogListener);
        }
        int count = listView.getCount();
        final int unReadNum;
        ItemConversationListBean itemConversationListBean = OneAccountHelper.getDatabase().getUserConversationItem(toChatUsername);
        if (itemConversationListBean != null) {
            unReadNum = itemConversationListBean.getUnreadMsgCount();
        } else {
            unReadNum = 0;
        }

        if (isFirstInPage) {
            if (unReadNum > SHOW_TOP_SCROLL_NEW_MSG_NUM) {
                mTopNewMsgTv.setVisibility(View.VISIBLE);
                mTopNewMsgTv.setText(String.format(getString(R.string.new_chat_message_num), unReadNum));
                mTopNewMsgTv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int scrollTo = listView.getCount() - unReadNum;
                        if (scrollTo < 0) {
                            scrollTo = 0;
                        }
                        UiUtils.scrollToPosition(listView, scrollTo);
                        mTopNewMsgTv.setVisibility(View.GONE);
                        mLastUnReadPosition = listView.getCount();
                    }
                });
                mLastUnReadPosition = listView.getCount() - unReadNum;
            }

            if (conversation != null)
                conversation.resetMsgSendStatusFail();
        }


        // 把此会话的未读数置为0
//        if (conversation != null)
//            conversation.resetUnreadMsgCount();
//        OneAccountHelper.getDatabase().clearUserConversationUnreadNum(toChatUsername);

        // 显示消息


        boolean ifScrollBottom = false;
        if (count >= 0 && isFirstInPage) {
            ifScrollBottom = true;
        }
        if (listView.getLastVisiblePosition() > count - BOTTOM_SCROLL_NUM) {
            ifScrollBottom = true;

        } else if (!isFirstInPage) {
            if (unReadNum > 0) {
                mBottomNewMsgTv.setVisibility(View.VISIBLE);
                mBottomNewMsgTv.setText(String.format(getString(R.string.new_chat_message_num), unReadNum));
                mBottomNewMsgTv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UiUtils.scrollToPosition(listView, listView.getCount() - unReadNum);
                        mBottomNewMsgTv.setVisibility(View.GONE);
                        OneAccountHelper.getDatabase().clearUserConversationUnreadNum(toChatUsername);
                    }
                });
            }

        }
        if (ifScrollBottom) {
            int duration = CommonConstants.CHAT_MOVE_BOTTOM_TIME;
            if (isFirstInPage) {
                duration = 0;
            }
            UiUtils.scrollToPosition(listView, count, duration);
            if (!ifInitLittle) {
                OneAccountHelper.getDatabase().clearUserConversationUnreadNum(toChatUsername);
            }
        }

    }

    /////////////////发送语音相关////////////////////////////
    private Boolean flag = true;
    private float int_x = 0;
    private float int_y = 0;
    private int oftenOperationTime = 500;
    private PopupWindow voice_popupWindow;
    private ImageView voice_shengyin;
    private View cancelRecordView, startRecordView;
    private MyChronometer mychronometer;
    private int sign = 0;
    /////////////////////////////////////////////

    private long base_time = 0;

    private void setTime(long time) {
        base_time = time;
    }

    private boolean getTimeTF(long time) {
        int data = (int) (time - base_time) / oftenOperationTime;
        if (data > 1 || data <= 0) {
            return true;
        } else {
            return false;
        }
    }

    //
    private File setVoiceToUp() {
        flag = false;
        if (voice_popupWindow != null) {
            voice_popupWindow.dismiss();
            mychronometer.stop();
        }
        MediaRecorderUtils.getInstence(voice_shengyin).MediaRecorderStop();
        int time = Integer.parseInt(mychronometer.getText().toString());

        if (time != 0) {
            File file = new File(MediaRecorderUtils.getInstence(voice_shengyin).getPath());
            if (file.length() > 0) {
                sendVoice(file.getPath(), "", time + "", true);
                return file;
            } else {
                ToastUtils.simpleToast(R.string.record_error_permission);
            }
        } else {
            ToastUtils.simpleToast(R.string.The_recording_time_is_too_short);
        }
        return null;
    }

    public void VoicePopupWindow() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.voice_popupwindow, null);
        voice_popupWindow = new PopupWindow(mActivity);
        voice_popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        voice_popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        voice_shengyin = (ImageView) view.findViewById(R.id.voice_shengyin);
        mychronometer = (MyChronometer) view.findViewById(R.id.mychronometer);
        cancelRecordView = view.findViewById(R.id.view_cancel_record);
        startRecordView = view.findViewById(R.id.view_start_record);
        voice_popupWindow.setContentView(view);
        voice_popupWindow.setFocusable(true);
        voice_popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.record_voice_bg));
        voice_popupWindow.showAtLocation(layout.findViewById(R.id.view_camera), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    protected void setListener() {
        buttonSend.setOnClickListener(this);
        buttonSetModeKeyboard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setModeKeyboard(view);
            }
        });
        mEditTextContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                editClick(view);
            }
        });
        view_camera = (LinearLayout) layout.findViewById(R.id.view_camera);
        view_camera.setOnClickListener(this);
        layout.findViewById(R.id.view_file).setOnClickListener(this);
//		layout.findViewById(R.id.view_video).setOnClickListener(this);
        layout.findViewById(R.id.view_photo).setOnClickListener(this);
        layout.findViewById(R.id.view_location).setOnClickListener(this);
        layout.findViewById(R.id.view_asset).setOnClickListener(this);
        layout.findViewById(R.id.view_red_packet).setOnClickListener(this);
        mLlGroupCommunity.setOnClickListener(this);
        mIvGroupCommunity.setOnClickListener(this);

//        findViewById(R.id.view_menu_location).setOnClickListener(this);
//        findViewById(R.id.view_menu_asset).setOnClickListener(this);

        // 注册接收消息广播
        receiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(CommonHelperUtils.getNewMessageBroadcastAction());
        // 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(5);
        mActivity.registerReceiver(receiver, intentFilter);

        // 注册消息状态广播
        receiverMessageStatus = new MessageStatusBroadcastReceiver();
        IntentFilter intentFilterMessageStatus = new IntentFilter(CommonHelperUtils.getMessageStatusBroadcastAction());
        // 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilterMessageStatus.setPriority(5);
        mActivity.registerReceiver(receiverMessageStatus, intentFilterMessageStatus);

        pushReceiver = new NewPushBroadcastReceiver();
        IntentFilter intentPushMessageStatus = new IntentFilter(CommonHelperUtils.getPushMessageBroadcastAction());
        intentPushMessageStatus.setPriority(5);
        mActivity.registerReceiver(pushReceiver, intentPushMessageStatus);

        // 注册一个ack回执消息的BroadcastReceiver
        IntentFilter ackMessageIntentFilter = new IntentFilter(CommonHelperUtils.getAckMessageBroadcastAction());
        ackMessageIntentFilter.setPriority(5);
        mActivity.registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

        // 注册一个消息送达的BroadcastReceiver
        IntentFilter deliveryAckMessageIntentFilter = new IntentFilter(
                CommonHelperUtils.getDeliveryAckMessageBroadcastAction());
        deliveryAckMessageIntentFilter.setPriority(5);
        mActivity.registerReceiver(deliveryAckMessageReceiver,
                deliveryAckMessageIntentFilter);


        mLlGroupAlpha.setOnClickListener(this);
        mShowPushView.setOnClickListener(this);

    }


    /**
     * onActivityResult
     */
    public void onParentActivityResult(int requestCode, int resultCode, Intent data) {
        btnContainer.setVisibility(View.GONE);

        UiUtils.scrollToPosition(listView, listView.getCount(), 0);
        if (resultCode == CommonUtils.RESULT_CODE_EXIT_GROUP) {
            mActivity.setResult(RESULT_OK);
            mActivity.finish();
            return;
        }
        if (requestCode == CommonUtils.REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
                case CommonUtils.RESULT_CODE_COPY: // 复制消息
                    ItemMessage copyMsg = adapter.getItem(data
                            .getIntExtra("position", -1));
                    if (copyMsg != null && copyMsg.getBody() != null && copyMsg.getBody() instanceof ItemTextItemMessageBody) {
                        clipboard.setText(((ItemTextItemMessageBody) copyMsg.getBody())
                                .getMessage());
                    }
                    break;
                case CommonUtils.RESULT_CODE_DELETE: // 删除消息
                    ItemMessage deleteMsg = adapter.getItem(data
                            .getIntExtra("position", -1));
                    if (deleteMsg != null) {
                        conversation.removeMessage(deleteMsg.getMsgId());
                        adapter.refresh();

                        UiUtils.scrollToPosition(listView, data.getIntExtra("position",
                                adapter.getCount()) - 1, 0);

                        OneAccountHelper.getDatabase().deleteMessageByUuid(deleteMsg.getMsgId());
                    }
                    break;

                case CommonUtils.RESULT_CODE_REWARD: // 赞赏消息
                    ItemMessage rewardMsg = adapter.getItem(data
                            .getIntExtra("position", -1));
                    if (rewardMsg != null) {
                        JumpAppPageUtil.jumpPayMsgPage(getActivity(), rewardMsg.getFrom(), rewardMsg.getMsgId());
                    }
                    break;

                case CommonUtils.RESULT_CODE_FORWARD: // 转发消息
                    // ItemMessage forwardMsg = (ItemMessage) adapter.getItem(data
                    // .getIntExtra("position", 0));
                    // Intent intent = new Intent(this,
                    // ForwardMessageActivity.class);
                    // intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
                    // startActivity(intent);

                    break;

                default:
                    break;
            }
        }
        if (resultCode == RESULT_OK) { // 清空消息
            if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
                // FIXME: 2017/8/1
                // 清空会话
//				getInstance().clearConversation(toChatUsername);

                adapter.refresh();
            } else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraSaveFile != null && new File(cameraSaveFile).exists())
                    sendPicture(cameraSaveFile);
            } else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // 发送本地选择的视频

                int duration = data.getIntExtra("dur", 0);
                String videoPath = data.getStringExtra("path");
                File file = new File(PathUtil.getInstance().getImagePath(),
                        "thvideo" + TimeUtils.getTrueTime());
                Bitmap bitmap = null;
                FileOutputStream fos = null;
                try {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                    if (bitmap == null) {
                        UtilLog.d("chatactivity",
                                "problem load video thumbnail bitmap,use default icon");
                        bitmap = BitmapFactory.decodeResource(getResources(),
                                R.drawable.app_panel_video_icon);
                    }
                    fos = new FileOutputStream(file);

                    bitmap.compress(CompressFormat.JPEG, 100, fos);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        fos = null;
                    }
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }

                }
                sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);

            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        sendFile(uri);
                    }
                }

            } else if (requestCode == REQUEST_CODE_MAP) { // 地图
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
                if (locationAddress != null && !locationAddress.equals("")) {
//                    more(more);
                    softInputLayout.hideAllView();
                    sendLocationMsg(latitude, longitude, "", locationAddress);
                } else {
                    String st = getResources().getString(
                            R.string.unable_to_get_loaction);
                    Toast.makeText(mActivity, st, Toast.LENGTH_SHORT).show();
                }
                // 重发消息
            } else if (requestCode == REQUEST_CODE_TEXT
                    || requestCode == REQUEST_CODE_VOICE
                    || requestCode == REQUEST_CODE_PICTURE
                    || requestCode == REQUEST_CODE_LOCATION
                    || requestCode == REQUEST_CODE_VIDEO
                    || requestCode == REQUEST_CODE_FILE) {
                resendMessage();
            } else if (requestCode == CommonUtils.REQUEST_CODE_COPY_AND_PASTE) {
                // 粘贴
                if (!TextUtils.isEmpty(clipboard.getText())) {
                    String pasteText = clipboard.getText().toString();
                    if (pasteText.startsWith(COPY_IMAGE)) {
                        // 把图片前缀去掉，还原成正常的path
                        sendPicture(pasteText.replace(COPY_IMAGE, ""));
                    }

                }
            } else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
                ItemMessage deleteMsg = adapter.getItem(data
                        .getIntExtra("position", -1));
                addUserToBlacklist(deleteMsg.getFrom());
            } else if (conversation.getMsgCount() > 0) {
                adapter.refresh();
                mActivity.setResult(RESULT_OK);
            } else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
                adapter.refresh();
            }
        }
    }

    /**
     * 消息图标点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.view_camera) {
            mActivity.checkPermission(new BaseActivity.CheckPermListener() {
                @Override
                public void superPermission() {
                    hideKeyboard();
                    selectPicFromCamera();// 点击照相图标
                }
            }, R.string.camera, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        } else if (id == R.id.view_file) {
            hideKeyboard();
            // 发送文件
//			selectFileFromLocal();
            Toast.makeText(mActivity, R.string.feature_come_soon, Toast.LENGTH_SHORT).show();

        } else if (id == R.id.view_video) {
            hideKeyboard();
//			// 视频通话
//			if (!getInstance().isConnected())
//				Toast.makeText(this, Constants.NET_ERROR, 0).show();
//			else
//				startActivity(new Intent(this, VideoCallActivity.class)
//						.putExtra("username", toChatUsername).putExtra(
//								"isComingCall", false));

        } else if (id == R.id.view_photo) {
            mActivity.checkPermission(new BaseActivity.CheckPermListener() {
                @Override
                public void superPermission() {
                    hideKeyboard();
                    selectPicFromLocal(); // 点击图片图标
                }
            }, R.string.gallery, Manifest.permission.WRITE_EXTERNAL_STORAGE);


//            case R.id.view_menu_location:
        } else if (id == R.id.view_location) {
            hideKeyboard();
            // TODO 位置
            mActivity.startActivityForResult(new Intent(mActivity, TencentMapActivity.class),
                    REQUEST_CODE_MAP);
//                Toast.makeText(this, R.string.feature_come_soon, Toast.LENGTH_SHORT).show();

//		case R.id.view_audio:
//			Toast.makeText(this, R.string.feature_come_soon, Toast.LENGTH_SHORT).show();
////			// 语音通话
////			if (!getInstance().isConnected())
////				Toast.makeText(this, Constants.NET_ERROR, 0).show();
////			else
////				startActivity(new Intent(CommonUtils.this,
////						VoiceCallActivity.class).putExtra("username",
////						toChatUsername).putExtra("isComingCall", false));
//			break;
//            case R.id.view_menu_asset:
        } else if (id == R.id.view_asset) {//发送资产
            hideKeyboard();
//                DialogUtil.selectAssetDialog(mActivity, new DialogUtil.ConfirmCallBackInf() {
//                    @Override
//                    public void onConfirmClick(String coinSymbol) {
//                        UserContactItem userContactItem = OneAccountHelper.getDatabase().getUserContactItemById(toChatUsername);
//                        if (userContactItem != null) {
//                            JumpAppPageUtil.jumpIOUAccountTransferPage(mActivity, coinSymbol, userContactItem.getAccountName(), true);
//                        }
//                    }
//                });


        } else if (id == R.id.view_red_packet) {//发红包
            hideKeyboard();
            JumpAppPageUtil.jumpSendRedPacketPage(mActivity, toChatUsername, CommonConstants.RED_PACKET_TYPE_GROUP, null);

        } else if (id == R.id.iv_emoticons_normal) {// 点击显示表情框
            hideKeyboard();
            iv_emoticons_normal.setVisibility(View.GONE);
            iv_emoticons_checked.setVisibility(View.VISIBLE);
            btnContainer.setVisibility(View.GONE);
            emojiIconContainer.setVisibility(View.VISIBLE);
            more.setVisibility(View.VISIBLE);

        } else if (id == R.id.iv_emoticons_checked) {// 点击隐藏表情框
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.GONE);
            btnContainer.setVisibility(View.VISIBLE);
            emojiIconContainer.setVisibility(View.GONE);
            more.setVisibility(View.GONE);
//                manager.showSoftInput(getCurrentFocus(), 0);

        } else if (id == R.id.btn_send) {// 点击发送按钮(发文字和表情)
            final String s = mEditTextContent.getText().toString();

//                boolean flag=false;
//                //language=RegExp
//
//                StringBuilder head = new StringBuilder();
//                for (int i=0;i<s.length();i++)
//                {
//                    String index = String.valueOf(s.charAt(i));
//
//                    if(s.charAt(i)<255!=flag)
//                    {
//                        head.append(" ");
//                    }
//                    head.append(index);
//                    flag=s.charAt(i)<255;
//                }

//                sendText(head.toString());
            final String msgType = CommonUtils.getMsgTypeByMemoString(s);
            if (chatType == Constants.CHATTYPE_GROUP && mGroupInfo != null && mGroupInfo.public_status == CommonConstants.CHAT_GROUP_STATUS_PUBLIC && !SharePreferenceUtils.contains(SharePreferenceUtils.SP_FIRST_SEND_URL)
                    && msgType.equals(oneapp.onechat.oneandroid.onewallet.Constants.MSG_TYPE_URL)) {

                DialogUtil.simpleDialog(mActivity, getString(R.string.group_chat_send_url_tip), new DialogUtil.ConfirmCallBackInf() {
                    @Override
                    public void onConfirmClick(String content) {
                        SharePreferenceUtils.putObject(SharePreferenceUtils.SP_FIRST_SEND_URL, false);
                        sendText(s, msgType);
                    }
                });

            } else {

                sendText(s, msgType);
            }

        } else if (id == R.id.btn_set_mode_voice) {
            mActivity.checkPermission(new BaseActivity.CheckPermListener() {
                @Override
                public void superPermission() {
                    setModeVoice();
                }
            }, R.string.file, Manifest.permission.RECORD_AUDIO);

        } else if (id == R.id.iv_group_community) {
            if (mLlGroupCommunity.getVisibility() == View.GONE) {
                mLlGroupCommunity.setVisibility(View.VISIBLE);
                buttonSetModeVoice.setVisibility(View.GONE);
                mEditView.setVisibility(View.GONE);
                btnMore.setVisibility(View.GONE);
                mIvGroupCommunity.setImageDrawable(getResources().getDrawable(R.drawable.switch_keyboard));
            } else {
                mLlGroupCommunity.setVisibility(View.GONE);
                buttonSetModeVoice.setVisibility(View.VISIBLE);
                mEditView.setVisibility(View.VISIBLE);
                btnMore.setVisibility(View.VISIBLE);
                mIvGroupCommunity.setImageDrawable(getResources().getDrawable(R.drawable.switch_menu));
                buttonSetModeKeyboard.setVisibility(View.GONE);
                buttonPressToSpeak.setVisibility(View.GONE);
                Keyboard.hideKeyboard(mActivity);
                softInputLayout.hideAllView();
            }

        } else if (id == R.id.ll_group_community) {
            JumpAppPageUtil.jumpWeiboList(mActivity, toChatUsername);

        } else if (id == R.id.view_push_msg) {//                msgDrawerLayout.openDrawer(mLeftView);

        } else {
        }
    }

//    public static boolean Test(String regix, String str)
//    {
//        return Pattern.compile(regix).matcher(str).matches();
//    }

    /**
     * 照相获取图片
     */
    public void selectPicFromCamera() {
        if (!CommonUtils.isExitsSdcard()) {
            String st = getResources().getString(
                    R.string.sd_card_does_not_exist);
            Toast.makeText(mActivity, st, Toast.LENGTH_SHORT).show();
            return;
        }

        cameraSaveFile = PathUtil.getInstance().getImagePath() + "one"
                + TimeUtils.getTrueTime() + ".jpg";
        JumpAppOutUtil.jumpTakePhoto(mActivity, cameraSaveFile, REQUEST_CODE_CAMERA);
    }

    /**
     * 选择文件
     */
    private void selectFileFromLocal() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("*/*");
        }
        mActivity.startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
        }
        mActivity.startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    /**
     * 发送文本消息
     *
     * @param content message content
     *                boolean resend
     */
    private void sendText(String content, String msgType) {
        String jsonParam = mEditTextContent.getUserIdString();
        ItemMessage message = messageSender.sendText(content, msgType, jsonParam);
//        if (content == null || content.length() <= 0) {
//            return;
//        }
//        if (!NetUtils.hasNetwork(mActivity)) {
//            Toast.makeText(
//                    mActivity,
//                    getString(R.string.string_network_disconnect),
//                    Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//
//            if (toChatUsername.equals(OneAccountHelper.getAccountId())) {
//                Toast.makeText(mActivity, R.string.message_send_self, Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            ItemMessage message = null;
//
//            // 消息id
//            final String uuid = UUID.randomUUID().toString();
//
//            // 如果是小密圈，设置chattype,默认是单聊
//            if (chatType == Constants.CHATTYPE_GROUP) {
//                message = CommonHelperUtils.buildTextMessage(OneAccountHelper.getAccountId(), toChatUsername,
//                        content, ItemMessage.Direct.SEND, ChatType.GroupChat, uuid);
//                final String groupUid = toChatUsername;
//                message.setGroupUid(groupUid);
//
//                String jsonParam = mEditTextContent.getUserIdString();
//
//                if (mGroupInfo != null && mGroupInfo.getMembers_size() > 0) {
//                    MemoMessage tempMemo = new MemoMessage(BtsApplication.getAdjustTimeNowMillis(),
//                            MemoMessage.MSG_TYPE_TXT, MemoMessage.CMD_SEND_MSG, content, null,
//                            groupUid, null, jsonParam, uuid);
//
//                    String strJson = tempMemo.toString();
//
//                    // FIXME: 2017/11/17 hs
//                    // 保存聊天消息
//                    final UserChatItem chatItem = new UserChatItem(null, null, tempMemo.getTime(), tempMemo.getTime(),
//                            tempMemo.getTime(), message.getFrom(), message.getTo(),
//                            "", "", strJson,
//                            CommonConstants.DEFAULT_DAO_CODE, "", uuid, 0,
//                            ItemMessage.Status.CREATE.ordinal(), 0, 0, groupUid);
//
//                    final String encryptStrJson = Util.bytesToHex(Util.encryptAES(strJson.getBytes(Charsets.UTF_8), mGroupInfo.encrypt_key.getBytes(Charsets.UTF_8)));
//
//                    String messageContent = "";
//                    if (mGroupInfo.public_status == CommonConstants.CHAT_GROUP_STATUS_PUBLIC) {
//                        messageContent = content;
//                    }
//                    //添加群聊请求
//                    RequestUtils.AddGroupMessageInfo(mActivity, groupUid, encryptStrJson, messageContent, msgType, uuid, null);
//                    OneAccountHelper.getDatabase().putUserChat(chatItem);
////                    for (int i = 0; i < 50000; i++) {
////                        chatItem.setUuid(UUID.randomUUID().toString());
////                        OneAccountHelper.getDatabase().putUserChat(chatItem);
////                    }
//
///**
// * 小密圈
// */
////                    for(String toOne : mGroupMemberList)
////                    {
////                        message.setReceipt(toOne);
////                        message.setTo(toOne);
////
////						MainActivity.mSendWebsocket.sendFunds(false, "",
////								CommonConstants.CHAT_ASSET_AMOUNT,
////								CommonConstants.CHAT_ASSET_SYMBOL,
////								message.getFrom(), message.getTo(),
////								tempMemo, false, true);
////						break;
////                    }
//                }
//            } else {
//                message = CommonHelperUtils.buildTextMessage(OneAccountHelper.getAccountId(), toChatUsername,
//                        content, ItemMessage.Direct.SEND, ChatType.Chat, uuid);
//
//                MemoMessage tempMemo = new MemoMessage(BtsApplication.getAdjustTimeNowMillis(),
//                        MemoMessage.MSG_TYPE_TXT, MemoMessage.CMD_SEND_MSG, content, null,
//                        null, null, null, uuid);
//
//                String strJson = tempMemo.toString();
//
//                // 保存聊天消息
//                UserChatItem chatItem = new UserChatItem(null, null, tempMemo.getTime(), tempMemo.getTime(),
//                        tempMemo.getTime(), message.getFrom(), message.getTo(),
//                        "", "", strJson,
//                        CommonConstants.DEFAULT_DAO_CODE, "", uuid, 0,
//                        ItemMessage.Status.CREATE.ordinal(), 0, 0, "");
//
//                OneAccountHelper.getDatabase().putUserChat(chatItem);
//
//                MessageSenderHandler.getInstance().startMessageSender();
//            }

            // 把messgage加到conversation中
            mEditTextContent.setText("");
            conversation.addMessage(message);
            // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
            adapter.refresh();
            UiUtils.scrollToPosition(listView, listView.getCount() - 1);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 发送语音
     *
     * @param filePath
     * @param fileName
     * @param length
     * @param isResend
     */
    private void sendVoice(final String filePath, String fileName, String length,
                           boolean isResend) {
        if (!(new File(filePath).exists())) {
            return;
        }
        ItemMessage message = messageSender.sendVoice(filePath, fileName, length, isResend);

        if (message == null) {
            return;
        }
        conversation.addMessage(message);
        adapter.refresh();
        UiUtils.scrollToPosition(listView, listView.getCount() - 1);
        mActivity.setResult(RESULT_OK);
    }

    /**
     * 发送图片
     *
     * @param filePath
     */
    private void sendPicture(final String filePath) {

        DialogUtil.showSendImgDialog(mActivity, filePath, new DialogUtil.ConfirmCallBackInf() {
            @Override
            public void onConfirmClick(final String lubanFilePath) {
                ItemMessage message = messageSender.sendPicture(filePath);

                if (message == null) {
                    return;
                }
                conversation.addMessage(message);
                adapter.refresh();
                UiUtils.scrollToPosition(listView, listView.getCount() - 1);
                mActivity.setResult(RESULT_OK);
            }
        });

    }

    /**
     * 发送视频消息
     */
    private void sendVideo(final String filePath, final String thumbPath,
                           final int length) {
        final File videoFile = new File(filePath);
        if (!videoFile.exists()) {
            return;
        }
        try {
            ItemMessage message = ItemMessage
                    .createSendMessage(ItemMessage.Type.VIDEO);
            // 如果是小密圈，设置chattype,默认是单聊
            if (chatType == Constants.CHATTYPE_GROUP)
                message.setChatType(ChatType.GroupChat);
            String to = toChatUsername;
            message.setReceipt(to);
            ItemVideoItemMessageBodyItem body = new ItemVideoItemMessageBodyItem(videoFile, thumbPath,
                    length, videoFile.length());
            message.addBody(body);
            conversation.addMessage(message);
            listView.setAdapter(adapter);
            adapter.refresh();
            UiUtils.scrollToPosition(listView, listView.getCount() - 1);
            mActivity.setResult(RESULT_OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    private void sendPicByUri(Uri selectedImage) {
        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = mActivity.getContentResolver().query(selectedImage, null, null,
                null, null);
        String st8 = getResources().getString(R.string.cant_find_pictures);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(mActivity, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendPicture(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(mActivity, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendPicture(file.getAbsolutePath());
        }

    }

    /**
     * 发送位置信息
     *
     * @param latitude
     * @param longitude
     * @param imagePath
     * @param locationAddress
     */
    private void sendLocationMsg(double latitude, double longitude,
                                 String imagePath, String locationAddress) {
        ItemMessage message = messageSender.sendLocationMsg(latitude, longitude, imagePath, locationAddress);

        if (message == null) {
            return;
        }
        conversation.addMessage(message);
        adapter.refresh();
        UiUtils.scrollToPosition(listView, listView.getCount() - 1);
        mActivity.setResult(RESULT_OK);

    }

    /**
     * 发送文件
     *
     * @param uri
     */
    private void sendFile(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = mActivity.getContentResolver().query(uri, projection, null,
                        null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            String st7 = getResources().getString(R.string.File_does_not_exist);
            Toast.makeText(mActivity, st7, Toast.LENGTH_SHORT).show();
            return;
        }
        if (file.length() > 10 * 1024 * 1024) {
            String st6 = getResources().getString(
                    R.string.The_file_is_not_greater_than_10_m);
            Toast.makeText(mActivity, st6, Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建一个文件消息
        ItemMessage message = ItemMessage.createSendMessage(ItemMessage.Type.FILE);
        // 如果是小密圈，设置chattype,默认是单聊
        if (chatType == Constants.CHATTYPE_GROUP)
            message.setChatType(ChatType.GroupChat);

        message.setReceipt(toChatUsername);
        // icon_recive_red message body
        ItemNormalItemFileItemMessageBody body = new ItemNormalItemFileItemMessageBody(new File(
                filePath));
        message.addBody(body);
        conversation.addMessage(message);
        listView.setAdapter(adapter);
        adapter.refresh();
        UiUtils.scrollToPosition(listView, listView.getCount() - 1);
        mActivity.setResult(RESULT_OK);
    }


    /**
     * 重发消息
     */
    private void resendMessage() {
//        ItemMessage msg = null;
//        msg = conversation.getMessage(resendPos);
//        // msg.setBackSend(true);
//        msg.status = ItemMessage.Status.CREATE;
//
//        adapter.refresh();
//        listView.setSelection(resendPos);


        // FIXME: 2017/7/31

//		ItemTextItemMessageBody txtBody = (ItemTextItemMessageBody)msg.getBody();
//
//		MemoMessage tempMemo = new MemoMessage(BtsApplication.getAdjustTimeNowMillis(),
//				MemoMessage.MSG_TYPE_TXT, MemoMessage.CMD_SEND_MSG, txtBody.getMessage(), null,
//				null, null, null);
//
//		String strJson = tempMemo.toString();
//
//		MainActivity.mSendWebsocket.sendFunds(false, "", "0.01", "1.3.0",
//				msg.getFrom(), msg.getTo(), strJson, tempMemo.getTime(), true, true);
    }

    /**
     * 显示语音图标按钮
     */
    public void setModeVoice() {
        hideKeyboard();
        mEditView.setVisibility(View.GONE);
        more.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
        buttonSend.setImageResource(R.drawable.chat_send_btn_gray);
        buttonSend.setVisibility(View.GONE);
        btnMore.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.GONE);
        btnContainer.setVisibility(View.VISIBLE);
        emojiIconContainer.setVisibility(View.GONE);

    }

    /**
     * 显示键盘图标
     *
     * @param view
     */
    public void setModeKeyboard(View view) {
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        mEditView.setVisibility(View.VISIBLE);
        mEditTextContent.requestFocus();
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mEditTextContent.getText())) {
            btnMore.setVisibility(View.VISIBLE);
            buttonSend.setImageResource(R.drawable.chat_send_btn_gray);
            buttonSend.setVisibility(View.GONE);
        } else {
            btnMore.setVisibility(View.GONE);
            buttonSend.setImageResource(R.drawable.chat_send_btn);
            buttonSend.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 点击清空聊天记录
     *
     * @param view
     */
    public void emptyHistory(View view) {
        String st5 = getResources().getString(
                R.string.Whether_to_empty_all_chats);
        mActivity.startActivityForResult(
                new Intent(mActivity, AlertDialog.class)
                        .putExtra("titleIsCancel", true).putExtra("msg", st5)
                        .putExtra("cancel", true), REQUEST_CODE_EMPTY_HISTORY);
    }

    /**
     * 点击进入群组详情
     *
     * @param view
     */
    public void toGroupDetails(View view) {
        // startActivityForResult(
        // (new Intent(this, GroupDeatilActivity.class).putExtra(
        // "groupId", toChatUsername)), REQUEST_CODE_GROUP_DETAIL);
    }

    /**
     * 显示或隐藏图标按钮页
     *
     * @param view
     */
//    public void more(View view) {
//        if (more.getVisibility() == View.GONE) {
//            System.out.println("more gone");
//            hideKeyboard();
//            more.setVisibility(View.VISIBLE);
//            btnContainer.setVisibility(View.VISIBLE);
//            emojiIconContainer.setVisibility(View.GONE);
//        } else {
//            if (emojiIconContainer.getVisibility() == View.VISIBLE) {
//                emojiIconContainer.setVisibility(View.GONE);
//                btnContainer.setVisibility(View.VISIBLE);
//                iv_emoticons_normal.setVisibility(View.VISIBLE);
//                iv_emoticons_checked.setVisibility(View.INVISIBLE);
//            } else {
//                more.setVisibility(View.GONE);
//            }
//
//        }
//
//    }

    /**
     * 点击文字输入框
     *
     * @param v
     */
    public void editClick(View v) {
        if (softInputLayout.ifHideAll()) {
            UiUtils.scrollToPosition(listView, listView.getCount() - 1);
            if (more.getVisibility() == View.VISIBLE) {
                more.setVisibility(View.GONE);
            }
        }

        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.GONE);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mEditTextContent, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mEditTextContent);
    }

    /**
     * 消息广播接收者
     */
    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                // 记得把广播给终结掉
//				abortBroadcast();

                if (true) {
                    new LoadMsgListTask().execute();
//                    refreshMsgList();
                    return;
                }
                String username = intent.getStringExtra("from");
                String msgid = intent.getStringExtra(JumpParamsContants.INTENT_MSG_IDS);
                if (msgid == null || msgid.equals("")) {
                    return;
                }

                HistoricalTransferEntry itemTrans = OneAccountHelper.getDatabase().getUserChat(msgid);
                if (itemTrans == null) {
                    return;
                }

                ItemMessage message = CommonHelperUtils.convertTransactionToMessage(CommonConstants.DEFAULT_DAO_CODE, itemTrans);
                if (message == null) {
                    return;
                }

                // 判断是否是小密圈
                if (itemTrans.getGroupUid() != null && itemTrans.getGroupUid().length() > 32) {
                    message.setChatType(ChatType.GroupChat);
                    if (!itemTrans.getGroupUid().equals(toChatUsername)) {
                        // 非本小密圈消息
                        return;
                    }
                } else {
                    // 当前是否是
                    if (chatType == Constants.CHATTYPE_GROUP) {
                        // 不是小密圈消息
                        return;
                    }

                    if (message.getTo() == null || !message.getTo().equals(OneAccountHelper.getAccountId()) || !message.getFrom().equals(toChatUsername)) {
                        // 不是发给当前用户的
                        return;
                    }
                }

                message.direct = ItemMessage.Direct.RECEIVE;

                // 把messgage加到conversation中
                conversation.addMessage(message);
                // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
                adapter.refresh();
                UiUtils.scrollToPosition(listView, listView.getCount() - 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updatePushMsgList() {
    }

    /**
     * 推送消息广播接收者
     */
    private class NewPushBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String username = intent.getStringExtra("from");
                String msgid = intent.getStringExtra(JumpParamsContants.INTENT_MSG_IDS);

                updatePushMsgList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 消息广播接收者
     */
    private class MessageStatusBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String username = intent.getStringExtra("from");
                String msgid = intent.getStringExtra(JumpParamsContants.INTENT_MSG_IDS);
                if (msgid == null || msgid.equals("")) {
                    return;
                }

                HistoricalTransferEntry itemTrans = OneAccountHelper.getDatabase().getUserChat(msgid);
                if (itemTrans == null) {
                    return;
                }

                ItemMessage message = CommonHelperUtils.convertTransactionToMessage(CommonConstants.DEFAULT_DAO_CODE, itemTrans);
                if (message == null) {
                    return;
                }

                if (message.getFrom().equals(OneAccountHelper.getAccountId())) {
                    // 更新自己发送消息状态
                    ItemMessage itemCache = conversation.getCacheMessage(msgid);
                    if (itemCache != null) {
                        //更新状态
                        itemCache.status = message.status;
                        adapter.updateMessageStatus(message);
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processOneMessage(JSONObject tempItem) {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 消息回执BroadcastReceiver
     */
    private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // FIXME: 2017/8/1
//			abortBroadcast();
//
//			String msgid = intent.getStringExtra(IntentParamsContants.INTENT_MSG_IDS);
//			String from = intent.getStringExtra("from");
//			ItemConversation conversation = getInstance()
//					.getConversation(from);
//			if (conversation != null) {
//				// 把message设为已读
//				ItemMessage msg = conversation.getMessage(msgid);
//				if (msg != null) {
//					msg.isAcked = true;
//				}
//			}
//			adapter.notifyDataSetChanged();

        }
    };

    /**
     * 消息送达BroadcastReceiver
     */
    private BroadcastReceiver deliveryAckMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // FIXME: 2017/8/1
//			abortBroadcast();
//
//			String msgid = intent.getStringExtra(IntentParamsContants.INTENT_MSG_IDS);
//			String from = intent.getStringExtra("from");
//			ItemConversation conversation = getInstance()
//					.getConversation(from);
//			if (conversation != null) {
//				// 把message设为已读
//				ItemMessage msg = conversation.getMessage(msgid);
//				if (msg != null) {
//					msg.isDelivered = true;
//				}
//			}
//
//			adapter.notifyDataSetChanged();
        }
    };
    private PowerManager.WakeLock wakeLock;

    /**
     * 按住说话listener
     */
    class PressToSpeakListen implements OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // FIXME: 2017/8/1

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    animationDrawable.start();
                    if (!CommonUtils.isExitsSdcard()) {
                        String st4 = getResources().getString(
                                R.string.Send_voice_need_sdcard_support);
                        Toast.makeText(mActivity, st4, Toast.LENGTH_SHORT)
                                .show();
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);

//                        if (VoicePlayClickListener.isPlaying)
//                            VoicePlayClickListener.currentPlayListener
//                                    .stopPlayVoice();
                        recordingContainer.setVisibility(View.VISIBLE);
                        recordingHint
                                .setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        voiceRecorder.startRecording(null, toChatUsername,
                                mActivity);
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (voiceRecorder != null)
                            voiceRecorder.discardRecording();
                        recordingContainer.setVisibility(View.INVISIBLE);
                        Toast.makeText(mActivity, R.string.recoding_fail,
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        recordingHint
                                .setText(getString(R.string.release_to_cancel));
                        recordingHint
                                .setBackgroundResource(R.color.base_color);
                    } else {
                        recordingHint
                                .setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        animationDrawable.start();
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    if (animationDrawable.isRunning()) {
                        animationDrawable.stop();
                    }
                    v.setPressed(false);
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        // discard the recorded audio.
                        voiceRecorder.discardRecording();

                    } else {
                        // stop recording and send voice file
                        String st1 = getResources().getString(
                                R.string.Recording_without_permission);
                        String st2 = getResources().getString(
                                R.string.The_recording_time_is_too_short);
                        String st3 = getResources().getString(
                                R.string.send_failure_please);
                        try {
                            int length = voiceRecorder.stopRecoding();
                            if (length > 0) {
                                sendVoice(voiceRecorder.getVoiceFilePath(),
                                        voiceRecorder
                                                .getVoiceFileName(toChatUsername),
                                        Integer.toString(length), false);
                            } else if (length == -1011) {
                                Toast.makeText(mActivity, st1,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mActivity, st2,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(mActivity, st3,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                    return true;
                default:
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (voiceRecorder != null)
                        voiceRecorder.discardRecording();
                    return false;
            }
        }
    }


    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 0; x <= getSum; x++) {
            String filename = "f_static_0" + x;

            reslist.add(filename);

        }
        return reslist;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        refreshGroupMsgHandler.removeCallbacks(runnable);
//		getInstance().removeGroupChangeListener(groupListener);

        // 注销广播
        try {
            mActivity.unregisterReceiver(receiver);
            receiver = null;
        } catch (Exception e) {
        }
        try {
            mActivity.unregisterReceiver(receiverMessageStatus);
            receiverMessageStatus = null;
        } catch (Exception e) {
        }
        try {
            mActivity.unregisterReceiver(ackMessageReceiver);
            ackMessageReceiver = null;
            mActivity.unregisterReceiver(deliveryAckMessageReceiver);
            deliveryAckMessageReceiver = null;
        } catch (Exception e) {
        }
        try {
            mActivity.unregisterReceiver(pushReceiver);
            pushReceiver = null;
        } catch (Exception e) {
        }

        if (voice_popupWindow != null)
            voice_popupWindow.dismiss();
    }

    Handler refreshGroupMsgHandler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            RequestUtils.GetGroupMessageInfo(context, null);
            OneGroupHelper.syncGroupChatInfo();
            refreshGroupMsgHandler.postDelayed(this, oneapp.onechat.oneandroid.onewallet.Constants.REFRESH_GROUP_MSG_TIME_CHAT);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        setUpView();
        if (chatType == Constants.CHATTYPE_GROUP) {
            refreshGroupMsgHandler.postDelayed(runnable, oneapp.onechat.oneandroid.onewallet.Constants.REFRESH_GROUP_MSG_TIME_CHAT);
        }

        OneAccountHelper.getDatabase().clearUserConversationExtraTip(toChatUsername);
    }

    @Override
    public void onPause() {
        super.onPause();

        refreshGroupMsgHandler.removeCallbacks(runnable);

        if (wakeLock.isHeld())
            wakeLock.release();

// 把此会话的未读数置为0
        if (conversation != null)
            conversation.resetUnreadMsgCount();
        OneAccountHelper.getDatabase().clearUserConversationUnreadNum(toChatUsername);

        OneAccountHelper.getDatabase().clearUserConversationExtraTip(toChatUsername);

        VoicePlayClickListener.stopCurrentPlay();
//		if (VoicePlayClickListener.isPlaying
//				&& VoicePlayClickListener.currentPlayListener != null) {
//			// 停止语音播放
//			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
//		}

        try {
            // 停止录音
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
                recordingContainer.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        softInputLayout.hideAllView();
//        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
//            if (getCurrentFocus() != null)
//                manager.hideSoftInputFromWindow(getCurrentFocus()
//                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
    }


    /**
     * 加入到黑名单
     *
     * @param username
     */
    private void addUserToBlacklist(String username) {

        // FIXME: 2017/8/1
//		String st11 = getResources().getString(
//				R.string.Move_into_blacklist_success);
//		String st12 = getResources().getString(
//				R.string.Move_into_blacklist_failure);
//		try {
//			getInstance().addUserToBlackList(username, false);
//			Toast.makeText(getApplicationContext(), st11, 0).show();
//		} catch (OneChatException e) {
//			e.printStackTrace();
//			Toast.makeText(getApplicationContext(), st12, 0).show();
//		}
    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        mActivity.finish();
    }

//    /**
//     * 覆盖手机返回键
//     */
//    @Override
//    public void onBackPressed() {
//        if (more.getVisibility() == View.VISIBLE) {
//            more.setVisibility(View.GONE);
//            iv_emoticons_normal.setVisibility(View.VISIBLE);
//            iv_emoticons_checked.setVisibility(View.GONE);
//        } else {
//            super.onBackPressed();
//        }
//    }

    /**
     * listview滑动监听listener
     */
    private class ListScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            try {
//				switch (scrollState) {
//                case OnScrollListener.SCROLL_STATE_IDLE:
//                    if (view.getFirstVisiblePosition() == 0 && !isloading
//                            && haveMoreData) {
//                        loadmorePB.setVisibility(View.VISIBLE);
//                        // sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
//                        List<ItemMessage> messages;
//                        try {
//                            // 获取更多messges，调用此方法的时候从db获取的messages
//                            // sdk会自动存入到此conversation中
//							// FIXME: 2017/8/2
//							if (chatType == CHATTYPE_SINGLE)
//                                messages = conversation.loadMoreMsgFromDB(adapter
//                                        .getItem(0).getMsgId(), pagesize);
//                            else
//                                messages = conversation.loadMoreGroupMsgFromDB(
//                                        adapter.getItem(0).getMsgId(), pagesize);
//                        } catch (Exception e1) {
//                            loadmorePB.setVisibility(View.GONE);
//                            return;
//                        }
//                        try {
//                            Thread.sleep(300);
//                        } catch (InterruptedException e) {
//                        }
//
//                        if(messages == null)
//						{
//							// FIXME: 2017/8/2
//							int i = 0;
//							int j = i;
//						}
//
//                        if (messages.size() != 0) {
//                            // 刷新ui
//                            adapter.notifyDataSetChanged();
//                            listView.setSelection(messages.size() - 1);
//                            if (messages.size() != pagesize)
//                                haveMoreData = false;
//                        } else {
//                            haveMoreData = false;
//                        }
//                        loadmorePB.setVisibility(View.GONE);
//                        isloading = false;
//
//                    }
//                    break;
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (mLastUnReadPosition >= firstVisibleItem || firstVisibleItem <= 1) {
                mTopNewMsgTv.setVisibility(View.GONE);
                mLastUnReadPosition = view.getCount();
            }

            if (mLastUnReadPosition > 0 && mLastUnReadPosition <= firstVisibleItem + visibleItemCount) {
                mBottomNewMsgTv.setVisibility(View.GONE);
                OneAccountHelper.getDatabase().clearUserConversationUnreadNum(toChatUsername);
            }
            if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                mBottomNewMsgTv.setVisibility(View.GONE);
            }
        }

    }


    /**
     * 转发消息
     *
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
        // FIXME: 2017/8/1
//		ItemMessage forward_msg = getInstance().getMessage(
//				forward_msg_id);
//
//		ItemMessage.Type type = forward_msg.getType();
//		switch (type) {
//		case TXT:
//			// 获取消息内容，发送消息
//			String content = ((ItemTextItemMessageBody) forward_msg.getBody())
//					.getMessage();
//			sendText(content);
//			break;
//		case IMAGE:
//			// 发送图片
//			String filePath = ((ItemImageItemMessageBodyItem) forward_msg.getBody())
//					.getLocalUrl();
//			if (filePath != null) {
//				File file = new File(filePath);
//				if (!file.exists()) {
//					// 不存在大图发送缩略图
//					filePath = ImageUtils.getThumbnailImagePath(filePath);
//				}
//				sendPicture(filePath);
//			}
//			break;
//		default:
//			break;
//		}
    }

    /**
     * 监测群组解散或者被T事件
     */
    class ItemGroupListener extends ItemGroupReomveListener {

        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            mActivity.runOnUiThread(new Runnable() {
                String st13 = getResources().getString(R.string.you_are_group);

                public void run() {
                    if (toChatUsername.equals(groupId)) {
                        Toast.makeText(mActivity, st13, Toast.LENGTH_LONG).show();
                        // if (GroupDeatilActivity.instance != null)
                        // GroupDeatilActivity.instance.finish();
                        // finish();
                    }
                }
            });
        }

        @Override
        public void onGroupDestroy(final String groupId, String groupName) {
            // 群组解散正好在此页面，提示群组被解散，并finish此页面
            mActivity.runOnUiThread(new Runnable() {
                String st14 = getResources().getString(
                        R.string.the_current_group);

                public void run() {
                    if (toChatUsername.equals(groupId)) {
                        Toast.makeText(mActivity, st14, Toast.LENGTH_LONG).show();
                        // if (GroupDeatilActivity.instance != null)
                        // GroupDeatilActivity.instance.finish();
                        // finish();
                    }
                }
            });
        }

    }

    public String getToChatUsername() {
        return toChatUsername;
    }

    private void initUserInfo() {
        // RequestParams params = new RequestParams();
        // String userid = UserUtils.getUserID(this);
        // params.put("user_id", userid);
        // params.put("obj_id", toChatUsername);
        // netClient.post(Constants.getUserInfoURL, params, new BaseJsonRes() {
        //
        // @Override
        // public void onMySuccess(String data) {
        // User user = JSON.parseObject(data, User.class);
        // // NetClient.getIconBitmap(img_avar, user.getFace_image());
        // txt_title.setText(user.getUserName());
        // }
        //
        // @Override
        // public void onMyFailure() {
        //
        // }
        // });
    }

    private void initGroupInfo() {
//		RequestParams params = new RequestParams();
//		String userid = UserUtils.getUserID(this);
//		params.put("user_id", userid);
//		params.put("group_id", toChatUsername);
//
////		netClient.post(Constants.getUserInfoURL, params, new BaseJsonRes() {
////
////			@Override
////			public void onMySuccess(String data) {
////				GroupInfo group = JSON.parseObject(data, GroupInfo.class);
////				// NetClient.getIconBitmap(img_avar, group.getGroup_name());
////				txt_title.setText(group.getGroup_name());
////			}
////
////			@Override
////			public void onMyFailure() {
////
////			}
////		});
    }

    @Override
    public void updateView(Boolean bUpdate) {

    }

}
