package ba.unsa.etf.si.utility.pdfutils;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.DailyReport;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.enums.ReceiptStatus;
import ba.unsa.etf.si.utility.date.DateConverter;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ba.unsa.etf.si.services.DailyReportService.dailyReportRepository;

public class PDFDailyReportFactory {

    ArrayList<Receipt> allReceipts = new ArrayList<>();
    float total = 0f;
    float vatTotal = 0f;

    private final int[] transactions = new int[]{0, 0, 0};

    public void updateReceiptList(List<Receipt> receipts, LocalDate date) {
        allReceipts.clear();
        transactions[0] = transactions[1] = transactions[2] = 0;
        total = vatTotal = 0f;
        System.out.println("TUSAM");
        for (Receipt receipt : receipts) {
            LocalDateTime receiptDate = receipt.getDate();
            if (receiptDate.getYear() == date.getYear() && receiptDate.getMonth() == date.getMonth()
                    && receiptDate.getDayOfMonth() == date.getDayOfMonth() && receipt.getReceiptStatus() == ReceiptStatus.PAID) {

                allReceipts.add(receipt);
                transactions[receipt.getPaymentMethod().ordinal()]++;
                total += receipt.getAmount();
                vatTotal += receipt.getVATPrice();
            }
        }
        total = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    private String getDestination(LocalDate date) {
        Path path = Paths.get(App.cashRegister.getReportPath());
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Paths.get(path.normalize().toString(), "dailyReport_" + new DateConverter().toString(date).replace("/", "-") + ".pdf").normalize().toString();
    }

    public void generateReport(LocalDate date) {
        PdfWriter writer;
        try {
            writer = new PdfWriter(getDestination(date));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Paragraph title = new Paragraph("Daily Cash Register\nBalancing Report");
            title.setFontSize(20);
            title.setMarginBottom(30f);
            title.setTextAlignment(TextAlignment.CENTER);
            title.setUnderline();
            document.add(title);

            String dateStr = new DateConverter().toString(date);
            document.add(new Paragraph().setTextAlignment(TextAlignment.RIGHT).setMultipliedLeading(1)
                    .add(new Text(String.format("Cash register ID: %s\n", App.CASH_REGISTER_ID)).setBold().setFontSize(14))
                    .add(new Text(String.format("Merchant ID: %s\n", App.cashRegister.getMerchantID())).setBold().setFontSize(14))
                    .add(new Text(String.format("Merchant name: %s\n", App.cashRegister.getMerchantName())).setBold().setFontSize(14))
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
                            new UnitValue(UnitValue.PERCENT, 55f),
                            new UnitValue(UnitValue.PERCENT, 20f),
                            new UnitValue(UnitValue.PERCENT, 25f)})
                    .setWidthPercent(100)
                    .setMarginTop(10).setMarginBottom(10);


            totalTable.addCell(createCell("Total:").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            totalTable.addCell(createCell(String.valueOf(vatTotal)).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            totalTable.addCell(createCell(String.valueOf(total)).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

            document.add(totalTable);
            document.close();

            DailyReport dailyReport = new DailyReport(date, transactions[0], transactions[1], transactions[2], total);
            dailyReportRepository.add(dailyReport);
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
                        new UnitValue(UnitValue.PERCENT, 30f),
                        new UnitValue(UnitValue.PERCENT, 25f),
                        new UnitValue(UnitValue.PERCENT, 20f),
                        new UnitValue(UnitValue.PERCENT, 25f)})
                .setWidthPercent(100)
                .setMarginTop(10).setMarginBottom(10);
        table.addHeaderCell(createHeaderCell("Receipt ID", TextAlignment.LEFT));
        table.addHeaderCell(createHeaderCell("Payment method", TextAlignment.CENTER));
        table.addHeaderCell(createHeaderCell("VAT amount", TextAlignment.CENTER));
        table.addHeaderCell(createHeaderCell("Total amount", TextAlignment.RIGHT));

        for (Receipt receipt : allReceipts) {
            table.addCell(createCell(receipt.getReceiptID()).setTextAlignment(TextAlignment.LEFT));
            table.addCell(createCell(receipt.getPaymentMethod().getMethod()).setTextAlignment(TextAlignment.CENTER));
            table.addCell(createCell(String.valueOf(receipt.getVATPrice())).setTextAlignment(TextAlignment.CENTER));
            table.addCell(createCell(String.valueOf(receipt.getAmount())).setTextAlignment(TextAlignment.RIGHT));
        }

        return table;
    }
}
