package ba.unsa.etf.si.notifications.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentNotification {

    private String receiptId;
    private String status;

    public PaymentNotification(JSONObject json) {
        receiptId = json.getString("receiptId");
        status = json.getString("status");
    }
}
