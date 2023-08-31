package com.analytics.AnalyticsService.persistence;


import com.analytics.AnalyticsService.pojo.Event;

import java.util.List;

public interface Persistence {

    void write(Event events);

    List<Event> read();

    void clear();

}