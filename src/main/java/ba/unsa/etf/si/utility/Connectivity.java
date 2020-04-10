package ba.unsa.etf.si.utility;


import ba.unsa.etf.si.utility.interfaces.ConnectivityObserver;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Connectivity {

    private final String target;

    private static final int INTERVAL = 15; //repeat after 15s
    private static final int PORT = 80;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private List<ConnectivityObserver> observerList = new ArrayList<>();

    public Connectivity (String target) {
        this.target = target;
    }

    public void subscribe(ConnectivityObserver observer) {
        observerList.add(observer);
    }

    private void offlineMode() {
        observerList.forEach(o -> {
            if(o != null) o.setOfflineMode();
        });
    }

    private void onlineMode() {
        observerList.forEach(o -> {
            if(o != null) o.setOnlineMode();
        });
    }

    private void removeNulls() {
        observerList = observerList.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private boolean isReachable() {
        try (Socket socket = new Socket()){
            socket.connect(new InetSocketAddress(target, PORT), 5000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void run() {
        scheduler.scheduleWithFixedDelay(() -> {
            removeNulls();
            if (isReachable()) {
                onlineMode();
                System.out.println("ONLINE!");
            }
            else {
                offlineMode();
                System.out.println("OFFLINE!");
            }
        }, 0, INTERVAL, TimeUnit.SECONDS);
    }
}
