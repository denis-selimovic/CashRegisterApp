package ba.unsa.etf.si.utility.payment;

import ba.unsa.etf.si.interfaces.CreditInfoResolver;
import ba.unsa.etf.si.interfaces.MessageReceiver;
import org.apache.commons.validator.routines.CreditCardValidator;
import org.json.JSONObject;
import java.time.LocalDate;

public class CreditInfoReceiver implements MessageReceiver {

    private final CreditInfoResolver resolver;
    Double priceToPay;

    public CreditInfoReceiver(CreditInfoResolver resolver, Double priceToPay) {
        this.resolver = resolver;
        this.priceToPay = priceToPay;
    }

    @Override
    public void onMessageReceived(String msg) {
        JSONObject infoJson = new JSONObject(msg);
        if ((!infoJson.has("error"))) resolver.resolve(checkIfValid(infoJson), "Credit card is not valid!");
        else resolver.resolve(false, "Credit card not inserted!");
    }

    private boolean checkIfValid(JSONObject infoJson) {
        try {
            CreditCardValidator creditCardValidator = new CreditCardValidator(CreditCardValidator.MASTERCARD + CreditCardValidator.VISA);
            String[] expiryDate = infoJson.getString("expiryDate").split("/");
            int expiryMonth = Integer.parseInt(expiryDate[0]);
            int expiryYear = Integer.parseInt(expiryDate[1]);
            return LocalDate.now().compareTo(LocalDate.of(expiryYear, expiryMonth, 28)) > 0 && infoJson.getString("cvvCode").length() == 3
                    && creditCardValidator.isValid(infoJson.getString("creditCardNumber")) && infoJson.getDouble("balance") >= priceToPay;
        } catch (Exception e) {
            return false;
        }
    }
}
