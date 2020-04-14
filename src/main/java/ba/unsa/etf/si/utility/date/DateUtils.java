package ba.unsa.etf.si.utility.date;

import ba.unsa.etf.si.models.Receipt;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
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

}
