package com.pinyougou.seckill.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/login")
public class LoginController {

    @RequestMapping(value = "/loading")
    public String login(HttpServletRequest request) {
        String referer = request.getHeader("referer");
        if (StringUtils.isNotBlank(referer)) {
            return "redirect:" + referer;
        }
        return "redirect:/seckill-index.html";
    }
}
