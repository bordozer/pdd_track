package com.pdd.track.entity.study;

import com.pdd.track.model.Testing;
import com.pdd.track.model.TimelineItem;
import com.pdd.track.model.TimelineEvent;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "PDD.TRACK.STUDYING_TIMELINE")
public class UserStudyTimelineEntity {

    private List<TimelineItem> timelineItems;
    private List<PddSectionTimelineEntity> pddSectionTimelineItems;

    @Data
    public static class SchoolTheoryExam extends TimelineEvent {

    }

    @Data
    public static class SchoolDrivingExam extends TimelineEvent {

    }

    @Data
    public static class GraduationTheoryExam extends TimelineEvent {
        private Testing testing;
        private boolean passed;
    }

    @Data
    public static class GraduationDrivingExam extends TimelineEvent {
        private boolean passed;
    }
}
