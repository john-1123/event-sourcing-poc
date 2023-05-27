package nccu.jpetstore.domain.test.account;

import nccu.jpetstore.domain.core.EventStore;
import nccu.jpetstore.domain.entity.account.Account;
import nccu.jpetstore.domain.entity.account.EventSourcedAccountRepository;

public class TestAccountRepo {
    private final static EventStore eventStore = new EventStore(
            "esdb://127.0.0.1:2113?tls=false&keepAliveTimeout=10000&keepAliveInterval=10000");
    private final static EventSourcedAccountRepository repository = new EventSourcedAccountRepository(eventStore);

    public static void main(String[] args) {
        String accountId = testCreate();
        testFindBy(accountId);
        testAppend(accountId);
        testFindBy(accountId, 4);
    }

    private static void testAppend(String accountId) {
        Account account = repository.findBy(accountId);
        account.setUsername("John-Updated-1");
        System.out.println("======Test Update Account Attribute======");
        System.out.println(repository.save(account));
        System.out.println(account);
    }

    private static void testFindBy(String accountId) {
        Account account = repository.findBy(accountId);
        System.out.println("======Test Find Account by AccountId======");
        System.out.println(account);
    }

    private static void testFindBy(String accountId, long version) {
        Account account = repository.findBy(accountId, version);
        System.out.println("======Test Find Account by AccountId to Version======");
        System.out.println(account);
    }

    private static String testCreate() {
        Account account = new Account();
        account.setUsername("John");
        account.setPassword("password");
        account.setEmail("john@gmail.com");
        account.setFirstName("TestF");
        account.setLastName("TestL");
        System.out.println("======Test Create Account======");
        System.out.println(repository.save(account));
        return account.getAccountId();
    }
}
