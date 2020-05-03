package ba.unsa.etf.si.utility.modelutils;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.ReceiptItem;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

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
        String jsstr = "{\n" +
                "\"cashRegisterId\": " + App.getCashRegisterID() + ",\n" +
                "\"officeId\": " + App.getBranchID() + ",\n" +
                "\"businessName\": \"BINGO\",\n" +
                "\"uuid\" :" + App.UUID + "\n" +
                "}";
        JSONObject js = new JSONObject(jsstr);
        System.out.println(js.toString());
        return jsstr;
    }
}
