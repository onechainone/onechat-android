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

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneOpenHelper;
import oneapp.onechat.oneandroid.onemessage.bean.FriendApplyResult;
import oneapp.onechat.oneandroid.onewallet.modle.MapResult;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onemessageui.dialog.DialogUtil;
import onemessageui.utils.CommonUtils;
import onewalletui.ui.BaseActivity;
import onewalletui.util.ImageUtils;
import onewalletui.util.jump.JumpAppPageUtil;
import sdk.android.onechatui.R;

/**
 * Created by zengyuxin on 2018/6/2.
 */

public class ApplyFriendAdapter extends BaseAdapter {

    private Context context;
    private List<FriendApplyResult> list;
    private LayoutInflater mInflater;
    private HashMap<String, Boolean> checkedMap = new HashMap<>();

    public ApplyFriendAdapter(Context context, List<FriendApplyResult> list) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holderFriend;
        if (convertView == null || convertView.getTag() == null) {
            holderFriend = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_apply, parent, false);
            holderFriend.userAvatar = (ImageView) convertView.findViewById(R.id.iv_user_avatar);
            holderFriend.userName = (TextView) convertView.findViewById(R.id.tv_user_name);
            holderFriend.userId = (TextView) convertView.findViewById(R.id.tv_user_id);
            holderFriend.agreeBt = (TextView) convertView.findViewById(R.id.tv_agree);
            holderFriend.refuseBt = (TextView) convertView.findViewById(R.id.tv_refuse);
            holderFriend.alreadyTv = (TextView) convertView.findViewById(R.id.tv_already);
            convertView.setTag(holderFriend);
        } else {
            holderFriend = (ViewHolder) convertView.getTag();
        }

        final FriendApplyResult friendApplyResult = list.get(position);
        final String accountName = friendApplyResult.getTo_account_name();
        ImageUtils.displayAvatarNetImage(context, list.get(position).getAvatar_url(), holderFriend.userAvatar, null);

        holderFriend.userName.setText(friendApplyResult.getShowName() + context.getString(R.string.apply_add_friend));
        holderFriend.userId.setText(friendApplyResult.getRemark());
        holderFriend.agreeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneAccountHelper.approveAddFriendRequest(accountName, new RequestSuccessListener<MapResult>() {
                    @Override
                    public void onResponse(MapResult result) {
                        if (OneOpenHelper.checkResultCode(result)) {
                            if (!checkedMap.containsKey(accountName)) {
                                checkedMap.put(accountName, true);
                            }
                            holderFriend.alreadyTv.setText(context.getString(R.string.already_agree));
                            refresh();
                            CommonUtils.agreeAddFriend((BaseActivity) context, accountName, false);
                        } else {
                            ToastUtils.simpleToast(R.string.erro);
                        }
                    }
                });
            }
        });

        holderFriend.refuseBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneAccountHelper.rejectFriendRequest(accountName, new RequestSuccessListener<MapResult>() {
                    @Override
                    public void onResponse(MapResult result) {
                        if (OneOpenHelper.checkResultCode(result)) {
                            if (!checkedMap.containsKey(accountName)) {
                                checkedMap.put(accountName, false);
                            }
                            holderFriend.alreadyTv.setText(context.getString(R.string.already_refuse));
                            refresh();
                        } else {
                            ToastUtils.simpleToast(R.string.erro);
                        }
                    }
                });
            }
        });

        if (checkedMap.containsKey(accountName)) {
            holderFriend.alreadyTv.setVisibility(View.VISIBLE);
            holderFriend.agreeBt.setVisibility(View.GONE);
            holderFriend.refuseBt.setVisibility(View.GONE);
            if (checkedMap.get(accountName)) {
                holderFriend.alreadyTv.setText(context.getString(R.string.already_agree));
            } else {
                holderFriend.alreadyTv.setText(context.getString(R.string.already_refuse));
            }
        } else {
            holderFriend.alreadyTv.setVisibility(View.GONE);
            holderFriend.agreeBt.setVisibility(View.VISIBLE);
            holderFriend.refuseBt.setVisibility(View.VISIBLE);
        }
        holderFriend.userId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.tipDialog(context, friendApplyResult.getRemark(), true);
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpAppPageUtil.jumpOtherUserInfoPageByName(context, accountName);
            }
        });

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
