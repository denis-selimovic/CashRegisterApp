package ba.unsa.etf.si.utility.routes;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.utility.HttpUtils;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

import static ba.unsa.etf.si.App.DOMAIN;

public class CashRegisterRoutes {

    private CashRegisterRoutes() {}

    public static void openCashRegister(String token, Consumer<String> response, Runnable error) {
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString("");
        HttpRequest POST = HttpUtils.POST(bodyPublisher, DOMAIN + "/api/cash-register/open?cash_register_id=" + App.getCashRegisterID(),
                "Authorization", "Bearer " + token);
        HttpUtils.send(POST, HttpResponse.BodyHandlers.ofString(), response, error);
    }
}
