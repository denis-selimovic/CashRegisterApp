package ba.unsa.etf.si;

import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.ReceiptItem;
import ba.unsa.etf.si.models.status.PaymentMethod;
import ba.unsa.etf.si.models.status.ReceiptStatus;
import ba.unsa.etf.si.utility.interfaces.PDFReceipt;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class GUIStartup {

    public static void main(String[] args) {
        try {
            org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
            File file = new File(PDFReceipt.DEST);
            file.getParentFile().mkdirs();
            Receipt newRec = new Receipt(LocalDateTime.now(), "imenko imenkovic", 1000, 10L);
            ReceiptItem x =   new ReceiptItem(new Product(12L, "Grah", 100, "noimage", "kom", 50, 13));
            ReceiptItem y=   new ReceiptItem(new Product(10L, "Sarma", 30, "noimage", "kom", 60, 15));
            List<ReceiptItem> list = new ArrayList<ReceiptItem>();
            list.add(x); list.add(y);
            list.add(new ReceiptItem(new Product(13L, "Suhe sljive", 13, "noimage", "kom", 50, 2)));
            list.add(new ReceiptItem(new Product(15L, "Suhe smokve", 12, "noimage", "kom", 22, 4)));
            list.add(new ReceiptItem(new Product(16L, "Carape", 15, "noimage", "kom", 11, 1)));
            list.add(new ReceiptItem(new Product(22L, "Hlace", 110, "noimage", "kom", 0, 3)));
            list.add(new ReceiptItem(new Product(33L, "Knjiga", 120, "noimage", "kom", 3, 2)));
            newRec.setId(10L);
            newRec.setReceiptItems(list);
            newRec.setPaymentMethod(PaymentMethod.CASH);
            newRec.setReceiptStatus(ReceiptStatus.INSUFFICIENT_FUNDS);
            PDFReceipt.createPdf(newRec);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
      //  App.main(args);
    }
}
