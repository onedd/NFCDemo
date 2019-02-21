package com.shenbo.nfc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shenbo.nfc.activity.ChukuActivity;
import com.shenbo.nfc.activity.CreateActivity;
import com.shenbo.nfc.activity.LogicActivity;
import com.shenbo.nfc.activity.UpdateActivity;
import com.shenbo.nfc.utils.CardUtil;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    Button btn_select, btn_sell, btn_ck, btn_charge;
    TextView tv_text;

    private String cardNo = null;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_main);
        btn_select = (Button) findViewById(R.id.btn_select);
        btn_sell = (Button) findViewById(R.id.btn_sell);
        btn_ck = (Button) findViewById(R.id.btn_ck);
        btn_charge = (Button) findViewById(R.id.btn_charge);
        tv_text = (TextView) findViewById(R.id.tv_text);

        btn_select.setOnClickListener(this);
        btn_sell.setOnClickListener(this);
        btn_ck.setOnClickListener(this);
        btn_charge.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select://查询
                Intent intent1 = new Intent(MainActivity.this, LogicActivity.class);
                intent1.putExtra(CardUtil.TAG_FROM, CardUtil.TAG_READ);
                startActivityForResult(intent1, 0);
                break;
            case R.id.btn_sell://制卡
                Intent intent2 = new Intent(MainActivity.this, CreateActivity.class);
                intent2.putExtra(CardUtil.TAG_FROM, CardUtil.TAG_SELL);
                startActivityForResult(intent2, 0);
                break;

            case R.id.btn_charge://替换
                Intent intent3 = new Intent(MainActivity.this, UpdateActivity.class);
                intent3.putExtra(CardUtil.TAG_FROM, CardUtil.TAG_CHARGE);
                startActivityForResult(intent3, 0);
                break;
            case R.id.btn_ck://出库
                Intent intent4 = new Intent(MainActivity.this, ChukuActivity.class);
                startActivity(intent4);
                break;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String metoInfo = "";
            switch (resultCode) {
                case CardUtil.TAG_CHARGE://替换
                    metoInfo = data.getStringExtra(CardUtil.TAG_CARD_MAKE);
                    break;
                case CardUtil.TAG_READ://读卡
                    cardNo = data.getStringExtra(CardUtil.TAG_CARD_NO);
                    metoInfo = "平板ID:" + cardNo;
                    break;
                case CardUtil.TAG_SELL://制卡
                    if (data.getStringExtra(CardUtil.TAG_CARD_MAKE).equals("0")) {
                        metoInfo = "制卡成功！";
                    } else {
                        metoInfo = "制卡失败！";
                    }
                    break;
            }
            Log.i(TAG, metoInfo);
            tv_text.setText(metoInfo);
        }
    }
}
