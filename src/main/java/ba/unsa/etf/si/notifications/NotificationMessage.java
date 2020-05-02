package ba.unsa.etf.si.notifications;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

public class NotificationMessage {

    public long id;
    public String message;

    public NotificationMessage() { }

    @JsonCreator
    public NotificationMessage(@JsonProperty("id") long id, @JsonProperty("message") String message) {
        this.id = id;
        this.message = message;
    }

    @JsonCreator
    public NotificationMessage(String msg) {
        this.message = msg;
    }
}
