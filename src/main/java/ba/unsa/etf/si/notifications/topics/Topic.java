package ba.unsa.etf.si.notifications.topics;

import java.lang.reflect.Type;
import java.util.function.Consumer;

public interface Topic {
    Type getType();
    String getTopic();
    Consumer<Object> getAction();
}
