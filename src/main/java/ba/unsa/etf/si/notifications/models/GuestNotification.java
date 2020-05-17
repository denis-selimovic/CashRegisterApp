package ba.unsa.etf.si.notifications.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GuestNotification {

    public long id;
    public String message;

    public GuestNotification() {
    }

    @JsonCreator
    public GuestNotification(@JsonProperty("id") long id, @JsonProperty("message") String message) {
        this.id = id;
        this.message = message;
    }

    @JsonCreator
    public GuestNotification(String msg) {
        this.message = msg;
    }
}
