package ba.unsa.etf.si.notifications.client;

import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import java.util.ArrayList;
import java.util.List;

public class SockJSUtils {

    private SockJSUtils() {}

    public static SockJsClient getSockJsClient() {
        return new SockJsClient(addTransports());
    }

    private static List<Transport> addTransports() {
        WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(simpleWebSocketClient));
        return transports;
    }
}
