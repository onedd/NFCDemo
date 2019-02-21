package com.shenbo.nfc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.shenbo.nfc.MainActivity;
import com.shenbo.nfc.R;
import com.shenbo.nfc.utils.ApiModel;
import com.shenbo.nfc.utils.CardUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.shenbo.nfc.R.id.btn_sm2;

public class UpdateActivity extends Activity {
    private Button btn_sm1, btn_sm2, btn_ok;
    private EditText et1, et2;
    private int mFrom = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        initView();
        mFrom = getIntent().getIntExtra(CardUtil.TAG_FROM, -1);

    }

    private void initView() {
        btn_sm1 = (Button) findViewById(R.id.btn_sm1);
        btn_sm2 = (Button) findViewById(R.id.btn_sm2);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        et1 = (EditText) findViewById(R.id.et_id1);
        et2 = (EditText) findViewById(R.id.et_id2);
        final Intent intent1 = new Intent(UpdateActivity.this, LogicActivity.class);
        intent1.putExtra(CardUtil.TAG_FROM, CardUtil.TAG_READ);
        btn_sm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent1, 1);
            }
        });
        btn_sm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent1, 2);
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpdate();
//                String settest = ApiModel.settest();
//                Log.e("settest",settest);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String cardNo = data.getStringExtra(CardUtil.TAG_CARD_NO);
            switch (requestCode) {
                case 1:
                    et1.setText(cardNo);
                    break;
                case 2:
                    et2.setText(cardNo);
                    break;
            }
        }
    }

    public void setUpdate() {
        String ole_id = et1.getText().toString().trim();
        String new_id = et2.getText().toString().trim();
        if (TextUtils.isEmpty(ole_id)) {
            Toast.makeText(UpdateActivity.this, "旧平板ID不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(new_id)) {
            Toast.makeText(UpdateActivity.this, "新平板ID不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mFrom != CardUtil.TAG_CHARGE) {
            return;
        }
        ApiModel.set_tablet(ole_id, new_id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Intent intent = new Intent();
                intent.putExtra(CardUtil.TAG_CARD_MAKE, "网络连接错误，替换失败，请重试！");
                setResult(CardUtil.TAG_CHARGE, intent);
                finish();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resultStr = response.body().string();
                try {
                    JSONObject json = new JSONObject(resultStr);
                    String errorMsg = json.optString("msg");
                    Intent intent = new Intent();
                    intent.putExtra(CardUtil.TAG_CARD_MAKE, errorMsg);
                    setResult(CardUtil.TAG_CHARGE, intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
