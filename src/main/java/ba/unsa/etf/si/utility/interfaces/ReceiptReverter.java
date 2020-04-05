package ba.unsa.etf.si.utility.interfaces;

import ba.unsa.etf.si.models.Receipt;

public interface ReceiptReverter {
    void onReceiptReverted(Receipt receipt);
}
