package ba.unsa.etf.si.utility.date;

import ba.unsa.etf.si.models.Receipt;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DateUtils {

    private DateUtils() {}

    public static boolean compareDates(LocalDate first, LocalDate second) {
        return (first == null) || first.isEqual(second);
    }

    public static List<Receipt> sortByDate(LocalDate date, List<Receipt> receipts) {
        return receipts.stream().filter(r -> compareDates(date, LocalDate.from(r.getDate()))).collect(Collectors.toList());
    }

    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
