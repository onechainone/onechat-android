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
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.graphenechain.models.UserContactItem;
import onemessageui.common.PingYinUtil;
import onemessageui.common.PinyinComparator;
import onemessageui.utils.ViewHolder;
import onewalletui.util.ImageUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;


//import oneapp.onemessage.bean.User;

public class ContactAdapter extends BaseAdapter implements SectionIndexer {
    private Context mContext;
    private List<UserContactItem> list = new ArrayList<>();
    private String searchStr = "";
    PinyinComparator pinyinComparator;

    @SuppressWarnings("unchecked")
    public ContactAdapter(Context mContext, List<UserContactItem> users) {
        this.mContext = mContext;
        this.list = users;
        if (this.list == null) {
            this.list = new ArrayList<>();
        }

        pinyinComparator = new PinyinComparator();
        // 排序(实现了中英文混排)
        Collections.sort(list, pinyinComparator);
//        Collections.sort(list, new Comparator<UserContactItem>() {
//            public int compare(UserContactItem uid1, UserContactItem uid2) {
//                if (!StringUtils.equalsNull(uid1.getUserName()) && !StringUtils.equalsNull(uid2.getUserName())) {
//                    Integer uidLong1 = uid1.getUserName().length();
//                    Integer uidLong2 = uid2.getUserName().length();
//                    return uidLong1.compareTo(uidLong2);
//                } else
//                    return 0;
//            }
//        });
    }

    @SuppressWarnings("unchecked")
    public ContactAdapter(Context mContext, List<UserContactItem> users, String searchStr) {
        this.mContext = mContext;
        this.list = users;
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        // 排序(实现了中英文混排)
//		Collections.sort(list, new PinyinComparator());
        this.searchStr = searchStr;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public UserContactItem getItem(int position) {
        return list.size() > position ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        final UserContactItem user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.contact_item, null);
        }

        if (user != null) {
            ImageView ivAvatar = ViewHolder.get(convertView,
                    R.id.contactitem_avatar_iv);
            TextView tvCatalog = ViewHolder.get(convertView,
                    R.id.contactitem_catalog);
            TextView tvLocal = ViewHolder
                    .get(convertView, R.id.contactitem_nick);
//			TextView tvNick = ViewHolder.get(convertView,
//					R.id.contactitem_catalog);
//			TextView tvId = ViewHolder
//					.get(convertView, R.id.contactitem_nick);
            String catalog = PingYinUtil.converterToFirstSpell(
                    user.getUserName()) + "";
            if (position == 0) {
                tvCatalog.setVisibility(View.VISIBLE);
                tvCatalog.setText(catalog);
            } else {
                UserContactItem Nextuser = list.get(position - 1);
                String lastCatalog = PingYinUtil.converterToFirstSpell(
                        Nextuser.getUserName()) + "";
                if (catalog.equals(lastCatalog)) {
                    tvCatalog.setVisibility(View.GONE);
                } else {
                    tvCatalog.setVisibility(View.VISIBLE);
                    tvCatalog.setText(catalog);
                }
            }
            ImageUtils.displayAvatarNetImage(mContext, user.avatar, ivAvatar, user.getSex());

//			UiUtils.SetSearchTextView(mContext, tvLocal, user.getUserName(), searchStr);
//			UiUtils.SetSearchTextView(mContext, tvLocal, user.getUserName(), searchStr);
//			UiUtils.SetSearchTextView(mContext, tvLocal, user.getUserName(), searchStr);

            String username_lower = user.getUserName().toLowerCase();
            int index = username_lower.indexOf(searchStr);
            if (index >= 0 && !StringUtils.equalsNull(searchStr)) {
                SpannableString span = new SpannableString(user.getUserName());
                span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.base_color)), index, index + searchStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvLocal.setText(user.getUserName());
                tvLocal.setText(span);  //设置字体变颜色
            } else
                tvLocal.setText(user.getUserName());
        }
        return convertView;
    }

    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < list.size(); i++) {
            UserContactItem user = list.get(i);
            char firstChar = PingYinUtil
                    .converterToFirstSpell(user.getUserName());
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

    public List<Character> getPinyinCharList() {
        if (pinyinComparator == null) {
            return new ArrayList<>();
        }
        return pinyinComparator.getCharList();
    }

    public void refreshList(List<UserContactItem> users) {
        this.list = users;
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        // 排序(实现了中英文混排)
        Collections.sort(list, pinyinComparator);
        notifyDataSetChanged();
    }
}
