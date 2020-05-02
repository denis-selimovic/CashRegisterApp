package ba.unsa.etf.si.utility.modelutils;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.controllers.PrimaryController;
import ba.unsa.etf.si.models.CashRegister;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.ReceiptItem;
import ba.unsa.etf.si.routes.CashRegisterRoutes;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class QRJsonUtils {

    private QRJsonUtils() {}

    private static String receiptItemsString(List<ReceiptItem> list) {
        StringBuilder receiptItems = new StringBuilder();
        for (ReceiptItem receiptItem : list) {
            int itemQuantity = (int) receiptItem.getQuantity();
            receiptItems.append(receiptItem.getName()).append(" (").append(itemQuantity).append(")");
            receiptItems.append(",");
        }
        return StringUtils.chop(receiptItems.toString());
    }

    public static String getDynamicQRCode(Receipt receipt) {
        return "{\n" +
                "\"cashRegisterId\": " + App.getCashRegisterID() + ",\n" +
                "\"officeId\": " + App.getBranchID() + ",\n" +
                "\"businessName\": \"BINGO\",\n" +
                "\"receiptId\": \"" + receipt.getTimestampID() + "\",\n" +
                "\"service\": \"" + receiptItemsString(receipt.getReceiptItems()) + "\",\n" +
                "\"totalPrice\": " + receipt.getAmount() + "\n" +
                "}";
    }

    public static String getStaticQRCode() {
        App.UUID = CashRegisterRoutes.getCashRegisterUUID(PrimaryController.currentUser.getToken());
        return "{\n" +
                "\"cashRegisterId\": " + App.getCashRegisterID() + ",\n" +
                "\"officeId\": " + App.getBranchID() + ",\n" +
                "\"businessName\": \"BINGO\"\n" +
                "\"uuid\": \"" + App.UUID + "\"\n" +
                "}";
    }
}
