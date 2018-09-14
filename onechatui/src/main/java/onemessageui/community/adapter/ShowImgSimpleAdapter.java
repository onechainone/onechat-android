package onemessageui.community.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.io.File;
import java.util.List;

import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onewallet.Constants;
import onewalletui.util.ImageUtils;


/**
 * Created by 何帅 on 2016/6/14.
 * 没有首个添加图片图标的adapter
 */
public class ShowImgSimpleAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private boolean shape;
    List<ImageItem> images;
    Activity context;
    private DeleteImgInf deleteImgInf;

    public boolean isShape() {
        return shape;
    }

    public void setShape(boolean shape) {
        this.shape = shape;
    }

    public ShowImgSimpleAdapter(Activity context, List<ImageItem> images) {
        this.images = images;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setDeleteImgInf(DeleteImgInf deleteImgInf) {
        this.deleteImgInf = deleteImgInf;
    }

    public void update(List<ImageItem> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    public int getCount() {
        return images.size();
    }

    @Override
    public ImageItem getItem(int i) {
        return images.size() > i ? images.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_send_weibo_img,
                    parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView
                    .findViewById(R.id.item_grida_image);

            holder.deleteIv = (ImageView) convertView.findViewById(R.id.iv_delect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == images.size()) {

            holder.image.setImageBitmap(BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.icon_addpic_unfocused));
            holder.deleteIv.setVisibility(View.GONE);

            if (position == 9) {
                holder.image.setVisibility(View.GONE);
            }

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == images.size()) {
                        //淡出选择图片
                        ImagePicker.getInstance().setSelectLimit(CommonConstants.MAX_SEND_WEIBO_IMG - images.size());
                        Intent intent = new Intent(context, ImageGridActivity.class);
                        context.startActivityForResult(intent, Constants.REQUEST_CODE_TAKE_PIC);
                    }
                }
            });
        } else {
            holder.image.setVisibility(View.VISIBLE);
            ImageUtils.displaySimpleNetImage(context, Uri.fromFile(new File(images.get(position).path)).toString(), holder.image);
            holder.deleteIv.setVisibility(View.VISIBLE);
        }

        // 删除按钮添加的监听器
        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImgInf.onDeleteImgClick(position);
            }
        });

        return convertView;
    }

    public class ViewHolder {
        public ImageView image;
        public ImageView deleteIv;
    }

    public interface DeleteImgInf {
        void onDeleteImgClick(int position);
    }
}
