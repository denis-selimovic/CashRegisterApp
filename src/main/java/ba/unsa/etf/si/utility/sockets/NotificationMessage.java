package ba.unsa.etf.si.utility.sockets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationMessage {

    public long id;
    public String message;

    public NotificationMessage() { }

    @JsonCreator
    public NotificationMessage(@JsonProperty("id") long id, @JsonProperty("message") String message) {
        this.id = id;
        this.message = message;
    }
}
