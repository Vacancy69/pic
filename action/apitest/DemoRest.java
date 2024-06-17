package com.epoint.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epoint.basic.api.common.ApiBaseController;

@RestController
@RequestMapping("/demorest")
public class DemoRest extends ApiBaseController
{

    // http://localhost:8089/epoint-web/rest/demorest/demomethod
    @RequestMapping(value = "/demomethod")
    public String demoMethod(@RequestParam(name = "params") String params) {
        return params;
    }

    // http://localhost:8089/epoint-web/rest/demorest/demomethod1
    @RequestMapping(value = "/demomethod1")
    public String demoMethod1(@RequestParam(name = "params") String params) {
        return params;
    }

}
