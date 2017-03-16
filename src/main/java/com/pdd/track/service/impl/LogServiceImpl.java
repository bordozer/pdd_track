package com.pdd.track.service.impl;

import com.pdd.track.converter.LogConverter;
import com.pdd.track.entity.DEL_LogEntity;
import com.pdd.track.model.DEL_LogItem;
import com.pdd.track.repository.LogRepository;
import com.pdd.track.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LogServiceImpl implements LogService {

    @Inject
    private LogRepository logRepository;

    @Override
    public List<DEL_LogItem> getAll() {
        LOGGER.info("About to fetch log items");

        return logRepository.findAll().stream()
                .map(this::getLogItem)
                .collect(Collectors.toList());
    }

    @Override
    public DEL_LogItem createLogRecord(final DEL_LogItem logItem) {
        LOGGER.info("About to create log item: {}", logItem);

        DEL_LogEntity entity = new DEL_LogEntity();
        entity.setTime(LocalDateTime.now());

        LogConverter.populateEntity(entity, logItem);

        return getLogItem(logRepository.save(entity));
    }

    private DEL_LogItem getLogItem(final DEL_LogEntity entity) {
        DEL_LogItem logItem = LogConverter.toModel(entity);
        LOGGER.info("Log item: {}", logItem);
        return logItem;
    }
}
