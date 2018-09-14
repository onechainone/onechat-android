package onemessageui.dialog;


import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.community.bean.WeiboSelectType;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onechat.oneandroid.onewallet.util.UiUtils;
import onemessageui.dialog.SelectMenu.SelectWeiboTypeAdapter;

public class PopupWindowUtil {
    public static int MENU_SHOW_TYPE_LEFT = 0;
    public static int MENU_SHOW_TYPE_RIGHT = 1;

    /**
     * 赞赏对话框
     *
     * @return
     */
    public static void showShangWindow(final Activity mActivity, final Context mContext, View view, final DialogUtil.ConfirmCallBackInf callBack) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mActivity).inflate(
                R.layout.popup_show_shang, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, UiUtils.dip2px(mContext, 380), true);
        // 设置照相按钮的点击事件

        //余额
        TextView mBalanceTv = (TextView) contentView.findViewById(R.id.tv_money);
        mBalanceTv.setText(mContext.getString(R.string.balance));

        //去充值
        TextView mGoChongzhiTv = (TextView) contentView.findViewById(R.id.tv_chongzhi);
        mGoChongzhiTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        final EditText mInputMoneyEt = (EditText) contentView.findViewById(R.id.et_input_money);

        final LinearLayout mInputMoneyLl = (LinearLayout) contentView.findViewById(R.id.ll_input_money);
        GridView mShangMoneyGv = (GridView) contentView.findViewById(R.id.gv_note);

//        final ShangMoneyAdapter mAdapter = new ShangMoneyAdapter(mContext);
//
//        //mShangMoneyGv.setLayoutAnimation(UIUtils.getAnimationController());
//        mShangMoneyGv.setAdapter(mAdapter);
//        mShangMoneyGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mAdapter.setSelect(position);
//                mInputMoneyLl.setVisibility(View.GONE);
//
//            }
//        });


        TextView mOtherMoneyTv = (TextView) contentView.findViewById(R.id.tv_other_money);
        mOtherMoneyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mAdapter.clearSelect();
                mInputMoneyLl.setVisibility(View.VISIBLE);
            }
        });

        TextView mSubmitTv = (TextView) contentView.findViewById(R.id.tv_submit_shang);
        mSubmitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.equalsNull(mInputMoneyEt.getText() + "")) {
//                    if (mAdapter.getSelectMoney() > -1) {
//                        popupWindow.dismiss();
//                        callBack.onConfirmClick(mAdapter.getSelectMoney() + "");
//                    } else
//                        ToastUtils.simpleToast("请填写金额");
                } else {
                    if (Double.parseDouble(mInputMoneyEt.getText() + "") > 0) {
                        popupWindow.dismiss();
                        callBack.onConfirmClick(mInputMoneyEt.getText() + "");
                    } else {
                        ToastUtils.simpleToast("输入金额必须大于0");
                    }
                }
            }
        });

// 设置关闭按钮的点击事件
        final ImageView mCloseNoteIv = (ImageView) contentView.findViewById(R.id.iv_close_note);
        mCloseNoteIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setTouchable(true);

        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.7f;
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//多加这一句，问题就解决了！这句的官方文档解释是：让窗口背景后面的任何东西变暗

        mActivity.getWindow().setAttributes(lp);
//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//
//            @Override
//            public void onDismiss() {
//                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
//                lp.alpha = 1f;
//                mActivity.getWindow().setAttributes(lp);
//            }
//        });
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        //popupWindow.setBackgroundDrawable(new BitmapDrawable(mActivity.getResources(), ZanUtils.blur(mActivity)));
        popupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(
                R.color.toumin));

        popupWindow.setAnimationStyle(R.style.AnimBottom);
        // 设置好参数之后再show
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);

    }

    /**
     * 赞赏对话框
     *
     * @return
     */
    public static void showSelectMenuPopup(final Activity mActivity, int selectPosition, List<WeiboSelectType> stringList, int showType, View view, final DialogUtil.ConfirmCallBackObject<Integer> callBack) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mActivity).inflate(
                R.layout.select_menu_popup, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                UiUtils.getWindowWidth(mActivity) / 2, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        ListView mListView = (ListView) contentView.findViewById(R.id.menu_list);

        final SelectWeiboTypeAdapter adapter = new SelectWeiboTypeAdapter(mActivity, stringList, selectPosition);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                popupWindow.dismiss();
                callBack.onConfirmClick(i);
            }
        });

        popupWindow.setTouchable(true);

        // 设置背景颜色变暗
//        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
//        lp.alpha = 1f;
//        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//多加这一句，问题就解决了！这句的官方文档解释是：让窗口背景后面的任何东西变暗

//        mActivity.getWindow().setAttributes(lp);
//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//
//            @Override
//            public void onDismiss() {
//                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
//                lp.alpha = 1f;
//                mActivity.getWindow().setAttributes(lp);
//            }
//        });
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        //popupWindow.setBackgroundDrawable(new BitmapDrawable(mActivity.getResources(), ZanUtils.blur(mActivity)));
        popupWindow.setBackgroundDrawable(mActivity.getResources().getDrawable(
                R.color.toumin));

//        popupWindow.setAnimationStyle(R.style.AnimTop);
        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);

    }
}
