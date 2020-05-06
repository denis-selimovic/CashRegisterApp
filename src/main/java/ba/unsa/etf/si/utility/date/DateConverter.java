package ba.unsa.etf.si.utility.date;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConverter extends StringConverter<LocalDate> {

    final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String toString(LocalDate date) {
        if (date != null) {
            return dateFormatter.format(date);
        }
        return "";
    }

    @Override
    public LocalDate fromString(String string) {
        if (string != null && !string.isEmpty()) return LocalDate.parse(string, dateFormatter);
        return null;
    }
}
