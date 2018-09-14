package oneapp.onechat.chat.network;

/**
 * Created by 何帅 on 2016/7/2.
 */

import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneOpenHelper;
import oneapp.onechat.oneandroid.graphenechain.interfaces.ProgressRequestListener;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.NetUtils;
import oneapp.onechat.oneandroid.onewallet.modle.Result;
import oneapp.onechat.oneandroid.onewallet.modle.ServiceUrlBean;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.LogUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;

/**
 * Created by zhy on 15/8/17.
 */
public class OkHttpClientManager {
    private static OkHttpClientManager mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private Gson mGson;
    private static final OkHttpClient.Builder builder = new OkHttpClient.Builder();

    private static final String TAG = "OkHttpClientManager";

    private OkHttpClientManager() {
        //cookie enabled

        builder.connectTimeout(ServiceConstants.HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS).cookieJar(new CookieJar() {
            private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url, cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url);
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        }).cache(new Cache(new File(OneAccountHelper.getContext().getCacheDir(), "http_cache"), 1024 * 1024 * 100));
//        builder.addInterceptor(new GzipRequestInterceptor());//gzip
        mOkHttpClient = builder.build();
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }

    public static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(ServiceUrlBean url, final ResultCallback callback, Param[] params, boolean ifCheckCode, boolean ifMainTask) {
        Request request = buildPostRequest(url.getHost_url(), params);
        deliveryResult(url.getService_key(), callback, request, ifCheckCode, ifMainTask);
    }

    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(ServiceUrlBean url, final ResultCallback callback, Param[] params, boolean ifCheckCode) {
        Request request = buildPostRequest(url.getHost_url(), params);
        deliveryResult(url.getService_key(), callback, request, ifCheckCode, true);
    }

    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(ServiceUrlBean url, final ResultCallback callback, Param[] params) {
        Request request = buildPostRequest(url.getHost_url(), params);
        deliveryResult(url.getService_key(), callback, request);
    }

    /**
     * 异步的get请求
     *
     * @param url
     * @param callback
     */
    private void _getAsyn(ServiceUrlBean url, final ResultCallback callback, HashMap<String, String> params, boolean ifAddPublicParam, boolean ifMainTask) {
        Request request = buildGetRequest(url.getHost_url(), params, ifAddPublicParam);
        deliveryResult(url.getService_key(), callback, request, true, ifMainTask);
    }

    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(ServiceUrlBean url, final ResultCallback callback, Map<String, String> params) {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(url.getHost_url(), paramsArr);
        deliveryResult(url.getService_key(), callback, request);
    }


    /**
     * 同步基于post的文件上传
     *
     * @param params
     * @return
     */
    private Response _post(String url, File[] files, String[] fileKeys, Param[] params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, null, params);
        return mOkHttpClient.newCall(request).execute();
    }

    private Response _post(String url, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null, null);
        return mOkHttpClient.newCall(request).execute();
    }

    private Response _post(String url, File file, String fileKey, Param[] params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null, params);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 异步基于post的文件上传
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     * @throws IOException
     */
    private void _postAsyn(ServiceUrlBean url, ResultCallback callback, File[] files, String[] fileKeys, Param[] params) throws IOException {
        Request request = buildMultipartFormRequest(url.getHost_url(), files, fileKeys, null, params);
        deliveryResult(url.getService_key(), callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件不带参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @throws IOException
     */
    private void _postAsyn(ServiceUrlBean url, ResultCallback callback, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url.getHost_url(), new File[]{file}, new String[]{fileKey}, null, null);
        deliveryResult(url.getService_key(), callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @param params
     * @throws IOException
     */
    private void _postAsyn(ServiceUrlBean url, ResultCallback callback, File file, String fileKey, Param[] params) throws IOException {
        Request request = buildMultipartFormRequest(url.getHost_url(), new File[]{file}, new String[]{fileKey}, null, params);
        deliveryResult(url.getService_key(), callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @param params
     * @throws IOException 带进度
     */
    private void _postAsyn(ServiceUrlBean url, ResultCallback callback, File file, String fileKey, ProgressRequestListener progressRequestListener, Param[] params) throws IOException {
        Request request = buildMultipartFormRequest(url.getHost_url(), new File[]{file}, new String[]{fileKey}, progressRequestListener, params);
        deliveryResult(url.getService_key(), callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     * @param params
     * @throws IOException 带进度
     */
    private void _postAsyn(ServiceUrlBean url, ResultCallback callback, File[] files, String[] fileKeys, ProgressRequestListener progressRequestListener, Param[] params) throws IOException {
        Request request = buildMultipartFormRequest(url.getHost_url(), files, fileKeys, progressRequestListener, params);
        deliveryResult(url.getService_key(), callback, request);
    }

    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     * @param callback
     */
    private void _downloadAsyn(final String url, final String destFileDir, final ResultCallback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedStringCallback(request, e, true, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(destFileDir, getFileName(url));
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    sendSuccessResultCallback(file.getAbsolutePath(), true, callback);
                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, true, callback);
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                    }
                }

            }
        });
    }

    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    private void setErrorResId(final ImageView view, final int errorResId) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                view.setImageResource(errorResId);
            }
        });
    }


    //*************对外公布的方法************

    public static void getAsyn(ServiceUrlBean url, ResultCallback callback, HashMap<String, String> params) {
        getInstance()._getAsyn(url, callback, params, true, true);
    }

    public static void getAsyn(ServiceUrlBean url, ResultCallback callback, HashMap<String, String> params, boolean ifAddPublicParam, boolean ifMainTask) {
        getInstance()._getAsyn(url, callback, params, ifAddPublicParam, ifMainTask);
    }

    public static void postAsyn(ServiceUrlBean url, final ResultCallback callback, Param[] params, boolean ifCheckCode, boolean ifMainTask) {
        getInstance()._postAsyn(url, callback, params, ifCheckCode, ifMainTask);
    }

    public static void postAsyn(ServiceUrlBean url, final ResultCallback callback, Param[] params, boolean ifCheckCode) {
        getInstance()._postAsyn(url, callback, params, ifCheckCode);
    }

    public static void postAsyn(ServiceUrlBean url, final ResultCallback callback, Param[] params) {
        getInstance()._postAsyn(url, callback, params);
    }


    public static void postAsyn(ServiceUrlBean url, final ResultCallback callback, Map<String, String> params) {
        getInstance()._postAsyn(url, callback, params);
    }


    public static Response post(String url, File[] files, String[] fileKeys, Param[] params) throws IOException {
        return getInstance()._post(url, files, fileKeys, params);
    }

    public static Response post(String url, File file, String fileKey) throws IOException {
        return getInstance()._post(url, file, fileKey);
    }

    public static Response post(String url, File file, String fileKey, Param[] params) throws IOException {
        return getInstance()._post(url, file, fileKey, params);
    }

    public static void postAsyn(ServiceUrlBean url, ResultCallback callback, File[] files, String[] fileKeys, Param[] params) throws IOException {
        getInstance()._postAsyn(url, callback, files, fileKeys, params);
    }

    public static void postAsyn(ServiceUrlBean url, ResultCallback callback, File[] files, String[] fileKeys, ProgressRequestListener progressRequestListener, Param[] params) throws IOException {
        getInstance()._postAsyn(url, callback, files, fileKeys, progressRequestListener, params);
    }


    public static void postAsyn(ServiceUrlBean url, ResultCallback callback, File file, String fileKey) throws IOException {
        getInstance()._postAsyn(url, callback, file, fileKey);
    }


    public static void postAsyn(ServiceUrlBean url, ResultCallback callback, File file, String fileKey, Param[] params) throws IOException {
        getInstance()._postAsyn(url, callback, file, fileKey, params);
    }

    //带进度
    public static void postAsyn(ServiceUrlBean url, ResultCallback callback, File file, String fileKey, ProgressRequestListener progressRequestListener, Param[] params) throws IOException {
        getInstance()._postAsyn(url, callback, file, fileKey, progressRequestListener, params);
    }

    public static void downloadAsyn(ServiceUrlBean url, String destDir, ResultCallback callback) {
        getInstance()._downloadAsyn(url.getHost_url(), destDir, callback);
    }

    //****************************


    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private Param[] validateParam(Param[] params) {
        if (params == null)
            return new Param[0];
        else return params;
    }

    private Param[] map2Params(Map<String, String> params) {
        if (params == null) return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }

    private static final String SESSION_KEY = "Set-Cookie";
    private static final String mSessionKey = "JSESSIONID";

    private Map<String, String> mSessions = new HashMap<String, String>();

    /**
     * 取消所有接口
     *
     * @return
     */
    public void cancelAll() {
        try {
            mOkHttpClient.dispatcher().cancelAll();
        } catch (Exception e) {

        }
    }

    private void deliveryResult(final String serviceKey, final ResultCallback callback, final Request request) {
        deliveryResult(serviceKey, callback, request, true, true);
    }

    private void deliveryResult(final String serviceKey, final ResultCallback callback, final Request request, final boolean ifCheckCode, final boolean ifMainTask) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!NetUtils.hasNetwork(OneAccountHelper.getContext())) {
//                    ToastUtils.simpleToast(R.string.network_unavailable);
                } else if (!StringUtils.equalsNull(serviceKey)) {
                    ServiceConstants.ResetServiceByKey(serviceKey);
                }
                sendFailedStringCallback(request, e, serviceKey, ifMainTask, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
//                    final String string = response.body().string();
                    //改为流式传输，防止OOM
                    final String string = BaseUtils.inputStream2String(response.body().byteStream());
                    if (!StringUtils.equalsNull(string)) {
                        LogUtils.d("http-->", string);
                        if (callback.mType == String.class) {
                            sendSuccessResultCallback(string, ifMainTask, callback);
                        } else {
                            Result result = mGson.fromJson(string, callback.mType);
                            if (ifCheckCode) {
                                boolean b = OneOpenHelper.checkResultCode(result);
//                                if (result.getCode() == 100800 || result.getCode() == 100900) {
//                                    String url = response.toString();
//                                    LogUtils.d(url);
//                                }
                            }
                            sendSuccessResultCallback(result, ifMainTask, callback);
                        }
                    } else {
                        sendFailedStringCallback(response.request(), new NullPointerException("string null"), ifMainTask, callback);
                    }
                } catch (com.google.gson.JsonParseException e)//Json解析的错误
                {
                    sendFailedStringCallback(response.request(), e, ifMainTask, callback);
                } catch (Exception e) {
                    sendFailedStringCallback(response.request(), e, ifMainTask, callback);
                }
            }
        });
    }

    private void sendFailedStringCallback(final Request request, final Exception e, boolean ifMainTask, final ResultCallback callback) {
        sendFailedStringCallback(request, e, null, ifMainTask, callback);
    }

    private void sendFailedStringCallback(final Request request, final Exception e, final String serviceKey, boolean ifMainTask, final ResultCallback callback) {
        if (ifMainTask) {

            mDelivery.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (callback != null)
                            callback.onError(request, e);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            });
        } else {
            try {
                if (callback != null)
                    callback.onError(request, e);
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
    }

    private void sendSuccessResultCallback(final Object object, boolean ifMainTask, final ResultCallback callback) {
        if (ifMainTask) {

            mDelivery.post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        try {
                            callback.onResponse(object);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        } else {
            if (callback != null) {
                try {
                    callback.onResponse(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private Request buildPostRequest(String url, Param[] params) {
        FormBody.Builder builder = new FormBody.Builder();

        boolean ifHasParams = false;
        if (params != null) {
            for (Param param : params) {
                String value = param.value;
                if (StringUtils.equalsNull(value)) {
                    value = "";
                }
                builder.add(param.key, value);
                ifHasParams = true;
            }

        }

        url = BaseUtils.buildUrl(url, ServiceConstants.GetHttpPublicParams());

        if (ifHasParams) {
            RequestBody requestBody = builder.build();
            return new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
        } else {
            return new Request.Builder()
                    .url(url)
                    .build();
        }
    }

    private Request buildPostRequest2(String url, Param[] params) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        boolean ifHasParams = false;
        if (params != null) {
            for (Param param : params) {
//                if (StringUtils.equalsNull(param.key)) {
//                    builder.addPart(Headers.of("Content-Type", "application/json"),
//                            RequestBody.create(null, param.value));
//                } else
                String value = param.value;
                if (StringUtils.equalsNull(value)) {
                    value = "";
                }
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                        RequestBody.create(null, value));
                ifHasParams = true;
            }

        }
//        for (Map.Entry<String, String> entry : ServiceConstants.SERVICE_PUBLIC_PARAMS.entrySet()) {
//            if (!StringUtils.equalsNull(entry.getValue()))
//                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
//                        RequestBody.create(null, entry.getValue()));
//        }

        url = BaseUtils.buildUrl(url, ServiceConstants.GetHttpPublicParams());

        if (ifHasParams) {
            RequestBody requestBody = builder.build();
            return new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
        } else {
            return new Request.Builder()
                    .url(url)
                    .build();
        }
    }


    //无参请求
    private Request buildGetRequest(String url, HashMap<String, String> params, boolean ifAddPublicParam) {
        if (params == null) {
            params = new HashMap<>();
        }
        if (ifAddPublicParam) {
            params.putAll(ServiceConstants.GetHttpPublicParams());
        }
        url = BaseUtils.buildUrl(url, params);

        return new Request.Builder()
                .url(url)
                .build();
    }

    //
//    //返回进度
//
//
    private Request buildMultipartFormRequest(String url, File[] files,
                                              String[] fileKeys, ProgressRequestListener progressRequestListener, Param[] params) {
        params = validateParam(params);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        if (params != null)
            for (Param param : params) {
                if (!StringUtils.equalsNull(param.value)) {
                    builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                            RequestBody.create(null, param.value));
                }
            }
        if (files != null) {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                //TODO 根据文件名设置contentType
                builder.addFormDataPart(fileKeys[i], fileName,
                        fileBody);
            }
        }

        url = BaseUtils.buildUrl(url, ServiceConstants.GetHttpPublicParams());

        RequestBody requestBody = builder.build();
        if (progressRequestListener != null)
            return new Request.Builder()
                    .url(url)
                    .post(new ProgressRequestBody(requestBody, progressRequestListener))
                    .build();
        else
            return new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
    }


    public static abstract class ResultCallback<T> {
        Type mType;

        public ResultCallback() {
            mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(T response);
    }

    public static class Param {
        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }


}

class GzipRequestInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
            return chain.proceed(originalRequest);
        }

        Request compressedRequest = originalRequest.newBuilder()
                .header("Content-Encoding", "gzip")
                .method(originalRequest.method(), gzip(originalRequest.body()))
                .build();
        return chain.proceed(compressedRequest);
    }

    private RequestBody gzip(final RequestBody body) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return body.contentType();
            }

            @Override
            public long contentLength() {
                return -1; // We don't know the compressed length in advance!
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                body.writeTo(gzipSink);
                gzipSink.close();
            }
        };
    }


}
