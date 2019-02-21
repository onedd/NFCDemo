package com.shenbo.nfc.utils;

import android.util.Log;
import android.widget.EditText;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OkHttpUtils {

    private static final OkHttpClient okHttpClient = new OkHttpClient();
    static {
        okHttpClient.newBuilder().connectTimeout(30*1000, TimeUnit.SECONDS);
    }
    private static final MediaType JSON = MediaType.parse("application/json;chartset=utf-8"); //post 提交数据是以Json格式提交
    //TODO 同步请求   get post
    //TODO 一步请求   get post
    //TODO  写一个接口 请求成功后调用成功或者失败的接口

    /**
     * 同步Get请求方式
     */
    public static String _synchronizeGet(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response  = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    /**
     * 异步Get请求方式
     */
    public static void _asynchronousGet(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    /**
     * 同步post请求
     */
    public static String _synchronizePost(String url, String json) throws IOException {
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody).build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    /**
     * 异步Post请求
     */
    public static void _asynchronousPost(String url, String json, Callback callback) {
        Log.e("post","接口URL:"+url+"\t"+"参数："+json);
        RequestBody requestBody = RequestBody.create(JSON,json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }


    /**异步下载文件*/
    public static void _downloadAsyn(String url, Callback callback){
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    /**未封装取消网络请求*/
    public static void _cancelResquest(String tag){

    }
}
