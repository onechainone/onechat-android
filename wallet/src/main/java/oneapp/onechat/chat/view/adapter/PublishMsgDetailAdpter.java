package oneapp.onechat.chat.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.apache.http.message.BasicNameValuePair;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.oneandroid.onemessage.Constants;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import onemessageui.common.ViewHolder;
import onemessageui.view.activity.SimpleWebViewActivity;

//订阅号信息详情页面
public class PublishMsgDetailAdpter extends BaseAdapter {
    protected Context context;

    public PublishMsgDetailAdpter(Context ctx) {
        context = ctx;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (position == 0) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.layout_item_publishmsgdetail, parent, false);
                convertView.setOnClickListener(onclicklister);
            } else {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.layout_item_publishmsgdetail2, parent, false);
                View layout_msg1 = ViewHolder
                        .get(convertView, R.id.layout_msg1);
                View layout_msg2 = ViewHolder
                        .get(convertView, R.id.layout_msg2);
                View layout_msg3 = ViewHolder
                        .get(convertView, R.id.layout_msg3);
                layout_msg1.setOnClickListener(onclicklister);
                layout_msg2.setOnClickListener(onclicklister);
                layout_msg3.setOnClickListener(onclicklister);
            }
        }

        return convertView;
    }

    private OnClickListener onclicklister = new OnClickListener() {

        @Override
        public void onClick(View v) {

            Utils.start_Activity(
                    (Activity) context,
                    SimpleWebViewActivity.class,
                    new BasicNameValuePair(Constants.Title, "人人都是产品经理"),
                    new BasicNameValuePair(
                            Constants.URL,
                            "http://mp.onechat.one/s?__biz=MjM5NTMxNTU0MQ==&mid=212741823&idx=1&sn=8b865adff465b7ee2b4ce9339301b8e6#rd"));
        }
    };
}
