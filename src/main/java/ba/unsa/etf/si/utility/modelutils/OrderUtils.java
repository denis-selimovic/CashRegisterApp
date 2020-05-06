package ba.unsa.etf.si.utility.modelutils;

import ba.unsa.etf.si.controllers.PrimaryController;
import ba.unsa.etf.si.models.Order;
import ba.unsa.etf.si.models.OrderItem;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.utility.stream.StreamUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderUtils {

    private OrderUtils() {}

    public static ObservableList<Order> getOrdersFromJSON(String response, List<Product> productList) {
        ObservableList<Order> orders = FXCollections.observableArrayList();
        JSONArray array = new JSONArray(response);
        for(int i = 0; i < array.length(); ++i) orders.add(getOrderFromJSON(array.getJSONObject(i), productList));
        return orders;
    }

    public static Order getOrderFromJSON(JSONObject json, List<Product> productList) {
        Order order = new Order(json.getLong("id"), PrimaryController.currentUser.getUsername(), LocalDateTime.now());
        order.setOrderItemList(getOrderItemsFromJSON(json.getJSONArray("receiptItems"), productList));
        return order;
    }

    public static ArrayList<OrderItem> getOrderItemsFromJSON(JSONArray array, List<Product> productList) {
        ArrayList<OrderItem> items = new ArrayList<>();
        for(int i = 0; i < array.length(); ++i) items.add(getOrderItemFromJSON(array.getJSONObject(i), productList));
        return items;
    }

    public static OrderItem getOrderItemFromJSON(JSONObject json, List<Product> productList) {
        return new OrderItem(StreamUtils.getProductByID(productList, json.getLong("id")), json.getDouble("quantity"));
    }
}
