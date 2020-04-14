package ba.unsa.etf.si.utility.routes;

import ba.unsa.etf.si.persistance.CredentialsRepository;
import ba.unsa.etf.si.utility.HttpUtils;

import javax.security.auth.callback.Callback;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

import static ba.unsa.etf.si.App.DOMAIN;

public class LoginRoutes {

    private static final CredentialsRepository credentialsRepository = new CredentialsRepository();

    private LoginRoutes() {}

    public static HttpRequest getLoginRequest(String username, String password) {
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString("{\"username\":\"" + username + "\"," +
                        "\"password\":\"" + password + "\"}");
        return HttpUtils.POST(bodyPublisher, DOMAIN + "/api/login", "Content-Type", "application/json");
    }

    public static HttpRequest getProfileRequest(String token) {
        return HttpUtils.GET(DOMAIN + "/api/profile", "Authorization", "Bearer " + token);
    }

    public static void sendLoginRequest(String username, String password, Consumer<String> callback, Runnable error) {
        HttpUtils.send(getLoginRequest(username, password), HttpResponse.BodyHandlers.ofString(), callback, error);
    }

    public static void getProfile(String token, Consumer<String> callback, Runnable err) {

    }
}
