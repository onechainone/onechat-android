package onemessageui.community;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.community.bean.ZanResult;

/**
 * Created by 何帅 on 2018/5/14.
 */

public class ZanUtils {
    /**
     * @return
     */
    public static void bindZanResut(Context context, String is_like, String like_num, String dislike_num, TextView mZanNumTv, TextView mCaiNumTv, ImageView mZanIv, ImageView mCaiIv) {
        if (is_like == null) {
            return;
        }
        //判断是否赞过
        switch (is_like) {
            case CommonConstants.WEIBO_ZAN_TYPE_NO:
                mZanIv.setImageResource(R.drawable.icon_go_up_gray);
                mZanNumTv.setTextColor(context.getResources().getColor(R.color.base_text_gray));
                mCaiIv.setImageResource(R.drawable.icon_go_down_gray);
                mCaiNumTv.setTextColor(context.getResources().getColor(R.color.base_text_gray));
                break;
            case CommonConstants.WEIBO_ZAN_TYPE_LIKE:
                mZanIv.setImageResource(R.drawable.icon_go_up_ok);
                mZanNumTv.setTextColor(context.getResources().getColor(R.color.zan_num_red));
                mCaiIv.setImageResource(R.drawable.icon_go_down_gray);
                mCaiNumTv.setTextColor(context.getResources().getColor(R.color.base_text_gray));
                break;
            case CommonConstants.WEIBO_ZAN_TYPE_DISLIKE:
                mZanIv.setImageResource(R.drawable.icon_go_up_gray);
                mZanNumTv.setTextColor(context.getResources().getColor(R.color.base_text_gray));
                mCaiIv.setImageResource(R.drawable.icon_go_down_ok);
                mCaiNumTv.setTextColor(context.getResources().getColor(R.color.zan_num_red));
                break;
        }

        mZanNumTv.setText(like_num);
        mCaiNumTv.setText(dislike_num);

        if (mZanNumTv.getText().equals("0"))
            mZanNumTv.setText("");
        if (mCaiNumTv.getText().equals("0"))
            mCaiNumTv.setText("");

    }

    /**
     * @return
     */
    public static void bindZanResut(Context context, ZanResult zanResult, TextView mZanNumTv, TextView mCaiNumTv, ImageView mZanIv, ImageView mCaiIv) {
        if (zanResult == null) {
            return;
        }
        bindZanResut(context, zanResult.getIs_like(), zanResult.getLikes_count(), zanResult.getDislikes_count(), mZanNumTv, mCaiNumTv, mZanIv, mCaiIv);
    }

    /**
     * @return
     */
    public static String getZanRequestType(String is_like, String zan_type) {
        String zanRequestType = CommonConstants.WEIBO_CANCLE_ZAN;
        //判断是否赞过
        switch (is_like) {
            case CommonConstants.WEIBO_ZAN_TYPE_NO:
                zanRequestType = CommonConstants.WEIBO_ZAN;
                break;
            case CommonConstants.WEIBO_ZAN_TYPE_LIKE:
                if (zan_type.equals(CommonConstants.WEIBO_ZAN_TYPE_LIKE))
                    zanRequestType = CommonConstants.WEIBO_CANCLE_ZAN;
                else {
                    zanRequestType = CommonConstants.WEIBO_ZAN;
                }
                break;
            case CommonConstants.WEIBO_ZAN_TYPE_DISLIKE:
                if (zan_type.equals(CommonConstants.WEIBO_ZAN_TYPE_DISLIKE))
                    zanRequestType = CommonConstants.WEIBO_CANCLE_ZAN;
                else {
                    zanRequestType = CommonConstants.WEIBO_ZAN;
                }
                break;
        }
        return zanRequestType;
    }
}
