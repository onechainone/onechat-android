package onemessageui.adpter;

/**
 * Created by 何帅 on 2017/12/25.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sdk.android.onechatui.R;


/**
 * Description:
 * User: chenzheng
 * Date: 2017/2/17 0017
 * Time: 11:43
 */
public class InputSeedWordAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<String> list;

    public InputSeedWordAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.list = new ArrayList<>();

    }

    public List<String> getList() {
        return list;
    }

    public void setItems(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.item_input_seed_flow_tag, null);
            holder.evaluate_tv = (TextView) convertView.findViewById(R.id.tv_seed_tag);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String seedWord = getItem(position);
        holder.evaluate_tv.setText(seedWord);
        return convertView;
    }

    private final class ViewHolder {
        private TextView evaluate_tv;
    }
}
