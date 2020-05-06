package ba.unsa.etf.si.interfaces;

import ba.unsa.etf.si.models.Receipt;

public interface ReceiptLoader {
    void onReceiptLoaded(Receipt receipt);
}
