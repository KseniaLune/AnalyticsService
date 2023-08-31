package com.analytics.AnalyticsService.service;


import com.analytics.AnalyticsService.pojo.Event;

import java.util.Queue;

public interface Service {

    int addEvent(Event event);

    void flashEvents(String url);

    void backupMemcache(String url);

    void clearMemcache();

    String getMemcache();
}
