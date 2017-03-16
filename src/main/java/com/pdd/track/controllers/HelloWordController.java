package com.pdd.track.controllers;

import com.pdd.track.converter.LogConverter;
import com.pdd.track.dto.DEL_LogItemDto;
import com.pdd.track.model.DEL_LogItem;
import com.pdd.track.service.LogService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping(value = "/")
public class HelloWordController {

    @Inject
    private LogService logService;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public List<DEL_LogItem> getAll() {
        return logService.getAll();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/log")
    public DEL_LogItemDto createLogRecord(@RequestBody final DEL_LogItemDto dto) {
        return LogConverter.toDto(logService.createLogRecord(LogConverter.toModel(dto)));
    }
}
