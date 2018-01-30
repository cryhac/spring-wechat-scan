package com.scan.controller;

import com.scan.config.ProjectUrlConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/wechat")
@Slf4j
public class WechatController {

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    @RequestMapping(value = "/scan")
    public ModelAndView index(Map<String, Object> map) {
        map.put("projectUrlConfig", projectUrlConfig);
        return new ModelAndView("/qr/index", map);
    }
}
