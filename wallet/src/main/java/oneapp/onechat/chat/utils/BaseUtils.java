package oneapp.onechat.chat.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.google.common.io.CharStreams;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import oneapp.onechat.chat.task.AddUserContactsTask;
import oneapp.onechat.chat.utils.jump.JumpAppOutUtil;
import oneapp.onechat.chat.view.BaseActivity;
import oneapp.onechat.androidapp.R;
import oneapp.onechat.oneandroid.onemessage.bean.PhoneContactBean;
import oneapp.onechat.oneandroid.onemessage.bean.UpgradeBean;
import oneapp.onechat.oneandroid.onemessage.common.URLDecoder;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.util.BigDecimalUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.TimeUtils;
import oneapp.onechat.oneandroid.onewallet.util.UiUtils;
import oneapp.onechat.chat.WalletApplication;


public class BaseUtils {

    /**
     * SD卡是否已经安装
     *
     * @return
     */
    public static boolean isMounted() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static File saveBitmapFile(Bitmap bitmap, String fileName) {
        return saveBitmapFile(bitmap, fileName, false);
    }

    public static File saveBitmapFile(Bitmap bitmap, String fileName, boolean ifBroadcastGallery) {
        File saveFile = new File(Environment.getExternalStorageDirectory() + Constants.SAVE_FILE_NAME);
        if (!saveFile.exists()) { //如果该文件夹不存在，则进行创建
            saveFile.mkdirs();//创建文件夹
        }
        File imgFile = new File(Environment.getExternalStorageDirectory() + Constants.SAVE_FILE_NAME + Constants.SAVE_IMAGE_FILE_NAME);
        if (!imgFile.exists()) {
            imgFile.mkdir();
        }
        File file = new File(imgFile, fileName);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ifBroadcastGallery) {
            inertImageToPhone(file);
        }
        return file;
    }

    public static void inertImageToPhone(File file) {
        try {
            //把文件插入到系统图库
            MediaStore.Images.Media.insertImage(WalletApplication.getInstance().getContentResolver(), file.getAbsolutePath(), file.getName(), null);
//保存图片后发送广播通知更新数据库
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            WalletApplication.getInstance().sendBroadcast(intent);
        } catch (Exception e) {

        }
    }

    public static String getSaveBitmapFile() {
        // 下载位置
        String DownloadDir;
        /**
         * 创建路径的时候一定要用[/],不能使用[\],但是创建文件夹加文件的时候可以使用[\].
         * [/]符号是Linux系统路径分隔符,而[\]是windows系统路径分隔符 Android内核是Linux.
         */
        if (BaseUtils.isMounted())// 判断是否插入SD卡
        {
            DownloadDir = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.SAVE_FILE_NAME;// 保存到SD卡路径下
        } else {
            DownloadDir = WalletApplication.getInstance().getApplicationContext().getFilesDir().getAbsolutePath() + Constants.SAVE_FILE_NAME;// 保存到app的包名路径下
        }
        File destDir = new File(DownloadDir);
        if (!destDir.exists()) {// 判断文件夹是否存在
            destDir.mkdirs();
        }

        destDir = new File(DownloadDir + Constants.SAVE_IMAGE_FILE_NAME);
        if (!destDir.exists()) {// 判断文件夹是否存在
            destDir.mkdirs();
        }

//        File downloadFile = new File(Environment.getExternalStorageDirectory(), DownloadDir);
        if (!destDir.mkdirs()) {
            try {
                destDir.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String savePath = destDir.getAbsolutePath();
        return savePath;
    }

    public static File uri2File(Context context, Uri uri) {
        String path = null;
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA}, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(proj[0]);
                if (columnIndex >= 0) {
                    path = cursor.getString(columnIndex);  //获取照片路径
                } else if (TextUtils.equals(uri.getAuthority(), SYSTEM_FILEPROVIDER)) {
                    path = parseOwnUri(uri);
                }

            }
            cursor.close();

            return new File(path);
        } else {
            //Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    public static String getPath(final Context context, final Uri uri) {

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        } else {
            File file = uri2File(context, uri);
            if (file != null) {
                return file.getPath();
            }
        }

        return null;
    }

    public static String getFromFileUri(Context context, Uri uri) {
        if (uri == null) return null;

        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            if (pfd == null) {
                return null;
            }
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);

            String tempFilename = getTempFilename(context);
            output = new FileOutputStream(tempFilename);

            int read;
            byte[] bytes = new byte[4096];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }

            return new File(tempFilename).getAbsolutePath();
        } catch (Exception ignored) {

            ignored.getStackTrace();
        } finally {
            closeSilently(input);
            closeSilently(output);
        }
        return null;
    }

    private static String getTempFilename(Context context) throws IOException {
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile("file", "tmp", outputDir);
        return outputFile.getAbsolutePath();
    }

    public static void closeSilently(@Nullable Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static final String SYSTEM_FILEPROVIDER = Constants.PACKAGE_NAME + ".fileprovider";
    private static final String SYSTEM_XML_FILE_PATH = "oneapp_file/";

    /**
     * 将TakePhoto 提供的Uri 解析出文件绝对路径
     *
     * @param uri
     * @return
     */
    public static String parseOwnUri(Uri uri) {
        if (uri == null) return null;
        String path;
        if (TextUtils.equals(uri.getAuthority(), SYSTEM_FILEPROVIDER)) {
            path = new File(uri.getPath().replace(SYSTEM_XML_FILE_PATH, "")).getAbsolutePath();
        } else {
            path = uri.getPath();
        }
        return path;
    }

    public static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), SYSTEM_FILEPROVIDER, file);
        } else {
            uri = Uri.fromFile(file);
        }

        return uri;
    }

    /**
     * 创建一条图片地址uri,用于保存拍照后的照片
     *
     * @param context
     * @return 图片的uri
     */
    public static Uri createImagePathUri(Context context) {
        Uri imageFilePath = null;
        try {

            String status = Environment.getExternalStorageState();
            SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
            long time = TimeUtils.getTrueTime();
            String imageName = timeFormatter.format(new Date(time));
            // ContentValues是我们希望这条记录被创建时包含的数据信息
            ContentValues values = new ContentValues(3);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
            values.put(MediaStore.Images.Media.DATE_TAKEN, time);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
            if (status.equals(Environment.MEDIA_MOUNTED)) {
                // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
                imageFilePath = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);
            } else {
                imageFilePath = context.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                        values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageFilePath;
    }


    public static Uri getImageUriForFile(Context context, File file) {
        ExifInterface exif = null;
        Uri uri = getUriForFile(context, file);
        int digree = 0;
        try {
            exif = new ExifInterface(file.getPath());
            if (exif != null) {

                // 读取图片中相机方向信息
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                // 计算旋转角度
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        Bitmap orc_bitmap = UiUtils.getBitmapFormUri(context, uri);
        if (orc_bitmap != null && digree != 0) {
            // 旋转图片
            Matrix m = new Matrix();
            m.postRotate(digree);
            orc_bitmap = Bitmap.createBitmap(orc_bitmap, 0, 0, orc_bitmap.getWidth(),
                    orc_bitmap.getHeight(), m, true);

            file = BaseUtils.saveBitmapFile(orc_bitmap, file.getName());
            uri = BaseUtils.getUriForFile(context, file);
        }

        return uri;
    }

    /***
     * 获取url 指定name的value;
     *
     * @param url
     * @param name
     * @return
     */
    public static String getUrlValueByName(String url, String name) {
        String result;
        Uri uri = Uri.parse(url);
        result = uri.getQueryParameter(name);
        try {
            result = URLDecoder.decode(result, Constants.DEFAULT_ENCODE_TYPE);
        } catch (Exception e) {
        }
        return result;
    }

    /***
     * 构造url 指定name的value;
     *
     * @param baseUrl
     * @param params
     * @return
     */
    public static String buildUrl(String baseUrl, HashMap<String, String> params) {
        String result;
        String decode = null;
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                String value = entry.getValue();
                if (StringUtils.equalsNull(value)) {
                    value = "";
                }
                builder.appendQueryParameter(entry.getKey(), URLEncoder.encode(value, Constants.DEFAULT_ENCODE_TYPE));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        result = builder.toString();
        try {
            decode = java.net.URLDecoder.decode(result, Constants.DEFAULT_ENCODE_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decode;
    }

    /***
     * 构造url 指定name的value;
     *
     * @param baseUrl
     * @param params
     * @return
     */
    public static String buildUrl2(String baseUrl, HashMap<String, String> params) {
        String result = baseUrl;
        String splid;
        if (baseUrl.contains("?")) {
            splid = "&";
        } else {
            splid = "?";
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                String value = entry.getValue();
                if (StringUtils.equalsNull(value)) {
                    value = "";
                }
                result += splid + entry.getKey() + "=" + URLEncoder.encode(value, Constants.DEFAULT_ENCODE_TYPE);
                splid = "&";
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    public static boolean deleteFile(File file) {
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 版本更新
     *
     * @param mActivity
     */
    public static void checkUpgrade(final BaseActivity mActivity, final UpgradeBean mUpgradeBean) {
        if (mUpgradeBean == null || mUpgradeBean.getApp_url() == null) {
            return;
        }
        if (mUpgradeBean.getApp_url().endsWith(".apk")) {
            mActivity.checkPermission(new BaseActivity.CheckPermListener() {
                @Override
                public void superPermission() {
                    switch (mUpgradeBean.getIs_update()) {
                        case Constants.UPGRADE_MUST:
                            DialogUtil.upgradeMustDialog(mActivity, mUpgradeBean.getApp_content(), mUpgradeBean.getApp_url());
                            break;
                        case Constants.UPGRADE_NOT_MUST:
                            DialogUtil.upgradeDialog(mActivity, mUpgradeBean.getApp_content(), mUpgradeBean.getApp_url());
                            break;
                    }

                }
            }, R.string.write_file, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            JumpAppOutUtil.jumpOutBrowser(mActivity, mUpgradeBean.getApp_url());
        }

    }

    /**
     * 判断是否都不为空
     *
     * @param objects
     * @return
     */
    public static boolean objectsHasNoNull(Object... objects) {
        boolean hasNoNullObj = true;
        try {
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] == null) {
                    hasNoNullObj = false;
                    break;
                }
            }
        } catch (Exception e) {
            hasNoNullObj = false;
        }
        return hasNoNullObj;
    }

    public static String inputStream2String(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(Constants.DEFAULT_ENCODE_TYPE);
    }

    public static String inputStream2String2(InputStream in) {
        String string = "";
        try {
            string = CharStreams.toString(new InputStreamReader(in, Constants.DEFAULT_ENCODE_TYPE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * 获取手机通讯录
     *
     * @param context
     * @return
     * @throws Throwable
     */
    public static List<PhoneContactBean> getPhoneContacts(Context context) {
        //联系人集合
        List<PhoneContactBean> data = new ArrayList<>();

        //key: contactId,value: 该contactId在联系人集合data的index
        HashMap<Integer, PhoneContactBean> contactIdMap = new HashMap<>();

        ContentResolver resolver = context.getContentResolver();
        //搜索字段
        String[] phoneProjection = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.Contacts.DISPLAY_NAME
        };
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                phoneProjection, null, null, null);
        if (phoneCursor != null) {
            int num = 0;
            while (phoneCursor.moveToNext()) {
                num++;
                if (num > Constants.MAX_ADD_USER_CONTACTS) {
                    break;
                }
                int contactId = 0;
                String name = "", replace = "", phoneNumber = "";
                try {
                    //获取联系人的ID
                    contactId = phoneCursor.getInt(0);
                    //获取联系人的号码
                    phoneNumber = phoneCursor.getString(1);
                    //获取联系人的姓名
                    name = phoneCursor.getString(2);
                    //号码处理
                    replace = phoneNumber.replace(" ", "").replace("-", "").replace("+", "");

                } catch (Exception e) {

                }
                //如果联系人Map已经包含该contactId
                if (contactIdMap.containsKey(contactId)) {
                    //重新设置号码数组
                    PhoneContactBean contacts = contactIdMap.get(contactId);
                    if (contacts.getPhone() == null) {
                        contacts.setPhone(new ArrayList<String>());
                    }
                    contacts.getPhone().add(replace);

                } else {
                    //如果联系人Map不包含该contactId
                    PhoneContactBean contacts = new PhoneContactBean();

                    contacts.setName(name);

                    List<String> phones = new ArrayList<>();
                    phones.add(replace);
                    contacts.setPhone(phones);

                    contactIdMap.put(contactId, contacts);
                }

            }
            phoneCursor.close();
        }

        //搜索字段
        String[] emailProjection = new String[]{
                ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                ContactsContract.CommonDataKinds.Email.ADDRESS
        };
        // 获取手机联系人
        Cursor emailCursor = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                emailProjection, null, null, null);
        if (emailCursor != null) {
            //key: contactId,value: 该contactId在联系人集合data的index
            int num = 0;
            while (emailCursor.moveToNext()) {
                num++;
                if (num > Constants.MAX_ADD_USER_CONTACTS) {
                    break;
                }
                int contactId = 0;
                String emailAddress = "";
                try {
                    //获取联系人的ID
                    contactId = emailCursor.getInt(0);
                    //获取联系人的邮箱
                    emailAddress = emailCursor.getString(1);

                } catch (Exception e) {

                }
                //如果联系人Map已经包含该contactId
                if (contactIdMap.containsKey(contactId)) {
                    //重新设置号码数组
                    PhoneContactBean contacts = contactIdMap.get(contactId);
                    if (contacts.getEmail() == null) {
                        contacts.setEmail(new ArrayList<String>());
                    }
                    contacts.getEmail().add(emailAddress);

                } else {
                    //如果联系人Map不包含该contactId
                    PhoneContactBean contacts = new PhoneContactBean();

                    List<String> emails = new ArrayList<>();
                    emails.add(emailAddress);
                    contacts.setEmail(emails);

                    contactIdMap.put(contactId, contacts);
                }

            }
            emailCursor.close();
        }

        //搜索字段
        String[] otherProjection = new String[]{
                ContactsContract.CommonDataKinds.Organization.CONTACT_ID,
                ContactsContract.CommonDataKinds.Organization.DATA,
                ContactsContract.CommonDataKinds.Organization.TITLE
        };
        // 获取手机联系人
        Cursor otherCursor = resolver.query(ContactsContract.Data.CONTENT_URI,
                otherProjection, null, null, null);
        if (otherCursor != null) {
            //key: contactId,value: 该contactId在联系人集合data的index
            int num = 0;
            while (otherCursor.moveToNext()) {
                num++;
                if (num > Constants.MAX_ADD_USER_CONTACTS) {
                    break;
                }

                int contactId = 0;
                String company = "", title = "";
                try {
                    //获取联系人的ID
                    contactId = otherCursor.getInt(0);
                    company = otherCursor.getString(1);
                    title = otherCursor.getString(2);

                } catch (Exception e) {

                }
                //如果联系人Map已经包含该contactId
                if (contactIdMap.containsKey(contactId)) {
                    //重新设置号码数组
                    PhoneContactBean contacts = contactIdMap.get(contactId);
                    contacts.setCompany(company);
                    contacts.setTitle(title);

                } else {
                    //如果联系人Map不包含该contactId
                    PhoneContactBean contacts = new PhoneContactBean();

                    contacts.setCompany(company);
                    contacts.setTitle(title);

                    contactIdMap.put(contactId, contacts);
                }

            }
            otherCursor.close();
        }

        data.addAll(contactIdMap.values());

        return data;
    }


    /**
     * 保存string到sdk
     *
     * @param string
     * @return
     */
    public static String saveStringToSD(String string, String saveFileName) {
        String fileName = null;
        StringBuffer sb = new StringBuffer();

        sb.append(string);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(Environment.getExternalStorageDirectory().toString() + Constants.SAVE_FILE_NAME);
            if (!dir.exists()) {
                dir.mkdir();
            }

            try {
                fileName = dir.toString() + File.separator + saveFileName;
                FileOutputStream fos = new FileOutputStream(fileName);
                fos.write(sb.toString().getBytes());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return fileName;

    }

    /**
     * 读sdk
     *
     * @param fileName
     * @return
     */
    public static String readFileSdcardFile(String fileName) throws IOException {
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);

            int length = fin.available();

            byte[] buffer = new byte[length];
            fin.read(buffer);

            res = EncodingUtils.getString(buffer, "UTF-8");

            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    public static byte[] File2byte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static File byte2File(byte[] buf, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }

    public static String formatByte(long value) {
        String string;
        if (value < 1024) {
            string = value + "B";
        } else if (value < 1024 * 1024) {
            string = BigDecimalUtils.divide(new BigDecimal(value), new BigDecimal(1024), 2).toPlainString() + "K";
        } else if (value < 1024 * 1024 * 1024) {
            string = BigDecimalUtils.divide(new BigDecimal(value), new BigDecimal(1024)).toPlainString();
            string = BigDecimalUtils.divide(new BigDecimal(string), new BigDecimal(1024), 2).toPlainString() + "K";
        } else {
            string = BigDecimalUtils.divide(new BigDecimal(value), new BigDecimal(1024)).toPlainString();
            string = BigDecimalUtils.divide(new BigDecimal(string), new BigDecimal(1024)).toPlainString();
            string = BigDecimalUtils.divide(new BigDecimal(string), new BigDecimal(1024), 2).toPlainString() + "G";
        }
        return string;

    }

    public static void AddUserContacts(final BaseActivity baseActivity, final boolean ifToast, final boolean ifJump) {
        baseActivity.checkPermission(new BaseActivity.CheckPermListener() {
            @Override
            public void superPermission() {
                new AddUserContactsTask(baseActivity, ifToast, ifJump).execute();
            }
        }, baseActivity.getString(R.string.allow_jurisdiction_to_bind_phone), Manifest.permission.READ_CONTACTS);
    }
}
