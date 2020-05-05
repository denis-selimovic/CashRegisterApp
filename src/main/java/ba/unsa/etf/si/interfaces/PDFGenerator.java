package ba.unsa.etf.si.interfaces;

import ba.unsa.etf.si.models.Receipt;

public interface PDFGenerator {
    void generatePDF(Receipt receipt);
}
