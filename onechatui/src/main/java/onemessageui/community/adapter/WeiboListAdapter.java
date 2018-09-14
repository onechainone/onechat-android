package onemessageui.community.adapter;

/**
 * Created by 何帅 on 2016/6/8.
 */

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneCommunityHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.community.bean.WeiboBean;
import oneapp.onechat.oneandroid.onemessage.community.bean.WeiboCatchModel;
import oneapp.onechat.oneandroid.onemessage.community.bean.ZanResult;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.util.KeyBoardUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.TimeUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onemessageui.community.ZanUtils;
import onemessageui.dialog.DialogUtil;
import onemessageui.widght.GridViewForScrollView;
import onewalletui.util.ImageUtils;
import onewalletui.util.UiUtils;
import onewalletui.util.jump.JumpAppPageUtil;
import sdk.android.onechatui.R;


public class WeiboListAdapter extends BaseAdapter {
    public static final int WEIBO_EORD_NUMBER = 100;

    public Context getContext() {
        return context;
    }

    private Context context;
    private List<WeiboBean> mWeiboList;
    private String groupId;

    public WeiboListAdapter(Context context, List<WeiboBean> mWeiboList, String groupId) {
        this.context = context;
        this.mWeiboList = mWeiboList;
        this.groupId = groupId;
    }

    @Override
    public int getCount() {
        return mWeiboList.size();
    }

    @Override
    public Object getItem(int position) {
        return mWeiboList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        WeiboBean mWeiboBean = mWeiboList.get(position);
        String type = mWeiboBean.getType();

        if (type.equals(CommonConstants.WEIBO_TYPE_REPOST))
            return getZhuanfaWeiboView(position, convertView);
        else
            return getSimpleWeiboView(position, convertView);

    }

    public interface ZhuanfaListener {
        void onResponse(String souseWeiboId, String weiboId, int position);

    }

    private ZhuanfaListener mZhuanfaListener;

    public void setZhuanfaListener(ZhuanfaListener mZhuanfaListener) {
        this.mZhuanfaListener = mZhuanfaListener;
    }

    private View getSimpleWeiboView(final int position, View convertView) {

        final WeiboBean mWeiboBean = mWeiboList.get(position);
        if (mWeiboBean != null && !WeiboCatchModel.getDeleteWeiboMap().containsKey(mWeiboBean.getId())) {
            final ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_weibo, null);
                holder = new ViewHolder();
                holder.mZhuanfaContentTv = (TextView) convertView.findViewById(R.id.tv_weibo_zhuanfa_content);
                holder.mUserAvatarTv = (ImageView) convertView.findViewById(R.id.iv_user_avatar);
                holder.mImgGv = (GridViewForScrollView) convertView.findViewById(R.id.gv_image);
                holder.mWeiboTimeTv = (TextView) convertView.findViewById(R.id.tv_weibo_time);
                holder.mUserNameTv = (TextView) convertView.findViewById(R.id.tv_user_name);
                holder.mJianjieTv = (TextView) convertView.findViewById(R.id.tv_user_jianjie);
                holder.mWeiboContentTv = (TextView) convertView.findViewById(R.id.tv_weibo_content);
                holder.mReplyCountTv = (TextView) convertView.findViewById(R.id.tv_weibo_repost_count);
                holder.mZanCountTv = (TextView) convertView.findViewById(R.id.tv_weibo_zan_count);
                holder.mZanIv = (ImageView) convertView.findViewById(R.id.iv_zan);
                holder.mCaiCountTv = (TextView) convertView.findViewById(R.id.tv_weibo_cai_count);
                holder.mCaiIv = (ImageView) convertView.findViewById(R.id.iv_cai);
                holder.mShangCountTv = (TextView) convertView.findViewById(R.id.tv_weibo_shang_count);
                holder.mZhuanfaIv = (ImageView) convertView.findViewById(R.id.iv_zhuanfa);
                holder.mVideoFl = (FrameLayout) convertView.findViewById(R.id.fl_video);
                holder.mVideoThumbIv = (ImageView) convertView.findViewById(R.id.iv_video);
                holder.mMoreIv = (ImageView) convertView.findViewById(R.id.iv_more);
                holder.mIsGoodWeibo = (ImageView) convertView.findViewById(R.id.iv_good_weibo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ImageUtils.displayAvatarNetImage(context, mWeiboBean.getAvatar_url(), holder.mUserAvatarTv, mWeiboBean.getSex());
            holder.mUserAvatarTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!StringUtils.equalsNull(mWeiboBean.getAccount_name())) {
                        if (!mWeiboBean.getAccount_name().equals(OneAccountHelper.getMeAccountName()))
                            JumpAppPageUtil.jumpOtherUserInfoPage(context, mWeiboBean.getAccount_id());
                    }
                }
            });
//        holder.mZhuanfaIv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!StringUtils.equalsNull(mWeiboBean.getId()))
//                    mZhuanfaListener.onResponse(mWeiboBean.getId(), mWeiboBean.getId(), position);
//            }
//        });
            holder.mWeiboTimeTv.setText(TimeUtils.toSecondTimeString(mWeiboBean.getCreate_time()));
            holder.mUserNameTv.setText(mWeiboBean.getNickname());

            holder.mJianjieTv.setVisibility(View.VISIBLE);
            holder.mJianjieTv.setText(mWeiboBean.getIntroduce());

//            if (StringUtils.equals(mWeiboBean.getWeibo_jinghua(), CommonConstants.TRUE_VALUE)) {
//                holder.mIsGoodWeibo.setVisibility(View.VISIBLE);
//            } else {
//                holder.mIsGoodWeibo.setVisibility(View.GONE);
//            }


            String showText = "";
            if (!StringUtils.equalsNull(mWeiboBean.getContent())) {
                if (mWeiboBean.getContent().length() > WEIBO_EORD_NUMBER) {
                    showText = mWeiboBean.getContent().substring(0, WEIBO_EORD_NUMBER - 2) + "...";
                } else {
                    showText = mWeiboBean.getContent();
                }
            } else {
                showText = "";
            }

            if (WeiboCatchModel.getJinghuaWeiboMap().containsKey(mWeiboBean.getId()) || StringUtils.equals(mWeiboBean.getWeibo_jinghua(), CommonConstants.TRUE_VALUE)) {
                UiUtils.addGoodWeiboIcon(context, holder.mWeiboContentTv, showText);
            } else {
                holder.mWeiboContentTv.setText(showText);
            }


            holder.mReplyCountTv.setText(mWeiboBean.getComment_count());

            ZanResult zanResult = WeiboCatchModel.getZanMap().get(mWeiboBean.getId());
            if (zanResult == null) {
                zanResult = new ZanResult(mWeiboBean.getIs_like(), mWeiboBean.getLikes_count(), mWeiboBean.getDislikes_count());
            }
            final String is_like = zanResult.getIs_like();
            ZanUtils.bindZanResut(context, zanResult, holder.mZanCountTv, holder.mCaiCountTv, holder.mZanIv, holder.mCaiIv);

            holder.mZanIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UiUtils.changeZanBig(holder.mZanIv, new UiUtils.ConfirmCallBackInf() {
                        @Override
                        public void onConfirmClick(String content) {
                            String mZanType = ZanUtils.getZanRequestType(is_like, CommonConstants.WEIBO_ZAN_TYPE_LIKE);

                            OneCommunityHelper.likeOrDislikeArticle(mWeiboBean.getId(), CommonConstants.WEIBO_ZAN_TYPE_LIKE, mZanType, groupId, new RequestSuccessListener<ZanResult>() {
                                @Override
                                public void onResponse(ZanResult zanBeanResult) {
                                    if (zanBeanResult != null) {
                                        if (!StringUtils.equalsNull(zanBeanResult.getPay_value())) {
                                            ToastUtils.simpleToast(context.getString(R.string.to_pay_a_oneluck) + zanBeanResult.getPay_value());
                                        }
                                        WeiboCatchModel.getZanMap().put(mWeiboBean.getId(), zanBeanResult);
                                        //判断是否赞过
                                        ZanUtils.bindZanResut(context, zanBeanResult, holder.mZanCountTv, holder.mCaiCountTv, holder.mZanIv, holder.mCaiIv);

                                        notifyDataSetChanged();

                                    }
                                }
                            });
                        }
                    });
                }
            });
            holder.mCaiIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UiUtils.changeZanBig(holder.mCaiIv, new UiUtils.ConfirmCallBackInf() {
                        @Override
                        public void onConfirmClick(String content) {
                            String mZanType = ZanUtils.getZanRequestType(is_like, CommonConstants.WEIBO_ZAN_TYPE_DISLIKE);

                            OneCommunityHelper.likeOrDislikeArticle(mWeiboBean.getId(), CommonConstants.WEIBO_ZAN_TYPE_DISLIKE, mZanType, groupId, new RequestSuccessListener<ZanResult>() {
                                @Override
                                public void onResponse(ZanResult zanBeanResult) {
                                    if (zanBeanResult != null) {
                                        if (!StringUtils.equalsNull(zanBeanResult.getPay_value())) {
                                            ToastUtils.simpleToast(context.getString(R.string.to_pay_a_oneluck) + zanBeanResult.getPay_value());
                                        }
                                        WeiboCatchModel.getZanMap().put(mWeiboBean.getId(), zanBeanResult);
                                        //判断是否赞过
                                        ZanUtils.bindZanResut(context, zanBeanResult, holder.mZanCountTv, holder.mCaiCountTv, holder.mZanIv, holder.mCaiIv);

                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    });
                }
            });
            if (!StringUtils.equalsNull(mWeiboBean.getReward_count()))
                holder.mShangCountTv.setText(mWeiboBean.getReward_count());
            holder.mZhuanfaContentTv.setVisibility(View.GONE);
//        holder.mImgGv.setClickable(false);
//        holder.mImgGv.setPressed(false);
//        holder.mImgGv.setEnabled(false);
            if (mWeiboBean.getImg_list() != null) {
                holder.mImgGv.setVisibility(View.VISIBLE);
                WeiboImgAdapter mImgAdapter = new WeiboImgAdapter(context, mWeiboBean.getImg_list());
                holder.mImgGv.setAdapter(mImgAdapter);
            } else {
                holder.mImgGv.setVisibility(View.GONE);
            }
            holder.mImgGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        String user_is_pay = WeiboCatchModel.getUserIsPayMap().get(mWeiboBean.getId());
                        if (user_is_pay == null) {
                            user_is_pay = mWeiboBean.getUser_is_pay();
                        }

                        if (StringUtils.equals(mWeiboBean.getIs_pay(), CommonConstants.NO_VALUE) || StringUtils.equals(user_is_pay, CommonConstants.TRUE_VALUE)) {
                            JumpAppPageUtil.jumpImagePage(context, "", position, mWeiboBean.getImg_list_max());
                        } else {
                            JumpAppPageUtil.jumpPayWeiboPage((Activity) context, mWeiboBean.getId(), mWeiboBean.getAsset_code(), mWeiboBean.getReward_price(), groupId);
                        }
                    } catch (Exception e) {

                    }
                }
            });

            //此微博为视频
            if (mWeiboList.get(position).getType().equals(CommonConstants.WEIBO_TYPE_VIDEO)) {
                holder.mVideoFl.setVisibility(View.VISIBLE);
                holder.mVideoFl.setBackgroundResource(R.color.base_bg_color_level1);
                ImageUtils.displaySimpleNetImage(context, mWeiboBean.getVideo_jietu_url(), holder.mVideoThumbIv);
                holder.mImgGv.setVisibility(View.GONE);
                //holder.mVideoThumbIv
            } else {
                holder.mVideoFl.setVisibility(View.GONE);
            }

            holder.mMoreIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogUtil.weiboMoreDialog(context, mWeiboBean.getId(), mWeiboBean.getId(), mWeiboBean, false, groupId, new DialogUtil.ConfirmCallBackInf() {
                        @Override
                        public void onConfirmClick(String callBackType) {
                            refresh();
                            KeyBoardUtils.TimerHideKeyboard(holder.mMoreIv);
                        }
                    });
                }
            });
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_empty, null);
            convertView.setVisibility(View.GONE);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(0, 0); //设置item的weidth和height都为0
            //将设置好的布局属性应用到ListView/GridView等的Item上;
            convertView.setLayoutParams(param);
        }
        return convertView;
    }

    private View getZhuanfaWeiboView(final int position, View convertView) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_weibo2, null);
            holder = new ViewHolder();
            holder.mUserAvatarTv = (ImageView) convertView.findViewById(R.id.iv_user_avatar);
            holder.mZhuanfaContentTv = (TextView) convertView.findViewById(R.id.tv_weibo_zhuanfa_content);
            holder.mImgGv = (GridViewForScrollView) convertView.findViewById(R.id.gv_image);
            holder.mWeiboTimeTv = (TextView) convertView.findViewById(R.id.tv_weibo_time);
            holder.mUserNameTv = (TextView) convertView.findViewById(R.id.tv_user_name);
            holder.mJianjieTv = (TextView) convertView.findViewById(R.id.tv_user_jianjie);
            holder.mWeiboContentTv = (TextView) convertView.findViewById(R.id.tv_weibo_content);
            holder.mReplyCountTv = (TextView) convertView.findViewById(R.id.tv_weibo_repost_count);
            holder.mZanCountTv = (TextView) convertView.findViewById(R.id.tv_weibo_zan_count);
            holder.mZanIv = (ImageView) convertView.findViewById(R.id.iv_zan);
            holder.mCaiCountTv = (TextView) convertView.findViewById(R.id.tv_weibo_cai_count);
            holder.mCaiIv = (ImageView) convertView.findViewById(R.id.iv_cai);
            holder.mShangCountTv = (TextView) convertView.findViewById(R.id.tv_weibo_shang_count);
            holder.mZhuanfaIv = (ImageView) convertView.findViewById(R.id.iv_zhuanfa);
            holder.mVideoFl = (FrameLayout) convertView.findViewById(R.id.fl_video);
            holder.mVideoThumbIv = (ImageView) convertView.findViewById(R.id.iv_video);
            holder.mMoreIv = (ImageView) convertView.findViewById(R.id.iv_more);
            holder.mIsGoodWeibo = (ImageView) convertView.findViewById(R.id.iv_good_weibo);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final WeiboBean mWeiboBean = mWeiboList.get(position);

        ImageUtils.displayAvatarNetImage(context, mWeiboBean.getAvatar_url(), holder.mUserAvatarTv, mWeiboBean.getSex());
        holder.mUserAvatarTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mWeiboBean.getAccount_name().equals(OneAccountHelper.getMeAccountName()))
                    JumpAppPageUtil.jumpOtherUserInfoPage(context, mWeiboBean.getAccount_id());
            }
        });
//        holder.mZhuanfaIv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mWeiboBean.getYuan_weibo() != null && !StringUtils.equalsNull(mWeiboBean.getYuan_weibo().getId()) && !StringUtils.equalsNull(mWeiboBean.getId()))
//                    mZhuanfaListener.onResponse(mWeiboBean.getYuan_weibo().getId(), mWeiboBean.getId(), position);
//            }
//        });
        holder.mWeiboTimeTv.setText(TimeUtils.toSecondTimeString(mWeiboBean.getCreate_time()));
        holder.mUserNameTv.setText(mWeiboBean.getNickname());

        holder.mJianjieTv.setVisibility(View.VISIBLE);
        holder.mJianjieTv.setText(mWeiboBean.getIntroduce());

//        if (StringUtils.equals(mWeiboBean.getWeibo_jinghua(), CommonConstants.TRUE_VALUE)) {
//            holder.mIsGoodWeibo.setVisibility(View.VISIBLE);
//        } else {
//            holder.mIsGoodWeibo.setVisibility(View.GONE);
//        }

        String showText = "";
        if (!StringUtils.equalsNull(mWeiboBean.getContent())) {
            if (mWeiboBean.getContent().length() > WEIBO_EORD_NUMBER) {
                showText = mWeiboBean.getContent().substring(0, WEIBO_EORD_NUMBER - 2) + "...";
            } else {
                showText = mWeiboBean.getContent();
            }
        } else {
            showText = "";
        }
        if (WeiboCatchModel.getJinghuaWeiboMap().containsKey(mWeiboBean.getId()) || StringUtils.equals(mWeiboBean.getWeibo_jinghua(), CommonConstants.TRUE_VALUE)) {
            UiUtils.addGoodWeiboIcon(context, holder.mWeiboContentTv, showText);
        } else {
            holder.mWeiboContentTv.setText(showText);
        }

        if (!StringUtils.equalsNull(mWeiboBean.getYuan_weibo().getId()) && !WeiboCatchModel.getDeleteWeiboMap().containsKey(mWeiboBean.getYuan_weibo().getId())) {
            holder.mReplyCountTv.setText(mWeiboBean.getYuan_weibo().getComment_count());

            ZanResult zanResult = WeiboCatchModel.getZanMap().get(mWeiboBean.getId());
            if (zanResult == null) {
                zanResult = new ZanResult(mWeiboBean.getIs_like(), mWeiboBean.getLikes_count(), mWeiboBean.getDislikes_count());
            }
            final String is_like = zanResult.getIs_like();

            ZanUtils.bindZanResut(context, zanResult, holder.mZanCountTv, holder.mCaiCountTv, holder.mZanIv, holder.mCaiIv);

            holder.mZanIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UiUtils.changeZanBig(holder.mZanIv, new UiUtils.ConfirmCallBackInf() {
                        @Override
                        public void onConfirmClick(String content) {
                            String mZanType = ZanUtils.getZanRequestType(is_like, CommonConstants.WEIBO_ZAN_TYPE_LIKE);

                            OneCommunityHelper.likeOrDislikeArticle(mWeiboBean.getId(), CommonConstants.WEIBO_ZAN_TYPE_LIKE, mZanType, groupId, new RequestSuccessListener<ZanResult>() {
                                @Override
                                public void onResponse(ZanResult zanBeanResult) {
                                    if (zanBeanResult != null) {
                                        if (!StringUtils.equalsNull(zanBeanResult.getPay_value())) {
                                            ToastUtils.simpleToast(context.getString(R.string.to_pay_a_oneluck) + zanBeanResult.getPay_value());
                                        }
                                        WeiboCatchModel.getZanMap().put(mWeiboBean.getId(), zanBeanResult);
                                        //判断是否赞过
                                        ZanUtils.bindZanResut(context, zanBeanResult, holder.mZanCountTv, holder.mCaiCountTv, holder.mZanIv, holder.mCaiIv);

                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    });
                }
            });
            holder.mCaiIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UiUtils.changeZanBig(holder.mCaiIv, new UiUtils.ConfirmCallBackInf() {
                        @Override
                        public void onConfirmClick(String content) {
                            String mZanType = ZanUtils.getZanRequestType(is_like, CommonConstants.WEIBO_ZAN_TYPE_DISLIKE);

                            OneCommunityHelper.likeOrDislikeArticle(mWeiboBean.getId(), CommonConstants.WEIBO_ZAN_TYPE_DISLIKE, mZanType, groupId, new RequestSuccessListener<ZanResult>() {
                                @Override
                                public void onResponse(ZanResult zanBeanResult) {
                                    if (zanBeanResult != null) {
                                        if (!StringUtils.equalsNull(zanBeanResult.getPay_value())) {
                                            ToastUtils.simpleToast(context.getString(R.string.to_pay_a_oneluck) + zanBeanResult.getPay_value());
                                        }
                                        WeiboCatchModel.getZanMap().put(mWeiboBean.getId(), zanBeanResult);
                                        //判断是否赞过
                                        ZanUtils.bindZanResut(context, zanBeanResult, holder.mZanCountTv, holder.mCaiCountTv, holder.mZanIv, holder.mCaiIv);

                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    });
                }
            });

            if (!StringUtils.equalsNull(mWeiboBean.getYuan_weibo().getReward_count()))
                holder.mShangCountTv.setText(mWeiboBean.getYuan_weibo().getReward_count());
            holder.mZhuanfaContentTv.setVisibility(View.VISIBLE);

            String sourseStr = "【" + mWeiboBean.getYuan_weibo().getNickname() + "】:" + mWeiboBean.getYuan_weibo().getContent();
            if (sourseStr.length() > WEIBO_EORD_NUMBER)
                sourseStr = sourseStr.substring(0, WEIBO_EORD_NUMBER - 2) + "...";
            SpannableStringBuilder builder = new SpannableStringBuilder(sourseStr);

            //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
            ForegroundColorSpan nameSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.base_color));
            if (!StringUtils.equalsNull(mWeiboBean.getYuan_weibo().getNickname()))
                builder.setSpan(nameSpan, 0, mWeiboBean.getYuan_weibo().getNickname().length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.mZhuanfaContentTv.setText(builder);
            if (mWeiboBean.getYuan_weibo().getImg_list() != null) {
                holder.mImgGv.setVisibility(View.VISIBLE);
                WeiboImgAdapter mImgAdapter = new WeiboImgAdapter(context, mWeiboBean.getYuan_weibo().getImg_list());
                holder.mImgGv.setAdapter(mImgAdapter);
            } else {
                holder.mImgGv.setVisibility(View.GONE);
            }
//        holder.mImgGv.setClickable(false);
//        holder.mImgGv.setPressed(false);
//        holder.mImgGv.setEnabled(false);
            holder.mImgGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    JumpAppPageUtil.jumpImagePage(context, "", position, mWeiboBean.getYuan_weibo().getImg_list_max());

                }
            });

            //原微博为视频
            if (!StringUtils.equalsNull(mWeiboList.get(position).getYuan_weibo().getType()) && mWeiboList.get(position).getYuan_weibo().getType().equals(CommonConstants.WEIBO_TYPE_VIDEO)) {
                holder.mVideoFl.setVisibility(View.VISIBLE);
                holder.mVideoFl.setBackgroundResource(R.color.base_background_bg);
                ImageUtils.displaySimpleNetImage(context, mWeiboBean.getYuan_weibo().getVideo_jietu_url(), holder.mVideoThumbIv);
                //holder.mVideoThumbIv
            } else {
                holder.mVideoFl.setVisibility(View.GONE);
            }

        } else {
            holder.mImgGv.setVisibility(View.GONE);
            holder.mVideoFl.setVisibility(View.GONE);
            holder.mZhuanfaContentTv.setVisibility(View.VISIBLE);
            holder.mZhuanfaContentTv.setText(context.getString(R.string.null_shuoshuo));
            holder.mZhuanfaContentTv.setTextColor(ContextCompat.getColor(context, R.color.black_second));
        }

        holder.mMoreIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogUtil.weiboMoreDialog(context, mWeiboBean.getId(), mWeiboBean.getYuan_weibo().getId(), mWeiboBean, false, groupId, new DialogUtil.ConfirmCallBackInf() {
                    @Override
                    public void onConfirmClick(String content) {
                        notifyDataSetChanged();
                        KeyBoardUtils.TimerHideKeyboard(holder.mMoreIv);
                    }
                });
            }
        });

        return convertView;
    }

    static class ViewHolder {
        /**
         * 微博文字
         **/
        private TextView mWeiboContentTv;
        /**
         * 名字
         **/
        private TextView mUserNameTv;

        private TextView mJianjieTv;
        /**
         * 微博时间
         **/
        private TextView mWeiboTimeTv;
        /**
         * 微博回复数量
         **/
        private TextView mReplyCountTv;
        /**
         * 微博点赞数量
         **/
        private TextView mZanCountTv;
        private ImageView mZanIv;
        /**
         * 微博点踩数量
         **/
        private TextView mCaiCountTv;
        private ImageView mCaiIv;
        /**
         * 微博打赏数量
         **/
        private TextView mShangCountTv;
        /**
         * 头像
         **/
        private ImageView mUserAvatarTv;
        /**
         * 图片GridView
         **/
        private GridViewForScrollView mImgGv;
        /**
         *
         */
        private TextView mZhuanfaContentTv;

        private ImageView mMoreIv;


        private ImageView mZhuanfaIv;
        private FrameLayout mVideoFl;
        private ImageView mVideoThumbIv;

        private ImageView mIsGoodWeibo;
    }

    public void refresh() {
//        if (WeiboCatchModel.getDeleteWeiboMap().size() > 0) {
//            for (int i = 0; i < mWeiboList.size(); i++) {
//                WeiboBean weiboBean = mWeiboList.get(i);
//                if (WeiboCatchModel.getDeleteWeiboMap().containsKey(weiboBean.getId()) && weiboBean.getType().equals(CommonConstants.WEIBO_TYPE_REPOST)) {
//                    mWeiboList.remove(i);
//                }
//            }
//        }
        notifyDataSetChanged();
    }

}