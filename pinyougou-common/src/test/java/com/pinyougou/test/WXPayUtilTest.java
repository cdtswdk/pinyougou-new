package com.pinyougou.test;

import com.github.wxpay.sdk.WXPayUtil;

import java.util.HashMap;
import java.util.Map;

public class WXPayUtilTest {
    public static void main(String[] args) throws Exception {
        String str = WXPayUtil.generateNonceStr();
        System.out.println(str);

        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("username","zhangsan");
        dataMap.put("password","123");
        String data = WXPayUtil.mapToXml(dataMap);
        System.out.println(data);

        Map<String, String> xmlToMap = WXPayUtil.xmlToMap(data);
        //System.out.println(xmlToMap);

        String data1 = WXPayUtil.generateSignedXml(dataMap, "data");
        System.out.println(data1);
    }
}
