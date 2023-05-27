package nccu.jpetstore.domain.entity.account;

import nccu.jpetstore.domain.core.EventStore;
import nccu.jpetstore.domain.core.event.DomainEvent;

import java.util.List;

public class EventSourcedAccountRepository {
    EventStore eventStore;

    public EventSourcedAccountRepository(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public String save(Account account) {
        String streamId = null;
        for(DomainEvent e: account.getEvents()) {
            try {
                streamId = e.getStreamId();
                eventStore.appendToStream(streamId, e);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return streamId;
    }

    public Account findBy(String accountId) {
        String streamId = Account.class.getName() + "." + accountId;
        List<DomainEvent> events = eventStore.getStream(streamId);
        Account account = new Account(accountId);
        for (DomainEvent event: events) {
            account.mutate(event);
        }
        return account;
    }

    public Account findBy(String accountId, long version) {
        String streamId = Account.class.getName() + "." + accountId;
        List<DomainEvent> events = eventStore.getStream(streamId, version);
        Account account = new Account(accountId);
        for (DomainEvent event: events) {
            account.mutate(event);
        }
        return account;
    }
}
