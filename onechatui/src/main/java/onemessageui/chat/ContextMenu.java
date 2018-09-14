package onemessageui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import onemessageui.utils.CommonUtils;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage;
import onewalletui.util.jump.JumpParamsContants;

public class ContextMenu extends AppCompatActivity {

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int txtValue = ItemMessage.Type.TXT.ordinal();
//        ItemMessage.ChatType.GroupChat.ordinal();
        int type = getIntent().getIntExtra("type", -1);
        int chattype = getIntent().getIntExtra(JumpParamsContants.INTENT_CHAT_TYPE, -1);

        if (type == ItemMessage.Type.TXT.ordinal()) {
            setContentView(R.layout.context_menu_for_text);
        } else if (type == ItemMessage.Type.LOCATION.ordinal()) {
            setContentView(R.layout.context_menu_for_location);
        } else if (type == ItemMessage.Type.IMAGE.ordinal()) {
            setContentView(R.layout.context_menu_for_image);
        } else if (type == ItemMessage.Type.VOICE.ordinal()) {
            setContentView(R.layout.context_menu_for_voice);
        } else if (type == ItemMessage.Type.VIDEO.ordinal()) {
            setContentView(R.layout.context_menu_for_video);
        } else if (type == ItemMessage.Type.RED_PACKET.ordinal()) {
            setContentView(R.layout.context_menu_for_red_packet);
        }

        try {
            if (chattype == ItemMessage.ChatType.GroupChat.ordinal()) {
                findViewById(R.id.tv_reward).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.tv_reward).setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }



		/*
         * switch (getIntent().getIntExtra("type", -1)) { case txtValue:
		 * setContentView(R.layout.context_menu_for_text); break; case
		 * ItemMessage.Type.LOCATION.ordinal():
		 * setContentView(R.layout.context_menu_for_location); break; case
		 * ItemMessage.Type.IMAGE.ordinal():
		 * setContentView(R.layout.context_menu_for_image); break; case
		 * ItemMessage.Type.VOICE.ordinal():
		 * setContentView(R.layout.context_menu_for_voice); break; //need to
		 * support netdisk and send netsdk? case Message.TYPE_NETDISK:
		 * setContentView(R.layout.context_menu_for_netdisk); break; case
		 * Message.TYPE_SENT_NETDISK:
		 * setContentView(R.layout.context_menu_for_sent_netdisk); break;
		 * default: break; }
		 */
        position = getIntent().getIntExtra("position", -1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    public void copy(View view) {
        setResult(CommonUtils.RESULT_CODE_COPY,
                new Intent().putExtra("position", position));
        finish();
    }

    public void delete(View view) {
        setResult(CommonUtils.RESULT_CODE_DELETE,
                new Intent().putExtra("position", position));
        finish();
    }

    public void forward(View view) {
        setResult(CommonUtils.RESULT_CODE_FORWARD,
                new Intent().putExtra("position", position));
        finish();
    }

    public void open(View v) {
        setResult(CommonUtils.RESULT_CODE_OPEN,
                new Intent().putExtra("position", position));
        finish();
    }

    public void download(View v) {
        setResult(CommonUtils.RESULT_CODE_DWONLOAD,
                new Intent().putExtra("position", position));
        finish();
    }

    public void toCloud(View v) {
        setResult(CommonUtils.RESULT_CODE_TO_CLOUD,
                new Intent().putExtra("position", position));
        finish();
    }

    //赞赏
    public void reward(View v) {
        setResult(CommonUtils.RESULT_CODE_REWARD,
                new Intent().putExtra("position", position));
        finish();
    }
}
