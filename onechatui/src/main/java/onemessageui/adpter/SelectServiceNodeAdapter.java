package onemessageui.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onewallet.modle.ServiceBean;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;

/**
 * @author John L. Jegutanis
 */
public class SelectServiceNodeAdapter extends BaseAdapter {
    private final Context context;
    private List<ServiceBean> mServiceList;
    ImageView status;
    TextView name;

    private boolean ifInitial;//初始状态

    public boolean isAllSuccess = true;


    public SelectServiceNodeAdapter(final Context context, List<ServiceBean> mServiceList) {
        this.context = context;
        this.mServiceList = mServiceList;
        ifInitial = false;
    }

    public SelectServiceNodeAdapter(final Context context, List<ServiceBean> mServiceList, boolean ifInitial) {
        this.context = context;
        this.mServiceList = mServiceList;
        this.ifInitial = ifInitial;
    }


    @Override
    public int getCount() {
        return mServiceList.size();
    }

    @Override
    public ServiceBean getItem(int position) {
        return mServiceList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.item_service_node, null);
        }

         status = (ImageView) row.findViewById(R.id.iv_status);
         name = (TextView) row.findViewById(R.id.tv_node_name);
        ButterKnife.bind(this, row);
        ServiceBean serviceBean = getItem(position);
        name.setText(serviceBean.getNode_name() + "-" + serviceBean.getService_uuid());
        if (!ifInitial && serviceBean.getLoad() < ServiceConstants.MAX_SERVICE_LOAD) {
            status.setImageResource(R.drawable.service_node_success);
        } else {
            status.setImageResource(R.drawable.service_node_fail);
            isAllSuccess = false;
        }

        return row;
    }

    public void refresh(List<ServiceBean> mServiceList, boolean ifInitial) {
        this.ifInitial = ifInitial;
        this.mServiceList = mServiceList;
        notifyDataSetChanged();
    }

}