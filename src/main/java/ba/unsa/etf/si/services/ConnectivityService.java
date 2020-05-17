package ba.unsa.etf.si.services;


import ba.unsa.etf.si.interfaces.ConnectivityObserver;
import ba.unsa.etf.si.utility.http.HttpUtils;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ConnectivityService {

    public boolean ping = true;

    private final String target;

    private static final int INTERVAL = 10;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private List<ConnectivityObserver> observerList = new ArrayList<>();

    public ConnectivityService(String target) {
        this.target = target;
    }

    public void subscribe(ConnectivityObserver observer) {
        observerList.add(observer);
    }

    private void offlineMode() {
        observerList.forEach(o -> {
            if (o != null) o.setOfflineMode();
        });
    }

    private void onlineMode() {
        observerList.forEach(o -> {
            if (o != null) o.setOnlineMode();
        });
    }

    private void removeNulls() {
        observerList = observerList.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private void ping() {
        try {
            HttpRequest GET = HttpUtils.GET(target);
            HttpUtils.sendSync(GET, HttpResponse.BodyHandlers.ofString());
            onlineMode();
        } catch (Exception timeout) {
            offlineMode();
        }
    }

    public void run() {
        scheduler.scheduleWithFixedDelay(() -> {
            removeNulls();
            if (ping) ping();
        }, 0, INTERVAL, TimeUnit.SECONDS);
    }

    public void stop() {
        if (!scheduler.isShutdown()) scheduler.shutdownNow();
    }
}
