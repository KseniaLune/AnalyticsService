package com.analytics.AnalyticsService.pojo;

import java.util.Map;
import java.util.Objects;



public class Event {
    private  String name;
    private  Map<String, String> parameters;

    public Event(String name, Map<String, String> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }
    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (!Objects.equals(name, event.name)) return false;
        return Objects.equals(parameters, event.parameters);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
            "name='" + name + '\'' +
            ", parameters=" + parameters +
            '}';
    }

}
