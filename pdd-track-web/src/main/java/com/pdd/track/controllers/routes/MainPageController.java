package com.pdd.track.controllers.routes;

import com.pdd.track.service.impl.DataGenerationServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class MainPageController {

    private static final String VIEW = "/index";

    @RequestMapping(method = RequestMethod.GET, value = "/{rulesSetKey}/")
    public String portalPage(@PathVariable("rulesSetKey") final String rulesSetKey, final Model model) {
        model.addAttribute("studentKey", DataGenerationServiceImpl.STUDENT.getKey());
        model.addAttribute("rulesSetKey", rulesSetKey);
        return VIEW;
    }
}
