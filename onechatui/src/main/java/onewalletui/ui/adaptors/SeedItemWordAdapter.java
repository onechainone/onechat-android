package onewalletui.ui.adaptors;

/**
 * Created by 何帅 on 2017/12/25.
 */

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sdk.android.onechatui.R;
import onewalletui.ui.widget.flowtag.Evaluate;


/**
 * Description:
 * User: chenzheng
 * Date: 2017/2/17 0017
 * Time: 11:43
 */
public class SeedItemWordAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<Evaluate> list;

    private int chooseResId = R.drawable.tuoyuan_base_color_bg;
    private int normalResId = R.drawable.tuoyuan_seed_normal_bg;

    private int chooseTextColor = R.color.base_bg_color_level1;
    private int normalTextColor = R.color.base_color;

    public SeedItemWordAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.list = new ArrayList<>();

    }

    public List<Evaluate> getList() {
        return list;
    }

    public void setItems(List<Evaluate> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    public void setBackGround(int chooseResId, int normalResId) {
        this.chooseResId = chooseResId;
        this.normalResId = normalResId;
    }

    public void setTextColor(int chooseTextColor, int normalTextColor) {
        this.chooseTextColor = chooseTextColor;
        this.normalTextColor = normalTextColor;
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
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.item_seed_flow_tag, null);
            holder.evaluate_tv = (TextView) convertView.findViewById(R.id.tv_seed_tag);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Evaluate ee = (Evaluate) getItem(position);
        holder.evaluate_tv.setText(ee.getName());
        if (ee.isIs_choosed()) {
                holder.evaluate_tv.setTextColor(ContextCompat.getColor(context, chooseTextColor));
            holder.evaluate_tv.setBackgroundResource(chooseResId);
        } else {
                holder.evaluate_tv.setTextColor(ContextCompat.getColor(context, normalTextColor));
            holder.evaluate_tv.setBackgroundResource(normalResId);
        }
        return convertView;
    }

    private final class ViewHolder {
        private TextView evaluate_tv;
    }
}
