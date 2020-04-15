package ba.unsa.etf.si.utility.stream;

import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.utility.date.DateUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilterUtils {

    private FilterUtils() {}

    public static <T> List<T> filter(List<T> list, Predicate<T> filter) {
        return list.stream().filter(filter).collect(Collectors.toList());
    }

    public static List<Receipt> sortByDate(LocalDate date, List<Receipt> receipts) {
        return filter(receipts, r -> DateUtils.compareDates(date, LocalDate.from(r.getDate())));
    }

    public static Product getProductByID(List<Product> products, Long id) {
        return products.stream().filter(p -> p.getServerID().equals(id)).findFirst().orElseGet(Product::new);
    }
}
