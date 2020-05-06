package ba.unsa.etf.si.utility.modelutils;

import ba.unsa.etf.si.models.OrderItem;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.ReceiptItem;
import ba.unsa.etf.si.persistance.ProductRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ProductUtils {

    private static final ProductRepository productRepository = new ProductRepository();

    public static Product getProductFromJSON(JSONObject jsonObj) {
        String imageString = null;
        if (jsonObj.get("imageBase64") != null) imageString = jsonObj.getString("imageBase64");
        return new Product(jsonObj.getLong("id"), jsonObj.getString("name"), jsonObj.getDouble("price"), imageString,
                jsonObj.getString("measurementUnit"), jsonObj.getDouble("discount"), jsonObj.getDouble("quantity"));
    }

    public static ObservableList<Product> getObservableProductListFromJSON(String response) {
        return FXCollections.observableArrayList(getProductsFromJSON(response));
    }

    public static List<Product> getProductsFromJSON(String jsonListOfProducts) {
        List<Product> productList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonListOfProducts);
        for (int i = 0; i < jsonArray.length(); i++) productList.add(getProductFromJSON((jsonArray.getJSONObject(i))));
        return productList;
    }

    public static List<Product> getProductsFromOrder(List<Product> products, List<OrderItem> items) {
        List<Product> productsItems = new ArrayList<>();
        products.forEach(p -> {
            items.forEach(i -> {
                if(p.getServerID().equals(i.getProductID())) {
                    p.setTotal((int) i.getQuantity());
                    productsItems.add(p);
                }
            });
        });
        return productsItems;
    }

    public static ArrayList<Product> getProductsFromReceipt(List<Product> products, Receipt receipt) {
        ArrayList<Product> pr = new ArrayList<>();
        for (Product p : products) {
            for (ReceiptItem r : receipt.getReceiptItems()) {
                if (r.getProductID().longValue() == p.getServerID().longValue()) {
                    p.setTotal((int) r.getQuantity());
                    pr.add(p);
                }
            }
        }
        return pr;
    }

    public static void updateLocalDatabase(List<Product> products) {
        List<Product> hibernate = productRepository.getAll();
        products.forEach(p -> {
            hibernate.forEach(h -> {
                if(h.equals(p)) p.setId(h.getId());
            });
            productRepository.update(p);
        });
    }

    public static void restartProducts(List<Product> products) {
        products.forEach(p -> {
            p.setQuantity(p.getQuantity() - p.getTotal());
            p.setTotal(0);
            if(p.getId() != null) productRepository.update(p);
        });
    }

    public static ObservableList<Product> getProductsFromOrder(List<Product> products, JSONArray jsonReceiptProducts) {
        ObservableList<Product> receiptProducts = FXCollections.observableArrayList();
        for (Product p : products) {
            for (int i = 0; i < jsonReceiptProducts.length(); i++) {
                if (p.getServerID().toString().equals(jsonReceiptProducts.getJSONObject(i).get("id").toString())) {
                    double doubleTotal = (double) jsonReceiptProducts.getJSONObject(i).get("quantity");
                    p.setTotal((int) doubleTotal);
                    receiptProducts.add(p);
                }
            }
        }
        return receiptProducts;
    }
}
