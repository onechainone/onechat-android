package onemessageui.adpter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import onemessageui.common.PingYinUtil;
import onemessageui.common.PinyinComparator;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import onemessageui.common.ViewHolder;

//import oneapp.onemessage.bean.User;

public class SeedWordAdapter extends BaseAdapter implements SectionIndexer {
    private Context mContext;
    private List<String> seedList;

    private String searchStr = "";

    public SeedWordAdapter(Context mContext, List<String> seedList) {
        this.mContext = mContext;
        this.seedList = seedList;
        // 排序(实现了中英文混排)
        Collections.sort(seedList, new PinyinComparator());
    }

    public SeedWordAdapter(Context mContext, List<String> seedList, String searchStr) {
        this.mContext = mContext;
        this.seedList = seedList;
        // 排序(实现了中英文混排)
//        Collections.sort(seedList, new PinyinComparator());
        this.searchStr = searchStr;
    }

    @Override
    public int getCount() {
        return seedList.size();
    }

    @Override
    public String getItem(int position) {
        return seedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String seed = seedList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_seed_word, null);

        }
        TextView tvCatalog = ViewHolder.get(convertView,
                R.id.contactitem_catalog);
        TextView tvSeedWord = ViewHolder.get(convertView, R.id.tv_seed_word);
        String catalog = PingYinUtil.converterToFirstSpell(seed) + "";
        if (position == 0) {
            tvCatalog.setVisibility(View.VISIBLE);
            tvCatalog.setText(catalog);
        } else {
            String Nextuser = seedList.get(position - 1);
            String lastCatalog = PingYinUtil.converterToFirstSpell(
                    Nextuser) + "";
            if (catalog.equals(lastCatalog)) {
                tvCatalog.setVisibility(View.GONE);
            } else {
                tvCatalog.setVisibility(View.VISIBLE);
                tvCatalog.setText(catalog);
            }
        }

        String seed_lower = seed.toLowerCase();
        int index = seed_lower.indexOf(searchStr);
        if (index >= 0 && !StringUtils.equalsNull(searchStr)) {
            SpannableString span = new SpannableString(seed);
            span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.base_color)), index, index + searchStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvSeedWord.setText(seed);
            tvSeedWord.setText(span);  //设置字体变颜色
        } else
            tvSeedWord.setText(seed);
        return convertView;
    }

    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < seedList.size(); i++) {
            char firstChar = PingYinUtil.converterToFirstSpell(seedList.get(i));
            if (firstChar == section) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}
