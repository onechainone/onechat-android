package onemessageui.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import sdk.android.onechatui.R;

/**
 * Created by zengyuxin on 2018/5/10.
 */

public class InputKeywordAdapter extends BaseAdapter {

    private Context context;
    private List<String> keywordList;
    private LayoutInflater mInflater;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void setItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public InputKeywordAdapter(Context context, List<String> keywordList) {
        this.context = context;
        this.keywordList = keywordList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return keywordList == null ? 0 : keywordList.size();
    }

    @Override
    public Object getItem(int position) {
        return keywordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_input_seed_flow_tag, parent, false);
            holder.tv_seed_tag = (TextView) convertView.findViewById(R.id.tv_seed_tag);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_seed_tag.setText(keywordList.get(position));
        holder.tv_seed_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.setItemClick(position);
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tv_seed_tag;
    }

    public void refresh() {
        notifyDataSetChanged();
    }
}
