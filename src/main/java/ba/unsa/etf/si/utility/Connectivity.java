package ba.unsa.etf.si.utility;


import ba.unsa.etf.si.utility.interfaces.ConnectivityObserver;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Connectivity {

    private InetAddress target = null;

    private static final int INTERVAL = 30; //repeat after 30s
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private List<ConnectivityObserver> observerList = new ArrayList<>();

     public Connectivity (String address) {
         try {
             target = InetAddress.getByName(address);
         } catch (Exception e) {
             target = null;
             e.printStackTrace();
         }
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

    public void run() {
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                if(target.isReachable(5000)) onlineMode();
                else offlineMode();
                removeNulls();
            } catch (IOException e) {
                e.printStackTrace();
            }
        },0, INTERVAL, TimeUnit.SECONDS);

    }
}
