package oneapp.onechat.chat.view.TitleMenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import oneapp.onechat.chat.MainActivity;
import oneapp.onechat.androidapp.R;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.Constants;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import onemessageui.utils.ViewHolder;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import onewalletui.ui.widget.ListViewAdaptWidth;
import oneapp.onechat.oneandroid.onewallet.util.UiUtils;
import onemessageui.view.activity.AddFriendActivity;
import onemessageui.view.activity.AddGroupChatActivity;
import onewalletui.ui.ScanActivity;
import onewalletui.util.jump.JumpAppPageUtil;

/**
 * 功能描述：标题按钮上的弹窗（继承自PopupWindow）
 */
public class TitlePopup extends PopupWindow {
    private Activity mContext;
    //titlePopup加号图片显示序号
    public static final int popFirstNumber = 0;
    public static final int popSecondNumber = 1;
    public static final int popThirdNumber = 2;

    // 列表弹窗的间隔
    protected final int LIST_PADDING = 10;

    // 实例化一个矩形
    private Rect mRect = new Rect();

    // 坐标的位置（x、y）
    private final int[] mLocation = new int[2];

    // 屏幕的宽度和高度
    private int mScreenWidth, mScreenHeight;

    // 判断是否需要添加或更新列表子类项
    private boolean mIsDirty;

    // 位置不在中心
    private int popupGravity = Gravity.NO_GRAVITY;

    // 弹窗子类项选中时的监听
    private OnItemOnClickListener mItemOnClickListener;

    // 定义列表对象
    private ListViewAdaptWidth mListView;

    // 定义弹窗子类项列表
    private ArrayList<ActionItem> mActionItems = new ArrayList<ActionItem>();

    public TitlePopup(Activity context, int whatFrag) {
        // 设置布局的参数
        this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, whatFrag);
    }

    public TitlePopup(Activity context, int width, int height, int whatFrag) {
        this.mContext = context;

        // 设置可以获得焦点
        setFocusable(true);
        // 设置弹窗内可点击
        setTouchable(true);
        // 设置弹窗外可点击
        setOutsideTouchable(true);

        // 获得屏幕的宽度和高度
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();

        // 设置弹窗的宽度和高度
        setWidth(width);
        setHeight(height);

        setBackgroundDrawable(new BitmapDrawable());

        // 设置弹窗的布局界面
        setContentView(LayoutInflater.from(mContext).inflate(
                R.layout.title_popup, null));
        setAnimationStyle(R.style.AnimHead);
        initUI(whatFrag);
    }

    /**
     * 初始化弹窗列表
     */
    private void initUI(final int whatFrag) {
        mListView = (ListViewAdaptWidth) getContentView().findViewById(R.id.title_list);

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long arg3) {
                // 点击子类项后，弹窗消失
                dismiss();

                if (mItemOnClickListener != null)
                    mItemOnClickListener.onItemClick(mActionItems.get(index),
                            index, whatFrag);
            }
        });

        /**
         * 默认配置
         */
        setItemOnClickListener(onitemClick);

        // 给标题栏弹窗添加子类
        switch (whatFrag) {
            case popFirstNumber:
                addAction(new ActionItem(mContext, R.string.switch_service_node,
                        R.drawable.icon_switch_service_node_new));
                addAction(new ActionItem(mContext, R.string.menu_scan_qrcode,
                        R.drawable.icon_menu_sao_new));
                addAction(new ActionItem(mContext, R.string.menu_groupchat,
                        R.drawable.icon_groupchat_new));
                addAction(new ActionItem(mContext, R.string.menu_addfriend,
                        R.drawable.icon_add_friend_new));
                addAction(new ActionItem(mContext, R.string.social_redpacket,
                        R.drawable.icon_social_redpacket));
                break;
            case popSecondNumber:
                addAction(new ActionItem(mContext, R.string.switch_service_node,
                        R.drawable.icon_switch_service_node_new));
                addAction(new ActionItem(mContext, R.string.menu_scan_qrcode,
                        R.drawable.icon_menu_sao_new));
                addAction(new ActionItem(mContext, R.string.menu_add_coin,
                        R.drawable.icon_menu_addcoin));
                break;
            case popThirdNumber:
                addAction(new ActionItem(mContext, R.string.switch_service_node,
                        R.drawable.icon_switch_service_node_new));
                addAction(new ActionItem(mContext, R.string.menu_scan_qrcode,
                        R.drawable.icon_menu_sao_new));
                addAction(new ActionItem(mContext, R.string.add_trade_team,
                        R.drawable.icon_add_transaction_new));
                addAction(new ActionItem(mContext, R.string.newton_plan,
                        R.drawable.icon_newton_plan));
                addAction(new ActionItem(mContext, R.string.transfer_fee,
                        R.drawable.icon_transaction_money_new));
                addAction(new ActionItem(mContext, R.string.money_go_and_out,
                        R.drawable.icon_money_go_and_out));
                break;
            default:
                break;
        }
//        // 给标题栏弹窗添加子类
//        addAction(new ActionItem(mContext, R.string.menu_add_coin,
//                R.drawable.icon_menu_addcoin));
//        addAction(new ActionItem(mContext, R.string.add_trade_team,
//                R.drawable.icon_menu_add_trade));
//        addAction(new ActionItem(mContext, R.string.menu_scan_qrcode,
//                R.drawable.icon_menu_sao));
//        addAction(new ActionItem(mContext, R.string.menu_addfriend,
//                R.drawable.icon_menu_addfriend));
//        addAction(new ActionItem(mContext, R.string.menu_groupchat,
//                R.drawable.icon_menu_group));
//        addAction(new ActionItem(mContext, R.string.switch_service_node,
//                R.drawable.icon_menu_detection));
    }


    private OnItemOnClickListener onitemClick = new OnItemOnClickListener() {
        @Override
        public void onItemClick(ActionItem item, int position, int whatFrag) {
            try {
                switch (whatFrag) {
                    case popFirstNumber:
                        switch (position) {
                            case 0://  节点检测
                                JumpAppPageUtil.jumpSetServiceNodePage(mContext);
                                break;
                            case 1://扫一扫
                                mContext.startActivityForResult(new Intent(mContext, ScanActivity.class), MainActivity.REQUEST_CODE_SCAN);
                                break;
                            case 2://发起小密圈
                                Utils.start_Activity(mContext,
                                        AddGroupChatActivity.class);
                                break;
                            case 3://添加朋友
                                Utils.start_Activity(mContext, AddFriendActivity.class,
                                        new BasicNameValuePair(Constants.NAME, "添加朋友"));
                                break;
                            case 4://社交红包
                                JumpAppPageUtil.jumpSendRedPacketPage(mContext, "", CommonConstants.RED_PACKET_TYPE_SOCIAL, null);
                                break;
                            default:
                                break;
                        }
                        break;
                    case popSecondNumber:
                        switch (position) {
                            case 0://  节点检测
                                JumpAppPageUtil.jumpSetServiceNodePage(mContext);
                                break;
                            case 1://扫一扫
                                mContext.startActivityForResult(new Intent(mContext, ScanActivity.class), MainActivity.REQUEST_CODE_SCAN);
                                break;
                            default:
                                break;
                        }
                        break;
                    case popThirdNumber:
                        switch (position) {
                            case 0://  节点检测
                                JumpAppPageUtil.jumpSetServiceNodePage(mContext);
                                break;
                            case 1://扫一扫
                                mContext.startActivityForResult(new Intent(mContext, ScanActivity.class), MainActivity.REQUEST_CODE_SCAN);
                                break;
                            case 3://牛顿计划
                                //牛顿计划
                                JumpAppPageUtil.jumpNativeWebView(mContext, ServiceConstants.GetNewdonPlanUrl().getHost_url(), "", CommonConstants.H5_TYPE_SIMPLE);
                                break;
                            case 4://交易费用
                                JumpAppPageUtil.jumpNativeWebView(mContext, ServiceConstants.GetTransferFeeUrl().getHost_url(), "", CommonConstants.H5_TYPE_SIMPLE);
                                break;
                            case 5://资金出入
                                //TODO 2018:04:06
                                JumpAppPageUtil.jumpNativeWebView(mContext, ServiceConstants.GetMoneyGoAndOutUrl().getHost_url(), mContext.getString(R.string.money_go_and_out), CommonConstants.H5_TYPE_SIMPLE);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 显示弹窗列表界面
     */
    public void show(View view) {
        // 获得点击屏幕的位置坐标
        view.getLocationOnScreen(mLocation);

        // 设置矩形的大小
        mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(),
                mLocation[1] + view.getHeight());

        // 判断是否需要添加或更新列表子类项
        if (mIsDirty) {
            populateActions();
        }

//        mListView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        setWidth(mListView.getMeasuredWidth());

        // 显示弹窗的位置
        showAtLocation(view, popupGravity, mScreenWidth - LIST_PADDING
                - (getWidth() / 2), mRect.bottom);
    }

    /**
     * 设置弹窗列表子项
     */
    private void populateActions() {
        mIsDirty = false;

        // 设置列表的适配器
        mListView.setAdapter(new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.layout_item_pop, parent, false);
                }
                TextView textView = ViewHolder.get(convertView, R.id.txt_title);
                // 设置文本居中
                textView.setGravity(Gravity.CENTER_VERTICAL);
                // // 设置文本域的范围
                // textView.setPadding(0, 10, 0, 10);
                // 设置文本在一行内显示（不换行）
                textView.setSingleLine(true);

                ActionItem item = mActionItems.get(position);

                // 设置文本文字
                textView.setText(item.mTitle);
                if (item.mDrawable != null) {
                    // 设置文字与图标的间隔
                    textView.setCompoundDrawablePadding(20);
                    item.mDrawable.setBounds(0, 0, UiUtils.dip2px(mContext, 26),
                            UiUtils.dip2px(mContext, 26));//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
                    // 设置在文字的左边放一个图标
                    textView.setCompoundDrawables(
                            item.mDrawable, null, null, null);
                }
                return convertView;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return mActionItems.get(position);
            }

            @Override
            public int getCount() {
                return mActionItems.size();
            }
        });

    }

    /**
     * 添加子类项
     */
    public void addAction(ActionItem action) {
        if (action != null) {
            mActionItems.add(action);
            mIsDirty = true;
        }
    }

    /**
     * 清除子类项
     */
    public void cleanAction() {
        if (!mActionItems.isEmpty()) {
            mActionItems.clear();
            mIsDirty = true;
        }
    }

    /**
     * 根据位置得到子类项
     */
    public ActionItem getAction(int position) {
        if (position < 0 || position > mActionItems.size())
            return null;
        return mActionItems.get(position);
    }

    /**
     * 设置监听事件
     */
    public void setItemOnClickListener(
            OnItemOnClickListener onItemOnClickListener) {
        this.mItemOnClickListener = onItemOnClickListener;
    }

    /**
     * @author yangyu 功能描述：弹窗子类项按钮监听事件
     */
    public static interface OnItemOnClickListener {
        public void onItemClick(ActionItem item, int position, int whatFrag);
    }
}