package ba.unsa.etf.si.utility.interfaces;

import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.ReceiptItem;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.zugferd.ZugferdConformanceLevel;
import com.itextpdf.zugferd.ZugferdDocument;
import com.itextpdf.layout.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;


public interface PDFReceipt {
    String DEST = "src/main/resources/ba/unsa/etf/si/pdf/basic.pdf";
    String ICC = "src/main/resources/ba/unsa/etf/si/color/sRGB_CS_profile.icm";
    String REGULAR = "src/main/resources/ba/unsa/etf/si/fonts/OpenSans-Regular.ttf";
    String BOLD = "src/main/resources/ba/unsa/etf/si/fonts/OpenSans-Bold.ttf";
    String NEWLINE = "\n";

    static void wrt () {

        System.out.println(System.getProperty("user.dir"));
    }

    static void createPdf (Receipt receipt) throws IOException {
       // String dest = String.format(DEST, receipt.getReceiptID());
       String dest = DEST;
        // Create the ZUGFeRD document
        ZugferdDocument pdfDocument = new ZugferdDocument(
                new PdfWriter(dest), ZugferdConformanceLevel.ZUGFeRDBasic,
                new PdfOutputIntent("Custom", "", "https://www.color.org",
                        "sRGB IEC61966-2.1", new FileInputStream(ICC)));

        Document document = new Document(pdfDocument);
        document.setFont(PdfFontFactory.createFont(REGULAR, true))
                .setFontSize(12);
        PdfFont bold = PdfFontFactory.createFont(BOLD, true);

        document.add(
                new Paragraph()
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setMultipliedLeading(1)
                        .add(new Text(String.format("RECEIPT %s\n",  receipt.getTimestampID().substring(6)))
                                .setFont(bold).setFontSize(14))
                        .add(receipt.getDate().toString().substring(0,10).replace("-", "/"))
                         .add(", "+ receipt.getDate().toString().substring(11,19)));

        // Add the line items
        document.add(createReceiptItemTable(receipt, bold));

        document.close();

    }

    static Table createReceiptItemTable(Receipt receipt, PdfFont bold) {
        Table table = new Table(
                new UnitValue[]{
                        new UnitValue(UnitValue.PERCENT, 38.75f),
                        new UnitValue(UnitValue.PERCENT, 12.5f),
                        new UnitValue(UnitValue.PERCENT, 6.25f),
                        new UnitValue(UnitValue.PERCENT, 15.5f),
                        new UnitValue(UnitValue.PERCENT, 15.5f),
                        new UnitValue(UnitValue.PERCENT, 15.5f)})
                .setWidthPercent(100)
                .setMarginTop(10).setMarginBottom(10);
        table.addHeaderCell(createCell("Name of product", bold));
        table.addHeaderCell(createCell("Quantity", bold));
        table.addHeaderCell(createCell("Unit", bold));
        table.addHeaderCell(createCell("Discount(%)", bold));
        table.addHeaderCell(createCell("Price w/o discount", bold));
        table.addHeaderCell(createCell("Total", bold));
        Product product;
        for (ReceiptItem item :receipt.getReceiptItems()) {

            //name of product
            table.addCell(createCell(item.getName()));
            // quantity
            table.addCell(createCell(format2dec(round(item.getQuantity()))).setTextAlignment(TextAlignment.RIGHT));
            // unit
            table.addCell(createCell(item.getUnit()).setTextAlignment(TextAlignment.RIGHT));
            // discount
            table.addCell(createCell( Double.toString(item.getDiscount())).setTextAlignment(TextAlignment.RIGHT));
            //price w/o discount
            table.addCell(createCell( Double.toString(item.getPrice())).setTextAlignment(TextAlignment.RIGHT));
            //total
            table.addCell(createCell( Double.toString(item.getTotalPrice())).setTextAlignment(TextAlignment.RIGHT));
        }
        return table;
    }


    static Cell createCell(String text) {
        return new Cell().setPadding(0.8f)
                .add(new Paragraph(text)
                        .setMultipliedLeading(1));
    }

    static Cell createCell(String text, PdfFont font) {
        return new Cell().setPadding(0.8f)
                .add(new Paragraph(text)
                        .setFont(font).setMultipliedLeading(1));
    }

    public static String format2dec(double d) {
        return String.format("%.2f", d);
    }
    public static String format4dec(double d) {
        return String.format("%.4f", d);
    }
    public static double round(double d) {
        d = d * 100;
        long tmp = Math.round(d);
        return (double) tmp / 100;
    }

}
