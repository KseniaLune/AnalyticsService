package com.analytics.AnalyticsService.persistence;

import com.analytics.AnalyticsService.pojo.Event;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

@Component
public class PersistenceImplFile implements Persistence {
    private static final String PATH = "Persistance.txt";
    private final Gson gson = new Gson();

    @Override
    public void write(Event event) {
        try {
            DataOutputStream a = new DataOutputStream(new FileOutputStream(PATH, true));
            try {
                a.writeUTF(event.getName());
                a.writeInt(event.getParameters().size());
                event.getParameters().forEach((k, v) -> {
                    try {
                        a.writeUTF(k);
                        a.writeUTF(v);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Event> read() {
        List<Event> events = new ArrayList<>();
        try {
            BufferedInputStream bi = new BufferedInputStream(new FileInputStream(PATH));
            DataInputStream di = new DataInputStream(bi);
            while (di.available() > 0) {
                String name = di.readUTF();
                int size = di.readInt();
                Map<String, String> params = new HashMap<>();
                for (int i = 0; i < size; i++) {
                    params.put(di.readUTF(), di.readUTF());
                }
                events.add(new Event(name, params));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return events;
    }

    @Override
    public void clear() {
        try {
            new FileOutputStream(PATH).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeWithGson(Event event) {
        try (ObjectOutputStream ous = new ObjectOutputStream(new FileOutputStream(PATH, true))) {
            ous.write(gson.toJson(event).getBytes());
            ous.write('\n');
            ous.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Event> readWithGson() {
        List<Event> result = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(PATH))) {
            byte[] events = inputStream.readAllBytes();
            String data = new String(events);
            String[] a = data.split("\n");
            result = Arrays.stream(a)
                .map((ev) -> gson.fromJson(ev, Event.class))
                .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
