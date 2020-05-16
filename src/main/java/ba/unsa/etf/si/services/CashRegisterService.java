package ba.unsa.etf.si.services;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.interfaces.CashRegisterObserver;

import java.time.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CashRegisterService {

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private CashRegisterObserver cashRegisterObserver;

    public void setCashRegisterObserver(CashRegisterObserver cashRegisterObserver) {
        this.cashRegisterObserver = cashRegisterObserver;
    }

    public void run() {
        LocalTime current = LocalTime.now();
        if(!(current.isAfter(App.cashRegister.getStartTime()) && current.isBefore(App.cashRegister.getEndTime()))) {
            cashRegisterObserver.close();
            executorService.schedule(() -> cashRegisterObserver.open(), computeNextDelay(), TimeUnit.SECONDS);
        }
    }

    public void stop() {
        executorService.shutdown();
    }

    private long computeNextDelay() {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.systemDefault();

        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNextTarget = zonedNow
                .withHour(App.cashRegister.getStartTime().getHour())
                .withMinute(App.cashRegister.getStartTime().getMinute())
                .withSecond(App.cashRegister.getStartTime().getSecond());

        //ZonedDateTime zonedNextTarget = zonedNow
        //      .withHour(21)
        //      .withMinute(0)
        //      .withSecond(0);

        if (zonedNow.compareTo(zonedNextTarget) > 0)
            zonedNextTarget = zonedNextTarget.plusDays(1);

        Duration duration = Duration.between(zonedNow, zonedNextTarget);
        return duration.getSeconds();
    }
}
