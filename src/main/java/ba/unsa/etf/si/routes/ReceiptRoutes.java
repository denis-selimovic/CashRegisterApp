package ba.unsa.etf.si.routes;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.enums.ReceiptStatus;
import ba.unsa.etf.si.persistance.repository.ReceiptRepository;
import ba.unsa.etf.si.utility.http.HttpUtils;
import org.json.JSONObject;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import static ba.unsa.etf.si.App.DOMAIN;

public class ReceiptRoutes {

    private static final ReceiptRepository receiptRepository = new ReceiptRepository();

    private ReceiptRoutes() {}

    private static HttpRequest postRequest(Receipt receipt, String token) {
        return HttpUtils.POST(HttpRequest.BodyPublishers.ofString(receipt.toString()),
                DOMAIN + "/api/receipts", "Content-Type", "application/json", "Authorization", "Bearer " + token);
    }

    private static HttpRequest getAllRequest(String token) {
        return HttpUtils.GET(DOMAIN + "/api/receipts?cash_register_id=" + App.cashRegister.getId(), "Authorization", "Bearer " + token);
    }

    private static HttpRequest deleteRequest(String token, String id) {
        return HttpUtils.DELETE(DOMAIN + "/api/receipts/" + id, "Authorization", "Bearer " + token);
    }

    public static String sendReceiptSync(Receipt receipt, String token) {
        return HttpUtils.sendSync(postRequest(receipt, token), HttpResponse.BodyHandlers.ofString());
    }

    public static void sendReceipts(String token) {
        new Thread(() -> {
            List<Receipt> receiptList = receiptRepository.getAll().stream().filter(r -> r.getReceiptStatus() == null).collect(Collectors.toList());
            receiptList.forEach(r -> {
                try {
                    JSONObject obj = new JSONObject(sendReceiptSync(r, token));
                    if(obj.has("statusCode") && obj.getInt("statusCode") == 200) {
                        r.setReceiptStatus(ReceiptStatus.PAID);
                        receiptRepository.update(r);
                    }
                }
                catch (Exception ignored) {}
            });
        }).start();
    }

    public static void getReceipts(String token, Consumer<String> callback, Runnable err) {
        HttpUtils.send(getAllRequest(token), HttpResponse.BodyHandlers.ofString(), callback, err);
    }

    public static void deleteReceipt(String token, String id, Consumer<String> callback, Runnable err) {
        HttpUtils.send(deleteRequest(token, id), HttpResponse.BodyHandlers.ofString(), callback, err);
    }

}
