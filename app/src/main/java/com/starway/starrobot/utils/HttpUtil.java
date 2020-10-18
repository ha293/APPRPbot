package com.starway.starrobot.utils;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by iBelieve on 2018/5/3.
 * HTTP简易工具类（基于okhttp）
 */

public class HttpUtil implements CookieJar {

    private static HttpUtil httpUtil;
    private HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private OkHttpClient httpClient;
    private Gson gson=new Gson();

    private HttpUtil() {
        httpClient = new OkHttpClient
                .Builder()
                .cookieJar(this)
                .connectTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build();
    }

    /**
     * GET请求（原始okhttp数据）
     * @param url
     * @param callback
     */
    public void get(String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(callback);
    }

    /**
     * GET请求（JSON）
     * @param url 请求地址
     * @param callback 结果回调
     */
    public void get(final String url, final ObjectCallback callback) {

        cookieStore.clear();

        Request request = new Request.Builder().url(url).build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String str = response.body().string();
                    System.out.println("请求Url：" + url);
                    System.out.println(cookieStore);
                    System.out.println("返回结果：" + str);
                    Object obj = gson.fromJson(str, callback.type);
                    callback.onResponse(call, obj);
                }catch (Exception e){
                    callback.onFailure(call, e);
                }
            }
        });
    }

    /**
     * GET请求（JSON、阻塞)
     * @param url 请求地址
     * @param type 返回对象类型
     * @param <T>
     * @return
     */
    public <T>T _get(String url,Class<T> type){
        Request request = new Request.Builder().url(url).build();
        try {
            String str = httpClient.newCall(request).execute().body().string();
            return gson.fromJson(str, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取httputil工具类单例
     * @return
     */
    public static HttpUtil getInstance() {
        if (httpUtil == null) {
            httpUtil = new HttpUtil();
        }
        return httpUtil;
    }

    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
        cookieStore.put(httpUrl.host(), list);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        List<Cookie> cookies = cookieStore.get(httpUrl.host());
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }

    public static abstract class ObjectCallback<T> {

        private Type type;

        public ObjectCallback() {
            type = getSuperclassTypeParameter(getClass());
        }

        /**
         * 请求或JSON反序列化失败结果（可不实现）
         * @param call
         * @param e
         */
        public void onFailure(Call call, Exception e){
            e.printStackTrace();
        }

        /**
         * 请求成功结果（必须实现）
         * @param call
         * @param obj
         */
        public abstract void onResponse(Call call, T obj);

        /**
         * 获取泛型T的类型
         * @param subclass
         * @return
         */
        private static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing <T> parameter.");
            }
            ParameterizedType parameterizedType = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[0]);
        }
    }
}
