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
import oneapp.onechat.oneandroid.chatsdk.OneCommunityHelper;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.community.bean.CommentBean;
import oneapp.onechat.oneandroid.onemessage.community.bean.ZanResult;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import onewalletui.util.ImageUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.TimeUtils;
import oneapp.onechat.oneandroid.onewallet.util.UiUtils;
import onewalletui.util.jump.JumpAppPageUtil;

public class WeiboCommentAdapter extends BaseAdapter {

    private Context context;
    private List<CommentBean> mCommentList;
    private String groupId = "";

    public WeiboCommentAdapter(Context context, List<CommentBean> mCommentList) {
        this.context = context;
        this.mCommentList = mCommentList;
    }

    public void setGroupId(String groupId) {

        this.groupId = groupId;
    }

    @Override
    public int getCount() {
        return mCommentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_weibo_reply, null);
            holder = new ViewHolder();

            holder.mUserAvatarIv = (ImageView) convertView.findViewById(R.id.iv_user_avatar);
            holder.mReplyTimeTv = (TextView) convertView.findViewById(R.id.tv_reply_time);
            holder.mUserNameTv = (TextView) convertView.findViewById(R.id.tv_user_name);
            holder.mReplyContentTv = (TextView) convertView.findViewById(R.id.tv_reply_content);
            holder.mLineView = convertView.findViewById(R.id.view_line);
            holder.mReplyIv = (ImageView) convertView.findViewById(R.id.iv_reply);
            holder.mYuanCommentTv = (TextView) convertView.findViewById(R.id.tv_yuan_comment_content);
            holder.mZanCountTv = (TextView) convertView.findViewById(R.id.tv_zan_num);
            holder.mZanIv = (ImageView) convertView.findViewById(R.id.iv_zan);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CommentBean mCommentBean = mCommentList.get(position);
        ImageUtils.displayAvatarNetImage(context, mCommentBean.getAvatar_url(), holder.mUserAvatarIv, mCommentBean.getSex());

        holder.mUserAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.equalsNull(mCommentBean.getAccount_name())) {
                    if (!mCommentBean.getAccount_name().equals(OneAccountHelper.getMeAccountName()))
                        JumpAppPageUtil.jumpOtherUserInfoPage(context, mCommentBean.getAccount_id());
                }
            }
        });

        holder.mReplyIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReplyListener.onResponse(mCommentBean);
            }
        });


        holder.mReplyTimeTv.setText(TimeUtils.toSecondTimeString(mCommentBean.getCreate_time()));
        holder.mUserNameTv.setText(mCommentBean.getNickname());
        holder.mReplyContentTv.setText(mCommentBean.getContent());

        if (mCommentBean.getYuan_comment() != null && !StringUtils.equalsNull(mCommentBean.getYuan_comment().getContent())) {
            holder.mYuanCommentTv.setVisibility(View.VISIBLE);
            if (!StringUtils.equalsNull(mCommentBean.getYuan_comment().getNickname())) {
                //拼接昵称和评论内容
                String sourseStr = mCommentBean.getYuan_comment().getNickname() + ":" + mCommentBean.getYuan_comment().getContent();
                SpannableStringBuilder builder = new SpannableStringBuilder(sourseStr);
                //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                ForegroundColorSpan nameSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.base_color));
                if (!StringUtils.equalsNull(mCommentBean.getYuan_comment().getNickname()))
                    builder.setSpan(nameSpan, 0, mCommentBean.getYuan_comment().getNickname().length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.mYuanCommentTv.setText(builder);
            } else {
                holder.mYuanCommentTv.setText(mCommentBean.getYuan_comment().getContent());
            }
        } else {
            holder.mYuanCommentTv.setVisibility(View.GONE);
        }

        if (position == mCommentList.size() - 1)
            holder.mLineView.setVisibility(View.GONE);
        else
            holder.mLineView.setVisibility(View.VISIBLE);

        //配置点赞
        if (!StringUtils.equalsNull(mCommentBean.getLikes_num())) {
            holder.mZanCountTv.setText(mCommentBean.getLikes_num() + "");
            if (holder.mZanCountTv.getText().equals("0"))
                holder.mZanCountTv.setText("");
        }
        if (!StringUtils.equalsNull(mCommentBean.getIs_like()) && mCommentBean.getIs_like().equals(CommonConstants.WEIBO_ZAN_TYPE_LIKE)) {

            holder.mZanIv.setImageResource(R.drawable.zan_ok_icon);
            holder.mZanCountTv.setTextColor(context.getResources().getColor(R.color.zan_num_red));
        } else {
            holder.mZanIv.setImageResource(R.drawable.zan_gray_icon);
            holder.mZanCountTv.setTextColor(context.getResources().getColor(R.color.base_text_gray));
        }
        holder.mZanIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.changeZanBig(holder.mZanIv, new UiUtils.ConfirmCallBackInf() {
                    @Override
                    public void onConfirmClick(String content) {
                        final String mZanType;
                        if (!StringUtils.equalsNull(mCommentBean.getIs_like()) && mCommentBean.getIs_like().equals(CommonConstants.WEIBO_ZAN_TYPE_NO)) {
                            mZanType = CommonConstants.WEIBO_ZAN;
                        } else {
                            mZanType = CommonConstants.WEIBO_CANCLE_ZAN;
                        }
                        OneCommunityHelper.likeComment(mCommentBean.getId(), mZanType, groupId, new RequestSuccessListener<ZanResult>() {
                            @Override
                            public void onResponse(ZanResult listBeanResult) {
                                //判断是否赞过
                                if (!StringUtils.equalsNull(listBeanResult.getIs_like()) && listBeanResult.getIs_like().equals(CommonConstants.WEIBO_ZAN_TYPE_NO)) {
                                    holder.mZanIv.setImageResource(R.drawable.zan_gray_icon);
                                    holder.mZanCountTv.setTextColor(context.getResources().getColor(R.color.base_text_gray));
                                    mCommentBean.setIs_like(CommonConstants.WEIBO_ZAN_TYPE_NO);
                                } else {
                                    mCommentBean.setIs_like(CommonConstants.WEIBO_ZAN_TYPE_LIKE);
                                    holder.mZanIv.setImageResource(R.drawable.zan_ok_icon);
                                    holder.mZanCountTv.setTextColor(context.getResources().getColor(R.color.zan_num_red));
                                }

                                mCommentBean.setLikes_num(listBeanResult.getLikes_count());
                                holder.mZanCountTv.setText(listBeanResult.getLikes_count());
                                if (holder.mZanCountTv.getText().equals("0"))
                                    holder.mZanCountTv.setText("");
                                //notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        });

        return convertView;
    }

    static class ViewHolder {
        /**
         * 回复内容
         **/
        private TextView mReplyContentTv;
        /**
         * 名字
         **/
        private TextView mUserNameTv;
        /**
         * 回复时间
         **/
        private TextView mReplyTimeTv;
        /**
         * 头像
         **/
        private ImageView mUserAvatarIv;

        //回复按钮
        private ImageView mReplyIv;

        //分割线
        private View mLineView;


        private TextView mYuanCommentTv;
        /**
         * 评论点赞数量
         **/
        private TextView mZanCountTv;
        private ImageView mZanIv;
    }

    //回复接口
    public interface ReplyListener {
        void onResponse(CommentBean commentBean);

    }

    private ReplyListener mReplyListener;

    public void setReplyListener(ReplyListener mReplyListener) {
        this.mReplyListener = mReplyListener;
    }

}