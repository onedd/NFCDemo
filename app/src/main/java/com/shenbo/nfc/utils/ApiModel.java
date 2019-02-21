package com.shenbo.nfc.utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Callback;

public class ApiModel {
    public ApiModel() {
    }
    /**
     * 替换平板ID
     * */
    public static void set_tablet(String old_id,String new_id, Callback callback)   {
        JSONObject json = new JSONObject();
        try {
            json.put("old", old_id);
            json.put("new", new_id);
        } catch (JSONException e ) {
            e.printStackTrace();
        }
        OkHttpUtils._asynchronousPost(Url.BASE_URL + Url.SET_TABLET, json + "",callback);
    }

    /**
     * 平板出库
     * */
    public static void out_put(String sid, List<String> list, Callback callback)   {
        JSONArray s = new JSONArray(list);
        JSONObject json = new JSONObject();
        try {
            json.put("sid", sid);
            json.put("tablet", s);
        } catch (JSONException e ) {
            e.printStackTrace();
        }
        String json1 = json.toString().replace("\\/", "/");
        OkHttpUtils._asynchronousPost(Url.BASE_URL + Url.OUT_PUT, json1 + "",callback);

    }


    public static String settest(){
        try {
            return OkHttpUtils._synchronizeGet("http://oxnqfwpc3.bkt.clouddn.com/wifi.png");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
