package com.analytics.AnalyticsService.memcache;


import com.analytics.AnalyticsService.pojo.Event;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Queue;

public class Memcache {
    private Queue<Event> memcache;

    public Memcache(Queue<Event> memcache) {
        this.memcache = memcache;
    }

    public Queue<Event> getMemcache() {
        if (memcache == null) {
            this.memcache = new ArrayDeque<>();
        }
        return memcache;
    }

    public void setMemcache(Queue<Event> memcache) {
        this.memcache = memcache;
    }

}
