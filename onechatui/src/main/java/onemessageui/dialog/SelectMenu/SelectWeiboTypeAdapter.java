package onemessageui.dialog.SelectMenu;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import sdk.android.onechatui.R;
import onemessageui.utils.ViewHolder;
import oneapp.onechat.oneandroid.onemessage.community.bean.WeiboSelectType;

//import oneapp.onemessage.bean.User;

public class SelectWeiboTypeAdapter extends BaseAdapter {
    private Context mContext;
    private List<WeiboSelectType> seedList;

    private int selectPosition = 0;


    public SelectWeiboTypeAdapter(Context mContext, List<WeiboSelectType> seedList, int selectPosition) {
        this.mContext = mContext;
        this.seedList = seedList;
        this.selectPosition = selectPosition;
    }

    @Override
    public int getCount() {
        return seedList.size();
    }

    @Override
    public WeiboSelectType getItem(int position) {
        return seedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WeiboSelectType seed = seedList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_select_menu, null);
        }

        TextView tvMenu = ViewHolder.get(convertView, R.id.tv_string_menu);

        tvMenu.setText(mContext.getString(seed.getTypeResId()));
        if (selectPosition == position) {
                tvMenu.setTextColor(ContextCompat.getColor(mContext, R.color.base_color));
        } else {
                tvMenu.setTextColor(ContextCompat.getColor(mContext, R.color.black_second));
        }
        return convertView;
    }

}
