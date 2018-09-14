package onemessageui.chat.utils;

import java.util.HashMap;

import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemConversation;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage;

/**
 * Created by 何帅 on 2018/8/8.
 */

public class MsgCatchUtils {
    private static HashMap<String, ItemConversation> conversationHashMap = new HashMap<>();

    public static HashMap<String, ItemConversation> getConversationHashMap() {
        return conversationHashMap;
    }

    public static void setConversationHashMap(HashMap<String, ItemConversation> conversationHashMap) {
        MsgCatchUtils.conversationHashMap = conversationHashMap;
    }

    public static ItemConversation getConversationByUsername(String userName) {
        if (!getConversationHashMap().containsKey(userName)) {
            getConversationHashMap().put(userName, new ItemConversation(userName));
        }
        return getConversationHashMap().get(userName);
    }

    public static void addConversationByUsername(String userName, ItemMessage message) {
        ItemConversation conversation = getConversationByUsername(userName);
        conversation.addMessage(message);
    }
}
