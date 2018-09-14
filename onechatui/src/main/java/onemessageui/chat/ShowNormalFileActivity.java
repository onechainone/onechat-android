package onemessageui.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ProgressBar;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemFileItemMessageBody;
import onewalletui.ui.BaseActivity;

public class ShowNormalFileActivity extends BaseActivity {
    private ProgressBar progressBar;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_file);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        final ItemFileItemMessageBody messageBody = getIntent().getParcelableExtra(
                "msgbody");
        file = new File(messageBody.getLocalUrl());
        // set head map
        final Map<String, String> maps = new HashMap<String, String>();
        if (!TextUtils.isEmpty(messageBody.getEncryptKey())) {
            maps.put("share-secret", messageBody.getEncryptKey());
        }
//		// 下载文件
//		new Thread(new Runnable() {
//			public void run() {
//				HttpFileManager fileManager = new HttpFileManager(
//						ShowNormalFileActivityChat.this, getInstance()
//								.getStorageUrl());
//				fileManager.downloadFile(messageBody.getRemoteUrl(),
//						messageBody.getLocalUrl(), maps,
//						new CloudOperationCallback() {
//
//							@Override
//							public void onSuccess(String result) {
//								runOnUiThread(new Runnable() {
//									public void run() {
//										FileUtils.openFile(file,
//												ShowNormalFileActivityChat.this);
//										finish();
//									}
//								});
//							}
//
//							@Override
//							public void onProgress(final int progress) {
//								runOnUiThread(new Runnable() {
//									public void run() {
//										progressBar.setProgress(progress);
//									}
//								});
//							}
//
//							@Override
//							public void onError(final String msg) {
//								runOnUiThread(new Runnable() {
//									public void run() {
//										if (file != null && file.exists()
//												&& file.isFile())
//											file.delete();
//										String str4 = getResources()
//												.getString(
//														R.string.Failed_to_download_file);
//										Toast.makeText(
//												ShowNormalFileActivityChat.this,
//												str4 + msg, Toast.LENGTH_SHORT)
//												.show();
//										finish();
//									}
//								});
//							}
//						});
//
//			}
//		}).start();

    }
}
