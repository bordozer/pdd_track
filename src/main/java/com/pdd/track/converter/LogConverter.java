package com.pdd.track.converter;

import com.pdd.track.dto.DEL_LogItemDto;
import com.pdd.track.entity.DEL_LogEntity;
import com.pdd.track.model.DEL_LogItem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogConverter {

    public static void populateEntity(final DEL_LogEntity entity, final DEL_LogItem model) {
        Assert.notNull(entity, "Entity must not nbe null");
        Assert.notNull(model, "Model must not nbe null");

        entity.setField(model.getField());
        entity.setValue(model.getValue());
    }

    public static DEL_LogItem toModel(final DEL_LogEntity entity) {
        Assert.notNull(entity, "Entity must not nbe null");

        DEL_LogItem result = new DEL_LogItem();
        result.setId(entity.get_id());
        result.setField(entity.getField());
        result.setValue(entity.getValue());
        result.setTime(entity.getTime());

        return result;
    }

    public static DEL_LogItem toModel(final DEL_LogItemDto dto) {
        Assert.notNull(dto, "DTO must not nbe null");

        DEL_LogItem result = new DEL_LogItem();
        result.setId(dto.getId());
        result.setField(dto.getField());
        result.setValue(dto.getValue());
        result.setTime(dto.getTime());

        return result;
    }

    public static DEL_LogItemDto toDto(final DEL_LogItem model) {
        Assert.notNull(model, "Model must not nbe null");

        DEL_LogItemDto result = new DEL_LogItemDto();
        result.setId(model.getId());
        result.setField(model.getField());
        result.setValue(model.getValue());
        result.setTime(model.getTime());

        return result;
    }
}
