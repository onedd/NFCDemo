package com.shenbo.nfc.utils;

import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.text.TextUtils;
import android.util.Log;

import com.shenbo.nfc.Converter;

import java.io.IOException;

public class MifareControl {
    private MifareClassic mfc;
    //	private static final String key = "5A5445494F54";
//	private static final String key = "FFFFFFFFFFFF";
    private static final int SECTOR0 = 0;
    private static final int SECTOR1 = 1;
    private static final int BLOCK_ZERO = 0;
    private static final int BLOCK_ONE = 1;
    private static final int BLOCK_TWO = 2;
    private static final int BLOCK_THREE = 3;
    private static final int BLOCK_FOR = 4;

    public MifareControl(Tag tag) {
        try {
            mfc = MifareClassic.get(tag);
            mfc.connect();
        } catch (IOException e) {
            Log.e("mifareControl", "mfc.connect()失败");
            e.printStackTrace();
        }
    }

    /**
     * 判断卡片是否为白卡
     *
     * @return true or false
     */
    public boolean isBlankCard() {
        try {
            if (mfc.authenticateSectorWithKeyA(SECTOR1, MifareClassic.KEY_DEFAULT)) {

            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 读取IC卡序列编号(空白编号) B035EC46 卡片的出场信息
     *
     * @return 卡片出场编号
     * @throws Exception
     */
  /*  public String readDefaultCarCardNo() throws Exception {
        String cardNo;
        if (mfc.authenticateSectorWithKeyA(SECTOR0, MifareClassic.KEY_DEFAULT)) {
            byte[] data = mfc.readBlock(BLOCK_ZERO);
            byte[] card_no_byte = new byte[4];
            System.arraycopy(data, 0, card_no_byte, 0, card_no_byte.length);
            cardNo = Converter.toHexString(card_no_byte, card_no_byte.length);
        } else {
            throw new AppException("读取卡片ID失败!");
        }
        return cardNo;
    }*/

    /**
     * 获取平板ID
     *
     * @return 平板ID
     * @throws Exception
     */
    public String readCardNo() throws Exception {
        String cardNo;
        if (mfc.authenticateSectorWithKeyA(SECTOR1, MifareClassic.KEY_DEFAULT)) {
            byte[] data = mfc.readBlock(BLOCK_FOR);
            cardNo = new String(data);
        } else {
            Log.e("mifareControl", "读取平板ID失败");
            throw new AppException("读取平板ID失败!");
        }
        Log.e("mifareControl", "读取平板ID成功"+cardNo);
        return cardNo;
    }

    /**
     * 发卡操作
     *
     * @param icCard icCard
     * @throws Exception
     */
    public void makeCarCard(IcCard icCard) throws Exception {
        if (mfc.authenticateSectorWithKeyA(SECTOR1, MifareClassic.KEY_DEFAULT)) {
            //写入卡号
            byte[] carNo = icCard.card_no.getBytes();
            // 必须为16字节不够自己补0
            byte[] f = new byte[16];
            for (int j = 0; j < carNo.length; j++) {
                f[j] = carNo[j];
            }
            if (carNo.length < 16) {
                int j = 16 - carNo.length;
                int k = carNo.length;
                for (int j2 = 0; j2 < j; j2++) {
                    f[k + j2] = (byte) 0x00;
                }
            }
            mfc.writeBlock(BLOCK_FOR, f);
        } else {
            Log.e("mifareControl", "写卡失败");
            throw new AppException("制卡失败!");
        }
    }


    /**
     * 修改卡内余额
     *
     * @param cardNo       原卡号
     * @param remain_count 修改后卡号
     * @throws Exception
     */
    public void updateRemainCount(String cardNo, String remain_count) throws Exception {
        if (!isTheSame(cardNo)) {
            throw new AppException("传入的卡号与当前读取的卡号不匹配!");
        }
        if (mfc.authenticateSectorWithKeyA(SECTOR1, MifareClassic.KEY_DEFAULT)) {
            byte[] data = mfc.readBlock(BLOCK_FOR);
            byte[] price = Converter.intToByteArray(Integer.parseInt(remain_count));
            System.arraycopy(price, 0, data, 2, price.length);
            mfc.writeBlock(BLOCK_TWO, data);
        } else {
            throw new AppException("读取余额失败!");
        }

    }

    /**
     * 判断两次卡号是否相同
     *
     * @param cardNo 卡号
     * @return 相同返回true, 不同返回false;
     * @throws Exception
     */
    private boolean isTheSame(String cardNo) throws Exception {
        boolean same = true;
        String car_no = readCardNo();
        if (TextUtils.isEmpty(cardNo) || !car_no.equals(cardNo)) {
            same = false;
        }
        return same;
    }

    public void close() throws Exception {
        if (mfc != null) {
            mfc.close();
        }
    }


    /*
     *//**
     * 改变默认密码
     * @throws Exception
     *//*
	private void changePwd() throws Exception{
		if (mfc.authenticateSectorWithKeyA(SECTOR_ADDRESS, MifareClassic.KEY_DEFAULT)){
			byte[] data = mfc.readBlock(BLOCK_THREE);
			byte[] keyByte = Converter.hexStringToBytes(key);
			System.arraycopy(keyByte, 0, data, 0, keyByte.length);
			mfc.writeBlock(BLOCK_THREE, data);
		}else {
			throw new AppException("更改默认秘钥失败!");
		}
	}*/

//	/**
//	 * 读取平板ID
//	 * @return IcCard
//	 * @throws Exception
//	 */
//	public IcCard readIcCard() throws Exception{
//		IcCard icCard = new IcCard();
//		icCard.card_no = readCardNo();
//		/*if (mfc.authenticateSectorWithKeyA(SECTOR_ADDRESS, Converter.hexStringToBytes(key))){
//			byte[] data = mfc.readBlock(BLOCK_TWO);
//			byte[] car_type = new byte[2];
//			System.arraycopy(data, 0, car_type, 0, car_type.length);
//			icCard.type = new String(car_type);
//			icCard.card_remain = readRemainCount(icCard.card_no);
//			byte[] card_date = new byte[10];
//			System.arraycopy(data, 6, card_date, 0, card_date.length);
//			icCard.card_date = new String(card_date);
//		}else {
//			throw new AppException("读取余额失败!");
//		}*/
//		return icCard;
//	}

    /*	*//**
     * 获取卡内余额
     * @param cardNo 卡号
     * @return 余额
     * @throws Exception
     *//*
	public String readRemainCount(String cardNo) throws Exception{
		String count;
		if (!isTheSame(cardNo)){
			throw new AppException("传入的卡号与当前读取的卡号不匹配!");
		}
		if (mfc.authenticateSectorWithKeyA(SECTOR_ADDRESS, Converter.hexStringToBytes(key))){
			byte[] data = mfc.readBlock(BLOCK_TWO);
			byte[] card_remain = new byte[4];
			System.arraycopy(data, 2, card_remain, 0, card_remain.length);
			count = String.valueOf(Converter.byteArrayToInt(card_remain));
		}else {
			throw new AppException("读取余额失败!");
		}
		return count;
	}*/



}
