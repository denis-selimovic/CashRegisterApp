package ba.unsa.etf.si.routes;

import ba.unsa.etf.si.utility.http.HttpUtils;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

import static ba.unsa.etf.si.App.DOMAIN;

public class PasswordRoutes {

    public PasswordRoutes() {
    }

    private static HttpRequest getResetTokenRequest(String userInfo) {
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString("{\"userInfo\":\"" + userInfo + "\"}");
        return HttpUtils.POST(bodyPublisher, DOMAIN + "/api/reset-token", "Content-Type", "application/json");
    }

    private static HttpRequest getVerificationInfoRequest(String userInfo, String resetToken) {
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString("{\"userInfo\":\"" + userInfo + "\"," +
                "\"resetToken\":\"" + resetToken + "\"}");
        return HttpUtils.POST(bodyPublisher, DOMAIN + "/api/verification-info", "Content-Type", "application/json");
    }

    private static HttpRequest getPasswordChangeRequest(String userInfo, String newPassword) {
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString("{\"userInfo\":\"" + userInfo + "\"," +
                "\"newPassword\":\"" + newPassword + "\"}");
        return HttpUtils.PUT(bodyPublisher, DOMAIN + "/api/password", "Content-Type", "application/json");
    }


    public static void getResetToken(String userinfo, Consumer<String> callback, Runnable error) {
        HttpUtils.send(getResetTokenRequest(userinfo), HttpResponse.BodyHandlers.ofString(), callback, error);
    }

    public static void sendVerificationInfo(String userInfo, String resetToken, Consumer<String> callback, Runnable err) {
        HttpUtils.send(getVerificationInfoRequest(userInfo, resetToken), HttpResponse.BodyHandlers.ofString(), callback, err);
    }

    public static void setNewPassword(String userInfo, String newPassword, Consumer<String> callback, Runnable err) {
        HttpUtils.send(getPasswordChangeRequest(userInfo, newPassword), HttpResponse.BodyHandlers.ofString(), callback, err);
    }
}
