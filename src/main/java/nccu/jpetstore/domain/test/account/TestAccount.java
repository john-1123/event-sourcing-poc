package nccu.jpetstore.domain.test.account;

import nccu.jpetstore.domain.entity.account.Account;

public class TestAccount {
    public static void main(String[] args) {
        Account account = new Account();

        account.setUsername("John");
        account.setPassword("password");
        account.setEmail("john@gmail.com");
        account.setFirstName("TestF");
        account.setLastName("TestL");

        System.out.println(account);
        account.getEvents().forEach(event -> {
            System.out.println("Event: " + event);
        });
    }
}
