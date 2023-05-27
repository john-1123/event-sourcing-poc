package nccu.jpetstore.domain.test.account;

import com.eventstore.dbclient.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import nccu.jpetstore.domain.core.event.DomainEvent;
import nccu.jpetstore.domain.entity.account.Account;

public class AccountEventStoreTest {

    public static void main(String[] args) throws Exception {
        EventStoreDBClientSettings settings = EventStoreDBConnectionString.parse(
                "esdb://127.0.0.1:2113?tls=false&keepAliveTimeout=10000&keepAliveInterval=10000");
        EventStoreDBClient client = EventStoreDBClient.create(settings);

//        EntityCreatedEvent event = new EntityCreatedEvent(
//                UUID.randomUUID().toString(), Category.class.getName(), new Date().getTime());

        Account account = new Account();

        //category.setCategoryId("123");
        account.setUsername("456");
        account.setPassword("789");
        account.setEmail("test@gmail.com");
        account.setFirstName("testF");
        account.setLastName("testL");

        //event.setData(category);

        for (DomainEvent e : account.getEvents()) {
            EventData eventData = EventData
                    .builderAsJson(e.getClass().getName(), e)
                    .build();
//            System.out.println(e.getStreamId());
            client.appendToStream(e.getStreamId(), eventData).get();
//            System.out.println(eventData);
        }

//        ReadStreamOptions options = ReadStreamOptions.get().forwards().fromRevision(2).maxCount(10);
//        ReadResult result = client.readStream(
//                "nccu.jpetstore.domain.entity.account.Account.bfa1cecf-9908-43b0-b803-293be562908c",
//                options).get();
//        ObjectMapper mapper = new ObjectMapper();
//        for (ResolvedEvent resolvedEvent: result.getEvents()) {
//            RecordedEvent recordedEvent = resolvedEvent.getOriginalEvent();
//            byte[] data = recordedEvent.getEventData();
//            System.out.println(mapper.readValue(data, Object.class));
//        }
        ReadStreamOptions options = ReadStreamOptions.get().fromEnd().backwards().maxCount(1);

        ReadResult result = client.readStream(account.getEvents().get(0).getStreamId(), options)
                .get();

        RecordedEvent e = result.getEvents().get(0).getOriginalEvent();
        byte[] data = e.getEventData();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.readValue(data, Object.class));

//        List<ResolvedEvent> events = result.getEvents();
//        for (ResolvedEvent e : events) {
//            RecordedEvent recordedEvent = e.getOriginalEvent();
//        }

    }
}
