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
import onemessageui.utils.ViewHolder;
import oneapp.onechat.oneandroid.onewallet.modle.CoinRateBean;
import oneapp.onechat.oneandroid.onewallet.util.CoinRateUtils;

//import oneapp.onemessage.bean.User;

public class ExchangeTypeAdapter extends BaseAdapter {
    private Context mContext;
    private List<CoinRateBean> RateInfos;
    private String mTempRateSymbol;

    public ExchangeTypeAdapter(Context mContext, List<CoinRateBean> RateInfos) {
        mTempRateSymbol = CoinRateUtils.getCurrentRateType();
        this.mContext = mContext;
        this.RateInfos = RateInfos;
    }

    @Override
    public int getCount() {
        return RateInfos.size();
    }

    @Override
    public CoinRateBean getItem(int position) {
        return RateInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CoinRateBean rateBean = RateInfos.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_exhange_type, null);
        }
        ImageView ivSelect = ViewHolder.get(convertView,
                R.id.iv_select);
        TextView tvName = ViewHolder.get(convertView,
                R.id.tv_coin_rate_type);
        tvName.setText(rateBean.getSymbol1());
        if (mTempRateSymbol.equals(rateBean.getSymbol1()))
            ivSelect.setImageResource(R.drawable.radiobutton_select);
        else
            ivSelect.setImageResource(R.drawable.radiobutton_normal);

        return convertView;
    }

    public void refreshSelect(int selectPosition) {
        mTempRateSymbol = getItem(selectPosition).getSymbol1();
        notifyDataSetChanged();
    }

    public String getSelectSymbol() {
        return mTempRateSymbol;
    }

}
