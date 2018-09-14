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

import butterknife.ButterKnife;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.modle.AssetInfo;
import oneapp.onechat.oneandroid.onewallet.util.CoinInfoUtils;
import onewalletui.util.ImageUtils;
import onewalletui.util.UiUtils;

/**
 * @author John L. Jegutanis
 */
public class SelectAssetAdapter extends BaseAdapter {
    private final Context context;
    private final List<AssetInfo> mAddressList;
    ImageView icon;
    TextView title;

    private String searchStr = "";

    public SelectAssetAdapter(final Context context, List<AssetInfo> mAddressList, String searchStr) {
        this.context = context;
        this.mAddressList = new ArrayList<>();
        this.searchStr = searchStr;
        for (AssetInfo assets : mAddressList) {
            boolean ifShow = false;
            try {
                ifShow = assets != null && assets.getCan_transfer().equals(Constants.YES_STRING);
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
    public AssetInfo getItem(int position) {
        return mAddressList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.select_asset_list_row, null);
        }
        icon = (ImageView) row.findViewById(R.id.item_icon);
        title = (TextView) row.findViewById(R.id.item_text);
        ButterKnife.bind(this, row);
        AssetInfo address = getItem(position);
        UiUtils.SetSearchTextView(context, title, address.getShort_name(), searchStr);
        ImageUtils.displayCoinIconImage(context, CoinInfoUtils.getCoinImgBySymbol(address.getBase_symbol()), icon);

        return row;
    }


}