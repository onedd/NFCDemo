package com.shenbo.nfc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shenbo.nfc.MainActivity;
import com.shenbo.nfc.R;
import com.shenbo.nfc.utils.CardUtil;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

public class CreateActivity extends Activity {
    private EditText et;
    private Button btn_sm,btn_ok;
    private int mFrom = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        initView();
        mFrom = getIntent().getIntExtra(CardUtil.TAG_FROM, -1);
    }

    private void initView() {
        et = (EditText) findViewById(R.id.et_id);
        btn_sm = (Button) findViewById(R.id.btn_sm);
        btn_ok = (Button) findViewById(R.id.btn_qd);
        btn_sm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 1);
            }
        });
       btn_ok.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String trim = et.getText().toString().trim();
               if (TextUtils.isEmpty(trim)){
                   Toast.makeText(CreateActivity.this,"请输入平板ID",Toast.LENGTH_SHORT).show();
                   return;
               }
               if (mFrom != CardUtil.TAG_SELL){
                   return;
               }
               Intent intent = new Intent(CreateActivity.this, LogicActivity.class);
               intent.putExtra(CardUtil.TAG_CARD_NO, trim);
               intent.putExtra(CardUtil.TAG_FROM, mFrom);
               startActivityForResult(intent, 0);
           }
       });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if (data != null){
                    setResult(CardUtil.TAG_SELL, data);
                    finish();
                }
                break;
            case 1:
                if (data != null) {
                    String content = data.getStringExtra(Constant.CODED_CONTENT);
                    Toast.makeText(CreateActivity.this,"扫描内容："+content,Toast.LENGTH_SHORT).show();
                    et.setText(content);
                }
                break;
        }
    }
}
