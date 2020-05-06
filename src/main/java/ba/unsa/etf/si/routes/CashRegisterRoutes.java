package ba.unsa.etf.si.routes;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.utility.http.HttpUtils;
import org.json.JSONObject;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

import static ba.unsa.etf.si.App.DOMAIN;

public class CashRegisterRoutes {


    private CashRegisterRoutes() {}

    private static HttpRequest getCashRegisterRequest(String token) {
        return HttpUtils.GET(DOMAIN + "/api/cash-register/data?cash_register_id=" + App.getCashRegisterID(), "Authorization", "Bearer " + token);
    }

    public static String getCashRegisterUUID(String token) {
        String response = HttpUtils.sendSync(getCashRegisterRequest(token), HttpResponse.BodyHandlers.ofString());
        System.out.println(new JSONObject(response).getString("uuid"));
        return new JSONObject(response).getString("uuid");
    }

    public static void getCashRegisterData (String token, Consumer<String> callback, Runnable err) {
        HttpRequest GET = HttpUtils.GET(DOMAIN + "/api/cash-register/data?cash_register_id=" + App.getCashRegisterID(), "Authorization", "Bearer " + token);
        HttpUtils.send(GET, HttpResponse.BodyHandlers.ofString(), callback, err);
    }
}
