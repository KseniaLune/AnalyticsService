package com.analytics.AnalyticsService;

import com.analytics.AnalyticsService.pojo.Event;
import com.analytics.AnalyticsService.service.Service;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/analytics")
public class Controller implements Runnable {

    private final Service service;
    private final Config mconfig;

    public Controller(Service service, Config config) {
        this.service = service;
        this.mconfig = config;
    }
    @PostConstruct
    public void init() {
        service.backupMemcache(mconfig.getUrl());
    }

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private static ScheduledFuture<?> task;

    @PostMapping("/add_event")
    public ResponseEntity<String> add(@RequestBody Event event) {
        if (event.getName().isBlank() || event.getParameters().isEmpty() || event.getParameters() == null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body("Add name or parameters are uncorrected");
        }
        executor.execute(() -> {
            int count = service.addEvent(event);
            if (count == mconfig.getBatchSize()) {
                service.flashEvents(mconfig.getUrl());
                this.flush();
            }
            if (task != null) {
                task.cancel(false);
                this.schedule();
            }
        });
        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(service.getMemcache());
    }

    @GetMapping("/flush")
    public ResponseEntity<String> flush() {
        this.schedule();
        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body("Events was flushed");
    }

    @GetMapping("/clean")
    public ResponseEntity<String> clear() {
        service.clearMemcache();
       return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body("Memcache was cleaned");
    }

    @Override
    public void run() {
        service.flashEvents(mconfig.getUrl());
        schedule();
    }

    private void schedule() {
        task = executor.schedule(this, mconfig.getBatchTime(), TimeUnit.SECONDS);
    }
}
