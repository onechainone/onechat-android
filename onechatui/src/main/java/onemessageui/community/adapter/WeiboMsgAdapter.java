package onemessageui.community.adapter;

/**
 * Created by 何帅 on 2016/6/8.
 */

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.community.bean.WeiboMsgBean;
import oneapp.onechat.oneandroid.onewallet.modle.AssetInfo;
import oneapp.onechat.oneandroid.onewallet.util.AssetInfoUtils;
import onewalletui.util.ImageUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.TimeUtils;
import onewalletui.util.jump.JumpAppPageUtil;

public class WeiboMsgAdapter extends BaseAdapter {

    private Context context;
    private List<WeiboMsgBean> mWeiboMsgList;

    public WeiboMsgAdapter(Context context, List<WeiboMsgBean> mWeiboMsgList) {
        this.context = context;
        this.mWeiboMsgList = mWeiboMsgList;
    }

    @Override
    public int getCount() {
        return mWeiboMsgList.size();
    }

    @Override
    public Object getItem(int position) {
        return mWeiboMsgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_weibo_msg, null);
            holder = new ViewHolder();
//
            holder.mUserAvatarIv = (ImageView) convertView.findViewById(R.id.iv_user_avatar);
            holder.mMsgTimeTv = (TextView) convertView.findViewById(R.id.tv_msg_time);
            holder.mUserNameTv = (TextView) convertView.findViewById(R.id.tv_user_name);
            holder.mMessageTv = (TextView) convertView.findViewById(R.id.tv_message);
            holder.mZanIv = (ImageView) convertView.findViewById(R.id.iv_zan);
            holder.mWeiboIv = (ImageView) convertView.findViewById(R.id.iv_weibo_img);
            holder.mPlayIv = (ImageView) convertView.findViewById(R.id.iv_play);
            holder.mWeiboTv = (TextView) convertView.findViewById(R.id.tv_weibo);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final WeiboMsgBean mWeiboMsgBean = mWeiboMsgList.get(position);
        ImageUtils.displayAvatarNetImage(context, mWeiboMsgBean.getAvatar_url(), holder.mUserAvatarIv, mWeiboMsgBean.getSex());
        holder.mUserAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mWeiboMsgBean.getAccount_name().equals(OneAccountHelper.getMeAccountName()))
                    JumpAppPageUtil.jumpOtherUserInfoPage(context, mWeiboMsgBean.getAccount_id());
            }
        });
        switch (mWeiboMsgBean.getType()) {
            //消息类型
            case WeiboMsgBean.TYPE_SHANG:
                String mPayValue = mWeiboMsgBean.getReward_amount();
                String mCoinName = mWeiboMsgBean.getAsset_code();
                AssetInfo assetInfo = AssetInfoUtils.getAssetInfoBySymbol(mWeiboMsgBean.getAsset_code());
                if (assetInfo != null) {
                    mCoinName = assetInfo.getShort_name();
                    mPayValue = OneAccountHelper.powerInStringSubstring(assetInfo.getPrecision() + "", mPayValue, true);
                }
                String value = mPayValue + " " + mCoinName;
                String shangString = context.getString(R.string.zanshang) + value;

                SpannableStringBuilder builder = new SpannableStringBuilder(shangString);

                //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                ForegroundColorSpan yuanSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.red_packet_color));
                builder.setSpan(yuanSpan, 2, 2 + value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.mMessageTv.setText(builder);
                holder.mMessageTv.setVisibility(View.VISIBLE);
                holder.mZanIv.setVisibility(View.GONE);
                break;
            case WeiboMsgBean.TYPE_ZAN:
            case WeiboMsgBean.TYPE_COMMENT_ZAN:
                holder.mMessageTv.setVisibility(View.GONE);
                holder.mZanIv.setVisibility(View.VISIBLE);
                if (!StringUtils.equalsNull(mWeiboMsgBean.getIs_like())) {
                    switch (mWeiboMsgBean.getIs_like()) {
                        case CommonConstants.WEIBO_ZAN_TYPE_NO:
                            break;
                        case CommonConstants.WEIBO_ZAN_TYPE_LIKE:
                            holder.mZanIv.setImageResource(R.drawable.icon_go_up_ok);
                            break;
                        case CommonConstants.WEIBO_ZAN_TYPE_DISLIKE:
                            holder.mZanIv.setImageResource(R.drawable.icon_go_down_ok);
                            break;
                    }
                }
                break;
            case WeiboMsgBean.TYPE_REPLY:
                holder.mMessageTv.setVisibility(View.VISIBLE);
                holder.mZanIv.setVisibility(View.GONE);
                holder.mMessageTv.setText(mWeiboMsgBean.getContent());
                break;
            default:
                holder.mMessageTv.setVisibility(View.GONE);
                holder.mZanIv.setVisibility(View.GONE);
                break;
        }

        switch (mWeiboMsgBean.getWeibo_type()) {
            //微博类型
            case CommonConstants.WEIBO_TYPE_VIDEO:
                holder.mWeiboIv.setVisibility(View.VISIBLE);
                holder.mPlayIv.setVisibility(View.VISIBLE);
                holder.mWeiboTv.setVisibility(View.GONE);

                ImageUtils.displaySimpleNetImage(context, mWeiboMsgBean.getVideo_jietu_url(), holder.mWeiboIv);

                break;
            case CommonConstants.WEIBO_TYPE_IMAGE:
                holder.mWeiboIv.setVisibility(View.VISIBLE);
                holder.mPlayIv.setVisibility(View.GONE);
                holder.mWeiboTv.setVisibility(View.GONE);

                if (!StringUtils.equalsNull(mWeiboMsgBean.getPic_url()))
                    ImageUtils.displaySimpleNetImage(context, mWeiboMsgBean.getPic_url(), holder.mWeiboIv);

                break;
            case CommonConstants.WEIBO_TYPE_FEED:
                holder.mWeiboIv.setVisibility(View.GONE);
                holder.mPlayIv.setVisibility(View.GONE);
                holder.mWeiboTv.setVisibility(View.VISIBLE);
                if (!StringUtils.equalsNull(mWeiboMsgBean.getWeibo_content()))
                    holder.mWeiboTv.setText(mWeiboMsgBean.getWeibo_content());
                break;
            default:
                holder.mWeiboIv.setVisibility(View.GONE);
                holder.mPlayIv.setVisibility(View.GONE);
                if (!StringUtils.equalsNull(mWeiboMsgBean.getWeibo_content())) {
                    holder.mWeiboTv.setVisibility(View.VISIBLE);
                    holder.mWeiboTv.setText(mWeiboMsgBean.getWeibo_content());
                } else
                    holder.mWeiboTv.setVisibility(View.GONE);
                break;
        }

        holder.mMsgTimeTv.setText(TimeUtils.toSecondTimeString(mWeiboMsgBean.getCreate_time()));
        holder.mUserNameTv.setText(mWeiboMsgBean.getNickname());


        return convertView;
    }

    static class ViewHolder {
        /**
         * 回复内容
         **/
        private TextView mMessageTv;
        /**
         * 名字
         **/
        private TextView mUserNameTv;
        /**
         * 回复时间
         **/
        private TextView mMsgTimeTv;
        /**
         * 头像
         **/
        private ImageView mUserAvatarIv;
        /**
         * 微博图片
         **/
        private ImageView mWeiboIv;
        /**
         * 播放按钮
         **/
        private ImageView mPlayIv;
        /**
         * 微博文字
         **/
        private TextView mWeiboTv;

        //点赞图标
        private ImageView mZanIv;

    }

}