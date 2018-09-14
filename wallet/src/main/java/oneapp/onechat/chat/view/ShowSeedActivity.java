package oneapp.onechat.chat.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import onewalletui.ui.*;
import oneapp.onechat.androidapp.R;
import onewalletui.util.jump.JumpParamsContants;

/**
 * @author John L. Jegutanis
 */
public class ShowSeedActivity extends BaseActivity implements ShowSeedFragment.Listener {

    private static final String SHOW_SEED_TAG = "show_seed_tag";

    private boolean ifFirstCreate = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        ifUnlockThisActivity = false;
        ifCanScreenShot = false;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_wrapper);

        readArguments();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ShowSeedFragment.newInstance(ifFirstCreate), SHOW_SEED_TAG)
                    .commit();
        }
    }

    private void readArguments() {
        final Intent intent = this.getIntent();
        final Serializable sear = intent
                .getSerializableExtra(JumpParamsContants.INTENT_PARAMS_MAP);
        if (sear != null) {
            final HashMap<String, Boolean> paramMap = (HashMap<String, Boolean>) sear;
            this.ifFirstCreate = paramMap.get(JumpParamsContants.INTENT_IF_FIRST_CREATE_ACCOUNT);
        }
    }

    @Override
    public void onSeedNotAvailable() {
        DialogBuilder.warn(this, R.string.seed_not_available_title)
                .setMessage(R.string.seed_not_available)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create().show();
    }

    @Override
    public void onPassword(CharSequence password) {
        ShowSeedFragment f = (ShowSeedFragment) getFM().findFragmentByTag(SHOW_SEED_TAG);
        if (f != null) {
            f.setPassword(password);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        //Android.R.id.home对应应用程序图标的id
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int keyBackClickCount = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (ifFirstCreate)
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                switch (keyBackClickCount++) {
                    case 0:
                        Toast.makeText(this, getString(R.string.click_back_again), Toast.LENGTH_SHORT).show();
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                keyBackClickCount = 0;
                            }
                        }, 3000);
                        break;
                    case 1:
                        exitApp();
                        break;
                }
                return true;
            }
        return super.onKeyDown(keyCode, event);
    }
}
