package ba.unsa.etf.si.utility.pdfutil;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.status.ReceiptStatus;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PDFCashierBalancingFactory {

    ArrayList<Receipt> allReceipts = new ArrayList<>();
    double total = 0.0;

    private static final String HOME = Paths.get("").toAbsolutePath().toString();
    private static final String DEST = Paths.get(HOME, "pdf").toAbsolutePath().toString();

    public PDFCashierBalancingFactory(List<Receipt> receipts) {
        for (Receipt receipt : receipts) {
            LocalDateTime receiptDate = receipt.getDate();
            LocalDateTime today = LocalDateTime.now();
            if (receiptDate.getYear() == today.getYear() && receiptDate.getMonth() == today.getMonth()
                    && receiptDate.getDayOfMonth() == today.getDayOfMonth() && receipt.getReceiptStatus() == ReceiptStatus.PAID) {
                allReceipts.add(receipt);
                transactions[receipt.getPaymentMethod().ordinal()]++;
                total += receipt.getAmount();
            }
        }
        total = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private final int[] transactions = new int[]{0, 0, 0};

    private String getDestination() {
        Path path = Paths.get(DEST);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Paths.get(path.normalize().toString(), "allReceipts_" + LocalDateTime.now().toString() + ".pdf").normalize().toString();
    }

    public void generatePdf() {
        PdfWriter writer = null;
        try {
            writer = new PdfWriter(getDestination());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Paragraph title = new Paragraph("Daily Cash Register\nBalancing Report");
            title.setFontSize(20);
            title.setMarginBottom(30f);
            title.setTextAlignment(TextAlignment.CENTER);
            title.setUnderline();
            document.add(title);

            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
            String dateStr = formatter.format(new Date());
            document.add(new Paragraph().setTextAlignment(TextAlignment.RIGHT).setMultipliedLeading(1)
                    .add(new Text(String.format("Cash register ID: %s\n", App.getCashRegisterID())).setBold().setFontSize(14))
                    .add(new Text(String.format("Branch ID: %s\n", App.getBranchID())).setBold().setFontSize(14))
                    .add(new Text("Branch name: Bingo\n").setBold().setFontSize(14))
                    .add(new Text("Date: " + dateStr).setBold().setFontSize(14)).setMarginBottom(20f)
            );

            document.add(new Paragraph().setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(1)
                    .add(new Text("Cash transactions: ").setFontSize(12).setBold()).add(transactions[0] + "\n")
                    .add(new Text("Card transactions: ").setFontSize(12).setBold()).add(transactions[1] + "\n")
                    .add(new Text("Pay app transactions: ").setFontSize(12).setBold()).add(transactions[2] + "\n")
                    .add(new Text("Total number of transactions: ").setFontSize(12).setBold()).add((transactions[0] + transactions[1] + transactions[2]) + "\n")
            .setMarginBottom(15f));

            document.add(createReceiptTable());

            Table totalTable = new Table(
                    new UnitValue[]{
                            new UnitValue(UnitValue.PERCENT, 70f),
                            new UnitValue(UnitValue.PERCENT, 30f)})
                    .setWidthPercent(100)
                    .setMarginTop(10).setMarginBottom(10);


            totalTable.addCell(createCell("Total:").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            totalTable.addCell(createCell(String.valueOf(total)).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

            document.add(totalTable);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Cell createHeaderCell(String text, TextAlignment textAlignment) {
        Color color = new DeviceRgb(31, 107, 183);
        Color fColor = new DeviceRgb(255, 255, 255);

        return new Cell().setPadding(0.8f).setBackgroundColor(color)
                .add(new Paragraph(text).setTextAlignment(textAlignment)
                        .setMultipliedLeading(1).setFontColor(fColor).setFontSize(12));
    }

    private Cell createCell(String text) {

        return new Cell().setPadding(0.8f)
                .add(new Paragraph(text)
                        .setMultipliedLeading(1));
    }

    private Table createReceiptTable() {
        Table table = new Table(
                new UnitValue[]{
                        new UnitValue(UnitValue.PERCENT, 45f),
                        new UnitValue(UnitValue.PERCENT, 25f),
                        new UnitValue(UnitValue.PERCENT, 30f)})
                .setWidthPercent(100)
                .setMarginTop(10).setMarginBottom(10);
        table.addHeaderCell(createHeaderCell("Receipt ID", TextAlignment.LEFT));
        table.addHeaderCell(createHeaderCell("Payment method", TextAlignment.CENTER));
        table.addHeaderCell(createHeaderCell("Total amount", TextAlignment.RIGHT));

        for (Receipt receipt : allReceipts) {
            table.addCell(createCell(receipt.getReceiptID()).setTextAlignment(TextAlignment.LEFT));
            table.addCell(createCell(receipt.getPaymentMethod().getMethod()).setTextAlignment(TextAlignment.CENTER));
            table.addCell(createCell(String.valueOf(receipt.getAmount())).setTextAlignment(TextAlignment.RIGHT));
        }

        return table;
    }
}
