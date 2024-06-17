package com.epoint.rest;

import java.util.HashMap;
import java.util.Map;

import com.epoint.core.utils.httpclient.HttpUtil;

public class Test
{

    public static void main(String[] args) {
        String apiUrl = "http://localhost:8089/epoint-web/rest/demorest/demomethod1";
        Map<String, String> map = new HashMap();
        map.put("params", "bbbbbbb");

        String returnString = HttpUtil.doHttp(apiUrl, map, HttpUtil.POST_METHOD, HttpUtil.RTN_TYPE_STRING);
        System.out.println(returnString);
    }

}
