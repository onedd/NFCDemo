package com.shenbo.nfc.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.shenbo.nfc.R;
import com.shenbo.nfc.utils.CardUtil;
import com.shenbo.nfc.utils.IcCard;
import com.shenbo.nfc.utils.MifareControl;

public class LogicActivity extends Activity {
	private static final String TAG = LogicActivity.class.getSimpleName();

	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private TextView mText;
	private int mFrom = -1;
	private String cardNo, cardType, cardCount, cardDate;
	private Intent mIntent = null;

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_logic);
		mText = (TextView) findViewById(R.id.text);
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		// Create a generic PendingIntent that will be deliver
		// to this activity. The NFC stack
		// will fill in the intent with the details of the
		// discovered tag before delivering to
		// this activity.
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		// Setup an intent filter for all MIME based dispatches
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		try {
			ndef.addDataType("*/*");
		} catch (IntentFilter.MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		mFilters = new IntentFilter[]{ndef,};
		// Setup a tech list for all MifareClassic tags
		mTechLists = new String[][]{new String[]{MifareClassic.class
				.getName()}};

		mFrom = getIntent().getIntExtra(CardUtil.TAG_FROM, -1);
		cardNo = getIntent().getStringExtra(CardUtil.TAG_CARD_NO);
//		cardType = getIntent().getStringExtra(CardUtil.TAG_CARD_TYPE);
//		cardCount = getIntent().getStringExtra(CardUtil.TAG_CARD_COUNT);
//		cardDate = getIntent().getStringExtra(CardUtil.TAG_CARD_DATE);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
				mTechLists);

		processIntent(mIntent);
	}

	@Override
	public void onNewIntent(Intent intent) {
		mIntent = intent;
	}

	private void processIntent(Intent intent){
		if (mFrom == -1){
			finish();
			return;
		}
		if (mIntent == null){
			return;
		}
		//取出封装在intent中的TAG
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		for (String tech : tagFromIntent.getTechList()) {
			System.out.println(tech);
		}

		MifareControl mc = new MifareControl(tagFromIntent);

		try {
			Intent intentResult = new Intent();
			switch (mFrom){
				case CardUtil.TAG_CHARGE://替换平板ID
					Log.e("LogicActivity", "修改");

					mc.updateRemainCount(cardNo, cardCount);
//					intentResult.putExtra(CardUtil.TAG_CARD_CHARGE, "0");
					setResult(CardUtil.TAG_CHARGE, intentResult);
					finish();

					break;
				case CardUtil.TAG_READ://读卡
					Log.e("LogicActivity", "读卡");
					intentResult.putExtra(CardUtil.TAG_CARD_NO, mc.readCardNo());
					setResult(CardUtil.TAG_READ, intentResult);
					finish();
					break;
				case CardUtil.TAG_SELL://写卡
					Log.e("LogicActivity", "写卡");

					IcCard icCardSell = new IcCard();
					icCardSell.card_no = cardNo;

					mc.makeCarCard(icCardSell);

					intentResult.putExtra(CardUtil.TAG_CARD_MAKE, "0");
					setResult(CardUtil.TAG_SELL, intentResult);
					finish();

					break;
				case -1:
					Log.i("LogicActivity", "-1");
					finish();
					break;
			}

			mc.close();

		} catch (Exception e) {
			Toast.makeText(LogicActivity.this, "读取卡失败", Toast.LENGTH_SHORT).show();
			finish();
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mAdapter.disableForegroundDispatch(this);
	}
}
