package onemessageui.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import oneapp.onechat.oneandroid.graphenechain.models.UserGroupInfoItem;
import onemessageui.utils.ViewHolder;
import onewalletui.util.ImageUtils;
import sdk.android.onechatui.R;

//import oneapp.onemessage.bean.User;

public class GroupItemAdapter extends BaseAdapter {
    private Context mContext;
    private List<UserGroupInfoItem> list;

    @SuppressWarnings("unchecked")
    public GroupItemAdapter(Context mContext, List<UserGroupInfoItem> groupInfoItems) {
        this.mContext = mContext;
        this.list = groupInfoItems;
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public UserGroupInfoItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        final UserGroupInfoItem group = list.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.group_item, null);
        }

        if (group != null) {
            ImageView ivAvatar = ViewHolder.get(convertView,
                    R.id.iv_group_avatar);
            TextView tvName = ViewHolder.get(convertView,
                    R.id.tv_group_name);
            TextView tvMemberSize = ViewHolder.get(convertView,
                    R.id.tv_group_member_size);

            ImageUtils.displayCircleNetImage(mContext, group.getGroupAvatarUrl(), ivAvatar, R.drawable.default_group);
            tvName.setText(group.group_name);
            tvMemberSize.setText("(" + group.members_size + ")");

        }
        return convertView;
    }

    public void refresh(List<UserGroupInfoItem> groupInfoItems) {
        this.list = groupInfoItems;
    }
}
