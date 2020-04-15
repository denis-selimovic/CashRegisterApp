package ba.unsa.etf.si.utility.routes;

import ba.unsa.etf.si.utility.server.HttpUtils;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

import static ba.unsa.etf.si.App.DOMAIN;

public class ProductRoutes {

    private ProductRoutes() {}

    private static HttpRequest getProductsRequest(String token) {
        return HttpUtils.GET(DOMAIN + "/api/products", "Authorization", "Bearer " + token);
    }

    public static void getProducts(String token, Consumer<String> callback, Runnable err) {
        HttpUtils.send(getProductsRequest(token), HttpResponse.BodyHandlers.ofString(), callback, err);
    }
}
