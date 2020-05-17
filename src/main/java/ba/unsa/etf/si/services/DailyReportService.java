package ba.unsa.etf.si.services;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.controllers.PrimaryController;
import ba.unsa.etf.si.persistance.repository.DailyReportRepository;

import java.time.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DailyReportService {

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private PrimaryController primaryController;
    public static final DailyReportRepository dailyReportRepository = new DailyReportRepository();
    public static LocalDate dbMinDate = LocalDate.now();
    boolean dailyReportsChecked = false;

    public void setPrimaryController(PrimaryController primaryController) {
        this.primaryController = primaryController;
        if (!dailyReportsChecked) {
            dailyReportsChecked = true;
            dbMinDate = dailyReportRepository.getMinDate().getDate();
            this.primaryController.dailyReport(false);
        }
    }

    public void resetDailyReportCheck() {
        dailyReportsChecked = false;
    }

    public void run() {
        long delay = computeNextDelay();
        executorService.schedule(() -> {
            this.primaryController.dailyReport(true);
        }, delay, TimeUnit.SECONDS);
    }

    private long computeNextDelay() {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.systemDefault();

        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNextTarget = zonedNow
                .withHour(App.cashRegister.getEndTime().getHour())
                .withMinute(App.cashRegister.getEndTime().getMinute())
                .withSecond(App.cashRegister.getEndTime().getSecond());

        //ZonedDateTime zonedNextTarget = zonedNow
        //      .withHour(21)
        //      .withMinute(0)
        //      .withSecond(0);

        if (zonedNow.compareTo(zonedNextTarget) > 0)
            zonedNextTarget = zonedNextTarget.plusDays(1);

        Duration duration = Duration.between(zonedNow, zonedNextTarget);
        return duration.getSeconds();
    }

    public void stop() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
}
