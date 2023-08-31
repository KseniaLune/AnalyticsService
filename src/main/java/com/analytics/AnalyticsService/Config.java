package com.analytics.AnalyticsService;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@ConfigurationProperties(prefix = "config")
public class Config {
    private String url;
    private int batchSize;
    private int batchTime;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getBatchTime() {
        return batchTime;
    }

    public void setBatchTime(int batchTime) {
        this.batchTime = batchTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Config config = (Config) o;

        if (batchSize != config.batchSize) return false;
        if (batchTime != config.batchTime) return false;
        return Objects.equals(url, config.url);
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + batchSize;
        result = 31 * result + batchTime;
        return result;
    }
}
