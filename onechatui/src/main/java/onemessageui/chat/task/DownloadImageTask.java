package onemessageui.chat.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.UtilLog;

public class DownloadImageTask extends AsyncTask<ItemMessage, Integer, Bitmap> {
    private DownloadFileCallback callback;
    Bitmap bitmap = null;
    public boolean downloadThumbnail = false;
    ItemMessage message;
    private String remoteDir;

    public DownloadImageTask(String remoteDir, DownloadFileCallback callback) {
        this.callback = callback;
        this.remoteDir = remoteDir;
    }

    @Override
    protected Bitmap doInBackground(ItemMessage... params) {
        /*
	    try {
	        message = params[1];//视频的图片path信息的message
        } catch (Exception e) {
            message = params[0];
        }
	  
	    
		String remoteFilePath = message.getFilePath().substring(message.getFilePath().lastIndexOf("/")+1);
		if(remoteDir != null){
			remoteFilePath = remoteDir + remoteFilePath;
		}
		final String localFilePath;
		if (downloadThumbnail) {
		    localFilePath = getThumbnailImagePath(message.getFilePath());
		    SMTLog.d("###", "localFilePath: "+localFilePath);
		} else {
		    localFilePath = message.getFilePath();
		}
//		final String remoteFilePath = message.getFilePath();
//		final String localFilePath = User.getImagePath() + "/"+ message.getImageName();
		SMTLog.d("###", "download picture from remote "+ remoteFilePath + " to local:" + localFilePath);
		final HttpFileManager httpFileMgr = new HttpFileManager(OneChatUserConfig.getInstance().applicationContext,
		        OneChatChatConfig.getInstance().ONECHAT_STORAGE_URL);
		CloudOperationCallback callback = new CloudOperationCallback() {
			public void onSuccess() {
				SMTLog.d("###", "offline file saved to "+ localFilePath);
				// after download to phone, we will delete the
				// file on server
				try {
					//httpFileMgr.deleteFileInBackground(remoteFilePath, null, null);
				    bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(localFilePath)));
					//bitmap = Bitmap.createScaledBitmap(bm, 120, 120, true);
					//bitmap = Bitmap.createBitmap(bm);
					//bm.recycle();
					//bm = null;
					
				} catch (Exception e) {
					e.printStackTrace();
					bitmap = null;
				}
			}

			public void onError(String msg) {
				SMTLog.e("###","offline file transfer error:" + msg);
				File file = new File(localFilePath);
				if(file.exists())
					file.delete();
			}

			public void onProgress(int progress) {
				onProgressUpdate(progress);
			}
		};
		if (downloadThumbnail) {
		    httpFileMgr.downloadThumbnailFile(remoteFilePath, localFilePath, OneChatUserConfig.getInstance().APPKEY, null, callback);
		} else {
		    httpFileMgr.downloadFile(remoteFilePath, localFilePath, OneChatUserConfig.getInstance().APPKEY, null, callback);
		}
		return bitmap;
		*/
        //todo: need to implement
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        callback.afterDownload(result);
    }

    @Override
    protected void onPreExecute() {
        callback.beforeDownload();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        callback.downloadProgress(values[0]);
    }


    public interface DownloadFileCallback {
        void beforeDownload();

        void downloadProgress(int progress);

        void afterDownload(Bitmap bitmap);
    }


    public static String getThumbnailImagePath(String imagePath) {
        String path = imagePath.substring(0, imagePath.lastIndexOf("/") + 1);
        path += "th" + imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());
        UtilLog.d("msg", "original image path:" + imagePath);
        UtilLog.d("msg", "thum image path:" + path);
        return path;
    }
}
