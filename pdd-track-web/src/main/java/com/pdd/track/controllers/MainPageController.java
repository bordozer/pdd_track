package com.pdd.track.controllers;

import com.pdd.track.service.impl.DataGenerationServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class MainPageController {

    private static final String VIEW = "/index";

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public String portalPage(final Model model) {
        model.addAttribute("userName", DataGenerationServiceImpl.STUDENT.getName());
        model.addAttribute("userKey", DataGenerationServiceImpl.STUDENT.getKey());
        return VIEW;
    }
}
