package com.pdd.track.service;


import com.pdd.track.model.LogItem;

import java.util.List;

public interface LogService {

    List<LogItem> getAll();

    LogItem createLogRecord(final LogItem logItem);
}
