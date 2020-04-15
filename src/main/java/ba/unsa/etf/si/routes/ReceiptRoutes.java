package ba.unsa.etf.si.routes;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.status.ReceiptStatus;
import ba.unsa.etf.si.persistance.ReceiptRepository;
import ba.unsa.etf.si.utility.server.HttpUtils;
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

    private static HttpRequest getPOSTRequest(Receipt receipt, String token) {
        return HttpUtils.POST(HttpRequest.BodyPublishers.ofString(receipt.toString()),
                DOMAIN + "/api/receipts", "Content-Type", "application/json", "Authorization", "Bearer " + token);
    }

    private static HttpRequest getReceiptsRequest(String token) {
        return HttpUtils.GET(DOMAIN + "/api/receipts?cash_register_id=" + App.getCashRegisterID(), "Authorization", "Bearer " + token);
    }

    private static HttpRequest getDeleteReceiptRequest(String token, String id) {
        return HttpUtils.DELETE(DOMAIN + "/api/receipts/" + id, "Authorization", "Bearer " + token);
    }

    public static void sendReceipt(Receipt receipt, String token, Consumer<String> callback, Runnable runnable) {
        HttpUtils.send(getPOSTRequest(receipt, token), HttpResponse.BodyHandlers.ofString(), callback, runnable);
    }

    public static String sendReceiptSync(Receipt receipt, String token) {
        return HttpUtils.sendSync(getPOSTRequest(receipt, token), HttpResponse.BodyHandlers.ofString());
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
        HttpUtils.send(getReceiptsRequest(token), HttpResponse.BodyHandlers.ofString(), callback, err);
    }

    public static void deleteReceipt(String token, String id, Consumer<String> callback, Runnable err) {
        HttpUtils.send(getDeleteReceiptRequest(token, id), HttpResponse.BodyHandlers.ofString(), callback, err);
    }
}
