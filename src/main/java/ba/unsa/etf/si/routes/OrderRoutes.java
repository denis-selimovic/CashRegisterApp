package ba.unsa.etf.si.routes;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.controllers.PrimaryController;
import ba.unsa.etf.si.models.Order;
import ba.unsa.etf.si.utility.http.HttpUtils;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class OrderRoutes {

    private OrderRoutes() {}

    private static HttpRequest getDeleteRequest(Long id) {
        return HttpUtils.DELETE(App.DOMAIN + "/api/orders/" + id, "Authorization", "Bearer " + PrimaryController.currentUser.getToken());
    }

    private static HttpRequest getOrderRequest() {
        return HttpUtils.GET(App.DOMAIN + "/api/orders", "Authorization", "Bearer " + PrimaryController.currentUser.getToken());
    }

    private static HttpRequest getPutRequest(String body) {
        return HttpUtils.PUT(HttpRequest.BodyPublishers.ofString(body), App.DOMAIN + "/api/orders", "Authorization", "Bearer " + PrimaryController.currentUser.getToken(), "Content-Type", "application/json");
    }

    public static void deleteOrder(Long id, Consumer<String> callback, Runnable err) {
        HttpUtils.send(getDeleteRequest(id), HttpResponse.BodyHandlers.ofString(), callback, err);
    }

    public static void getOrders(Consumer<String> callback, Runnable err) {
        HttpUtils.send(getOrderRequest(), HttpResponse.BodyHandlers.ofString(), callback, err);
    }

    public static void updateOrder(Order order, Consumer<String> callback, Runnable err) {
        HttpUtils.send(getPutRequest(order.toString()), HttpResponse.BodyHandlers.ofString(), callback, err);
    }
}
