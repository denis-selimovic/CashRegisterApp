package ba.unsa.etf.si.models;

import ba.unsa.etf.si.utility.date.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashRegister {

    private Long cashRegisterID;
    private String cashRegisterName;
    private Long officeID;
    private Long merchantID;
    private String merchantName;
    private String uuid;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean restaurant;

    public void initialize(JSONObject jsonObject) {
        try {
            tryInit(jsonObject);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void tryInit(JSONObject jsonObject) {
        cashRegisterID = jsonObject.getLong("cashRegisterId");
        cashRegisterName = jsonObject.getString("cashRegisterName");
        officeID = jsonObject.getLong("officeId");
        merchantID = jsonObject.getLong("businessId");
        merchantName = jsonObject.getString("businessName");
        uuid = jsonObject.getString("uuid");
        restaurant = jsonObject.getBoolean("restaurant");
        startTime = DateUtils.localTimeFromString(jsonObject.getString("startTime"));
        endTime = DateUtils.localTimeFromString(jsonObject.getString("endTime"));
    }
}
