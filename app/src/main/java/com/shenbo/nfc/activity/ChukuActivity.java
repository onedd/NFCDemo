package com.shenbo.nfc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.shenbo.nfc.MainActivity;
import com.shenbo.nfc.R;
import com.shenbo.nfc.utils.ApiModel;
import com.shenbo.nfc.utils.CardUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChukuActivity extends Activity {
    private ListView lv;
    private Button btn_sm,btn_tj;
    private EditText et_bh;
    private List<String> list = new ArrayList<>();
    private ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chuku);
        initView();
        handler = new Handler(Looper.myLooper());
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        lv.setAdapter(adapter);
    }

    private Handler handler ;

    private void initView() {
        lv = (ListView) findViewById(R.id.ck_lv);
        btn_sm = (Button) findViewById(R.id.btn_ck_tm);
        btn_tj = (Button) findViewById(R.id.btn_ck_sc);
        et_bh = (EditText) findViewById(R.id.et_bh);
        btn_sm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() > 11){
                    Toast.makeText(ChukuActivity.this,"单次最多传10条",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent1 = new Intent(ChukuActivity.this, LogicActivity.class);
                intent1.putExtra(CardUtil.TAG_FROM, CardUtil.TAG_READ);
                startActivityForResult(intent1, 0);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                list.remove(position);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        btn_tj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String bh = et_bh.getText().toString().trim();
               if (TextUtils.isEmpty(bh)){
                   Toast.makeText(ChukuActivity.this,"门店编号不能为空",Toast.LENGTH_SHORT).show();
                   return;
               }
                if (list.size() == 0){
                    Toast.makeText(ChukuActivity.this,"请写入平板ID",Toast.LENGTH_SHORT).show();
                    return;
                }
               out_put(bh);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == CardUtil.TAG_READ ) {
                String metoInfo = data.getStringExtra(CardUtil.TAG_CARD_NO).trim();
                if (list.contains(metoInfo)){
                    Toast.makeText(ChukuActivity.this,"已录入",Toast.LENGTH_SHORT).show();
                    return;
                }
                list.add(metoInfo);
                adapter.notifyDataSetChanged();
            }

        }
    }

    private void out_put(String bh){
        ApiModel.out_put(bh, list, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChukuActivity.this,"网络请求失败，请重试",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resultStr = response.body().string();
                try {
                    JSONObject json = new JSONObject(resultStr);
                    final String code = json.optString("code");
                    final String errorMsg = json.optString("msg");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChukuActivity.this,errorMsg,Toast.LENGTH_SHORT).show();
                            if (code.equals("success")){
                                list.clear();
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
