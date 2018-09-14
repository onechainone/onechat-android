package onemessageui.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oneapp.onechat.oneandroid.graphenechain.models.UserContactItem;
//import oneapp.onemessage.bean.User;

public class PinyinComparator implements Comparator {

    private Map<Character, Character> charMap = new HashMap<>();

    public List<Character> getCharList() {
        List<Character> characterList = new ArrayList<>(charMap.values());
        Collections.sort(characterList, new Comparator<Character>() {
            @Override
            public int compare(Character catalog0, Character catalog1) {
                return catalog0.compareTo(catalog1);
            }
        });
        return characterList;
    }

    @Override
    public int compare(Object arg0, Object arg1) {
        if (arg0 instanceof UserContactItem) {
            // 按照名字排序
            UserContactItem user0 = (UserContactItem) arg0;
            UserContactItem user1 = (UserContactItem) arg1;
            Character catalog0 = PingYinUtil.DEAULT_FIRST_CHART;
            Character catalog1 = PingYinUtil.DEAULT_FIRST_CHART;

            if (user0 != null && user0.getUserName() != null
                    && user0.getUserName().length() > 0) {
                catalog0 = PingYinUtil.converterToFirstSpell(user0.getUserName());
            }
            if (user1 != null && user1.getUserName() != null
                    && user1.getUserName().length() > 0) {
                catalog1 = PingYinUtil.converterToFirstSpell(user1.getUserName());
            }
            charMap.put(catalog0, catalog0);
            charMap.put(catalog1, catalog1);

            int flag = catalog0.compareTo(catalog1);
            return flag;
        } else if (arg0 instanceof String) {
            // 按照名字排序
            String string1 = (String) arg0;
            String string2 = (String) arg1;
            Character catalog0 = PingYinUtil.DEAULT_FIRST_CHART;
            Character catalog1 = PingYinUtil.DEAULT_FIRST_CHART;

            if (string1 != null
                    && string1.length() > 0)
                catalog0 = PingYinUtil.converterToFirstSpell(string1);

            if (string2 != null && string2.length() > 0)
                catalog1 = PingYinUtil.converterToFirstSpell(string2);

            charMap.put(catalog0, catalog0);
            charMap.put(catalog1, catalog1);
            int flag = catalog0.compareTo(catalog1);
            return flag;
        } else {
            return 0;
        }
    }

}
