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

import butterknife.ButterKnife;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.bean.RedPacketAssetBean;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.modle.AssetInfo;
import oneapp.onechat.oneandroid.onewallet.util.AssetInfoUtils;
import oneapp.onechat.oneandroid.onewallet.util.CoinInfoUtils;
import onewalletui.util.ImageUtils;

/**
 * @author John L. Jegutanis
 */
public class SelectRedPacketAssetListAdapter extends BaseAdapter {
    private final Context context;
    private final List<RedPacketAssetBean> mAddressList;
    ImageView icon;
    TextView title;
    TextView assetValueTv;

    public SelectRedPacketAssetListAdapter(final Context context, List<RedPacketAssetBean> mAddressList) {
        this.context = context;
        this.mAddressList = new ArrayList<>();
        for (RedPacketAssetBean assets : mAddressList) {
            boolean ifShow = false;
            try {
                AssetInfo assetInfo = AssetInfoUtils.getAssetInfoBySymbol(assets.getAsset_code());
                ifShow = assetInfo != null && assetInfo.getCan_transfer().equals(Constants.YES_STRING);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ifShow)
                this.mAddressList.add(assets);
        }
    }


    @Override
    public int getCount() {
        return mAddressList.size();
    }

    @Override
    public RedPacketAssetBean getItem(int position) {
        return mAddressList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.item_select_red_packet_asset, null);
        }

        icon = (ImageView) row.findViewById(R.id.item_icon);
        title = (TextView) row.findViewById(R.id.item_text);
        assetValueTv = (TextView) row.findViewById(R.id.item_asset_value);

        ButterKnife.bind(this, row);
        RedPacketAssetBean assetBean = getItem(position);
        AssetInfo assetInfo = AssetInfoUtils.getAssetInfoBySymbol(assetBean.getAsset_code());
        String min_precision = CommonConstants.DEFAULT_CORE_ASSET_PRECISION;
        if (assetInfo != null) {
            title.setText(assetInfo.getShort_name());
            ImageUtils.displayCoinIconImage(context, CoinInfoUtils.getCoinImgBySymbol(assetInfo.getBase_symbol()), icon);
            min_precision = String.valueOf(assetInfo.getMin_precision());
        }

        assetValueTv.setText(OneAccountHelper.powerInStringSubstring(min_precision, assetBean.getAmount_available()));
        return row;
    }

}