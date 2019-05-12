package org.alindner.projects.alarmclock.models;

import java.util.List;

import lombok.Data;

@Data
public class ResultModel {
    List<TimeModel> times;
}