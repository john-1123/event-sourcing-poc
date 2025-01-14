package nccu.jpetstore.domain.core;

import com.eventstore.dbclient.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import nccu.jpetstore.domain.core.event.AttributeUpdatedEvent;
import nccu.jpetstore.domain.core.event.DomainEvent;
import nccu.jpetstore.domain.core.event.EntityCreatedEvent;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class EventStore {

    private EventStoreDBClient client;

    public EventStore(String connectionString) {
        EventStoreDBClientSettings settings = null;
        try {
            settings = EventStoreDBConnectionString.parse(connectionString);
            client = EventStoreDBClient.create(settings);
        } catch (ConnectionStringParsingException e) {
            throw new RuntimeException(e);
        }
    }

//    public void createStream(String streamId, EntityCreatedEvent entityCreatedEvent) {
//
//    }

    public String appendToStream(String streamId, DomainEvent e) throws ExecutionException, InterruptedException {
        EventData eventData = EventData.builderAsJson(e.getClass().getName(), e).build();
        client.appendToStream(streamId, eventData).get();
        return streamId;
    }

    public List<String> findStreamIdByEntityType(String entityType){
        return null;
    }
// 一開始建立event store類別時，應該要將stream id建一個cache?


//    public List<ResolvedEvent> getStream(String streamId, int from, int to) {
//        List<ResolvedEvent> results = new ArrayList<>();
//        try {
//            if (to < from) throw new IllegalArgumentException("to must >= from");
//            ReadStreamOptions options = ReadStreamOptions.get()
//                    .forwards()
//                    .fromRevision(from)
//                    .maxCount(to - from);
//            results = client.readStream(streamId, options).get().getEvents();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return results;
//    }

//    public List<Map> getStream(String streamId) {
//        List<Map> results = new ArrayList<>();
//
//        try {
//            ReadStreamOptions options = ReadStreamOptions.get()
//                    .forwards()
//                    .fromStart();
//            List<ResolvedEvent> events = client.readStream(streamId, options).get().getEvents();
//            for (ResolvedEvent event : events) {
//                ObjectMapper mapper = new ObjectMapper();
//                results.add(mapper.readValue(event.getOriginalEvent().getEventData(), LinkedHashMap.class));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return results;
//    }

    public List<DomainEvent> getStream(String streamId, long version) {
        List<DomainEvent> results = new ArrayList<>();

        try {
            ReadStreamOptions options = ReadStreamOptions.get()
                    .fromRevision(version)
                    .backwards();
            List<ResolvedEvent> events = client.readStream(streamId, options).get().getEvents();
            for (ResolvedEvent event : events) {
                ObjectMapper mapper = new ObjectMapper();
                results.add(deserialize(mapper.readValue(event.getOriginalEvent().getEventData(), LinkedHashMap.class)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    public List<DomainEvent> getStream(String streamId) {
        List<DomainEvent> results = new ArrayList<>();

        try {
            ReadStreamOptions options = ReadStreamOptions.get()
                    .forwards()
                    .fromStart();
            List<ResolvedEvent> events = client.readStream(streamId, options).get().getEvents();
            for (ResolvedEvent event : events) {
                ObjectMapper mapper = new ObjectMapper();
                results.add(deserialize(mapper.readValue(event.getOriginalEvent().getEventData(), LinkedHashMap.class)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        EventStore eventStore = new EventStore(
                "esdb://127.0.0.1:2113?tls=false&keepAliveTimeout=10000&keepAliveInterval=10000");
        List<DomainEvent> results = eventStore.getStream(
                "nccu.jpetstore.domain.entity.category.Category.3235999e-cc9d-45c2-b99c-587ab8a99037");
        for (DomainEvent e : results) {
            System.out.println(e);
        }
    }

    private static DomainEvent deserialize(Map map) {
        String eventType = (String) map.get("eventType");
        DomainEvent result = null;
        if ("nccu.jpetstore.domain.core.event.EntityCreatedEvent".equals(eventType)) {
            result = new EntityCreatedEvent(
                    (String) map.get("streamId"), (String) map.get("entityType"), (long) map.get("timestamp"));
        } else if ("nccu.jpetstore.domain.core.event.AttributeUpdatedEvent".equals(eventType)) {
            AttributeUpdatedEvent event = new AttributeUpdatedEvent(
                    (String) map.get("streamId"), (String) map.get("entityType"), (long) map.get("timestamp"));
            event.setName((String) map.get("name"));
            event.setValue(map.get("value"));
            result = event;
        }
        return result;
    }

//    public static Object reconstructObject(Map<String, Object> map) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
//        String className = (String) map.get("eventType");
//        Class<?> clazz = Class.forName(className);
//        Object instance = clazz.newInstance();
//
//        for (Map.Entry<String, Object> entry : map.entrySet()) {
//            String attributeName = entry.getKey();
//            Object attributeValue = entry.getValue();
//
//            if (!attributeName.equals("eventType")) {
//                try {
//                    Field field = clazz.getDeclaredField(attributeName);
//                    field.setAccessible(true);
//                    field.set(instance, attributeValue);
//                } catch (NoSuchFieldException e) {
//                    System.err.println("Field not found: " + attributeName);
//                }
//            }
//        }
//
//        return instance;
//    }

}
