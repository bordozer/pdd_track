package com.pdd.track.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class HelloWordController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public MyResponse getPong() {
        return new MyResponse("Pong");
    }

    @Getter
    @AllArgsConstructor
    private class MyResponse {
        private final String text;
    }
    //./gradlew build && java -jar build/libs/pdd-track.jar
}
