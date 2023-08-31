package com.analytics.AnalyticsService.service;

import com.analytics.AnalyticsService.memcache.Memcache;
import com.analytics.AnalyticsService.persistence.Persistence;
import com.analytics.AnalyticsService.persistence.PersistenceImplFile;
import com.analytics.AnalyticsService.pojo.Event;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service{

    private Memcache memcache = new Memcache(new ArrayDeque<>());
    private Persistence persistence = new PersistenceImplFile();

    @Override
    public int addEvent(Event event) {
        //add to memcache
        Queue<Event> events = memcache.getMemcache();
        events.add(event);
        memcache.setMemcache(events);

        //add to persistence
        persistence.write(event);

        return events.size();
    }

    @Override
    public void flashEvents(String url) {
        List<Event> list = memcache.getMemcache().stream().toList();
        //request with events
        Gson gson = new Gson();
        String json = gson.toJson(list);

        RequestBody body = RequestBody.create(
           json, MediaType.parse("application/json")
        );
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();

        Response response = null;
        int count = 0;
        do {
            try {
                response = okHttpClient.newCall(request)
                    .execute();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();
                }

                count++;
            }
        } while ((response == null || !response.isSuccessful()) && count < 2);


        memcache.getMemcache().clear(); // clear memcache
        persistence.clear();
    }

    public String getMemcache() {
        List<Event> list = memcache.getMemcache().stream().toList();
        return list.toString();
    }

    @Override
    public void backupMemcache(String url) {
        //stars with start/restart app
        List<Event> read = persistence.read();
        Queue<Event> queue = new ArrayDeque<>(read);

        if (!queue.isEmpty()) {
            memcache.setMemcache(queue);
            flashEvents(url);
        }
    }

    @Override
    public void clearMemcache() {
        memcache.setMemcache(new ArrayDeque<>());
        persistence.clear();
    }


}
