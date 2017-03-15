package com.pdd.track.converter;

import com.pdd.track.dto.LogItemDto;
import com.pdd.track.entity.LogEntity;
import com.pdd.track.model.LogItem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogConverter {

    public static void populateEntity(final LogEntity entity, final LogItem model) {
        Assert.notNull(entity, "Entity must not nbe null");
        Assert.notNull(model, "Model must not nbe null");

        entity.setField(model.getField());
        entity.setValue(model.getValue());
    }

    public static LogItem toModel(final LogEntity entity) {
        Assert.notNull(entity, "Entity must not nbe null");

        LogItem result = new LogItem();
        result.setId(entity.get_id());
        result.setField(entity.getField());
        result.setValue(entity.getValue());
        result.setTime(entity.getTime());

        return result;
    }

    public static LogItem toModel(final LogItemDto dto) {
        Assert.notNull(dto, "DTO must not nbe null");

        LogItem result = new LogItem();
        result.setId(dto.getId());
        result.setField(dto.getField());
        result.setValue(dto.getValue());
        result.setTime(dto.getTime());

        return result;
    }

    public static LogItemDto toDto(final LogItem model) {
        Assert.notNull(model, "Model must not nbe null");

        LogItemDto result = new LogItemDto();
        result.setId(model.getId());
        result.setField(model.getField());
        result.setValue(model.getValue());
        result.setTime(model.getTime());

        return result;
    }
}
