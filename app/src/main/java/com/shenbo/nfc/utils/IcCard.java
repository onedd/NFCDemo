package com.shenbo.nfc.utils;

/**
 * IC卡 ISO14443 A标准
 */
public class IcCard {

    public static final String TYPE_SELLER = "01";
    public static final String TYPE_CUSTOMER = "02";

    public String card_no;// 平板id
//    public String type;// 卡片类型 第1扇区第三块数据，卡片类型(2个字符)   平板状态  00
//    public String car_owner_no;//
//    public String card_remain;//
//    public String card_date;//
}