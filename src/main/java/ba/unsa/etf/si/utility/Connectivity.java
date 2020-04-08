package ba.unsa.etf.si.utility;


import java.net.InetAddress;

public class Connectivity implements Runnable {

    InetAddress target = null;
    String connectionStatus = "online";

    private static final int INTERVAL = 30000; //repeat after 30s


     public Connectivity (String address) {
         try {
             target = InetAddress.getByName(address);
         } catch (Exception e) {
             target = null;
             e.printStackTrace();
         }
     }

     public String getConnectionStatus () {
         return connectionStatus;
     }

     public void setConnectionStatus (String connectionStatus) {
         this.connectionStatus = connectionStatus;
     }

    @Override
    public void run() {

         if (target != null) {
             try {
                 while (true) {
                     boolean online = target.isReachable(1000);
                     if (online) setConnectionStatus("online");
                     else setConnectionStatus("offline");
                     System.out.println(getConnectionStatus());
                     Thread.sleep(INTERVAL);
                 }
             }
             catch (Exception e) {
                 e.printStackTrace();
             }
         }
    }
}
