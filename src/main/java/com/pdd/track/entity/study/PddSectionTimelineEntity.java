package com.pdd.track.entity.study;

import com.pdd.track.entity.UserEntity;
import com.pdd.track.entity.rule.PddSectionEntity;
import com.pdd.track.model.Testing;
import com.pdd.track.model.TimelineItem;
import com.pdd.track.model.TimelineEvent;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "PDD.TRACK.PDD_SECTION_TIMELINE")
public class PddSectionTimelineEntity {

    private String _id;
    private UserEntity user;
    private PddSectionEntity pddSection;
    private List<TimelineItem> timelineItems;

    @Data
    public abstract static class PddSectionTimelineEvent extends TimelineEvent {

    }

    @Data
    public static class PddSectionLecture extends PddSectionTimelineEvent {

    }

    @Data
    public static class PddSectionStudying extends PddSectionTimelineEvent {

    }

    @Data
    public static class PddSectionTesting extends PddSectionTimelineEvent {
        private Testing testing;
    }
}
