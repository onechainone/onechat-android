package onemessageui.community.adapter;

/**
 * Created by 何帅 on 2016/6/8.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import onewalletui.util.ImageUtils;

public class WeiboImgAdapter extends BaseAdapter {

    private Context context;
    private List<String> mImgList;


    public WeiboImgAdapter(Context context, List<String> mNoteHistoryList) {
        this.context = context;
        this.mImgList = mNoteHistoryList;
    }

    @Override
    public int getCount() {
        return mImgList.size();
    }

    @Override
    public Object getItem(int position) {
        return mImgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_weibo_img, null);
            holder = new ViewHolder();
            holder.mImg = (ImageView) convertView.findViewById(R.id.iv_note_history_img);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageUtils.displaySimpleNetImage(context, ServiceConstants.GetWeiboImgConfigServer() + mImgList.get(position), holder.mImg);

        return convertView;
    }

    static class ViewHolder {
        /**
         *
         **/
        private ImageView mImg;

    }

}