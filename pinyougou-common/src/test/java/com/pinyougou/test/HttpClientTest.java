package com.pinyougou.test;

import com.pinyougou.util.HttpClient;

import java.io.IOException;
import java.text.ParseException;

public class HttpClientTest {
    public static void main(String[] args) throws Exception {
        HttpClient httpClient = new HttpClient("http://www.itcast.com");
        httpClient.setHttps(false);
        httpClient.setXmlParam("");//发送的xml数据
        httpClient.get();
        String content = httpClient.getContent();
        System.out.println(content);
    }
}
