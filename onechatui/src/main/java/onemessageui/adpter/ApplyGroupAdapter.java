package onemessageui.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import oneapp.onechat.oneandroid.chatsdk.OneOpenHelper;
import oneapp.onechat.oneandroid.onemessage.bean.GroupApplyResult;
import oneapp.onechat.oneandroid.onewallet.modle.MapResult;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onechat.oneandroid.onewallet.util.UserInfoUtils;
import onemessageui.utils.CommonUtils;
import onewalletui.util.ImageUtils;
import sdk.android.onechatui.R;

/**
 * Created by zengyuxin on 2018/6/2.
 */

public class ApplyGroupAdapter extends BaseAdapter {

    private Context context;
    private List<GroupApplyResult> list;
    private LayoutInflater mInflater;
    private HashMap<String, Boolean> checkedMap = new HashMap<>();

    public ApplyGroupAdapter(Context context, List<GroupApplyResult> list) {
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holderGroup;
        if (convertView == null || convertView.getTag() == null) {
            holderGroup = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_apply, parent, false);
            holderGroup.userAvatar = (ImageView) convertView.findViewById(R.id.iv_user_avatar);
            holderGroup.userName = (TextView) convertView.findViewById(R.id.tv_user_name);
            holderGroup.userId = (TextView) convertView.findViewById(R.id.tv_user_id);
            holderGroup.agreeBt = (TextView) convertView.findViewById(R.id.tv_agree);
            holderGroup.refuseBt = (TextView) convertView.findViewById(R.id.tv_refuse);
            holderGroup.alreadyTv = (TextView) convertView.findViewById(R.id.tv_already);
            convertView.setTag(holderGroup);
        } else {
            holderGroup = (ViewHolder) convertView.getTag();
        }

        GroupApplyResult groupApplyResult = list.get(position);
        final String groupUid = groupApplyResult.getGroup_uid();
        final String accountId = groupApplyResult.getAccount_id();
        holderGroup.userName.setText(groupApplyResult.getNickname() + context.getString(R.string.invite_you_join_group));
        holderGroup.userId.setText(groupApplyResult.getGroup_name());
        ImageUtils.displayCircleNetImage(context, list.get(position).getAvatar_url(), holderGroup.userAvatar, R.drawable.default_group);

        holderGroup.agreeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneGroupHelper.agreeToJoinGroup(groupUid, new RequestSuccessListener<MapResult>() {
                    @Override
                    public void onResponse(MapResult result) {
                        if (OneOpenHelper.checkResultCode(result)) {
                            if (!checkedMap.containsKey(groupUid)) {
                                checkedMap.put(groupUid, true);
                            }
                            holderGroup.alreadyTv.setText(context.getString(R.string.already_agree));
                            refresh();
                            CommonUtils.sendMessageToGroup(context, groupUid, String.format(context.getResources().getString(R.string.join_group_tip), UserInfoUtils.getUserInfo().getNickname()));
                        } else {
                            ToastUtils.simpleToast(R.string.erro);
                        }
                    }
                });
            }
        });

        holderGroup.refuseBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneGroupHelper.disAgreeToJoinGroup(groupUid, new RequestSuccessListener<MapResult>() {
                    @Override
                    public void onResponse(MapResult result) {
                        if (OneOpenHelper.checkResultCode(result)) {
                            if (!checkedMap.containsKey(groupUid)) {
                                checkedMap.put(groupUid, false);
                            }
                            holderGroup.alreadyTv.setText(context.getString(R.string.already_refuse));
                            refresh();
                        } else {
                            ToastUtils.simpleToast(R.string.erro);
                        }
                    }
                });
            }
        });

        if (checkedMap.containsKey(groupUid)) {
            holderGroup.alreadyTv.setVisibility(View.VISIBLE);
            holderGroup.agreeBt.setVisibility(View.GONE);
            holderGroup.refuseBt.setVisibility(View.GONE);
            if (checkedMap.get(groupUid)) {
                holderGroup.alreadyTv.setText(context.getString(R.string.already_agree));
            } else {
                holderGroup.alreadyTv.setText(context.getString(R.string.already_refuse));
            }
        } else {
            holderGroup.alreadyTv.setVisibility(View.GONE);
            holderGroup.agreeBt.setVisibility(View.VISIBLE);
            holderGroup.refuseBt.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    class ViewHolder {
        ImageView userAvatar;
        TextView userName;
        TextView userId;
        TextView agreeBt;
        TextView refuseBt;
        TextView alreadyTv;
    }

    public void refresh() {
        notifyDataSetChanged();
    }
}
