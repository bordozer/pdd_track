package com.pdd.track.conf;

import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Configuration
public class ThymeleafExtension {

    @Inject
    private SpringTemplateEngine templateEngine;

    @PostConstruct
    public void extension() {
        FileTemplateResolver resolver = new FileTemplateResolver();
//        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setPrefix("src/main/webapp/WEB-INF/templates");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);
        templateEngine.addTemplateResolver(resolver);
    }
}
