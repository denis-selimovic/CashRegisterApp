package ba.unsa.etf.si.routes;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.controllers.PrimaryController;
import ba.unsa.etf.si.utility.http.HttpUtils;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class TableRoutes {

    private TableRoutes() {}

    private static HttpRequest tablesGetRequest() {
        return HttpUtils.GET(App.DOMAIN + "/api/tables", "Authorization", "Bearer " + PrimaryController.currentUser.getToken());
    }

    public static void getTables(Consumer<String> callback, Runnable err) {
        HttpUtils.send(tablesGetRequest(), HttpResponse.BodyHandlers.ofString(), callback, err);
    }
}
