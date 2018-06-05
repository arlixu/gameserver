package com.source3g.platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by huhuaiyong on 2017/9/13.
 */
@Controller
public class ViewController {
    @RequestMapping("")
    public String index(Model model) {
        return "index";
    }
}
