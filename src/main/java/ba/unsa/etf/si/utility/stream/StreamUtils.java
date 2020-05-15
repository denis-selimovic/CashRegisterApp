package ba.unsa.etf.si.utility.stream;

import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.Table;
import ba.unsa.etf.si.utility.date.DateUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StreamUtils {

    private StreamUtils() {}

    public static <T> List<T> filter(List<T> list, Predicate<T> filter) {
        return list.stream().filter(filter).collect(Collectors.toList());
    }

    public static List<Receipt> sortByDate(LocalDate date, List<Receipt> receipts) {
        return filter(receipts, r -> DateUtils.compareDates(date, LocalDate.from(r.getDate())));
    }

    public static Product getProductByID(List<Product> products, Long id) {
        return products.stream().filter(p -> p.getServerID().equals(id)).findFirst().orElseGet(Product::new);
    }

    public static List<Product> search(List<Product> productList, String value) {
        return filter(productList, p -> p.getName().toLowerCase().contains(value.toLowerCase()));
    }

    public static double price(List<Product> list) {
        return list.stream().mapToDouble( p -> {
            String format = String.format("%.2f", p.getTotalPrice());
            if(format.contains(",")) format = format.replace(",", ".");
            return Double.parseDouble(format);
        }).sum();
    }

    public static int getNumberFromString(String text) {
        int id;
        try {
            id = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            id = -1;
        }
        return id;
    }

}
