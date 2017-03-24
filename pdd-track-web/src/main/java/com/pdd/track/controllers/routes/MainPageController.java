package com.pdd.track.controllers.routes;

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
        model.addAttribute("studentKey", DataGenerationServiceImpl.STUDENT.getKey());
        model.addAttribute("rulesSetKey", "1");
        return VIEW;
    }
}
