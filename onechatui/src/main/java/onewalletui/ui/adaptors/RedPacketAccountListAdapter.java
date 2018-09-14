package onewalletui.ui.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import onewalletui.ui.widget.Amount;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onemessage.bean.CoinBasicInfo;
import oneapp.onechat.oneandroid.onemessage.bean.RedPacketAssetBean;
import oneapp.onechat.oneandroid.onewallet.modle.AssetInfo;
import oneapp.onechat.oneandroid.onewallet.modle.CoinRateBean;
import oneapp.onechat.oneandroid.onewallet.util.AssetInfoUtils;
import oneapp.onechat.oneandroid.onewallet.util.CoinInfoUtils;
import oneapp.onechat.oneandroid.onewallet.util.CoinRateUtils;
import onewalletui.util.ImageUtils;

/**
 * @author John L. Jegutanis
 */
public class RedPacketAccountListAdapter extends BaseAdapter {
    private final Context context;
    private List<RedPacketAssetBean> redPacketAssetList;


    public RedPacketAccountListAdapter(final Context context, List<RedPacketAssetBean> redPacketAssetList) {
        this.context = context;
        detailList(redPacketAssetList);
    }

    @Override
    public int getCount() {
        return redPacketAssetList.size();
    }

    @Override
    public RedPacketAssetBean getItem(int position) {
        return redPacketAssetList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.account_row, null);
        }

        View lineView = row.findViewById(R.id.view_bottom_line);
        if (position == redPacketAssetList.size() - 1) {
            lineView.setVisibility(View.GONE);
        } else {
            lineView.setVisibility(View.VISIBLE);
        }

        RedPacketAssetBean redPacketAssetBean = getItem(position);
        AssetInfo assetInfo = AssetInfoUtils.getAssetInfoBySymbol(redPacketAssetBean.getAsset_code());

        if (assetInfo != null) {

            CoinBasicInfo coinBasicInfo = CoinInfoUtils.getCoinInfoBySymbol(assetInfo.getBase_symbol());
            ImageView mIconIv = (ImageView) row.findViewById(R.id.account_icon);
            ImageUtils.displayCoinIconImage(context, CoinInfoUtils.getCoinImgBySymbol(assetInfo.getBase_symbol()), mIconIv);
            final TextView rowLabel = (TextView) row.findViewById(R.id.account_description);
            rowLabel.setText(assetInfo.getShort_name());
            final Amount rowValue = (Amount) row.findViewById(R.id.account_balance);


            final TextView typeLabel = (TextView) row.findViewById(R.id.coin_type_tag);
                typeLabel.setVisibility(View.GONE);

            String tempBalance = OneAccountHelper.powerInStringSubstring(String.valueOf(assetInfo.getPrecision()), redPacketAssetBean.getAmount_available());
            rowValue.setAmount(tempBalance);
            rowValue.setSymbol(assetInfo.getShort_name());

            // FIXME: 2017/10/14 hs
            final Amount rowBalanceRateValue = (Amount) row.findViewById(R.id.account_balance_rate);
            CoinRateBean coinRateBean = CoinRateUtils.getCoinRateBySymbol(assetInfo.getBase_symbol());
            if (coinRateBean != null) {
                rowBalanceRateValue.setAmount(CoinRateUtils.getFormatRateValue(tempBalance, assetInfo.getBase_symbol()));
                rowBalanceRateValue.setSymbol(coinRateBean.getSymbol1());
            } else {
                rowBalanceRateValue.setSymbol("");
                rowBalanceRateValue.setAmount(context.getString(R.string.default_value));
            }

            final Amount rowRateValue = (Amount) row.findViewById(R.id.exchange_rate_row_rate);
            if (coinRateBean != null) {
                rowRateValue.setAmount(coinRateBean.getPrice());
                rowRateValue.setSymbol(coinRateBean.getSymbol1());
            } else {
                rowRateValue.setAmount(context.getString(R.string.default_value));
                rowRateValue.setSymbol("");
            }
        }
        return row;
    }

    public void replace(List<RedPacketAssetBean> redPacketAssetList) {
        detailList(redPacketAssetList);
        notifyDataSetChanged();
    }

    private void detailList(List<RedPacketAssetBean> redPacketAssetList) {
        this.redPacketAssetList = new ArrayList<>();
        for (RedPacketAssetBean redPacketAssetBean : redPacketAssetList) {
            boolean ifShow = false;
            try {
                AssetInfo assetInfo = AssetInfoUtils.getAssetInfoBySymbol(redPacketAssetBean.getAsset_code());
//                ifShow = assetInfo != null && assetInfo.getCan_transfer().equals(Constants.YES_STRING);
                ifShow = assetInfo != null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ifShow)
                this.redPacketAssetList.add(redPacketAssetBean);
        }
    }
}