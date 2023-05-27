package nccu.jpetstore.domain.entity.account;

import nccu.jpetstore.domain.core.event.AttributeUpdatedEvent;
import nccu.jpetstore.domain.core.event.DomainEvent;
import nccu.jpetstore.domain.core.event.EntityCreatedEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Account {
    private String accountId;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
//    private String status;
//    private String address1;
//    private String address2;
//    private String city;
//    private String state;
//    private String zip;
//    private String country;
//    private String phone;
//    private String favouriteCategoryId;
//    private String languagePreference;
//    private boolean listOption;
//    private boolean bannerOption;
//    private String bannerName;

    private List<DomainEvent> eventCache;

    public Account() {
        this.eventCache = new ArrayList<>();
        this.accountId = UUID.randomUUID().toString();
    }

    public Account(String accountId) {
        this.eventCache = new ArrayList<>();
        this.accountId = accountId;
    }

    public void setUsername(String username) {
        AttributeUpdatedEvent event = generateAttributeUpdatedEvent("username", username);
        cause(event);
    }
    public String getUsername() {
        return this.username;
    }

    public void setPassword(String password) {
        AttributeUpdatedEvent event = generateAttributeUpdatedEvent("password", password);
        cause(event);
    }
    public String getPassword() {
        return this.getPassword();
    }

    public void setEmail(String email) {
        AttributeUpdatedEvent event = generateAttributeUpdatedEvent("email", email);
        cause(event);
    }
    public String getEmail() {
        return this.email;
    }

    public void setFirstName(String firstName) {
        AttributeUpdatedEvent event = generateAttributeUpdatedEvent("firstName", firstName);
        cause(event);
    }
    public String getFirstName() {
        return this.firstName;
    }

    public void setLastName(String lastName) {
        AttributeUpdatedEvent event = generateAttributeUpdatedEvent("lastName", lastName);
        cause(event);
    }
    public String getLastName() {
        return this.lastName;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public String getStreamId() {
        return Account.class.getName() + "." + this.accountId;
    }

    public void cause(DomainEvent event) {
        mutate(event);
        this.eventCache.add(event);
    }

    public void mutate(DomainEvent event) {
        if (event instanceof EntityCreatedEvent) {
            // applyCreatedEvent((EntityCreatedEvent<Category>) event);
        } else if (event instanceof AttributeUpdatedEvent) {
            applyUpdatedEvent((AttributeUpdatedEvent) event);
        } else throw new IllegalArgumentException();
    }

    private void applyUpdatedEvent(AttributeUpdatedEvent event) {
        switch (event.getName()) {
            case "username":
                this.username = (String) event.getValue();
                break;
            case "password":
                this.password = (String) event.getValue();
                break;
            case "email":
                this.email = (String) event.getValue();
                break;
            case "firstName":
                this.firstName = (String) event.getValue();
                break;
            case "lastName":
                this.lastName = (String) event.getValue();
                break;
        }
    }

    private AttributeUpdatedEvent generateAttributeUpdatedEvent(String key, Object value) {
        AttributeUpdatedEvent event =
                new AttributeUpdatedEvent(getStreamId(), Account.class.getName(), new Date().getTime());
        event.setName(key);
        event.setValue(value);
        return event;
    }

    public void reset() {
        this.eventCache.clear();
    }

    public List<DomainEvent> getEvents() {
        return this.eventCache;
    }

    @Override
    public String toString() {
        return String.format("Account{accountId='%s', username='%s', password='%s, email='%s, firstName='%s, , LastName='%s'}",
                accountId, username, password, email, firstName, lastName);
    }
}
