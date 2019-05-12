package org.alindner.projects.alarmclock.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TimeModel {
    long id;
    String name;
    long time;
    Weekdays weekdays;
}