package ba.unsa.etf.si.notifications.topics;

import ba.unsa.etf.si.notifications.models.GuestNotification;
import ba.unsa.etf.si.utility.javafx.NotificationUtils;
import javafx.application.Platform;
import javafx.geometry.Pos;
import java.lang.reflect.Type;
import java.util.function.Consumer;

public class GuestNotificationTopic implements Topic {

    private final Consumer<Object> action = payload -> {
        GuestNotification notification = (GuestNotification) payload;
        Platform.runLater(() -> NotificationUtils.showInformation(Pos.BASELINE_RIGHT, "Guest notification", notification.message, 10));
    };

    @Override
    public Type getType() {
        return GuestNotification.class;
    }

    @Override
    public String getTopic() {
        return "/api/notifications";
    }

    @Override
    public Consumer<Object> getAction() {
        return action;
    }
}
