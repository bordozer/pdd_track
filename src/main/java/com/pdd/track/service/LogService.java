package com.pdd.track.service;


import com.pdd.track.model.DEL_LogItem;

import java.util.List;

public interface LogService {

    List<DEL_LogItem> getAll();

    DEL_LogItem createLogRecord(final DEL_LogItem logItem);
}
