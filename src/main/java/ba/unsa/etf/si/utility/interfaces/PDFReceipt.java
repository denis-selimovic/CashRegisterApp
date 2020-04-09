package ba.unsa.etf.si.utility.interfaces;

public interface PDFReceipt {
    public static final String DEST = "src/main/resources/ba/unsa/etf/si/pdf";
    public static final String ICC = "src/main/resources/ba/unsa/etf/si/color/sRGB_CS_profile.icm";
    public static final String REGULAR = "src/main/resources/ba/unsa/etf/si/fonts/OpenSans-Regular.ttf";
    public static final String BOLD = "src/main/resources/ba/unsa/etf/si/fonts/OpenSans-Bold.ttf";
    public static final String NEWLINE = "\n";

    public static void wrt () {
        System.out.println(System.getProperty("user.dir"));
    }
}
