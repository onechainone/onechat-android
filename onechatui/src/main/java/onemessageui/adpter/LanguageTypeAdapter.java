package onemessageui.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onewallet.modle.LanguageBean;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import oneapp.onechat.oneandroid.onewallet.util.SystemLanguageUtils;
import onemessageui.common.ViewHolder;

public class LanguageTypeAdapter extends BaseAdapter {
    private Context mContext;
    private List<LanguageBean> languageList;
    private int mTempLanguage = SystemLanguageUtils.DEFAULT_LANGUAGE_POSITION;

    public LanguageTypeAdapter(Context mContext) {
        if (SharePreferenceUtils.contains(SharePreferenceUtils.SP_LANGUAGE_TYPE_POSITION))
            mTempLanguage = (int) SharePreferenceUtils.getObject(SharePreferenceUtils.SP_LANGUAGE_TYPE_POSITION);
        this.mContext = mContext;
        this.languageList = SystemLanguageUtils.SUPPORTED_LANGUAGES;
    }

    @Override
    public int getCount() {
        return languageList.size();
    }

    @Override
    public LanguageBean getItem(int position) {
        return languageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_exhange_type, null);
        }
        ImageView ivSelect = ViewHolder.get(convertView,
                R.id.iv_select);
        TextView tvName = ViewHolder.get(convertView,
                R.id.tv_coin_rate_type);
        tvName.setText(languageList.get(position).getName());
        if (mTempLanguage == position)
            ivSelect.setImageResource(R.drawable.radiobutton_select);
        else
            ivSelect.setImageResource(R.drawable.radiobutton_normal);

        return convertView;
    }

    public void refreshSelect(int selectPosition) {
        mTempLanguage = selectPosition;
        notifyDataSetChanged();
    }

    public int getSelectLanguage() {
        return mTempLanguage;
    }

}
