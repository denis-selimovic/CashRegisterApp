package ba.unsa.etf.si.utility.modelutils;

import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.ReceiptItem;
import ba.unsa.etf.si.models.enums.ReceiptStatus;
import ba.unsa.etf.si.utility.stream.StreamUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReceiptUtils {

    private ReceiptUtils() {}

    public static String getReceiptItemsAsString(List<ReceiptItem> items) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < items.size(); ++i) {
            builder.append(items.get(i).toString());
            if(i == items.size() - 1) continue;
            builder.append(",\n");
        }
        builder.append("\n");
        return builder.toString();
    }

    public static List<ReceiptItem> receiptItemListFromJSON (JSONArray jsonArray, List<Product> arrayList) {
        List<ReceiptItem> receiptItems = new ArrayList<>();
        for (Product product : arrayList) {
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject obj = jsonArray.getJSONObject(j);
                if (product.getServerID() == obj.getLong("id")) {
                    ReceiptItem r = new ReceiptItem(product);
                    r.setQuantity(obj.getDouble("quantity"));
                    receiptItems.add(r);
                }
            }
        }
        return receiptItems;
    }

    public static List<Receipt> getReceipts(JSONArray arr, List<Product> productList) {
        ArrayList<Receipt> receipts = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            Receipt newRecp = new Receipt(arr.getJSONObject(i), productList);
            if (newRecp.getReceiptStatus() != ReceiptStatus.PAID) continue;
            receipts.add(newRecp);
        }
        return receipts;
    }

    public static Receipt createReceiptFromTable (List<Product> productList, LocalDateTime date, String cashier, long sellerReceiptID) {
        Receipt receipt = new Receipt(date, cashier, StreamUtils.price(productList));
        for(Product p : productList) receipt.getReceiptItems().add(new ReceiptItem(p));
        if(sellerReceiptID != -1) receipt.setServerID(sellerReceiptID);
        return receipt;
    }
}
