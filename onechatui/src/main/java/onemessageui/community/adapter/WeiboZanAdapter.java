package onemessageui.community.adapter;

/**
 * Created by 何帅 on 2016/6/8.
 */

import android.content.Context;
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
import oneapp.onechat.oneandroid.onemessage.community.bean.ZanshangBean;
import oneapp.onechat.oneandroid.onewallet.modle.AssetInfo;
import oneapp.onechat.oneandroid.onewallet.util.AssetInfoUtils;
import onewalletui.util.ImageUtils;
import oneapp.onechat.oneandroid.onewallet.util.TimeUtils;
import onewalletui.util.jump.JumpAppPageUtil;

public class WeiboZanAdapter extends BaseAdapter {

    private Context context;
    private List<ZanshangBean> mZhanshanglist;


    public WeiboZanAdapter(Context context, List<ZanshangBean> mZhanshanglist) {
        this.context = context;
        this.mZhanshanglist = mZhanshanglist;
    }

    @Override
    public int getCount() {
        return mZhanshanglist.size();
    }

    @Override
    public Object getItem(int position) {
        return mZhanshanglist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_weibo_zan, null);
            holder = new ViewHolder();
//
            holder.mUserAvatarIv = (ImageView) convertView.findViewById(R.id.iv_user_avatar);
            holder.mZanTimeTv = (TextView) convertView.findViewById(R.id.tv_zan_time);
            holder.mUserNameTv = (TextView) convertView.findViewById(R.id.tv_user_name);
            holder.mZanMoneyTv = (TextView) convertView.findViewById(R.id.tv_zan_money);
            holder.mLineView = convertView.findViewById(R.id.view_line);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final ZanshangBean mZanshangBean = mZhanshanglist.get(position);
        ImageUtils.displayAvatarNetImage(context, mZanshangBean.getAvatar_url(), holder.mUserAvatarIv, mZanshangBean.getSex());

        holder.mUserAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mZanshangBean.getAccount_name().equals(OneAccountHelper.getMeAccountName()))
                    JumpAppPageUtil.jumpOtherUserInfoPage(context, mZanshangBean.getAccount_id());
            }
        });

        holder.mZanTimeTv.setText(TimeUtils.toSecondTimeString(mZanshangBean.getCreate_time()));
        holder.mUserNameTv.setText(mZanshangBean.getNickname());

        String mPayValue = mZanshangBean.getReward_amount();
        String mCoinName = mZanshangBean.getAsset_code();
        AssetInfo assetInfo = AssetInfoUtils.getAssetInfoBySymbol(mZanshangBean.getAsset_code());
        if (assetInfo != null) {
            mCoinName = assetInfo.getShort_name();
            mPayValue = OneAccountHelper.powerInStringSubstring(assetInfo.getPrecision() + "", mPayValue, true);
        }
        holder.mZanMoneyTv.setText(mPayValue + " " + mCoinName);

        if (position == mZhanshanglist.size() - 1)
            holder.mLineView.setVisibility(View.GONE);
        else
            holder.mLineView.setVisibility(View.VISIBLE);

        return convertView;
    }

    static class ViewHolder {
        /**
         * 赞赏内容
         **/
        private TextView mZanMoneyTv;
        /**
         * 名字
         **/
        private TextView mUserNameTv;
        /**
         * 赞赏时间
         **/
        private TextView mZanTimeTv;
        /**
         * 头像
         **/
        private ImageView mUserAvatarIv;

        //分割线
        private View mLineView;
    }

}