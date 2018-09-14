package oneapp.onechat.chat.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import oneapp.onechat.androidapp.R;
import onemessageui.utils.skin.SkinBean;
import onemessageui.utils.skin.SkinUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import onemessageui.common.ViewHolder;


public class SkinTypeAdapter extends BaseAdapter {
    private Context mContext;
    private List<SkinBean> skinList;
    private String mTempSkin = SkinUtils.DEFAULT_SKIN;

    public SkinTypeAdapter(Context mContext) {
        mTempSkin = SkinUtils.getSkinName();
        this.mContext = mContext;
        this.skinList = SkinUtils.getSkinList();
    }

    @Override
    public int getCount() {
        return skinList.size();
    }

    @Override
    public SkinBean getItem(int position) {
        return skinList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_exhange_type, null);
        }
        ImageView ivSelect = ViewHolder.get(convertView,
                R.id.iv_select);
        TextView tvName = ViewHolder.get(convertView,
                R.id.tv_coin_rate_type);

        final String skinName = skinList.get(position).getName();
        tvName.setText(mContext.getString(skinList.get(position).getStringId()));
        if (StringUtils.equals(mTempSkin, skinName))
            ivSelect.setImageResource(R.drawable.radiobutton_select);
        else
            ivSelect.setImageResource(R.drawable.radiobutton_normal);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTempSkin = skinName;
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public String getSelectSkin() {
        return mTempSkin;
    }

}
